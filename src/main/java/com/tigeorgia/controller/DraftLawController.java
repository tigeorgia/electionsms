package com.tigeorgia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tigeorgia.model.Draftlaw;
import com.tigeorgia.service.DraftlawService;
import com.tigeorgia.util.Constants;

@Controller
@RequestMapping("/draftlaw-import")
public class DraftLawController {
	
	/**
	 * Shows draft law import page
	 * @param model
	 * @return view
	 */
	
	@Autowired
	private DraftlawService draftLawService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String draftlawPage() {
		
		
		return Constants.DRAFTLAW_IMPORT_VIEW;
	}

}
