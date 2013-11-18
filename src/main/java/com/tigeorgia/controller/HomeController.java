package com.tigeorgia.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tigeorgia.util.Constants;

@Controller
public class HomeController {
	
	/**
	 * Shows home page.
	 * @param model
	 * @return view
	 */
	@RequestMapping(value="/", method = RequestMethod.GET)
	public String welcome() {
		return Constants.HOME_VIEW;
	}
	
	/**
	 * Login screen.
	 */
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String loginForm(){
		return Constants.LOGIN_VIEW;
	}
	
	/**
	 * Login screen, after attempt to login failed.
	 */
	@RequestMapping(value="/error", method=RequestMethod.GET)
	public String loginErrorForm(ModelMap model){
		model.addAttribute("errorFlag", true);
		return Constants.LOGIN_VIEW;
	}

}
