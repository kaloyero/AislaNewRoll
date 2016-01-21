package com.aisla.newrolit.com;

/**
 * ScreenField class is the model of a screen field 
 * with all its attributes, attributes masks
 * and some basic util methods 
 * <p>
 * Main objects are the following
 * <p>
 * @author      Luis Paradela
 * @version     1.0
 * @since       1.0
 */

// See 
//		http://pic.dhe.ibm.com/infocenter/iseries/v7r1m0/index.jsp?topic=%2Fapis%2Fdsm1f.htm
//		http://community.psion.com/knowledge/w/knowledgebase/ibm-5250-data-stream.aspx

public class ScreenField {

	//Host Field types	
	public int  _ftNonInput = 1;
	public int  _ftProtectedInput = 2;
	public int  _ftInput = 3;
	public int  _ftWindow = 4;
	public int  _ftMask = 5;
	
	//Host Field attributes
	// TODO: These are static attributes (?), there is no need
	// to have one instance of them for each field.
	public int  _atrNonDisplayMask	= 0x7;
	public int  _atrReverseImage = 0x1;				// If this bit is set, color displays on background. 
	public int  _atrUnderscore = 0x4;
	public int  _atrSeparatorMask = 0x18;
	public int  _atrSeparator = 0x10;
	public int  _atrColorMask = 0x1a;
	public int  _atrGreen = 0;
	public int  _atrWhite = 0x2;
	public int  _atrRed = 0x8;
	public int  _atrRedBlink = 0xa;
	public int  _atrTurquoise = 0x10;
	public int  _atrYellow = 0x12;
	public int  _atrPink = 0x18;
	public int  _atrBlue = 0x1a;
	public int  _atrInputField = 0x100;
	public int  _atrCheck10	= 0x1000;
	public int  _atrCheck11	= 0x2000;
	public int  _atrAdjustFillMask	= 0x70000;
	public int  _atrNoAdjust = 0;
	public int  _atrAdjustZeroFill = 0x50000;
	public int  _atrAdjustBlankFill	= 0x60000;
	public int  _atrMandatoryFill = 0x70000;		// This field must be entirely filled if any data is entered to it.
	public int  _atrMandatoryEnter = 0x80000;
	public int  _atrMonocase = 0x200000;			// Translate lower-case characters entered into this field to upper-case
	public int  _atrExitRequired = 0x400000;
	public int  _atrAutoEnter = 0x800000;			// Simulates an ENTER key press at the conclusion of the field. 
	public int  _atrShiftEditMask = 0x7000000;
	public int  _atrAlphaShift = 0;
	public int  _atrAlphaOnly = 0x1000000;
	public int  _atrNumericShift = 0x2000000;
	public int  _atrNumericOnly	= 0x3000000;
	public int  _atrKatakanaShift = 0x4000000;
	public int  _atrDigitsOnly = 0x5000000;
	public int  _atrFeatureInput = 0x6000000;
	public int  _atrSignedNumeric = 0x7000000;
	public int  _atrMDT = 0x8000000;
	public int  _atrDup	= 0x10000000;
	public int  _atrBypass = 0x20000000;
	
	private ScreenElementType _type;
    private int _linearPosition = 0;
    private int _length = 0;
    private int _attributes = 0;
	private int _col = 0;
    private int _row = 0;
    private boolean _visible = true;
    private String _text = "";

    public int getCol() {
		return _col;
	}

	public int getCol1Based() {
		return (_col + 1);
	}
    
	public void setCol(int col) {
		this._col = col;
	}
	
	public int getRow() {
		return _row;
	}
	
	public int getRow1Based() {
		return (_row + 1);
	}

	public void setRow(int row) {
		this._row = row;
	}

	public boolean getVisible() {
		return this._visible;
	}
	
	public void setVisible(boolean visible) {
		this._visible = visible;
	}
	
	/**
	 * Returns a GSColor color 
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @param  none
	 * 
	 * @return 	GSColor enum
	 */    
	public GSColor getColor() {
		if((this.getAttributes() &_atrGreen) == _atrGreen) {return GSColor.atrGreen; }
		if((this.getAttributes() &_atrWhite) == _atrWhite) {return GSColor.atrWhite; }
		if((this.getAttributes() &_atrRed) == _atrRed) {return GSColor.atrRed; }
		if((this.getAttributes() &_atrRedBlink) == _atrRedBlink) {return GSColor.atrRedBlink; }
		if((this.getAttributes() &_atrTurquoise) == _atrTurquoise) {return GSColor.atrTurquoise; }
		if((this.getAttributes() &_atrYellow) == _atrYellow) {return GSColor.atrYellow; }
		if((this.getAttributes() &_atrPink) == _atrPink) {return GSColor.atrPink; }
		if((this.getAttributes() &_atrBlue) == _atrBlue) {return GSColor.atrBlue; }
		
		return null;
	}
	
