package com.aisla.newrolit.base;




import java.io.IOException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.aisla.newrolit.agents.AgentController;
import com.aisla.newrolit.agents.AgentID;
import com.aisla.newrolit.com.Comm5250Logic;
import com.aisla.newrolit.com.CommBufferLogic;
import com.aisla.newrolit.com.IComm;
import com.aisla.newrolit.com.ScreenElementType;
import com.aisla.newrolit.com.ScreenField;
import com.aisla.newrolit.com.ServerSession;
import com.aisla.newrolit.connections.ConnectionData;
import com.aisla.newrolit.connections.ConnectionsHandler;
import com.aisla.newrolit.customization.CustomizationManager;
import com.aisla.newrolit.customization.CustomizationUtils;
import com.aisla.newrolit.customization.CustomizedField;
import com.aisla.newrolit.display.DisplaySettingsHandler;
import com.aisla.newrolit.global.DataParser;
import com.aisla.newrolit.global.HostType;
import com.aisla.newrolit.global.JSONUtils;
import com.aisla.newrolit.global.LoginType;
import com.aisla.newrolit.service.SessionTimeService;
import com.aisla.newrolit.service.SessionTimeServiceImpl;
import com.aisla.newrolit.system.Settings;
import com.aisla.newrolit.users.User;
import com.aisla.newrolit.users.UserHandler;
import com.aisla.newrolit.users.UserType;



/**
 * Servlet implementation class ClientDataExchange
 * 
 * Author:Belasoft
 * 
 * This class serves as the main communication between the client and Rol It Server.
 */
@WebServlet("/ClientDataExchange")
public class ClientDataExchange extends HttpServlet {
	
	static  Hashtable<String, IComm> _comms = new Hashtable<String, IComm>();
	private IComm _comm = null;
	static  Hashtable<String, ServerSession> _sessions = new Hashtable<String, ServerSession>();
	Preferences sysPref = Preferences.systemRoot();
	
	SessionTimeService sessionTimeService =  new SessionTimeServiceImpl();	
	private String lastSessionIdCreated="";
	
