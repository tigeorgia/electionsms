package com.tigeorgia.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import com.tigeorgia.controller.DraftLawController;
import com.tigeorgia.model.Draftlaw;
import com.tigeorgia.model.DraftlawContainer;
import com.tigeorgia.model.DraftlawValidationMessage;

public class GoogleSpreadsheetParser {
	
	public static final String GOOGLE_ACCOUNT_USERNAME = "****";
	public static final String GOOGLE_ACCOUNT_PASSWORD = "****";

	public static final String GOOGLE_SPREADSHEET_TITLE = "კანონპროექტების ბაზა | Draft law database";

	public static final String SPREADSHEET_URL = "https://docs.google.com/spreadsheet/ccc?key=0AtLaNvIaw4_rdEZkRWlDX2RhTlF6NEx5SU10Vll2R3c&usp=sharing#gid=0";
	
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

	public static final LinkedList<String> statusLabelEn = new LinkedList<String>(Arrays.asList("1st Committee","1st Plenary","2nd Committee","2nd Plenary","3rd Committee","3rd Plenary"));
	public static final LinkedList<String> statusLabelKa = new LinkedList<String>(Arrays.asList("პირველი კომიტეტი","პირველი პლენარული","მეორე კომიტეტი","მეორე პლენარული","მესამე კომიტეტი","მესამე პლენარული"));
	public static final LinkedList<String> hearingLabelsKa = new LinkedList<String>(Arrays.asList("firstCommitteeHearing","firstPlenaryHearing","secondCommitteeHearing","secondPlenaryHearing","thirdCommitteeHearing","thirdPlenaryHearing"));
	public static final LinkedList<String> hearingLabelsEn = new LinkedList<String>(Arrays.asList("firstCommitteeHearingEn","firstPlenaryHearingEn","secondCommitteeHearingEn","secondPlenaryHearingEn","thirdCommitteeHearingEn","thirdPlenaryHearingEn"));
	
