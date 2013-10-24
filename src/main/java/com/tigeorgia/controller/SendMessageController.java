package com.tigeorgia.controller;

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
		return Constants.SEND_MESSAGE_VIEW;
 
	}
	
	/**
	 * Processes message to be sent to recipients, via Magti webservices.
	 * @param messageToBeSent
	 * @return view
	 */
	@RequestMapping(value="/result", method = RequestMethod.POST)
	public String sendMessage(@ModelAttribute("messageModel") Message messageToBeSent){
		
		if (messageToBeSent != null){
			String messageBody = messageToBeSent.getBody();
			if (messageBody != null && !messageBody.isEmpty()){
				
				//---------------------------
				// Send message to Magti here
				//---------------------------
				
				logger.info(Constants.MESSAGE_TAG + " Message sent: " + messageBody);
			}
		}
		
		
		return Constants.SEND_MESSAGE_VIEW;
	}
	
}