	public boolean isInverted()
	{
		// A field is inverted (color displays on background)
		// when the last bit in _attributes is set.
		// The attribute nonDisplayMask has this bit set also, so
		// we must also check that this attribute is not set.
		return 
			(this._attributes & _atrReverseImage) == _atrReverseImage &&
			(this._attributes & _atrNonDisplayMask) != _atrNonDisplayMask;
	}
	
	public boolean isMonocase()
	{
		return (_attributes & _atrMonocase) == _atrMonocase;
	}

	// TODO: No field except popup fields use this attribute.
	public boolean isNonDisplay() 
	{
		return (this._attributes & _atrNonDisplayMask) == _atrNonDisplayMask;
	}

	/**
	 * Checks for a valid key (it is a good feature for agents for example)  
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @param  key value to be evaluated
	 * 
	 * @return 	true / false
	 */    
	public boolean isKey(String key) {
        StringBuilder fieldKey = new StringBuilder();
        fieldKey.append("00");
        fieldKey.append(DataLowLevelUtils.PadLeftZeros(String.valueOf(this.getRow1Based()), 2));
        fieldKey.append(DataLowLevelUtils.PadLeftZeros(String.valueOf(this.getCol1Based()), 3));
		return fieldKey.toString().equals(key);
	}
	
	public ScreenElementType getType() {
		return _type;
	}
	public int getLinearPosition() {
		return _linearPosition;
	}
	public int getLinearPosition(int screenWidth) {
		return this.getCol() + this.getRow() * screenWidth;
	}
	public int getLength() {
		return _length;
	}
	public int getAttributes() {
		return _attributes;
	}
	public String getText() {
		return _text;
	}
	public void setType(ScreenElementType type) {
		this._type = type;
	}
	public void setLinearPosition(int linearPosition) {
		this._linearPosition = linearPosition;
	}
	public void setLength(int length) {
		this._length = length;
	}
	public void setAttributes(int attributes) {
		this._attributes = attributes;
	}
	public void setText(String text) {
		this._text = text;
	}
	
	/**
	 * Based on field attributes return the field type 
	 * as Label or Input classification  
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @return 	ScreenFieldType
	 */    
	public ScreenFieldType getFieldType() {
    		int attribute = 0;
    		try {
    			attribute = _attributes & _atrBypass + _atrInputField + 0x80000000;
    		}
    		catch(Exception e) {
    		}
        	
        	if(attribute == 0) {
        		return ScreenFieldType.Label;
        	}
        	
        	if(attribute == _atrInputField) {
        		return ScreenFieldType.Input;
        	}
        	
        	if(attribute == (_atrBypass + _atrInputField)) {
        		return ScreenFieldType.Label;
        	}

        	return ScreenFieldType.Label;
	}
	
	/**
	 * Raw data is a serialization of all the field attributes
	 * in a string separated by pipes 
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @return 	String with the serialization of all the field attributes
	 * separated by pipes
	 */    
	public String getRawData() {
		StringBuilder response = new StringBuilder();
		response.append(this.getFieldType().toString());
		response.append("|");
		response.append(this.getAttributes());
		response.append("|");
		response.append(this.getLength());
		response.append("|");
		response.append(this.getRow());
		response.append("|");
		response.append(this.getCol());
		return response.toString();
	}
	
	/**
	 * Raw data is a serialization of all the field attributes
	 * in a string separated by pipes with more detailes
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @return 	String with the serialization of all the field attributes
	 * separated by pipes
	 */    
	public String getLogData() {
		StringBuilder response = new StringBuilder();
		response.append(this.getType().toString());
		response.append("|");
		response.append(this.getFieldType().toString());
		response.append("|");
		response.append(this.getAttributes());
		response.append("|");
		response.append(this.getLength());
		response.append("|");
		response.append(this.getRow());
		response.append("|");
		response.append(this.getCol());
		response.append("|");
		response.append(this.getText());
		return response.toString();
	}

	/**
	 * Clone an instance of the ScreenField object
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @return 	ScreenField
	 */    
	public ScreenField clone(){
		ScreenField newField = new ScreenField();
		
		newField.setAttributes(_attributes);
		newField.setLength(_length);
		newField.setCol(_col);
		newField.setRow(_row);
		newField.setVisible(_visible);
		newField.setText(_text);
		newField.setLinearPosition(_linearPosition);
		newField.setType(_type);
		
		return newField;
	}
	
	/**
	 * Return a field id by its column and row
	 * <p>
	 * This method always returns immediately. 
	 *
	 * @return 	string with the field ID
	 */    
	public String getGSID(){
		String prefix = "00";
		String row = DataLowLevelUtils.PadLeftZeros(String.valueOf(getRow1Based()), 2);
		String col = DataLowLevelUtils.PadLeftZeros(String.valueOf(getCol1Based()), 3);		
		return prefix + row + col;
	}
}