	public static DraftlawContainer validateSpreadsheet(Map<String, String> draftlawHeadings){
		
		SpreadsheetService service = new SpreadsheetService("tig-draftlaw-spreadsheet");

		SpreadsheetEntry spreadsheet = getGoogleSpreadsheet(service);
		
		List<WorksheetEntry> worksheets;
		
		DraftlawContainer result = new DraftlawContainer();
		
		// List to store read draft laws
		ArrayList<Draftlaw> parsedDraftlaws = new ArrayList<Draftlaw>();
		
		// List to store error message
		ArrayList<DraftlawValidationMessage> draftlawErrorMessages = new ArrayList<DraftlawValidationMessage>(); 
		
		// Bureau date format, for validation
		SimpleDateFormat bureauDateFormat = new SimpleDateFormat("MM.dd.yyyy");
		
		
		try {
			worksheets = spreadsheet.getWorksheets();
			
			// Store the spreadsheet in a list of Draftlaw instances.
			for (WorksheetEntry worksheet : worksheets) {
				// Get the worksheet's title, row count, and column count.
				String title = worksheet.getTitle().getPlainText();

				System.out.println(title);

				// We are currently only taking care of the 3rd spreadsheet (FALL 2013)
				String lastSpreadsheetTitle = "SPRING 2014";
				if (title != null && title.contains(lastSpreadsheetTitle)){

					// Print the fetched information to the screen for this worksheet.
					URL listFeedUrl = worksheet.getListFeedUrl();

					ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);

					// Iterate through each row, printing its cell values.
					int countrow=0;
					
					for (ListEntry row : listFeed.getEntries()) {
						// Print the first column's cell value
						// Iterate over the remaining columns, and print each cell value
						if (countrow > 0){
							Map<String,String> rowData = new HashMap<String,String>();

							int countForColumn = 0;
							Draftlaw draftlaw = new Draftlaw();
							
							for (String tag : row.getCustomElements().getTags()) {
								String fieldName = colNames.get(countForColumn);
								String value = row.getCustomElements().getValue(tag);
								
								BeanUtils.setProperty(draftlaw,fieldName,value);
								
								countForColumn++;
							}
							
							parsedDraftlaws.add(draftlaw);
							
							// The real row number matches the row number, in the spreadsheet.
							int realRowNumber = countrow + 2;
							
							// For this Draft law, we check if all the mandatory fields have been filled.
							// If a mandatory field ha not been filled, we log it in a list, so we can display 
							// an appropriate error message later.
							for (String mandatoryField : listOfMandatoryFields){
								try {
									String fieldValue = BeanUtils.getProperty(draftlaw, mandatoryField);
									if ((fieldValue == null) || (fieldValue != null && fieldValue.isEmpty())){
										DraftlawValidationMessage message = new DraftlawValidationMessage();
										// message.setDraftLaw(draftlaw);
										message.setMessage("The mandatory field '" + draftlawHeadings.get(mandatoryField) + 
												"' is missing, for the draft law on row " + Integer.toString(realRowNumber) + " (page '" + lastSpreadsheetTitle +"')");
										draftlawErrorMessages.add(message);
									}
								} catch (NoSuchMethodException e) {
									// There was a problem while reading a field. We log it.
									DraftlawValidationMessage message = new DraftlawValidationMessage();
									//message.setDraftLaw(draftlaw);
									message.setMessage("There was a problem while reading the field '" + draftlawHeadings.get(mandatoryField) + 
											"' for the draft law on row " + Integer.toString(realRowNumber) + " (page '" + lastSpreadsheetTitle +"')");
									draftlawErrorMessages.add(message);
								}
							}
							
							// For this draft law the 'bureau date' field should have the following format: MM.DD.YYYY
							String bureauDateStr = draftlaw.getBureauDate();
							try {
								if (bureauDateStr != null && !bureauDateStr.isEmpty()){
									bureauDateFormat.parse(bureauDateStr);
								}
							} catch (ParseException e) {
								DraftlawValidationMessage message = new DraftlawValidationMessage();
								message.setMessage("The bureau date '"+bureauDateStr+"' does not match the mandatory format 'MM.DD.YYYY', for the draft law on row "
											+ Integer.toString(realRowNumber) + " (page '" + lastSpreadsheetTitle +"')");
								draftlawErrorMessages.add(message);
							}
							
							// Registration number should match following format: “#xx-x/xxx”, where x is a number.
							String billNumber = draftlaw.getBillNumber();
							if (!billNumber.matches(REGULAR_EXPRESSION_FOR_BILL_NUMBER)){
								DraftlawValidationMessage message = new DraftlawValidationMessage();
								message.setMessage("The registration number '"+billNumber+"' does not match the '#xx-x/xxx' format, where x must be a digit - for the draft law on row "
											+ Integer.toString(realRowNumber) + " (page '" + lastSpreadsheetTitle +"')");
								draftlawErrorMessages.add(message);
							}
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

	private static SpreadsheetEntry getGoogleSpreadsheet(SpreadsheetService service){

		SpreadsheetEntry spreadsheetResult = null;

		/** Our view of Google Spreadsheets as an authenticated Google user. */
		service.setProtocolVersion(SpreadsheetService.Versions.V3);

		// Login and prompt the user to pick a sheet to use.
		try {
			service.setUserCredentials(GOOGLE_ACCOUNT_USERNAME, GOOGLE_ACCOUNT_PASSWORD);

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
				if (doctitle != null && doctitle.equalsIgnoreCase(GOOGLE_SPREADSHEET_TITLE)){
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

	private static void processSpreadsheet(SpreadsheetEntry spreadsheet, SpreadsheetService service, String outputPath, Date lastScrapingDate) throws ParseException{
		// Make a request to the API to fetch information about all
		// worksheets in the spreadsheet.
		List<WorksheetEntry> worksheets;
		try {
			worksheets = spreadsheet.getWorksheets();

			// Iterate through each worksheet in the spreadsheet.
			for (WorksheetEntry worksheet : worksheets) {
				// Get the worksheet's title, row count, and column count.
				String title = worksheet.getTitle().getPlainText();

				System.out.println(title);

				// We are currently only taking care of the 3rd spreadsheet (FALL 2013)
				String lastSpreadsheetTitle = "SPRING 2014";
				if (title != null && title.contains(lastSpreadsheetTitle)){

					// Print the fetched information to the screen for this worksheet.
					URL listFeedUrl = worksheet.getListFeedUrl();

					ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);

					// Iterate through each row, printing its cell values.
					int countrow=0;

					for (ListEntry row : listFeed.getEntries()) {
						// Print the first column's cell value
						// Iterate over the remaining columns, and print each cell value
						if (countrow > 0){
							Map<String,String> rowData = new HashMap<String,String>();

							int countForColumn = 0;
							for (String tag : row.getCustomElements().getTags()) {
								rowData.put(colNames.get(countForColumn), row.getCustomElements().getValue(tag));
								countForColumn++;
							}

							// We need the data after the most recent published draft law (13.06.2013)
							String regDate = rowData.get("registrationDate");
							SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
							boolean shouldBeAdded = false;
							try {
								if (regDate != null){
									Date lawDate = sdf.parse(regDate);
									shouldBeAdded = lastScrapingDate.before(lawDate);
								}
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							String registrationNumber = rowData.get("registrationNumber");

							// Formatting data here, for the SQL query.
							String registrationNumberForQuery = parseDataForQuery(rowData.get("registrationNumber"),true);
							String draftLawTitle = parseDataForQuery(rowData.get("draftLawTitle"),true);
							String draftLawTitleEn = parseDataForQuery(rowData.get("draftLawTitleEn"),true);
							String initiator = parseDataForQuery(rowData.get("initiator"),true);
							String initiatorEn = parseDataForQuery(rowData.get("initiatorEn"),true);
							String author = parseDataForQuery(rowData.get("author"),true);
							String authorEn = parseDataForQuery(rowData.get("authorEn"),true);
							String summary = parseDataForQuery(rowData.get("description"),false);
							String summaryEn = parseDataForQuery(rowData.get("descriptionEn"),false);
							
							String registrationDate = null;
							if (rowData.get("registrationDate") != null){
								registrationDate = "TO_DATE('"+rowData.get("registrationDate")+"','MM.DD.YYYY')";
							}else{
								registrationDate = "TO_DATE('01.01.1970','MM.DD.YYYY')";
							}
							
							// Definition of the draft law's current status
							String statusEnglish = defineCurrentStatus(rowData,"En");
							String statusGeorgian = defineCurrentStatus(rowData,"Ka");


							if (isPrimaryDraft(rowData)){
								if (shouldBeAdded){
									// We're dealing with the parent law, and its hearing related information

									registrationNumberForQuery = registrationNumberForQuery.trim().replaceAll("–","-").replaceAll("\\(N'", "").replaceAll("\\(N", "").replaceAll("\\(", "").replaceAll("\"", "")
											.replaceAll(" ", "").replace("'0", "'#0");
									
									String[] slugSplit = registrationNumberForQuery.split(":");
									if (slugSplit != null && slugSplit.length > 1){
										registrationNumberForQuery = slugSplit[0]+"'";
									}
									
									slugSplit = registrationNumberForQuery.split(";");
									if (slugSplit != null && slugSplit.length > 1){
										registrationNumberForQuery = slugSplit[0]+"'";
									}
									
									slugSplit = registrationNumberForQuery.split(",");
									if (slugSplit != null && slugSplit.length > 1){
										registrationNumberForQuery = slugSplit[0]+"'";
									}
									
									slugSplit = registrationNumberForQuery.split("\\.");
									if (slugSplit != null && slugSplit.length > 1){
										registrationNumberForQuery = slugSplit[0]+"'";
									}
									
									String slug = registrationNumberForQuery.replaceAll("#", "").replaceAll("/", "");
									

									if (registrationNumberForQuery.trim().contains("110")){
										System.out.println();
									}

									// Insert main information into draftlaw_draftlaw table
									String insertQuery = "INSERT INTO draftlaw_draftlaw (bureau_date,bill_number,title,title_en,"
											+ "title_ka,initiator,initiator_en,initiator_ka,author,author_en,author_ka,status,status_en,status_ka,"
											+ "summary,summary_en,summary_ka,full_text,full_text_url,enacted_text_url,related_1,related_2,related_3," 
											+ "related_4,related_5,slug) "
											+ "VALUES ("+registrationDate+","+ registrationNumberForQuery.trim() +","+draftLawTitle+","+draftLawTitleEn+","
											+draftLawTitle+","+initiator+","+initiatorEn+","+initiator+","+author+","+authorEn+","+author
											+","+statusEnglish+","+statusEnglish+","+statusGeorgian+","+summary+","+summaryEn+","+summary+","
											+ "'','','','','','','','',"+slug+");\n";

									

									//bw.write(insertQuery);


								}else{
									// The parent law has already been added. We're doing updates here then. 
									
									String updateQuery = "UPDATE draftlaw_draftlaw SET "
											+ "title_ka = " + draftLawTitle + ", "
											+ "title_en = " + draftLawTitleEn + ", "
											+ "initiator_ka = " + initiator + ", "
											+ "initiator_en = " + initiatorEn + ", "
											+ "author_ka = " + author + ", "
											+ "author_en = " + authorEn + ", "
											+ "status_ka = " + statusGeorgian + ", "
											+ "status_en = " + statusEnglish + ", "
											+ "summary_ka = " + summary + ", "
											+ "summary_en = " + summaryEn + " "
											+ "WHERE bureau_date = " + registrationDate + " AND bill_number = " + registrationNumberForQuery.trim() + " AND "
											+ "title = " + draftLawTitle + ";\n";
									
									
									//bw.write(updateQuery);
									
								}
								
								// Completing the insert queries with hearing info.
								//writeLawHearingQueries(bw, lastScrapingDate, rowData, registrationNumberForQuery);

								//bw.write("\n");
								
								
							}else{
								// We're dealing with a child law
								registrationNumberForQuery = registrationNumberForQuery.trim();

								if (!registrationNumberForQuery.isEmpty()){

									String childLawTitleEn = parseDataForQuery(rowData.get("draftLawTitleEn"),true);
									String childLawTitle = parseDataForQuery(rowData.get("draftLawTitle"),true);
									String lawNumber = parseDataForQuery(rowData.get("lawNumber"),true);

									String parentIdClause = "(SELECT id FROM draftlaw_draftlaw WHERE bill_number=" + registrationNumberForQuery +")";

									String insertQuery = "INSERT INTO draftlaw_draftlawchild (parent_id,bill_number,title,law_number,title_en,title_ka,place) VALUES ("
											+ parentIdClause + ","+registrationNumberForQuery+","+childLawTitle+","+lawNumber+","+childLawTitleEn+","+childLawTitle+",'');";

									//childLawBw.write(insertQuery + "\n");
								}
							}


						}
						countrow++;
					}
				}
			}
			//bw.close();
			//childLawBw.close();

			// Merging files now: insertDraftLaws.sql first, then insertChildLaw.sql because of the dependencies.
			File[] files = new File[2];
			//files[0] = file;
			//files[1] = childLawFile;

			File outputFile = new File(outputPath+"/updateDraftLaws.sql");
			System.out.println("File is at: " + outputPath+"/updateDraftLaws.sql");

			if (outputFile.exists()) {
				outputFile.delete();
			}
			outputFile.createNewFile();

			mergeFiles(files, outputFile);

		} catch (IOException e) {
			System.out.println("Problem occured while creating SQL files");
			e.printStackTrace();
		} catch (ServiceException e) {
			System.out.println("An error occured while instantiating SpreadsheedFeed.");
			e.printStackTrace();
		}

		System.out.println("Done.");

	}

		
	private static void writeLawHearingQueries(BufferedWriter bw, Date lastScrapingDate, Map<String,String> rowData, String registrationNumber) throws IOException, ParseException{
		
		writeHearingQuery(lastScrapingDate, rowData.get("firstCommitteeHearing"), registrationNumber, bw, 0);
		writeHearingQuery(lastScrapingDate, rowData.get("firstPlenaryHearing"), registrationNumber, bw, 1);
		writeHearingQuery(lastScrapingDate, rowData.get("secondCommitteeHearing"), registrationNumber, bw, 2);
		writeHearingQuery(lastScrapingDate, rowData.get("secondPlenaryHearing"), registrationNumber, bw, 3);
		writeHearingQuery(lastScrapingDate, rowData.get("thirdCommitteeHearing"), registrationNumber, bw, 4);
		writeHearingQuery(lastScrapingDate, rowData.get("thirdPlenaryHearing"), registrationNumber, bw, 5);
		
	}

	/**
	 * Defines the current status of a law
	 * @param rowData
	 * @param statusLabel 
	 * @return status String
	 */
	private static String defineCurrentStatus(Map<String, String> rowData, String lang) {
		LinkedList<String> hearingLabelsToUse = null;
		LinkedList<String> statusLabelToUse = null;
		if (lang.equalsIgnoreCase("En")){
			hearingLabelsToUse = hearingLabelsEn;
			statusLabelToUse = statusLabelEn;
		}else if (lang.equalsIgnoreCase("Ka")){
			hearingLabelsToUse = hearingLabelsKa;
			statusLabelToUse = statusLabelKa;
		}
		String status = "''";
		int countHearing = 0;
		for (String label : hearingLabelsToUse){
			String hearing = rowData.get(label);
			if (hearing != null && !hearing.trim().isEmpty()){
				status = "'"+statusLabelToUse.get(countHearing).replaceAll("'", "''") + " - " + hearing.replaceAll("'", "''")+"'";
				
			}
			countHearing++;
		}
		return status;
	}

	/**
	 * Defines whether a row gives information about parent draft (as opposed to child draft)
	 * @param rowData
	 * @return
	 */
	private static boolean isPrimaryDraft(Map<String, String> rowData){
		String value = rowData.get("primaryDraftLaw");
		return (value != null && value.trim().equalsIgnoreCase("Primary"));
	}

	/**
	 * Completes SQL queries, when inserting data in draftlaw_draftlawdiscussion table
	 * @param lastScrapingDate 
	 * @param insertHearingQuery
	 * @param hearingDescription
	 * @param registrationNumber
	 * @param bw
	 * @param stage
	 * @throws IOException
	 * @throws ParseException 
	 */
	private static void writeHearingQuery(Date lastScrapingDate, String hearingDescription, 
			String registrationNumber, BufferedWriter bw, int stage) throws IOException, ParseException{
		
		
		String insertQuery = "INSERT INTO draftlaw_draftlawdiscussion (draftlaw_id,date,stage,place,place_en,place_ka,passed) VALUES ";
		String deleteQuery = "DELETE FROM draftlaw_draftlawdiscussion ";
		
		if (hearingDescription != null && !hearingDescription.isEmpty()){

			String hearingValues = null;
			String hearingDateStr = getDateFromHearingDescription(hearingDescription);
			String hearingDateForQuery = null;
			SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.yyyy");
			Boolean isHearingInformationAleardyAdded = false;
			if (hearingDateStr != null){
				hearingDateForQuery = "TO_DATE('"+hearingDateStr+"','MM.DD.YYYY')";
				Date hearingDate = sdf.parse(hearingDateStr);
				isHearingInformationAleardyAdded = lastScrapingDate.after(hearingDate);
				
			}else{
				// There is no way to track if a law discussion that does not have any date has been already added. So we delete the releant record,
				// before adding it again.
				hearingDateForQuery = "TO_DATE('01.01.1970','MM.DD.YYYY')";
				String deleteWhere = "WHERE draftlaw_id = (SELECT id FROM draftlaw_draftlaw WHERE bill_number = " + registrationNumber.trim() + ") AND stage = " + Integer.toString(stage) + ";";
				bw.write(deleteQuery + deleteWhere + "\n");
			}
			
			if (!isHearingInformationAleardyAdded){
				String place = null;
				String placeKa = null;
				if (stage % 2 == 0){
					place = "Committee";
					placeKa = "კომიტეტი";
				}else{
					place = "Plenary";
					placeKa = "პლენარული";
				}
				if (registrationNumber != null){
					hearingValues = ("((SELECT id FROM draftlaw_draftlaw WHERE bill_number = " + registrationNumber.trim() +"),"+hearingDateForQuery+","+Integer.toString(stage)+
							",'"+place+"','"+place+"','"+placeKa+"','N');");
	
				}
	
				if (hearingValues != null){
					bw.write(insertQuery + hearingValues+"\n");
				}
			}
		}
	}

	/**
	 * Formatting data to get it ready to be injected in SQL query clauses.
	 * @param data
	 * @param isLimitedInSpace
	 * @return
	 */
	private static String parseDataForQuery(String data, boolean isLimitedInSpace){
		String result = "";
		if (data != null){
			// Some fields are limited to 255 characters
			if (isLimitedInSpace && data.length() >= 255){
				data = data.substring(0, 250) + "...";
			}
			data = data.replaceAll("'", "\"");
			result = "'"+data.replaceAll("'", "''")+"'";
		}else{
			result = "''";
		}
		return result;
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

	/**
	 * Merges 2 SQL files into a single new one.
	 * @param files
	 * @param mergedFile
	 */
	public static void mergeFiles(File[] files, File mergedFile) {

		FileWriter fstream = null;
		BufferedWriter out = null;
		try {
			fstream = new FileWriter(mergedFile, true);
			out = new BufferedWriter(fstream);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		for (File f : files) {
			System.out.println("merging: " + f.getName());
			FileInputStream fis;
			try {
				fis = new FileInputStream(f);
				BufferedReader in = new BufferedReader(new InputStreamReader(fis));

				String aLine;
				while ((aLine = in.readLine()) != null) {
					out.write(aLine);
					out.newLine();
				}

				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
