package com.tigeorgia.validator;

import java.io.InputStream;

import org.apache.log4j.Logger;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.tigeorgia.controller.ShowListController;
import com.tigeorgia.model.CsvFile;
import com.tigeorgia.model.UploadedFile;
import com.tigeorgia.util.Utilities;

public class UploadedFileValidator implements Validator{

	private static final Logger logger = Logger.getLogger(ShowListController.class);

	@Override
	public boolean supports(Class<?> arg0) {
		return false;
	}
	
	@Override  
	public void validate(Object uploadedFile, Errors errors) {  

		UploadedFile file = (UploadedFile) uploadedFile;  

		if (file.getFile().getSize() == 0) {  
			errors.rejectValue("file", "uploadForm.selectFile",  
					"Please select a file.");  
		}else{
			// Check if it is formatted as a CSV file, with headers "name,langauge,number,groups"
			CsvFile csvfile = Utilities.checkUploadedListOfRecipients(file.getFile(), logger);
			String errorMessage = csvfile.getErrorMessage();
			if (errorMessage != null){
				errors.rejectValue("file", "fileupload.error", errorMessage);
			}
		}

	}  

}
