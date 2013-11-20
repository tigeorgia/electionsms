package com.tigeorgia.model;

import java.util.ArrayList;

public class Message {
	
	private String body;
	private String lang;
	private ArrayList<String> chosenGroups;

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

	public ArrayList<String> getChosenGroups() {
		return chosenGroups;
	}

	public void setChosenGroups(ArrayList<String> chosenGroups) {
		this.chosenGroups = chosenGroups;
	}	
	
}
