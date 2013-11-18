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
	
	public static CsvFile checkUploadedListOfRecipients(MultipartFile file, Logger logger){
		return processListOfRecipients(logger, null, file);
	}
	
	public static CsvFile getListOfRecipients(Logger logger) {
		String path = "/tmp/PhoneNumberList.csv";
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
						if (splitLine.length >= 4){
							Person person = new Person();
							person.setName(splitLine[0]);
							person.setLanguage(splitLine[1]);

							String formattedNumbers = splitLine[2].trim().replaceAll("-", "")
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
							
							String[] splitGroups = splitLine[3].split("\\|");
							person.setGroup(splitGroups[0].replaceAll("\"", ""));

							recipients.add(person);
						}else{
							// the CSV file is not well formatted, we need to raise an error
							errorMessage = "The line #" + lineNumber + " is not well formatted (less than 4 fields). ";
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
		} catch (IOException e) {
			errorMessage = "The list of recipients (CSV file) could not be read.";
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
				String group = StringUtils.capitalize(recipient.getGroup().toLowerCase());
				if (!groups.contains(group)){
					groups.add(group);
				}
			}
		}
	
		if(groups != null){
			result = groups.size();
		}
		
		return result;
			
	}

}
