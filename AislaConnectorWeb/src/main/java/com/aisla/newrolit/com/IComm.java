package com.aisla.newrolit.com;

import java.io.IOException;
import java.net.UnknownHostException;
//import global.*;


import com.aisla.newrolit.connections.ConnectionData;

public interface IComm {
	CommBufferLogic getCommBuffer();
	void connect(ConnectionData connectionData) throws UnknownHostException, IOException;
	void disconnect();
	void putPacket() throws IOException, InterruptedException;
    void getKeyInData(ScreenField screenField);
    void getAidKey(Integer aidKey, Integer screenPosn) throws IOException;
    boolean isDataToReadyProcess() throws IOException, InterruptedException;
    void getPacket() throws IOException, InterruptedException;
    boolean isClosed();
	void setUsername(String sentUser);
	void setPassword(String sentPass);
	String getUsername();
	String getPassword();
}
