package com.aisla.newrolit.com;


import java.io.IOException;

/**
 * Comm5250Parser is the main parser object 
 * that works at Telnet level and call objects
 * that handle 5250 operations
 * <p>
 * @author      Luis Paradela
 * @version     1.0
 * @since       1.0
 */
public class Comm5250Parser {

	/**
	 * Main parser method, that basically calls
	 * telnet negotiation method or 5250 methods 
	 * once the telnet negotiation is complete 
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @param  commBuffer CommBufferLogic object
	 * 
	 * @see CommBufferLogic
	 */    
	public static void ParseSocketData(CommBufferLogic commBuffer) throws IOException {
//		SimpleLogger.writeLine("ParseSocketData", "Begin ParseSocketData", commBuffer.getConnectionData().isWriteLog());

		Integer byDoNegotiation = commBuffer.getCurrentByte();
		if((byDoNegotiation - TelnetCommandValue.TNC_IAC) == 0) {
			ParseNegotiation(commBuffer);
		}
		else if(HaveTN5250Header(commBuffer)){
			commBuffer.resetSend();
			commBuffer.setDoDataStream(true);
			Comm5250ResponseBuilder.DoTN5250Header(commBuffer);
		}
		
//		SimpleLogger.writeLine("ParseSocketData", "End ParseSocketData", commBuffer.getConnectionData().isWriteLog());
	}
	
