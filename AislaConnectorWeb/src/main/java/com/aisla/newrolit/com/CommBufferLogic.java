package com.aisla.newrolit.com;

import com.aisla.newrolit.customization.GSBufferEmulator;
import com.aisla.newrolit.connections.ConnectionData;
import java.io.*;
import java.util.*;

/**
 * CommBufferLogic is the global buffer that holds all the data
 * and logic of the green screen emulation
 * <p>
 * Main objects are the following
 * <ul>
 * <li>GSBufferEmulator
 * <li>receiveData buffer
 * <li>sendData buffer
 * <li>wtdCmdCtrl write to display commands
 * <li>FormatTable object
 * </ul>
 * <p>
 * @author      Luis Paradela
 * @version     1.0
 * @since       1.0
 */
public class CommBufferLogic {
	private GSBufferEmulator _gsBufferEmulator = new GSBufferEmulator();
	private List<Integer> _receiveData = new ArrayList<Integer>();
	private ReceiveDataQ _receiveDataQ = new ReceiveDataQ();
	private List<Integer> _sendData = new ArrayList<Integer>();
	private List<Integer> _auxBuffer = new ArrayList<Integer>();
	private List<Integer> _wtdCmdCtrl = new ArrayList<Integer>();
	private FormatTable [] _formatTable = new FormatTable[256];
	private Integer[] _fieldFmtTblPosn = new Integer[256];

	private SaveScreen _saveScreen = new SaveScreen();

	private CommScreen _commScreen = new CommScreen(this);

	private ConnectionData _connectionData = null;

	private int _receiveDataPointer = 0;
	private int _sendDataPointer = 0;

	private boolean _negotiating = false;
	private boolean _haveDataStream = false;
	private boolean _hadNewFmt = false;
	private boolean _cancelInvite = false;
	private boolean _doBinary = false;
	private boolean _doBinaryAgain = false;
	private boolean _doDoEndrecord = false;
	private boolean _doDataStream = false;

	private boolean _isDsp28x132 = true;
    private boolean _isColorTube = true;
    private boolean _isClrUnitAlt = false;
    private boolean _isInpField = false;
    private boolean _isInpFieldUpdate = false;
	private boolean _isTextField = false;
	private boolean _isProcessError = false;
	private boolean _isRptNull = false;
	private boolean _isMasterMDT = false;
	private boolean _isSetHomePosn = false;
	private boolean _haveSOH = false;

	private int _negotiationNumber = 0;

	private int _currentRow = 0;
	private int _currentCol = 0;
	private int _homeRow = 0;
	private int _homeCol = 0;
	private int _saveHomeRow = 0;
	private int _saveHomeCol = 0;

	private int _tn5250Attr = 0;
	private int _tn5250Cmdkeys = 0;
	private int _tn5250AidCode = 0;
	private int _bbCOMAidKey = 0; // AID processing
	private int _errorLine = _commScreen.getScreenHeight() + 1;

    private int _sbaRow = 0; // Processing orders
	private int _sbaCol = 0; // Processing orders

	private int _fieldReseq = 0;
	private int _fieldLength = 0;
	private int _fieldRow = 0;
	private int _fieldCol = 0;
	private int _lastFieldLength = 0;
	private int _sfOrderCount = 0;
	
	private int _receiveCountKiller = 0;

	private ReceiverListener _receiveListener = null;
	
	private boolean waiting = false;
	
	public boolean isWaiting(){
		 return waiting;
	 }
	 
	 public void setWaiting(boolean temp){
		 this.waiting = temp;
	 }

	/**
	 * Returns a boolean to check if the update field is an input or not.
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return      true / false
	 */
	public boolean isInpFieldUpdate() {
		return _isInpFieldUpdate;
	}

	/**
	 * sets if the update field is an input or not.
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param      _isInpFieldUpdate
	 */
	public void setInpFieldUpdate(boolean _isInpFieldUpdate) {
		this._isInpFieldUpdate = _isInpFieldUpdate;
	}

	/**
	 * Returns the GSBufferEmulator object
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return      GSBufferEmulator object
	 * @see  GSBufferEmulator
	 */
	public GSBufferEmulator getGsBufferEmulator() {
		return _gsBufferEmulator;
	}

	/**
	 * Set the GSBufferEmulator object
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param     gsBufferEmulator GSBufferEmulator object
	 * @see  GSBufferEmulator
	 */
	public void setGsBufferEmulator(GSBufferEmulator gsBufferEmulator) {
		this._gsBufferEmulator = gsBufferEmulator;
	}

	/**
	 * Returns the do data stream status, which is used during
	 * the telnet negotiation operations
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return      true / false
	 * @see         TelnetNegotiationLogic
	 */
	public boolean isDoDataStream() {
		return _doDataStream;
	}

