package com.tigeorgia.model;

import java.util.List;

public class CsvFile {
	
	private List<Person> electionRecipients;
	private List<Person> parliamentRecipients;
	private List<Person> recipients;
	private String errorMessage;
	
	public CsvFile(){}
	
	public CsvFile(List<Person> recipients, String errorMessage) {
		super();
		this.recipients = recipients;
		this.errorMessage = errorMessage;
	}
	
	
	
	public List<Person> getElectionRecipients() {
		return electionRecipients;
	}

	public CsvFile(List<Person> electionRecipients,
			List<Person> parliamentRecipients, String errorMessage) {
		super();
		this.electionRecipients = electionRecipients;
		this.parliamentRecipients = parliamentRecipients;
		this.errorMessage = errorMessage;
	}

	public void setElectionRecipients(List<Person> electionRecipients) {
		this.electionRecipients = electionRecipients;
	}

	public List<Person> getParliamentRecipients() {
		return parliamentRecipients;
	}

	public void setParliamentRecipients(List<Person> parliamentRecipients) {
		this.parliamentRecipients = parliamentRecipients;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public List<Person> getRecipients() {
		return recipients;
	}

	public void setRecipients(List<Person> recipients) {
		this.recipients = recipients;
	}

}
