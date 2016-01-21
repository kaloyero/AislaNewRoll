package com.aisla.newrolit.customization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aisla.newrolit.com.ScreenField;

public class CustomizationUtils 
{
	public static String GetStringFromPattern(String text, String regexp)
	{
		Pattern regExp = Pattern.compile(regexp);
		Matcher matcher = regExp.matcher(text);
		if(!matcher.find())
			return "";
		
		return text.substring(matcher.start(), matcher.end());
	}
	
	public static int GetMinColFormCollection(List<CustomizedField> fields, ControlType controlType) {
		
		int minCol = 200;
		int fieldCol = 0;
		for(CustomizedField field:fields) {
			if(field.getControlType().equals(controlType)) {
				fieldCol = field.getGreenScreenField().getCol();
				if(fieldCol < minCol) {
					minCol = fieldCol;
				}
			}
		}
		return minCol;
	}

	public static int GetMaxColFormCollection(List<CustomizedField> fields, ControlType controlType) {
		
		int maxCol = 0;
		int fieldCol = 0;
		int fieldLength = 0;
		for(CustomizedField field:fields) {
			if(field.getControlType().equals(controlType)) {
				fieldCol = field.getGreenScreenField().getCol();
				fieldLength = field.getGreenScreenField().getLength();
				if((fieldCol + fieldLength) > maxCol) {
					maxCol = fieldCol + fieldLength;
				}
			}
		}
		return maxCol;
	}
	
	
	public static void SortCustomizedFields(List<CustomizedField> fields)
	{
		Collections.sort
		(
				fields,
				new Comparator<CustomizedField>() 
				{
					@Override
					public int compare(CustomizedField o1, CustomizedField o2)
					{
						int row1 = o1.getGreenScreenField().getRow();
						int row2 = o2.getGreenScreenField().getRow();
						int rowResult = row1 - row2;
						
						if (rowResult != 0)
							return rowResult;
						else
						{
							int col1 = o1.getGreenScreenField().getCol();
							int col2 = o2.getGreenScreenField().getCol();
							
							return col1 - col2;
						}
					}
				}
		);	
	}
	
	
	/**
	 * Converts fields into a list of strings,
	 * where each string represents a line on the green screen.
	 * The method only includes CustomizedField's marked as Label.
	 * 
	 * @param fields
	 * 		List of CustomizedField's obtained from the green screen
	 * 
	 * @return
	 * 		List of strings, where each string is a line on the green screen. 
	 */
	
	public static List<String> ConvertCustomizedFieldsToScreenLines(List<CustomizedField> fields)
	{
		// Sort fields by row, then column
		SortCustomizedFields(fields);
		
		List<String> result = new ArrayList<String>();
		StringBuilder currentLine = new StringBuilder();
		
		int processingRow = 0;
		int currRow = 0;
		int currCol = 0;
		
		for (CustomizedField field : fields)
		{
			if (field.getControlType() != ControlType.Label)
				continue;
			
			ScreenField screenField = field.getGreenScreenField();
			currRow = screenField.getRow();
			currCol = screenField.getCol();
			
			// -----------------
			// Check for new row
			// -----------------
			
			if (currRow != processingRow)
			{
				// Add computed row
				result.add(currentLine.toString());
				
				// Add blank rows between this row and the previous one
				for (int row = processingRow + 1; row < currRow; row++)
					result.add(" ");

				currentLine = new StringBuilder();
				processingRow = currRow;
			}
			
			// -------------------
			// Add field to string
			// -------------------
			
			// First pad with spaces according to the field's column
			int numberOfSpaces = currCol - currentLine.length();
			
			for (int i = 0; i < numberOfSpaces; i++)
				currentLine.append(" ");
			
			// Add field text to line.
			currentLine.append(screenField.getText());
			//lineColPosition += screenField.getText().length() + numberOfSpaces;
		}
		
		// Add final line.
		result.add(currentLine.toString());
		
		return result;
	}
}
