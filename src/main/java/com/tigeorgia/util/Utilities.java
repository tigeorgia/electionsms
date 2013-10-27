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

import com.tigeorgia.model.Person;

public class Utilities {

	public static List<Person> getListOfRecipients(Logger logger) {

		InputStream inputStream = null;
		List<Person> recipients = null;
		BufferedReader br = null;
		try {
			inputStream = new FileInputStream("/tmp/PhoneNumberList.csv");
			if (inputStream != null){
				br = new BufferedReader(new InputStreamReader(inputStream));
				recipients = new LinkedList<Person>();

				while (br.ready()) {
					String line = br.readLine();
					if (line != null && !line.isEmpty()){
						String[] splitLine = line.split(",");
						if (splitLine.length >= 3){
							Person person = new Person();
							person.setName(splitLine[0]);

							String formattedNumbers = splitLine[1].trim().replaceAll("-", "")
									.replaceAll(" ","")
									.replaceAll("/", "\\|");
							
							String[] splitNumbers = formattedNumbers.split("\\|");
							ArrayList<String> numbers = new ArrayList<String>();
							for (String number : splitNumbers){
								if(number.startsWith("5")){
									numbers.add(number);
								}
							}
							person.setNumbers(numbers);

							person.setGroup(StringUtils.capitalize(splitLine[2].toLowerCase()));

							recipients.add(person);
						}
					}			    
				}	
			}
		} catch (FileNotFoundException e) {
			logger.error("Could not find /tmp/PhoneNumberList.csv (file not available)",e);
		} catch (IOException e) {
			String errorMessage = "The list of recipients (CSV file) could not be read.";
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
		
		return recipients;
	}

}
