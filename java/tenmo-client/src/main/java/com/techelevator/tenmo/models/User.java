package com.techelevator.tenmo.models;

public class User {

	private long id;
	private String username;

	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		if (this.id != 0) {
			return "ID: " + id +"\tName: " + username;
		} else {
			return "Cancel Transfer Request";
		}
	}
}
