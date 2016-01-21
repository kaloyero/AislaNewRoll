package com.aisla.newrolit.agents;

import org.json.simple.JSONObject;

public class Agent {
	
	private JSONObject data = null;
	
	public Agent(){
		
	}

	public JSONObject getData() {
		return data;
	}

	public void setData(JSONObject data) {
		this.data = data;
	}
	
	
}
