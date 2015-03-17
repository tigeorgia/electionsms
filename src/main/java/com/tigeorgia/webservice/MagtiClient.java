package com.tigeorgia.webservice;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.springframework.xml.xpath.XPathOperations;

import com.tigeorgia.model.CsvFile;
import com.tigeorgia.model.Message;
import com.tigeorgia.model.Person;
import com.tigeorgia.model.Summary;
import com.tigeorgia.util.Constants;
import com.tigeorgia.util.Utilities;


public class MagtiClient {

	@Autowired
	private RestTemplate restTemplate;

	private String magtiWebserviceEndpoint;
	
	private String magtiWebserviceEndpointComplete;
	
	private String smsServerIp;
	private String smsServerUsername;
	private String smsServerPassword;

	Map<String, String> wsVariables;

	@Autowired
	private XPathOperations xpathTemplate;

	private static final Logger logger = Logger.getLogger(MagtiClient.class);

	public Summary sendMessages(Message message, String contactType) {

		List<Person> recipients = null;
		if (contactType.equalsIgnoreCase(Utilities.ELECTION_CONTACT_TYPE)){
			CsvFile file = Utilities.getListOfRecipients(logger, contactType);
			if (file != null){
				recipients = file.getRecipients();
			}
		}else if (contactType.equalsIgnoreCase(Utilities.PARLIAMENT_CONTACT_TYPE)){
			// We retrieve the Parliament list from the MyParliament API
			recipients = Utilities.getParliamentaryContacts();
		}
		
		Summary summary = null;
		if (recipients != null){
			String magtiResponse = null;
			summary = new Summary();
			if (recipients != null && recipients.size() > 0){
				// We have the whole list of recipients, we need to get the chosen ones, 
				// based on the selected groups.
				ArrayList<String> chosenGroups = null;
				if (contactType.equalsIgnoreCase(Utilities.ELECTION_CONTACT_TYPE)){
					chosenGroups = message.getChosenElectionGroups();
				}else if (contactType.equalsIgnoreCase(Utilities.PARLIAMENT_CONTACT_TYPE)){
					chosenGroups = message.getChosenParliamentaryGroups();
				}
				String chosenLanguage = message.getLang();
				
				int totalNumberOfGroups = Utilities.totalNumberOfGroups(recipients);
				summary.setTotalNumberOfGroups(totalNumberOfGroups);
				boolean allGroups = (chosenGroups.size() == totalNumberOfGroups);				
				List<Person> peopleWhoDidntReceive = new ArrayList<Person>();
				int countTotalMessageSent = 0;
				int countSuccess = 0;
				int countFail = 0;

				for (Person recipient : recipients){
					if ((allGroups || isInChosenGroup(recipient, chosenGroups)) && isInChosenLanguage(recipient, chosenLanguage)){
						// Recipient is in chosen group, and in chosen language: we send the message.
						ArrayList<String> recipientNumbers = recipient.getNumbers();
						String statusCode = null;
						if (recipientNumbers != null && recipientNumbers.size() >= 1){
							String recipientNumber = recipientNumbers.get(0);
							if (!(recipientNumber.startsWith("995") || recipientNumber.startsWith("+995"))){
								recipientNumber = "995"+recipientNumber;
							}
							
							wsVariables.put("to", recipientNumber);
							wsVariables.put("text", message.getBody());

							String urlEncodedText = "";
							
							try {
								urlEncodedText = URLEncoder.encode(message.getBody(), "UTF-8");
							} catch (UnsupportedEncodingException e) {
							}
								
							String commandToPass = String.format(magtiWebserviceEndpointComplete, recipientNumber, urlEncodedText);
							commandToPass = "curl '"+commandToPass+"'";

							// Previous way to send message to Magti - since we have to SSH to the server connected via VPN to Magti first, we're using other means than RestTemplate. 
							//magtiResponse = restTemplate.getForObject(magtiWebserviceEndpoint, String.class, wsVariables);
							magtiResponse = Utilities.sendMessageToMagtiOverSsh(commandToPass, smsServerIp, smsServerUsername, smsServerPassword);
							
							if (magtiResponse != null){
								String[] responses = magtiResponse.split(" - ");
								if (responses != null && responses.length == 2){
									statusCode = responses[0].trim();
								}else{
									statusCode = magtiResponse;
								}
							}
						}else{
							// There is been a formatting problem, in the CSV file - phone number was not entered properly.
							statusCode = Constants.MAGTI_WSCODE_CSVFILE_ERROR;
						}
						
						countTotalMessageSent++;
						
						if (statusCode != null && statusCode.equalsIgnoreCase(Constants.MAGTI_WSCODE_SUCCESS)){
							countSuccess++;
						}else{
							countFail++;
							Person recipientDidntReceive = new Person(recipient.getName(), recipient.getLanguage(), recipient.getNumbers(), recipient.getGroups());
							recipientDidntReceive.setErrorCode(statusCode);
							peopleWhoDidntReceive.add(recipientDidntReceive);
						}

					}
				}
				summary.setFailNumber(countFail);
				summary.setSuccessNumber(countSuccess);
				summary.setTotalNumber(countTotalMessageSent);
				summary.setDidntReceive(peopleWhoDidntReceive);
			}
		}

		return summary;
	}

	private boolean isInChosenGroup(Person recipient, ArrayList<String> chosenGroups){
		ArrayList<String> personGroups = new ArrayList<String>();
		for (String recipientGroup: recipient.getGroups()){
			personGroups.add(recipientGroup.toUpperCase());
		}
		for (String group : chosenGroups){
			if (personGroups.contains(group.toUpperCase())){
				return true;
			}
		}
		return false;
	}
	
	private boolean isInChosenLanguage(Person recipient, String lang){
		return (recipient.getLanguage().equalsIgnoreCase(lang));
	}

	public String getMagtiWebserviceEndpoint() {
		return magtiWebserviceEndpoint;
	}

	public void setMagtiWebserviceEndpoint(String magtiWebserviceEndpoint) {
		this.magtiWebserviceEndpoint = magtiWebserviceEndpoint;
	}

	public Map<String, String> getWsVariables() {
		return wsVariables;
	}

	public void setWsVariables(Map<String, String> wsVariables) {
		this.wsVariables = wsVariables;
	}

	public String getMagtiWebserviceEndpointComplete() {
		return magtiWebserviceEndpointComplete;
	}

	public void setMagtiWebserviceEndpointComplete(
			String magtiWebserviceEndpointComplete) {
		this.magtiWebserviceEndpointComplete = magtiWebserviceEndpointComplete;
	}

	public String getSmsServerIp() {
		return smsServerIp;
	}

	public void setSmsServerIp(String smsServerIp) {
		this.smsServerIp = smsServerIp;
	}

	public String getSmsServerUsername() {
		return smsServerUsername;
	}

	public void setSmsServerUsername(String smsServerUsername) {
		this.smsServerUsername = smsServerUsername;
	}

	public String getSmsServerPassword() {
		return smsServerPassword;
	}

	public void setSmsServerPassword(String smsServerPassword) {
		this.smsServerPassword = smsServerPassword;
	}
	
	

}
