package com.aisla.newrolit.agents;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.aisla.newrolit.system.Settings;
import com.aisla.newrolit.global.Paths;

public class AgentController {
	static String _filePath = "";
	boolean setFilePath = false;
	
	public void setUsername(String username){
		_filePath = Paths.GetAgentsPath() + "\\IC_" + username + "_Agents.JSON";
		createFileIfNotExists();
		setFilePath = true;
	}
	
	public synchronized JSONObject getAll(){
		if(!setFilePath){
			return null;
		}
		JSONObject agents =  (JSONObject) getJSONObject();
		
		return agents;
	}
	
//	@SuppressWarnings("unchecked")
//	public synchronized void writeAgent(String type, int id, JSONObject iAgent ){
//		
//		if(!setFilePath){
//			return;
//		}
//		createFileIfNotExists();
//		try{
//		JSONObject root = (JSONObject) getJSONObject().get("IDs");
//		
//		JSONObject specifiedID = (JSONObject) root.get(Integer.toString(id));
//		if(specifiedID == null){
//			specifiedID = new JSONObject();
//			root.put(Integer.toString(id), specifiedID);
//		}
//		
//		JSONArray links = (JSONArray) specifiedID.get("agents");
//		if(links == null){
//			links = new JSONArray();
//			specifiedID.put("agents", links);
//		}
//		links.add(iAgent);
//		
//		writeJSON(root);
//		
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}
	
	
	
	public void createFileIfNotExists(){
		File yourFile = new File(_filePath);
		if(!yourFile.exists()) {
		    try {
				yourFile.createNewFile();
				
				BufferedWriter writer = new BufferedWriter( new FileWriter(yourFile));
				writer.write("{}");
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public synchronized JSONObject getJSONObject(){
		if(!setFilePath){
			return null;
		}
		createFileIfNotExists();
		
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
		if(!setFilePath){
			return;
		}
		createFileIfNotExists();
		
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
	
	public void saveGlobalAgent(JSONObject agent, String username,
			String password){
		
		JSONObject root = getJSONObject();
		JSONArray globalAgents = (JSONArray) root.get("GLOBAL_AGENTS");
		if(globalAgents == null){
			globalAgents = new JSONArray();
			root.put("GLOBAL_AGENTS", globalAgents);
		}
		
		boolean foundAgent = false;
		
		for(int i = 0; i < globalAgents.size(); i++){
			JSONObject temp = (JSONObject) globalAgents.get(i);
			
			if( temp.get("ID").toString().equals(agent.get("ID").toString()) ){
				globalAgents.set(i, agent);
				foundAgent = true;
				break;
			}
		}
		
		if(!foundAgent){
			agent.put("ID", Settings.getCleanID());
			globalAgents.add(agent);
		}
		
		
		writeJSON(root);
	}
	
	public void saveIDAgent(JSONObject agent, String username, String password){
		
		String oldFilePath = _filePath;
		
		setUsername("cloudDefault");
		
		JSONObject root = getJSONObject();
		
		root.put("ID_AGENT", agent);
		
		writeJSON(root);
		
		_filePath = oldFilePath;
	}
	
	public JSONObject getIDAgent(){
		String oldFilePath = _filePath;
		
		setUsername("cloudDefault");
		
		JSONObject root = getJSONObject();
		JSONObject idAgent = (JSONObject) root.get("ID_AGENT");
				
		_filePath = oldFilePath;
		
		return idAgent;
	}
	
	@SuppressWarnings("unchecked")
	public void saveDefaultGlobalAgent(JSONObject agent, String username,
			String password){
		
		String oldFilePath = _filePath;
		
		setUsername("cloudDefault");
		
		JSONObject root = getJSONObject();
		JSONArray globalAgents = (JSONArray) root.get("GLOBAL_AGENTS");
		if(globalAgents == null){
			globalAgents = new JSONArray();
			root.put("GLOBAL_AGENTS", globalAgents);
		}
		
		boolean foundAgent = false;
		
		for(int i = 0; i < globalAgents.size(); i++){
			JSONObject temp = (JSONObject) globalAgents.get(i);
			try{
			if( temp.get("ID").toString().equals(agent.get("ID").toString()) ){
				globalAgents.set(i, agent);
				foundAgent = true;
				break;
			}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		if(!foundAgent){
			agent.put("ID", Settings.getCleanID());
			globalAgents.add(agent);
		}
		
		
		writeJSON(root);
		_filePath = oldFilePath;
	}
	
	
	
	@SuppressWarnings({"unchecked" })
	public void saveNewDefaultAgent(JSONObject agent, String pageID, String username,
			String password) {
		System.out.println("Saving new default agent.");
		String oldFilePath = _filePath;
		
		setUsername("cloudDefault");
		
		
		JSONObject root = getJSONObject();
		JSONObject links;
		JSONObject agents;
		
		
		
		
		
		
		if(!root.containsKey("LINKS")){
			links = new JSONObject();
			root.put("LINKS", links);
		}
		if(!root.containsKey("AGENTS")){
			agents = new JSONObject();
			root.put("AGENTS", agents);
		}
		
		links = (JSONObject) root.get("LINKS");
		agents = (JSONObject) root.get("AGENTS");
		
		
		
		JSONObject link = (JSONObject) links.get(pageID);
		if(link == null){
			link = new JSONObject();
			links.put(pageID, link);
		}
		
		JSONArray agentLinks = (JSONArray) link.get("AGENT_LINKS");
		if(agentLinks == null){
			agentLinks = new JSONArray();
			link.put("AGENT_LINKS", agentLinks);
		}
		
//		int highestID = 0;
//		for( int i = 0; i < agentLinks.size(); i++){
//			JSONObject temp = (JSONObject) agentLinks.get(i);
//			if( Integer.parseInt(temp.get("ID").toString()) > highestID ){
//				highestID = Integer.parseInt(temp.get("ID").toString());
//			}
//		}
//		highestID++;
//		agent.put("ID", Integer.toString(highestID) );
		String newID = Settings.getCleanID() + "";
		agent.put("ID", newID);
		
		
		JSONObject newLink = new JSONObject();
		
		newLink.put( "ID", agent.get("ID").toString() );
		newLink.put( "TYPE", agent.get("TYPE").toString() );
		agentLinks.add(newLink);
		
		
		JSONObject storedAgentByType = (JSONObject) agents.get(agent.get("TYPE").toString());
		
		
		if(storedAgentByType == null){
			storedAgentByType = new JSONObject();
			agents.put(agent.get("TYPE").toString(), storedAgentByType);
		}
		
		storedAgentByType.put(agent.get("ID").toString(), agent);
		
		
		writeJSON(root);
		_filePath = oldFilePath;
	}
	
	
	@SuppressWarnings({ "unused", "unchecked" })
	public void saveNewAgent(JSONObject agent, String pageID, String username,
			String password) {
		System.out.println("Saving new user agent.");
		UserAgentHandler ah = new UserAgentHandler();
		
		JSONObject root = getJSONObject();
		JSONObject links;
		JSONObject agents;
		
		
		
		if(!root.containsKey("LINKS")){
			links = new JSONObject();
			root.put("LINKS", links);
		}
		if(!root.containsKey("AGENTS")){
			agents = new JSONObject();
			root.put("AGENTS", agents);
		}
		
		links = (JSONObject) root.get("LINKS");
		agents = (JSONObject) root.get("AGENTS");
		
		
		
		JSONObject link = (JSONObject) links.get(pageID);
		if(link == null){
			link = new JSONObject();
			links.put(pageID, link);
		}
		
		JSONArray agentLinks = (JSONArray) link.get("AGENT_LINKS");
		if(agentLinks == null){
			agentLinks = new JSONArray();
			link.put("AGENT_LINKS", agentLinks);
		}
		
		
		agent.put("ID", Integer.toString(Settings.getCleanID()) );
		
		
		JSONObject newLink = new JSONObject();
		
		newLink.put( "ID", agent.get("ID").toString() );
		newLink.put( "TYPE", agent.get("TYPE").toString() );
		agentLinks.add(newLink);
		
		
		JSONObject storedAgentByType = (JSONObject) agents.get(agent.get("TYPE").toString());
		
		
		if(storedAgentByType == null){
			storedAgentByType = new JSONObject();
			agents.put(agent.get("TYPE").toString(), storedAgentByType);
		}
		
		storedAgentByType.put(agent.get("ID").toString(), agent);
		
		
		writeJSON(root);
	}
	
	
	
	@SuppressWarnings({ "unused", "unchecked" })
	public void saveDefaultAgent(JSONObject agent, String pageID, String username,
			String password) {
		System.out.println("Saving default agent.");
		
		UserAgentHandler ah = new UserAgentHandler();
		
		String oldFilePath = _filePath;

		setUsername("cloudDefault");
		
		
		JSONObject root = getJSONObject();
		JSONObject links;
		JSONObject agents;
		
		
		
		if(!root.containsKey("LINKS")){
			links = new JSONObject();
			root.put("LINKS", links);
		}
		if(!root.containsKey("AGENTS")){
			agents = new JSONObject();
			root.put("AGENTS", agents);
		}
		
		links = (JSONObject) root.get("LINKS");
		agents = (JSONObject) root.get("AGENTS");
		
		
		
		JSONObject link = (JSONObject) links.get(pageID);
		if(link == null){
			link = new JSONObject();
			links.put(pageID, link);
		}
		
		JSONArray agentLinks = (JSONArray) link.get("AGENT_LINKS");
		if(agentLinks == null){
			agentLinks = new JSONArray();
			link.put("AGENT_LINKS", agentLinks);
		}
		
		boolean alreadyHasThatLink = false;
		for( int i = 0; i < agentLinks.size(); i++){
			JSONObject temp = (JSONObject) agentLinks.get(i);
			if(temp.get("ID").toString().equals(agent.get("ID").toString())){
				alreadyHasThatLink = true;
			}
		}
		
		String newID = null;
		if(!alreadyHasThatLink || Integer.parseInt(agent.get("ID").toString()) == -1 ){
			JSONObject newLink = new JSONObject();
			newID = Settings.getCleanID() + "";
			agent.put("ID", newID);
			
			newLink.put( "ID", agent.get("ID").toString() );
			newLink.put( "TYPE", agent.get("TYPE").toString() );
			agentLinks.add(newLink);
		}
		
		
		
		
		JSONObject storedAgent = (JSONObject) agents.get(agent.get("TYPE").toString());
		
		if(storedAgent == null){
			storedAgent = new JSONObject();
			agents.put(agent.get("TYPE").toString(), storedAgent);
		}
		
		
		storedAgent.put(agent.get("ID").toString(), agent);
		
//		JSONObject individualAgent = (JSONObject) storedAgent.get(agent.get("ID").toString());
		
//		if(individualAgent == null){
//			individualAgent = new JSONObject();
//			storedAgent.put( agent.get("ID").toString(), individualAgent );
//		}
		writeJSON(root);
		_filePath = oldFilePath;
	}
	
	

	@SuppressWarnings({ "unused", "unchecked" })
	public void saveAgent(JSONObject agent, String pageID, String username,
			String password) {
		System.out.println("Saving user agent.");
		UserAgentHandler ah = new UserAgentHandler();
		
		JSONObject root = getJSONObject();
		JSONObject links;
		JSONObject agents;
		
		
		
		if(!root.containsKey("LINKS")){
			links = new JSONObject();
			root.put("LINKS", links);
		}
		if(!root.containsKey("AGENTS")){
			agents = new JSONObject();
			root.put("AGENTS", agents);
		}
		
		links = (JSONObject) root.get("LINKS");
		agents = (JSONObject) root.get("AGENTS");
		
		
		
		JSONObject link = (JSONObject) links.get(pageID);
		if(link == null){
			link = new JSONObject();
			links.put(pageID, link);
		}
		
		JSONArray agentLinks = (JSONArray) link.get("AGENT_LINKS");
		if(agentLinks == null){
			agentLinks = new JSONArray();
			link.put("AGENT_LINKS", agentLinks);
		}
		
		boolean alreadyHasThatLink = false;
		for( int i = 0; i < agentLinks.size(); i++){
			JSONObject temp = (JSONObject) agentLinks.get(i);
			if(temp.get("ID").toString().equals(agent.get("ID").toString())){
				
				alreadyHasThatLink = true;
			}
			System.out.println(temp.get("ID").toString() + "    " + agent.get("ID").toString());
		}
		
		String cleanID = Settings.getCleanID() + "";
		
		if(!alreadyHasThatLink){ // if link did not already exist, create one
			JSONObject newLink = new JSONObject();
			
			newLink.put( "ID", cleanID );
			newLink.put( "TYPE", agent.get("TYPE").toString() );
			agentLinks.add(newLink);
			agent.put("ID", cleanID + "" );
		}
		
		
		
		
		JSONObject storedAgent = (JSONObject) agents.get(agent.get("TYPE").toString()); // gets the agent by TYPE
		
		if(storedAgent == null){
			storedAgent = new JSONObject();
			agents.put(agent.get("TYPE").toString(), storedAgent);
		}
		
		storedAgent.put(agent.get("ID").toString(), agent);
		
//		JSONObject individualAgent = (JSONObject) storedAgent.get(agent.get("ID").toString());
		
//		if(individualAgent == null){
//			individualAgent = new JSONObject();
//			storedAgent.put( agent.get("ID").toString(), individualAgent );
//		}
		writeJSON(root);
	}
	
	public void deleteAgent(String username, String id, String pageID, String type, boolean isDefault, boolean isGlobal){
		if(isDefault){
			setUsername("cloudDefault");
		}else{
			setUsername(username);
		}
		JSONObject root = getJSONObject();
		
		if(isGlobal){
			
			JSONArray globals = (JSONArray) root.get("GLOBAL_AGENTS");
			
			for(int i = 0; i < globals.size(); i++){
				JSONObject temp = (JSONObject) globals.get(i);
				if(temp.get("ID").toString().equals(id)){
					globals.remove(i);
				}
			}
			
			
			
		}else{
			
			
			
			
			// Goes through all of the links
			JSONObject links = (JSONObject) root.get("LINKS");
			JSONObject currPage = (JSONObject) links.get(pageID);
			
			JSONArray agentLinks = (JSONArray) currPage.get("AGENT_LINKS");
			
			for(int i = 0; i < agentLinks.size(); i++){
				JSONObject tempLink = (JSONObject) agentLinks.get(i);
				
				if(tempLink.get("ID").toString().equals(id)){
					agentLinks.remove(i); // Deletes the link on the page specified
					
				}
				
			}
			
			//Check to see if other pages use that link, if not, delete the whole agent
			boolean isUsed = false; // is used by other links
			
		    Iterator iter = links.entrySet().iterator();
		    
		    while(iter.hasNext()){
		    	Map.Entry entry = (Map.Entry)iter.next();
		    	
		    	JSONObject cp = (JSONObject) entry.getValue();
		    	JSONArray al = (JSONArray) cp.get("AGENT_LINKS");
		    	
		    	for( int i = 0; i < al.size(); i++){
		    		JSONObject tempLink = (JSONObject) al.get(i);
		    		if(tempLink.get("ID").toString().equals(id)){
		    			isUsed = true; // found another link that uses the agent
		    		}
		    	}
		    	
		    }
		    
		    if(!isUsed){
		    	JSONObject agents = (JSONObject) root.get("AGENTS");
		    	JSONObject agentType = (JSONObject) agents.get(type);
		    	agentType.remove(id);
		    }
			
		}
		
		
		
		writeJSON(root);
		
		
	}
	
}
