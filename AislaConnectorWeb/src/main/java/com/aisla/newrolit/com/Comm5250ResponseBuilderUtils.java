package com.aisla.newrolit.com;

import com.aisla.newrolit.global.GreenScreenElement;
import com.aisla.newrolit.global.TN5250CommandValue;

/**
 * Comm5250ResponseBuilderUtils methods for 5250 
 * response as a toolkit for the Do5250DataStream and DoWTDOrders methods.
 * In this class green screen fields collection are added
 * <p>
 * @author      Luis Paradela
 * @version     1.0
 * @since       1.0
 */
public class Comm5250ResponseBuilderUtils {
	/**
	 * Add the input fields and other fields as save screen and unlock keyboard
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @param  commBuffer CommBufferLogic object
	 * @param  tn5250CommandValue
	 * 
	 */    
	public static void GetInputData(CommBufferLogic commBuffer, Integer tn5250CommandValue) {
		commBuffer.setInpField(false); // Display complete, setup to get keyin
        // or internal data to send.
        Integer tvFlag = 0; //Empty the receive buffer prior to doing this.

        if (tn5250CommandValue == TN5250CommandValue.READ_MDTFLDS) {
            commBuffer.setProcessingMode(ProcessMode.modeReadMDTflds);
        }
        else if (tn5250CommandValue == TN5250CommandValue.READ_INPFLDS) {
        	commBuffer.setProcessingMode(ProcessMode.modeReadInpflds);
        }
        else if (tn5250CommandValue == TN5250CommandValue.SAVE_SCREEN) {
        	commBuffer.setProcessingMode(ProcessMode.modeSaveScreen);
        }
        
        if ((tn5250CommandValue == TN5250CommandValue.READ_MDTFLDS) || 
            (tn5250CommandValue == TN5250CommandValue.READ_INPFLDS)) {
            for (Integer idx = 0; idx < commBuffer.getCommScreen().getInpfldCount(); idx++) {
                if (!commBuffer.getFormatTable()[idx].isGuiDefined()) { // Check input fields have been defined
                	commBuffer.getFormatTable()[idx].setGuiDefined(true);
                	tvFlag = setupFieldAttr(commBuffer, idx);
                	commBuffer.getCommScreen().addNewField(commBuffer.getCommScreen(), tvFlag);
                }
            }
            
            if (commBuffer.getHomeRow() == 0) {
            	commBuffer.setHomeRow(commBuffer.getCurrentRow());
            	commBuffer.setHomeCol(commBuffer.getCurrentCol());
            }

            Integer scrPosn = commBuffer.getCommScreen().screenPosn(commBuffer.getHomeRow(), commBuffer.getHomeCol()) - 1;
            commBuffer.getCommScreen().addNewField(ScreenElementType.UnlockKeyboard, scrPosn, 0, 0, "");
        }
        else if (tn5250CommandValue == TN5250CommandValue.SAVE_SCREEN) {
        	commBuffer.getCommScreen().addNewFieldFormat(ScreenElementType.SaveDisplay, ""); // Get restore display packets
        }
	}
	