	private String _cursorRow = "1";
	private String _cursorCol = "1";
	static  int _lastNewSessionId = 1;
	private String _i36SessionID = "";
	private String _sessionId = "session00001";
	int killerCount = 0;
	int dummyValue = 0;
	int maxUsers = 10;
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ClientDataExchange() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try{
			System.out.println("Communication started.");
		switch(request.getParameter("OPERATION")){
		
//		case "query_fields":
//			write2(response);
//			break;
			
		case "settings":
			settings(request, response);
			break;
			
		case "login":
			logUser(request, response);
//			getScreenResponse(request, response, true);
			break;
			
		case "screen_response":
			getScreenResponse(request, response, true);
			break;
			
		case "screen_refresh":
			getScreenRefresh(request, response);
			break;
		case "send_data":
			sendData(request, response, "");
			getScreenResponse(request, response, true);
			break;
			
		case "authentification":
			manageLoginCredentials(request, response);
			break;
			
		case "agents":
			manageAgents(request, response);
			break;
		case "displayProps":
			manageDisplayProps(request, response);
			break;
		case "user":
			manageUser(request, response);
			break;
		default:
			System.out.println("No operation code.");
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void manageUser(HttpServletRequest request,
			HttpServletResponse response){
		String username = request.getParameter("username").toString();
		String password = request.getParameter("password").toString();
		UserHandler uh = new UserHandler();
		
		String reqType = request.getParameter("SETTINGS_CODE").toString();
		try{
			if(reqType.equals("CREATE")){
				User tempUser = new User();
				tempUser.setPassword("default");
				tempUser.setUser(request.getParameter("NEW_USERNAME"));
				
				String priv = request.getParameter("PRIVILEGES").toString();
				
				if(priv.equals("ADMINISTRATOR")){
					tempUser.setUserType(UserType.ADMINISTRATOR);
					
				}else if(priv.equals("STANDARD_USER")){
					tempUser.setUserType(UserType.STANDARD_USER);
					
				}else{
					tempUser.setUserType(UserType.GUEST);
				}
				
				uh.createNewUser(tempUser);
				
			}else if(reqType.equals("RESET_PASSWORD")){
				
				String u = request.getParameter("M_USER").toString();
				
				uh.resetPassword(u);
				
			}else if(reqType.equals("CHANGE_PASSWORD")){
				String u = request.getParameter("M_USER").toString();
				String p = request.getParameter("M_USER_PASS").toString();
				
				uh.changePassword(u, p);
				
			}else if(reqType.equals("GET_ALL")){
				JSONObject allUsers = uh.getJSONUsersObjectNoPass();
				
				response.getWriter().write(allUsers.toJSONString());
				
				return;
			}
			
			JSONObject allUsers = uh.getJSONUsersObjectNoPass();
			
			response.getWriter().write(allUsers.toJSONString());
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void manageDisplayProps(HttpServletRequest request,
			HttpServletResponse response) {
		String username = request.getParameter("username").toString();
		String password = request.getParameter("password").toString();
		DisplaySettingsHandler ds = new DisplaySettingsHandler();
		if(request.getParameter("SETTINGS_CODE").toString().equals("SAVE")){
			String temp = request.getParameter("DISPLAY_OBJECT");
			JSONObject obj = JSONUtils.stringToJSON(temp);
			
			
			
			String isDefault = request.getParameter("ISDEFAULT").toString();
						
			if(isDefault.equals("true")){
				ds.setUser("cloudDefault");
				
			}else{
				ds.setUser(username);
			}
			
			ds.saveDisplaySettings(obj);
			
			
			
			
		}else if(request.getParameter("SETTINGS_CODE").toString().equals("GETALL")){
			
		}
		
		JSONObject root = ds.getAll(username);
		
		try {
			response.getWriter().write(root.toJSONString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public JSONObject getAllAgents(String username, String password){
		AgentController ac = new AgentController();
		ac.setUsername(username);
		JSONObject userAgents = ac.getJSONObject();
		
		ac.setUsername("cloudDefault");
		JSONObject defaultAgents = ac.getJSONObject();
		
		JSONObject root = new JSONObject();
		
		root.put("user", userAgents);
		root.put("cloudDefault", defaultAgents);
		
		return root;
	}
	
	@SuppressWarnings("unchecked")
	private void manageAgents(HttpServletRequest request,
			HttpServletResponse response) {
				
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		if( request.getParameter("SETTINGS_CODE").toString().equals("GET_ALL") ){
			AgentController ac = new AgentController();
			ac.setUsername(username);
			
			JSONObject root = new JSONObject();
			root = getAllAgents(username, password);
			
			try {
				response.getWriter().write(root.toJSONString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else if(request.getParameter("SETTINGS_CODE").equals("SAVE")){
			
			AgentController ac = new AgentController();
//			String pageID = request.getParameter("PAGEID");
			String isDefault = request.getParameter("ISDEFAULT");
			JSONObject agent = JSONUtils.stringToJSON(request.getParameter("AGENT"));
			String type = request.getParameter("TYPE").toString();
			String saveType = request.getParameter("NEW_SAVE").toString();
			String pageID = request.getParameter("PAGEID").toString();
			
			String agentID = agent.get("ID").toString();
			
			if(agentID == null){
				agent.put("ID", Settings.getCleanID() );
			}
			
			if(type.equals("ID")){
				ac.saveIDAgent(agent, username, password);
				JSONObject newAgents = ac.getJSONObject();
				try {
					JSONObject userAgents = ac.getJSONObject();
					
					ac.setUsername("cloudDefault");
					JSONObject defaultAgents = ac.getJSONObject();
					
					JSONObject root = new JSONObject();
					
					root.put("user", userAgents);
					root.put("cloudDefault", defaultAgents);
					
					response.getWriter().write(root.toJSONString());
					
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
				
				return;
			}
			
			boolean isNewSave = false;
			if(saveType.equals("SAVE_NEW")){
				isNewSave = true;
			}
			
			ac.setUsername(username);
			
			String pageType = agent.get("pageType").toString();
			
			if(isDefault.equals("true")){
				if(pageType.equals("STANDARD")){
					ac.saveDefaultGlobalAgent(agent, username, password);
				}else{
					if(isNewSave){
						ac.saveNewDefaultAgent(agent, pageID, username, password);
					}else{
						ac.saveDefaultAgent(agent, pageID, username, password);
					}
				}
			}else{
				if(pageType.equals("STANDARD")){
					ac.saveGlobalAgent(agent, username, password);
				}else{
					if(isNewSave){
						ac.saveNewAgent(agent, pageID, username, password);
					}else{
						ac.saveAgent(agent, pageID, username, password);
					}
				}
			}
			
		}else if(request.getParameter("SETTINGS_CODE").equals("DELETE")){
			
			String id = request.getParameter("ID").toString();
			
			boolean isDefault = false;
			if(request.getParameter("ISDEFAULT").toString().equals("true")){
				isDefault = true;
			}
			boolean isGlobal = false;
			if(request.getParameter("ISGLOBAL").toString().equals("true")){
				isGlobal = true;
			}
			
			String pageID = request.getParameter("PAGEID").toString();
			String agentType = request.getParameter("TYPE").toString();
			
			System.out.println("Deleting something. " + id + " " + agentType + " " + isDefault + " " + isGlobal);
			
			AgentController ac = new AgentController();
			ac.setUsername(username);
			ac.deleteAgent(username, id, pageID, agentType, isDefault, isGlobal);
			
			JSONObject root = new JSONObject();
			root = getAllAgents(username, password);
			
			try {
				response.getWriter().write(root.toJSONString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	

	public void manageLoginCredentials( HttpServletRequest request, HttpServletResponse response ){
		UserType ut = getUserPermissions(request, response);
		JSONObject r = new JSONObject();
		
		
		
		if( ut == UserType.ADMINISTRATOR ){
			r.put("user_type", "ADMINISTRATOR");
			
		}else if( ut == UserType.STANDARD_USER ){
			r.put("user_type", "STANDARD_USER");
			
		}else if( ut == UserType.GUEST ){
			r.put("user_type", "GUEST");
			
		}else if( ut == UserType.NONE){
			r.put("user_type", "NONE");
		}
		
		try {
			
			response.getWriter().write(r.toJSONString());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public UserType getUserPermissions( HttpServletRequest request, HttpServletResponse response ){
		String iUser = request.getParameter("username");
		String iPass = request.getParameter("password");
		
		UserHandler uh = new UserHandler();
		
		
		if(uh.checkValidity(iUser, iPass)){ // Only runs if the user is a valid user
			
			User user = uh.getUser(iUser, iPass);
			
			return user.getUserType();	
		}
		return UserType.NONE;
		
	}
	
	
	public void settings(HttpServletRequest request, HttpServletResponse response ){
		// Handles the settings sent from the user. Can return settings back to the user, or update the settings, depending on what the user sent.
		String temp = request.getParameter("SETTINGS_CODE");
		try{
			
			switch(temp){
			case "connection":
				String code = request.getParameter("SETTINGS_CONNECTION_CODE");
				ConnectionsHandler ch = new ConnectionsHandler();
				
				if(code.equals("getConnections")){
					// Returns all of the connections to the client
					response.getWriter().write(ch.getJSONConnectionsObject().toJSONString());
					
					
					
				}else if(code.equals("editConnection")){
					// Edit a connection, "key" is the name. No ID is implemented yet.
					ConnectionData tempCon = new ConnectionData();

					tempCon.setCodePage( Integer.parseInt( request.getParameter("code_page") ) );
					if(Integer.parseInt(request.getParameter("host_type").toString() ) == HostType.AS400.id() ){
						tempCon.setHostType(HostType.AS400);
					}else if( Integer.parseInt(request.getParameter("host_type").toString() ) == HostType.i36.id() ){
						tempCon.setHostType(HostType.i36);
					}else if( Integer.parseInt(request.getParameter("host_type").toString() ) == HostType.iSeries.id() ){
						tempCon.setHostType(HostType.iSeries);
					}
					tempCon.setHostIP(request.getParameter("ip"));
					if(Integer.parseInt(request.getParameter("login_type").toString() ) == LoginType.BASIC.id() ){
						tempCon.setLoginType(LoginType.BASIC);
					}else if( Integer.parseInt(request.getParameter("login_type").toString() ) == LoginType.SINGLE.id() ){
						tempCon.setLoginType(LoginType.SINGLE);
					}else if( Integer.parseInt(request.getParameter("login_type").toString() ) == LoginType.EXTERNAL_DB.id() ){
						tempCon.setLoginType(LoginType.EXTERNAL_DB);
					}
					tempCon.setName(request.getParameter("name"));
					
					if(request.getParameter("negotiation_delay").equals("true")){
						tempCon.setNegotiationDelay(true);
					}else{
						tempCon.setNegotiationDelay(false);
					}
					tempCon.setPass(request.getParameter("password"));
					tempCon.setPort(Integer.parseInt(request.getParameter("port")));
					tempCon.setUser(request.getParameter("user"));
					if(request.getParameter("log_enabled").equals("true")){
						tempCon.setWriteLog(true);
					}else{
						tempCon.setWriteLog(false);
					}
					ch.editConnection(tempCon);
					response.getWriter().write(ch.getJSONConnectionsObject().toJSONString());
					
					
					
				}else if(code.equals("deleteConnection")){
					// Deletes a connection by name
					ch.deleteConnection(request.getParameter("NAME"));
					response.getWriter().write(ch.getJSONConnectionsObject().toJSONString());
					
					
					
				}else if(code.equals("newConnection")){
					// Creates a new connection
					ConnectionData tempCon = new ConnectionData();
					tempCon.setName(request.getParameter("NAME"));
					tempCon.setLoginType(LoginType.SINGLE);
					tempCon.setHostType(HostType.AS400);
					tempCon.setCodePage(37);
					tempCon.setWriteLog(false);
					tempCon.setNegotiationDelay(true);
					ch.writeNewConnection(tempCon);
					response.getWriter().write(ch.getJSONConnectionsObject().toJSONString());
					
					
					
				}else if(code.equals("setDefault")){
					// Sets the default connection to the name of the connection sent by the client
					String defName = request.getParameter("NAME");
					ch.setDefault(defName);
					response.getWriter().write("k");
				}
				break;
			case "agents":
				break;
			case "user":
				manageUserQuery(request, response);
				
				break;
			case "tools":
				break;
			default:
				System.out.println("No settings code.");
				break;
		}
		}catch(Exception e){
			System.out.println("Error in settings code analyzer:" + e.getMessage());
		}
	}
	
	public void manageUserQuery( HttpServletRequest request, HttpServletResponse response ){
		try {
			String code = request.getParameter("USER_CODE");
			UserHandler h = new UserHandler();
			if( code.equals("editUser") ){
				
			}else if( code.equals("deleteUser") ){
					h.deleteUser(request.getParameter("username"), request.getParameter("password") );
			}else if( code.equals("createUser") ){
				User u = new User();
				u.setUser(request.getParameter("username"));
				u.setPassword(request.getParameter("password"));
				
				if(request.getParameter("userType").toString() == "ADMINISTRATOR"){
					u.setUserType(UserType.ADMINISTRATOR);
				}else if(request.getParameter("userType").toString() == "STANDARD_USER"){
					u.setUserType(UserType.STANDARD_USER);
				}else if(request.getParameter("userType").toString() == "GUEST"){
					u.setUserType(UserType.GUEST);
				}
				
				int id = h.createNewUser(u);
				
			}else if( code.equals("getUsers") ){
				response.getWriter().write(h.getJSONUsersObjectNoPass().toJSONString());
			}
		
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	

	
	private String getFreeSessionId() 
	{
		String freeSessionId = "";
        Boolean found = false;
		
        for(int i = 0; i < _lastNewSessionId; ++i) {
			ServerSession session = (ServerSession)_sessions.get(ServerSession.GetSessionId(i));
			if(session != null) {
				if(!session.isUsed()) {
					found = true;
					freeSessionId = session.getSessionId();
					_comm = (Comm5250Logic)_comms.get(freeSessionId);
					_comm.disconnect();
					System.out.println("Disconnected session: " + i);
					break;
				}
			}
		}
		
		if(!found) {
			ServerSession session = new ServerSession();
			_lastNewSessionId++;
			session.setId(_lastNewSessionId);
			freeSessionId = session.getSessionId();
			_sessions.put(freeSessionId, session);
		}
		
		return freeSessionId;
	}
	
	
	public synchronized String checkForExistingSessions(Hashtable mp, String username, String password) {
	    Iterator it = mp.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        
	        String tempKey = pair.getKey().toString();
	        
	        Comm5250Logic tempCom = (Comm5250Logic) pair.getValue();
	        
	        if(tempCom.getUsername().equals(username) && tempCom.getPassword().equals(password)){
	        	return tempKey;
	        	
	        }
	        
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	    return "";
	}
	@SuppressWarnings("unchecked")
	public String logUserTest( String userLog, String pass,HttpServletRequest request) throws UnknownHostException, IOException, InterruptedException, Exception 
	{	

		
		
		String sentUser = userLog;
    	String sentPassword = pass;
    	 
    	String sessionTest = checkForExistingSessions(_comms, sentUser, sentPassword);
    	
    	//if(!sessionTest.equals("")){
    		//_comm = _comms.get(sessionTest);
    		//getScreenResponseRefresh(request, response);
    		//return "";
    	//}
    	
    	if(sentUser != null && sentPassword != null && sentUser != "" && sentPassword != ""){
    		
    	}else{
    		JSONObject o = new JSONObject();
    		o.put("user_error", "Please enter a username/password combination.");
    		
    		return o.toJSONString();
    		
    	}
    	
    	UserHandler uh = new UserHandler();
    	User user = new User();
    	
    	if(uh.checkValidity(sentUser, sentPassword) ){
    		user = uh.getUser(sentUser, sentPassword);
    	}else{
    		JSONObject o = new JSONObject();
    		o.put("user_error", "Invalid username/password combination.");
    		
    		return (o.toJSONString() );
    		
    	}
		
		String freeSessionId = this.getFreeSessionId();
    	
    	if(_comms.get(freeSessionId) == null)
    	{
			_comm = new Comm5250Logic();
			_comms.put(freeSessionId, new Comm5250Logic());
    	}
    	//Agrego la sesion
    	sessionTimeService.addNewSession(freeSessionId);
    	setLastSessionIdCreated(freeSessionId);
    	_comm = (Comm5250Logic)_comms.get(freeSessionId);
    	_sessionId = freeSessionId;
    	((ServerSession)_sessions.get(freeSessionId)).refresh();
    	
			
    	_comm.setUsername(sentUser);
    	_comm.setPassword(sentPassword);
		
    	Preferences prefs = Preferences.userNodeForPackage(ConnectionsHandler.class);
    	
    	
    	ConnectionData connectionData = new ConnectionData();
    	//if(!prefs.get("default_connection", "").equals("")){
    		connectionData.loadDefaultConnection(request);
    		System.out.println("Loading default connection for session: " + _sessionId);
    	//}
  
		_comm.connect(connectionData);
		_comm.getCommBuffer().setSendDataPointer(0);
		_comm.getCommBuffer().getSendData().clear();
		
		_comm.putPacket();	//sends input data / AID key / position
		
		return this.getScreenResponseTest( true);
		

	}
	
	@SuppressWarnings("unchecked")
	private void logUser( HttpServletRequest request, HttpServletResponse response) throws UnknownHostException, IOException, InterruptedException, Exception 
	{	
//		GLOBALS
//		boolean _hasBeenVerified = false;
//		boolean _licenseApproved = false;
//		TODO: Verify License
//		
//		if(!_hasBeenVerified){
//			verifyLicense();
//		}
//		if(!_licenseApproved){
//			return;
//		}
		
		 
		
		
		String sentUser = request.getParameter("username");
    	String sentPassword = request.getParameter("password");
    	 
    	String sessionTest = checkForExistingSessions(_comms, sentUser, sentPassword);
    	
    	if(!sessionTest.equals("")){
    		_comm = _comms.get(sessionTest);
    		getScreenResponseRefresh(request, response);
    		return;
    	}
    	
    	if(sentUser != null && sentPassword != null && sentUser != "" && sentPassword != ""){
    		
    	}else{
    		JSONObject o = new JSONObject();
    		o.put("user_error", "Please enter a username/password combination.");
    		
    		response.getWriter().write(o.toJSONString());
    		
    		return;
    	}
    	
    	UserHandler uh = new UserHandler();
    	User user = new User();
    	
    	if(uh.checkValidity(sentUser, sentPassword) ){
    		user = uh.getUser(sentUser, sentPassword);
    	}else{
    		JSONObject o = new JSONObject();
    		o.put("user_error", "Invalid username/password combination.");
    		
    		response.getWriter().write(o.toJSONString());
    		
    		return;
    	}
		
		String freeSessionId = this.getFreeSessionId();
    	
    	if(_comms.get(freeSessionId) == null)
    	{
			_comm = new Comm5250Logic();
			_comms.put(freeSessionId, new Comm5250Logic());
    	}
    	
    	_comm = (Comm5250Logic)_comms.get(freeSessionId);
    	_sessionId = freeSessionId;
    	((ServerSession)_sessions.get(freeSessionId)).refresh();
    	
			
    	_comm.setUsername(sentUser);
    	_comm.setPassword(sentPassword);
		
    	Preferences prefs = Preferences.userNodeForPackage(ConnectionsHandler.class);
    	
    	
    	ConnectionData connectionData = new ConnectionData();
    	if(!prefs.get("default_connection", "").equals("")){
    		connectionData.loadDefaultConnection(request);
    		System.out.println("Loading default connection for session: " + _sessionId);
    	}
    	
    	
		String finalUser = sentUser;
		String finalPass = sentPassword;
		
		
		
		
		
		
		
		

		_comm.connect(connectionData);
		_comm.getCommBuffer().setSendDataPointer(0);
		_comm.getCommBuffer().getSendData().clear();
		
		_comm.putPacket();	//sends input data / AID key / position
		
		this.getScreenResponse( request,  response, false);	// sends the data back to the client
//		this.getScreenRefresh( request, response );
		this.getScreenResponse( request, response, true );
		
		
//		if(finalUser == "" || finalPass == ""){
//			if(connectionData.getUser() == "" || connectionData.getPass() == ""){
//				System.out.println("No user or password set!");
//				JSONObject json = new JSONObject();
//				json.put("error", "No user or password set!");
////				response.getWriter().write(json.toJSONString());
////				TODO: make an error that tells user that there is no user/pass set
//				return;
//			}
//			finalUser = connectionData.getUser();
//			finalPass = connectionData.getPass();
//			System.out.println("Using default login credentials.");
//		}
//		
//		System.out.println("User: " + finalUser);
//		System.out.println("Pass: " + finalPass);
//		
//		
//
//		JSONObject root = new JSONObject();
//				
//		if(connectionData.getHostType().equals(HostType.i36)) {
//			
//			root.put("sessionId", _sessionId);
//			
//			JSONObject aidkey = new JSONObject();
//				aidkey.put("cursorY", 5);
//				aidkey.put("cursorX", 63);
//				aidkey.put("value", 33554432);
//			root.put("aidKey", aidkey);
//			
//			
//			
//			JSONArray fields = new JSONArray();
//			
////				Domain
//				JSONObject connData = new JSONObject();
//				connData.put("cursorY", 6);
//				connData.put("cursorX", 63);
//				connData.put("length", 15);
//				connData.put("data", connectionData.getSingleDomain());
//				connData.put("data", "");
//				fields.add(connData);
//
////				username
//				JSONObject user = new JSONObject();
//				user.put("cursorY", 5);
//				user.put("cursorX", 63);
//				user.put("length", 16);
//				user.put("data", finalUser);
//				fields.add(user);
//				
////				password
//				JSONObject pass = new JSONObject();
//				pass.put("cursorY", 7);
//				pass.put("cursorX", 63);
//				pass.put("length", 8);
//				pass.put("data", finalPass);
//				fields.add(pass);
//				
////				extra empty field
//				JSONObject eF = new JSONObject();
//				eF.put("cursorY", 9);
//				eF.put("cursorX", 63);
//				eF.put("length", 8);
//				eF.put("data", "");
//				fields.add(eF);
//				
////				extra empty field
//				JSONObject eF2 = new JSONObject();
//				eF2.put("cursorY", 12);
//				eF2.put("cursorX", 13);
//				eF2.put("length", 60);
//				eF2.put("data", "");
//				fields.add(eF2);
//				
////				domain field
//				JSONObject df = new JSONObject();
//				df.put("cursorY", 14);
//				df.put("cursorX", 63);
//				df.put("length", 10);
//				df.put("data", "");
//				fields.add(df);
//			
//			root.put("fields", fields);
//		} else {
//			
//			root.put("sessionId", _sessionId);
//			
//			JSONObject aidkey = new JSONObject();
//			aidkey.put("cursorY", "6");
//			aidkey.put("cursorX", "53");
//			aidkey.put("value", "33554432");
//			root.put("aidKey", aidkey);
//			
//			JSONArray fields = new JSONArray();
//
////				username
//				JSONObject user = new JSONObject();
//				user.put("cursorY", "6");
//				user.put("cursorX", "53");
//				user.put("length", "10");
//				user.put("data", finalUser);
//				fields.add(user);
//				
////				password
//				JSONObject pass = new JSONObject();
//				pass.put("cursorY", "7");
//				pass.put("cursorX", "53");
//				pass.put("length", "10");
//				pass.put("data", finalPass);
//				fields.add(pass);
//			root.put("fields", fields);
//		}
////		
//		sendData(request, response, root.toJSONString());
	}
	public String closeSesion(String sessionId) {
		if (_comms.get(sessionId)!=null){
			_comms.remove(sessionId);
			return "sesion terminada";
		}else{
			return "sesion inexistente";
		}
		
  
    }
	public String getScreenRefreshTest(HttpServletRequest request, HttpServletResponse response,String sessionId) throws java.rmi.RemoteException {
    	try
    	{

//    		TODO: fix this ?
			//String sessionId = request.getParameter("sessionId");
			_sessionId = sessionId;
			/* Valid Expired Time Session */
			boolean expiredTimeSession = sessionTimeService.validExpiredTimeSession(sessionId);
			if (expiredTimeSession){
			_comm = (Comm5250Logic)_comms.get(sessionId);
			if (_comm==null){
				JSONObject o = new JSONObject();
				o.put("No existe sesion", "-1");
				o.put("sessionId", _sessionId );
				return o.toJSONString();
			}
			//if the screen is not dirty then return just the session ID because there are no updates
			/*if (!_comm.isDataToReadyProcess())
			{
				System.out.println("No refresh needed.");
				JSONObject o = new JSONObject();
				o.put("refresh", "0");
				o.put("sessionId", _sessionId );
				//response.getWriter().write( o.toJSONString() );
				return o.toJSONString();
			}*/
			System.out.println("Doing page refresh");
			return this.getScreenResponseTest( true );
			}else{
				return "sesion invalida";
			}
    	}
    	catch(IOException e1)
    	{
    		throw new java.rmi.RemoteException();
    	}
    	catch(InterruptedException i2)
    	{
    		throw new java.rmi.RemoteException();
    	}
    	catch(Exception e)
    	{
    		throw new java.rmi.RemoteException();
    	}
    }

	
	@SuppressWarnings("unchecked")
	public String getScreenResponseTest(boolean writeOutput) throws IOException, InterruptedException, Exception
	{
		JSONObject json = new JSONObject();
		json.put("sessionId", _sessionId);
		
		// Get screen fields from Comm Module
		if(_comm == null){ 
			System.out.println("_comm was null");
			return "";
			
		}else{
			

		
		CommBufferLogic commBuffer = _comm.getCommBuffer();
		
		
		Iterator<ScreenField> screenFieldsIterator = commBuffer.getCommScreen().getScreenFields().iterator();
		List<CustomizedField> screenFields = CustomizationManager.Instance().ConvertFields(screenFieldsIterator, commBuffer);
		_i36SessionID = CustomizationManager.Instance().getI36SessionID();

			// If there is no popup, refresh whole screen.
		json.put("action", "refresh-screen");
//			json.put("popupNumber", "0");
			

		
		Thread.sleep(100);
//		Sort the fields
		CustomizationUtils.SortCustomizedFields(screenFields);

		
		//	Turn the fields into JSON here
		DataParser dp = new DataParser();
		JSONArray ja = new JSONArray();
		System.out.println("Screenfields: " + ja.size());
		
		ArrayList<JSONObject> tempFields = new ArrayList<JSONObject>();
		for (int i = 0; i < screenFields.size(); i++){
			CustomizedField sf = screenFields.get(i);
			JSONObject jo = new JSONObject();
			
			jo.put("type", dp.parse(sf.getGreenScreenField().getType().toString()) );
			jo.put("attributes", dp.parse( sf.getGreenScreenField().getAttributes()) );
			jo.put("length", dp.parse(sf.getGreenScreenField().getLength()) );
			jo.put("x", dp.parse(sf.getX()) );
			jo.put("y", dp.parse(sf.getY()) );
			jo.put("iX", sf.getGreenScreenField().getCol());
			jo.put("iY", sf.getGreenScreenField().getRow());
			
			jo.put("text", dp.parse(sf.getText()) );
			jo.put("fieldId", dp.parse(sf.hashCode()) );
			jo.put("controlType", sf.getControlType().toString());
			
			jo.put("className", dp.parse(sf.getClassName()) );
			jo.put("hideField", sf.getHideField() );
			
//			System.out.println("Field Data: " + sf.getGreenScreenField().getText());
			tempFields.add(jo);
			ja.add(jo);
		}
		json.put("fields", ja);
		
		json.put("action", "refresh-screen");
		json.put("i36Session", dp.parse(_i36SessionID) );
		
		
		
		
		//TODO: put screen id recognition here
		
		AgentID agentID = new AgentID();
		int pageID = agentID.getPageID(tempFields);
		
		
		json.put("pageID", pageID);
		
		
		if(writeOutput){
			System.out.println(json.toJSONString());
			return (json.toJSONString());
			
		}else{
			System.out.println("Not writing output!");
		}
		
		}
		return "FIN";
	}
	@SuppressWarnings("unchecked")
	private void getScreenResponse(HttpServletRequest request, HttpServletResponse response, boolean writeOutput) throws IOException, InterruptedException, Exception
	{
		JSONObject json = new JSONObject();
		json.put("sessionId", _sessionId);
		
		// Get screen fields from Comm Module
		if(_comm == null){ 
			System.out.println("_comm was null");
			return;
			
		}else{
			

		
		CommBufferLogic commBuffer = _comm.getCommBuffer();
		
		
		Iterator<ScreenField> screenFieldsIterator = commBuffer.getCommScreen().getScreenFields().iterator();
		List<CustomizedField> screenFields = CustomizationManager.Instance().ConvertFields(screenFieldsIterator, commBuffer);
		_i36SessionID = CustomizationManager.Instance().getI36SessionID();

			// If there is no popup, refresh whole screen.
		json.put("action", "refresh-screen");
//			json.put("popupNumber", "0");
			

		
		Thread.sleep(100);
//		Sort the fields
		CustomizationUtils.SortCustomizedFields(screenFields);
		
		
		
		
		
		
		
		
		//	Turn the fields into JSON here
		DataParser dp = new DataParser();
		JSONArray ja = new JSONArray();
		System.out.println("Screenfields: " + ja.size());
		
		ArrayList<JSONObject> tempFields = new ArrayList<JSONObject>();
		for (int i = 0; i < screenFields.size(); i++){
			CustomizedField sf = screenFields.get(i);
			JSONObject jo = new JSONObject();
			
			jo.put("type", dp.parse(sf.getGreenScreenField().getType().toString()) );
			jo.put("attributes", dp.parse( sf.getGreenScreenField().getAttributes()) );
			jo.put("length", dp.parse(sf.getGreenScreenField().getLength()) );
			jo.put("x", dp.parse(sf.getX()) );
			jo.put("y", dp.parse(sf.getY()) );
			jo.put("iX", sf.getGreenScreenField().getCol());
			jo.put("iY", sf.getGreenScreenField().getRow());
			
			jo.put("text", dp.parse(sf.getText()) );
			jo.put("fieldId", dp.parse(sf.hashCode()) );
			jo.put("controlType", sf.getControlType().toString());
			
			jo.put("className", dp.parse(sf.getClassName()) );
			jo.put("hideField", sf.getHideField() );
			
//			System.out.println("Field Data: " + sf.getGreenScreenField().getText());
			tempFields.add(jo);
			ja.add(jo);
		}
		json.put("fields", ja);
		
		json.put("action", "refresh-screen");
		json.put("i36Session", dp.parse(_i36SessionID) );
		
		
		
		
		//TODO: put screen id recognition here
		
		AgentID agentID = new AgentID();
		int pageID = agentID.getPageID(tempFields);
		
		
		json.put("pageID", pageID);
		
		
		if(writeOutput){
			response.getWriter().write(json.toJSONString());
			System.out.println(json.toJSONString());
		}else{
			System.out.println("Not writing output!");
		}
		
		}
	}
	
	
	public String sendDataTest(String oJSON) {
		System.out.println("sendData() called " + oJSON);
		

		
		JSONObject data = null;
		oJSON = oJSON.replace("'", "\"");
		
		
		if(oJSON != ""){
			
			data = JSONUtils.stringToJSON(oJSON);
		}else{
			//data = JSONUtils.stringToJSON(request.getParameter("wrapper"));
		}
		
	
		
		// Get the SessionId and objects associated
		String sessionId = data.get("sessionId").toString();
		boolean expiredTimeSession = sessionTimeService.validExpiredTimeSession(sessionId);
		if (expiredTimeSession){
		
		
		
		_sessionId = sessionId;
		_comm = (Comm5250Logic)_comms.get(sessionId);
		((ServerSession)_sessions.get(sessionId)).refresh(); //resets the "last visited" time
		
		
		// Get the first element in the 'aidKey' array (only one is currently being sent)
		JSONObject keyStroke = (JSONObject) data.get("aidKey");
		System.out.println("Keystroke: " + keyStroke);
		
		
		// ------------------------
		// Send field's information
		// ------------------------
		
		JSONArray fields = (JSONArray) data.get("fields");
		System.out.println( "Fields sent to server: " + fields.size() );
		
		for (int i = 0; i < fields.size(); i++)
		{
			JSONObject field = (JSONObject) fields.get(i);
			
			ScreenField screenField = new ScreenField();
			
			screenField.setRow(Integer.parseInt(field.get("cursorY").toString()) );
			screenField.setCol(Integer.parseInt(field.get("cursorX").toString()) );
			screenField.setLength(Integer.parseInt(field.get("length").toString()) );
			
			String formattedString = String.format(("%-" + screenField.getLength() + "s"), field.get("data").toString());
			screenField.setText(formattedString);
			
			// If there is any Data in the screenField, it is sent to the Communication module. (And added to the send buffer)
			if(!screenField.getText().trim().equals("")) {
				System.out.println("Sending text field\n\tRow:\t" + screenField.getRow());
				System.out.println("\tCol:\t" + screenField.getCol());
				System.out.println("\tLength:\t" + screenField.getLength());
				System.out.println("\tTxt:\t" + screenField.getText());
				System.out.println();
				_comm.getKeyInData(screenField);
			}
		}
		
		try 
		{
			// -----------------
			// Execute keystroke
			// -----------------
			int screenBufferPosition = (Integer.parseInt(keyStroke.get("cursorY").toString()) - 1) * _comm.getCommBuffer().getCommScreen().getScreenWidth() + (Integer.parseInt(keyStroke.get("cursorX").toString()) - 1); 
			int aidCode = Integer.parseInt(keyStroke.get("value").toString());
			System.out.println("Row: " + (Integer.parseInt(keyStroke.get("cursorY").toString()) - 1));
			System.out.println("Col: " + (Integer.parseInt(keyStroke.get("cursorX").toString()) - 1) );
			System.out.println("Aid Key(code): " + aidCode);
			System.out.println("BufferPos: " + screenBufferPosition);
			
			//The Keystroke is sent to the Communication Module
			_comm.getAidKey(aidCode, screenBufferPosition);
		} 
		catch (IOException e) {
			e.printStackTrace();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		boolean done = false;
		List<ScreenField> tempFields = new ArrayList<ScreenField>();
		
		while(!done) 
		{
			if(killerCount > 300){
				done = true;
			}
			// ---------
			// Send Data
			// ---------
			try 
			{
				System.out.println("Sending data!");
				_comm.putPacket();
				System.out.println("Data sent!");
				
				boolean killer = _comm.isClosed();
				if(killer){
					done = true;
				}
				
			} 
			catch (IOException e) {
				e.printStackTrace();
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			killerCount++;
			
			
			// ----------------------------------------------------
			// Get green screen fields generated after sending data
			// ----------------------------------------------------
			System.out.println("Getting screen fields.");
			Iterator<ScreenField> screenFieldsIterator = _comm.getCommBuffer().getCommScreen().getScreenFields().iterator();
			
			while(screenFieldsIterator.hasNext()) {
				
				ScreenField screenFieldCurr = screenFieldsIterator.next();
				tempFields.add(screenFieldCurr);
				
				if (screenFieldCurr.getType().equals(ScreenElementType.UnlockKeyboard)) 
				{
					_cursorRow = String.valueOf(screenFieldCurr.getRow());
					_cursorCol = String.valueOf(screenFieldCurr.getCol());
					done = true;
					
					// Copy fields in commBuffer
					_comm.getCommBuffer().getCommScreen().getScreenFields().clear();
					screenFieldsIterator = tempFields.iterator();
				
					while(screenFieldsIterator.hasNext()) 
					{
						screenFieldCurr = screenFieldsIterator.next();
						_comm.getCommBuffer().getCommScreen().getScreenFields().add(screenFieldCurr);
					}
					
					break;
				}
			}
		}
		
		killerCount = 0;

  
	}else{
		return "sesion invalida";
	}
		return "sesion valida";
	}
	
	
	
	public void sendData(HttpServletRequest request, HttpServletResponse response, String oJSON) {
		System.out.println("sendData() called");
		
		//The code to parse the parameter 'in' has been replaced with a JSON-based code
		//The previous code snippets have been comented under the 'Legacy Code' Tag
		//Once the JSON code has been validated, Legacy Code tags should be deleted.
		//Date: 11/20/2013 - note: not all the original 'legacy code' tags are now present
		
//		String inClean = in.replaceAll("_ampersandampersand_", "&")	;
//		inClean = inClean.replaceAll("_lessless_", "<")	;
//		inClean = inClean.replaceAll("_moremore_", ">")	;
		
		
		JSONObject data = null;
		
		if(oJSON != ""){
			data = JSONUtils.stringToJSON(oJSON);
		}else{
			data = JSONUtils.stringToJSON(
					request.getParameter("wrapper"));
		}
		
		
		
		// Get the SessionId and objects associated
		String sessionId = data.get("sessionId").toString();
		
		_sessionId = sessionId;
		_comm = (Comm5250Logic)_comms.get(sessionId);
		((ServerSession)_sessions.get(sessionId)).refresh(); //resets the "last visited" time
		
		
		// Get the first element in the 'aidKey' array (only one is currently being sent)
		JSONObject keyStroke = (JSONObject) data.get("aidKey");
		System.out.println("Keystroke: " + keyStroke);
		
		
		// ------------------------
		// Send field's information
		// ------------------------
		
		JSONArray fields = (JSONArray) data.get("fields");
		System.out.println( "Fields sent to server: " + fields.size() );
		
		for (int i = 0; i < fields.size(); i++)
		{
			JSONObject field = (JSONObject) fields.get(i);
			
			ScreenField screenField = new ScreenField();
			
			screenField.setRow(Integer.parseInt(field.get("cursorY").toString()) );
			screenField.setCol(Integer.parseInt(field.get("cursorX").toString()) );
			screenField.setLength(Integer.parseInt(field.get("length").toString()) );
			
			String formattedString = String.format(("%-" + screenField.getLength() + "s"), field.get("data").toString());
			screenField.setText(formattedString);
			
			// If there is any Data in the screenField, it is sent to the Communication module. (And added to the send buffer)
			if(!screenField.getText().trim().equals("")) {
				System.out.println("Sending text field\n\tRow:\t" + screenField.getRow());
				System.out.println("\tCol:\t" + screenField.getCol());
				System.out.println("\tLength:\t" + screenField.getLength());
				System.out.println("\tTxt:\t" + screenField.getText());
				System.out.println();
				_comm.getKeyInData(screenField);
			}
		}
		
		try 
		{
			// -----------------
			// Execute keystroke
			// -----------------
			int screenBufferPosition = (Integer.parseInt(keyStroke.get("cursorY").toString()) - 1) * _comm.getCommBuffer().getCommScreen().getScreenWidth() + (Integer.parseInt(keyStroke.get("cursorX").toString()) - 1); 
			int aidCode = Integer.parseInt(keyStroke.get("value").toString());
			System.out.println("Row: " + (Integer.parseInt(keyStroke.get("cursorY").toString()) - 1));
			System.out.println("Col: " + (Integer.parseInt(keyStroke.get("cursorX").toString()) - 1) );
			System.out.println("Aid Key(code): " + aidCode);
			System.out.println("BufferPos: " + screenBufferPosition);
			
			//The Keystroke is sent to the Communication Module
			_comm.getAidKey(aidCode, screenBufferPosition);
		} 
		catch (IOException e) {
			e.printStackTrace();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		boolean done = false;
		List<ScreenField> tempFields = new ArrayList<ScreenField>();
		
		while(!done) 
		{
			if(killerCount > 300){
				done = true;
			}
			// ---------
			// Send Data
			// ---------
			try 
			{
				System.out.println("Sending data!");
				_comm.putPacket();
				System.out.println("Data sent!");
				
				boolean killer = _comm.isClosed();
				if(killer){
					done = true;
				}
				
			} 
			catch (IOException e) {
				e.printStackTrace();
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			killerCount++;
			
			
			// ----------------------------------------------------
			// Get green screen fields generated after sending data
			// ----------------------------------------------------
			System.out.println("Getting screen fields.");
			Iterator<ScreenField> screenFieldsIterator = _comm.getCommBuffer().getCommScreen().getScreenFields().iterator();
			
			while(screenFieldsIterator.hasNext()) {
				
				ScreenField screenFieldCurr = screenFieldsIterator.next();
				tempFields.add(screenFieldCurr);
				
				if (screenFieldCurr.getType().equals(ScreenElementType.UnlockKeyboard)) 
				{
					_cursorRow = String.valueOf(screenFieldCurr.getRow());
					_cursorCol = String.valueOf(screenFieldCurr.getCol());
					done = true;
					
					// Copy fields in commBuffer
					_comm.getCommBuffer().getCommScreen().getScreenFields().clear();
					screenFieldsIterator = tempFields.iterator();
				
					while(screenFieldsIterator.hasNext()) 
					{
						screenFieldCurr = screenFieldsIterator.next();
						_comm.getCommBuffer().getCommScreen().getScreenFields().add(screenFieldCurr);
					}
					
					break;
				}
			}
		}
		
		killerCount = 0;
		
//		String response = "ok" + DataLowLevelUtils.PadLeftZeros(_cursorRow, 2) + DataLowLevelUtils.PadLeftZeros(_cursorCol, 3);
//		try {
//			response.getWriter().write( "ok" + DataLowLevelUtils.PadLeftZeros(_cursorRow, 2) + DataLowLevelUtils.PadLeftZeros(_cursorCol, 3) );
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    }
	
	public void getScreenRefresh(HttpServletRequest request, HttpServletResponse response) throws java.rmi.RemoteException {
    	try
    	{
//    		TODO: fix this ?
			String sessionId = request.getParameter("sessionId");
			_sessionId = sessionId;
			_comm = (Comm5250Logic)_comms.get(sessionId);
			
			//if the screen is not dirty then return just the session ID because there are no updates
			if (!_comm.isDataToReadyProcess())
			{
				System.out.println("No refresh needed.");
				JSONObject o = new JSONObject();
				o.put("refresh", "0");
				o.put("sessionId", _sessionId );
				response.getWriter().write( o.toJSONString() );
				return;
			}
			System.out.println("Doing page refresh");
			this.getScreenResponseRefresh( request, response );
    	}
    	catch(IOException e1)
    	{
    		throw new java.rmi.RemoteException();
    	}
    	catch(InterruptedException i2)
    	{
    		throw new java.rmi.RemoteException();
    	}
    	catch(Exception e)
    	{
    		throw new java.rmi.RemoteException();
    	}
    }
	
	public void getScreenRefreshTest(HttpServletRequest request, HttpServletResponse response) throws java.rmi.RemoteException {
    	try
    	{
//    		TODO: fix this ?
			String sessionId = request.getParameter("sessionId");
			_sessionId = sessionId;
			_comm = (Comm5250Logic)_comms.get(sessionId);
			
			//if the screen is not dirty then return just the session ID because there are no updates
			if (!_comm.isDataToReadyProcess())
			{
				System.out.println("No refresh needed.");
				JSONObject o = new JSONObject();
				o.put("refresh", "0");
				o.put("sessionId", _sessionId );
				response.getWriter().write( o.toJSONString() );
				return;
			}
			System.out.println("Doing page refresh");
			this.getScreenResponseRefresh( request, response );
    	}
    	catch(IOException e1)
    	{
    		throw new java.rmi.RemoteException();
    	}
    	catch(InterruptedException i2)
    	{
    		throw new java.rmi.RemoteException();
    	}
    	catch(Exception e)
    	{
    		throw new java.rmi.RemoteException();
    	}
    }
	private void getScreenResponseRefresh(HttpServletRequest request, HttpServletResponse response) throws IOException, InterruptedException, Exception
    {
        // Get screen fields from Comm Module
        if(_comm == null) {  }else{
        	_comm.getPacket();
            
            getScreenResponse(request, response, true);
        }
        
    }

	public String getLastSessionIdCreated() {
		return lastSessionIdCreated;
	}

	public void setLastSessionIdCreated(String lastSessionIdCreated) {
		this.lastSessionIdCreated = lastSessionIdCreated;
	}
	
//	private void connectFromFile(ConnectionData connectionData) {
//			
//			try {
//				HostConnection connection = new HostConnection();
//				connectionData.load(connection);
//			}
//			catch(Exception e){ }
//		}
}
