package com.aisla.newrolit.com;

import com.aisla.newrolit.global.GreenScreenElement;
import com.aisla.newrolit.global.TN5250CommandValue;

import java.util.ArrayList;

/**
 * Comm5250ResponseBuilder is the class holds  
 * logic of the answers to 5250 host
 * <p>
 * @author      Luis Paradela
 * @version     1.0
 * @since       1.0
 */
public class Comm5250ResponseBuilder {
	
	/**
	 * Parses the 5250 header and call methods 
	 * that performs the 5250 operation 
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @param  commBuffer CommBufferLogic object
	 * 
	 * @see CommBufferLogic
	 */    
	public static int DoTN5250Header( CommBufferLogic commBuffer) {
//		SimpleLogger.writeLine("DoTN5250Header", "Begin DoTN5250Header", commBuffer.getConnectionData().isWriteLog());
        // General Datastream Header (RFC1205)
        // |++++++++++++++|+++++++++++++++|
        // | 0x04 | Bits  |   0   | Opcode|                 Header[2] -- 4 Bytes
        // |++++++++++++++|+++++++++++++++|
        // followed by 5250DS <IAC><EOR>
        // or only <IAC><EOR>
        // May be multiple occurrences of this
        TN5250Header tn5250Header = new TN5250Header();
        Integer byDScmd;

        if (commBuffer.getCurrentByte() != 0x04) {
            return -1;
        }

        tn5250Header.setFixedlen(commBuffer.getCurrentByte()); // 0x04 (always)
        commBuffer.incReceiveDataPointer();

        if ((commBuffer.getCurrentByte() & 0x80) > 0) {
            tn5250Header.setOutdserr(TelnetCommandValue.BITON); // Bits
        }
        else {
        	tn5250Header.setOutdserr(TelnetCommandValue.BITOFF);
        }

        if ((commBuffer.getCurrentByte() & 0x40) > 0) {
            tn5250Header.setAttnkey(TelnetCommandValue.BITON); // Bits
        }
        else {
        	tn5250Header.setAttnkey(TelnetCommandValue.BITOFF); // Bits
        }

        if ((commBuffer.getCurrentByte() & 0x20) > 0) {
        	tn5250Header.setSysrqskey(TelnetCommandValue.BITON); // Bits
        }
        else {
        	tn5250Header.setSysrqskey(TelnetCommandValue.BITOFF); // Bits
        }

        if ((commBuffer.getCurrentByte() & 0x10) > 0) {
        	tn5250Header.setTestkey(TelnetCommandValue.BITON); // Bits
        }
        else {
        	tn5250Header.setTestkey(TelnetCommandValue.BITOFF); // Bits
        }
        
        if ((commBuffer.getCurrentByte() & 0x08) > 0) {
        	tn5250Header.setHelpinerr(TelnetCommandValue.BITON); // Bits
        }
        else {
        	tn5250Header.setHelpinerr(TelnetCommandValue.BITOFF); // Bits
        }

        commBuffer.incReceiveDataPointer();

        tn5250Header.setReserved8(commBuffer.getCurrentByte()); // 0
        commBuffer.incReceiveDataPointer();

        tn5250Header.setOpcode(commBuffer.getCurrentByte()); // 0x00..0x0C
        commBuffer.incReceiveDataPointer();

        if (tn5250Header.getFixedlen() != 0x04)  {
            return -1;
        }

        do { // One screen has 0F 35 00 ???
            byDScmd = commBuffer.getCurrentByte();
            commBuffer.incReceiveDataPointer();
        } while (((byDScmd - TelnetCommandValue.DS5250_ESCAPE) != 0) && 
        		((byDScmd - TelnetCommandValue.TNC_IAC) != 0));

        commBuffer.decReceiveDataPointer();
        
        switch (tn5250Header.getOpcode())
        {
            case 0x00: //TelnetCommandValue.OP_NOP:
                break;

            case 0x01: //TelnetCommandValue.OP_INVITE:                 
            	// Not sure if require format name
            	//Seems to appear after Save/Restore
            case 0x02: //TelnetCommandValue.OP_OUTONLY:
            case 0x03: //TelnetCommandValue.OP_PUTGET:
                	// Remove 052300
                	if ((tn5250Header.getOpcode() - TelnetCommandValue.OP_INVITE) != 0) {
                        commBuffer.getCommScreen().addFormatNameField(commBuffer);
                    }
                    commBuffer.setHadNewFmt(true);
                    
                    byDScmd = commBuffer.getCurrentByte();
                    commBuffer.incReceiveDataPointer();
                    
                    while ((byDScmd - TelnetCommandValue.DS5250_ESCAPE) == 0) { // 0x04
                        Do5250DataStream(commBuffer);
                        byDScmd = commBuffer.getCurrentByte();
                        commBuffer.incReceiveDataPointer();
                    }
                    commBuffer.decReceiveDataPointer();
                    break;
            case 0x04: //TelnetCommandValue.OP_SAVSCR:
            case 0x05: //TelnetCommandValue.OP_RSTSCR:
                    byDScmd = commBuffer.getCurrentByte();
                    commBuffer.incReceiveDataPointer();
                    while ((byDScmd - TelnetCommandValue.DS5250_ESCAPE) == 0) { // 0x04
                        Do5250DataStream(commBuffer);
                        byDScmd = commBuffer.getCurrentByte();
                        commBuffer.incReceiveDataPointer();
                    }
                    commBuffer.decReceiveDataPointer();
                    break;

            case 0x06: //TelnetCommandValue.OP_RDIMMED:
            case 0x08: //TelnetCommandValue.OP_RDSCREEN:
                    byDScmd = commBuffer.getCurrentByte();
            		commBuffer.incReceiveDataPointer();
                    while ((byDScmd - TelnetCommandValue.DS5250_ESCAPE) == 0) { // 0x04
                        Do5250DataStream(commBuffer);
                        byDScmd = commBuffer.getCurrentByte();
                        commBuffer.incReceiveDataPointer();
                    }
                    commBuffer.decReceiveDataPointer();
                    break;

            case 0x0A: //TelnetCommandValue.OP_INVOFF:
                                                  // Send back Cancel Invite (RFC1205)
                    Integer sendlen = 10;         // Get this with System Request
                    commBuffer.setAuxBuffer(new ArrayList<Integer>());
                    commBuffer.getAuxBuffer().add(TelnetCommandValue.NULLCHAR);
                    commBuffer.getAuxBuffer().add(0x0A); // Record Length
                    commBuffer.getAuxBuffer().add(0x12); // SNA Header
                    commBuffer.getAuxBuffer().add(0xA0);
                    commBuffer.getAuxBuffer().add(TelnetCommandValue.NULLCHAR);
                    commBuffer.getAuxBuffer().add(TelnetCommandValue.NULLCHAR);
                    commBuffer.getAuxBuffer().add(0x04); // Second header
                    commBuffer.getAuxBuffer().add(TelnetCommandValue.NULLCHAR);
                    commBuffer.getAuxBuffer().add(TelnetCommandValue.NULLCHAR);
                    commBuffer.getAuxBuffer().add(TelnetCommandValue.OP_INVOFF);
                    commBuffer.socketFillSendbuf(sendlen);
                    commBuffer.setReceiveDataPointer(commBuffer.getReceiveCount());
                    commBuffer.setCancelInvite(true);
                    break;

            case 0x0B: //TelnetCommandValue.OP_MSGLTON:
                break;

            case 0x0C: //TelnetCommandValue.OP_MSGLTOFF:
                break;

        }

//		SimpleLogger.writeLine("DoTN5250Header", "End DoTN5250Header", commBuffer.getConnectionData().isWriteLog());
        return 0;
	}
	
