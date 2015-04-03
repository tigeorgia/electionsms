package com.tigeorgia.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tigeorgia.model.CsvFile;
import com.tigeorgia.model.Message;
import com.tigeorgia.model.Person;
import com.tigeorgia.util.Constants;
import com.tigeorgia.util.Utilities;
import com.tigeorgia.webservice.MagtiClient;

@Controller
@RequestMapping("/sendmessage")
@EnableAsync
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
			
			ArrayList<String> parliamentGroups = messageToBeSent.getChosenParliamentaryGroups();
			ArrayList<String> electionGroups = messageToBeSent.getChosenElectionGroups();
			
			if ((electionGroups == null || (electionGroups != null && electionGroups.size() == 0)) 
					&& (parliamentGroups == null || (parliamentGroups != null && parliamentGroups.size() == 0))) {
				model.addAttribute("errorMessage", "Please select at least a group.");
			}else{
				sendMessageByChosenGroupType(messageToBeSent, model, Utilities.ELECTION_CONTACT_TYPE);
				sendMessageByChosenGroupType(messageToBeSent, model, Utilities.PARLIAMENT_CONTACT_TYPE);
			}
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
				magtiClient.sendMessages(messageToBeSent, contactType);
				
				model.addAttribute("validMessage", "The message is currently being sent to the recipients of the selected groups. Go to the logging page in a moment to see the sending report.");
			}else{
				model.addAttribute("errorMessage", "Please write a message to send.");
			}
		}
	}

	/**
	 * This method lists all the group names to be displayed on the "Send message" page, based off of the CSV file.
	 * @return
	 */
	private List<String> initGroups(String contactGroup){

		List<Person> recipientList = null;
		if (contactGroup.equalsIgnoreCase(Utilities.ELECTION_CONTACT_TYPE)){
			CsvFile file = Utilities.getListOfRecipients(logger, contactGroup);
			if (file != null){
				recipientList = file.getRecipients();
			}
		}else if (contactGroup.equalsIgnoreCase(Utilities.PARLIAMENT_CONTACT_TYPE)){
			// We retrieve the Parliament list from the MyParliament API
			recipientList = Utilities.getParliamentaryContacts();
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