	/**
	 * Set the do data stream status, which is used during
	 * the telnet negotiation operations
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param      doDataStream true / false
	 * @see         TelnetNegotiationLogic
	 */
	public void setDoDataStream(boolean doDataStream) {
		this._doDataStream = doDataStream;
	}

	/**
	 * Returns the aid key, which is used
	 * by Do5250BinaryResponse method
	 * the telnet negotiation operations
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return      true / false
	 * @see         Do5250BinaryResponse
	 */
	public int getBBCOMAidKey() {
		return _bbCOMAidKey;
	}

	/**
	 * Set the aid key, which is used
	 * by Do5250BinaryResponse method
	 * the telnet negotiation operations
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param      _bbCOMAidKey true / false
	 * @see         Do5250BinaryResponse
	 */
	public void setBBCOMAidKey(int _bbCOMAidKey) {
		this._bbCOMAidKey = _bbCOMAidKey;
	}

	/**
	 * Returns the error line number which depends
	 * on the screen mode 24 or 27 lines
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return      the error line number
	 * @see         getCommScreen
	 */
	public int getErrorLine() {
		return _errorLine;
	}

	/**
	 * Set the error line number which depends
	 * on the screen mode 24 or 27 lines
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param      errorLine the error line number
	 * @see         getCommScreen
	 */
	public void setErrorLine(int errorLine) {
		this._errorLine = errorLine;
	}

	/**
	 * Returns the last buffer address col position
	 * result of an SBA operation
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return      a valid column id
	 * @see         getSBARow
	 */
    public int getSBACol() {
		return _sbaCol;
	}

	/**
	 * Set the last buffer address col position
	 * result of an SBA operation
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param      sbaCol a valid column id
	 * @see         getSBARow
	 */
	public void setSBACol(int sbaCol) {
		this._sbaCol = sbaCol;
	}

	/**
	 * Returns the last buffer address row position
	 * result of an SBA operation
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return      a valid row id
	 * @see         getSBACol
	 */
	public int getSBARow() {
		return _sbaRow;
	}

	/**
	 * Set the last buffer address row position
	 * result of an SBA operation
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param     sbaRow a valid row id
	 * @see         getSBACol
	 */
	public void setSBARow(int sbaRow) {
		this._sbaRow = sbaRow;
	}

	/**
	 * Returns the last aid code (keystroke)
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return      a valid aid code
	 */
	public int getTN5250AidCode() {
		return _tn5250AidCode;
	}

	/**
	 * Set the last aid code (keystroke)
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param     _tn5250AidCode a valid aid code
	 */
	public void setTN5250AidCode(int _tn5250AidCode) {
		this._tn5250AidCode = _tn5250AidCode;
	}

	/**
	 * Returns the SaveScreen object
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return      a SaveScreen object
	 * @see         SaveScreen
	 */
	public SaveScreen getSaveScreen() {
		return _saveScreen;
	}

	/**
	 * Set the SaveScreen object
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param      _saveScreen a SaveScreen object
	 * @see         SaveScreen
	 */
	public void setSaveScreen(SaveScreen _saveScreen) {
		this._saveScreen = _saveScreen;
	}

	/**
	 * Returns the SF (start fields orders) count
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return      SF orders count
	 */
	public int getSFOrderCount() {
		return _sfOrderCount;
	}

	/**
	 * Set the SF (start fields orders) count
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param      _sfOrderCount SF orders count
	 */
	public void setSFOrderCount(int _sfOrderCount) {
		this._sfOrderCount = _sfOrderCount;
	}

	/**
	 * Returns the length of the last field added
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return      a field length
	 */
	public int getLastFieldLength() {
		return _lastFieldLength;
	}

	/**
	 * Set the length of the last field added
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param     _lastFieldLength a field length
	 */
	public void setLastFieldLength(int _lastFieldLength) {
		this._lastFieldLength = _lastFieldLength;
	}

	/**
	 * Returns the lenght of the current field
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return      a field length
	 */
	public int getFieldLength() {
		return _fieldLength;
	}

	/**
	 * Set the length of the current field
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param     fieldLength a field length
	 */
	public void setFieldLength(int fieldLength) {
		this._fieldLength = fieldLength;
	}

	/**
	 * Returns the current field resequence
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return      a field length
	 * @see         FormatTable
	 */
	public int getFieldReseq() {
		return _fieldReseq;
	}

	/**
	 * Set the current field resequence
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param      _fieldReseq a field length
	 * @see         FormatTable
	 */
	public void setFieldReseq(int _fieldReseq) {
		this._fieldReseq = _fieldReseq;
	}

	/**
	 * Returns the current command key
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return      an integer representing the command key
	 */
	public int getTN5250Cmdkeys() {
		return _tn5250Cmdkeys;
	}

	/**
	 * Set the current command key
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param _tn5250Cmdkeys an integer representing the command key
	 */
	public void setTN5250Cmdkeys(int _tn5250Cmdkeys) {
		this._tn5250Cmdkeys = _tn5250Cmdkeys;
	}