	/**
	 * Parses the 5250 stream  
	 * and performs the 5250 operations
	 * it is call by DoTN5250Header
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @param  commBuffer CommBufferLogic object
	 * 
	 * @see CommBufferLogic
	 */    
	public static void Do5250DataStream(CommBufferLogic commBuffer) {
//		SimpleLogger.writeLine("Do5250DataStream", "Begin Do5250DataStream", commBuffer.getConnectionData().isWriteLog());

		commBuffer.setClrUnitAlt(false);
        Integer dsCmd;
        Integer i = 0;
        boolean clrUnitAlt = false;
        GreenScreenElement gsElement = new GreenScreenElement(0x0, 0x20); 
        if(commBuffer.getCommScreen().getScreenElements().length == 0) {
            for (i = 0; i < commBuffer.getCommScreen().getMaxScreenElements(); i++) {
            	GreenScreenElement gsElementAux = new GreenScreenElement(0x00, 0x20);
            	commBuffer.getCommScreen().getScreenElements()[i] = gsElementAux;		// sets the screen elements sent in the stream to the commBuffer
            }
        }

        dsCmd = commBuffer.getCurrentByte();
        commBuffer.incReceiveDataPointer();

        switch (dsCmd) {
            case 0x02: //SAVE_SCREEN                                // Send 0412 (Restore Screen) + more
                    Do5250BinaryResponse(commBuffer, TN5250CommandValue.SAVE_SCREEN);
                    Comm5250ResponseBuilderUtils.GetInputData(commBuffer, dsCmd);
                    break;
            case 0x03: //SAVE_SCR_PARTIAL
                break;

            case 0x11: //WRITE_TO_DISPLAY
                    if (commBuffer.getWTDCmdCtrl().size() == 0) {
                    	commBuffer.getWTDCmdCtrl().add(0);
                    	commBuffer.getWTDCmdCtrl().add(0);
                    }

                    commBuffer.getWTDCmdCtrl().set(0, commBuffer.getCurrentByte());
                    commBuffer.incReceiveDataPointer();
                    commBuffer.getWTDCmdCtrl().set(1, commBuffer.getCurrentByte());
                    commBuffer.incReceiveDataPointer();

                    if (((commBuffer.getCurrentByte() - TelnetCommandValue.DS5250_ESCAPE) != 0) &&
                        (!commBuffer.isNewFmt())) {
                    	commBuffer.getCommScreen().addFormatNameField(commBuffer);
                    }
                    else {
                    	commBuffer.setHadNewFmt(false);
                    }

                    DoWTDOrders(commBuffer);
                    DoCmdCtrlChars(commBuffer);

                    break;
            case 0x12: //RESTORE_SCREEN
                    Do5250BinaryResponse(commBuffer, TN5250CommandValue.RESTORE_SCREEN);
                    break;

            case 0x13: //RESTORE_SCR_PARTIAL
                break;

            case 0X16: //COPY_TO_PRINTER
                break;

            case 0x21: //WRITE_ERRCODE

                    commBuffer.setCurrentRow(commBuffer.getCommScreen().getErrorLine());   // Clear and place on screen error line
                    commBuffer.setCurrentCol(1);
                    Comm5250ResponseBuilderUtils.UpdateFieldData(commBuffer, true);     // Force out any field data
                    Integer scrPosn = commBuffer.getCommScreen().screenPosn(commBuffer.getCurrentRow(), commBuffer.getCurrentCol());
                    commBuffer.setProcessError(true);
                    commBuffer.getCommScreen().addNewField(ScreenElementType.ErrorNotification, scrPosn - 1, 0, 0, "");
                    DoWTDOrders(commBuffer);             // May include IC order
                    break;
            case 0x22: //WRITE_ERRCODE_WINDOW
                //NOT operational (not allowed yet)
        		commBuffer.setReceiveDataPointer(commBuffer.getReceiveDataPointer() + 2);
        		commBuffer.setProcessError(true);
                DoWTDOrders(commBuffer);
                break;

            case 0x23: //ROLL_SCREEN
                    boolean rollUp = (commBuffer.getCurrentByte() & 0x80) == 0x00;
                    Integer nbrLines = commBuffer.getCurrentByte();
                    commBuffer.incReceiveDataPointer();
                    Integer topLine = commBuffer.getCurrentByte();
                    commBuffer.incReceiveDataPointer();
                    Integer btmLine = commBuffer.getCurrentByte();
                    commBuffer.incReceiveDataPointer();

                    if ((topLine == 0) ||
                        (btmLine > commBuffer.getCommScreen().getScreenHeight()) ||
                        (topLine >= btmLine) ||
                        (nbrLines > (btmLine - topLine))) {
                        // Invalid data stream command 0x1003 0x01 0x01
                        Do5250NegativeResponse(commBuffer, 0x10030101);
                    }
                    else if ((rollUp) && (nbrLines > 0)) {
                        Integer bgnPosn = commBuffer.getCommScreen().screenPosn(topLine + nbrLines, 1);
                        Integer endPosn = commBuffer.getCommScreen().screenPosn(btmLine, commBuffer.getCommScreen().getScreenWidth());
                        Integer newPosn =  commBuffer.getCommScreen().screenPosn(topLine, 1);
                        Integer length = endPosn - bgnPosn + 1;

                        for (Integer trans = 0; trans < length; ++trans) {
                            commBuffer.getCommScreen().getScreenElements()[newPosn - 1 + trans] = 
                            	commBuffer.getCommScreen().getScreenElements()[bgnPosn - 1 + trans];
                        }
                        commBuffer.getCommScreen().addNewField(ScreenElementType.RollUp, topLine - 1, btmLine - 1, nbrLines, "");
                    }
                    break;
            case 0x20: //CLEAR_UNIT_ALT
//        		SimpleLogger.writeLine("Do5250DataStream", "CLEAR_UNIT_ALT operation", commBuffer.getConnectionData().isWriteLog());

                    if (!commBuffer.isDsp28x132()) {
                        // Invalid data stream command 0x1003 0x01 0x01
                        Do5250NegativeResponse(commBuffer, 0x10030101);
                    }
                    
                    Integer parm = commBuffer.getCurrentByte();
                    commBuffer.incReceiveDataPointer();
                    if (parm == 0x00) {
                        if (commBuffer.getCommScreen().getScreenHeight() != commBuffer.getCommScreen().getMaxScreenHeight()) {
                            commBuffer.getCommScreen().setScreenHeight(commBuffer.getCommScreen().getMaxScreenHeight()); //27
                            commBuffer.getCommScreen().setScreenWidth(commBuffer.getCommScreen().getMaxScreenWidth()); //132
                            commBuffer.getCommScreen().addNewFieldFormat(ScreenElementType.Set27x132, "");
                        }
                        
                        clrUnitAlt = true;     // Fall thru to Clear_Unit
                    }
                    else if (parm == 0x80) {    // Used for Image/Fax only
                        break;
                    }
                    else {
                        // Invalid clear cmd   0x1003 0x01 0x05
                    	Do5250NegativeResponse(commBuffer, 0x10030105);
                    	
                        commBuffer.setTN5250Attr(gsElement.getAttribute());                        
                        if (commBuffer.isDsp28x132() && (!clrUnitAlt) && 
                           (commBuffer.getCommScreen().getScreenHeight() - commBuffer.getCommScreen().getMinScreenHeight()) != 0) {
                            commBuffer.getCommScreen().setScreenHeight(commBuffer.getCommScreen().getMinScreenHeight()); //24
                            commBuffer.getCommScreen().setScreenWidth(commBuffer.getCommScreen().getMinScreenWidth());   //80
                            commBuffer.getCommScreen().addNewFieldFormat(ScreenElementType.Set24x80, "");
                        }
                        clrUnitAlt = false;
                        clearUnitStates(commBuffer);
                        break;

                    }
	                    if (clrUnitAlt){
	                    	commBuffer.setTN5250Attr(gsElement.getAttribute());
	                        if (commBuffer.isDsp28x132() && (!clrUnitAlt) && 
	                            (commBuffer.getCommScreen().getScreenHeight() - commBuffer.getCommScreen().getMinScreenHeight()) != 0) {
                                 commBuffer.getCommScreen().setScreenHeight(commBuffer.getCommScreen().getMinScreenHeight()); //24
                                 commBuffer.getCommScreen().setScreenWidth(commBuffer.getCommScreen().getMinScreenWidth());   //80
                                 commBuffer.getCommScreen().addNewFieldFormat(ScreenElementType.Set24x80, "");
	                        }
	                        
							clrUnitAlt = false;
							clearUnitStates(commBuffer);
//							SimpleLogger.writeLine("Do5250DataStream", "CLEAR_UNIT operation", commBuffer.getConnectionData().isWriteLog());
							break;
                    }
                    break;
            case 0x40: //CLEAR_UNIT
//                	SimpleLogger.writeLine("Do5250DataStream", "CLEAR_UNIT operation", commBuffer.getConnectionData().isWriteLog());
                    commBuffer.setTN5250Attr(0x20);
                    if ((commBuffer.isDsp28x132()) && (!clrUnitAlt) && ((commBuffer.getCommScreen().getScreenHeight() - commBuffer.getCommScreen().getMinScreenHeight()) != 0)) {
                    	commBuffer.getCommScreen().setScreenHeight(commBuffer.getCommScreen().getMinScreenHeight()); // 24
                    	commBuffer.getCommScreen().setScreenWidth(commBuffer.getCommScreen().getMinScreenWidth()); // 80
                    	commBuffer.getCommScreen().addNewField(ScreenElementType.Set24x80, "");
                    }
                    clrUnitAlt = false;
                    clearUnitStates(commBuffer);
                    break;

            case 0x42: //READ_INPFLDS
//            		SimpleLogger.writeLine("Do5250DataStream", "READ_INPFLDS operation", commBuffer.getConnectionData().isWriteLog());
            		Comm5250ResponseBuilderUtils.UpdateFieldData(commBuffer, true);     // Force out any field data
            		commBuffer.getWTDCmdCtrl().set(0, commBuffer.getCurrentByte());
            		commBuffer.incReceiveDataPointer();
            		commBuffer.getWTDCmdCtrl().set(1, commBuffer.getCurrentByte());
            		commBuffer.incReceiveDataPointer();
            		Comm5250ResponseBuilderUtils.GetInputData(commBuffer, dsCmd);
                    break;
            case 0x50: //CLEAR_FMTTBL
//            		SimpleLogger.writeLine("Do5250DataStream", "CLEAR_FMTTBL operation", commBuffer.getConnectionData().isWriteLog());
            		commBuffer.getCommScreen().addNewFieldFormat(ScreenElementType.ClearInput, "");
                    // lockKeyboard ();
            		commBuffer.getCommScreen().setInpfldCount(0);
                    commBuffer.setFieldReseq(0);
                    commBuffer.setLastFieldLength(0);
                    commBuffer.setSFOrderCount(0);
                    commBuffer.setMasterMDT(false);
                    break;
            case 0x52: //READ_MDTFLDS:
//            		SimpleLogger.writeLine("Do5250DataStream", "READ_MDTFLDS operation", commBuffer.getConnectionData().isWriteLog());

            		Comm5250ResponseBuilderUtils.UpdateFieldData(commBuffer, true);     // Force out any field data
            		commBuffer.getWTDCmdCtrl().set(0, commBuffer.getCurrentByte());
            		commBuffer.incReceiveDataPointer();
            		commBuffer.getWTDCmdCtrl().set(1, commBuffer.getCurrentByte());
            		commBuffer.incReceiveDataPointer();
            		Comm5250ResponseBuilderUtils.GetInputData(commBuffer, dsCmd);
                    break;

            case 0x62: //READ_SCREEN:
                	// Send screen as is to AS/400
//            		SimpleLogger.writeLine("Do5250DataStream", "READ_SCREEN operation", commBuffer.getConnectionData().isWriteLog());
            		Do5250BinaryResponse(commBuffer, TN5250CommandValue.READ_SCREEN);
            		if(commBuffer.getAuxBuffer().size() > 0) {
            			commBuffer.socketFillSendbuf(commBuffer.getAuxBuffer().size());
            		}
                    // lockKeyboard ();
                    break;
            case 0x64: //READ_SCREEN_EA:
//            	SimpleLogger.writeLine("Do5250DataStream", "READ_SCREEN_EA operation", commBuffer.getConnectionData().isWriteLog());
                break;

            case 0x66: //READ_SCRTOPRT:
//            	SimpleLogger.writeLine("Do5250DataStream", "READ_SCRTOPRT operation", commBuffer.getConnectionData().isWriteLog());
                break;

            case 0x68: //READ_SCRTOPRT_EA:
//            	SimpleLogger.writeLine("Do5250DataStream", "READ_SCRTOPRT_EA operation", commBuffer.getConnectionData().isWriteLog());
                break;

            case 0x6A: //READ_SCRTOPRT_GL:
//            	SimpleLogger.writeLine("Do5250DataStream", "READ_SCRTOPRT_GL operation", commBuffer.getConnectionData().isWriteLog());
                break;

            case 0x6C: //READ_SCRTOPRT_EA_GL:
//            	SimpleLogger.writeLine("Do5250DataStream", "READ_SCRTOPRT_EA_GL operation", commBuffer.getConnectionData().isWriteLog());
                break;

            case 0x72: //READ_IMMEDIATE:
                // Send input data fields only
//            	SimpleLogger.writeLine("Do5250DataStream", "READ_IMMEDIATE operation", commBuffer.getConnectionData().isWriteLog());
        		Do5250BinaryResponse(commBuffer, TN5250CommandValue.SAVE_SCREEN);
        		if(commBuffer.getAuxBuffer().size() > 0) {
        			commBuffer.socketFillSendbuf(commBuffer.getAuxBuffer().size());
        		}
                // lockKeyboard ();
                break;
                
            case 0x82: //READ_MDTFLDS_ALT:
//            	SimpleLogger.writeLine("Do5250DataStream", "READ_MDTFLDS_ALT operation", commBuffer.getConnectionData().isWriteLog());
                break;

            case 0x83: //READ_IMMEDIATE_ALT:
//            	SimpleLogger.writeLine("Do5250DataStream", "READ_IMMEDIATE_ALT operation", commBuffer.getConnectionData().isWriteLog());
                break;

            case 0xF3: //WRITE_STRUCTFLD:
//            	SimpleLogger.writeLine("Do5250DataStream", "WRITE_STRUCTFLD operation", commBuffer.getConnectionData().isWriteLog());
                DoWSFOrders(commBuffer);
                break;

            case 0xF4: //WRITE_STRUCTFLD_SNGL:
//            	SimpleLogger.writeLine("Do5250DataStream", "WRITE_STRUCTFLD_SNGL operation", commBuffer.getConnectionData().isWriteLog());
                break;
        }
	}

