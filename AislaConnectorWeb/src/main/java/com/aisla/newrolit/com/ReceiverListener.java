package com.aisla.newrolit.com;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.aisla.newrolit.connections.ConnectionData;
import com.aisla.newrolit.global.HostType;

/**
 * ReceiverListener implements Runnable and  
 * holds the listener that feeds the data 
 * stream queue
 * It runs in a separate thread 
 * <p>
 * Main objects are the following
 * <p>
 * @author      Luis Paradela
 * @version     1.0
 * @since       1.0
 */
public class ReceiverListener implements Runnable {
	
	private ReceiveDataQ _receiveDataQ = null;
	private CommBufferLogic _commBufferLogic = null;
	private ConnectionData _connectionData = null;
	boolean endFlag = true;
		
	/**
	 * Constructor that receives all the objects in the initialization
	 * and launch the separate thread 
	 * <p>
	 * This method always returns immediately. 
	 *
	 */    
	public ReceiverListener(ReceiveDataQ receiveDataQ, CommBufferLogic commBufferLogic, ConnectionData connectionData) {
		this._receiveDataQ = receiveDataQ;
		this._commBufferLogic = commBufferLogic;
		this._connectionData = connectionData;
			new Thread(this, "ReceiverListener").start();
	}
	
	/**
	 * run method required by the Runnable interface
	 * <p>
	 * This method always returns immediately. 
	 *
	 */    
	public void run(){
		 while(endFlag) {
			 try {
				receive();
			} catch (IOException e) {
//				SimpleLogger.writeLine("receiver Listener run", e.getMessage().toString() + " 12345 ", _connectionData.isWriteLog());
			}
		 }
	 }
	 
	/**
	 * receiver logic that loads the synchronized queue
	 * <p>
	 * This method always returns immediately. 
	 *
	 */    
	private void receive() throws IOException {
		if (_commBufferLogic.is_negotiating() && _connectionData.isNegotiationDelay()) {
			//Add extra delay of iSeries initial negotiation 
			try {
				Thread.sleep(2000);
			} catch (InterruptedException exc) {}
		}
		if(_connectionData.getInputStream() == null) {
			_connectionData.setInputStream(new BufferedInputStream (_connectionData.getSocket().getInputStream(), 4096) );
		}
		byte [] buffer = new byte[8192];
		Integer [] intBuffer = new Integer[8192];
		boolean done = false;
		int receivedCount = 0;
		int retryMaxCount = 100;
		int retryCount = 0;
		int receiveCount = 0;
		
		
//		Loop for receiving the data from the host. i36 connections require more iterations because the sent packets are in smaller chunks
		do {
			if(retryCount < retryMaxCount) {
				
				// Gets the input stream
				receivedCount = _connectionData.getInputStream().read(buffer, receiveCount, buffer.length - receiveCount);
//				System.out.println("Received " + receiveCount + " bytes");
				
				
				if(receivedCount > 0) {
					// adds the new received integers to the buffer
					for(int i = receiveCount; i < receivedCount + receiveCount; ++i) {
						intBuffer[i] = (int)buffer[i] & 0xFF;
						System.out.print(intBuffer[i] + ", ");
					}
					receiveCount += receivedCount; // incrementing the amount of bytes received to the new correct value (after getting more ints in the stream)
					
				}
			}
			else{
				done = true;
                //Only for i36 read two more times
                if (_connectionData.getHostType()== HostType.i36) {
                    if (!_commBufferLogic.is_negotiating()) {
                    	//GIGIO
                        if (receiveCount > 2 && intBuffer[receiveCount - 3] != 0x0 && 
                        		intBuffer[receiveCount - 3] != 0x52) {
                        	done = false;
                        }
                    }
                }
				
			}
			
			retryCount++;
			
			if(receivedCount > 2) {
				if (_commBufferLogic.isDoDoEndrecord() && _commBufferLogic.isDoBinary() &&
	                	((intBuffer[receiveCount - 2] - TelnetCommandValue.TNC_IAC) != 0) &&
	                    ((intBuffer[receiveCount - 1] - TelnetCommandValue.TNC_EF) != 0)
	                    ) {
                        try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
                        
                } else {
                	done = true;
                	
                	//I36 additional reads
                	if(_connectionData.getHostType().equals(HostType.i36)) {
                		
                	}
                }
			}
			

			try {
				if(!done) { Thread.sleep(500); }
			} catch (InterruptedException eSleep) {
			}
		}while(!done);
		
		System.out.println("Finished recieving packets.");
		try {
			Thread.sleep(50);
		} catch (InterruptedException eSleep) {
		}
//		if ( receivedCount < 0 && retryCount > retryMaxCount){
//			endFlag = false;
//		}
		List<Integer> auxBuffer = new ArrayList<Integer>(); 

		for(int i=0; i < receiveCount; ++i) {
			auxBuffer.add(Integer.valueOf(intBuffer[i]));
		}

		this._receiveDataQ.put(auxBuffer);
		this._receiveDataQ.notifyCaller();
	}
 }
