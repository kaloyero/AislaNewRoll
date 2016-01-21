package com.aisla.newrolit.display;

public class DisplaySettings {
	
	private String red = "";
	private String pink = "";
	private String yellow = "";
	private String green = "";
	private String blue = "";
	private String turquoise = "";
	private String white = "";
	
	private boolean underscore;
	private boolean blinking;
	private int blinkSpeed;
	private boolean reverseImage;
	private boolean enabled;
	
	private int userID;
	
	public DisplaySettings(){
		red = "red";
		pink = "pink";
		yellow = "yellow";
		green = "green";
		blue = "blue";
		turquoise = "turquoise";
		white = "white";
		underscore = true;
		blinking = true;
		reverseImage = true;
		blinkSpeed = 1000;
		userID = 0;
	}
	
	public String getRed() {
		return red;
	}
	public void setRed(String red) {
		this.red = red;
	}
	public String getPink() {
		return pink;
	}
	public void setPink(String pink) {
		this.pink = pink;
	}
	public String getYellow() {
		return yellow;
	}
	public void setYellow(String yellow) {
		this.yellow = yellow;
	}
	public String getGreen() {
		return green;
	}
	public void setGreen(String green) {
		this.green = green;
	}
	public String getBlue() {
		return blue;
	}
	public void setBlue(String blue) {
		this.blue = blue;
	}
	public String getTurquoise() {
		return turquoise;
	}
	public void setTurquoise(String turquoise) {
		this.turquoise = turquoise;
	}
	public String getWhite() {
		return white;
	}
	public void setWhite(String white) {
		this.white = white;
	}
	public boolean isUnderscore() {
		return underscore;
	}
	public void setUnderscore(boolean underscore) {
		this.underscore = underscore;
	}
	public boolean isBlinking() {
		return blinking;
	}
	public void setBlinking(boolean blinking) {
		this.blinking = blinking;
	}
	public boolean isReverseImage() {
		return reverseImage;
	}
	public void setReverseImage(boolean reverseImage) {
		this.reverseImage = reverseImage;
	}
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public int getBlinkSpeed() {
		return blinkSpeed;
	}
	public void setBlinkSpeed(int blinkSpeed) {
		this.blinkSpeed = blinkSpeed;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
}