	/**
	 * Generates the 5250 response  
	 * It is call by DoTN5250Header
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @param  commBuffer commBuffer CommBufferLogic object
	 * @param  tn5250CommandValue the 5250 command
	 * 
	 * @return 	the size of the buffer
	 * @see CommBufferLogic
	 */    
	public static Integer Do5250BinaryResponse(CommBufferLogic commBuffer, Integer tn5250CommandValue) {
        char EBCDICBLANK = '@';       // 0x40
        char EBCDICMINUS = '`';       // 0x60
        int returnVal = 1000;

        Integer tempVal;
        Integer idx, jdx;
        Integer rsplen = 0;
        Integer scrDataSz = commBuffer.getCommScreen().getScreenHeight() * commBuffer.getCommScreen().getScreenWidth();
        Integer scrElemSz = (commBuffer.getCommScreen().getScreenHeight() + 1) * commBuffer.getCommScreen().getScreenWidth();

        commBuffer.setSaveScreen(new SaveScreen());

        boolean nodata = false;
        Integer tempInt = 0x20;

        commBuffer.getAuxBuffer().clear();
        // Setup Binary Response Layout
        commBuffer.getAuxBuffer().add(TelnetCommandValue.NULLCHAR); // Rlen 12A0 Reserved
        commBuffer.getAuxBuffer().add(TelnetCommandValue.NULLCHAR); // Rlen = total record length - FFEF

        commBuffer.getAuxBuffer().add(0x12); // Record type
        commBuffer.getAuxBuffer().add(0xA0); // Record type

        commBuffer.getAuxBuffer().add(TelnetCommandValue.NULLCHAR); // Reserved
        commBuffer.getAuxBuffer().add(TelnetCommandValue.NULLCHAR); // Reserved

        commBuffer.getAuxBuffer().add(0x04); // 04 Flags Reserved Opcode
        commBuffer.getAuxBuffer().add(TelnetCommandValue.NULLCHAR);
        // Bit0 = Negative Response Code
        //        Sent as data after Opcode
        // Bit1 = Attention key
        if ((tn5250CommandValue - TelnetCommandValue.ATTN_KEY) == 0) {
        	commBuffer.getAuxBuffer().set(7, 0x40);
        }
        // Bit5 = System Request key
        if ((tn5250CommandValue - TelnetCommandValue.SYSRQS_KEY) == 0) {
        	commBuffer.getAuxBuffer().set(7, 0x04);
        }
        // Bit7 = Help in Error State
        commBuffer.getAuxBuffer().add(TelnetCommandValue.NULLCHAR); // Reserved
        commBuffer.getAuxBuffer().add(TelnetCommandValue.NULLCHAR); // Opcode 
        // data FFEF (End of Rec)
        
        if (((tn5250CommandValue - TN5250CommandValue.READ_MDTFLDS) == 0) || 
            ((tn5250CommandValue - TN5250CommandValue.READ_INPFLDS) == 0)) {
            // Cursor R,C | AID | field [1..n]
        	commBuffer.getAuxBuffer().add(commBuffer.getCurrentRow());
        	commBuffer.getAuxBuffer().add(commBuffer.getCurrentCol());
        	commBuffer.getAuxBuffer().add(commBuffer.getTN5250AidCode());
        }

        if (commBuffer.getTN5250Cmdkeys() != 0) { // Check for no data returned
            nodata = (commBuffer.getTN5250Cmdkeys() & commBuffer.getBBCOMAidKey()) == commBuffer.getBBCOMAidKey();
        }
        if ((tn5250CommandValue - TN5250CommandValue.READ_SCREEN) == 0) {
            for (idx = 0; idx < scrDataSz; idx++) {
            	commBuffer.getAuxBuffer().add(commBuffer.getCommScreen().getScreenElements()[idx].getValue());
            }
        }
        else if ((tn5250CommandValue - TN5250CommandValue.RESTORE_SCREEN) == 0) {
            Integer scrWidth = commBuffer.getCurrentByte();
            Integer scrDepth = commBuffer.getNextByte();
            boolean restoreScreenSizeChanged = false;
            if (scrWidth != commBuffer.getCommScreen().getScreenWidth()) {
            	commBuffer.getCommScreen().setScreenWidth(scrWidth);
            	commBuffer.getCommScreen().setScreenHeight(scrDepth);
                restoreScreenSizeChanged = true;
                scrElemSz = (commBuffer.getCommScreen().getScreenHeight() + 1) * commBuffer.getCommScreen().getScreenWidth();
            }

            commBuffer.getSaveScreen().setTN5250Cmdkeys(DataLowLevelUtils.GetLong(commBuffer.getReceiveData(), 
            											commBuffer.getReceiveDataPointer() + 2));
            commBuffer.getSaveScreen().setCurrentRow(DataLowLevelUtils.GetShort(commBuffer.getReceiveData(), 
													 commBuffer.getReceiveDataPointer() + 6));

            commBuffer.getSaveScreen().setCurrentCol(DataLowLevelUtils.GetShort(commBuffer.getReceiveData(), 
					 								 commBuffer.getReceiveDataPointer() + 8));

            commBuffer.getSaveScreen().setHomeRow(DataLowLevelUtils.GetShort(commBuffer.getReceiveData(), 
					 							  commBuffer.getReceiveDataPointer() + 10));
            commBuffer.getSaveScreen().setHomeCol(DataLowLevelUtils.GetShort(commBuffer.getReceiveData(), 
					  							  commBuffer.getReceiveDataPointer() + 12));

            commBuffer.getSaveScreen().setInpfldCount(DataLowLevelUtils.GetShort(commBuffer.getReceiveData(), 
					  							      commBuffer.getReceiveDataPointer() + 14));

            commBuffer.getSaveScreen().setFieldReseq(DataLowLevelUtils.GetShort(commBuffer.getReceiveData(), 
				                                     commBuffer.getReceiveDataPointer() + 16));

            commBuffer.getSaveScreen().setSFOrderCount(DataLowLevelUtils.GetShort(commBuffer.getReceiveData(), 
                                                       commBuffer.getReceiveDataPointer() + 18));
            if(commBuffer.getReceiveData().get(commBuffer.getReceiveDataPointer() + 20) == 0xC) {
            	commBuffer.getSaveScreen().setMasterMDT(true);
            }
            else {
            	//Originally said true but should be false - LDP
            	commBuffer.getSaveScreen().setMasterMDT(false);
            }
            
            commBuffer.incReceiveDataPointer(26);
            
            commBuffer.setTN5250Cmdkeys(commBuffer.getSaveScreen().getTN5250Cmdkeys());
            commBuffer.setCurrentRow(commBuffer.getSaveScreen().getCurrentRow());
            commBuffer.setCurrentCol(commBuffer.getSaveScreen().getCurrentCol());
            commBuffer.setHomeRow(commBuffer.getSaveScreen().getHomeRow());
            commBuffer.setHomeCol(commBuffer.getSaveScreen().getHomeCol());
            commBuffer.getCommScreen().setInpfldCount(commBuffer.getSaveScreen().getInpfldCount());
            commBuffer.setFieldReseq(commBuffer.getSaveScreen().getFieldReseq());
            commBuffer.setMasterMDT(commBuffer.getSaveScreen().isMasterMDT());
            commBuffer.setSFOrderCount(commBuffer.getSaveScreen().getSFOrderCount());

            for (idx = 0; idx <= commBuffer.getSFOrderCount(); idx++) {
            	commBuffer.getCommScreen().getFieldFmtTblPosn()[idx] = commBuffer.getReceiveData().get(commBuffer.getReceiveDataPointer());
            	commBuffer.incReceiveDataPointer();
            }

            StringBuilder buffer = new StringBuilder();
            StringBuilder ASCIIbuffer = new StringBuilder();
            buffer.append("{INPUTSDESCRIPTION}");

            if (commBuffer.getCommScreen().getInpfldCount() > 0) {
                for (idx = 0; idx < commBuffer.getCommScreen().getInpfldCount(); idx++) {
                	if (commBuffer.getFormatTable()[idx] == null) {
                		commBuffer.getFormatTable()[idx] = new FormatTable();
                	}                	
                	commBuffer.getFormatTable()[idx].setBeginRow(DataLowLevelUtils.GetShort(commBuffer.getReceiveData(), commBuffer.getCurrentByte()));
                	commBuffer.getFormatTable()[idx].setBeginCol(DataLowLevelUtils.GetShort(commBuffer.getReceiveData(), commBuffer.getCurrentByte() + 2));
                	commBuffer.getFormatTable()[idx].setFieldLen(DataLowLevelUtils.GetShort(commBuffer.getReceiveData(), commBuffer.getCurrentByte() + 4));
                	commBuffer.getFormatTable()[idx].setNextField(DataLowLevelUtils.GetShort(commBuffer.getReceiveData(), commBuffer.getCurrentByte() + 6));
                	commBuffer.getFormatTable()[idx].getFFW()[0] = commBuffer.getReceiveData().get(commBuffer.getCurrentByte() + 8);
                	commBuffer.getFormatTable()[idx].getFFW()[1] = commBuffer.getReceiveData().get(commBuffer.getCurrentByte() + 12);
                	commBuffer.getFormatTable()[idx].getFFW()[0] = commBuffer.getReceiveData().get(commBuffer.getCurrentByte() + 16);
                	commBuffer.getFormatTable()[idx].getFFW()[1] = commBuffer.getReceiveData().get(commBuffer.getCurrentByte() + 20);
                	commBuffer.getFormatTable()[idx].setGuiDefined(DataLowLevelUtils.GetBoolean(commBuffer.getReceiveData().get(commBuffer.getCurrentByte() + 24)));
                	
                	
                    buffer.append(DataLowLevelUtils.PadLeftZeros(Integer.toHexString(commBuffer.getFormatTable()[idx].getBeginRow()), 4));
                    buffer.append(DataLowLevelUtils.PadLeftZeros(Integer.toHexString(commBuffer.getFormatTable()[idx].getBeginCol()), 4));
                    buffer.append(DataLowLevelUtils.PadLeftZeros(Integer.toHexString(commBuffer.getFormatTable()[idx].getFieldLen()), 4));
                    buffer.append(DataLowLevelUtils.PadLeftZeros(Integer.toHexString(commBuffer.getFormatTable()[idx].getNextField()), 4));
                    buffer.append(DataLowLevelUtils.PadLeftZeros(Integer.toHexString(commBuffer.getFormatTable()[idx].getFFW()[0]), 2));
                    buffer.append(DataLowLevelUtils.PadLeftZeros(Integer.toHexString(commBuffer.getFormatTable()[idx].getFFW()[1]), 2));
                    buffer.append(DataLowLevelUtils.PadLeftZeros(Integer.toHexString(commBuffer.getFormatTable()[idx].getFFW()[0]), 2));
                    buffer.append(DataLowLevelUtils.PadLeftZeros(Integer.toHexString(commBuffer.getFormatTable()[idx].getFFW()[1]), 2));

                    commBuffer.incReceiveDataPointer(28);
                }
            }

            idx = 0;

            buffer.append("{SCREENDESCRIPTION}");

            for (jdx = 0; jdx < scrElemSz; jdx++) {
                commBuffer.getCommScreen().getScreenElements()[jdx].setValue(0);
                commBuffer.getCommScreen().getScreenElements()[jdx].setAttribute(0);
            }

            while (idx < scrElemSz) {
                tempVal = commBuffer.getCurrentByte();
                commBuffer.incReceiveDataPointer();

                if ((tempVal >= 0x20) && (tempVal <= 0x3F)) {
                	tempInt = tempVal;
                }

                commBuffer.getCommScreen().getScreenElements()[idx].setValue(tempVal);
                commBuffer.getCommScreen().getScreenElements()[idx].setAttribute(tempInt);
                idx += 1;
            }

            Integer lastAttr = 0;
            for (jdx = 0; jdx < scrElemSz; jdx++) {
                if (lastAttr != commBuffer.getCommScreen().getScreenElements()[jdx].getAttribute()) {
                    buffer.append("[");
                    buffer.append(DataLowLevelUtils.PadLeftZeros(Integer.toHexString(commBuffer.getCommScreen().getScreenElements()[jdx].getAttribute()), 2));
                    buffer.append("]");
                    lastAttr = commBuffer.getCommScreen().getScreenElements()[jdx].getAttribute();
                }
                else 
                {
                	EncodeUtils eu = new EncodeUtils();
                    Integer auxChar =  eu.doEbcdicToAscii(commBuffer, commBuffer.getCommScreen().getScreenElements()[jdx].getValue());
                    if (auxChar > 127) {
                    	auxChar = 0;
                    }
                    buffer.append(DataLowLevelUtils.PadLeftZeros(Integer.toHexString(auxChar), 2));
                    ASCIIbuffer.append(auxChar);
                }                    
            }

            commBuffer.getCommScreen().addNewFieldFormat(ScreenElementType.BeginRestore, buffer.toString());

            if (restoreScreenSizeChanged) {
                if (commBuffer.getCommScreen().getScreenWidth() > 130) {
                	commBuffer.getCommScreen().addNewFieldFormat(ScreenElementType.Set27x132, "");
                }
                else {
                	commBuffer.getCommScreen().addNewFieldFormat(ScreenElementType.Set24x80, "");
                }
            }
            returnVal = 0;
        }
        else if (tn5250CommandValue == TN5250CommandValue.SAVE_SCREEN) {
            rsplen = 0;
            commBuffer.getAuxBuffer().add(TelnetCommandValue.DS5250_ESCAPE);
            commBuffer.getAuxBuffer().add(TN5250CommandValue.RESTORE_SCREEN);
            commBuffer.getAuxBuffer().add(commBuffer.getCommScreen().getScreenWidth());
            commBuffer.getAuxBuffer().add(commBuffer.getCommScreen().getScreenHeight());
            
            commBuffer.getSaveScreen().setTN5250Cmdkeys(commBuffer.getTN5250Cmdkeys());
            commBuffer.getSaveScreen().setCurrentRow(commBuffer.getCurrentRow());
            commBuffer.getSaveScreen().setCurrentCol(commBuffer.getCurrentCol());
            commBuffer.getSaveScreen().setHomeRow(commBuffer.getHomeRow());
            commBuffer.getSaveScreen().setHomeCol(commBuffer.getHomeCol());
            commBuffer.getSaveScreen().setInpfldCount(commBuffer.getCommScreen().getInpfldCount());
            commBuffer.getSaveScreen().setFieldReseq(commBuffer.getFieldReseq());
            commBuffer.getSaveScreen().setSFOrderCount(commBuffer.getSFOrderCount());
            commBuffer.getSaveScreen().setMasterMDT(commBuffer.isMasterMDT());

            commBuffer.getAuxBuffer().add(commBuffer.getSaveScreen().getTN5250Cmdkeys() & 0xff);
            commBuffer.getAuxBuffer().add((commBuffer.getSaveScreen().getTN5250Cmdkeys() & 0xff00) >> 8);
            commBuffer.getAuxBuffer().add((commBuffer.getSaveScreen().getTN5250Cmdkeys() & 0xff0000) >> 16);
            commBuffer.getAuxBuffer().add((commBuffer.getSaveScreen().getTN5250Cmdkeys() & 0xff000000) >> 24);

            commBuffer.getAuxBuffer().add((commBuffer.getSaveScreen().getCurrentRow() & 0xff));
            commBuffer.getAuxBuffer().add((commBuffer.getSaveScreen().getCurrentRow() & 0xff00) >> 8);

            commBuffer.getAuxBuffer().add((commBuffer.getSaveScreen().getCurrentCol() & 0xff));
            commBuffer.getAuxBuffer().add((commBuffer.getSaveScreen().getCurrentCol() & 0xff00) >> 8);

            commBuffer.getAuxBuffer().add((commBuffer.getSaveScreen().getHomeRow() & 0xff));
            commBuffer.getAuxBuffer().add((commBuffer.getSaveScreen().getHomeRow() & 0xff00) >> 8);
            
            commBuffer.getAuxBuffer().add((commBuffer.getSaveScreen().getHomeCol() & 0xff));
            commBuffer.getAuxBuffer().add((commBuffer.getSaveScreen().getHomeCol() & 0xff00) >> 8);

            commBuffer.getAuxBuffer().add((commBuffer.getSaveScreen().getInpfldCount() & 0xff));
            commBuffer.getAuxBuffer().add((commBuffer.getSaveScreen().getInpfldCount() & 0xff00) >> 8);

            commBuffer.getAuxBuffer().add((commBuffer.getSaveScreen().getFieldReseq() & 0xff));
            commBuffer.getAuxBuffer().add((commBuffer.getSaveScreen().getFieldReseq() & 0xff00) >> 8);

            commBuffer.getAuxBuffer().add((commBuffer.getSaveScreen().getSFOrderCount() & 0xff));
            commBuffer.getAuxBuffer().add((commBuffer.getSaveScreen().getSFOrderCount() & 0xff00) >> 8);

            if (!commBuffer.getSaveScreen().isMasterMDT()) {
            	commBuffer.getAuxBuffer().add(0xC);
            }
            else {
            	commBuffer.getAuxBuffer().add(0);
            }
            
        	commBuffer.getAuxBuffer().add(0);
        	commBuffer.getAuxBuffer().add(0);
        	commBuffer.getAuxBuffer().add(0);
        	commBuffer.getAuxBuffer().add(0);
        	commBuffer.getAuxBuffer().add(0);

            for (idx = 0; idx <= commBuffer.getSFOrderCount(); idx++) {
            	commBuffer.getAuxBuffer().add(commBuffer.getFieldFmtTblPosn()[idx]);
            }

            if (commBuffer.getCommScreen().getInpfldCount() > 0) {
                for (idx = 0; idx < commBuffer.getCommScreen().getInpfldCount(); idx++) {
                	commBuffer.getAuxBuffer().add(commBuffer.getFormatTable()[idx].getBeginRow() & 0xFF);
                	commBuffer.getAuxBuffer().add((commBuffer.getFormatTable()[idx].getBeginRow() >> 8) & 0xFF);

                	commBuffer.getAuxBuffer().add(commBuffer.getFormatTable()[idx].getBeginCol() & 0xFF);
                	commBuffer.getAuxBuffer().add((commBuffer.getFormatTable()[idx].getBeginCol() >> 8) & 0xFF);

                	commBuffer.getAuxBuffer().add(commBuffer.getFormatTable()[idx].getFieldLen() & 0xFF);
                	commBuffer.getAuxBuffer().add((commBuffer.getFormatTable()[idx].getFieldLen() >> 8) & 0xFF);

                	commBuffer.getAuxBuffer().add(commBuffer.getFormatTable()[idx].getNextField() & 0xFF);
                	commBuffer.getAuxBuffer().add((commBuffer.getFormatTable()[idx].getNextField() >> 8) & 0xFF);

                	commBuffer.getAuxBuffer().add(commBuffer.getFormatTable()[idx].getFFW()[0]);
                	commBuffer.getAuxBuffer().add(0);
                	commBuffer.getAuxBuffer().add(0);
                	commBuffer.getAuxBuffer().add(0);
                	
                	commBuffer.getAuxBuffer().add(commBuffer.getFormatTable()[idx].getFFW()[1]);
                	commBuffer.getAuxBuffer().add(0);
                	commBuffer.getAuxBuffer().add(0);
                	commBuffer.getAuxBuffer().add(0);

                	commBuffer.getAuxBuffer().add(commBuffer.getFormatTable()[idx].getFCW()[0]);
                	commBuffer.getAuxBuffer().add(0);
                	commBuffer.getAuxBuffer().add(0);
                	commBuffer.getAuxBuffer().add(0);
                	
                	commBuffer.getAuxBuffer().add(commBuffer.getFormatTable()[idx].getFCW()[1]);
                	commBuffer.getAuxBuffer().add(0);
                	commBuffer.getAuxBuffer().add(0);
                	commBuffer.getAuxBuffer().add(0);

                	int value = 0;
                	if(commBuffer.getFormatTable()[idx].isGuiDefined()) { value = 1; }
                	commBuffer.getAuxBuffer().add(value);
                	commBuffer.getAuxBuffer().add(0);
                	commBuffer.getAuxBuffer().add(0);
                	commBuffer.getAuxBuffer().add(0);
                }
            }
            
            idx = 0;
            while (idx < scrElemSz) {
            	tempVal = commBuffer.getCommScreen().getScreenElements()[idx].getValue();
                idx += 1;
                commBuffer.getAuxBuffer().add(tempVal);
            }
        }
        else if (((commBuffer.isMasterMDT()) && (!nodata)) || (tn5250CommandValue == TN5250CommandValue.READ_IMMEDIATE))
            for (Integer fno = 1; fno <= commBuffer.getSFOrderCount(); fno++)
            {
                if ((fno == 1) && (commBuffer.getFieldReseq() > 0)) {
                    idx = commBuffer.getFieldReseq();
                }
                else if (commBuffer.getFieldReseq() > 0) {
                    idx = commBuffer.getFormatTable()[fno].getNextField();
                }
                else {
                    idx = fno;
                }
                
                if (idx > 0)
                    idx = commBuffer.getFieldFmtTblPosn()[idx];
                idx -= 1;
                if (idx > commBuffer.getCommScreen().MAX_INP_FIELDS) {
                    // Invalid Resequencing error 0x1005 0x01 0x03
                    Do5250NegativeResponse(commBuffer, 0x10050103);
                }
                
                if (((tn5250CommandValue == TN5250CommandValue.READ_MDTFLDS) &&
                    ((commBuffer.getFormatTable()[idx].getFFW()[0] & 0x08) > 0)) ||
                    (tn5250CommandValue == TN5250CommandValue.READ_IMMEDIATE)) {
                    commBuffer.getAuxBuffer().add(TN5250CommandValue.SBA_ORDER);
                    if(commBuffer.getFormatTable()[idx] != null) {
	                    commBuffer.getAuxBuffer().add(DataLowLevelUtils.GetLowByte( commBuffer.getFormatTable()[idx].getBeginRow() ));
	                    commBuffer.getAuxBuffer().add(DataLowLevelUtils.GetLowByte( commBuffer.getFormatTable()[idx].getBeginCol() ));
                    }
                }
                if ((tn5250CommandValue != TN5250CommandValue.READ_MDTFLDS) ||
                    ((tn5250CommandValue == TN5250CommandValue.READ_MDTFLDS) &&
                    ((commBuffer.getFormatTable()[idx].getFFW()[0] & 0x08) > 0))) // MDT
                {
                    Integer scrPosnBgn = commBuffer.getCommScreen().screenPosn(commBuffer.getFormatTable()[idx].getBeginRow(),
                    		commBuffer.getFormatTable()[idx].getBeginCol());
                    Integer scrPosnEnd = commBuffer.getCommScreen().screenPosn(commBuffer.getFormatTable()[idx].getBeginRow(),
                    		commBuffer.getFormatTable()[idx].getBeginCol() +
                    		commBuffer.getFormatTable()[idx].getFieldLen() - 1);

                    for (jdx = scrPosnBgn; jdx <= scrPosnEnd; jdx++) {
	                    Integer tempLoc = commBuffer.getCommScreen().getScreenElements()[jdx - 1].getValue();
	                    if (((commBuffer.getFormatTable()[idx].getFFW()[0] & 0x07) == 0x07) &&
	                        (jdx == scrPosnEnd)) {
	                    	// Signed -- change zone or skip
	                        if (tempLoc == EBCDICMINUS)
	                        {
	                            tempLoc = commBuffer.getAuxBuffer().get(commBuffer.getAuxBuffer().size() - 1);
	                            tempLoc = tempLoc & 0xDF;
	                            commBuffer.getAuxBuffer().set(commBuffer.getAuxBuffer().size() - 1, tempLoc);
	                        }
	                    }
	                    else
	                        if (((commBuffer.getFormatTable()[idx].getFFW()[0] & 0x07) == 0x03) &&
	                        		(jdx == scrPosnEnd)) {
	                        	// Numeric -- change zone if possible
	                            if (tempLoc == EBCDICMINUS) {
	                                tempLoc =  commBuffer.getAuxBuffer().get(commBuffer.getAuxBuffer().size() - 1);
	                                if ((tempLoc >= 0xF0) && (tempLoc <= 0xF9)) {
	                                    tempLoc = tempLoc & 0xDF;
	                                    commBuffer.getAuxBuffer().set(commBuffer.getAuxBuffer().size() - 1, tempLoc);
	                                }                                    
	                                else {
	                                	commBuffer.getAuxBuffer().add(Integer.getInteger(String.valueOf(EBCDICMINUS)));
	                                    returnVal++;
	                                    rsplen++;
	                                }
	                            }
	                            else {
	                                if ((tempLoc - TelnetCommandValue.NULLCHAR) == 0)
	                                    tempLoc = Integer.getInteger(String.valueOf(EBCDICBLANK));
	                                	commBuffer.getAuxBuffer().add(tempLoc);
	                                    returnVal++;
	                                    rsplen++;
	                            }
	                        }
	                        else
	                        {
	                            if ((tempLoc - TelnetCommandValue.NULLCHAR) == 0)
	                                tempLoc = Integer.getInteger(String.valueOf(EBCDICBLANK));
	
	                            commBuffer.getAuxBuffer().add(tempLoc);
	                            returnVal++;
	                            rsplen++;
	                        }
                    }
                }
            }
        
        if (returnVal > 0) {
        	// Total response length
        	commBuffer.getAuxBuffer().set(0, DataLowLevelUtils.GetHiByte(commBuffer.getAuxBuffer().size() - 2));
        	commBuffer.getAuxBuffer().set(1, DataLowLevelUtils.GetLowByte(commBuffer.getAuxBuffer().size() - 2));
        }

        return commBuffer.getAuxBuffer().size();
	}
	
