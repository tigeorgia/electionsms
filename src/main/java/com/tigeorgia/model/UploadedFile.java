package com.tigeorgia.model;

import org.springframework.web.multipart.MultipartFile;

public class UploadedFile {

	private MultipartFile file;
	private String contactType;

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public String getContactType() {
		return contactType;
	}

	public void setContactType(String contactType) {
		this.contactType = contactType;
	}
		
}
