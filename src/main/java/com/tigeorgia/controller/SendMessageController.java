package com.tigeorgia.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tigeorgia.model.Message;
import com.tigeorgia.util.Constants;

@Controller
@RequestMapping("/sendmessage")
public class SendMessageController {
	
	private static final int TOTAL_NUMBER_OF_GROUPS = 11; 

	private static final Logger logger = Logger.getLogger(SendMessageController.class);
	
	/**
	 * Shows form to send a message.
	 * @param model
	 * @return view
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String writeMessage(ModelMap model) {
		Message message = new Message();
		
		model.addAttribute("messageModel", message);
		model.addAttribute("groups", initGroups());
		
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
			ArrayList<String> messageGroups = messageToBeSent.getChosenGroups();
			String messageBody = messageToBeSent.getBody();
			
			// Definition of recipient groups
			if (messageGroups != null && messageGroups.size() > 0){
				
				if (messageBody != null && !messageBody.isEmpty()){
					
					//---------------------------
					// Send message to Magti here
					//---------------------------
					
					String message = Constants.MESSAGE_TAG + " Message sent: " + messageBody 
							+ " - recipients: ";
					if (messageGroups.size() == TOTAL_NUMBER_OF_GROUPS){
						message += "All";
					}else{
						for (String group : messageGroups){
							message += group + ", ";
						}
						message = message.substring(0, message.length()-2);
					}
					
					logger.info(message);
					model.addAttribute("validMessage", "Your message has been sent.");
					model.addAttribute("messageModel", new Message());
					
				}else{
					model.addAttribute("errorMessage", "Please write a message to send.");
				}
			}else{
				model.addAttribute("errorMessage", "Please select at least a group.");
			}
		}
		
		model.addAttribute("groups", initGroups());
		
		
		return Constants.SEND_MESSAGE_VIEW;
	}
	
	private List<String> initGroups(){
		List<String> groups = new ArrayList<String>();
		groups.add("Group 1");
		groups.add("Group 2");
		groups.add("Group 3");
		groups.add("Group 4");
		groups.add("Group 5");
		groups.add("Group 6");
		groups.add("Group 7");
		groups.add("Group 8");
		groups.add("Group 9");
		groups.add("Group 10");
		groups.add("Mobile group");
		return groups;
		
	}
	
}