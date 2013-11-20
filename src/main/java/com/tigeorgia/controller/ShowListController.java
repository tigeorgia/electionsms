package com.tigeorgia.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.tigeorgia.model.CsvFile;
import com.tigeorgia.model.Person;
import com.tigeorgia.model.UploadedFile;
import com.tigeorgia.util.Constants;
import com.tigeorgia.util.Utilities;
import com.tigeorgia.validator.UploadedFileValidator;

@Controller
public class ShowListController {

	private static final Logger logger = Logger.getLogger(ShowListController.class);

	@Autowired  
	UploadedFileValidator fileValidator;  

	/**
	 * Shows list of recipients, after reading their details from a CSV file.
	 * @param model
	 * @return view
	 */
	@RequestMapping(value="/showlist", method = RequestMethod.GET)
	public String showlist(ModelMap model) {

		CsvFile file = Utilities.getListOfRecipients(logger);
		List<Person> recipients = null;
		if (file != null){
			recipients = file.getRecipients();
		}

		if (file.getErrorMessage() != null){
			model.addAttribute("errorMessage", file.getErrorMessage());
		}else if (recipients == null){
			model.addAttribute("errorMessage", "There was a problem while trying to retrieve the recipient list.");
		}
		model.addAttribute("recipients", recipients);
		model.addAttribute("uploadedFile", new UploadedFile());

		return Constants.RECIPIENT_LIST_VIEW;

	}

	@RequestMapping(value="/uploadlist", method = RequestMethod.POST)
	public String uploadList(ModelMap model,   
			@ModelAttribute("uploadedFile") UploadedFile uploadedFile,  
			BindingResult result) {

		InputStream inputStream = null;  
		OutputStream outputStream = null;  

		MultipartFile file = uploadedFile.getFile();  
		fileValidator.validate(uploadedFile, result);  

		String fileName = file.getOriginalFilename();  

		if (result.hasErrors()){
			model.addAttribute("isUploadedSuccessfully", false);
		}else{
			try {  
				inputStream = file.getInputStream();  

				File newFile = new File("/tmp/PhoneNumberList.csv");  
				if (!newFile.exists()) {  
					newFile.createNewFile();
				}else{
					File backupFile = new File("/tmp/" + fileName+".old");  
					if (backupFile.exists()) {
						backupFile.delete();
					}
					newFile.renameTo(new File("/tmp/" + fileName+".old"));
					newFile = new File("/tmp/PhoneNumberList.csv");
				}

				outputStream = new FileOutputStream(newFile);  
				int read = 0;  
				byte[] bytes = new byte[1024];  

				while ((read = inputStream.read(bytes)) != -1) {  
					outputStream.write(bytes, 0, read);  
				}  
			} catch (IOException e) {  
				e.printStackTrace();  
			} finally {
				try {
					outputStream.close();
				} catch (IOException e) {
					logger.error("Problem closing OutpuStream.");
				}
			}
			model.addAttribute("isUploadedSuccessfully", true);
			model.addAttribute("uploadedFile", new UploadedFile());

			// We remove the backup
			File backupFile = new File("/tmp/" + fileName+".old");  
			if (backupFile.exists()) {
				backupFile.delete();
			}
		}

		// Loading the current CSV file (whether or not the upload succeeded)
		CsvFile csvfile = Utilities.getListOfRecipients(logger);
		List<Person> recipients = null;
		if (csvfile != null){
			recipients = csvfile.getRecipients();
		}

		if (recipients == null){
			model.addAttribute("errorMessage", "There was a problem while trying to retrieve the recipient list, after upload was complete");
		}

		model.addAttribute("recipients", recipients);

		return Constants.RECIPIENT_LIST_VIEW;

	}

}
