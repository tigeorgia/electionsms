package com.tigeorgia.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import com.tigeorgia.model.Draftlaw;
import com.tigeorgia.model.DraftlawContainer;
import com.tigeorgia.model.DraftlawDiscussion;
import com.tigeorgia.model.DraftlawValidationMessage;
import com.tigeorgia.model.GoogleInformation;

public class GoogleSpreadsheetParser {
	
	public static final String REGULAR_EXPRESSION_FOR_BILL_NUMBER = "#\\d{2}-\\d/\\d{3}";


	public static final LinkedList<String> colNames = new LinkedList<String>(Arrays.asList("bureauDate","billNumber","registrationDate",
			"draftlawType", "draftlawTypeEn",
			"fullTextUrl",
			"primaryParentDraftLaw","primaryParentDraftLawEn",
			"titleKa","titleEn",
			"initiatorKa","initiatorEn",
			"authorKa","authorEn",
			"leadingCommitteeKa","leadingCommitteeEn",
			"summaryKa","summaryEn",
			"firstCommitteeHearing","firstPlenaryHearing","secondCommitteeHearing","secondPlenaryHearing","thirdCommitteeHearing","thirdPlenaryHearing",
			"lawNumber","numberDaysDiscussion"));
	
	// The following values are used to match Draftlaw class attributes, via reflection.
	public static final LinkedList<String> listOfMandatoryFields = new LinkedList<String>(Arrays.asList("bureauDate", "billNumber", "fullTextUrl", "draftlawType", "draftlawTypeEn", "primaryParentDraftLaw", "primaryParentDraftLawEn", "titleEn", "titleKa",
			"initiatorEn", "initiatorKa", "authorEn", "authorKa", "leadingCommitteeEn", "leadingCommitteeKa", "summaryKa"));
	
	public static final LinkedList<String> listOfDiscussionFields = new LinkedList<String>(Arrays.asList("firstCommitteeHearing","firstPlenaryHearing","secondCommitteeHearing","secondPlenaryHearing","thirdCommitteeHearing","thirdPlenaryHearing"));

	public static final LinkedList<String> statusLabelEn = new LinkedList<String>(Arrays.asList("1st Committee","1st Plenary","2nd Committee","2nd Plenary","3rd Committee","3rd Plenary"));
	public static final LinkedList<String> statusLabelKa = new LinkedList<String>(Arrays.asList("პირველი კომიტეტი","პირველი პლენარული","მეორე კომიტეტი","მეორე პლენარული","მესამე კომიტეტი","მესამე პლენარული"));
	public static final LinkedList<String> hearingLabelsKa = new LinkedList<String>(Arrays.asList("firstCommitteeHearing","firstPlenaryHearing","secondCommitteeHearing","secondPlenaryHearing","thirdCommitteeHearing","thirdPlenaryHearing"));
	public static final LinkedList<String> hearingLabelsEn = new LinkedList<String>(Arrays.asList("firstCommitteeHearingEn","firstPlenaryHearingEn","secondCommitteeHearingEn","secondPlenaryHearingEn","thirdCommitteeHearingEn","thirdPlenaryHearingEn"));
	
	
	public static SpreadsheetEntry getGoogleSpreadsheet(SpreadsheetService service, GoogleInformation googleInformation){
		SpreadsheetEntry spreadsheet = getGoogleSpreadsheetInfo(service, googleInformation);
		return spreadsheet;
	}
	
