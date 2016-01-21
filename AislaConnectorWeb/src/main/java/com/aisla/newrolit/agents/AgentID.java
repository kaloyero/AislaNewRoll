package com.aisla.newrolit.agents;


import com.aisla.newrolit.global.CellPair;
import com.aisla.newrolit.global.Pair;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class AgentID {
	static ArrayList<CellPair> _coordinates = new ArrayList<CellPair>();
	static boolean loaded = false;
	
	
	
	public int getPageID(ArrayList<JSONObject> fields){
		
		AgentHandler ah = new AgentHandler();
		AgentController ac = new AgentController();
		
		if(!loaded){ // Load the cells from the agent into _coordinates
			JSONArray cells = (JSONArray) ac.getIDAgent().get("CELLS");
			_coordinates.clear();
			
			for(int i = 0; i < cells.size(); i++){
				JSONObject temp = (JSONObject) cells.get(i);
				
				CellPair cp = new CellPair();
				
				cp.setRow(Integer.parseInt(temp.get("r").toString()));
				cp.setColumn(Integer.parseInt(temp.get("c").toString()));
				
				_coordinates.add(cp);
				System.out.println("adding cell pair coordinates: " + cp.getRow() + " " + cp.getColumn());
			}
			
//			loaded = true;
		}
		
		char[][] page = new char[38][140];
		
		int _atrInputField = 0x100;
		
		
		String test = "";
		
		int hexInt = 0x0;
		for (int i = 0; i < fields.size(); i++){
			JSONObject temp = fields.get(i);
			
			int iLength = Integer.parseInt(temp.get("length").toString());
			for(int j = 0; j < iLength; j++){
				
				hexInt = Integer.parseInt(temp.get("attributes").toString() );
				if((hexInt & _atrInputField) == _atrInputField){
					
					
					page[ Integer.parseInt(temp.get("iY").toString())][  Integer.parseInt(temp.get("iX").toString()) + j] = '_';
//					page[temp.iY][temp.iX + j].fieldId = i;
					
				}else{
					page[ Integer.parseInt(temp.get("iY").toString())][ Integer.parseInt(temp.get("iX").toString()) + j] = temp.get("text").toString().charAt(j);
//					page[temp.iY][temp.iX + j].fieldId = i;
					
					test += temp.get("text").toString().charAt(j);
				}
				
			}
		}
		
		
		int returnValue = 0;
		
		for(int i = 0; i < _coordinates.size(); i++){
			CellPair temp = _coordinates.get(i);
			int value = (int)page[temp.getRow()][temp.getColumn()];
			returnValue += value;
			System.out.println("Character Value: " + value);
		}
		
		
		
		System.out.println("Page ID: " + returnValue);
		
		return returnValue;
		
	}
}