	/**
	 * Performs the write to display orders  
	 * It is call by Do5250DataStream
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @param  commBuffer CommBufferLogic object
	 * 
	 * @see CommBufferLogic
	 */    
	public static void DoWTDOrders(CommBufferLogic commBuffer) {
        Integer wtdOrders;
        Integer temp;
        Integer wordTemp;
        boolean done = false;
        boolean haveCmdkeys = false;        
        Integer scrDataSz = commBuffer.getCommScreen().getScreenHeight()* commBuffer.getCommScreen().getScreenWidth();

        wtdOrders = commBuffer.getCurrentByte();
        commBuffer.incReceiveDataPointer();        
        
    	while (!done) {
    		
            switch (wtdOrders) {
            
                case 0x01: //SOH_ORDER:             // Start of Header
                    	// Len Flag 0x00 Reseq Errline CmdKey(3)
                        Integer[] cmdKeys = new Integer[4];
                        Integer length = commBuffer.getCurrentByte();
                        commBuffer.incReceiveDataPointer();

                        commBuffer.getCommScreen().addNewFieldFormat(ScreenElementType.ClearInput, "");

                        commBuffer.getCommScreen().setInpfldCount(0);
                        commBuffer.setFieldReseq(0);
                        commBuffer.setLastFieldLength(0);
                        commBuffer.setSFOrderCount(0);
                        commBuffer.setHaveSOH(true);
                        commBuffer.setMasterMDT(false);
                        commBuffer.setHomePosn(false);

                        if ((length == 0) || (length > 7)) {
                            // Invalid SOH length 0x1005 0x01 0x2B
                            Do5250NegativeResponse(commBuffer,0x1005012B);
                        }
                        if (length == 7) {
                            haveCmdkeys = true;
                        }

                        for (Integer idx = 0; idx < length; idx++) {
                            temp = commBuffer.getCurrentByte();
                            commBuffer.incReceiveDataPointer();
                            if (temp == 0xFF) { // Need two for one (TCP/IP Protocol)
                                commBuffer.incReceiveDataPointer();
                            }
                            
                            if (idx == 0) {
                                //sohFlag = temp;
                            }
                            else if (idx == 2) {
                                commBuffer.setFieldReseq(temp);
                            }
                            else if (idx == 3) {
                            	commBuffer.setErrorLine(temp);
                                if (commBuffer.getErrorLine() > commBuffer.getCommScreen().getScreenHeight() + 1) {
                                    commBuffer.setErrorLine(commBuffer.getCommScreen().getScreenHeight() + 1);
                                }
                            }
                            else if (idx >= 4) {
                                cmdKeys[idx - 3] = temp;
                            }
                        }
                        if (haveCmdkeys) {      // Cmdkey BITON, only cursor address and
                        					  // AID is returned, else field data also
                            cmdKeys[0] = 0;   // bytes 5..7 = Keys 24..17, 16..9, 8..1
                            commBuffer.setTN5250Cmdkeys(DataLowLevelUtils.MAKELONG(
				                            		DataLowLevelUtils.MAKEWORD(cmdKeys[3], cmdKeys[2]),
				                            		DataLowLevelUtils.MAKEWORD(cmdKeys[1], cmdKeys[0])));
                            
                            if (commBuffer.getTN5250Cmdkeys() > 0) {
                            	commBuffer.getCommScreen().addNewFieldKeys(0x00, commBuffer.getTN5250Cmdkeys());
                            }
                        }
                        break;
                case 0x02: //RA_ORDER:              // Repeat (character) to Address
                    	// Row Col RepeatChar
                		Integer raRow = DataLowLevelUtils.MAKEWORD(commBuffer.getCurrentByte(), 0);
                        commBuffer.incReceiveDataPointer();
                        Integer raCol = DataLowLevelUtils.MAKEWORD(commBuffer.getCurrentByte(), 0);
                        commBuffer.incReceiveDataPointer();
                        Integer rptchar = commBuffer.getCurrentByte();
                        commBuffer.incReceiveDataPointer();
                        Integer rptPosn = commBuffer.getCommScreen().screenPosn(raRow, raCol);
                        if(commBuffer.getCurrentRow() > raRow) {commBuffer.setCurrentRow(raRow); }
                        Integer scrPosn = commBuffer.getCommScreen().screenPosn(commBuffer.getCurrentRow(), 
                        														commBuffer.getCurrentCol());

                        if ((rptPosn == 0) ||
                            (rptPosn > scrDataSz) ||
                            (rptPosn < scrPosn)) {
                            // Invalid RA position 0x1005 0x01 0x23
                            Do5250NegativeResponse(commBuffer, 0x10050123);
                        }
                        
                        if ((commBuffer.getCurrentCol() <= 2) &&
                            ((rptchar - TelnetCommandValue.NULLCHAR) == 0) &&
                            ((rptPosn - scrPosn + 1) >= (commBuffer.getCommScreen().getScreenWidth() - 1))) {
                        	// Clear rows -- save Col1 element
                        	GreenScreenElement saveElem = new GreenScreenElement(
                                commBuffer.getCommScreen().getScreenElements()[scrPosn - 2].getValue(),
                                commBuffer.getCommScreen().getScreenElements()[scrPosn - 2].getAttribute());

                            for (Integer idx = scrPosn; idx <= rptPosn; idx++) {
                            	commBuffer.getCommScreen().getScreenElements()[idx - 1] = 
                            				new GreenScreenElement(rptchar, saveElem.getAttribute());
                            }
                            
                            commBuffer.getCommScreen().addNewFieldScroll(
                            											 commBuffer.getCurrentRow(), 
                            											 commBuffer.getCurrentCol() - 1, 
                            											 raRow, 
                            											 raCol);
                            commBuffer.setCurrentRow(raRow);
                            commBuffer.setCurrentCol(raCol);
                            Comm5250ResponseBuilderUtils.UpdateCurrentCol(commBuffer, 1);
                        }
                        else {
                            Integer nbrRepeats = rptPosn - scrPosn + 1;
	                            if ((rptchar - TelnetCommandValue.NULLCHAR) == 0) { // Need text field for SFL data
	                            	commBuffer.setRptNull(true);
	                                temp = commBuffer.getCurrentByte();
	                                commBuffer.setTextField((temp >= 0x20) && (temp <= 0x3F));

			    		            if (commBuffer.isTextField()) { // TextField == RA RowCol 0 @ WTD_Order
	                                    temp = commBuffer.getNextByte(); 
	                                    commBuffer.setTextField(temp < 0x20);
	                                }
	                            }

	    		            for (Integer idx = 0; idx < nbrRepeats; idx++) {
                            	Comm5250ResponseBuilderUtils.InsertScrElem(commBuffer, rptchar, false);
                            }
                        }
                        break;
                case 0x03: //EA_ORDER:              // Erase (attribute) to Address
                        // Row Col Length AttrList
                        // NOT operational (not allowed yet)
                        Integer eaRow = DataLowLevelUtils.MAKEWORD(commBuffer.getCurrentByte(), 0);
                        commBuffer.incReceiveDataPointer();
                        Integer eaCol = DataLowLevelUtils.MAKEWORD(commBuffer.getCurrentByte(), 0);
                        commBuffer.incReceiveDataPointer();
                        Integer locLength = DataLowLevelUtils.MAKEWORD(commBuffer.getCurrentByte(), 0);
                        commBuffer.incReceiveDataPointer();
                        for (Integer idx = 0; idx < locLength; idx++) {
                            temp = commBuffer.getCurrentByte();
                            commBuffer.incReceiveDataPointer();
                        }
                        commBuffer.setCurrentRow(eaRow);
                        commBuffer.setCurrentCol(eaCol);
                        Comm5250ResponseBuilderUtils.UpdateCurrentCol(commBuffer, 1);
                        break;
                case 0x10: //TD_ORDER:              // Transparent Data
                        // DataLength(2) Data
                        // NOT operational (not allowed yet)
                        if ((commBuffer.getReceiveCount() - commBuffer.getReceiveDataPointer()) < 2) {
                            // Invalid data stream 0x1005 0x01 0x21
                            Do5250NegativeResponse(commBuffer, 0x10050121);
                        }

                        wordTemp = commBuffer.getCurrentByte() * 0xFF + commBuffer.getNextByte(); 
                        commBuffer.incReceiveDataPointer(2);

                        if ((commBuffer.getReceiveDataPointer() + wordTemp) > commBuffer.getReceiveCount()) {
                            // Invalid data stream 0x1005 0x01 0x21
                            Do5250NegativeResponse(commBuffer, 0x10050121);
                        }
                        
                        if ((commBuffer.getCommScreen().screenPosn(commBuffer.getCurrentRow(), commBuffer.getCurrentCol()) + wordTemp) > scrDataSz) {
                            // Invalid data stream 0x1005 0x01 0x21
                            Do5250NegativeResponse(commBuffer, 0x10050121);
                        }
                        for (Integer w = 0; w < wordTemp; w++) {
                            Comm5250ResponseBuilderUtils.InsertScrElem(commBuffer,
                            										   commBuffer.getCurrentByte(), 
                            										   true);
                            commBuffer.incReceiveDataPointer();
                        }
                        break;
                case 0x11: //SBA_ORDER:             // Set Buffer Address
                        // Row Col
                        Integer curPosn = commBuffer.getCommScreen().screenPosn(commBuffer.getCurrentRow(), commBuffer.getCurrentCol());
                        commBuffer.setSBARow(commBuffer.getCurrentByte());
                        commBuffer.incReceiveDataPointer();
                        commBuffer.setSBACol(commBuffer.getCurrentByte());
                        commBuffer.incReceiveDataPointer();
                        
                        if ((commBuffer.getSBARow() < 1) || (commBuffer.getSBARow() > commBuffer.getCommScreen().getScreenHeight()) ||
                            (commBuffer.getSBACol() < 1) || (commBuffer.getSBACol() > commBuffer.getCommScreen().getScreenWidth())) {
                            // Invalid RowCol length 0x1005 0x01 0x22
                            Do5250NegativeResponse(commBuffer, 0x10050122);
                        }
                        /**/
                        //This looks as InfiniteSeries specific
                        if ((!commBuffer.isHaveSOH()) && (commBuffer.getCommScreen().getInpfldCount() > 0)) {                                    // Unibol -- Window border is output
                            Integer x = commBuffer.getReceiveDataPointer();          // prior to SOH command causing grief
                            while (x < commBuffer.getReceiveCount())         // with recognition logic as input
                            {                                // field count may be non-zero.  See
                                temp = commBuffer.getReceiveData().get(x); // insertScrElem and updateFieldData
                                if ((temp == 0xFF) &&
                                    (commBuffer.getNextByte() == 0xEF)) {
                                    break;                            // In FTTSOH.C
                                }
                                else if ((temp == 0x01) && // lbuff[0] = FTT_ORD_SOH
                                    (commBuffer.getReceiveData().get(x + 1) == 0x07) && // lbuff[1] = 7
                                    (commBuffer.getReceiveData().get(x + 2) == 0x00) && // lbuff[2] = 0
                                    (commBuffer.getReceiveData().get(x + 3) == 0x00) && // lbuff[3] = 0
                                    (commBuffer.getReceiveData().get(x + 4) == 0x00) && // lbuff[4] = 0 ...
                                    (commBuffer.getReceiveData().get(x - 1) != 0x11) && // lbuff[8] = (a_ckey & 0x000000FF)
                                    (commBuffer.getReceiveData().get(x - 2) != 0x11)) {
                                    
                                	commBuffer.getCommScreen().setInpfldCount(0);
                                	commBuffer.setHaveSOH(true);
                                    break;
                                }
                                else {
                                    x += 1;
                                }
                            }
                        }
                        commBuffer.setCurrentRow(commBuffer.getSBARow());
                        commBuffer.setCurrentCol(commBuffer.getSBACol());
                        temp = commBuffer.getCurrentByte();
                        if (((temp - TelnetCommandValue.NULLCHAR) == 0) || (temp < 0x20) || (temp > 0x3F)) {
                            Integer locScrPosn = commBuffer.getCommScreen().screenPosn(commBuffer.getSBARow(), commBuffer.getSBACol());

                            if (Math.abs(locScrPosn - curPosn) >= 1) {
                                Comm5250ResponseBuilderUtils.UpdateFieldData(commBuffer,true); // In case output with no attr
                            }
                            // do not update if adjacent
                            if (locScrPosn > 1) {
                                wordTemp = 2;
                            }
                            else {
                                wordTemp = 1;
                            }
                            commBuffer.setTN5250Attr(commBuffer.getCommScreen().getScreenElements()[locScrPosn - wordTemp].getAttribute());
                        }
                        break;

                case 0x12: //WEA_ORDER:             // Write Extended Attributes
                    // AttrType Attr
                    // NOT operational (not allowed yet)
                    temp = commBuffer.getCurrentByte(); // Attribute type
                    commBuffer.incReceiveDataPointer();
                    
                    if ((temp != 0x01) && // Primary -- Mono
                        (temp != 0x03) && // Foreground -- Color
                        (temp != 0x05)) {   // Ideographic
                        // Invalid Attribute type 0x1005 0x01 0x2D
                        Do5250NegativeResponse(commBuffer, 0x1005012d);
                    }
                    if (temp == 0x03) {
                        temp = commBuffer.getCurrentByte(); // Extended attr
                        commBuffer.incReceiveDataPointer();
                        if ((temp != 0x00) && ((temp < 0x81) || (temp > 0x8F))) {
                            // Invalid Extended Attribute 0x1005 0x01 0x2F
                            Do5250NegativeResponse(commBuffer, 0x1005012f);
                        }
                        Comm5250ResponseBuilderUtils.ModifyScrAttr(commBuffer, temp);
                    }
                    else {
                        temp = commBuffer.getCurrentByte();
                        commBuffer.incReceiveDataPointer();
                    }
                    
                    break;
                        
                        
        		case 0x13: //IC_ORDER:              // Insert Cursor
                        // Row Col
                        boolean inside = true;
                        Integer icRow = DataLowLevelUtils.MAKEWORD(commBuffer.getCurrentByte(), 0);
                        commBuffer.incReceiveDataPointer();
                        Integer icCol = DataLowLevelUtils.MAKEWORD(commBuffer.getCurrentByte(), 0);
                        commBuffer.incReceiveDataPointer();

                        if (commBuffer.isProcessError()) {
                        	commBuffer.setSaveHomeRow(commBuffer.getHomeRow());
                        	commBuffer.setSaveHomeCol(commBuffer.getHomeCol());
                            inside = true;
                        }

                        if (inside) {
                        	commBuffer.setHomeRow(icRow);
                        	commBuffer.setHomeCol(icCol);
                            if ((commBuffer.getHomeRow() < 1) || (commBuffer.getHomeRow() > commBuffer.getCommScreen().getScreenHeight()) ||
                                (commBuffer.getHomeCol() < 1) || (commBuffer.getHomeCol() > commBuffer.getCommScreen().getScreenWidth())) {
                                // Invalid RowCol length 0x1005 0x01 0x22
                                Do5250NegativeResponse(commBuffer, 0x10050122);
                            }
                            commBuffer.setHomePosn(true);
                        }
                        break;
                        
                case 0x14: 
                		//MC_ORDER:              
                		// Move Cursor
                        // Row Col
                        // NOT operational (not allowed yet)
                		commBuffer.setCurrentRow(DataLowLevelUtils.MAKEWORD(commBuffer.getCurrentByte(), 0));
                		commBuffer.incReceiveDataPointer();
                		commBuffer.setCurrentCol(DataLowLevelUtils.MAKEWORD(commBuffer.getCurrentByte(), 0));
                		commBuffer.incReceiveDataPointer();

                		if ((commBuffer.getCurrentRow() < 1) || 
                			(commBuffer.getCurrentRow() > commBuffer.getCommScreen().getScreenHeight()) ||
                            (commBuffer.getCurrentCol() < 1) || 
                            (commBuffer.getCurrentCol() > commBuffer.getCommScreen().getScreenWidth())) {
                            // Invalid RowCol length 0x1005 0x01 0x22
                            Do5250NegativeResponse(commBuffer, 0x10050122);
                        }
                        break;

                case 0x15: //WDSF_ORDER:            // Write Display Structured Field
                    // NOT operational (not allowed yet)
                    break;                  // See 14.6.13

                case 0x1D: //SF_ORDER:              // Start of Field
                        // [FFW(2)] [FCW(2)] Attr Length(2)
                        boolean updateField = false;
                        Integer attr;
                        Integer idx = 0;
                        Integer len;
                        Integer saveRow, saveCol = 0;

                        commBuffer.setInpField(false);

                        if ((commBuffer.getSBARow() == 0) && 
                        	(commBuffer.getSBACol() == 0) && 
                        	(commBuffer.getLastFieldLength() == 0)) {
                            // Invalid SF Field address 0x1005 0x01 0x26
                            Do5250NegativeResponse(commBuffer, 0x10050126);
                        }

                        if (((commBuffer.getSBARow() - commBuffer.getCurrentRow()) != 0) || 
                        	((commBuffer.getSBACol() - commBuffer.getCurrentCol()) != 0))
                        	
                        	Comm5250ResponseBuilderUtils.SetCurrentRowCol(commBuffer, 
                        												  commBuffer.getCommScreen().screenPosn(commBuffer.getCurrentRow(), commBuffer.getCurrentCol()) + commBuffer.getLastFieldLength());

                        	commBuffer.setInpField((commBuffer.getCurrentByte() & 0x40) == 0x40);

                        if (commBuffer.isInpField()) {
                            // lockKeyboard ();
                            if ((commBuffer.getCommScreen().getInpfldCount() == 0) && 
                            	(commBuffer.getFormatTable() == null)) {
                                commBuffer.setFormatTable(new FormatTable[256]);
                            }
                            else if (commBuffer.getCommScreen().getInpfldCount() == 256) {
                                // Invalid SF Format Table overflow 0x1005 0x01 0x29
                                Do5250NegativeResponse(commBuffer, 0x10050129);
                            }

                            for (idx = 0; idx < commBuffer.getCommScreen().getInpfldCount(); idx++) {
                            	if(commBuffer.getCommScreen().screenPosn(commBuffer.getFormatTable()[idx].getBeginRow(),
                            											 commBuffer.getFormatTable()[idx].getBeginCol()) ==
                            	   commBuffer.getCommScreen().screenPosn(commBuffer.getCurrentRow(), commBuffer.getCurrentCol()) + 1) {
                            		   
                                   updateField = true;
                                   break;
                                }
                            }
                            
                            if (!updateField) {
                                idx = commBuffer.getCommScreen().getInpfldCount();
                                commBuffer.setSFOrderCount(commBuffer.getSFOrderCount() + 1);
                                
                                commBuffer.getFieldFmtTblPosn()[commBuffer.getSFOrderCount()] = DataLowLevelUtils.GetLowByte(idx + 1);
                                if (commBuffer.getFormatTable()[idx] == null) {
                                	commBuffer.getFormatTable()[idx] = new FormatTable();
                                }

                                commBuffer.getFormatTable()[idx].setNextField(0);
                            }
                            
                            commBuffer.getFormatTable()[idx].getFFW()[0] = commBuffer.getCurrentByte();
                            commBuffer.incReceiveDataPointer();
                            	
                            commBuffer.getFormatTable()[idx].getFFW()[1] = commBuffer.getCurrentByte();
                            commBuffer.incReceiveDataPointer();
                            
                            if (!commBuffer.isMasterMDT()) {    // Check for DSPATR(MDT)
                                commBuffer.setMasterMDT((commBuffer.getFormatTable()[idx].getFFW()[0] & 0x08) > 0);
                            }
                            /*****************************************************************************/
                            /*  Inhibit keyboard input is FCW[0] == 0x46.  This setting is supposed to   */
                            /*  allow input only with a magnetic stripe reader or light pen.  However,   */
                            /*  Library News400, Call Usropr has a pulldown menu with 0x46 0x80. Testb4xx*/
                            /*  display Testdsp2, format Datatr has field INHBTD = I and it uses settings*/
                            /*  0x46 0x00.  Appears that if FFW[1] == 0 it is inhibited, else it is not. */
                            /*****************************************************************************/
                            if ((               // Check for field type Inhibit
                                (commBuffer.getFormatTable()[idx].getFFW()[0] & 0x07) == 0x06) &&
                                (commBuffer.getFormatTable()[idx].getFFW()[1] == 0)) {
                            	commBuffer.getFormatTable()[idx].getFFW()[0] |= 0x20; // Bypass
                            }
                            //m_aFormatTable[idx].byFFW[0].byAllbits |= 0x04; // Bypass

                            if ((commBuffer.getCurrentByte() & 0x80) == 0x80){
                            	// Field control word only with FFW
                                if (!updateField) {
                                	commBuffer.getFormatTable()[idx].getFCW()[0] =
                                		commBuffer.getCurrentByte();
                                		commBuffer.incReceiveDataPointer();
                                    	commBuffer.getFormatTable()[idx].getFCW()[1] =
                                    		commBuffer.getCurrentByte();
                                    		commBuffer.incReceiveDataPointer();

                                    if (commBuffer.getFormatTable()[idx].getFCW()[0] == 0x80) {
                                    	commBuffer.getFormatTable()[idx].setNextField(DataLowLevelUtils.MAKEWORD(0, commBuffer.getFormatTable()[idx].getFCW()[0]));
                                    }
                                }
                                else {           // Ignore for update field data
                                	commBuffer.incReceiveDataPointer(2);
                                }
                            }
                            else {
                            	commBuffer.getFormatTable()[idx].getFCW()[0] = 0;
                            	commBuffer.getFormatTable()[idx].getFCW()[1] = 0;
                            }
                        }

                        attr = commBuffer.getCurrentByte();
                        commBuffer.incReceiveDataPointer();

                        if ((attr < 0x20) || (attr > 0x3F)) {
                            // Invalid SF Field attribute 0x1005 0x01 0x30
                            Do5250NegativeResponse(commBuffer, 0x10050130);
                        }
                        
                        len = commBuffer.getCurrentByte() * 0xFF + commBuffer.getNextByte();
                        commBuffer.incReceiveDataPointer(2);

                        if ((len <= 0) || (len > (scrDataSz - 1))) {
                            // Invalid SF Field length 0x1005 0x01 025
                            Do5250NegativeResponse(commBuffer, 0x10050125);
                        }
                        
                        if (commBuffer.getCommScreen().screenPosn(commBuffer.getCurrentRow(), commBuffer.getCurrentCol()) + len > scrDataSz) {
                            // Invalid SF Field extends past end of display 0x1005 0x01 0x28
                            Do5250NegativeResponse(commBuffer, 0x10050128);
                        }

                        Comm5250ResponseBuilderUtils.InsertScrElem(commBuffer, attr, false);
                        saveRow = commBuffer.getCurrentRow();
                        saveCol = commBuffer.getCurrentCol();

                        if ((commBuffer.isInpField()) && (!updateField)) {
                        	commBuffer.getFormatTable()[idx].setBeginRow(commBuffer.getCurrentRow());
                        	commBuffer.getFormatTable()[idx].setBeginCol(commBuffer.getCurrentCol());
                        	commBuffer.getFormatTable()[idx].setFieldLen(len);
                        	commBuffer.getFormatTable()[idx].setGuiDefined(false);

                            if ((!commBuffer.isSetHomePosn()) &&
                                ((commBuffer.getFormatTable()[idx].getFFW()[0] & 0x04) == 0)) {
                            	// Non-bypass
                            	commBuffer.setHomeRow(commBuffer.getCurrentRow());
                            	commBuffer.setHomeCol(commBuffer.getCurrentCol());
                            	commBuffer.setHomePosn(true);
                            }

                            commBuffer.setLastFieldLength(len);
                            commBuffer.getCommScreen().incInpfldCount();
                        }

                        commBuffer.setInpField(false);
                        commBuffer.setCurrentRow(saveRow);
                        commBuffer.setCurrentCol(saveCol);
                        break;

                default:                    // Data or ???
                    if ((wtdOrders - TelnetCommandValue.DS5250_ESCAPE) == 0) {
                        done = true;       // Data is 0x04
                    }
                    else if ((wtdOrders - TelnetCommandValue.TNC_IAC) == 0) {
                        if ((commBuffer.getCurrentByte() - TelnetCommandValue.TNC_IAC) == 0) {
                            wtdOrders = commBuffer.getCurrentByte();
                            commBuffer.incReceiveDataPointer();
                            if (commBuffer.getCurrentByte() == TN5250CommandValue.GRAPHICS_ON) {
                                // Invalid data stream command 0x1003 0x01 0x01
                                Do5250NegativeResponse(commBuffer, 0x10030101);
                            }
                            else if (commBuffer.getCurrentByte() == TN5250CommandValue.GRAPHICS_OFF) {
                                // Invalid data stream command 0x1003 0x01 0x01
                                Do5250NegativeResponse(commBuffer, 0x10030101);
                            }
                            else {   // Remove -- works on SeaGull and SNA displays
                                // Invalid display character (0xFF) 0x1005 0x01 0x42
                                // do5250NegativeResponse (0x10050142);

                                wtdOrders = TelnetCommandValue.NULLCHAR;

                            }
                        }
                        else
                            done = (((commBuffer.getCurrentByte() - TelnetCommandValue.TNC_IAC) != 0) &&
                                ((commBuffer.getCurrentByte() - TelnetCommandValue.TNC_EF) >= 0));
                    }

                    if (!done) {
                    	// Character or attribute (0x20..0x3F)
                        if ((!commBuffer.isTextField()) && (!commBuffer.isRptNull()))
                        	commBuffer.setTextField((wtdOrders >= 0x20) &&
                        							(wtdOrders <= 0x3F) &&
                        							(commBuffer.getFieldLength() > 0));
                        try {
                        	Comm5250ResponseBuilderUtils.InsertScrElem(commBuffer, wtdOrders, false);
                        }
                        catch (Exception exc) {
//                        	SimpleLogger.writeLine("Response Builder", "InsertScrElem - " + exc.getLocalizedMessage(), commBuffer.getConnectionData().isWriteLog());
                        	done = true;
                        }
                    }
                    else {
                        // Force out any field data
                        Integer tvFlag = 0;
                        Comm5250ResponseBuilderUtils.UpdateFieldData(commBuffer, true);
                        for (Integer locIdx = 0; locIdx < commBuffer.getCommScreen().getInpfldCount(); locIdx++) { 
                        	if (!commBuffer.getFormatTable()[locIdx].isGuiDefined()) { // Check input fields have been defined
                        		commBuffer.getFormatTable()[locIdx].setGuiDefined(true);
                        		tvFlag = Comm5250ResponseBuilderUtils.setupFieldAttr(commBuffer, locIdx);

                        		commBuffer.getCommScreen().addNewField(commBuffer.getFormatTable()[locIdx].getBeginRow(), 
                        											   commBuffer.getFormatTable()[locIdx].getBeginCol(), 
                        											   commBuffer.getFormatTable()[locIdx].getFieldLen(), 
                        											   tvFlag);
                            }
                        }
                    }
                    break;
            }

            if (!done) {
                wtdOrders = commBuffer.getCurrentByte();
            	commBuffer.incReceiveDataPointer();
            }
        }

    	commBuffer.decReceiveDataPointer();
	}
	
