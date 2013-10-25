package com.tigeorgia.model;

import java.util.ArrayList;

public class Person {
	
	private String name;
	private ArrayList<String> numbers;
	private String group;
	private String errorCode;
	
	public Person() {	}
	
	public Person(String name, ArrayList<String> numbers, String group) {
		this.name = name;
		this.numbers = numbers;
		this.group = group;
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
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	

}
