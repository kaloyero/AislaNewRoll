package com.aisla.newrolit.com;


import com.aisla.newrolit.global.TN5250CommandValue;

import java.io.IOException;
import java.net.*;

import com.aisla.newrolit.connections.ConnectionData;

/**
 * Comm5250Logic implements IComm interface and expose the main
 * methods to interact with a host.
 * <p>
 * Interaction with the host starts with the connect method and ends
 * with a call to disconnect; if this operation is not done a
 * session will be remain open in the host.
 *
 * @author      Luis Paradela
 * @version     1.0
 * @since       1.0
 */
public class Comm5250LogicOLD implements IComm {

	private ConnectionData _connectionData = null;
	private CommBufferLogic commBuffer = null;
	

	/**
	 * Returns the CommBufferLogic object.
	 * <p>
	 * This method always returns immediately, and the object may be null,
	 * hence null check must be performed before any operation.
	 * @return      the CommBufferLogic object instance
	 * @see         CommBufferLogic
	 */
	public CommBufferLogic getCommBuffer() {
		return commBuffer;
	}
	
//	public boolean isClosed(){
//		return _connectionData.isKiller();
//	}
	
	/**
	 * Connects to the specified host.
	 * This method may take some time to return depending on the speed
	 * of the host response.
	 * <p>
	 * If an error occurs throws an IOException.
	 * @param  connectionData object, containing all the host connection data
	 * @see         ConnectionData
	 */	public void connect(ConnectionData connectionData) throws IOException {

		 System.out.println("\nStarting connection:");
		 System.out.println(connectionData.getHostIPString());
		 System.out.println(connectionData.getPort());
		 System.out.println(connectionData.getUser());
		 System.out.println(connectionData.getPass());
		 System.out.println(connectionData.getHostType());
		 System.out.println(connectionData.getLoginType());
		 System.out.println("");
		 
		_connectionData = connectionData;
		commBuffer = new CommBufferLogic();
		commBuffer.setConnectionData(connectionData);
		
		Socket socket = null;
		try {
			InetAddress addr = InetAddress.getByAddress(connectionData.getHostIP());
			int port = connectionData.getPort();
			SocketAddress sockaddr = new InetSocketAddress(addr, port);
			
			socket = new Socket();
			int timeoutMs = 2000;
			socket.connect(sockaddr, timeoutMs);
			_connectionData.setSocket(socket);
			
			
			
			//Telnet negotiation
			TelnetNegotiationLogic telnetNegotiationLogic = new TelnetNegotiationLogic();
			commBuffer.setNegotiating(true);
			telnetNegotiationLogic.negotiate(commBuffer, connectionData);
			commBuffer.setNegotiating(false);
			
			if(socket.isConnected()){
				System.out.println("isConnected(): true");
			}else{
				System.out.println("isConnected(): false");
			}
			
		}
		catch(UnknownHostException unknownExc) {
			unknownExc.printStackTrace();;
			throw unknownExc;
		}
		catch(SocketTimeoutException timeOutExc) {
			timeOutExc.printStackTrace();
			throw timeOutExc;
		}
		catch(IOException ioExc) {
			ioExc.printStackTrace();
			throw ioExc;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Finished connecting.");
	}
	 
	 
	 
	/**
	 * Close the current connection to the host.
	 * <p>
	 * This method always returns fast, but it depends on the host response.
	 */
	public void disconnect() {

//		SimpleLogger.writeLine("disconnect", "Begin disconnect", _connectionData.isWriteLog());
		try {
			_connectionData.getSocket().close();
			_connectionData.setKiller(true);
		} catch (IOException e) {
//			SimpleLogger.writeLine("disconnect", "Error:" + e.getMessage(), _connectionData.isWriteLog());
		}
//		SimpleLogger.writeLine("disconnect", "End disconnect", _connectionData.isWriteLog());
	}

	/**
	 * Process data output first and data input next.
	 * <p>
	 * This method requires an open connection else it will throw an IOException
	 *
	 * @see         connect
	 */
    public void putPacket() throws IOException, InterruptedException {
        if (commBuffer.getProcessingMode().equals(ProcessMode.modeSaveScreen)) {
        	commBuffer.setProcessingMode(ProcessMode.modePutPackets);
            if (commBuffer.getAuxBuffer().size() > 0) {
            	// puts the aux buffer into the _sendData buffer
                commBuffer.socketFillSendbuf(commBuffer.getAuxBuffer().size());
                // clears the aux buffer (since the data has been moved to _sendData in CommBufferLogic.java)
                commBuffer.getAuxBuffer().clear();
            }
        }
        else if (commBuffer.getProcessingMode().equals(ProcessMode.modeConnect)) {
        	commBuffer.setProcessingMode(ProcessMode.modePutPackets);
        	commBuffer.setSendDataPointer(0);
        	commBuffer.getAuxBuffer().clear();
        }
        else if ((commBuffer.getProcessingMode().equals(ProcessMode.modeReadMDTflds)) ||
        		(commBuffer.getProcessingMode().equals(ProcessMode.modeReadInpflds))) {
            commBuffer.setProcessingMode(ProcessMode.modePutPackets);
        }
        
        int lengthOffset = 0;
        if (commBuffer.getSendData().size() > 0 ) {
            if (commBuffer.isDoDoEndrecord() && commBuffer.isHaveDataStream()) {
                commBuffer.getSendData().add(TelnetCommandValue.TNC_IAC);
                commBuffer.incSendDataPointer();
                commBuffer.getSendData().add(TelnetCommandValue.TNC_EF);
                commBuffer.incSendDataPointer();
                lengthOffset = 2;
            }

          	commBuffer.getSendData().set(1, commBuffer.getSendData().size() - lengthOffset);

            for(int i = 0; i < commBuffer.getSendData().size(); ++i) {
            	if(commBuffer.getSendData().get(i) == null) {
            		commBuffer.getSendData().set(i, 64);
            	}
            }
            
           	commBuffer.send(_connectionData);
           	
           	
            if (commBuffer.getSendData().size() > 0) {
                commBuffer.setSendDataPointer(0);
                commBuffer.getSendData().clear();
            }
            else if (commBuffer.getSendData().size() == 0) {
            	if (commBuffer.isDoDoEndrecord() && commBuffer.isHaveDataStream()) {
                    commBuffer.setSendDataPointer(commBuffer.getSendDataPointer() - 2);
            	}
            }
        }

        
        if (commBuffer.getProcessingMode().equals(ProcessMode.modePutPackets)) { // runs when the module is waiting for a response
            commBuffer.getCommScreen().getScreenFields().clear();
            if (commBuffer.getReceiveCount() == 0) {
            	commBuffer.getReceiveData().clear();
            	commBuffer.setReceiveDataPointer(0);
                commBuffer.receive(_connectionData);
                if (commBuffer.getReceiveData().size() > 0) {
                	commBuffer.setHaveSOH(false);
                	commBuffer.setSendDataPointer(0);
                	commBuffer.setReceiveDataPointer(0);
                }
            }
            
            while (commBuffer.getReceiveDataPointer() < commBuffer.getReceiveCount()) { //gets stuck here
            	Comm5250Parser.ParseSocketData(commBuffer);
            }

            commBuffer.getReceiveData().clear();
        }
        else if (commBuffer.getProcessingMode().equals(ProcessMode.modeTerminate)) {
        	commBuffer.getCommScreen().addNewField(ScreenElementType.Terminate, "");
        }

        //Attribute correction post-process
        if (commBuffer.getCommScreen().getScreenFields().size() > 0) {
            for (int ix = 0; ix < commBuffer.getCommScreen().getScreenFields().size(); ++ix) {
                Integer scrPos = commBuffer.getCommScreen().getScreenFields().get(ix).getLinearPosition();
                if ((commBuffer.getCommScreen().getScreenFields().get(ix).getAttributes() & 0x100) == 0) {
                    if (scrPos >= 1) {
                    	if(commBuffer.getCommScreen().getScreenElements()[scrPos - 1].getAttribute().equals(commBuffer.getCommScreen().getScreenElements()[scrPos - 1].getValue())) {
                    		commBuffer.getCommScreen().getScreenFields().get(ix).setAttributes(commBuffer.getCommScreen().getScreenElements()[scrPos - 1].getAttribute());
                        }
                    }
                }
            }

            //Check for characters that really are attributes
            for (Integer ix = 0; ix < commBuffer.getCommScreen().getScreenFields().size() - 1; ++ix) {
                //Consecutive HostFields and the previous contains the second
                if (commBuffer.getCommScreen().getScreenFields().get(ix).getType().equals(ScreenElementType.HostField))
                    if (commBuffer.getCommScreen().getScreenFields().get(ix+1).getType().equals(ScreenElementType.HostField))
                        if (commBuffer.getCommScreen().getScreenFields().get(ix+1).getLinearPosition() > commBuffer.getCommScreen().getScreenFields().get(ix).getLinearPosition() &&
                        		((commBuffer.getCommScreen().getScreenFields().get(ix+1).getLinearPosition() + commBuffer.getCommScreen().getScreenFields().get(ix+1).getLength()) <=
                            (commBuffer.getCommScreen().getScreenFields().get(ix).getLinearPosition() + commBuffer.getCommScreen().getScreenFields().get(ix).getLength())))
                            if (commBuffer.getCommScreen().getScreenFields().get(ix+1).getAttributes() < 256) {
                                Integer localChar = commBuffer.getCommScreen().getScreenFields().get(ix+1).getAttributes() & 0xFF;
                                Integer pos = commBuffer.getCommScreen().getScreenFields().get(ix+1).getText().indexOf(localChar);
                                if (pos >= 0)
                                    if ((commBuffer.getCommScreen().getScreenFields().get(ix).getLinearPosition() + pos) - (commBuffer.getCommScreen().getScreenFields().get(ix+1).getLinearPosition() - 1) == 0)
                                        //Set element invisible
                                    	commBuffer.getCommScreen().getScreenFields().get(ix).setText("");
                                commBuffer.getCommScreen().getScreenFields().get(ix).setAttributes(0x27);
                            }
            }
            //End Check for characters that really are attributes
        }

        if (commBuffer.isCancelInvite()) {
            if (commBuffer.getCommScreen().getScreenFields().size() >= 2) {
                for (Integer ix = commBuffer.getCommScreen().getScreenFields().size() - 2; ix >= 0; --ix) {
                    if (commBuffer.getCommScreen().getScreenFields().get(ix).equals(ScreenElementType.UnlockKeyboard)) {
                        commBuffer.getCommScreen().getScreenFields().get(ix).setType(ScreenElementType.None);
                    }
                }
            }
        }
    }

	/**
	 * Adds each input field to the output screen buffer
	 * <p>
	 * This method always returns immediately. It requires COMM module to be initialized.
	 *
	 * @param  	screenField a ScreenField object
	 * @see     ScreenField
	 */
    public void getKeyInData(ScreenField screenField) {
        Integer row = screenField.getRow();
        Integer col = screenField.getCol();
        commBuffer.setCurrentRow(row);
        commBuffer.setCurrentCol(col);
        
        for (Integer idx = 0; idx < commBuffer.getCommScreen().getInpfldCount(); idx++) {
            Integer rcBgn =
            		commBuffer.getCommScreen().rowCol(commBuffer.getFormatTable()[idx].getBeginRow(),
            												 commBuffer.getFormatTable()[idx].getBeginCol());
            Integer rcEnd =
        		commBuffer.getCommScreen().rowCol(commBuffer.getFormatTable()[idx].getBeginRow(),
						 commBuffer.getFormatTable()[idx].getBeginCol() + commBuffer.getFormatTable()[idx].getFieldLen() - 1);
            Integer rcCsr =
        		commBuffer.getCommScreen().rowCol(row, col);
            boolean inside = (((rcCsr - rcBgn) >= 0) && ((rcCsr - rcEnd) <= 0));
            if (inside) { // Set MDT on
            	commBuffer.getFormatTable()[idx].getFFW()[0] = commBuffer.getFormatTable()[idx].getFFW()[0] | 0x08;
                commBuffer.setMasterMDT(true);
                break;
            }
        }

        int[] tempCharBuffer = new int[140];

        char[] charBuffer = screenField.getText().toCharArray();
        for(int i = 0; i < charBuffer.length; ++i) {
        	tempCharBuffer[i] = (int)charBuffer[i];
        }

        Integer scrPosn = commBuffer.getCommScreen().screenPosn(row, col);
        EncodeUtils eu = new EncodeUtils();
        for (Integer idx = 0; idx < charBuffer.length; idx++) {
            int hexchar = (int)tempCharBuffer[idx];
            if ((hexchar - commBuffer.getCommScreen().XLATEBYTE) >= 0) {
                hexchar = eu.getANSITOASCII()[hexchar - commBuffer.getCommScreen().XLATEBYTE];
            }

            tempCharBuffer[idx] = (char)hexchar;
            tempCharBuffer[idx] = eu.doAsciiToEbcdic(commBuffer, tempCharBuffer[idx]);
            commBuffer.getCommScreen().getScreenElements()[scrPosn - 1].setValue(tempCharBuffer[idx]);
            Comm5250ResponseBuilderUtils.UpdateCurrentCol(commBuffer, 1);
            scrPosn += 1;
        }
    }


	/**
	 * Check if there is data in the input buffer ready to be processed.
	 * It must be called before getPacket() method
	 * <p>
	 * This method requires an open connection else it will throw an IOException
	 * @return 	true / false
	 * @see     connect
	 */
    public boolean isDataToReadyProcess() throws IOException, InterruptedException {
    	//return false;
    	try {
        commBuffer.receive(_connectionData);
        if (commBuffer.getReceiveData().size() > 0) {
        	commBuffer.setHaveSOH(false);
        	commBuffer.setSendDataPointer(0);
        	commBuffer.setReceiveDataPointer(0);
        }
    	}
    	catch(Exception exc){
    		String s = exc.getMessage();
    	}
        return commBuffer.getReceiveData().size() > 0;
    }



	/**
	 * Process input.
	 * <p>
	 * This method requires an open connection else it will throw an IOException
	 *
	 * @see         connect
	 */
    public void getPacket() throws IOException, InterruptedException {

/*	        if (commBuffer.getProcessingMode().equals(ProcessMode.modePutPackets) ||
	        		(commBuffer.getProcessingMode().equals(ProcessMode.modeReadMDTflds)) ||
	        		(commBuffer.getProcessingMode().equals(ProcessMode.modeReadInpflds))) {
*/

            if (commBuffer.getProcessingMode().equals(ProcessMode.modePutPackets) ||
            		(commBuffer.getProcessingMode().equals(ProcessMode.modeReadMDTflds))) {
            commBuffer.getCommScreen().getScreenFields().clear();
            if (commBuffer.getReceiveData().size() == 0) {
            	commBuffer.setReceiveDataPointer(0);
                commBuffer.receive(_connectionData);
                if (commBuffer.getReceiveData().size() > 0) {
                	commBuffer.setHaveSOH(false);
                	commBuffer.setSendDataPointer(0);
                	commBuffer.setReceiveDataPointer(0);
                }
            }

            while (commBuffer.getReceiveDataPointer() < commBuffer.getReceiveCount()) {
            	Comm5250Parser.ParseSocketData(commBuffer);
            }

            commBuffer.getReceiveData().clear();
        }
        else if (commBuffer.getProcessingMode().equals(ProcessMode.modeTerminate)) {
        	commBuffer.getCommScreen().addNewField(ScreenElementType.Terminate, "");
        }

        //Attribute correction post-process
        if (commBuffer.getCommScreen().getScreenFields().size() > 0) {
            for (int ix = 0; ix < commBuffer.getCommScreen().getScreenFields().size(); ++ix) {
                Integer scrPos = commBuffer.getCommScreen().getScreenFields().get(ix).getLinearPosition();
                if ((commBuffer.getCommScreen().getScreenFields().get(ix).getAttributes() & 0x100) == 0) {
                    if (scrPos >= 1) {
                    	if(commBuffer.getCommScreen().getScreenElements()[scrPos - 1].getAttribute().equals(commBuffer.getCommScreen().getScreenElements()[scrPos - 1].getValue())) {
                    		commBuffer.getCommScreen().getScreenFields().get(ix).setAttributes(commBuffer.getCommScreen().getScreenElements()[scrPos - 1].getAttribute());
                        }
                    }
                }
            }

            //Check for characters that really are attributes
            for (Integer ix = 0; ix < commBuffer.getCommScreen().getScreenFields().size() - 1; ++ix) {
                //Consecutive HostFields and the previous contains the second
                if (commBuffer.getCommScreen().getScreenFields().get(ix).getType().equals(ScreenElementType.HostField))
                    if (commBuffer.getCommScreen().getScreenFields().get(ix+1).getType().equals(ScreenElementType.HostField))
                        if (commBuffer.getCommScreen().getScreenFields().get(ix+1).getLinearPosition() > commBuffer.getCommScreen().getScreenFields().get(ix).getLinearPosition() &&
                        		((commBuffer.getCommScreen().getScreenFields().get(ix+1).getLinearPosition() + commBuffer.getCommScreen().getScreenFields().get(ix+1).getLength()) <=
                            (commBuffer.getCommScreen().getScreenFields().get(ix).getLinearPosition() + commBuffer.getCommScreen().getScreenFields().get(ix).getLength())))
                            if (commBuffer.getCommScreen().getScreenFields().get(ix+1).getAttributes() < 256) {
                                Integer localChar = commBuffer.getCommScreen().getScreenFields().get(ix+1).getAttributes() & 0xFF;
                                Integer pos = commBuffer.getCommScreen().getScreenFields().get(ix+1).getText().indexOf(localChar);
                                if (pos >= 0)
                                    if ((commBuffer.getCommScreen().getScreenFields().get(ix).getLinearPosition() + pos) - (commBuffer.getCommScreen().getScreenFields().get(ix+1).getLinearPosition() - 1) == 0)
                                        //Set element invisible
                                    	commBuffer.getCommScreen().getScreenFields().get(ix).setText("");
                                commBuffer.getCommScreen().getScreenFields().get(ix).setAttributes(0x27);
                            }
            }
            //End Chk for characters that really are attributes
        }

        if (commBuffer.isCancelInvite()) {
            if (commBuffer.getCommScreen().getScreenFields().size() >= 2) {
                for (Integer ix = commBuffer.getCommScreen().getScreenFields().size() - 2; ix >= 0; --ix) {
                    if (commBuffer.getCommScreen().getScreenFields().get(ix).equals(ScreenElementType.UnlockKeyboard)) {
                        commBuffer.getCommScreen().getScreenFields().get(ix).setType(ScreenElementType.None);
                    }
                }
            }
        }

    }




	/**
	 * Sends the keystroke and the cursor position.
	 * <p>
	 * This method always returns as fast as host response,
	 * and requires an open connection.
	 *
	 * @param  aidKey the key code (Auto,F01, F02, F03, F04, F05, F06, F07,F08, F09, F10, F11, F12, F13, F14, F15,F16, F17, F18, F19, F20, F21, F22, F23,F24, Clr, Entr,Help,RlDn,RlUp,Prnt,RBsp)
	 * @param  screenPosn value of the index of the raw linear screen buffer
	 */
    public void getAidKey(Integer aidKey, Integer screenPosn) throws IOException {
        Integer[] AIDKEYS = {
							 // Auto  F01   F02   F03   F04   F05   F06   F07
								0xF1, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37,
								
							 // F08   F09   F10   F11   F12   F13   F14   F15
								0x38, 0x39, 0x3A, 0x3B, 0x3C, 0xB1, 0xB2, 0xB3,
								
							 // F16   F17   F18   F19   F20   F21   F22   F23
								0xB4, 0xB5, 0xB6, 0xB7, 0xB8, 0xB9, 0xBA, 0xBB,
								
							 // F24   Clr   Entr  Help  RlDn  RlUp  Prnt  RBsp
								0xBC, 0xBD, 0xF1, 0xF3, 0xF4, 0xF5, 0xF6, 0xF8
							};

        Integer sendlen = 0;
        Integer row = screenPosn / commBuffer.getCommScreen().getScreenWidth();
        Integer col = screenPosn - row * commBuffer.getCommScreen().getScreenWidth();
        row += 1;                          // ScreenPosn is 0,0 relative
        col += 1;                          // AS400 R/C is 1,1 relative
        commBuffer.setCurrentRow(row);
        commBuffer.setCurrentCol(col);

        commBuffer.setBBCOMAidKey(aidKey);
        if (aidKey == TN5250CommandValue.AidSystemRequest.intValue()) {
        	commBuffer.setTN5250Attr(TelnetCommandValue.SYSRQS_KEY);
        }
        else if (aidKey == TN5250CommandValue.AidAttention.intValue()) {
        	commBuffer.setTN5250Attr(TelnetCommandValue.ATTN_KEY);
        }
        else if (aidKey == TN5250CommandValue.AidErrorHelp.intValue()) {
        	commBuffer.setTN5250Attr(TelnetCommandValue.ERR_HELP);
        }
        else if (aidKey == TN5250CommandValue.AidAbort.intValue()) {
        	commBuffer.setTN5250Attr(TelnetCommandValue.ABORT_KEY);
        }
        else  { // 0x00000000 .. 0x40000000
            Integer temp = aidKey;
            Integer idx = 0;
            while (temp > 0) {
                temp = temp / 2;
                idx += 1;
            }
            commBuffer.setTN5250AidCode(AIDKEYS[idx]);
        }
        if (commBuffer.isProcessError()) {
            commBuffer.setProcessError(false);
            if (commBuffer.getSaveHomeRow() > 0) {
            	commBuffer.setHomeRow(commBuffer.getSaveHomeRow());
            	commBuffer.setHomeCol(commBuffer.getSaveHomeCol());
            }

            commBuffer.setSaveHomeRow(0);
            commBuffer.setSaveHomeCol(0);
        }

        commBuffer.setFieldRow(1);
        commBuffer.setFieldCol(1);
        commBuffer.setFieldLength(0);
        commBuffer.setSBARow(0);
        commBuffer.setSBACol(0);

        if ((commBuffer.getTN5250AidCode() - TelnetCommandValue.SYSRQS_KEY) == 0) {
            sendlen = Comm5250ResponseBuilder.Do5250BinaryResponse(commBuffer, TelnetCommandValue.SYSRQS_KEY);
        }
        else if ((commBuffer.getTN5250AidCode() - TelnetCommandValue.ATTN_KEY) == 0) {
        	sendlen = Comm5250ResponseBuilder.Do5250BinaryResponse(commBuffer, TelnetCommandValue.ATTN_KEY);
        }
        else if ((commBuffer.getTN5250AidCode() - TelnetCommandValue.ERR_HELP) == 0) {
        	sendlen = Comm5250ResponseBuilder.Do5250BinaryResponse(commBuffer, TelnetCommandValue.ERR_HELP);
        }
        else if ((commBuffer.getTN5250AidCode() - TelnetCommandValue.ABORT_KEY) != 0) {
            if (commBuffer.getProcessingMode() == ProcessMode.modeReadMDTflds) {
        		sendlen = Comm5250ResponseBuilder.Do5250BinaryResponse(commBuffer, TN5250CommandValue.READ_MDTFLDS);
            }
            else {
            	sendlen = Comm5250ResponseBuilder.Do5250BinaryResponse(commBuffer, TN5250CommandValue.READ_INPFLDS);
            }
        }


        if (sendlen > 0) {
        	commBuffer.socketFillSendbuf(sendlen);
        }

        if ((commBuffer.getTN5250AidCode() - TelnetCommandValue.ABORT_KEY) != 0) {
            // LockKeyboard (); ???
        	Comm5250ResponseBuilder.DoCmdCtrlChars(commBuffer);
        }
        else {
            commBuffer.getConnectionData().getSocket().shutdownInput();
            commBuffer.getConnectionData().getSocket().shutdownOutput();
            commBuffer.getConnectionData().getSocket().close();
            commBuffer.setProcessingMode(ProcessMode.modeTerminate);
        }
    }

	public boolean isClosed() {
		return _connectionData.isKiller();
	}

	@Override
	public void setUsername(String sentUser) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPassword(String sentPass) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}
}