	/**
	 * Update / Add input fields. In case the field exists
	 * the method update its lenght
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @param  commBuffer CommBufferLogic object
	 * @param  newField tells the method that it is a new field
	 * 
	 */    
    public static void UpdateFieldData(CommBufferLogic commBuffer, boolean newField) {
        Integer idx, jdx;
        Integer scrPosn;
        GreenScreenElement tempElem;
        Integer tvFlag = 0;
        commBuffer.setInpFieldUpdate(false);
        
        CommScreen commScreen = commBuffer.getCommScreen();

        if (newField) {
            if (commBuffer.getCommScreen().getFieldLen() > 0) { // If input field, make sure it is
            													// filled to end else GUI truncates
            	tvFlag = setupFieldAttr(commBuffer, -1);
                if (!commBuffer.isProcessError()) {
                    for (idx = 0; idx < commScreen.getInpfldCount(); idx++) {
                        if (((commScreen.getFieldRow() - commBuffer.getFormatTable()[idx].getBeginRow()) == 0) &&
                            ((commScreen.getFieldCol() - commBuffer.getFormatTable()[idx].getBeginCol()) >= 0) &&
                            ((commScreen.getFieldCol() - (commBuffer.getFormatTable()[idx].getBeginCol() +
                             commBuffer.getFormatTable()[idx].getFieldLen() - 1)) < 0 )) {
                            if (commScreen.getFieldLen() < commBuffer.getFormatTable()[idx].getFieldLen()) { // Use field start color attribute
                                scrPosn = commScreen.screenPosn(commScreen.getFieldRow(), commScreen.getFieldCol());
                                tempElem = commScreen.getScreenElements()[scrPosn - 2];
                                tempElem.setValue(TelnetCommandValue.NULLCHAR);
                                scrPosn = commScreen.screenPosn(commScreen.getFieldRow(), commScreen.getFieldCol() + commScreen.getFieldLen());
                                for (jdx = commScreen.getFieldCol() + commScreen.getFieldLen();
                                    jdx <= commBuffer.getFormatTable()[idx].getBeginCol() +
                                    commBuffer.getFormatTable()[idx].getFieldLen() - 1;
                                    jdx++) { // Make doubly sure do not truncate
                                    
                                	if ( (commScreen.getScreenElements()[scrPosn - 1].getValue() - TelnetCommandValue.NULLCHAR) != 0) {
                                        break;
                                	}
                                    else {
                                    	commScreen.getScreenElements()[scrPosn - 1] = new GreenScreenElement(tempElem.getValue(), tempElem.getAttribute());
                                        scrPosn++;
                                    }
                                }

                                commScreen.setFieldLen(commBuffer.getFormatTable()[idx].getFieldLen());
                            }
                            break;
                        }
                    }
                }
                else {
                    commBuffer.setInpFieldUpdate(false);
                }
                
                jdx = 0;
                
                if (!commBuffer.isInpFieldUpdate()) {
                    scrPosn = commScreen.screenPosn(commScreen.getFieldRow(), commScreen.getFieldCol());
                    for (idx = 0; idx < commScreen.getFieldLen(); idx++) {
                        if ((commScreen.getScreenElements()[idx + scrPosn - 1].getValue() - TelnetCommandValue.NULLCHAR) == 0) {
                            jdx++;
                        }
                        else {
                            break;
                        }
                    }
                }
                // GUI needs all null text field data for SFL, customize, etc.
                // Difficult to tell what is SFL data, window, SFL in window, etc.
                // Assume -- Text --> SBA RowCol @ RA RowCol 0x00 WTD_Order
                //           Text --> SBA RowCol @ 0x00..0x00 @
                //           Clear--> SBA Rowcol @ Data @ RA RowCol 0x00 @

                if (jdx < commScreen.getFieldLen()) {
                	// Need field for customization
                    commScreen.addNewField(commScreen, tvFlag);
                }
                else if (commBuffer.isTextField())
                {                           // Need field for customization
                    if (!commBuffer.isProcessError()) {     // Don't send all nulls to reset popup
                        commScreen.addNewField(commScreen, tvFlag);
                    }
                }
                else { // Assume have window
                	   // New clear partial row
                       // Clear leading and trailing attrs
                    commScreen.addNewFieldScroll(commScreen.getFieldRow(), commScreen.getFieldCol() - 1, commScreen.getFieldRow(), commScreen.getFieldCol() + commScreen.getFieldLen());
                }
            }

            commScreen.setFieldLen(0);
            commScreen.setFieldRow(commBuffer.getCurrentRow());
            commScreen.setFieldCol(commBuffer.getCurrentCol());
            commBuffer.setTextField(false);
            commBuffer.setRptNull(false);
        }
        else {
        	commScreen.incFieldLen();
        }
    }

