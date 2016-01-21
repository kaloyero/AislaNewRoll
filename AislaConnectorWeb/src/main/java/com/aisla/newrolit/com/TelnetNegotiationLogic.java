package com.aisla.newrolit.com;

import java.io.IOException;

import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;

import com.aisla.newrolit.connections.ConnectionData;


public class TelnetNegotiationLogic {
	public TelnetNegotiationLogic() {}
	
	public void negotiate(CommBufferLogic commBuffer, ConnectionData connectionData) throws IOException, InterruptedException {
		commBuffer.reset();
		commBuffer.setDoDataStream(false);
    	System.out.println("Negociando");

		while ( ( commBuffer.getReceiveDataPointer() < commBuffer.getReceiveCount() || commBuffer.getReceiveCount() == 0 ) && 
				!commBuffer.isHaveDataStream()) {
			if(commBuffer.getReceiveCount() == 0) {
				
				commBuffer.receive(connectionData);
				if(commBuffer.getReceiveCount() > 0) {
					commBuffer.setHaveSOH(false);
					commBuffer.getAuxBuffer().clear();
					commBuffer.setSendDataPointer(0);
					commBuffer.setReceiveDataPointer(0);
				} 
			}
			else if(commBuffer.getReceiveData().size() == 0) {
				break;
			}
			
			//Data received from the host
			while((commBuffer.getReceiveDataPointer() - commBuffer.getReceiveCount()) < 0 &&
				  !commBuffer.isHaveDataStream()) {
            	System.out.println("Entrando Data From Host");

				commBuffer.evalHaveDataStream();
				if(!commBuffer.isHaveDataStream()) {
					Comm5250Parser.ParseSocketData(commBuffer);
				}else {
					commBuffer.setSendDataPointer(0);
				}
			}
			
            if (!commBuffer.isHaveDataStream()) {
                commBuffer.getReceiveData().clear();
                commBuffer.setReceiveDataPointer(0);
            }

            if (commBuffer.getSendDataPointer() > 0) {
            	System.out.println("Entrando");
                if (commBuffer.isDoDoEndrecord() && commBuffer.isDoDataStream()) {
                    commBuffer.getSendData().add(TelnetCommandValue.TNC_IAC);
                    commBuffer.incSendDataPointer();
                    commBuffer.getSendData().add(TelnetCommandValue.TNC_EF);
                    commBuffer.incSendDataPointer();
                }

                commBuffer.send(connectionData);

                if (commBuffer.getSendData().size() > 0) {
                    commBuffer.setSendDataPointer(0);
                	commBuffer.getSendData().clear();
                }
                else if (commBuffer.getSendData().size() < 0) {
                    break;
                }
                else if (commBuffer.isDoDoEndrecord() && commBuffer.isDoDataStream()) {
                    commBuffer.setSendDataPointer(commBuffer.getSendDataPointer() - 2);
                }
           }
        }
	}
}
