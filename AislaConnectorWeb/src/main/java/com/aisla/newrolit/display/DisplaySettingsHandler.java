package com.aisla.newrolit.display;

import com.aisla.newrolit.global.Paths;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DisplaySettingsHandler {
	static String _filePath = Paths.GetHomePath() + "\\InfiniteCloud\\DisplaySettings\\IC_cloudDefault_DisplaySettings.JSON";
	
	public void createFileIfNotExists(){
		
		
		File yourFile = new File(Paths.GetHomePath() + "\\InfiniteCloud\\DisplaySettings");
		if(!yourFile.exists()) {
		    yourFile.mkdir();
		}
		
		File yourFile2 = new File(_filePath);
		if(!yourFile2.exists()) {
		    try {
				yourFile2.createNewFile();
				FileWriter file = null;
		        
	        	file = new FileWriter(_filePath);
	            file.write("{}");
				file.flush();
				file.close();
					
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setUser(String username){
		
		_filePath = Paths.GetHomePath() + "\\InfiniteCloud\\DisplaySettings\\IC_" + username + "_DisplaySettings.JSON";
		
	}
	
	public JSONObject getAll(String username){
		setUser(username);
		JSONObject rootUser = getJSONDisplaySettingsObject();
		setUser("cloudDefault");
		JSONObject rootDefault = getJSONDisplaySettingsObject();
		JSONObject root = new JSONObject();
		root.put("user", rootUser);
		root.put("cloudDefault", rootDefault);
		return root;
	}
	
	
	@SuppressWarnings("unchecked")
	public JSONObject getUserDisplaySettings(int userID){
		JSONObject root = getJSONDisplaySettingsObject();
		return root;
	}
	
	
	@SuppressWarnings("unchecked")
	public void saveDisplaySettings(JSONObject ds){
		writeJSON(ds);
	}
	
public JSONObject getJSONDisplaySettingsObject(){
		createFileIfNotExists();
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
	
	public void writeJSON(JSONObject root){
		createFileIfNotExists();
		FileWriter file = null;
        try {
        	file = new FileWriter(_filePath);
            file.write(root.toJSONString());
            System.out.println("Successfully copied JSON object to File...");
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
}