	/**
	 * Telnet negotiation parser
	 * Notes: Only 5250 related commands are implemented
	 * but enviroment commando is not implemented yet 
	 * <p>
	 * This method takes as long as the host process to return. 
	 *
	 * @param  commBuffer CommBufferLogic object
	 * 
	 * @see CommBufferLogic
	 */    
	public static void ParseNegotiation(CommBufferLogic commBuffer) throws IOException {
//		SimpleLogger.writeLine("ParseNegotiation", "Begin ParseSocketData", commBuffer.getConnectionData().isWriteLog());
        // Recv <TNC_IAC><TNC_DO><TNO_TTYP>
        // Send <TNC_IAC><TNC_WILL><TNO_TTYP>

        // Recv <TNC_IAC><TNC_SB><TNO_TTYP><TNO_SEND><TNC_IAC><TNC_SE>
        // Send <TNC_IAC><TNC_SB><TNO_TTYP><TNO_IS><IBM-5292-2><TNC_IAC><TNC_SE>

        // Recv <TNC_IAC><TNC_DO><TNO_EOR><TNC_IAC><TNC_WILL><TNO_EOR>
        //      <TNC_IAC><TNC_DO><TNO_TBIN><TNC_IAC><TNC_WILL><TNO_TBIN>
        // Send <TNC_IAC><TNC_WILL><TNO_EOR><TNC_IAC><TNC_DO><TNO_EOR>
        //      <TNC_IAC><TNC_WILL><TNO_TBIN><TNC_IAC><TNC_DO><TNO_TBIN><TNC_IAC><TNC_EF>

        // Recv <TNC_IAC><TNC_DO><TNO_TBIN><TNC_IAC><TNC_WILL><TNO_TBIN>
        //      EBCDIC Binary Screen Data <TNC_IAC><TNC_EF>
		
        Integer byNegotiationType = 0;
        Integer byAnswerType = 0;
        Integer bySubType = 0;
        byNegotiationType = commBuffer.getCurrentByte();
        commBuffer.incReceiveDataPointer();
        if ((byNegotiationType - TelnetCommandValue.TNC_IAC) == 0) {
            byNegotiationType = commBuffer.getCurrentByte();
            commBuffer.incReceiveDataPointer();
            if ((byNegotiationType - TelnetCommandValue.TNC_DO) == 0) {
                byAnswerType = TelnetCommandValue.TNC_WILL;
            }
            else if ((byNegotiationType - TelnetCommandValue.TNC_WILL) == 0) {
                byAnswerType = TelnetCommandValue.TNC_DO;
            }
            else if ((byNegotiationType - TelnetCommandValue.TNC_DONT) == 0) {
                byAnswerType = TelnetCommandValue.TNC_WONT;
            }
            else if ((byNegotiationType - TelnetCommandValue.TNC_WONT) == 0) {
                byAnswerType = TelnetCommandValue.TNC_DONT;
            }
            
            switch (byNegotiationType)
            {
                case 253: //TNC_DO: 0xFD Offer to do - WILL/WONT response
                case 251: //TNC_WILL: 0xFB Offer to do - DO/DONT response
                    bySubType = commBuffer.getCurrentByte();
                    commBuffer.incReceiveDataPointer();
                    
                    switch (bySubType)
                    {
                        case 0: //TNO_TBIN: Transmit Binary
                            {                   // Sometimes get two Do Binary
                                if (!commBuffer.isDoBinary()) // and need to acknowledge else lose
                                {               // connection
                                    ParseSendAnswer(byAnswerType, bySubType, commBuffer);
                                    if ((byNegotiationType - TelnetCommandValue.TNC_WILL) == 0) {
                                        commBuffer.setDoBinary(true);
                                    }
                                }
                                else if (!commBuffer.isDoBinaryAgain()) {
                                    ParseSendAnswer(byAnswerType, bySubType, commBuffer);
                                    if ((byNegotiationType - TelnetCommandValue.TNC_DO) == 0) {
                                        commBuffer.setDoBinary(true);
                                    }
                                }
                                break;

                            }
                        case 3:   //TNO_SGA: Suppress Go Ahead  - RFC858
                            ParseSendAnswer(byAnswerType, bySubType, commBuffer);
                            break;

                        case 24: //TNO_TTYP:Terminal Type
                            ParseSendAnswer(byAnswerType, bySubType, commBuffer);
                            break;

                        case 25: //TNO_EOR: End of Record
                                if (!commBuffer.isDoDoEndrecord()) {
                                    ParseSendAnswer(byAnswerType, bySubType, commBuffer);
                                    if ((byNegotiationType - TelnetCommandValue.TNC_WILL) == 0) {
                                    	commBuffer.setDoDoEndrecord(true);
                                    }
                                }
                                break;
                        case 39: //TNO_ENV: New enviroment
                        		/*
	                            if (commBuffer.getConnectionData().getDisplayDevice().length() == 0) {
	                            	commBuffer.getConnectionData().setDisplayDevice("DefDev");
	                            }
                                ParseSendAnswer(byAnswerType, bySubType, commBuffer);
                                ManageEnvProtocol(commBuffer);
                                */
                                break;
                        default:
                            {                   // Not processed
                                if ((byNegotiationType - TelnetCommandValue.TNC_WILL) == 0)
                                    byAnswerType = TelnetCommandValue.TNC_DONT;
                                else
                                    byAnswerType = TelnetCommandValue.TNC_WONT;
                                ParseSendAnswer(byAnswerType, bySubType, commBuffer);
                                break;

                            }
                    }
                    break;

                case 254: //TNC_DONT
                case 252: //TNC_WONT
                        bySubType = commBuffer.getCurrentByte();
                        commBuffer.incReceiveDataPointer();
                        ParseSendAnswer(byAnswerType, bySubType, commBuffer);
                        break;
                case 250: //TNC_SB: Subnegotiation Begin
                    ParseSubnegotiation(commBuffer);
                    break;

                case 249: //TNC_GA: Go Ahead
                    break;

                case 248: //TNC_EL: Erase Line
                    break;

                case 247: //TNC_EC: Erase Character
                    break;

                case 246: //TNC_AYT: Are You There
                    break;

                case 245: //TNC_AO: Abort Output
                    break;

                case 244: //TNC_IP: interrupt Process
                    break;

                case 243: //TNC_BRK: Break
                    break;

                case 242: //TNC_DM: Data Mark
                    break;

                case 241: //TNC_NOP: No Operation
                    break;

                case 240: //TNC_SE: Subnegotiation End
                    break;

                case 239: //TNC_EF: End of Record
                    break;

            }
        }
        else {
        	commBuffer.decReceiveDataPointer();
        }
//		SimpleLogger.writeLine("ParseNegotiation", "End ParseSocketData", commBuffer.getConnectionData().isWriteLog());
	}

