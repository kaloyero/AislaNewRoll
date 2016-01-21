package com.aisla.newrolit.com;

import com.aisla.newrolit.global.DateUtils;

import java.util.Date;
import java.util.Calendar;

public class ServerSession {
	private int _id = 0;
	private String _name = "";
	private Date _lastVisit;

	public int getId() {
		return _id;
	}
	public void setId(int _id) {
		this._id = _id;
	}
	public String getName() {
		return _name;
	}
	public void setName(String _name) {
		this._name = _name;
	}
	public Date getLastVisit() {
		return _lastVisit;
	}
	public void setLastVisit(Date _lastVisit) {
		this._lastVisit = _lastVisit;
	}
	
	public ServerSession() {		
		Calendar cal = Calendar.getInstance();
		_lastVisit = cal.getTime();
	}
	
	public  String getSessionId() {
		String sessionPrefix = "session";		
		String sessionNumber = "00000" + String.valueOf(_id);		
		sessionNumber = sessionNumber.substring(String.valueOf(_id).length(), sessionNumber.length());		
		return sessionPrefix + sessionNumber;
	}

	public static String GetSessionId(int id) {
		String sessionPrefix = "session";		
		String sessionNumber = "00000" + String.valueOf(id);		
		sessionNumber = sessionNumber.substring(String.valueOf(id).length(), sessionNumber.length() - 1);		
		return sessionPrefix + sessionNumber;
	}
	
	public void refresh() {
		Calendar cal = Calendar.getInstance();
		_lastVisit = cal.getTime();
	}

	public Boolean isUsed() {
		Calendar cal = Calendar.getInstance();
		Calendar initialTime = Calendar.getInstance();
		initialTime.setTime(_lastVisit);
		Calendar currentTime = Calendar.getInstance();
		currentTime.setTime(cal.getTime());
		long initialTimeMS = initialTime.getTimeInMillis();
		long currentTimeMS = currentTime.getTimeInMillis();
		long diffMinutes = (currentTimeMS - initialTimeMS) / (60 * 1000);
		
		if(diffMinutes > 1) {
			return false;
		}
		
		return true;
	}
}