	/**
	 * Performs the write to display commands  
	 * It is call by Comm5250ResponseBuilder
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @param  commBuffer CommBufferLogic object
	 * 
	 * @see CommBufferLogic
	 */    
	public static void DoCmdCtrlChars(CommBufferLogic commBuffer){
	
		if (commBuffer.getWTDCmdCtrl().size() == 0) { 
			commBuffer.getWTDCmdCtrl().add(0); 
			commBuffer.getWTDCmdCtrl().add(0); 
		}
        if (commBuffer.getWTDCmdCtrl().get(0) > 0) {
            commBuffer.setTN5250AidCode(TelnetCommandValue.NULLCHAR);
            // lockKeyboard ();
            commBuffer.getCommScreen().addNewFieldFormat(ScreenElementType.lockKeyboard, "");
        }
        switch (commBuffer.getWTDCmdCtrl().get(0)) { // See Function Ref 14.6.1
            case 0x40:
                DoResetFmtTblMDT(commBuffer, false);
                break;
            case 0x60:
            	DoResetFmtTblMDT(commBuffer, true);
                break;
            case 0x80:
                DoSetFieldsNull(commBuffer, false);
                break;
            case 0xA0:
            	DoSetFieldsNull(commBuffer, true);
            	DoResetFmtTblMDT(commBuffer, false);
                break;
            case 0xC0:
                DoSetFieldsNull(commBuffer, false);
            	DoResetFmtTblMDT(commBuffer, false);
                break;
            case 0xE0:
                DoSetFieldsNull(commBuffer, true);
            	DoResetFmtTblMDT(commBuffer, true);
                break;
        }
        if ((commBuffer.getWTDCmdCtrl().get(1) & 0x40) > 0)    // Move cursor to default position
        { }
        if ((commBuffer.getWTDCmdCtrl().get(1) & 0x20) > 0)    // Reset blinking cursor
        { }
        if ((commBuffer.getWTDCmdCtrl().get(1) & 0x10) > 0)    // Set blinking cursor
        { }
        if ((commBuffer.getWTDCmdCtrl().get(1) & 0x08) > 0)    // Unlock keyboard and reset AID bytes
        {

            commBuffer.setTN5250AidCode(TelnetCommandValue.NULLCHAR);

            if (!commBuffer.isSetHomePosn()) {
            	commBuffer.setCurrentRow(1);
            	commBuffer.setCurrentCol(1);
            	commBuffer.setHomeRow(0);
            	commBuffer.setHomeCol(0);
            }

        }
        if ((commBuffer.getWTDCmdCtrl().get(1) & 0x04) > 0)    // Sound alarm
        //if ( ( m_aWTDCmdCtrl[1].byAllbits & 0x20 ) > 0 )    // Sound alarm
        {
        	commBuffer.getCommScreen().addNewField(ScreenElementType.SoundAlarm, 0, 0, 0, "");
        }
        if ((commBuffer.getWTDCmdCtrl().get(1) & 0x02) > 0)    // Msg indicator off
        //if ( ( m_aWTDCmdCtrl[1].byAllbits & 0x40 ) > 0 )    // Msg indicator off
        {
        	commBuffer.getCommScreen().addNewField(ScreenElementType.MsgWaitingOff, 0, 0, 0, "");
        }
        if ((commBuffer.getWTDCmdCtrl().get(1) & 0x01) > 0)    // Msg indicator on
        //if ( ( m_aWTDCmdCtrl[1].byAllbits & 0x80 ) > 0 )    // Msg indicator on
        {
        	commBuffer.getCommScreen().addNewField(ScreenElementType.MsgWaitingOn, 0, 0, 0, "");
        }
	}
	
	
	/**
	 * If unexpected or incorrect data is found in the   
	 * stream a negative response is generated by this method
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @param  commBuffer CommBufferLogic object
	 * @param  invalid code
	 * 
	 * @see CommBufferLogic
	 */    
	public static void Do5250NegativeResponse(CommBufferLogic commBuffer, Integer invalid){
		commBuffer.getAuxBuffer().clear();
		commBuffer.getAuxBuffer().add(TelnetCommandValue.NULLCHAR); // Rlen
		commBuffer.getAuxBuffer().add(14); // Rlen = total record length
		commBuffer.getAuxBuffer().add(0x12); // Record type
		commBuffer.getAuxBuffer().add(0xA0);
		commBuffer.getAuxBuffer().add(TelnetCommandValue.NULLCHAR); // Reserved
		commBuffer.getAuxBuffer().add(TelnetCommandValue.NULLCHAR);
		commBuffer.getAuxBuffer().add(0x04);
		commBuffer.getAuxBuffer().add(0x80); // Data Stream Output Error
		commBuffer.getAuxBuffer().add(TelnetCommandValue.NULLCHAR); // Reserved
		commBuffer.getAuxBuffer().add(TelnetCommandValue.NULLCHAR); // Opcode
		commBuffer.getAuxBuffer().add(DataLowLevelUtils.GetHiByte(DataLowLevelUtils.GetHiWord(invalid)));
		commBuffer.getAuxBuffer().add(DataLowLevelUtils.GetLowByte(DataLowLevelUtils.GetHiWord(invalid)));
		commBuffer.getAuxBuffer().add(DataLowLevelUtils.GetHiByte(DataLowLevelUtils.GetLowWord(invalid)));
		commBuffer.getAuxBuffer().add(DataLowLevelUtils.GetLowByte(DataLowLevelUtils.GetLowWord(invalid)));
		commBuffer.resetSend();
		commBuffer.socketFillSendbuf(commBuffer.getAuxBuffer().size());
		commBuffer.setReceiveDataPointer(commBuffer.getReceiveData().size());
        //LDP
        //throw ( NegResponse_T ) HIWORD (dwInvalid);
	}
	