	/**
	 * Checks it the stream contains a 5250 valid header
	 * <p>
	 * This method takes as long as the host process to return. 
	 *
	 * @param  CommBufferLogic object
	 * 
	 * @return 	true / false
	 * @see CommBufferLogic
	 */    
	public static boolean HaveTN5250Header(CommBufferLogic commBuffer) {
//		SimpleLogger.writeLine("HaveTN5250Header", "Begin HaveTN5250Header", commBuffer.getConnectionData().isWriteLog());
        // |++++++++++++++|+++++++++++++++|+++++++++++++++|
        // | LglRcdLength |    0x12A0     |   Reserved    | Header[1] -- 6 bytes
        // |++++++++++++++|+++++++++++++++|+++++++++++++++|

		boolean result = true;
		int gdsType = 0x12A0;            // General Data Stream Type
		int temp = 0;
		if(commBuffer.getNextByte() == 0xFF && 
		   commBuffer.getReceiveData().get(commBuffer.getReceiveDataPointer() + 2) == 0xFF) {
			commBuffer.incReceiveDataPointer(3);
		}
		else {
			commBuffer.incReceiveDataPointer(2);
		}
		
		temp = (0xFF & commBuffer.getCurrentByte()) << 8 | (0xFF & commBuffer.getNextByte());
		result = temp == gdsType;
		if(result) {
			commBuffer.incReceiveDataPointer(4);
		}		
		
//		SimpleLogger.writeLine("HaveTN5250Header", "End HaveTN5250Header", commBuffer.getConnectionData().isWriteLog());
		return result;
	}
	
	/**
	 * Builds a negotiation answer
	 * Used by ParseNegotiation method
	 * <p>
	 * This method takes as long as the host process to return. 
	 *
	 * @param  screenField negotiation code
	 * @param  option negotiation sub code
	 * @param  commBuffer CommBufferLogic object
	 * 
	 * @see CommBufferLogic
	 */    
	public static void ParseSendAnswer(Integer negotiation, Integer option, CommBufferLogic commBuffer) {
//		SimpleLogger.writeLine("ParseSendAnswer", "Begin ParseSendAnswer negotiation:" + negotiation.toString() + " option:" + option.toString(), commBuffer.getConnectionData().isWriteLog());		
		commBuffer.getSendData().add(TelnetCommandValue.TNC_IAC);
		commBuffer.getSendData().add(negotiation);
		commBuffer.getSendData().add(option);
		commBuffer.incSendDataPointer(3);
//		SimpleLogger.writeLine("ParseSendAnswer", "End ParseSendAnswer", commBuffer.getConnectionData().isWriteLog());		
		
	}
	
	/**
	 * Environment protocol
	 * Needs more work for the final version
	 * <p>
	 * This method takes as long as the host process to return. 
	 *
	 * @param  commBuffer CommBufferLogic object
	 * 
	 * @see CommBufferLogic
	 */    
	public static void ManageEnvProtocol(CommBufferLogic commBuffer) throws IOException {		
//		SimpleLogger.writeLine("ManageEnvProtocol", "Begin ManageEnvProtocol", commBuffer.getConnectionData().isWriteLog());		
        Boolean screenTypePresent = false;
        Integer i = 0;

        //Check to see if 0x18 is there ( Terminal Type )
        for(i=0; i < commBuffer.getReceiveCount(); ++i) { 
        	if(commBuffer.getCurrentByte().equals(0x18)) {
        		screenTypePresent = true;
        		break;
        	}
        }

        //Send ff fb 27 sequence for environment
        commBuffer.send(commBuffer.getConnectionData());
        //Receive ff fa 27 01 ff f0
        commBuffer.receive(commBuffer.getConnectionData());
        if ((commBuffer.getReceiveCount() == 6) &&
        	 commBuffer.getReceiveData().get(0) == 0xff &&
        	 commBuffer.getReceiveData().get(1) == 0xfa &&
        	 commBuffer.getReceiveData().get(2) == 0x27 &&
        	 commBuffer.getReceiveData().get(3) == 0x01 &&
        	 commBuffer.getReceiveData().get(4) == 0xff &&
        	 commBuffer.getReceiveData().get(5) == 0xf0
            ) {
            commBuffer.setSendDataPointer(0);
        }


        //Prepare Env with Device name Var
        commBuffer.resetSend();
        commBuffer.getSendData().add(0xff);
        commBuffer.getSendData().add(0xfa);
        commBuffer.getSendData().add(0x27);
        commBuffer.getSendData().add(0x00);
        commBuffer.getSendData().add(0x03);
        commBuffer.getSendData().add(0x44);
        commBuffer.getSendData().add(0x45);
        commBuffer.getSendData().add(0x56);
        commBuffer.getSendData().add(0x4e);
        commBuffer.getSendData().add(0x41);
        commBuffer.getSendData().add(0x4d);
        commBuffer.getSendData().add(0x45);
        commBuffer.getSendData().add(0x01);

        char[] displayDeviceArr = commBuffer.getConnectionData().getDisplayDevice().toCharArray();
        for (i = 0; i < displayDeviceArr.length; ++i) {
        	commBuffer.getSendData().add((int)String.valueOf(displayDeviceArr[i]).charAt(0));
        }
        commBuffer.getSendData().add(0xff);
        commBuffer.getSendData().add(0xf0);

        //Send Env with Device name Var
        commBuffer.send(commBuffer.getConnectionData());
        commBuffer.resetSend();

        
        //Prepare ff fd 18 on received
        if (screenTypePresent && commBuffer.getReceiveDataPointer() == 6)         {
            commBuffer.resetSend();
            commBuffer.getReceiveData().add(0xff);
            commBuffer.getReceiveData().add(0xfd);
            commBuffer.getReceiveData().add(0x18);
        }
//		SimpleLogger.writeLine("ManageEnvProtocol", "End ManageEnvProtocol", commBuffer.getConnectionData().isWriteLog());		
	}
	