	/**
	 * Returns if the logic flow is in a set cursor position order as a SF command
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return      true / false
	 */
	public boolean isSetHomePosn() {
		return _isSetHomePosn;
	}

	/**
	 * Set if the logic flow is in a set cursor position order as a SF command
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param      _isSetHomePosn true / false
	 */
	public void setHomePosn(boolean _isSetHomePosn) {
		this._isSetHomePosn = _isSetHomePosn;
	}

	/**
	 * Returns if is an Modified Data Tag (MDT)
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return      true / false
	 */
	public boolean isMasterMDT() {
		return _isMasterMDT;
	}

	/**
	 * Set if is an Modified Data Tag (MDT)
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param      _isMasterMDT true / false
	 */
	public void setMasterMDT(boolean _isMasterMDT) {
		this._isMasterMDT = _isMasterMDT;
	}

	/**
	 * Returns if is an Start of header (SOH)
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return      true / false
	 */
	public boolean isHaveSOH() {
		return _haveSOH;
	}

	/**
	 * Set if is an Start of header (SOH)
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param      haveSOH true / false
	 */
	public void setHaveSOH(boolean haveSOH) {
		this._haveSOH = haveSOH;
	}

	/**
	 * Returns current field attribute
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return      an integer representing the field attribute
	 */
	public int getTN5250Attr() {
		return _tn5250Attr;
	}

	/**
	 * Set current field attribute
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param     _tn5250Attr an integer representing the field attribute
	 */
	public void setTN5250Attr(int _tn5250Attr) {
		this._tn5250Attr = _tn5250Attr;
	}

	/**
	 * Returns current the current cursor row
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return      an integer representing the cursor row
	 */
	public int getHomeRow() {
		return _homeRow;
	}

	/**
	 * Set current the current cursor row
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param     homeRow an integer representing the cursor row
	 */
	public void setHomeRow(int homeRow) {
		this._homeRow = homeRow;
	}

	/**
	 * Returns current the current cursor column
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return      an integer representing the cursor column
	 */
	public int getHomeCol() {
		return _homeCol;
	}

	/**
	 * Set current the current cursor column
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param      homeCol an integer representing the cursor column
	 */
	public void setHomeCol(int homeCol) {
		this._homeCol = homeCol;
	}

	/**
	 * Returns current the current cursor column for saved screen
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return      an integer representing the cursor column for saved screen
	 */
	public int getSaveHomeCol() {
		return _saveHomeCol;
	}

	/**
	 * Setcurrent the current cursor column for saved screen
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param      saveHomeCol an integer representing the cursor column for saved screen
	 */
	public void setSaveHomeCol(int saveHomeCol) {
		this._saveHomeCol = saveHomeCol;
	}

	/**
	 * Returns current the current cursor row for saved screen
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return      an integer representing the cursor row for saved screen
	 */
	public int getSaveHomeRow() {
		return _saveHomeRow;
	}

	/**
	 * Set current the current cursor row for saved screen
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param     saveHomeRow an integer representing the cursor row for saved screen
	 */
	public void setSaveHomeRow(int saveHomeRow) {
		this._saveHomeRow = saveHomeRow;
	}

	private ProcessMode _processingMode = ProcessMode.modeConnect;

	/**
	 * Returns the current process mode used in the putPacket method logic
	 * (modeAbort,modeConnect,modePutPackets,modeReadInpflds,modeReadMDTflds,
	 * modeSaveScreen,modeTerminate,modeReadModAll,modeReadBuffer,modeReadModified)
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	a ProccessMode enum value
	 * @see 	ProcessMode
	 */
	public ProcessMode getProcessingMode() {
		return _processingMode;
	}

	/**
	 * set the current process mode used in the putPacket method logic
	 * (modeAbort,modeConnect,modePutPackets,modeReadInpflds,modeReadMDTflds,
	 * modeSaveScreen,modeTerminate,modeReadModAll,modeReadBuffer,modeReadModified)
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param 	processingMode a ProccessMode enum value
	 * @see 	ProcessMode
	 */
	public void setProcessingMode(ProcessMode processingMode) {
		this._processingMode = processingMode;
	}

	/**
	 * Returns if it is a null char
	 * used in a Repeat to Address (RA) command
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	true / false
	 */
	public boolean isRptNull() {
		return _isRptNull;
	}

	/**
	 * Set if it is a null char
	 * used in a Repeat to Address (RA) command
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param 	isRptNull true / false
	 */
	public void setRptNull(boolean isRptNull) {
		this._isRptNull = isRptNull;
	}

	/**
	 * Returns if the current field is a text field (contains text characters between 0x20 and 0x3F)
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	true / false
	 */
	public boolean isTextField() {
		return _isTextField;
	}

