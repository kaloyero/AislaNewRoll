package com.aisla.newrolit.service;

public interface SessionTimeService {

	
	/**
	 * Store the new Session time
	 * 
	 * @param sessionId
	 */
	void addNewSession(String sessionId);
	
	/**
	 * Validate if the session time has expired
	 * 
	 * @param sessionId
	 * @return
	 */
	boolean validExpiredTimeSession(String sessionId);
	
	/**
	 * Remove the session time 
	 * 
	 * @param sessionId
	 */
	void removeSessionTime(String sessionId);

	
}
