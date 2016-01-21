package com.aisla.newrolit.agents;

import com.aisla.newrolit.global.Paths;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.aisla.newrolit.users.UserHandler;
import com.aisla.newrolit.users.UserType;

public class AgentHandler {
	static ArrayList<Agent> _agents = new ArrayList<Agent>();
	static String _filePath = Paths.GetHomePath() + "\\InfiniteCloud\\Agents\\IC_Agents.JSON";
	
	
	public String getFilePath(){
		return _filePath;
	}
	
	public synchronized void writeJSON(JSONObject root){
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
	
	
	
	
	
	
	@SuppressWarnings({ "unchecked" })
	public synchronized boolean writeAgent(String username, String password, JSONObject iAgent, String type, boolean iDefault){
		try{
		JSONObject root = getJSONAgentObject();
		
		JSONObject agent = (JSONObject) root.get(type);
		
		if(agent == null){
			agent = new JSONObject();
			root.put(type, agent);
		}
		if(iDefault){
			agent.put("defaultCloud", iAgent);
		}else{
			agent.put(username, iAgent);
		}
		
		
		writeJSON(root);
		
		return true;
		
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public synchronized void deleteAgent(String username, String password, String type, boolean iDefault) {
		JSONObject root = getJSONAgentObject();
		JSONObject agent = (JSONObject) root.get(type);
		
		UserHandler u = new UserHandler();
		
		if(iDefault && u.getAuthority(username,password) == UserType.ADMINISTRATOR){
			JSONObject temp = (JSONObject) agent.get("defaultCloud");
			temp.clear();
			
		}else if( !iDefault && u.checkValidity(username, password) && 
				(u.getAuthority(username, password) == UserType.ADMINISTRATOR || u.getAuthority(username, password) == UserType.STANDARD_USER) ){
			
			JSONObject temp = (JSONObject) agent.get(username);
			temp.clear();
		}else{
			System.out.println("Could not delete agent. : " + username + "; type : " + type + "; iDefault : " + iDefault);
		}
		
		
		writeJSON(root);
		
	}
	
	
	
	public synchronized JSONObject getUserAgent(String iUser, String iPass, String type){
		JSONObject agent =  (JSONObject) getJSONAgentObject().get(type);
		
		agent = (JSONObject) agent.get(iUser);
		
		return agent;
	}
	
	public synchronized JSONObject getDefaultAgent(String iUser, String iPass, String type){
		JSONObject agent =  (JSONObject) getJSONAgentObject().get(type);
		
		agent = (JSONObject) agent.get("defaultCloud");
		
		return agent;
	}
	
	public synchronized JSONObject getIDAgent(){
		JSONObject agent =  (JSONObject) getJSONAgentObject().get("ID");
		
		agent = (JSONObject) agent.get("defaultCloud");
		
		return agent;
	}
	
	public synchronized JSONObject getAgentUserAndDefault(String iUser, String iPass, String type){
		JSONObject root = new JSONObject();
		
		JSONObject agent =  (JSONObject) getJSONAgentObject().get(type);
		
		root.put("defaultCloud", 	(JSONObject) agent.get("defaultCloud"));
				
		root.put(iUser, 			(JSONObject) agent.get(iUser));
		
		return root;
	}
	
	
	
//	
//	
//	public synchronized User getAgents(String iUser, String iPass){
//		JSONArray agents =  (JSONArray) getJSONAgentIDObject().get("agents");
//		
//		for (int i = 0; i < users.size(); i++){
//			
//			JSONObject temp = (JSONObject) users.get(i);
//			try{
//				
//				if( temp.get("username").toString().equals(iUser) && temp.get("password").toString().equals( toHash(iPass) ) ){
//					
//					User user = new User();
//					user.setUser(temp.get("username").toString());
//					user.setPassword(temp.get("password").toString());
//					
//					
//					if(temp.get("usertype").equals("ADMINISTRATOR")){
//						user.setUserType(UserType.ADMINISTRATOR);
//						
//					} else if(temp.get("usertype").equals("STANDARD_USER")){
//						user.setUserType(UserType.STANDARD_USER);
//						
//					} else if(temp.get("usertype").equals("GUEST")){
//						user.setUserType(UserType.GUEST);
//					}
//					user.setUserID(Integer.parseInt(temp.get("userID").toString()));
//					
//					
//					return user;
//				}
//			} catch (NoSuchAlgorithmException e) {
//				e.printStackTrace();
//			}
//		}
//		return null;
//		
//	}
	
	
	
	public synchronized JSONObject getJSONAgentObject(){
		
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
	
}
