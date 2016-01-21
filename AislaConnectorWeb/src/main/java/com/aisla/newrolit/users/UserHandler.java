package com.aisla.newrolit.users;

import com.aisla.newrolit.global.Paths;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.aisla.newrolit.system.Settings;

public class UserHandler {
	
	static ArrayList<User> _users = new ArrayList<User>();
	static String _filePath = Paths.GetHomePath() + "\\InfiniteCloud\\Users\\IC_Users.JSON";
	
	
	
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
				e.printStackTrace();
			}
            
        }
	}
	public boolean canEditAgents(String username, String password){
		if(checkValidity( username, password )){
			if( getAuthority( username, password ) == UserType.ADMINISTRATOR ){
				return true;
			}
		}else{
			return false;
		}
		return false;
	}
	
public void createFileIfNotExists(){
		
		
		File yourFile = new File(Paths.GetHomePath() + "\\InfiniteCloud\\Users");
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
	
	
	
	
	@SuppressWarnings({ "unchecked" })
	public int createNewUser(User iUser) throws NoSuchAlgorithmException{
		createFileIfNotExists();
		JSONObject root = getJSONUsersObject();
		JSONArray users = (JSONArray) root.get("Users");
		
		int cleanID = Settings.getCleanID();
		
		JSONObject nu = new JSONObject();
		nu.put("username", iUser.getUser()					);
		nu.put("password", toHash( iUser.getPassword() ) 	);	// Make sure that the password is hashed!
		nu.put("usertype", iUser.getUserType().toString()	);
		nu.put("userID", cleanID);
		users.add(nu);
		
		writeJSON(root);
		
		return cleanID;
	}
	
	public void deleteUser(String username, String password) throws NoSuchAlgorithmException{
		createFileIfNotExists();
		JSONObject root = getJSONUsersObject();
		JSONArray users = (JSONArray) root.get("Users");
		JSONArray newUsers = new JSONArray();
		
		for( int i = 0; i < users.size(); i++){
			
			JSONObject user = (JSONObject) users.get(i);
			
			if ( user.get("username").equals(username) && user.get("password").equals(toHash(password) ) ){
				System.out.println("Deleting User: " + username );
			}else{
				newUsers.add(user);
			}
				
		}
		
		root.put("Users", newUsers);
		
		writeJSON(root);
		
	}
	
	public User getUser(String iUser, String iPass){
		createFileIfNotExists();
		JSONArray users =  (JSONArray) getJSONUsersObject().get("Users");
		
		for (int i = 0; i < users.size(); i++){
			
			JSONObject temp = (JSONObject) users.get(i);
			try{
				
				if( temp.get("username").toString().equals(iUser) && temp.get("password").toString().equals( toHash(iPass) ) ){
					
					User user = new User();
					user.setUser(temp.get("username").toString());
					user.setPassword(temp.get("password").toString());
					
					
					if(temp.get("usertype").equals("ADMINISTRATOR")){
						user.setUserType(UserType.ADMINISTRATOR);
						
					} else if(temp.get("usertype").equals("STANDARD_USER")){
						user.setUserType(UserType.STANDARD_USER);
						
					} else if(temp.get("usertype").equals("GUEST")){
						user.setUserType(UserType.GUEST);
					}
					user.setUserID(Integer.parseInt(temp.get("userID").toString()));
					
					
					return user;
				}
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		return null;
		
	}
	
	public boolean checkValidity(String user, String pass){
		createFileIfNotExists();
		JSONObject obj = getJSONUsersObject();
		JSONArray users = (JSONArray) obj.get("Users");
		
		for (int i = 0; i < users.size(); i++){
			
			JSONObject temp = (JSONObject) users.get(i);
			try{
				if( temp.get("username").toString().equals(user) && temp.get("password").toString().equals( toHash(pass) ) ){
					return true;
				}
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		return false; 
	}
	
	
	public UserType getAuthority(String user, String pass){
		createFileIfNotExists();
		JSONObject obj = getJSONUsersObject();
		JSONArray users = (JSONArray) obj.get("Users");
		
		for (int i = 0; i < users.size(); i++){
			
			JSONObject temp = (JSONObject) users.get(i);
			try{
				if( temp.get("username").toString().equals(user) && temp.get("password").toString().equals( toHash(pass) ) ){
					
					if(temp.get("usertype").toString().equalsIgnoreCase("administrator")){
						return UserType.ADMINISTRATOR;
					}else if(temp.get("usertype").toString().equalsIgnoreCase("standard_user")){
						return UserType.STANDARD_USER;
					}else if(temp.get("usertype").toString().equalsIgnoreCase("guest")){
						return UserType.GUEST;
					}else{
						return UserType.NONE;
					}
					
					
				}
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		return UserType.NONE; 
	}
	
	public String toHash(String password) throws NoSuchAlgorithmException{
		
		 
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes());
 
        byte byteData[] = md.digest();
 
        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
 
//        System.out.println("Hex format : " + sb.toString());
		
		return sb.toString();
	}
	
	public JSONObject getJSONUsersObject(){
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
	
	public JSONObject getJSONUsersObjectNoPass(){
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
		JSONArray users = (JSONArray) obj.get("Users");
		
		for (int i = 0; i < users.size(); i++){
			JSONObject temp = (JSONObject) users.get(i);
			temp.remove("password");
		}
		return obj;
	}
	public void resetPassword(String m_u) throws NoSuchAlgorithmException {
		JSONObject root = getJSONUsersObject();
		JSONArray users = (JSONArray) root.get("Users");
		
		for( int i = 0; i < users.size(); i++){
			JSONObject curUser = (JSONObject) users.get(i);
			if(curUser.get("username").toString().equals(m_u)){
				curUser.put("password", toHash("default"));
			}
		}
		
		writeJSON(root);
	}
	public void changePassword(String m_u, String password) throws NoSuchAlgorithmException {
		JSONObject root = getJSONUsersObject();
		JSONArray users = (JSONArray) root.get("Users");
		
		for( int i = 0; i < users.size(); i++){
			JSONObject curUser = (JSONObject) users.get(i);
			if(curUser.get("username").toString().equals(m_u)){
				curUser.put("password", toHash(password));
			}
		}
		writeJSON(root);
	}
}
