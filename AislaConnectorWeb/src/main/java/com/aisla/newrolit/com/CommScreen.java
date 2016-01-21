package com.aisla.newrolit.com;

import com.aisla.newrolit.global.GreenScreenElement;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * CommScreen holds all the objects for the 
 * green screen emulation 
 * <p>
 * Main objects are the following
 * <ul>
 * <li>screenWidth = 80;
 * <li>screenHeight = 24;
 * <li>minScreenWidth = 80;
 * <li>maxScreenWidth = 132;
 * <li>minScreenHeight = 24;
 * <li>maxScreenHeight = 27;
 * <li>maxScreenElements = 4000;
 * <li>errorLine = 24;
 * <li>fieldRow = 0; // All screen field creation
 * <li>fieldCol = 0; // All screen field creation
 * <li>fieldLen = 0; // All screen field creation
 * <li>inpfldCount = 0;
 * </ul>
 * <p>
 * @author      Luis Paradela
 * @version     1.0
 * @since       1.0
 */
public class CommScreen {
	private int _screenWidth = 80;
	private int _screenHeight = 24;
	private int _minScreenWidth = 80;
	private int _maxScreenWidth = 132;
	private int _minScreenHeight = 24;
	private int _maxScreenHeight = 27;
	private int _maxScreenElements = 4000;
	private int _errorLine = 24;
    private int _fieldRow = 0; // All screen field creation
    private int _fieldCol = 0; // All screen field creation
    private int _fieldLen = 0; // All screen field creation
    private int _inpfldCount = 0;
    public final Integer  XLATEBYTE = 0x80;
    public final Integer  MAX_INP_FIELDS = 256;
    public CommBufferLogic _commBuffer = null;
    
    
    private List<ScreenField> _screenFields = new ArrayList<ScreenField>();
	private GreenScreenElement[] _screenElements = new GreenScreenElement[4000];
	private Integer[] _fieldFmtTblPosn = new Integer[256];

	public CommBufferLogic getCommBuffer() {
		return _commBuffer;
	}

	public void setCommBuffer(CommBufferLogic commBuffer) {
		this._commBuffer = commBuffer;
	}

	public Integer[] getFieldFmtTblPosn() {
		return _fieldFmtTblPosn;
	}

	public void setFieldFmtTblPosn(Integer[] _fieldFmtTblPosn) {
		this._fieldFmtTblPosn = _fieldFmtTblPosn;
	}

	public GreenScreenElement[] getScreenElements() {
		return _screenElements;
	}

	public int getScreenWidth() {
		return _screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this._screenWidth = screenWidth;
	}

	public int getScreenHeight() {
		return _screenHeight;
	}

	public void setScreenHeight(int _screenHeight) {
		this._screenHeight = _screenHeight;
	}

	public void setScreenElements(GreenScreenElement[] screenElements) {
		this._screenElements = screenElements;
	}

	public int getInpfldCount() {
		return _inpfldCount;
	}

	public void incInpfldCount() {
		this._inpfldCount++;
	}
	
	public void setInpfldCount(int _inpfldCount) {
		this._inpfldCount = _inpfldCount;
	}
	public int getMinScreenWidth() {
		return _minScreenWidth;
	}
	public int getMaxScreenWidth() {
		return _maxScreenWidth;
	}
	public int getMinScreenHeight() {
		return _minScreenHeight;
	}
	public int getMaxScreenHeight() {
		return _maxScreenHeight;
	}
	public List<ScreenField> getScreenFields() {
		return _screenFields;
	}
	public void setScreenFields(List<ScreenField> _screenFields) {
		this._screenFields = _screenFields;
	}

	public int getMaxScreenElements() {
		return _maxScreenElements;
	}

	public void setMaxScreenElements(int _maxScreenElements) {
		this._maxScreenElements = _maxScreenElements;
	}
	
	public int getErrorLine() {
		return _errorLine;
	}
	
	public void setErrorLine(int _errorLine) {
		this._errorLine = _errorLine;
	}

	public int getFieldRow() {
		return _fieldRow;
	}
	
	public void setFieldRow(int _fieldRow) {
		this._fieldRow = _fieldRow;
	}
	
	public int getFieldCol() {
		return _fieldCol;
	}
	
	public void setFieldCol(int _fieldCol) {
		this._fieldCol = _fieldCol;
	}
	
	public int getFieldLen() {
		return _fieldLen;
	}

	public void incFieldLen() {
		_fieldLen++;
	}
	
	public void setFieldLen(int _fieldLen) {
		this._fieldLen = _fieldLen;
	}
	
	public CommScreen(CommBufferLogic commBuffer) {
		_commBuffer = commBuffer;
		
	}
	
