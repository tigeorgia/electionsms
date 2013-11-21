package com.tigeorgia.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tigeorgia.model.CsvFile;
import com.tigeorgia.model.Message;
import com.tigeorgia.model.Person;
import com.tigeorgia.model.Summary;
import com.tigeorgia.util.Constants;
import com.tigeorgia.util.Utilities;
import com.tigeorgia.webservice.MagtiClient;

@Controller
@RequestMapping("/sendmessage")
public class SendMessageController {

	@Autowired
	private MagtiClient magtiClient;

	private static final String DEFAULT_LANGUAGE = "ka";

	private static final Logger logger = Logger.getLogger(SendMessageController.class);

	/**
	 * Shows form to send a message.
	 * @param model
	 * @return view
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String writeMessage(ModelMap model) {
		Message message = new Message();
		message.setLang(DEFAULT_LANGUAGE);
		model.addAttribute("messageModel", message);
		model.addAttribute("electionGroups", initGroups(Utilities.ELECTION_CONTACT_TYPE));
		model.addAttribute("parliamentGroups", initGroups(Utilities.PARLIAMENT_CONTACT_TYPE));

		return Constants.SEND_MESSAGE_VIEW;

	}

	/**
	 * Processes message to be sent to recipients, via Magti webservices.
	 * @param messageToBeSent
	 * @return view
	 */
	@RequestMapping(value="/result", method = RequestMethod.POST)
	public String sendMessage(@ModelAttribute("messageModel") Message messageToBeSent, ModelMap model){

		if (messageToBeSent != null){
			String messageBody = messageToBeSent.getBody();

			sendMessageByChosenGroupType(messageToBeSent, model, Utilities.ELECTION_CONTACT_TYPE);
			sendMessageByChosenGroupType(messageToBeSent, model, Utilities.PARLIAMENT_CONTACT_TYPE);
		}

		model.addAttribute("electionGroups", initGroups(Utilities.ELECTION_CONTACT_TYPE));
		model.addAttribute("parliamentGroups", initGroups(Utilities.PARLIAMENT_CONTACT_TYPE));


		return Constants.SEND_MESSAGE_VIEW;
	}

	private void sendMessageByChosenGroupType(Message messageToBeSent, ModelMap model, String contactType){
		
		String messageBody = messageToBeSent.getBody();
		ArrayList<String> messageGroups = null;
		if (contactType.equalsIgnoreCase(Utilities.ELECTION_CONTACT_TYPE)){
			messageGroups = messageToBeSent.getChosenElectionGroups();
		}else if (contactType.equalsIgnoreCase(Utilities.PARLIAMENT_CONTACT_TYPE)){
			messageGroups = messageToBeSent.getChosenParliamentaryGroups();
		}
		
		// Definition of recipient groups
		if (messageGroups != null && messageGroups.size() > 0){

			if (messageBody != null && !messageBody.isEmpty()){

				// Messages to be sent, taking place here
				Summary summary = magtiClient.sendMessages(messageToBeSent, contactType);

				// Logging taking place here
				if (summary != null){						
					// We log the summary here
					logger.info(Constants.MESSAGE_TAG + "------------------------------");
					// First line: text + recipient groups + chosen laguage
					String message = Constants.MESSAGE_TAG + " Message sent: " + messageBody 
							+ " - language: " + messageToBeSent.getLang() + " - recipients: ";
					if (messageGroups.size() == summary.getTotalNumberOfGroups()){
						message += "All";
					}else{
						for (String group : messageGroups){
							message += group + ", ";
						}
						message = message.substring(0, message.length()-2);
					}

					logger.info(message);

					// Second line: success and fails.
					message = Constants.MESSAGE_TAG + " Success: " + summary.getSuccessNumber()  + "/" + summary.getTotalNumber();;
					if (summary.getFailNumber() > 0){
						message += " - Fail: " + summary.getFailNumber() + "/" + summary.getTotalNumber();
					}

					logger.info(message);

					// Third line: if there is any fails
					if (summary.getFailNumber() > 0){
						logger.info(Constants.MESSAGE_TAG + " People who did not receive message:");
						int i=0;
						for (Person person : summary.getDidntReceive()){
							i++;
							logger.info(Constants.MESSAGE_TAG + " ### " + i + ") " + person.getName() + " - " + person.getNumbers().get(0) + 
									" - Error code: " + person.getErrorCode());
						}
						model.addAttribute("didntReceiveMessage", " Your message has been sent, but " + summary.getFailNumber() + " people did not receive the message (see logs)");
					}else{
						model.addAttribute("validMessage", "Your message has been sent to all the recipients you selected.");
					}

					logger.info(Constants.MESSAGE_TAG + "------------------------------");
					model.addAttribute("messageModel", new Message());
				}else{
					model.addAttribute("errorMessage", "The list of recipients (CSV file) could not be read"); 
				}

			}else{
				model.addAttribute("errorMessage", "Please write a message to send.");
			}
		}else{
			model.addAttribute("errorMessage", "Please select at least a group.");
		}
	}

	/**
	 * This method lists all the group names to be displayed on the "Send message" page, based off of the CSV file.
	 * @return
	 */
	private List<String> initGroups(String contactGroup){

		CsvFile file = Utilities.getListOfRecipients(logger, contactGroup);
		List<Person> recipientList = null;
		if (file != null){
			recipientList = file.getRecipients();
		}
		List<String> groups = null;

		if (recipientList != null){
			groups = new ArrayList<String>();
			for (Person recipient : recipientList){
				ArrayList<String> personGroups = recipient.getGroups();
				for (String group : personGroups){
					group = StringUtils.capitalize(group.toLowerCase());
					if (!groups.contains(group)){
						groups.add(group);
					}
				}
			}
		}

		return groups;

	}

}