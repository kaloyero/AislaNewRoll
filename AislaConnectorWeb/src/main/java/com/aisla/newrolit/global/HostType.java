package com.aisla.newrolit.global;

public enum HostType {
	AS400 (1, "AS400"),
	iSeries (2, "iSeries"),
	i36 (3, "i36");
	
	private int _id;
	private String _description;
	HostType(int id, String description) {
		this._id = id;
		this._description = description;
	}
	
	public int id() { return this._id; }
	public String description() { return this._description; }
}
