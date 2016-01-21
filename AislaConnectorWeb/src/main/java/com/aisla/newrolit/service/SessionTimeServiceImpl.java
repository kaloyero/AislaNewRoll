package com.aisla.newrolit.service;

import java.util.Date;

import com.aisla.newrolit.global.SessionTime;

public class SessionTimeServiceImpl implements SessionTimeService {

	@Override
	public void addNewSession(String sessionId) {
		// Get the realtime Date and time that the session has been created
		Date date = new Date();
		// Store the time session
		SessionTime.addOrUpdateSession(sessionId, date);

	}

	@Override
	public boolean validExpiredTimeSession(String sessionId) {
		boolean validSession = false;

		// Get the time of the session id
		Date initDate = SessionTime.getDateBySession(sessionId);
		// Get the realtime of the action
		Date endDate = new Date();

		if (getDiferenceBetweenDatesInMinutes(initDate,endDate) < SessionTime.getExpiredSessionTime()) {
			validSession = true;
			updateSessionTime(sessionId, endDate);
		} else {
			removeSessionTime(sessionId);
		}

		return validSession;
	}

	@Override
	public void removeSessionTime(String sessionId) {
		//Remove the session
		SessionTime.removeSession(sessionId);
	}

	/**
	 * 
	 */
	protected void updateSessionTime(String sessionId, Date date) {
		SessionTime.addOrUpdateSession(sessionId, date);
	}

	private double getDiferenceBetweenDatesInMinutes(Date initDate, Date endDate) {
		//By default return a number mayor than the expired session 
		long totalMinutos = SessionTime.getExpiredSessionTime()+1;
		
		if (initDate != null){
			//Calculate the session time difference
			totalMinutos = modDates(initDate, endDate);
		}
		
		return totalMinutos;
	}

	private long modDates(Date initDate, Date endDate) {
		long totalMinutos = ((endDate.getTime() - initDate.getTime()) / 1000 / 60);
		return totalMinutos;
	}
	
}
