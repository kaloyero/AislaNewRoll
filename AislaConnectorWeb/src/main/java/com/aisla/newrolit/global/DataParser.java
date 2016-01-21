package com.aisla.newrolit.global;

public class DataParser {
	public DataParser(){
		
	}
	public String parse(String temp){
		temp.replace("\"", "&#34");
		temp.replace("\\", "&#92");
		temp.replace("/", "&#47");
		temp.replace("'", "&#39");
		return temp;
	}
	public String parse(int t){
		String temp = Integer.toString(t);
		temp.replace("\"", "&#34");
		temp.replace("\\", "&#92");
		temp.replace("/", "&#47");
		temp.replace("'", "&#39");
		return temp;
	}
	
}
