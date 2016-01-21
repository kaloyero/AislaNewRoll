package com.aisla.newrolit.global;

public enum LoginType {
	SINGLE (1, "SINGLE"),
	BASIC (2, "BASIC"),
	EXTERNAL_DB (3, "EXTERNAL_DB");
	
	private int _id;
	private String _description;
	LoginType(int id, String description) {
		this._id = id;
		this._description = description;
	}
	
	public int id() { return this._id; }
	public String description() { return this._description; }
}
