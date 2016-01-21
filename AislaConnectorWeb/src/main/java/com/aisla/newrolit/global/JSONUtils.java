package com.aisla.newrolit.global;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public abstract class JSONUtils {
	public static JSONObject stringToJSON(String string){
		JSONParser parser = new JSONParser();
		JSONObject obj = null;
		
		try {
			obj = (JSONObject) parser.parse( string );
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return obj;
	}
}