	/**
	 * Set if the current field is a text field (contains text characters between 0x20 and 0x3F)
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param 	isTextField true / false
	 */
	public void setTextField(boolean isTextField) {
		this._isTextField = isTextField;
	}

	/**
	 * Returns if the current field is a process error (WRITE ERRCODE operation)
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	true / false
	 */
	public boolean isProcessError() {
		return _isProcessError;
	}

	/**
	 * Set if the current field is a process error (WRITE ERRCODE operation)
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param	isProcessError true / false
	 */
	public void setProcessError(boolean isProcessError) {
		this._isProcessError = isProcessError;
	}

	/**
	 * Returns if the current field is an input field
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	true / false
	 */
	public boolean isInpField() {
		return _isInpField;
	}

	/**
	 * Set if the current field is an input field
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param 	isInpField true / false
	 */
	public void setInpField(boolean isInpField) {
		this._isInpField = isInpField;
	}

	/**
	 * Returns the current row
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	current row
	 */
	public int getCurrentRow() {
		return _currentRow;
	}

	/**
	 * Returns the current column
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	current column
	 */
	public int getCurrentCol() {
		return _currentCol;
	}

	/**
	 * Set the current row
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param 	_currentRow current row
	 */
	public void setCurrentRow(int _currentRow) {
		//if(_currentRow > 25) {
		//	String s = "Nos pasamos mal :)";
		//}
		this._currentRow = _currentRow;
	}

	/**
	 * Set the current column
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param 	_currentCol current column
	 */
	public void setCurrentCol(int _currentCol) {
		this._currentCol = _currentCol;
	}

	/**
	 * Returns the Write to Display (WDT) commands list
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	list of WDT commands
	 */
	public List<Integer> getWTDCmdCtrl() {
		return _wtdCmdCtrl;
	}

	/**
	 * Set the Write to Display (WDT) commands list
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param 	_wtdCmdCtrl list of WDT commands
	 */
	public void setWTDCmdCtrl(List<Integer> _wtdCmdCtrl) {
		this._wtdCmdCtrl = _wtdCmdCtrl;
	}

	/**
	 * Returns if the current operation is a clear unit alt
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	true / false
	 */
	public boolean isClrUnitAlt() {
		return _isClrUnitAlt;
	}

	/**
	 * Set if the current operation is a clear unit alt
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param 	clrUnitAlt true / false
	 */
	public void setClrUnitAlt(boolean clrUnitAlt) {
		this._isClrUnitAlt = clrUnitAlt;
	}

	/**
	 * Returns if the current display is 132 x 28 format
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	true / false
	 */
	public boolean isDsp28x132() {
		return _isDsp28x132;
	}

	/**
	 * Returns if the current display is color
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	true / false
	 */
	public boolean isColorTube() {
		return _isColorTube;
	}

	/**
	 * Set if the current display is 132 x 28 format
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param	_dsp28x132 true / false
	 */
	public void setDsp28x132(boolean _dsp28x132) {
		this._isDsp28x132 = _dsp28x132;
	}

	/**
	 * Set if the current display is color
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param 	_colorTube true / false
	 */
	public void setColorTube(boolean _colorTube) {
		this._isColorTube = _colorTube;
	}

	/**
	 * Returns if the current negotiation number
	 * specific for this implementation
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	true / false
	 */
	public int getNegotiationNumber() {
		return _negotiationNumber;
	}

	/**
	 * Set if the current negotiation number
	 * specific for this implementation
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param 	_negotiationNumber true / false
	 */
	public void setNegotiationNumber(int _negotiationNumber) {
		this._negotiationNumber = _negotiationNumber;
	}

	/**
	 * increments the negotiation number
	 * <p>
	 * This method always returns immediately.
	 *
	 */
	public void incNegotiationNumber() {
		this._negotiationNumber++;
	}

	public ReceiveDataQ getReceiveDataQ() {
		if(_receiveDataQ == null) {
			_receiveDataQ = new ReceiveDataQ();
		}
		return _receiveDataQ;
	}

	public void setReceiveDataQ(ReceiveDataQ receiveDataQ) {
		this._receiveDataQ = receiveDataQ;
	}

	/**
	 * Returns the received data as a list of integers
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	list of integers with the data from host
	 */
	public List<Integer> getReceiveData() {
		return _receiveData;
	}

	/**
	 * Returns the current byte of the received data list
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	the current byte (as an integer)
	 */
	public Integer getCurrentByte() {
		if(_receiveData.size() <= this._receiveDataPointer) {
			return 0;
		}
		return _receiveData.get(this._receiveDataPointer);
	}

	/**
	 * Returns next byte as an integer
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	next byte as an integer
	 */
	public Integer getNextByte() {
		Integer result = 0;
		if(_receiveData.size() > (this._receiveDataPointer + 1)) {
			result = _receiveData.get(this._receiveDataPointer + 1);
		}
		return result;
	}

