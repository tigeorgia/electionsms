package com.tigeorgia.webservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.springframework.xml.xpath.XPathOperations;

import com.tigeorgia.model.Message;
import com.tigeorgia.model.Person;
import com.tigeorgia.model.Summary;
import com.tigeorgia.util.Constants;
import com.tigeorgia.util.Utilities;


public class MagtiClient {

	@Autowired
	private RestTemplate restTemplate;

	private String magtiWebserviceEndpoint;

	Map<String, String> wsVariables;

	@Autowired
	private XPathOperations xpathTemplate;

	private static final Logger logger = Logger.getLogger(MagtiClient.class);

	public Summary sendMessages(Message message) {

		//InputStream inputStream = getClass().getClassLoader().getResourceAsStream("PhoneNumberList.csv");
		//InputStream inputStream  = Utilities.getRecipientFile(logger);
		List<Person> recipients = Utilities.getListOfRecipients(logger);
		Summary summary = null;
		if (recipients != null){
			String magtiResponse = null;
			summary = new Summary();
			if (recipients != null && recipients.size() > 0){
				// We have the whole list of recipients, we need to get the chosen ones, 
				// based on the selected groups.
				ArrayList<String> chosenGroups = message.getChosenGroups();
				boolean allGroups = (chosenGroups.size() == Constants.TOTAL_NUMBER_OF_GROUPS);
				List<Person> peopleWhoDidntReceive = new ArrayList<Person>();
				int countTotalMessageSent = 0;
				int countSuccess = 0;
				int countFail = 0;

				for (Person recipient : recipients){
					if (allGroups || isInChosenGroup(recipient, chosenGroups)){
						// Recipient is in chosen group, we send the message.
						ArrayList<String> recipientNumbers = recipient.getNumbers();
						// TODO: take care of the 2nd number, if any.
						wsVariables.put("to", recipientNumbers.get(0));
						wsVariables.put("text", message.getBody());

						magtiResponse = restTemplate.getForObject(magtiWebserviceEndpoint, String.class, wsVariables);
						countTotalMessageSent++;
						if (magtiResponse.equalsIgnoreCase(Constants.MAGTI_WSCODE_SUCCESS)){
							countSuccess++;
						}else{
							countFail++;
							Person recipientDidntReceive = new Person(recipient.getName(), recipient.getNumbers(), recipient.getGroup());
							recipientDidntReceive.setErrorCode(magtiResponse);
							peopleWhoDidntReceive.add(recipientDidntReceive);
						}

					}
				}
				summary.setFailNumber(countFail);
				summary.setSuccessNumber(countSuccess);
				summary.setSuccessNumber(countTotalMessageSent);
				summary.setDidntReceive(peopleWhoDidntReceive);
			}
		}

		return summary;
	}

	private boolean isInChosenGroup(Person recipient, ArrayList<String> chosenGroups){
		String personGroup = recipient.getGroup();
		for (String group : chosenGroups){
			if (personGroup.equalsIgnoreCase(group)){
				return true;
			}
		}
		return false;
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

}
