package com.tigeorgia.webservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestTemplate;
import org.springframework.xml.xpath.XPathOperations;

import com.tigeorgia.controller.ShowListController;
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

	/**
	 * Method called asynchronously which sends the text message, to the selected groups of recipients. 
	 * @param message
	 * @param contactType
	 */
	@Async
	public void sendMessages(Message message, String contactType) {
		
		List<Person> recipients = null;
		if (contactType.equalsIgnoreCase(Utilities.ELECTION_CONTACT_TYPE)){
			CsvFile file = Utilities.getListOfRecipients(logger);
			if (file != null){
				recipients = file.getRecipients();
			}
		}else if (contactType.equalsIgnoreCase(Utilities.PARLIAMENT_CONTACT_TYPE)){
			// We retrieve the Parliament list from the MyParliament API
			recipients = Utilities.getParliamentaryContacts();
		}
		
		
		Summary summary = null;
		int countTotalMessageSent = 0;
		int countSuccess = 0;
		int countFail = 0;
		List<Person> peopleWhoDidntReceive = new ArrayList<Person>();
		
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
				
				// Starting to log the progress of message sending.
				logger.info(Constants.MESSAGE_TAG + "------------------------------");
				logger.info(Constants.MESSAGE_TAG + " Message currently processed: " + message.getBody());

				for (Person recipient : recipients){
					if ((allGroups || isInChosenGroup(recipient, chosenGroups)) && isInChosenLanguage(recipient, chosenLanguage)){
						Thread.sleep(1000);
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
							
							// Sends SMS message to Magti, via SSH to our server connected via VPN to Magti.
							String script_path = ShowListController.class.getClassLoader().getResource("sendMessageOverSsh.py").getFile();
							ProcessBuilder pb = new ProcessBuilder("python", script_path, commandToPass, smsServerIp, smsServerUsername, smsServerPassword);
							Process p;
							try {
								p = pb.start();
								BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
								magtiResponse = in.readLine();
							} catch (IOException e) {
								e.printStackTrace();
							}
							
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
							
							if (countSuccess % 50 == 0){
								// Tracking progress of a messsage sent to a lot of people. 
								logger.info(Constants.MESSAGE_TAG + " " + countSuccess + " recipients have successfully received the message so far.");
							}
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
		
		// Logging the summary of all these message that have been sent
		if (summary != null){						
			// We log the summary here
			logger.info(Constants.MESSAGE_TAG + " All done.");
			// Logging successes and fails
			logger.info(Constants.MESSAGE_TAG + " Success: " + countSuccess + "/" + countTotalMessageSent);
			if (countSuccess < countTotalMessageSent) {
				logger.info(Constants.MESSAGE_TAG + " Fail: " + countFail + "/" + countTotalMessageSent);
			}

			// Third line: if there is any fails
			if (summary.getFailNumber() > 0){
				logger.info(Constants.MESSAGE_TAG + " People who did not receive message:");
				int i=0;
				for (Person person : summary.getDidntReceive()){
					i++;
					if (person.getNumbers() != null && person.getNumbers().size() >= 1){
						logger.info(Constants.MESSAGE_TAG + " ### " + i + ") " + person.getName() + " - " + person.getNumbers().get(0) + 
								" - Error code: " + person.getErrorCode());
					}else{
						logger.info(Constants.MESSAGE_TAG + " ### " + i + ") " + person.getName() + " - (phone number can't be read) - "
								+ "Error code: " + person.getErrorCode());
					}
					
				}
			}

			logger.info(Constants.MESSAGE_TAG + "------------------------------");

		} 
		
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