	/**
	 * Set the received data as a list of integers
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param 	_receiveData list of integers with the data from host
	 */
	public void setReceiveData(List<Integer> _receiveData) {
		this._receiveData = _receiveData;
	}

	/**
	 * Returns the send data buffer as a list of integers
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	list of integers with the data to send to host
	 */
	public List<Integer> getSendData() {
		return _sendData;
	}

	/**
	 * Set the send data buffer as a list of integers
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param 	_sendData list of integers with the data to send to host
	 */
	public void setSendData(List<Integer> _sendData) {
		this._sendData = _sendData;
	}

	/**
	 * Returns the received data buffer current pointer value
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	received data buffer current pointer value
	 */
	public int getReceiveDataPointer() {
		return _receiveDataPointer;
	}

	/**
	 * Set the received data buffer current pointer value
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param 	_receiveDataPointer received data buffer current pointer value
	 */
	public void setReceiveDataPointer(int _receiveDataPointer) {
		this._receiveDataPointer = _receiveDataPointer;
	}

	/**
	 * Increment the send data buffer pointer in any value
	 * <p>
	 * This method always returns immediately.
	 *
	 */
	public void incSendDataPointer(int increment) {
		this._sendDataPointer += increment;
	}

	/**
	 * Increment the send data buffer pointer
	 * <p>
	 * This method always returns immediately.
	 *
	 */
	public void incSendDataPointer() {
		this._sendDataPointer++;
	}

	/**
	 * Increment the receive data buffer pointer
	 * <p>
	 * This method always returns immediately.
	 *
	 */
	public void incReceiveDataPointer() {
		this._receiveDataPointer++;
	}

	/**
	 * Decrement the received data buffer pointer
	 * <p>
	 * This method always returns immediately.
	 *
	 */
	public void decReceiveDataPointer() {
		this._receiveDataPointer--;
	}

	/**
	 * Increment the receive data buffer pointer in any amount
	 * <p>
	 * This method always returns immediately.
	 *
	 */
	public void incReceiveDataPointer(int amount) {
		this._receiveDataPointer += amount;
	}

	/**
	 * Return the send data buffer pointer
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	the send data buffer pointer
	 */
	public int getSendDataPointer() {
		return _sendDataPointer;
	}

	/**
	 * Set the send data buffer pointer
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param 	_sendDataPointer the send data buffer pointer
	 */
	public void setSendDataPointer(int _sendDataPointer) {
		this._sendDataPointer = _sendDataPointer;
	}

	/**
	 * Return the received data buffer length (which is an array of int)
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	the received data buffer length
	 */
	public int getReceiveCount() {
		return this._receiveData.size();
	}

	/**
	 * Return if it is a negotiation operation in progress or not
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	true / false
	 */
	public boolean is_negotiating() {
		return _negotiating;
	}

	/**
	 * Set if it is a negotiation operation in progress or not
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param 	_negotiating true / false
	 */
	public void setNegotiating(boolean _negotiating) {
		this._negotiating = _negotiating;
	}

	/**
	 * Return if there is more data to read from the receive buffer
	 * or current byte is TNC_IAC which means no more to process
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	true / false
	 */
	public boolean isHaveDataStream() {
		return _haveDataStream;
	}

	/**
	 * Return the auxiliary buffer use for intermediate processing
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	data as list of integers
	 */
	public List<Integer> getAuxBuffer() {
		return _auxBuffer;
	}

	/**
	 * Set the auxiliary buffer use for intermediate processing
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param 	_auxBuffer data as list of integers
	 */
	public void setAuxBuffer(List<Integer> _auxBuffer) {
		this._auxBuffer = _auxBuffer;
	}

	/**
	 * Return if there is more data to read from the receive buffer
	 * or current byte is TNC_IAC which means no more to process
	 * and also set the local boolean for use with isHaveDataStream getter
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	true / false
	 */
	public boolean evalHaveDataStream() {
		_haveDataStream = !((getCurrentByte() - TelnetCommandValue.TNC_IAC) == 0);
		return _haveDataStream;
	}

	/**
	 * Return if there is a new format
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	true / false
	 */
	public boolean isNewFmt() {
		return _hadNewFmt;
	}

	/**
	 * Set if there is a new format
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param 	_hadNewFmt true / false
	 */
	public void setHadNewFmt(boolean _hadNewFmt) {
		this._hadNewFmt = _hadNewFmt;
	}

	/**
	 * Return the CommScreen object
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	CommScreen object
	 * @see	CommScreen
	 */
	public CommScreen getCommScreen() {
		return _commScreen;
	}

	/**
	 * Set the CommScreen object
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param 	_commScreen CommScreen object
	 * @see	CommScreen
	 */
	public void setCommScreen(CommScreen _commScreen) {
		this._commScreen = _commScreen;
	}

	/**
	 * Return if there is a cancel invite sent by the host
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	true / false
	 */
	public boolean isCancelInvite() {
		return _cancelInvite;
	}