	/**
	 * Perform Write Structured Field (WSF) commands   
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @param  commBuffer CommBufferLogic object
	 * 
	 * @see CommBufferLogic
	 */    
	public static void DoWSFOrders(CommBufferLogic commBuffer){
        //Integer wsflen;
        Integer cmdclass;
        Integer typecode;
        //Integer flags;

        //wsflen = commBuffer.getNextByte() * 0xFF + commBuffer.getCurrentByte();
        commBuffer.incReceiveDataPointer(2);

        cmdclass = commBuffer.getCurrentByte(); // 0xD9
        commBuffer.incReceiveDataPointer();
        typecode = commBuffer.getCurrentByte(); // 0x70
        commBuffer.incReceiveDataPointer();
        //flags = commBuffer.getCurrentByte(); // 0x00
        commBuffer.incReceiveDataPointer();

        if (cmdclass == 0xD9)
        {
            if (typecode == 0x70)
            {
                Integer sendlen = commBuffer.socketSend5250QueryData();
                if (sendlen > 0)
                    commBuffer.socketFillSendbuf(sendlen);
            }
        }
	}
	
	/**
	 * Reset the Modified Data Tag (MDT)   
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @param  commBuffer CommBufferLogic object
	 * @param  resetAll to force to reset all the input fields
	 * 
	 * @see CommBufferLogic
	 */    
    public static void DoResetFmtTblMDT(CommBufferLogic commBuffer, boolean resetAll) {
        // bResetAll TRUE = Reset All, FALSE = Reset Non-bypass only
    	commBuffer.setMasterMDT(false);
        for (Integer idx = 0; idx < commBuffer.getCommScreen().getInpfldCount(); idx++) {
            if ((resetAll) ||
                (commBuffer.getFormatTable()[idx].getFFW()[0] & 0x20) == 0) { // Non-bypass
            	commBuffer.getFormatTable()[idx].getFFW()[0] &= 0xF7;    // MDT off
            }
        }
    }

