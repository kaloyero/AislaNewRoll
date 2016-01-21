package com.aisla.newrolit.com;

/**
 * SaveScreen class holds all the attributes 
 * of the emulator screen to store saved screens  
 * <p>
 * Main objects are the following
 * <p>
 * @author      Luis Paradela
 * @version     1.0
 * @since       1.0
 */
public class SaveScreen {
	private Integer _tn5250Cmdkeys;
    private Integer _currentRow;
    private Integer _currentCol;
    private Integer _homeRow;
    private Integer _homeCol;
    private Integer _inpfldCount;
    private Integer _fieldReseq;
    private Integer _sfOrderCount;
    private boolean _masterMDT;

    public Integer getTN5250Cmdkeys() {
		return _tn5250Cmdkeys;
	}
	public void setTN5250Cmdkeys(Integer _tn5250Cmdkeys) {
		this._tn5250Cmdkeys = _tn5250Cmdkeys;
	}
	public Integer getCurrentRow() {
		return _currentRow;
	}
	public void setCurrentRow(Integer _currentRow) {
		if(_currentRow > 25) {
			String s = "Nos pasamos mal :)";			
		}
		this._currentRow = _currentRow;
	}
	public Integer getCurrentCol() {
		return _currentCol;
	}
	public void setCurrentCol(Integer _currentCol) {
		this._currentCol = _currentCol;
	}
	public Integer getHomeRow() {
		return _homeRow;
	}
	public void setHomeRow(Integer _homeRow) {
		this._homeRow = _homeRow;
	}
	public Integer getHomeCol() {
		return _homeCol;
	}
	public void setHomeCol(Integer _homeCol) {
		this._homeCol = _homeCol;
	}
	public Integer getInpfldCount() {
		return _inpfldCount;
	}
	public void setInpfldCount(Integer _inpfldCount) {
		this._inpfldCount = _inpfldCount;
	}
	public Integer getFieldReseq() {
		return _fieldReseq;
	}
	public void setFieldReseq(Integer _fieldReseq) {
		this._fieldReseq = _fieldReseq;
	}
	public Integer getSFOrderCount() {
		return _sfOrderCount;
	}
	public void setSFOrderCount(Integer _sfOrderCount) {
		this._sfOrderCount = _sfOrderCount;
	}
	public boolean isMasterMDT() {
		return _masterMDT;
	}
	public void setMasterMDT(boolean _masterMDT) {
		this._masterMDT = _masterMDT;
	}
}
