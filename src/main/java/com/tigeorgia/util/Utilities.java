package com.tigeorgia.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.tigeorgia.model.CsvFile;
import com.tigeorgia.model.Person;

public class Utilities {
	
	public static final String PARLIAMENT_CONTACT_TYPE = "parliament";
	public static final String ELECTION_CONTACT_TYPE = "election";
	
	public static final String GEORGIAN_LANGUAGE = "ka";
	public static final String ENGLISH_LANGUAGE = "en";
	
	public static CsvFile checkUploadedListOfRecipients(MultipartFile file, Logger logger){
		return processListOfRecipients(logger, null, file);
	}
	
	public static CsvFile getListOfRecipients(Logger logger, String contactType) {
		String path = null;
		if (contactType.equalsIgnoreCase(PARLIAMENT_CONTACT_TYPE)){
			path = "/tmp/ParliamentPhoneNumberList.csv";
		}else if (contactType.equalsIgnoreCase(ELECTION_CONTACT_TYPE)){
			path = "/tmp/ElectionPhoneNumberList.csv";
		}
		
		return processListOfRecipients(logger, path, null);
	}

	private static CsvFile processListOfRecipients(Logger logger, String path, MultipartFile file) {

		InputStream inputStream = null;
		List<Person> recipients = null;
		BufferedReader br = null;
		String errorMessage = null;
		try {
			if (path != null){
				inputStream = new FileInputStream(path);
			}else{
				inputStream = file.getInputStream();
			}
			
			if (inputStream != null){
				br = new BufferedReader(new InputStreamReader(inputStream));
				recipients = new LinkedList<Person>();
				int lineNumber = 1;
				while (br.ready()) {
					String line = br.readLine();
					if (line != null && !line.isEmpty()){
						String[] splitLine = line.split(",");
						if (splitLine.length >= 3){
							// The CSV file might have either 3 or 4 columns:
							// - 3 columns: Name, Number, Groups
							// - 4 columns: Name, Language, Number, Groups
							// If it is a 3-column document, default language will be 'Georgian', if it is not, we'll have either 'en' or 'ka'
							// on the second column.
							
							boolean is4ColumnDocument = (splitLine[1].equalsIgnoreCase(ENGLISH_LANGUAGE) || splitLine[1].equalsIgnoreCase(GEORGIAN_LANGUAGE));
							
							String language = null;
							String phoneNumber = null;
							int firstGroupIndex = 0;
							if (is4ColumnDocument){
								if (splitLine[1] == null || (splitLine[1] != null && splitLine[1].isEmpty())){
									language = "ka";
								}else{
									language = splitLine[1];
								}
								phoneNumber = splitLine[2];
								firstGroupIndex = 3;
							}else{
								language = "ka";
								phoneNumber = splitLine[1];
								firstGroupIndex = 2;
							}
							
							
							Person person = new Person();
							person.setName(splitLine[0]);
							person.setLanguage(language);

							String formattedNumbers = phoneNumber.trim().replaceAll("-", "")
									.replaceAll(" ","")
									.replaceAll("/", "\\|");
							
							String[] splitNumbers = formattedNumbers.split("\\|");
							ArrayList<String> numbers = new ArrayList<String>();
							for (String number : splitNumbers){
								if(number.startsWith("5") || number.startsWith("995")){
									numbers.add(number);
								}
							}
							person.setNumbers(numbers);
							
							// Groups
							String groupLine = "";
							for (int i=firstGroupIndex;i<splitLine.length;i++){
								groupLine += splitLine[i]+",";
							}
							groupLine = groupLine.substring(0, groupLine.length()-1);
							String[] splitGroups = groupLine.split("\\|");
							
							ArrayList<String> groups = null;
							if (splitGroups != null && splitGroups.length > 0){
								groups = new ArrayList<String>();
								for (String group : splitGroups){
									groups.add(group.replaceAll("\"", ""));
								}
								person.setGroups(groups);
							}else{
								person.setGroups(null);
							}
							
							recipients.add(person);
						}else{
							// the CSV file is not well formatted, we need to raise an error
							errorMessage = "The line #" + lineNumber + " is not well formatted (less than 3 fields). ";
							if (file != null){
								errorMessage += "Please review your CSV file, and try to upload it again.";
								logger.error(errorMessage);
							}
							recipients = null;
							break;
						}
						lineNumber++;
					}			    
				}	
			}
		} catch (FileNotFoundException e) {
			logger.error("Could not find /tmp/PhoneNumberList.csv (file not available)",e);
			errorMessage = "Contact list not available: no CSV file was found";
		} catch (IOException e) {
			errorMessage = "The list of recipients (CSV file) could not be read";
			logger.error(errorMessage, e);
		} finally{
			// releases system resources associated with this stream
			if(inputStream != null)
				try {
					inputStream.close();
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		
		CsvFile result = new CsvFile(recipients, errorMessage);
		
		return result;
	}
	
	public static int totalNumberOfGroups(List<Person> recipientsList){
		List<String> groups = null;
		int result = 0;
		if (recipientsList != null){
			groups = new ArrayList<String>();
			for (Person recipient : recipientsList){
				ArrayList<String> personGroups = recipient.getGroups();
				for (String group : personGroups){
					group = StringUtils.capitalize(group.toLowerCase());
					if (!groups.contains(group)){
						groups.add(group);
					}
				}
			}
		}
	
		if(groups != null){
			result = groups.size();
		}
		
		return result;
			
	}

}