	/**
	 * Set if there is a cancel invite sent by the host
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param 	_cancelInvite true / false
	 */
	public void setCancelInvite(boolean _cancelInvite) {
		this._cancelInvite = _cancelInvite;
	}

	/**
	 * Return if there is a do binary status,
	 * used in the TNO_TBIN (transmit binary) operation
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	true / false
	 */
	public boolean isDoBinary() {
		return _doBinary;
	}

	/**
	 * Set if there is a do binary status,
	 * used in the TNO_TBIN (transmit binary) operation
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param 	_doBinary true / false
	 */
	public void setDoBinary(boolean _doBinary) {
		this._doBinary = _doBinary;
	}

	/**
	 * Return if there is a do binary status,
	 * used in the TNO_TBIN (transmit binary) operation
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	true / false
	 */
	public boolean isDoBinaryAgain() {
		return _doBinaryAgain;
	}

	/**
	 * Set if there is a do binary status,
	 * used in the TNO_TBIN (transmit binary) operation
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param 	_doBinaryAgain true / false
	 */
	public void setDoBinaryAgain(boolean _doBinaryAgain) {
		this._doBinaryAgain = _doBinaryAgain;
	}

	/**
	 * Return if there is a do binary status,
	 * used in the TNO_EOR (End of Record) operation
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	true / false
	 */
	public boolean isDoDoEndrecord() {
		return _doDoEndrecord;
	}

	/**
	 * Set if there is a do binary status,
	 * used in the TNO_EOR (End of Record) operation
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param 	_doDoEndrecord true / false
	 */
	public void setDoDoEndrecord(boolean _doDoEndrecord) {
		this._doDoEndrecord = _doDoEndrecord;
	}

	/**
	 * Return a ConnectionData object
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	ConnectionData object
	 * @see	ConnectionData
	 */
	public ConnectionData getConnectionData() {
		return _connectionData;
	}

	/**
	 * Set a ConnectionData object
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param 	_connectionData ConnectionData object
	 * @see	ConnectionData
	 */
	public void setConnectionData(ConnectionData _connectionData) {
		this._connectionData = _connectionData;
	}

	/**
	 * Return a FormatTable array
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	FormatTable array
	 * @see	FormatTable
	 */
	public FormatTable[] getFormatTable() {
		return _formatTable;
	}

	/**
	 * Set a FormatTable array
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param 	_formatTable FormatTable array
	 * @see	FormatTable
	 */
	public void setFormatTable(FormatTable[] _formatTable) {
		this._formatTable = _formatTable;
	}

	/**
	 * Return an array  of positions of fields
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	array of positions of fields
	 * @see	FormatTable
	 */
	public Integer[] getFieldFmtTblPosn() {
		return _fieldFmtTblPosn;
	}

	/**
	 * Set an array of positions of fields
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param 	fieldFmtTblPosn array of positions of fields
	 * @see	FormatTable
	 */
	public void setFieldFmtTblPosn(Integer[] fieldFmtTblPosn) {
		this._fieldFmtTblPosn = fieldFmtTblPosn;
	}

	/**
	 * Return current field row
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	current field row
	 */
	public int getFieldRow() {
		return _fieldRow;
	}

	/**
	 * Set current field row
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param 	fieldRow current field row
	 */
	public void setFieldRow(int fieldRow) {
		this._fieldRow = fieldRow;
	}

	/**
	 * Return current field col
	 * <p>
	 * This method always returns immediately.
	 *
	 * @return 	current field col
	 */
	public int getFieldCol() {
		return _fieldCol;
	}

	/**
	 * Set current field col
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param 	fieldCol current field col
	 */
	public void setFieldCol(int fieldCol) {
		this._fieldCol = fieldCol;
	}

	/**
	 * Reset buffer state
	 * <p>
	 * This method always returns immediately.
	 *
	 */
	public void reset() {
		this._receiveDataPointer = 0;
		this._sendDataPointer = 0;
		this._receiveData.clear();
		this._haveDataStream = false;
	}

	/**
	 * Reset receive buffer
	 * <p>
	 * This method always returns immediately.
	 *
	 */
	public void resetRec() {
		this._receiveDataPointer = 0;
		this._receiveData.clear();
	}

	/**
	 * Reset send buffer
	 * <p>
	 * This method always returns immediately.
	 *
	 */
	public void resetSend() {
		this._sendDataPointer = 0;
		this._sendData.clear();
	}

