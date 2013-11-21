package com.tigeorgia.model;

import java.util.ArrayList;

public class Message {
	
	private String body;
	private String lang;
	private ArrayList<String> chosenParliamentaryGroups;
	private ArrayList<String> chosenElectionGroups;

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public ArrayList<String> getChosenParliamentaryGroups() {
		return chosenParliamentaryGroups;
	}

	public void setChosenParliamentaryGroups(
			ArrayList<String> chosenParliamentaryGroups) {
		this.chosenParliamentaryGroups = chosenParliamentaryGroups;
	}

	public ArrayList<String> getChosenElectionGroups() {
		return chosenElectionGroups;
	}

	public void setChosenElectionGroups(ArrayList<String> chosenElectionGroups) {
		this.chosenElectionGroups = chosenElectionGroups;
	}

}
