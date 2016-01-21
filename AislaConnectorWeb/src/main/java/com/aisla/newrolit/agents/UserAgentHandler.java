package com.aisla.newrolit.agents;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.aisla.newrolit.global.Paths;

public class UserAgentHandler {
	
	String _filePath = "";
	boolean isUserSet = false;
	
	public synchronized void writeAgent(String type, int id, JSONObject iAgent ){
		
		if(!isUserSet){
			return;
		}
		try{
		JSONObject root = getJSONAgentObject();
		
		JSONObject agent = (JSONObject) root.get(type);
		
		if(agent == null){
			agent = new JSONObject();
			root.put(type, agent);
		}
		
		agent.put(id, iAgent);
		
		writeJSON(root);
		
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public synchronized JSONObject getAllAgents(){
		if(!isUserSet){
			return null;
		}
		JSONObject agents =  (JSONObject) getJSONAgentObject();
		
		return agents;
	}
	
	
	
	
	
	
	
	
	public void setFilePath(String username){
		
		_filePath = Paths.GetHomePath() + "\\InfiniteCloud\\Agents\\IC_" + username + "_Agents.JSON";
		isUserSet = true;
		createFileIfNotExists();
	}
	
	public void createFileIfNotExists(){
		
		
		File yourFile = new File(Paths.GetHomePath() + "\\InfiniteCloud\\Agents");
		if(!yourFile.exists()) {
		    yourFile.mkdir();
		}
		
		File yourFile2 = new File(_filePath);
		if(!yourFile.exists()) {
		    try {
				yourFile2.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
public synchronized JSONObject getJSONAgentObject(){
		if(!isUserSet){
			return null;
		}
		JSONParser parser = new JSONParser();
		JSONObject obj = null;
		FileReader fr = null;
		try {
			fr = new FileReader(_filePath);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		try {
			obj = (JSONObject) parser.parse( fr );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		try {
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return obj;
	}
	
	public synchronized void writeJSON(JSONObject root){
		if(!isUserSet){
			return;
		}
		FileWriter file = null;
        try {
        	file = new FileWriter(_filePath);
            file.write(root.toJSONString());
            System.out.println("Successfully Copied JSON Agent to File...");
            System.out.println("\nJSON Agent: " + root.toJSONString());
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
