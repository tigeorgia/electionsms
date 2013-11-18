package com.tigeorgia.model;

import java.util.List;

public class CsvFile {
	
	private List<Person> recipients;
	private String errorMessage;
	
	public CsvFile(){}
	
	public CsvFile(List<Person> recipients, String errorMessage) {
		super();
		this.recipients = recipients;
		this.errorMessage = errorMessage;
	}
	public List<Person> getRecipients() {
		return recipients;
	}
	public void setRecipients(List<Person> recipients) {
		this.recipients = recipients;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	

}