	/**
	 * Parse second level telnet negotiation or subnegotiation
	 * Needs more work for the final version
	 * <p>
	 * This method takes as long as the host process to return. 
	 *
	 * @param  commBuffer CommBufferLogic object
	 * 
	 * @see CommBufferLogic
	 */    
	public static void ParseSubnegotiation(CommBufferLogic commBuffer) {		
		
		Integer subType;
        
        subType = commBuffer.getCurrentByte();
        commBuffer.incReceiveDataPointer();
        
        
        if ((subType - TelnetCommandValue.TNO_ENV) == 0) {
            subType = commBuffer.getCurrentByte();
            commBuffer.incReceiveDataPointer();
            if ((subType - TelnetCommandValue.TENV_SEND) == 0) {
            	//TODO
            	//Send environment data was not implemented in the original API5250
            }
        }
        else if ((subType - TelnetCommandValue.TNO_TTYP) == 0) {
            subType = commBuffer.getCurrentByte();
            commBuffer.incReceiveDataPointer();
            if ((subType - TelnetCommandValue.TTYP_SEND) == 0) {
                commBuffer.incNegotiationNumber();
                if (!commBuffer.isDsp28x132()) {
                    if (commBuffer.isColorTube()) {
                   		commBuffer.socketSendTerminalType(TelnetCommandValue.IBM_3179);
                    }
                    else {
                    	commBuffer.socketSendTerminalType(TelnetCommandValue.IBM_5291);
                    }
                }
                else {
                    if (commBuffer.isColorTube())
                    {
                        //First time negotiate INET, next IBM terminal type
                        if (commBuffer.getNegotiationNumber() == 1) {
                            //ibytesSent = socketSendTerminalType ( INET3477C );
                            //LDP: Dougal SUN iSeries Change
                        	commBuffer.socketSendTerminalType(TelnetCommandValue.IBM3477C);
                        }
                        else {
                        	commBuffer.socketSendTerminalType(TelnetCommandValue.IBM3477C);
                        }
                    }
                    else {
                    	commBuffer.socketSendTerminalType(TelnetCommandValue.IBM3477G);
                    }
                }
                if (commBuffer.getAuxBuffer().size() > 0) {
                	commBuffer.socketFillSendbuf(commBuffer.getAuxBuffer().size());
                }
            }
        }                                   // Move past <TNC_IAC> <TNC_SE>
        
        while (((subType - TelnetCommandValue.TNC_SE) != 0) && 
        		(commBuffer.getReceiveDataPointer() < commBuffer.getReceiveCount())) {
            subType = commBuffer.getCurrentByte();
            commBuffer.incReceiveDataPointer();
        }
//		SimpleLogger.writeLine("ParseSubnegotiation", "End ParseSubnegotiation", commBuffer.getConnectionData().isWriteLog());		
	}
}
