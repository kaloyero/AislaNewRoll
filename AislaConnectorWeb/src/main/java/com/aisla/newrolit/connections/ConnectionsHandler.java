package com.aisla.newrolit.connections;

import com.aisla.newrolit.global.HostType;
import com.aisla.newrolit.global.LoginType;
import com.aisla.newrolit.global.Paths;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class ConnectionsHandler {
	
	static ArrayList<ConnectionData> _conns = new ArrayList<ConnectionData>();
	static String _filePath = Paths.GetHomePath();
	
	
	public void createFileIfNotExists(){
		
		
		File yourFile = new File(Paths.GetHomePath() );
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
	
	public void resetDefaultSettings(){
		Preferences root = Preferences.userNodeForPackage(ConnectionsHandler.class);
		
		root.put("not", "encrypted");
		
		Preferences subnode = root.node("defaultConnection");
		subnode.put("ip", "localhost");
		subnode.put("port", "8080");
		subnode.put("code_page", "37");
		subnode.put("host_type", "1");
		subnode.put("write_log", "1");
		subnode.put("negotiation_delay", "1");
		subnode.put("login_type", "1");
		subnode.put("username", "");
		subnode.put("password", "");
		
		
//		try {
//			root.exportSubtree(System.out);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (BackingStoreException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	public void setDefault(String name){
		Preferences root = Preferences.userNodeForPackage(ConnectionsHandler.class);
		
		root.put("default_connection", name);
		System.out.println("Default connection set to: " + name);
	}
	
	@SuppressWarnings("unchecked")
	public void editConnection(ConnectionData iConn){
		createFileIfNotExists();
		JSONObject root = getJSONConnectionsObject();
		JSONArray list = new JSONArray(); 
		JSONArray oldArray = (JSONArray) root.get("connections"); 
		int len = oldArray.size();
		if (oldArray != null) {
		   for ( int i = 0 ; i < len; i++ ){ 
		       //Excluding the item at position
			   JSONObject temp = (JSONObject) oldArray.get(i);
		        if ( !temp.get("name").equals(iConn.getName()) ) 
		        {
		            list.add(oldArray.get(i));
		            
		        }else{
		        	System.out.println("edited: " + iConn.getName());
		        	temp.put("ip", iConn.getHostIPString());
		        	temp.put("port", iConn.getPort());
		        	temp.put("host_type", iConn.getHostType().id());
		        	temp.put("code_page", iConn.getCodePage());
		        	temp.put("write_log", iConn.isWriteLog());
		        	temp.put("negotiation_delay", iConn.isNegotiationDelay());
		        	temp.put("login_type", iConn.getLoginType().id());
		        	temp.put("user", iConn.getUser());
		        	temp.put("pass", iConn.getPass());
		        	temp.put("name", iConn.getName());
		        	list.add(temp);
		        }
		   } 
		}
		root.put("connections", list);
		writeJSON(root);
	}
	
	public void deleteConnection(String conName){
		createFileIfNotExists();
		JSONObject root = getJSONConnectionsObject();
		JSONArray list = new JSONArray(); 
		JSONArray oldArray = (JSONArray) root.get("connections"); 
		int len = oldArray.size();
		if (oldArray != null) {
		   for ( int i = 0 ; i < len; i++ ){ 
		       //Excluding the item at position
			   JSONObject temp = (JSONObject) oldArray.get(i);
		        if ( !temp.get("name").equals(conName) ) 
		        {
		            list.add(oldArray.get(i));
		            
		        }else{
		        	System.out.println("Deleted: " + conName);
		        	
		        }
		   } 
		}
		root.put("connections", list);
		writeJSON(root);
	}
	
	public void writeJSON(JSONObject root){
		createFileIfNotExists();
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        }
	}
	
	@SuppressWarnings("unchecked")
	public void writeNewConnection(ConnectionData iConn) throws IOException{
		createFileIfNotExists();
		JSONObject root = getJSONConnectionsObject();
		JSONArray conns = (JSONArray) root.get("connections");
		JSONObject conn = new JSONObject();
		conn.put("ip", iConn.getHostIPString());
		conn.put("port", iConn.getPort());
		conn.put("host_type", iConn.getHostType().id());
		conn.put("write_log", iConn.isWriteLog());
		conn.put("negotiation_delay", iConn.isNegotiationDelay());
		conn.put("login_type", iConn.getLoginType().id());
		conn.put("user", iConn.getUser());
		conn.put("pass", iConn.getPass());
		conn.put("name", iConn.getName());
		conn.put("code_page", iConn.getCodePage());
		
		conns.add(conn);
		
		writeJSON(root);
	}
	
	
	// Reads all JSON connections from customTest.JSON
		public void readJSONConnections(){
			createFileIfNotExists();
			JSONArray conns = (JSONArray) getJSONConnectionsObject().get("connections");
			
			for(int i = 0; i < conns.size(); i++){
				JSONObject temp = (JSONObject) conns.get(i);
				ConnectionData conn = new ConnectionData();
				
				// Get all of the connections
				conn.setHostIP(  temp.get("ip").toString() );
				conn.setPort(Integer.parseInt( temp.get("port").toString() ));
				conn.setCodePage(Integer.parseInt( temp.get("code_page").toString() ));

				if(Integer.parseInt(temp.get("host_type").toString() ) == HostType.AS400.id() ){
					conn.setHostType(HostType.AS400);
				}else if( Integer.parseInt(temp.get("host_type").toString() ) == HostType.i36.id() ){
					conn.setHostType(HostType.i36);
				}else if( Integer.parseInt(temp.get("host_type").toString() ) == HostType.iSeries.id() ){
					conn.setHostType(HostType.iSeries);
				}
				if(Integer.parseInt(temp.get("login_type").toString() ) == LoginType.BASIC.id() ){
					conn.setLoginType(LoginType.BASIC);
				}else if( Integer.parseInt(temp.get("login_type").toString() ) == LoginType.SINGLE.id() ){
					conn.setLoginType(LoginType.SINGLE);
				}else if( Integer.parseInt(temp.get("login_type").toString() ) == LoginType.EXTERNAL_DB.id() ){
					conn.setLoginType(LoginType.EXTERNAL_DB);
				}
				conn.setUser(temp.get("user").toString());
				conn.setPass(temp.get("pass").toString());
				conn.setName(temp.get("name").toString());
				if(temp.get("negotiation_delay").equals("1")){
					conn.setNegotiationDelay(true);
				}else{
					conn.setNegotiationDelay(false);
				}
				if(temp.get("write_log").equals("1")){
					conn.setWriteLog(true);
				}else{
					conn.setWriteLog(false);
				}
				
				_conns.add(conn);
			}
		}
		
		// Gets a JSON object with data from the JSON connections file
		public JSONObject getJSONConnectionsObject(){
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
	
	
}
