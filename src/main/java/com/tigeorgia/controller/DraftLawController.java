package com.tigeorgia.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.tigeorgia.model.DraftLawPage;
import com.tigeorgia.model.Draftlaw;
import com.tigeorgia.model.DraftlawContainer;
import com.tigeorgia.model.DraftlawDiscussion;
import com.tigeorgia.model.DraftlawValidationMessage;
import com.tigeorgia.model.Message;
import com.tigeorgia.service.DraftlawDiscussionService;
import com.tigeorgia.service.DraftlawService;
import com.tigeorgia.util.Constants;
import com.tigeorgia.util.GoogleSpreadsheetParser;

@Controller
@RequestMapping("/draftlaw-import")
public class DraftLawController {

		
	@Autowired
	@Resource(name="spreadsheet")
	private SpreadsheetEntry spreadsheet;
	
	@Autowired
	@Resource(name="service")
	private SpreadsheetService service;

	@Autowired
	@Resource(name="draftlawHeadings")
	private Map<String,String> draftlawHeadings;

	@Autowired
	private DraftlawService draftLawService;

	@Autowired
	private DraftlawDiscussionService draftLawDiscussionService;

	public static final LinkedList<String> colNamesForDatabase = new LinkedList<String>(Arrays.asList("bureauDate","billNumber","fullTextUrl","fullTextUrlEn","fullTextUrlKa",
			"title","titleKa","titleEn",
			"initiator","initiatorKa","initiatorEn",
			"author","authorKa","authorEn",
			"summary","summaryKa","summaryEn",
			"lawNumber","numberDaysDiscussion","slug"));

	public static final LinkedList<String> colNamesForDiscussions = new LinkedList<String>(Arrays.asList("discussionDate","stage",
			"place","placeEn","placeKa","passed"));

	SimpleDateFormat fieldDateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy");

	@RequestMapping(method = RequestMethod.GET)
	public String draftlawPage(ModelMap model) {
		
		DraftLawPage pageModel = new DraftLawPage();
		
		// Get Draft law Google spreadsheet
		spreadsheet = GoogleSpreadsheetParser.getGoogleSpreadsheet(service);
		
		// Let's get all the pages title. We'll work on the 2014 pages onwards.
		List<String> pageTitles = GoogleSpreadsheetParser.getAllPageNames(spreadsheet);
		List<String> pageTitlesToInclude = new ArrayList<String>();
		for (String title : pageTitles){
			if (!title.contains("2013") && !title.contains("2012")){
				pageTitlesToInclude.add(title);
			}
		}
		pageModel.setPage(pageTitlesToInclude.get(0));
		
		model.addAttribute("titleList", pageTitlesToInclude);
		model.addAttribute("draftLawPageModel", pageModel);
		return Constants.DRAFTLAW_IMPORT_VIEW;
	}

