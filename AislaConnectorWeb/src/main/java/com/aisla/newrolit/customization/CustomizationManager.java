package com.aisla.newrolit.customization;

import com.aisla.newrolit.global.HostType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import com.aisla.newrolit.com.CommBufferLogic;
import com.aisla.newrolit.com.ScreenField;

import com.aisla.newrolit.connections.ConnectionData;


/*
 * This class receives a collection of fields representing a screen and runs the agents
 * (or applies the customization) that corresponds to the configuration applied.  
 * The class is a singleton pattern and in the first call it loads its configuration
*/ 
 
 
public class CustomizationManager 
{
	public static Stack< List<CustomizedField> > popups = new Stack<List<CustomizedField>>();
	private String i36SessionID = "";
	
	public String getI36SessionID() {
		return i36SessionID;
	}

	public void setI36SessionID(String i36SessionID) {
		this.i36SessionID = i36SessionID;
	}


	// Singleton instance
	public static CustomizationManager instance = null;
	public static CustomizationManager Instance()
	{
		if(instance == null)
			instance = new CustomizationManager();
		
		return instance;
	}
	
	
	// Collection of agents to run when processing screen
//	private List<IAgent> agents = new ArrayList<IAgent>();
	
	// Collection of agents to run when processing a popup
//	private List<IAgent> popupAgents = new ArrayList<IAgent>();
	
	// Scaling used to convert rows and columns to pixel coords
	private int currentScreenScaleX = 9;
	private int currentScreenScaleY = 20;

	// Margin used to render the screen
	private int currentScreenMarginX = 20;
	private int currentScreenMarginY = 80;
	
	
	public int getCurrentScreenScaleX() {
		return currentScreenScaleX;
	}

	public void setCurrentScreenScaleX(int currentScreenScaleX) {
		this.currentScreenScaleX = currentScreenScaleX;
	}

	public int getCurrentScreenScaleY() {
		return currentScreenScaleY;
	}

	public void setCurrentScreenScaleY(int currentScreenScaleY) {
		this.currentScreenScaleY = currentScreenScaleY;
	}

	public int getCurrentScreenMarginX() {
		return currentScreenMarginX;
	}

	public void setCurrentScreenMarginX(int currentScreenMarginX) {
		this.currentScreenMarginX = currentScreenMarginX;
	}

	public int getCurrentScreenMarginY() {
		return currentScreenMarginY;
	}

	public void setCurrentScreenMarginY(int currentScreenMarginY) {
		this.currentScreenMarginY = currentScreenMarginY;
	}
	
	public CustomizationManager() {
//		SimpleLogger.log4jClass.debug("BEFOR INIT AGENTS");
//		initAgents();
//		SimpleLogger.log4jClass.debug("AFTER INIT AGENTS");
	}
	
	/**
	 * Add instances of IAgents to process CutomizedFields.
	 * All agents to be run on normal screen must be added to the agents collection.
	 * For an agent to process the content of a popup, it must be added to
	 * the popupAgents collection.
	 */
//	public void initAgents() 
//	{
//		//initialize configurations
//		try	
//		{
//			String configFolder = Paths.GetAgentsPath();
//			agents.clear();
//			IAgent reportAgent = new ReportAgent(configFolder);
//			agents.add(reportAgent);
//
//			IAgent hideAgent = new HideAgent(configFolder);
//			agents.add(hideAgent);
//			popupAgents.add(hideAgent);
//
//			IAgent titleAgent = new TitleAgent(configFolder);
//			agents.add(titleAgent);
//			
//			IAgent dateTimeAgent = new DateTimeAgent(configFolder);
//			agents.add(dateTimeAgent);
//
//			IAgent menuAgent = new MenuAgent(configFolder);
//			agents.add(menuAgent);
//
//			//IAgent menuAgentCIS = new MenuAgentCIS(configFolder);
//			//agents.add(menuAgentCIS);
//			
//			IAgent fkAgent = new FunctionKeysAgent(configFolder);
//			agents.add(fkAgent);
//			popupAgents.add(fkAgent);
//
////			IAgent sfAgentLPS = new SubFileAgentLPS(configFolder);
////			agents.add(sfAgentLPS);
//
//			IAgent sfAgent = new SubFileAgent(configFolder);
//			agents.add(sfAgent);
//			
//			IAgent comboBoxAgent = new ComboBoxAgent(configFolder);
//			agents.add(comboBoxAgent);
//			popupAgents.add(comboBoxAgent);
//
//			IAgent integrationAgent = new IntegrationAgent(configFolder);
//			agents.add(integrationAgent);
//			
//			IAgent messageRendererOutlook = new MessageAgent(configFolder);
//			agents.add(messageRendererOutlook);
//			
//			IAgent buttonAgent = new ButtonAgent(configFolder);
//			agents.add(buttonAgent);
//		}
//		catch(Exception e) 
//		{
//			e.printStackTrace();
//		}
//	}
	