	/**
	 * Send data in the send buffer
	 * <p>
	 * This method always returns immediately.
	 *
	 * @param  connectionData ConnectionData object
	 */
	public void send(ConnectionData connectionData) throws IOException {
//		SimpleLogger.writeLine("send", "Begin send", connectionData.isWriteLog());
		byte [] buffer = new byte[32768];
		Integer bufferPointer = 0;
		for(int i = 0; i < this._sendData.size(); ++i) {
			if(this._sendData.get(i) == null) {
				this._sendData.set(i, 0x40);
			}

		}

		Iterator<Integer> sendBufferIterator = this._sendData.iterator();
		
    	while(sendBufferIterator.hasNext()) {
   			buffer[bufferPointer] = sendBufferIterator.next().byteValue();
			bufferPointer++;
    	}
    	if(connectionData.getOutputStream() == null) {
	    	OutputStream rawOut = connectionData.getSocket().getOutputStream();
	    	connectionData.setOutputStream(new BufferedOutputStream(rawOut, 32768));
    	}
    	connectionData.getOutputStream().write(buffer, 0, this._sendData.size());
    	connectionData.getOutputStream().flush();
    	waiting = true;

//		SimpleLogger.writeData("send", this._sendData, this);
//		SimpleLogger.writeLine("send", "End send", connectionData.isWriteLog());
	}

