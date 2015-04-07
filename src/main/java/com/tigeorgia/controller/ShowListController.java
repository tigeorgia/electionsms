package com.tigeorgia.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

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
	public ModelAndView showlist(String message) {
		
		ModelAndView mav = new ModelAndView(Constants.RECIPIENT_LIST_VIEW);

		mav.addObject("electionRecipients", getRecipientListFromOneFile(Utilities.ELECTION_CONTACT_TYPE, mav.getModelMap()));
		mav.addObject("parliamentRecipients", getRecipientListFromOneFile(Utilities.PARLIAMENT_CONTACT_TYPE, mav.getModelMap()));

		UploadedFile uploadFile = new UploadedFile();
		uploadFile.setContactType(Utilities.PARLIAMENT_CONTACT_TYPE);
		mav.addObject("uploadedFile", uploadFile);
		
		if (message != null){
			mav.addObject("downloadError", message);
		}

		return mav;

	}

	private List<Person> getRecipientListFromOneFile(String contactType, ModelMap model){
		
		List<Person> recipients = null;
		
		if (contactType.equalsIgnoreCase(Utilities.ELECTION_CONTACT_TYPE)){
			// We retrieve the Election list from the CSV file
			CsvFile file = Utilities.getListOfRecipients(logger, contactType);
			if (file != null){
				recipients = file.getRecipients();
			}

			if (file.getErrorMessage() != null){
				model.addAttribute("errorMessage"+contactType, file.getErrorMessage() + " (" + contactType + ")");
			}
		}else if (contactType.equalsIgnoreCase(Utilities.PARLIAMENT_CONTACT_TYPE)){
			// We retrieve the Parliament list from the MyParliament API
			recipients = Utilities.getParliamentaryContacts();
		}
		
		
		if (recipients == null){
			model.addAttribute("errorMessage"+contactType, "There was a problem while trying to retrieve the recipient list (" + contactType + ")");
		}
		return recipients;
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
			model.addAttribute("uploadErrorMessage", result.getFieldError().getDefaultMessage());
		}else{
			try {  

				String filePath = "/tmp/electionsms/ElectionPhoneNumberList.csv";

				inputStream = file.getInputStream();
				
				// Creating new file here
				File newFile = new File(filePath);  
				if (!newFile.exists()) {  
					newFile.createNewFile();
				}else{
					File backupFile = new File("/tmp/" + fileName+".old");  
					if (backupFile.exists()) {
						backupFile.delete();
					}
					newFile.renameTo(new File("/tmp/" + fileName+".old"));
					newFile = new File(filePath);
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
					if (outputStream != null){
						outputStream.close();
					}
				} catch (IOException e) {
					logger.error("Problem closing OutputStream.");
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
		model.addAttribute("electionRecipients", getRecipientListFromOneFile(Utilities.ELECTION_CONTACT_TYPE, model));
		model.addAttribute("parliamentRecipients", getRecipientListFromOneFile(Utilities.PARLIAMENT_CONTACT_TYPE, model));

		// Reinitializing upload object
		UploadedFile uploadFile = new UploadedFile();
		uploadFile.setContactType(Utilities.PARLIAMENT_CONTACT_TYPE);
		model.addAttribute("uploadedFile", uploadFile);

		return Constants.RECIPIENT_LIST_VIEW;

	}

	@RequestMapping(value = "/download/{file_name}", method = RequestMethod.GET)
	private ModelAndView getElectionFile(@PathVariable("file_name") String fileName, HttpServletResponse response){

		try {
			InputStream is = new FileInputStream("//tmp//" + fileName + ".csv");  
			// copy it to response's OutputStream
			
			response.setContentType("application/csv");
		    response.setHeader("Content-Disposition", "attachment; filename="+fileName+".csv");
			
			IOUtils.copy(is, response.getOutputStream());
			response.flushBuffer();
		} catch (IOException ex) {
			logger.info("Error writing file to output stream. Filename was '" + fileName + "'");
			return showlist("Problem occured when trying to download CSV file.");
		}
		
		return null;

	}

}
