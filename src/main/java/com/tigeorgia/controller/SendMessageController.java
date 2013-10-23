package com.tigeorgia.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/sendmessage")
public class SendMessageController {

	private static final Logger logger = Logger.getLogger(SendMessageController.class);
	
	@RequestMapping(method = RequestMethod.GET)
	public String sendMessage(ModelMap model) {
		return "messageform";
 
	}
	
}