	@RequestMapping(value="/upload", method = RequestMethod.POST)
	public String uploadDraftlawInfo(@ModelAttribute("draftLawPageModel") DraftLawPage draftlawPage, ModelMap model) {
		
		// Analyze every row of the spreadsheet, and for each of them, apply the set of validation 

		// If every validation passed for all the rows, then we can update safely the MyParliament DB
		// If not, display error messages, and what needs to be fixed.
		DraftlawContainer draftlawInfo = GoogleSpreadsheetParser.validateSpreadsheet(spreadsheet, draftlawPage.getPage(), service, draftlawHeadings);

		ArrayList<DraftlawValidationMessage> validatedSpreadsheet = draftlawInfo.getErrorMessages();

		if (validatedSpreadsheet != null && validatedSpreadsheet.size() > 0){
			model.addAttribute("errorMessages", validatedSpreadsheet);
		}else{
			// All the draft laws are valid, we can upload them.
			ArrayList<Draftlaw> draftlaws = draftlawInfo.getDraftlaws();
			
			// Counting new and updated draft laws.
			int newDraftlawCount = 0;
			
			ArrayList<DraftlawValidationMessage> discussionErrors = new ArrayList<DraftlawValidationMessage>();

			for (Draftlaw draftlaw : draftlaws){
				Draftlaw draftlawToSendBack = draftLawService.getDraftlawWithDiscussions(draftlaw.getBillNumber());

				if (draftlawToSendBack != null){
					// the draft law already exist, we just need to update it
					List<DraftlawDiscussion> discussionsToSendBack = draftLawDiscussionService.getDraftlawDiscussionByDraftLawId(draftlawToSendBack.getId());

					// We stored these updated discussions in a map, so they can be reused conveniently later. Discussion stage will be used as the key.
					Map<Integer, DraftlawDiscussion> updatedDiscussionMap = null;
					if (discussionsToSendBack != null && discussionsToSendBack.size() > 0){
						updatedDiscussionMap = new HashMap<Integer, DraftlawDiscussion>();
						for (DraftlawDiscussion updatedDiscussion : draftlaw.getDiscussions()){
							updatedDiscussionMap.put(updatedDiscussion.getStage(), updatedDiscussion);
						}
					}

					// updating draft law fields
					for (String colName : colNamesForDatabase){
						try {
							String valueToBeSet = BeanUtils.getProperty(draftlaw, colName);
							if (colName.equalsIgnoreCase("bureauDate")){
								Date valueToBeSetDate = fieldDateFormat.parse(valueToBeSet);
								BeanUtils.setProperty(draftlawToSendBack,colName,valueToBeSetDate);
							}else{
								BeanUtils.setProperty(draftlawToSendBack,colName,valueToBeSet);
							}

						} catch (IllegalAccessException
								| InvocationTargetException
								| NoSuchMethodException | ParseException e) {
						}
					}

					// We update here the existing discussions (those that are already in the database)
					List<Integer> alreadyExistingDiscussionStages = new ArrayList<Integer>();
					if (discussionsToSendBack != null && discussionsToSendBack.size() > 0){
						for (DraftlawDiscussion discussion : discussionsToSendBack){
							alreadyExistingDiscussionStages.add(discussion.getStage());
							for (String colName : colNamesForDiscussions){
								try {
									DraftlawDiscussion discussionToRead = updatedDiscussionMap.get(discussion.getStage());
									String valueToBeSet = BeanUtils.getProperty(discussionToRead, colName);
									if (colName.equalsIgnoreCase("discussionDate")){
										Date valueToBeSetDate = fieldDateFormat.parse(valueToBeSet);
										BeanUtils.setProperty(discussion,colName,valueToBeSetDate);
									}else{
										BeanUtils.setProperty(discussion,colName,valueToBeSet);
									}

								} catch (IllegalArgumentException e1){
									DraftlawValidationMessage discMessage = new DraftlawValidationMessage();
									discMessage.setMessage("Error occured while updating discussions for draftlaw " + draftlaw.getBillNumber() + ". "
											+ "Are there unexpected differences between the spreadsheet and the website (ie past discussion appearing only on the website)?");
									discussionErrors.add(discMessage);
								} catch (IllegalAccessException
										| InvocationTargetException
										| NoSuchMethodException | ParseException e) {
								}

							}
							draftLawDiscussionService.updateDraftlawDiscussion(discussion);
						}
					}


					// We add here the new discussions, to the discussion set.
					for (DraftlawDiscussion updatedDiscussion : draftlaw.getDiscussions()){
						if (!alreadyExistingDiscussionStages.contains(updatedDiscussion.getStage())){
							// We didn't process this discussion in the previous for loop: it's a new one.
							updatedDiscussion.setDraftlaw_id(draftlawToSendBack.getId());
							draftLawDiscussionService.addDraftlawDiscussion(updatedDiscussion);
						}
					}

					draftLawService.updateDraftlaw(draftlawToSendBack);

					System.out.println("updated " + draftlaw.getBillNumber() + " (id: " + draftlaw.getId() + ")");
				}else{
					// it's a new draft law, we need to create it in the database
					draftLawService.addDraftlaw(draftlaw);
					newDraftlawCount++;

					// we then create the discussions attached to it.
					for (DraftlawDiscussion updatedDiscussion : draftlaw.getDiscussions()){
						updatedDiscussion.setDraftlaw_id(draftlaw.getId());
						draftLawDiscussionService.addDraftlawDiscussion(updatedDiscussion);
					}
					System.out.println("added " + draftlaw.getBillNumber() + " (id: " + draftlaw.getId() + ")");
				}
			}

			model.addAttribute("isUpdateSuccess", true);
			String message = "Success! The draft laws have been imported to the MyParliament database.\n"
					+ "<strong>" + Integer.toString(newDraftlawCount) + " new draft laws have been added. The existing ones have been updated.</strong>\n"
					+ "Go check it out on the MyParliament website: <a href =\"http://chemiparlamenti.ge/en/what/\" target=\"_blank\">Draft law page</a>";
			model.addAttribute("successMessage", message);
			
			if (discussionErrors != null && discussionErrors.size() > 0){
				model.addAttribute("discussionErrors", discussionErrors);
			}

		}

		return Constants.DRAFTLAW_IMPORT_VIEW;

	}

}
