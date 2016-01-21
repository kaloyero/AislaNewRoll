package com.aisla.newrolit.global;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.nio.channels.FileChannel;

public class Paths {
	
	// Get Path Separator
	public static  String GetSeparator() {
		String separator = "/";
		
		if(System.getProperty("os.name").toUpperCase().indexOf("WIND") > -1) {
			separator = "\\";
		}
		
		return separator;
	}
	
	public static String GetAgentsPath(){
		return GetHomePath() + "\\InfiniteCloud\\Agents";
	}
	
	public static String GetHomePath() {
		String separator = System.getProperty("user.home");

//		if(System.getProperty("os.name").toUpperCase().indexOf("WIND") < 0) {
//			separator = "/root";
//		}

		return separator;
	}

	public static String GetLogPath(String logName) {
		String sep = GetSeparator();
		String configFolder = GetHomePath() + sep + "custom" + sep + "logs" + sep;
		return configFolder + logName;
	}
	
//	public static String GetAgentsPath() {
//		String sep = GetSeparator();
//		String configFolder = GetHomePath() + sep + "custom" + sep + "agents" + sep;
////		return configFolder + sep;
//		return configFolder;
//	}
	
	public static String GetToolsPath(){
		String sep = GetSeparator();
		String configFolder = GetHomePath() + sep + "custom" + sep + "tools" + sep;
//		return configFolder + sep;
		return configFolder;
	}
	
	public static String GetConnectionPath() {
		//Returns the path to the Connections folder which contains all the connections as XML files
		String sep = GetSeparator();
		String configFolder = GetHomePath() + sep + "custom" + sep + "connections" + sep;

		return configFolder;
	}
	public static String GetWidgetsPath(){
		//Returns the path of the Widgets folder which contains all of the widget XML files
		String sep = GetSeparator();
		String configFolder = GetHomePath() + sep + "custom" + sep + "widgets" + sep;
		
		return configFolder;
	}

	public static String GetConnectionPath(String connectionName) {
		//Returns the path to the Connections folder which contains all the connections as XML files
		String sep = GetSeparator();
		String configFolder = GetHomePath() + sep + "custom" + sep + "connections";

		return configFolder + sep + AppendXMLIfNeeded(connectionName);
	}

	public static String GetQueriesPath(String queryName) {
		//Returns the path to the Queries folder which contains all the Queries as XML files
		String sep = GetSeparator();
		String configFolder = GetHomePath() + sep + "custom" + sep + "queries";

		return configFolder + sep + AppendXMLIfNeeded(queryName);
	}
	
	public static String GetTransactionPath(String transactionName) {
		//Returns the path to the Transactions folder which contains all the transactions as XML files
		String sep = GetSeparator();
		String configFolder = GetHomePath() + sep + "custom" + sep + "transactions";

		return configFolder  + sep + AppendXMLIfNeeded(transactionName);
	}
	
	public static String GetSettingsPath(String settingName) {
		//Returns the path to the Settings folder which contains all the setting files
		String sep = GetSeparator();
		String configFolder = GetHomePath() + sep + "custom" + sep + "settings";

		return configFolder + sep + settingName;
	}
	
	public static String AppendXMLIfNeeded(String name) {
		if(!name.toUpperCase().trim().endsWith(".XML") && !name.toUpperCase().trim().endsWith(".LLC")) {
			return name + ".xml";
		}
		return name;
	}
	
	public static List<String> ListFilesForFolder(String folder) {
		File folderObj = new File(folder);
		List<String> files = new ArrayList<String>();
	    for (final File fileEntry : folderObj.listFiles()) {
	        if (!fileEntry.isDirectory()) {
	        	files.add(fileEntry.getName());
	        }
	    }
	    
	    return files;
	}
	
	@SuppressWarnings("resource")
	public static void CopyFile(String sourceFile, String destFile) throws IOException {
		File sourceFileObj = new File(sourceFile);
		File destFileObj = new File(destFile);
	    if(!sourceFileObj.exists()) {
	    	destFileObj.createNewFile();
	    }

	    FileChannel source = null;
	    FileChannel destination = null;
	    try {
	        source = new FileInputStream(sourceFile).getChannel();
	        destination = new FileOutputStream(destFile).getChannel();

	        // previous code: destination.transferFrom(source, 0, source.size());
	        // to avoid infinite loops, should be:
	        long count = 0;
	        long size = source.size();              
	        while((count += destination.transferFrom(source, count, size-count))<size);
	        source.close();
	        destination.close();
	    }
	    finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}}