	/**
	 * Set null char for input fields   
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @param  commBuffer CommBufferLogic object
	 * @param  setAll to force to set all the input fields
	 * 
	 * @see CommBufferLogic
	 */    
    public static void DoSetFieldsNull(CommBufferLogic commBuffer, boolean setAll) {
        // pSetAll TRUE = Null All Non-bypass, FALSE = Null Non-bypass with MDT only
        for (Integer idx = 0; idx < commBuffer.getCommScreen().getInpfldCount(); idx++) {
            if ((commBuffer.getFormatTable()[idx].getFFW()[0] & 0x20) == 0)  // Non-bypass
            {
                if ((setAll) ||
                    (commBuffer.getFormatTable()[idx].getFFW()[0] |= 0x08) > 0) // MDT
                {
                    Integer scrPosnBgn = commBuffer.getCommScreen().screenPosn(commBuffer.getFormatTable()[idx].getBeginRow(),
                    														   commBuffer.getFormatTable()[idx].getBeginCol());
                    Integer scrPosnEnd = commBuffer.getCommScreen().screenPosn(commBuffer.getFormatTable()[idx].getBeginRow(),
                    		commBuffer.getFormatTable()[idx].getBeginCol() +
                    		commBuffer.getFormatTable()[idx].getFieldLen() - 1);
                    for (Integer jdx = scrPosnBgn; jdx <= scrPosnEnd; jdx++) {
                    	commBuffer.getCommScreen().getScreenElements()[jdx - 1].setValue(TelnetCommandValue.NULLCHAR);
                    }
                }
            }
        }
    }
	
