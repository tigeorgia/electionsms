package com.tigeorgia.model;

import java.util.ArrayList;

public class Message {
	
	private String body;
	private ArrayList<String> chosenGroups;

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public ArrayList<String> getChosenGroups() {
		return chosenGroups;
	}

	public void setChosenGroups(ArrayList<String> chosenGroups) {
		this.chosenGroups = chosenGroups;
	}	
	
}