	public static List<String> getAllPageNames(SpreadsheetEntry spreadsheet){
		
		List<WorksheetEntry> worksheets;
		List<String> titles = new ArrayList<String>();
		try {
			worksheets = spreadsheet.getWorksheets();
			
			// Store the spreadsheet in a list of Draftlaw instances.
			for (WorksheetEntry worksheet : worksheets) {
				titles.add(worksheet.getTitle().getPlainText());
			}
			
		} catch (IOException | ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return titles;
					
	}
	
	public static DraftlawContainer validateSpreadsheet(SpreadsheetEntry spreadsheet, String spreadsheetTitle, SpreadsheetService service, Map<String, String> draftlawHeadings){
		
		List<WorksheetEntry> worksheets;
		
		DraftlawContainer result = new DraftlawContainer();
		
		// List to store read draft laws
		ArrayList<Draftlaw> parsedDraftlaws = new ArrayList<Draftlaw>();
		
		// List to store error message
		ArrayList<DraftlawValidationMessage> draftlawErrorMessages = new ArrayList<DraftlawValidationMessage>(); 
		
		// Bureau date format, for validation
		SimpleDateFormat fieldDateFormat = new SimpleDateFormat("MM.dd.yyyy");
		
		Date todaydate = new Date();
		
		
		try {
			worksheets = spreadsheet.getWorksheets();
			
			// Store the spreadsheet in a list of Draftlaw instances.
			for (WorksheetEntry worksheet : worksheets) {
				// Get the worksheet's title, row count, and column count.
				String title = worksheet.getTitle().getPlainText();

				System.out.println(title);
				
				// We taking out the georgian characters from the title, to test if we are on the right page.
				String[] titleSplit = spreadsheetTitle.split(" ");
				String shortSpreadsheetTitle = titleSplit[0] + " " + titleSplit[1];

				if (title != null && title.contains(shortSpreadsheetTitle)){

					// Print the fetched information to the screen for this worksheet.
					URL listFeedUrl = worksheet.getListFeedUrl();

					ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);

					// Iterate through each row, printing its cell values.
					int countrow=0;
					HashMap<String, Integer> billNumberHash = new HashMap<String, Integer>();
					
					for (ListEntry row : listFeed.getEntries()) {
						// Print the first column's cell value
						// Iterate over the remaining columns, and print each cell value
						if (countrow > 0){
							int countForColumn = 0;
							Draftlaw draftlaw = new Draftlaw();
							
							// The real row number matches the row number, in the spreadsheet.
							int realRowNumber = countrow + 2;
							
							for (String tag : row.getCustomElements().getTags()) {
								String fieldName = colNames.get(countForColumn);
								String value = row.getCustomElements().getValue(tag);
								
								if (fieldName.equalsIgnoreCase("bureauDate")){
									try {
										if (value != null && !value.isEmpty()){
											Date bureauDate = fieldDateFormat.parse(value);

											String dateForTest = fieldDateFormat.format(bureauDate);
											if (!dateForTest.equalsIgnoreCase(value)){
												// This test is to make sure that the date was not entered with the 'DD.MM.YYYY' format (day and month inversed)
												//throw new ParseException(bureauDate.toString(), 0);
												DraftlawValidationMessage message = new DraftlawValidationMessage();
												message.setMessage("The bureau date '"+value+"' does not match the mandatory format 'MM.DD.YYYY', for the draft law on row "
															+ Integer.toString(realRowNumber) + " (page '" + spreadsheetTitle +"')");
												draftlawErrorMessages.add(message);
											}else if (bureauDate.after(todaydate)){
												// The bureau date cannot be after today's date.
												DraftlawValidationMessage message = new DraftlawValidationMessage();
												message.setMessage("The bureau date '"+value+"' cannot be after today's date, for the draft law on row "
															+ Integer.toString(realRowNumber) + " (page '" + spreadsheetTitle +"')");
												draftlawErrorMessages.add(message);
											}else{
												draftlaw.setBureauDate(bureauDate);
											}
										}
									} catch (ParseException e) {
										
									} 
											
								}else{
									// Setting information in draftlaw instance, by reflection.
									if (value != null){
										BeanUtils.setProperty(draftlaw,fieldName,value.replaceAll("'", ""));
									}else{
										BeanUtils.setProperty(draftlaw,fieldName,"");
									}
								}
								
								countForColumn++;
							}
							
							if (draftlaw.getPrimaryParentDraftLawEn().equalsIgnoreCase("Primary")){
								// We take care of the Primary draft laws only, for now. Not the part of the package.
								draftlaw.setTitle(draftlaw.getTitleEn());
								draftlaw.setAuthor(draftlaw.getAuthorEn());
								draftlaw.setInitiator(draftlaw.getInitiatorEn());
								draftlaw.setSummary(draftlaw.getSummaryKa());
								draftlaw.setFullText(draftlaw.getSummaryKa());
								draftlaw.setEnactedTextUrl("");
								draftlaw.setRelatedOne("");
								draftlaw.setRelatedTwo("");
								draftlaw.setRelatedThree("");
								draftlaw.setRelatedFour("");
								draftlaw.setRelatedFive("");
								draftlaw.setModerateAnnotations(new Date());
								
								String slug = draftlaw.getBillNumber().substring(1,draftlaw.getBillNumber().length()).replaceAll("/", "");

								// Tracking the number of bill numbers that were processed
								if (billNumberHash.containsKey(draftlaw.getBillNumber())){
									int value = billNumberHash.get(draftlaw.getBillNumber());
									slug = slug + "-" + Integer.toString(value+1);
									billNumberHash.put(draftlaw.getBillNumber(), value+1);
								}else{
									billNumberHash.put(draftlaw.getBillNumber(), 1);
									slug = slug + "-1";
								}
								
								
								draftlaw.setSlug(slug);
								
								parsedDraftlaws.add(draftlaw);
							}
							
							// For this Draft law, we check if all the mandatory fields have been filled.
							// If a mandatory field ha not been filled, we log it in a list, so we can display 
							// an appropriate error message later.
							for (String mandatoryField : listOfMandatoryFields){
								try {
									String fieldValue = BeanUtils.getProperty(draftlaw, mandatoryField);
									if ((fieldValue == null) || (fieldValue != null && fieldValue.isEmpty())){
										DraftlawValidationMessage message = new DraftlawValidationMessage();
										message.setMessage("The mandatory field '" + draftlawHeadings.get(mandatoryField) + 
												"' is missing, for the draft law on row " + Integer.toString(realRowNumber) + " (page '" + spreadsheetTitle +"')");
										draftlawErrorMessages.add(message);
									}
								} catch (NoSuchMethodException e) {
									// There was a problem while reading a field. We log it.
									DraftlawValidationMessage message = new DraftlawValidationMessage();
									message.setMessage("There was a problem while reading the field '" + draftlawHeadings.get(mandatoryField) + 
											"' for the draft law on row " + Integer.toString(realRowNumber) + " (page '" + spreadsheetTitle +"')");
									draftlawErrorMessages.add(message);
								}
							}
														
							// Registration number should match following format: “#xx-x/xxx”, where x is a number.
							String billNumber = draftlaw.getBillNumber();
							if (!billNumber.matches(REGULAR_EXPRESSION_FOR_BILL_NUMBER)){
								DraftlawValidationMessage message = new DraftlawValidationMessage();
								message.setMessage("The registration number '"+billNumber+"' does not match the '#xx-x/xxx' format, where x must be a digit - for the draft law on row "
											+ Integer.toString(realRowNumber) + " (page '" + spreadsheetTitle +"')");
								draftlawErrorMessages.add(message);
							}
							
							// Now let's work on this draft law's discussions.
							Set<DraftlawDiscussion> discussions = new HashSet<DraftlawDiscussion>();
							int stage = 0;
							
							// initialization of the variable that will be used to define the draft law's status.
							int highestStage = -1;
							String highestStageDate = null;
									
							
							for (String discussionFieldName : listOfDiscussionFields){
								
								String discussionFieldValue = null;
								try {
									discussionFieldValue = BeanUtils.getProperty(draftlaw, discussionFieldName);
								} catch (NoSuchMethodException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								
								if (discussionFieldValue != null && !discussionFieldValue.isEmpty()){

									DraftlawDiscussion discussion = new DraftlawDiscussion();
									
									String hearingDateStr = getDateFromHearingDescription(discussionFieldValue);
									Date hearingDate = null;
									if (hearingDateStr != null){
										try{
											hearingDate = fieldDateFormat.parse(hearingDateStr);

											String dateForTest = fieldDateFormat.format(hearingDate);
											if (!dateForTest.equalsIgnoreCase(hearingDateStr)){
												// This test is to make sure that the date was not entered with the 'DD.MM.YYYY' format (day and month inversed)
												DraftlawValidationMessage message = new DraftlawValidationMessage();
												message.setMessage("The date '"+discussionFieldValue+"', in field'"+discussionFieldName+"' does not match the mandatory format 'MM.DD.YYYY', for the draft law on row "
															+ Integer.toString(realRowNumber) + " (page '" + spreadsheetTitle +"')");
												draftlawErrorMessages.add(message);
												continue;
											}else if (hearingDate.after(todaydate)){
												// The discussion date cannot be in the future
												DraftlawValidationMessage message = new DraftlawValidationMessage();
												message.setMessage("The date '"+discussionFieldValue+"', in field'"+discussionFieldName+"' cannot be set in the future, for the draft law on row "
															+ Integer.toString(realRowNumber) + " (page '" + spreadsheetTitle +"')");
												draftlawErrorMessages.add(message);
												continue;
											}else{
												discussion.setDiscussionDate(hearingDate);
											}
											
										} catch (ParseException e) {}
									}else if (discussionFieldValue != null && hearingDateStr == null ){
										DraftlawValidationMessage message = new DraftlawValidationMessage();
										message.setMessage("The date in '"+discussionFieldValue+"', in the field'"+discussionFieldName+"' is invalid, for the draft law on row "
													+ Integer.toString(realRowNumber) + " (page '" + spreadsheetTitle +"'). Please enter a valid date (MM.DD.YYYY), if you're writing some details about a discussion.");
										draftlawErrorMessages.add(message);
									}
									
									String place = null;
									String placeKa = null;
									if (stage % 2 == 0){
										place = "Committee";
										placeKa = "კომიტეტი";
									}else{
										place = "Plenary";
										placeKa = "პლენარული";
									}
									
									discussion.setPlace(place);
									discussion.setPlace_en(place);
									discussion.setPlace_ka(placeKa);
									discussion.setPassed("N");
									discussion.setStage(stage);
									
									discussions.add(discussion);
									
									if (stage > highestStage && discussion != null){
										highestStage = stage;
										Date discussionDate = discussion.getDiscussionDate();
										if (discussionDate != null){
											highestStageDate = fieldDateFormat.format(discussionDate);
										}
									}
									
								}
								stage++;
							
							}
							
							String enStatusStage = null;
							String kaStatusStage = null;
									
							switch(highestStage){
							case 0:
								enStatusStage = "1st Committee";
								kaStatusStage = "I მოსმენა კომიტეტი";
								break;
							case 1:
								enStatusStage = "1st Plenary";
								kaStatusStage = "I მოსმენა პლენარული";
								break;
							case 2:
								enStatusStage = "2nd Committee";
								kaStatusStage = "II მოსმენა კომიტეტი";
								break;
							case 3:
								enStatusStage = "2nd Plenary";
								kaStatusStage = "II მოსმენა პლენარული";
								break;
							case 4:
								enStatusStage = "3rd Committee";
								kaStatusStage = "III მოსმენა კომიტეტი";
								break;
							case 5:
								enStatusStage = "3rd Plenary";
								kaStatusStage = "III მოსმენა პლენარული";
								break;
							}
							
							
							String englishStatus = enStatusStage + " " + highestStageDate;
							String georgianStatus = kaStatusStage + " " + highestStageDate;
							
							if (highestStage == 5){
								englishStatus += ": PASS";
								georgianStatus += ": მიღებულია";
								
								draftlaw.setShortStatus("P");
							}else{
								// Short status for didn't pass (yet)
								draftlaw.setShortStatus("D");
							}
							draftlaw.setStatus(englishStatus);
							draftlaw.setStatusEn(englishStatus);
							draftlaw.setStatusKa(georgianStatus);
							
							draftlaw.setDiscussions(discussions);
						}
						countrow++;
					}
				}
			}
			
		} catch (IOException | ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		result.setDraftlaws(parsedDraftlaws);
		result.setErrorMessages(draftlawErrorMessages);
		
		return result;
		
	}
	
	public static void updateMyParliamentDatabase(ArrayList<Draftlaw> draftLaws){
		
	}

	private static SpreadsheetEntry getGoogleSpreadsheetInfo(SpreadsheetService service, GoogleInformation googleInformation){

		SpreadsheetEntry spreadsheetResult = null;

		/** Our view of Google Spreadsheets as an authenticated Google user. */
		service.setProtocolVersion(SpreadsheetService.Versions.V3);

		// Login and prompt the user to pick a sheet to use.
		try {
			service.setUserCredentials(googleInformation.getUsername(), googleInformation.getPassword());

			URL SPREADSHEET_FEED_URL = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");

			// Make a request to the API and get all spreadsheets.
			SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL, SpreadsheetFeed.class);
			List<SpreadsheetEntry> spreadsheets = feed.getEntries();

			// Iterate through all of the spreadsheets returned
			int spreadsheetIndex = 0;
			int count = 0;
			for (SpreadsheetEntry spreadsheet : spreadsheets) {
				// Print the title of this spreadsheet to the screen
				String doctitle = spreadsheet.getTitle().getPlainText();
				if (doctitle != null && doctitle.equalsIgnoreCase(googleInformation.getSpreadsheetTitle())){
					spreadsheetIndex = count;
				}
				count++;
			}

			if (spreadsheets != null){
				spreadsheetResult = spreadsheets.get(spreadsheetIndex);
			}

		} catch (AuthenticationException e) {
			System.out.println("An error occured while authenticating to Google");
			e.printStackTrace();
		} catch (MalformedURLException e) {
			System.out.println("Error: given URL is malformed");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error: Google file not found");
			e.printStackTrace();
		} catch (ServiceException e) {
			System.out.println("An error occured while instantiating SpreadsheedFeed.");
			e.printStackTrace();
		}

		return spreadsheetResult;

	}
	

	/**
	 * Parsing spreadsheet cell to find date.
	 * @param hearing
	 * @return date String
	 */
	private static String getDateFromHearingDescription(String hearing){
		String date = null;
		if (hearing != null){
			if (hearing.contains(".201")){
				// the description contains a date to extract.
				String[] dateSplit = hearing.split("\\(");
				if (dateSplit != null && dateSplit.length >= 2){
					String[] dateSplit2 = dateSplit[dateSplit.length-1].split("\\)");
					if (dateSplit2 != null && dateSplit2.length >= 1){
						date = dateSplit2[0];
					}
				}

				if (date == null || (date != null && !date.contains(".201"))){
					// For some cell, dates are not surrounded by parenthesis. They are just at the end of the description.
					dateSplit = hearing.split(" ");
					if (dateSplit != null && dateSplit.length >= 1){
						date = dateSplit[dateSplit.length-1];
					}

				}
			}
		}
		return date; 
	}

}
