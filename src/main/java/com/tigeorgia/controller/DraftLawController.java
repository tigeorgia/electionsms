package com.tigeorgia.controller;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tigeorgia.model.Draftlaw;
import com.tigeorgia.model.DraftlawContainer;
import com.tigeorgia.model.DraftlawValidationMessage;
import com.tigeorgia.service.DraftlawService;
import com.tigeorgia.util.Constants;
import com.tigeorgia.util.GoogleSpreadsheetParser;

@Controller
@RequestMapping("/draftlaw-import")
public class DraftLawController {
	
	/**
	 * Shows draft law import page
	 * @param model
	 * @return view
	 */
	
	@Autowired
	@Resource(name="draftlawHeadings")
	private Map<String,String> draftlawHeadings;
	
	@Autowired
	private DraftlawService draftLawService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String draftlawPage() {
		
		return Constants.DRAFTLAW_IMPORT_VIEW;
	}
	
	@RequestMapping(value="/upload", method = RequestMethod.GET)
	public String uploadDraftlawInfo(ModelMap model) {
		
		// Get Draft law Google spreadsheet
		
		// Analyze every row of the spreadsheet, and for each of them, apply the set of validation 
		
		// If every validation passed for all the rows, then we can update safely the MyParliament DB
		// If not, display error messages, and what needs to be fixed.
		DraftlawContainer draftlawInfo = GoogleSpreadsheetParser.validateSpreadsheet(draftlawHeadings);
		
		ArrayList<DraftlawValidationMessage> validatedSpreadsheet = draftlawInfo.getErrorMessages();
		
		if (validatedSpreadsheet != null && validatedSpreadsheet.size() > 0){
			model.addAttribute("errorMessages", validatedSpreadsheet);
		}else{
			// All the draft laws are valid, we can upload them.
			ArrayList<Draftlaw> draftlaws = draftlawInfo.getDraftlaws();
			
			for (Draftlaw draftlaw : draftlaws){
				Draftlaw draftlawToCheck = draftLawService.getDraftlaw(draftlaw.getBillNumber());
				if (draftlawToCheck != null){
					// the draft law already exist, we just need to update it
					draftlaw.setId(draftlawToCheck.getId());
					draftLawService.updateDraftlaw(draftlaw);
					System.out.println("updated " + draftlaw.getBillNumber() + " (id: " + draftlaw.getId() + ")");
				}else{
					// it's a new draft law, we need to create it in the database
					draftLawService.addDraftlaw(draftlaw);
				}
			}
			
			model.addAttribute("isUpdateSuccess", true);
			
		}
		
		return Constants.DRAFTLAW_IMPORT_VIEW;
		
	}

}
