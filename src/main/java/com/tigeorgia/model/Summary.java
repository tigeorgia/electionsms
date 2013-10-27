package com.tigeorgia.model;

import java.util.List;

public class Summary {

	private int failNumber;
	private int successNumber;
	private int totalNumber;
	private List<Person> didntReceive;
	private int totalNumberOfGroups;
	
	public List<Person> getDidntReceive() {
		return didntReceive;
	}
	public void setDidntReceive(List<Person> didntReceive) {
		this.didntReceive = didntReceive;
	}
	public int getFailNumber() {
		return failNumber;
	}
	public void setFailNumber(int failNumber) {
		this.failNumber = failNumber;
	}
	public int getSuccessNumber() {
		return successNumber;
	}
	public void setSuccessNumber(int successNumber) {
		this.successNumber = successNumber;
	}
	public int getTotalNumber() {
		return totalNumber;
	}
	public void setTotalNumber(int totalNumber) {
		this.totalNumber = totalNumber;
	}
	public int getTotalNumberOfGroups() {
		return totalNumberOfGroups;
	}
	public void setTotalNumberOfGroups(int totalNumberOfGroups) {
		this.totalNumberOfGroups = totalNumberOfGroups;
	}
	
	
}