	/**
	 * Add a format name field any time a new format is added
	 * by a WRITE_TO_DISPLAY operation
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @param  commBuffer CommBufferLogic
	 * 
	 */    
	public void addFormatNameField(CommBufferLogic commBuffer) {
        boolean isDidSignon = false;
        String blank = " ";
        Integer[] EBCDIC_SIGNON = new Integer[8];
        Integer[] signon = new Integer[8];
        int i;

        EBCDIC_SIGNON[0] = 226;
        EBCDIC_SIGNON[1] = 137;
        EBCDIC_SIGNON[2] = 135;
        EBCDIC_SIGNON[3] = 149;
        EBCDIC_SIGNON[4] = 64;
        EBCDIC_SIGNON[5] = 214;
        EBCDIC_SIGNON[6] = 149;
        EBCDIC_SIGNON[7] = 0;

        if (commBuffer.getReceiveDataPointer() + 38 < commBuffer.getReceiveCount()) {
            for (i = 0; i < 7; ++i) {
                signon[i] = commBuffer.getReceiveData().get(commBuffer.getReceiveDataPointer() + 32 + i);  
            }

            isDidSignon = true;
            for (i = 0; i < signon.length; ++i) {
            	if (EBCDIC_SIGNON[i] != signon[i]) {
                    isDidSignon = false;
                }
            }

            if (!isDidSignon) {
                for (i = 0; i < signon.length; ++i){
                    signon[i] = 0;
                }

                for (i = 0; i < 7; ++i)
                    signon[i] = commBuffer.getReceiveData().get(commBuffer.getReceiveDataPointer() + 26 + i);
	                isDidSignon = true;
	                for (i = 0; i < signon.length; ++i) {
	                    if (EBCDIC_SIGNON[i] != signon[i]) {
	                        isDidSignon = false;	
	                    };
	                }
            }
        }
        else {
            isDidSignon = false;
        }

        if (!isDidSignon) {
            addNewField(ScreenElementType.LibraryName, blank);
            addNewField(ScreenElementType.MemberName, blank);
            addNewField(ScreenElementType.FormName, blank);
        }
        else
        {
            addNewField(ScreenElementType.LibraryName, "QSYS");
            addNewField(ScreenElementType.MemberName, "SIGNON");
            addNewField(ScreenElementType.FormName, "SIGNON");
        }
	} 

	/**
	 * Add a new field by type and its text
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @param  type ScreenElementType
	 * @param  text value
	 * 
	 * @see ScreenElementType
	 */    
	public void addNewField(ScreenElementType type, String text) {
		addNewField(type, 0, 0, 0, text);
	}
	
	/**
	 * Add a new field by all its attributes
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @param  type ScreenElementType
	 * @param  linearPosition in the raw buffer
	 * @param  length of the field
	 * @param  attributes
	 * @param  text value
	 * 
	 * @see ScreenElementType
	 */    
	public void addNewField(ScreenElementType type, int linearPosition, 
							int length, int attributes, 
							String text) {
		ScreenField screenField = new ScreenField();
		screenField.setType(type);
		screenField.setLinearPosition(linearPosition);
		screenField.setLength(length);
		screenField.setCol(linearPosition % getScreenWidth());
		screenField.setRow(linearPosition / getScreenWidth());
		screenField.setAttributes(attributes);
		screenField.setText(text);
		
		_screenFields.add(screenField);
	}

	/**
	 * Return a buffer raw position by the column and row
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @param  row
	 * @param  col column
	 * 
	 * @return 	raw buffer position
	 */    
    public Integer screenPosn(Integer row, Integer col) {
        return (row - 1) * _screenWidth + col;                     // Returns 1..1920
    }

	/**
	 * Add a new field by all its attributes
	 * and its row and column
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @param  row
	 * @param  col
	 * @param  length
	 * @param  attributes
	 * 
	 */    
    public void addNewField(Integer row, Integer col, Integer length, Integer attributes) {
        Integer scrPosn = screenPosn(row, col) - 1;

        Integer[] tempBuffer = new Integer[length];
        String temp;

        for (Integer i = 0; i < length; ++i) {
            tempBuffer[i] = TelnetCommandValue.NULLCHAR;
        }

        EncodeUtils eu = new EncodeUtils();
        for (Integer idx = 0; idx < length; idx++) {
            tempBuffer[idx] =  getScreenElements()[scrPosn + idx].getValue();
            tempBuffer[idx] = eu.doEbcdicToAscii(_commBuffer, tempBuffer[idx]);
            if ((tempBuffer[idx] - TelnetCommandValue.NULLCHAR) == 0) {
                tempBuffer[idx] = TelnetCommandValue.BLANKCHAR;
            }
            else {
                Integer hexchar = tempBuffer[idx];
                if ((hexchar -XLATEBYTE) >= 0)
                    hexchar = eu.getASCIITOANSI()[hexchar - XLATEBYTE];
                tempBuffer[idx] = hexchar;
            }
        }

        // Ascii Encoding was using CP37 always
        // That's why I replaced it with a String Builder
        StringBuilder oSB = new StringBuilder();
        try {
	        for (Integer i = 0; i < length; i++) {
	        	if(tempBuffer[i] == 32) {
	        		oSB.append(" ");
	        	}
	        	else {
		            if (tempBuffer[i] >= 32) {
		            	byte[] bytesAux = new byte[1];
		            	bytesAux[0] = tempBuffer[i].byteValue();
							oSB.append(new String(bytesAux, "UTF-8"));
		            }
		            else {
		                oSB.append(eu.getUnicode(tempBuffer[i]));
		            }
	        	}
	        }
		} catch (UnsupportedEncodingException e) {
		}
        

        temp = oSB.toString();
        addNewField(ScreenElementType.HostField, scrPosn, length, attributes, temp);
    }
    
