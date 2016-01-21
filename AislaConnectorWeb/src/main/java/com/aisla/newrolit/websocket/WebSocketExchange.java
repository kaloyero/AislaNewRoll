package com.aisla.newrolit.websocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.server.ServerEndpoint;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.json.simple.JSONObject;

@ServerEndpoint( value = "/WebSocketExchange")
public class WebSocketExchange {
	
	static Set<Session> _users = Collections.synchronizedSet(new HashSet<Session>());
	
	@OnOpen
	public void handleOpen(Session userSession){
		_users.add(userSession);
	}
	
	@OnMessage
	public void handleMessage(String message, Session userSession){
		try{
			
			if(userSession.getUserProperties().get("username") == null){
				userSession.getUserProperties().put("username", message);
				System.out.println("Adding new user: " + message);
//				return newUserJSONResponse(message);
				userSession.getBasicRemote().sendText(newUserJSONResponse(message));
			}
			System.out.println("Message: " + message);
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
//		return newUserJSONResponse("error1234");
		try {
			userSession.getBasicRemote().sendText(newUserJSONResponse("error1234"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@OnClose
	public void handleClose(Session userSession){
		_users.remove(userSession);
	}
	
	public String newUserJSONResponse(String username){
		JSONObject j = new JSONObject();
		j.put("ICmessage", "You are now connected as: " + username);
		
		return j.toJSONString();
	}
}
