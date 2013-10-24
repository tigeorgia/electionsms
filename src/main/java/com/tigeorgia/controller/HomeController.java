package com.tigeorgia.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tigeorgia.util.Constants;

@Controller
@RequestMapping({"/","/home"})
public class HomeController {
	
	/**
	 * Shows home page.
	 * @param model
	 * @return view
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String welcome(ModelMap model) {

		return Constants.HOME_VIEW;
 
	}

}
