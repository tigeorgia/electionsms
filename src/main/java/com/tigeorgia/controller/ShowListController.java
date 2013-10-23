package com.tigeorgia.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tigeorgia.model.Person;

@Controller
@RequestMapping("/showlist")
public class ShowListController {

	private static final Logger logger = Logger.getLogger(ShowListController.class);

	@RequestMapping(method = RequestMethod.GET)
	public String showlist(ModelMap model) {

		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("PhoneNumberList.csv");
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		List<Person> recipients = new LinkedList<Person>();
		try {
			while (br.ready()) {
				String line = br.readLine();
				if (line != null && !line.isEmpty()){
					String[] splitLine = line.split(",");
					if (splitLine.length >= 2){
						Person person = new Person();
						person.setName(splitLine[0]);
						String formattedNumbers = splitLine[1].trim().replaceAll("-", "").replaceAll(" ","");
						String[] splitNumbers = formattedNumbers.split("\\|");
						ArrayList<String> numbers = new ArrayList<String>();
						for (String number : splitNumbers){
							numbers.add(number);
						}
						person.setNumbers(numbers);

						recipients.add(person);
					}
				}			    
			}
		} catch (IOException e) {
			String errorMessage = "ERROR: line in log file could not be read.";
			logger.error(errorMessage, e);
			model.addAttribute("errorMsg", errorMessage);
		}finally{
			// releases system resources associated with this stream
			if(inputStream != null)
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

		logger.warn("list to be displayed now");

		model.addAttribute("recipients", recipients);

		return "recipientlist";

	}

}