	/**
	 * Adds a hostfield by its attributes
	 * and current field
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @param  commScreen CommScreen object
	 * @param  attributes value
	 * 
	 */    
    public void addNewField(CommScreen commScreen, Integer attributes) {
    	int row = commScreen.getFieldRow();
    	int col = commScreen.getFieldCol();
    	int length = commScreen.getFieldLen();
    	EncodeUtils encodeUtils = new EncodeUtils();
    	
        Integer scrPosn = screenPosn(row, col) - 1;

        int[] saTemp = new int[length];
        String temp;

        for (Integer i = 0; i < length; ++i) {
            saTemp[i] = TelnetCommandValue.NULLCHAR;
        }

        for (Integer idx = 0; idx < length; idx++) {
            saTemp[idx] = _screenElements[scrPosn + idx].getValue();
            saTemp[idx] = encodeUtils.doEbcdicToAscii(_commBuffer, saTemp[idx]);
            if ((saTemp[idx] - TelnetCommandValue.NULLCHAR) == 0) {
                saTemp[idx] = TelnetCommandValue.BLANKCHAR;
            }
            else {
                Integer hexchar = saTemp[idx];
                if ((hexchar - XLATEBYTE) >= 0) {
                    hexchar = encodeUtils.getASCIITOANSI()[hexchar - XLATEBYTE];
                }
                saTemp[idx] = hexchar;
            }
        }

        // Ascii Encoding was using CP37 always
        // That's why I replaced it with a String Builder
        StringBuilder oSB = new StringBuilder();
        for (Integer  i = 0; i < length; i++) {
            if (saTemp[i] >= 32) {
                oSB.append((char)saTemp[i]);
            }
            else {
                oSB.append(encodeUtils.getUnicode(saTemp[i]));
            }
        }

        temp = oSB.toString();

        addNewField(ScreenElementType.HostField, scrPosn, length, attributes, temp);
    }

	/**
	 * Adds a ClearDisplayArea field
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @param  fromRow
	 * @param  fromCol
	 * @param  toRow
	 * @param  toCol
	 * 
	 */    
    public void addNewFieldScroll(Integer fromRow, Integer fromCol, Integer toRow, Integer toCol) {
        Integer length = screenPosn(toRow, toCol) - screenPosn(fromRow, fromCol) + 1;
        Integer scrPosn = screenPosn(fromRow, fromCol) - 1;
        addNewField(ScreenElementType.ClearDisplayArea, scrPosn, length, 0, "");
    }

	/**
	 * Adds a new format field
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @param  screenElementType ScreenElementType
	 * @param  data
	 * 
	 * @see ScreenElementType
	 */    
    public void addNewFieldFormat(ScreenElementType screenElementType, String data) {
        addNewField(screenElementType, 0, 0, 0, data);
    }
    
	/**
	 * Adds a new NonTransmitKeys field
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @param  fctKeys
	 * @param  cmdKeys
	 * 
	 */    
    public void addNewFieldKeys(Integer fctKeys, Integer cmdKeys) {
        Integer keys = cmdKeys;
        Integer temp = DataLowLevelUtils.MAKELONG(DataLowLevelUtils.MAKEWORD(0, 0), DataLowLevelUtils.MAKEWORD(0, fctKeys));
        keys = keys | temp;
        addNewField(ScreenElementType.NonTransmitKeys, 0, 0, keys, "");
    }

	/**
	 * Returns raw position by row and column
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @param  row
	 * @param  col
	 * 
	 * @return 	buffer raw position
	 */    
    public Integer rowCol(Integer row, Integer col) {
        if (col > this.getScreenWidth()) {
            Integer temp = (col - 1) / this.getScreenWidth();
            row = row + temp;
            col = col - (temp * this.getScreenWidth());
        }
        return  DataLowLevelUtils.MAKEWORD(DataLowLevelUtils.GetLowByte(col), DataLowLevelUtils.GetLowByte(row)); 
    }
}