	/**
	 * Clear the emulated unit state   
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @param  CommBufferLogic object
	 * 
	 * @return 	void
	 * @see CommBufferLogic
	 */    
	private static void clearUnitStates(CommBufferLogic commBuffer) {
        for (int transf = 0; transf < commBuffer.getCommScreen().getScreenElements().length; transf++) {
        	commBuffer.getCommScreen().getScreenElements()[transf] = new GreenScreenElement(0x0, 0x20); 
        }
        commBuffer.setMasterMDT(false);
        commBuffer.setHomePosn(false);
        commBuffer.setTN5250Cmdkeys(0); // All keys return input AID and data
        commBuffer.getCommScreen().setInpfldCount(0);
        commBuffer.setFieldReseq(0);
        commBuffer.setLastFieldLength(0);
        commBuffer.setSFOrderCount(0);
        commBuffer.setCurrentRow(1);
        commBuffer.setCurrentCol(1);
        commBuffer.setHomeRow(0);
        commBuffer.setHomeCol(0);
        
        commBuffer.getCommScreen().addNewFieldFormat(ScreenElementType.ClearInput, "");
        commBuffer.getCommScreen().addNewFieldScroll(1, 
        											 1, 
        											 commBuffer.getCommScreen().getScreenHeight() + 1, 
        											 commBuffer.getCommScreen().getScreenWidth());
        // lockKeyboard ();
        commBuffer.getCommScreen().addNewFieldFormat(ScreenElementType.lockKeyboard, "");
		
	}
}
