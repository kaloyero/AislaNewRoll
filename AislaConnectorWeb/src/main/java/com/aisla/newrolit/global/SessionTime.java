package com.aisla.newrolit.global;

import java.util.Date;
import java.util.HashMap;

public class SessionTime {
    private static SessionTime sessionTime = null;
    private HashMap<String,Date> times;
    private int defaultExpiredSessionTime;

    /**
     * Constructor
     */
    private SessionTime() {
    	times = new HashMap<String,Date>();
    	defaultExpiredSessionTime = 10;
    }

    
    /**
     * devuelve la instancia de sessionTime
     * 
     * @return period
     */
    private static SessionTime getSessionTime() {
        if(sessionTime == null) {
        	sessionTime = new SessionTime();
        }
        return sessionTime;
    }

    public static void addOrUpdateSession(String sessionId , Date date){
    	//Add the session and time in the times hashMap 
    	getSessionTime().addTime(sessionId, date);
    }

    public void addTime(String sessionId , Date date){
    	times.put(sessionId, date);
    }

    public static HashMap<String,Date> getTimesSessionList(){
    	return getSessionTime().getTimes();
    }

    public HashMap<String,Date> getTimes(){
    	return times;
    }

    public static void removeSession(String sessionId ){
    	getSessionTime().removeTime(sessionId);
    }

    public void removeTime(String sessionId){
    	times.remove(sessionId);
    }

	public static int getExpiredSessionTime() {
		return getSessionTime().getDefaultExpiredSessionTime();
	}
    
	public int getDefaultExpiredSessionTime() {
		return defaultExpiredSessionTime;
	}

	public static Date getDateBySession(String sessionId) {
		return getSessionTime().getTimeByKey(sessionId);
	}
    
	public Date getTimeByKey(String sessionId) {
		return times.get(sessionId);
	}

}