	/**
	 * Receives data in the receive listener
	 * queue and load the receive buffer
	 * <p>
	 * This method always returns immediately.
	 * <p>
	 * @param  connectionData ConnectionData object
	 *
	 */
	public void receive(ConnectionData connectionData) throws IOException {
		// begin receive
		if(_receiveListener == null) {
			_receiveListener = new ReceiverListener(_receiveDataQ, this, _connectionData);
		}

		if(!this.getReceiveDataQ().isEmpty()) {
			waiting = false;
			
			//	adding packets
			this.getReceiveData().addAll(this.getReceiveDataQ().get());
		}
		try {
			Thread.sleep(50);
			_receiveCountKiller++;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// end receive
	}

	/**
	 * Fills the send datas buffer with information
	 * to accomplish a WRITE_STRUCTFLD operation
	 * <p>
	 * This method always returns immediately.
	 * <p>
	 *
	 * @return 	buffer size
	 */
	public Integer socketSend5250QueryData() {
        // Functions Reference (14.27.2)
        getAuxBuffer().clear();

        // NOTE -- numeric values are swapped

        getAuxBuffer().add(TelnetCommandValue.NULLCHAR); // Common 6 byte binary response header
        getAuxBuffer().add(TelnetCommandValue.NULLCHAR); // Record length -- Not including EOR
        getAuxBuffer().add(0x12); // SNA record type = 0x12A0
        getAuxBuffer().add(0xA0);
        getAuxBuffer().add(TelnetCommandValue.NULLCHAR);
        getAuxBuffer().add(TelnetCommandValue.NULLCHAR);

        getAuxBuffer().add(0x04);			// Common 4 byte 5250 datastream header
        getAuxBuffer().add(TelnetCommandValue.NULLCHAR);     // Flags
        getAuxBuffer().add(TelnetCommandValue.NULLCHAR);
        getAuxBuffer().add(TelnetCommandValue.NULLCHAR);     // Opcode (NOP)

        getAuxBuffer().add(TelnetCommandValue.NULLCHAR);     // 00 Cursor Row
        getAuxBuffer().add(TelnetCommandValue.NULLCHAR);     // 01 Cursor Column
        getAuxBuffer().add(0x88);			// 02 Inbound Write Structured Field AID

        getAuxBuffer().add(TelnetCommandValue.NULLCHAR);
        getAuxBuffer().add(0x44);        // 04 Total Length of structured field

        getAuxBuffer().add(0xD9);        // 05 5250 structured field command class
        getAuxBuffer().add(0x70);        // 06 Type code for query function
        getAuxBuffer().add(0x80);        // 07 Fixed code for 5250 query response
        getAuxBuffer().add(0x06);
        getAuxBuffer().add(TelnetCommandValue.NULLCHAR);           // 09 Controller - '0600' is 5250 Emulator
        getAuxBuffer().add(0x01);        //    Code level 010300
        //    m_iTempbuf[i++] = 0x04;        //    Code level 040300 (11/94)
        getAuxBuffer().add(0x03);
        getAuxBuffer().add(TelnetCommandValue.NULLCHAR);           // 12 Controller code level

        // 28 Reserved (16 bytes)
        for(Integer i = 0; i < 16; ++i)
        	getAuxBuffer().add(TelnetCommandValue.NULLCHAR);

        getAuxBuffer().add(0x01);        // 29 Workstation type = display station
        if (!isDsp28x132()) {
            if (isColorTube()) {
            	getAuxBuffer().add(0xF3);
            	getAuxBuffer().add(0xF1);
            	getAuxBuffer().add(0xF7);
                getAuxBuffer().add(0xF9);
                getAuxBuffer().add(0xF0);
                getAuxBuffer().add(0xF0);
                getAuxBuffer().add(0xF2);// 36 IBM3179-2 (No Graphics)
            }
            else {
            	getAuxBuffer().add(0xF5);
            	getAuxBuffer().add(0xF2);
            	getAuxBuffer().add(0xF9);
            	getAuxBuffer().add(0xF1);
            	getAuxBuffer().add(0xF0);
            	getAuxBuffer().add(0xF0);
            	getAuxBuffer().add(0xF1);// 36 IBM5291-1 (No Graphics)
            }
        }
        else
        {
        	getAuxBuffer().add(0xF3);
        	getAuxBuffer().add(0xF4);
        	getAuxBuffer().add(0xF7);
        	getAuxBuffer().add(0xF7);    // 33 EBCDIC device type - 3477
        	getAuxBuffer().add(0xC6);
            if (isColorTube()) {
            	getAuxBuffer().add(0xC3);//    EBCDIC device model - FC Color
            }
            else {
            	getAuxBuffer().add(0xC7);//    EBCDIC device model - FG Mono
            }
            getAuxBuffer().add(0x40);    // 36 EBCDIC space  ('FC ' or 'FG ')
        }
        getAuxBuffer().add(0x02);        // 37 Standard keyboard ID
        getAuxBuffer().add(TelnetCommandValue.NULLCHAR);   // 38 Extended keyboard ID
        getAuxBuffer().add(TelnetCommandValue.NULLCHAR);   // 39 Reserved
        // 43 Serial Number (4 bytes)
        for(Integer i = 0; i < 4; ++i) {
        	getAuxBuffer().add(TelnetCommandValue.NULLCHAR);
        }

        getAuxBuffer().add(DataLowLevelUtils.GetHiByte(getCommScreen().MAX_INP_FIELDS));
        getAuxBuffer().add(DataLowLevelUtils.GetLowByte(getCommScreen().MAX_INP_FIELDS));

        getAuxBuffer().add(TelnetCommandValue.NULLCHAR); // 46 Control Unit Customization
        getAuxBuffer().add(TelnetCommandValue.NULLCHAR); // 48 Reserved
        getAuxBuffer().add(TelnetCommandValue.NULLCHAR);

        getAuxBuffer().add(TelnetCommandValue.NULLCHAR); // 49 Display capabilities - No 1,1
        if (!isDsp28x132()) {
            if (isColorTube()) {
            	getAuxBuffer().add(0x11);       // 50 24x80 & IBM 5292/3179 style color
            }
            else {
            	getAuxBuffer().add(0x10);       // 50 24x80 monochrome
            }
        }
        else {
            if (isColorTube()) {
            	getAuxBuffer().add(0x31);       // 50 27x132 Color
            }
            else {
            	getAuxBuffer().add(0x30);       // 50 27x132 Mono
            }
        }
        getAuxBuffer().add(TelnetCommandValue.NULLCHAR); // 51 No extended attributes
        getAuxBuffer().add(TelnetCommandValue.NULLCHAR); // 52 No double byte
        //    See 14.27.2 5250 Query Command
        getAuxBuffer().add(TelnetCommandValue.NULLCHAR); // 53 No graphics

        for(Integer i = 0; i < 17; ++i)
        	getAuxBuffer().add(TelnetCommandValue.NULLCHAR);

        if (getCommScreen().MAX_INP_FIELDS != 255) {          // Record length
        	getAuxBuffer().set(1, DataLowLevelUtils.GetLowByte(DataLowLevelUtils.GetLowWord(getAuxBuffer().size())));
        }

        return getAuxBuffer().size(); // Return record length
	}

	/**
	 * Fills the send data buffer with information
	 * of the terminal type (RFC1091)
	 * <p>
	 * This method always returns immediately.
	 * <p>
	 *
	 * @param	termType a string with the terminal type
	 *
	 */
    public void socketSendTerminalType(String termType) {
        // RFC1091
        char[] termTypeArray = termType.toCharArray();

        this._auxBuffer.clear();
        this._auxBuffer.add(TelnetCommandValue.TNC_IAC );
        this._auxBuffer.add(TelnetCommandValue.TNC_SB  );
        this._auxBuffer.add(TelnetCommandValue.TNO_TTYP);
        this._auxBuffer.add(TelnetCommandValue.TTYP_IS );

        for (int j = 0; j < termType.length(); j++) {
            this._auxBuffer.add((int)String.valueOf(termTypeArray[j]).charAt(0));
        }

        this._auxBuffer.add(TelnetCommandValue.TNC_IAC );
        this._auxBuffer.add(TelnetCommandValue.TNC_SE  );
    }

	/**
	 * Copy aux send data buffer to main buffer
	 * <p>
	 * This method always returns immediately.
	 * <p>
	 *
	 * @param	length the length of the data to be copied
	 *
	 */
    public void socketFillSendbuf(int length) {
    	Iterator<Integer> tempIterator = this._auxBuffer.iterator();
    	while(tempIterator.hasNext()) {
    		Integer item = tempIterator.next();
    		this._sendData.add(item);
    		this._sendDataPointer = this._sendData.size();
    	}
    }
}