	/**
	 * Returns a select field attribute.
	 * The field is selected by id 
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @param  commBuffer CommBufferLogic object
	 * @param  inputId id to select the field
	 * 
	 * @return 	attribute (including extended attributes)
	 */    
    public static Integer setupFieldAttr(CommBufferLogic commBuffer, int inputId) {
    	Integer row, col;
    	if(inputId < 0) {
	    	row = commBuffer.getCommScreen().getFieldRow();
	    	col = commBuffer.getCommScreen().getFieldCol();
    	}
    	else {
	    	row = commBuffer.getFormatTable()[inputId].getBeginRow();
	    	col = commBuffer.getFormatTable()[inputId].getBeginCol();
    	}
        CommScreen commScreen = commBuffer.getCommScreen();
        Integer fcw = 0;
        Integer ffw = 0;
        boolean rtnval = false;
        
        Integer posn = commBuffer.getCommScreen().screenPosn(row, col);
        Integer ckInp = 0x00;

        if ((posn - 2) < 0) {
            rtnval = false;
        }

        Integer color = commScreen.getScreenElements()[posn - 2].getAttribute();

        for (Integer idx = 0; idx < commScreen.getInpfldCount(); idx++) {
            Integer fieldPosn = commScreen.screenPosn(commBuffer.getFormatTable()[idx].getBeginRow(), commBuffer.getFormatTable()[idx].getBeginCol());
            if ((posn >= fieldPosn) &&
                (posn <= fieldPosn + commBuffer.getFormatTable()[idx].getFieldLen() - 1)) {
                rtnval = true;
                ckInp = 0x01;
                color = commScreen.getScreenElements()[fieldPosn - 2].getAttribute();
                //Remove FFW identification flag
                ffw = DataLowLevelUtils.MAKEWORD(commBuffer.getFormatTable()[idx].getFFW()[1],                    
                								 commBuffer.getFormatTable()[idx].getFFW()[0] & 0xBF);
                fcw = DataLowLevelUtils.MAKEWORD(commBuffer.getFormatTable()[idx].getFCW()[1],
                								 commBuffer.getFormatTable()[idx].getFCW()[0]);
                if (fcw == DataLowLevelUtils.CKMOD10) {
                    ckInp = ckInp | 0x10;
                }
                else if (fcw == DataLowLevelUtils.CKMOD11) {
                    ckInp = ckInp | 0x20;
                }
                commBuffer.getFormatTable()[idx].setGuiDefined(true);
                break;
            }
        }
        fcw = DataLowLevelUtils.MAKEWORD(color, ckInp);
        commBuffer.setInpFieldUpdate(rtnval);
        return DataLowLevelUtils.MAKELONG(fcw, ffw);
    }

	/**
	 * Updates the current column by an increment
	 * Calculation involves screen width
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @param  commBuffer CommBufferLogic object
	 * @param  incr increment value
	 * 
	 */    
    public static void UpdateCurrentCol(CommBufferLogic commBuffer, Integer incr)  {
    	commBuffer.setCurrentCol(commBuffer.getCurrentCol() + incr);

        while (commBuffer.getCurrentCol() > commBuffer.getCommScreen().getScreenWidth()) {
        	commBuffer.setCurrentRow(commBuffer.getCurrentRow() + 1);
        	commBuffer.setCurrentCol(commBuffer.getCurrentCol() - commBuffer.getCommScreen().getScreenWidth());
        }
    }