	public List<CustomizedField> ProcessScreen(List<CustomizedField> customizedFields, ConnectionData connectionData)
	{
		return ProcessScreen(customizedFields, connectionData, false);
	}
	
	public List<CustomizedField> ProcessScreen(List<CustomizedField> customizedFields, ConnectionData connectionData, boolean isPopup) 
	{
//		if(connectionData.isWriteLog()) 
//		{
//			for(CustomizedField field : customizedFields)
//			{
//				SimpleLogger.log4jClass.debug("pre-agents fields: " + field.printDebugInfoCustomized());
//			}
//			SimpleLogger.log4jClass.debug("---------------------------------------");
//		}

		// Process each agent against the fields
//		List<IAgent> agentsToRun;
		
//		ToolsConfigurationSerializer toggleRenderer = new ToolsConfigurationSerializer();
//		int flag = 0;
//		try {
//			toggleRenderer.LoadParameters(Paths.GetToolsPath() + "GreenScreenButton.xml");
//			flag = Integer.parseInt(toggleRenderer.getValue("Enabled").getValue().toString());	
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
//		if (isPopup)
//			agentsToRun = popupAgents;
//		else
//			agentsToRun = agents;
//		if(flag == 1){
//		
//			for(IAgent agent : agentsToRun) // Here it Runs the agents! this is where all of the agents run() function is invoked
//			{
//				try 
//				{
//					SimpleLogger.log4jClass.debug("Next agent: " + agent.getClass().getName());
//					
//					agent.run(customizedFields);
//				}
//				catch(Exception exc){
//					SimpleLogger.log4jClass.error("Error in Agent: " + agent.getClass().getName());
//					SimpleLogger.log4jClass.error("Exception Message:" + exc.getMessage());
//					SimpleLogger.log4jClass.error("Exception Trace:" + exc.getStackTrace().toString());				
//				}
//			}
//		}else{
//			SimpleLogger.log4jClass.debug("Agents prevented from rendering by the GreenScreenButton.xml file.");
//		}

		if(connectionData.isWriteLog()) {
			for(CustomizedField field : customizedFields) {
//				SimpleLogger.log4jClass.debug("post-agents fields: " + field.printDebugInfoCustomized());
			}
//			SimpleLogger.log4jClass.debug("---------------------------------------");
		}

		return customizedFields;
	}
	
	
	public List<CustomizedField> ConvertFields(Iterator<ScreenField> gsFields, CommBufferLogic commBufferData)	{
		// ----------------------------------------------------------------
		// Converts green screen fields to a collection of CustomizedFields
		// ----------------------------------------------------------------
		
		List<CustomizedField> customFields = new ArrayList<CustomizedField>();
		List<String> customFieldsIndex = new ArrayList<String>();
		ConnectionData connectionData = commBufferData.getConnectionData();
		GSBufferEmulator gsBufferEmulator = commBufferData.getGsBufferEmulator();
		
		List<ScreenField> screenFields = gsBufferEmulator.getProcessedScreenFields(gsFields, connectionData);

		for(ScreenField screenField:screenFields) 
		{
			CustomizedField customField = new CustomizedField(screenField);
//			SimpleLogger.log4jClass.debug("ConvertFields post Buffer emulator: " + screenField.getLogData());
			
			// Get I36 Session ID : Stored in a specific place, with a specific length
			if(commBufferData.getConnectionData().getHostType().equals(HostType.i36)) {
				if(screenField.getRow() == 0 && screenField.getCol() == 77 && screenField.getLength() == 2) {
					this.i36SessionID = screenField.getText();
				}
			}
			
//			if(!(customField.getText().trim().equals("") && 
//				screenField.getFieldType().equals(ScreenFieldType.Label)) ||
//				((customField.getGreenScreenField().getAttributes() & screenField._atrReverseImage) == screenField._atrReverseImage)
//				) 
				StringBuilder item = new StringBuilder();
				item.append(screenField.getCol());
				item.append("_");
				item.append(screenField.getRow());
				item.append("_");
				item.append(screenField.getText());
				
				if(!customFieldsIndex.contains(item.toString())) 
				{
					customFields.add(customField);
					customFieldsIndex.add(item.toString());
				}
			//}
		}
		
		return customFields;
	}
	
	public int convertXCoord(int column) {
		return column * currentScreenScaleX + currentScreenMarginX;
	}
	
	public int convertYCoord(int row) {
		return row * currentScreenScaleY + currentScreenMarginY;
	}
}
