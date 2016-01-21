package com.aisla.newrolit.connections;
/*
 * Author: Justin Cazares
 * Date 3/16/2015
 * 
 * This class holds the connection parameters for connection to hosts logged in the JSON objects
 * Actions:
 * 		Can load the default connection (if one is set)
 * 		Can check to see if the server is up (via a ping)
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.prefs.Preferences;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.aisla.newrolit.global.HostType;
import com.aisla.newrolit.global.LoginType;
import com.aisla.newrolit.global.Paths;

public class ConnectionData {
	private byte [] hostIP = new byte[4];
	private int port = 0;
	private int codePage = 37;
	private HostType hostType = HostType.AS400;
	private boolean writeLog = false;
	private boolean negotiationDelay = false;
	private LoginType loginType;
	private String user = "";
	private String pass = "";
	private String singleDomain = "";
	private String name = "";
	private Socket socket = null;
	static String _filePath = Paths.GetHomePath();
	private String displayDevice = "";
	private boolean killer = false;
	
	private BufferedInputStream inputStream = null;
	private BufferedOutputStream outputStream = null;
	
	public ConnectionData(){
		
	}
	
	public ConnectionData(byte[] iIp, int iPort, int iCodePage, HostType iHostType, boolean iWriteLog, 
			boolean iNegotiationDelay, LoginType iLoginType, String iUser, String iPass, String iName){
		this.hostIP = iIp;
		this.port = iPort;
		this.codePage = iCodePage;
		this.hostType = iHostType;
		this.writeLog = iWriteLog;
		this.negotiationDelay = iNegotiationDelay;
		this.loginType = iLoginType;
		this.user = iUser;
		this.pass = iPass;
		this.name = iName;
	}
	public byte [] getHostIP() {
		return hostIP;
	}
	public void setHostIP(byte [] hostIP) {
		this.hostIP = hostIP;
	}
	public void setHostIP(String ip){
		
		String sp[] = ip.split("\\.");
		byte[] bytes = new byte[4];
		bytes[0] = Integer.valueOf( (sp[0]) ).byteValue();
		bytes[1] = Integer.valueOf( (sp[1]) ).byteValue();
		bytes[2] = Integer.valueOf( (sp[2]) ).byteValue();
		bytes[3] = Integer.valueOf( (sp[3]) ).byteValue();
		this.setHostIP( bytes );
	}
	public String getHostIPString() {
		String hostIpStr = String.valueOf(0xFF & hostIP[0]) + "."; 
		hostIpStr += String.valueOf(0xFF & hostIP[1]) + ".";
		hostIpStr += String.valueOf(0xFF & hostIP[2]) + ".";
		hostIpStr += String.valueOf(0xFF & hostIP[3]);
		return hostIpStr;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getCodePage() {
		return codePage;
	}
	public void setCodePage(int codePage) {
		this.codePage = codePage;
	}
	public HostType getHostType() {
		return hostType;
	}
	public void setHostType(HostType hostType) {
		this.hostType = hostType;
	}
	public boolean isNegotiationDelay() {
		return negotiationDelay;
	}
	public void setNegotiationDelay(boolean negotiationDelay) {
		this.negotiationDelay = negotiationDelay;
	}
	public boolean isWriteLog() {
		return writeLog;
	}
	public void setWriteLog(boolean writeLog) {
		this.writeLog = writeLog;
	}
	public LoginType getLoginType() {
		return loginType;
	}
	public void setLoginType(LoginType loginType) {
		this.loginType = loginType;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDisplayDevice() {
		return displayDevice;
	}
	public void setDisplayDevice(String displayDevice) {
		this.displayDevice = displayDevice;
	}
	public BufferedOutputStream getOutputStream() {
		return outputStream;
	}
	public void setOutputStream(BufferedOutputStream outputStream) {
		this.outputStream = outputStream;
	}
	public Properties getDataConnection(HttpServletRequest request){
		
		Properties properties = new Properties();

		 String  propertyFolder ="/properties";
			String serverFolder =request.getSession().getServletContext().getRealPath(propertyFolder);
		   InputStream is = null;
				try {
					is = new FileInputStream(serverFolder+"/archivo.properties");
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				   try {
					properties.load(is);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
		return properties;
		
	}
	public void loadDefaultConnection(HttpServletRequest request){	// Loads the default connection to this connection object
		
			//JSONArray conns = (JSONArray) getJSONConnectionsObject().get("connections");
			
			//for(int i = 0; i < conns.size(); i++){
				//JSONObject temp = (JSONObject) conns.get(i);
			Properties properties =getDataConnection(request);
				ConnectionData conn = new ConnectionData();
				//Preferences root = Preferences.userNodeForPackage(ConnectionsHandler.class);
				//if(!root.get("default_connection", "no_default_connection").equals("no_default_connection") &&	// check to see if a default connection is set
						//root.get("default_connection", "").equals(temp.get("name")) ){						// check to see if the current iteration is the correct connection
					
				//if (temp.get("name").equals("Test Server")){
					//	Special way of converting the string representation of the IP to a byte array
					String tempIP = properties.getProperty("ip").toString();
					String sp[] = tempIP.split("\\.");
					byte[] bytes = new byte[4];
					bytes[0] = Integer.valueOf( (sp[0]) ).byteValue();
					bytes[1] = Integer.valueOf( (sp[1]) ).byteValue();
					bytes[2] = Integer.valueOf( (sp[2]) ).byteValue();
					bytes[3] = Integer.valueOf( (sp[3]) ).byteValue();
					this.setHostIP( bytes );
					
					this.setPort(Integer.parseInt( properties.getProperty("port").toString() ));
					this.setCodePage(Integer.parseInt( properties.getProperty("code_page").toString() ));
					
					
					if(Integer.parseInt(properties.getProperty("host_type").toString() ) == HostType.AS400.id() ){
						this.setHostType(HostType.AS400);
					}else if( Integer.parseInt(properties.getProperty("host_type").toString() ) == HostType.i36.id() ){
						this.setHostType(HostType.i36);
					}else if( Integer.parseInt(properties.getProperty("host_type").toString() ) == HostType.iSeries.id() ){
						this.setHostType(HostType.iSeries);
					}
					
					
					if(Integer.parseInt(properties.getProperty("login_type").toString() ) == LoginType.BASIC.id() ){
						this.setLoginType(LoginType.BASIC);
					}else if( Integer.parseInt(properties.getProperty("login_type").toString() ) == LoginType.SINGLE.id() ){
						this.setLoginType(LoginType.SINGLE);
					}else if( Integer.parseInt(properties.getProperty("login_type").toString() ) == LoginType.EXTERNAL_DB.id() ){
						this.setLoginType(LoginType.EXTERNAL_DB);
					}
					
					this.setUser(properties.getProperty("user").toString());
					this.setPass(properties.getProperty("pass").toString());
					this.setName(properties.getProperty("name").toString());
					if(properties.getProperty("negotiation_delay").equals("1")){
						conn.setNegotiationDelay(true);
					}else{
						conn.setNegotiationDelay(false);
					}
					if(properties.getProperty("write_log").equals("1")){
						conn.setWriteLog(true);
					}else{
						conn.setWriteLog(false);
					}
				//}
			//}
		
	}
	public JSONObject getJSONConnectionsObject(){
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
	
	public boolean isServerUp(String serverIp) {
		try {
			byte [] ip = new byte[4];
			for(int i = 0; i < 4; i++) {
				ip[i] = Integer.valueOf(Integer.parseInt(serverIp.replace(".", "-").split("-")[i])).byteValue();
			}
			return InetAddress.getByAddress(ip).isReachable(5000);
		} catch (UnknownHostException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public BufferedInputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(BufferedInputStream inputStream) {
		this.inputStream = inputStream;
	}
	public boolean isKiller() {
		return killer;
	}
	public void setKiller(boolean killer) {
		this.killer = killer;
	}

	public String getSingleDomain() {
		return singleDomain;
	}

	public void setSingleDomain(String singleDomain) {
		this.singleDomain = singleDomain;
	}
	
}
