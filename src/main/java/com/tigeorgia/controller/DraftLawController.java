package com.tigeorgia.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.tigeorgia.model.GoogleInformation;
import com.tigeorgia.util.Constants;
import com.tigeorgia.util.GoogleSpreadsheetParser;
import com.tigeorgia.util.Utilities;

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

	/*@Autowired
	private DraftlawService draftLawService;*/

	@Autowired
	private GoogleInformation googleInformation;
	
	@Autowired
	private String pathToShenmartavScript;
	
	@Autowired
	private String pathToSqlFile;

	/*@Autowired
	private DraftlawDiscussionService draftLawDiscussionService;*/

	public static final LinkedList<String> colNamesForDatabase = new LinkedList<String>(Arrays.asList("bureauDate","billNumber","fullTextUrl","fullTextUrlEn","fullTextUrlKa",
			"title","titleKa","titleEn",
			"initiator","initiatorKa","initiatorEn",
			"author","authorKa","authorEn",
			"summary","summaryKa","summaryEn",
			"lawNumber","numberDaysDiscussion","slug"));

	public static final LinkedList<String> colNamesForDiscussions = new LinkedList<String>(Arrays.asList("discussionDate","stage",
			"place","placeEn","placeKa","passed"));

	public static final SimpleDateFormat fieldDateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy");

	// Bureau date format, for validation
	public static final SimpleDateFormat dateFormatForInsert = new SimpleDateFormat("MM.dd.yyyy");

	@RequestMapping(method = RequestMethod.GET)
	public String draftlawPage(ModelMap model) {

		DraftLawPage pageModel = new DraftLawPage();

		// Get Draft law Google spreadsheet
		spreadsheet = GoogleSpreadsheetParser.getGoogleSpreadsheet(service, googleInformation);

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

			ArrayList<DraftlawValidationMessage> errors = new ArrayList<DraftlawValidationMessage>();

			// We get the date when the last scraping was performed
			List<String> discussionsToDelete = new ArrayList<String>();
			List<String> discussionsToInsert = new ArrayList<String>();

			List<String> draftlawToDelete = new ArrayList<String>();
			List<String> draftlawToInsert = new ArrayList<String>();

			// Initializing the output file
			File file = new File(pathToSqlFile+"/updateDraftlaws.sql");
			if (file.exists()) {
				file.delete();
			}

			try {
				file.createNewFile();
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);

				for (Draftlaw draftlaw : draftlaws){

					Set<DraftlawDiscussion> discussions = draftlaw.getDiscussions();

					String bureauDate = dateFormatForInsert.format(draftlaw.getBureauDate());

					// We update here the existing discussions (those that are already in the database)
					List<Integer> alreadyExistingDiscussionStages = new ArrayList<Integer>();
					for (DraftlawDiscussion discussion : discussions){
						alreadyExistingDiscussionStages.add(discussion.getStage());

						String discussionDate = dateFormatForInsert.format(discussion.getDiscussionDate());

						String deleteQuery = "DELETE FROM draftlaw_draftlawdiscussion "
								+ "WHERE draftlaw_id = (SELECT id FROM draftlaw_draftlaw WHERE bill_number = '" + draftlaw.getBillNumber() + "') "
								+ "AND stage = " + discussion.getStage() + ";\n";

						discussionsToDelete.add(deleteQuery);
						
						deleteQuery = "DELETE FROM draftlaw_draftlawdiscussion "
								+ "WHERE draftlaw_id = (SELECT id FROM draftlaw_draftlaw WHERE slug = '" + draftlaw.getSlug() + "') "
								+ "AND stage = " + discussion.getStage() + ";\n";

						discussionsToDelete.add(deleteQuery);

						String insertQuery = "INSERT INTO draftlaw_draftlawdiscussion (draftlaw_id,date,stage,place,place_en,place_ka,passed) VALUES " +
								"((SELECT id FROM draftlaw_draftlaw WHERE slug = '" + draftlaw.getSlug() +"'),"+ 
								"TO_DATE('"+discussionDate+"','MM.DD.YYYY')" +","+ discussion.getStage() +",'"+ discussion.getPlace() +"','"+ 
								discussion.getPlace_en() +"','"+ discussion.getPlace_ka() +"','N');\n";

						discussionsToInsert.add(insertQuery);

					}
					
					String deleteDraftlawQuery = "DELETE FROM draftlaw_draftlaw WHERE bill_number = '" + draftlaw.getBillNumber() + "';\n";

					draftlawToDelete.add(deleteDraftlawQuery);

					deleteDraftlawQuery = "DELETE FROM draftlaw_draftlaw WHERE slug = '" + draftlaw.getSlug() + "';\n";

					draftlawToDelete.add(deleteDraftlawQuery);

					String insertDraftlawQuery = "INSERT INTO draftlaw_draftlaw (bureau_date,bill_number,title,title_en,"
							+ "title_ka,initiator,initiator_en,initiator_ka,author,author_en,author_ka,status,status_en,status_ka,"
							+ "summary,summary_en,summary_ka,full_text,full_text_url,enacted_text_url,related_1,related_2,related_3," 
							+ "related_4,related_5,slug) "
							+ "VALUES (TO_DATE('"+bureauDate+"','MM.DD.YYYY'),'"+ draftlaw.getBillNumber() +"','"+draftlaw.getTitle()+"','"+draftlaw.getTitleEn()+"','"
							+ draftlaw.getTitleKa()+"','"+draftlaw.getInitiator()+"','"+draftlaw.getInitiatorEn()+"','"+draftlaw.getInitiatorKa()+"','"+draftlaw.getAuthor()+"','"
							+ draftlaw.getAuthorEn()+"','"+ draftlaw.getAuthorKa() +"','"+ draftlaw.getStatus()+"','"+ draftlaw.getStatusEn()+"','"+draftlaw.getStatusKa()+"','"
							+ draftlaw.getSummary()+"','"+draftlaw.getSummaryEn()+"','"+draftlaw.getSummaryKa()+"','"
							+ draftlaw.getFullText()+"','"+draftlaw.getFullTextUrl()+"','"+draftlaw.getEnactedTextUrl()+"','','','','','','"+draftlaw.getSlug()+"');\n";

					draftlawToInsert.add(insertDraftlawQuery);

				}

				// We now write the queries in the output file

				for (String query : discussionsToDelete){
					bw.write(query);
				}

				for (String query : draftlawToDelete){
					bw.write(query);
				}

				for (String query : draftlawToInsert){
					bw.write(query);
				}

				for (String query : discussionsToInsert){
					bw.write(query);
				}

				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			model.addAttribute("isUpdateSuccess", true);
			String message = "SQL file has been generated: " + pathToSqlFile + "/updateDraftlaws.sql";
			model.addAttribute("successMessage", message);
			
			if (errors != null && errors.size() > 0){
				model.addAttribute("errors", errors);
			}

		}

		return Constants.DRAFTLAW_IMPORT_VIEW;

	}

}
