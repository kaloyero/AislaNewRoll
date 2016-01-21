package com.aisla.newrolit.system;

import com.aisla.newrolit.global.Paths;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Settings {
	
	static String _filePath = Paths.GetHomePath() + "\\InfiniteCloud\\SystemSettings\\cloudSystemSettings.json";
	
	
	
	public static synchronized int getCleanID(){
		JSONObject root = getJSONObject();
		
		if(!root.containsKey("INCREMENT_ID")){
			root.put("INCREMENT_ID", 0);
		}
		
		int i = Integer.parseInt(root.get("INCREMENT_ID").toString());
		
		i++;
		
		root.put("INCREMENT_ID", i);
		
		writeJSON(root);
		
		return i;
	}
	
	
	public static synchronized void writeJSON(JSONObject root){
		FileWriter file = null;
        try {
        	file = new FileWriter(_filePath);
            file.write(root.toJSONString());
            System.out.println("Successfully Copied JSON Object to File...");
            System.out.println("\nJSON Object: " + root.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
 
        } finally {
            try {
				file.flush();
				file.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
            
        }
	}
	
	public  static synchronized  JSONObject getJSONObject(){
		
		JSONParser parser = new JSONParser();
		JSONObject obj = null;
		FileReader fr = null;
		try {
			fr = new FileReader(_filePath);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			obj = (JSONObject) parser.parse( fr );
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return obj;
	}
}
