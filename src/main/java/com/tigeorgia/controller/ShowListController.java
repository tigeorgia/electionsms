package com.tigeorgia.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tigeorgia.model.Person;
import com.tigeorgia.util.Constants;
import com.tigeorgia.util.Utilities;

@Controller
@RequestMapping("/showlist")
public class ShowListController {

	private static final Logger logger = Logger.getLogger(ShowListController.class);

	/**
	 * Shows list of recipients, after reading their details from a CSV file.
	 * @param model
	 * @return view
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String showlist(ModelMap model) {

		List<Person> recipients = Utilities.getListOfRecipients(logger);
		
		if (recipients == null){
			model.addAttribute("errorMessage", "There was a problem while trying to retrieve the recipient list.");
		}
		model.addAttribute("recipients", recipients);

		return Constants.RECIPIENT_LIST_VIEW;

	}

}
