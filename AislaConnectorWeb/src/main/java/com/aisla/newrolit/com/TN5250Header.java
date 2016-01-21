package com.aisla.newrolit.com;

/**
 * TN5250Header holds the TN5250 header state values 
 * <p>
 * Main objects are the following
 * <p>
 * @author      Luis Paradela
 * @version     1.0
 * @since       1.0
 */
public class TN5250Header {
    private Integer fixedlen; // 0x04 (always)
    private Integer outdserr; // Bits
    private Integer attnkey;
    private Integer sysrqskey;
    private Integer testkey;
    private Integer helpinerr;
    private Integer reserved8; // 0
    private Integer opcode; // 0x00..0x0C
    
    /**
     * Returns the header fixed lenght
     * <p>
     * This method always returns immediately. 
     *
     * @return 	fixed length
     */    
	public Integer getFixedlen() {
		return fixedlen;
	}
	
    /**
     * Returns the out data standard error 
     * <p>
     * This method always returns immediately. 
     *
     * @return 	0 / 1
     */    
	public Integer getOutdserr() {
		return outdserr;
	}
	
    /**
     * Returns the attention key
     * <p>
     * This method always returns immediately. 
     *
     * @return 	0 / 1
     */    
	public Integer getAttnkey() {
		return attnkey;
	}
	
    /**
     * Returns the system request key 
     * <p>
     * This method always returns immediately. 
     *
     * @return 	0 / 1
     */    
	public Integer getSysrqskey() {
		return sysrqskey;
	}
	
    /**
     * Returns the test 
     * <p>
     * This method always returns immediately. 
     *
     * @return 	0 / 1
     */    
	public Integer getTestkey() {
		return testkey;
	}
	
    /**
     * Returns the help 
     * <p>
     * This method always returns immediately. 
     *
     * @return 	0 / 1
     */    
	public Integer getHelpinerr() {
		return helpinerr;
	}
	
    /**
     * Reserved bit value
     * <p>
     * This method always returns immediately. 
     *
     * @return 1 / 0
     */    
	public Integer getReserved8() {
		return reserved8;
	}
	
    /**
     * Current opcode
     * <p>
     * This method always returns immediately. 
     *
     * @return 	opcode
     */    
	public Integer getOpcode() {
		return opcode;
	}
	
    /**
     * Set fixed length
     * <p>
     * This method always returns immediately. 
     *
     * @param fixedlen fixed length
     */    
	public void setFixedlen(Integer fixedlen) {
		this.fixedlen = fixedlen;
	}
	
    /**
     * Set output standard error key bit value
     * <p>
     * This method always returns immediately. 
     *
     * @param outdserr 1 / 0
     */    
	public void setOutdserr(Integer outdserr) {
		this.outdserr = outdserr;
	}
	
    /**
     * Set Attention key bit value
     * <p>
     * This method always returns immediately. 
     *
     * @param attnkey 1 / 0
     */    
	public void setAttnkey(Integer attnkey) {
		this.attnkey = attnkey;
	}
	
    /**
     * Set System Request key bit value
     * <p>
     * This method always returns immediately. 
     *
     * @param sysrqskey 1 / 0
     */    
	public void setSysrqskey(Integer sysrqskey) {
		this.sysrqskey = sysrqskey;
	}
	
    /**
     * Set test key bit value
     * <p>
     * This method always returns immediately. 
     *
     * @param testkey 1 / 0
     */    
	public void setTestkey(Integer testkey) {
		this.testkey = testkey;
	}
	
    /**
     * Set the help bit value 
     * <p>
     * This method always returns immediately. 
     *
     * @param	helpinerr 0 / 1
     */    
	public void setHelpinerr(Integer helpinerr) {
		this.helpinerr = helpinerr;
	}
	
    /**
     * Set Reserved bit value
     * <p>
     * This method always returns immediately. 
     *
     * @param reserved8 1 / 0
     */    
	public void setReserved8(Integer reserved8) {
		this.reserved8 = reserved8;
	}
	
    /**
     * Set current opcode
     * <p>
     * This method always returns immediately. 
     *
     * @param 	opcode opcode value
     */    
	public void setOpcode(Integer opcode) {
		this.opcode = opcode;
	}
}
