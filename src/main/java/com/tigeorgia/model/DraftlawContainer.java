package com.tigeorgia.model;

import java.util.ArrayList;

public class DraftlawContainer {
	
	private ArrayList<Draftlaw> draftlaws;
	private ArrayList<DraftlawValidationMessage> errorMessages;

	public ArrayList<Draftlaw> getDraftlaws() {
		return draftlaws;
	}
	public void setDraftlaws(ArrayList<Draftlaw> draftlaws) {
		this.draftlaws = draftlaws;
	}
	public ArrayList<DraftlawValidationMessage> getErrorMessages() {
		return errorMessages;
	}
	public void setErrorMessages(ArrayList<DraftlawValidationMessage> errorMessages) {
		this.errorMessages = errorMessages;
	}
	


}
