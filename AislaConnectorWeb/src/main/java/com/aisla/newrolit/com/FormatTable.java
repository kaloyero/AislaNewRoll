package com.aisla.newrolit.com;

/**
 * FormatTable holds all the format table objects 
 * and logic of the green screen emulation
 * <p>
 * Main objects are the following
 * <ul>
 * <li>beginRow
 * <li>beginCol
 * <li>fieldLen
 * <li>nextField
 * <li>ffw (Field Format Word)
 * <li>fcw (Field Control Word)
 * </ul>
 * <p>
 * @author      Luis Paradela
 * @version     1.0
 * @since       1.0
 */
public class FormatTable {
	private Integer beginRow;
    private Integer beginCol;
    private Integer fieldLen;
    private Integer nextField;
    private Integer[] _ffw = new Integer[2];
    private Integer[] _fcw = new Integer[2];
    private boolean guiDefined;

    public Integer getBeginRow() {
		return beginRow;
	}
	
    public void setBeginRow(Integer beginRow) {
		this.beginRow = beginRow;
	}
	
    public Integer getBeginCol() {
		return beginCol;
	}
	
    public void setBeginCol(Integer beginCol) {
		this.beginCol = beginCol;
	}
	
    public Integer getFieldLen() {
		return fieldLen;
	}
	
    public void setFieldLen(Integer fieldLen) {
		this.fieldLen = fieldLen;
	}
	
    public Integer getNextField() {
		return nextField;
	}
	
    public void setNextField(Integer nextField) {
		this.nextField = nextField;
	}
	
    public Integer[] getFFW() {
		return _ffw;
	}
	
    public void setFFW(Integer[] _ffw) {
		this._ffw = _ffw;
	}
	
    public Integer[] getFCW() {
		return _fcw;
	}
	
    public void setFCW(Integer[] _fcw) {
		this._fcw = _fcw;
	}
	
    public boolean isGuiDefined() {
		return guiDefined;
	}
	
    public void setGuiDefined(boolean guiDefined) {
		this.guiDefined = guiDefined;
	}
}