	/**
	 * Adds an screen element to the screen buffer
	 * used in the RA_ORDER (repeat character) command
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @param  commBuffer CommBufferLogic object
	 * @param  aorC the character
	 * @param  transparent
	 * 
	 */    
    public static void InsertScrElem(CommBufferLogic commBuffer, Integer aorC, boolean transparent) {

        boolean inside = false;
        boolean atbgn = false;
        boolean atend = false;
        Integer scrPosn;                      // 1..1920, Screen is 0..1919
        Integer idx = 0;


        // Update both TN5250
        if (!commBuffer.isProcessError()) {
            for (idx = 0; idx < commBuffer.getCommScreen().getInpfldCount(); idx++) {
                atbgn = ((aorC >= 0x20) && (aorC <= 0x3F) &&
                    (((commBuffer.getCurrentRow() - commBuffer.getFormatTable()[idx].getBeginRow()) == 0) &&
                    ((commBuffer.getCurrentCol() + 1) - commBuffer.getFormatTable()[idx].getBeginCol()) == 0));
                
                if (atbgn) { // Special case for redefinition of the
                    commBuffer.setInpField(true);         // input field -- see Work with Folders
                    // ie. SBA SF (no data) then SBA RA data
                }
                else {
                    Integer rcbgn = RowCol(commBuffer, commBuffer.getFormatTable()[idx].getBeginRow(),
                    		commBuffer.getFormatTable()[idx].getBeginCol());
                    Integer wRCEnd = RowCol(commBuffer, commBuffer.getFormatTable()[idx].getBeginRow(),
                    		commBuffer.getFormatTable()[idx].getBeginCol() +
                    		commBuffer.getFormatTable()[idx].getFieldLen() - 1);
                    Integer wRCCsr = RowCol(commBuffer, commBuffer.getCurrentRow(), commBuffer.getCurrentCol());
                    inside = (((wRCCsr - rcbgn) >= 0) && ((wRCCsr - wRCEnd) < 0));
                    atend = (wRCCsr - wRCEnd) == 0;
                }
                
                if ((atbgn) || (inside) || (atend)) {
                    break;
                }
            }
        }
        
        scrPosn = commBuffer.getCommScreen().screenPosn(commBuffer.getCurrentRow(), commBuffer.getCurrentCol());
        if ((aorC >= 0x20) && (aorC <= 0x3F) && (!transparent)) {
        	// Put attr uinto both attribute and
        	commBuffer.setTN5250Attr(aorC); // char until support extended attr
        	commBuffer.getCommScreen().getScreenElements()[scrPosn - 1].setAttribute(commBuffer.getTN5250Attr());
        	commBuffer.getCommScreen().getScreenElements()[scrPosn - 1].setValue(commBuffer.getTN5250Attr());
        }
        else {
        	commBuffer.getCommScreen().getScreenElements()[scrPosn - 1].setAttribute(commBuffer.getTN5250Attr());
        	commBuffer.getCommScreen().getScreenElements()[scrPosn - 1].setValue(aorC);
        }

        UpdateCurrentCol(commBuffer, 1);

        if (atbgn) {
        	commBuffer.setInpField(false);
        }
        if ((aorC >= 0x20) && (aorC <= 0x3F) && (!transparent)) {
            if (inside) {
                UpdateFieldData(commBuffer, false);
            }
            else {
            	UpdateFieldData(commBuffer, true);
            }
        }
        else if ((atend) && (!commBuffer.isRptNull())) {
        	commBuffer.setFieldLength(commBuffer.getFieldLength() + 1);
            UpdateFieldData(commBuffer, true);
        }
        else {
            UpdateFieldData(commBuffer, false);
        }
    }

	/**
	 * Returns a position in the raw buffer
	 * given a row and a column
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @param  commBuffer CommBufferLogic object
	 * @param  row
	 * @param  col column
	 * 
	 * @return 	a position in the raw buffer
	 */    
    public static Integer RowCol(CommBufferLogic commBuffer, Integer row, Integer col) {
    	Integer r = row;
    	Integer c = col;
    	Integer retnval = 0;

        if (c > commBuffer.getCommScreen().getScreenWidth()) {
            Integer temp = (c - 1) / commBuffer.getCommScreen().getScreenWidth();
            r = r + temp;
            c = c - (temp * commBuffer.getCommScreen().getScreenWidth());
        }
        retnval = DataLowLevelUtils.MAKEWORD(DataLowLevelUtils.GetLowByte(c), 
        									DataLowLevelUtils.GetLowByte(r));
        return retnval;
    }

	/**
	 * modify an attribute in the screen buffer
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @param  commBuffer CommBufferLogic object
	 * @param  newAttr new attribute value
	 * 
	 */    
    public static void ModifyScrAttr(CommBufferLogic commBuffer, Integer newAttr) {
        Integer scrPosn;                      // 1..1920, Screen is 0..1919
        scrPosn =  commBuffer.getCommScreen().screenPosn(commBuffer.getCurrentRow(), commBuffer.getCurrentCol());
        commBuffer.getCommScreen().getScreenElements()[scrPosn - 1].setAttribute(newAttr);

    }

	/**
	 * Set the current column and row given the 
	 * raw buffer position
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @param  commBuffer CommBufferLogic object
	 * @param  rcPosn
	 * 
	 */    
    public static void SetCurrentRowCol(CommBufferLogic commBuffer, Integer rcPosn) {
    	commBuffer.setCurrentRow(rcPosn / commBuffer.getCommScreen().getScreenWidth());
    	commBuffer.setCurrentCol(rcPosn - (commBuffer.getCurrentRow() * commBuffer.getCommScreen().getScreenWidth()));
    	commBuffer.setCurrentRow(commBuffer.getCurrentRow() + 1);
    }
}
