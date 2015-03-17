package com.tigeorgia.model;

import java.util.ArrayList;

public class Person {
	
	private String name;
	private String language;
	private ArrayList<String> numbers;
	private ArrayList<String> groups;
	private String errorCode;
	private String email;
	
	public Person() {	}
	
	public Person(String name, String language, ArrayList<String> numbers, ArrayList<String> groups) {
		this.name = name;
		this.language = language;
		this.numbers = numbers;
		this.groups = groups;
	}
	
	public Person(String name, String language, ArrayList<String> numbers, ArrayList<String> groups, String email) {
		this.name = name;
		this.language = language;
		this.numbers = numbers;
		this.groups = groups;
		this.email = email;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<String> getNumbers() {
		return numbers;
	}
	public void setNumbers(ArrayList<String> numbers) {
		this.numbers = numbers;
	}
	public ArrayList<String> getGroups() {
		return groups;
	}
	public void setGroups(ArrayList<String> groups) {
		this.groups = groups;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	

}
