package com.aisla.newrolit.customization;

import com.aisla.newrolit.com.GSColor;

/**
 * Special properties a CustomizedField might have
 * (obtained from the GreenScreen).
 */
public enum CustomizedFieldProperty 
{
	ColorBlue,
	ColorGreen,
	ColorPink,
	ColorRed,
	ColorTurquoise,
	ColorWhite,
	ColorYellow,
	ColorRedBlink,
	Inverted,
	None;
	
	public static CustomizedFieldProperty GetPropertyFromGreenScreenColor(GSColor gscolor)
	{
		// (Properties might be in the future other things beside colors, 
		// that is why a different enum was made. Delete this comment
		// when there is no more confussion about this).
		switch(gscolor)
		{
			case atrBlue:
				return ColorBlue;
				
			case atrPink:
				return ColorPink;
				
			case atrGreen: 
				return ColorGreen;
				
			case atrRed:
				return ColorRed;
				
			case atrRedBlink:
				return ColorRedBlink;
				
			case atrTurquoise:
				return ColorTurquoise;
				
			case atrWhite:
				return ColorWhite;
				
			case atrYellow:
				return ColorYellow;
				
			default:
				return None;
		}
	}
	
	public static CustomizedFieldProperty GetPropertyFromString(String strProperty)
	{
		CustomizedFieldProperty result;
		
		if (strProperty.equals("BLUE"))
			result = CustomizedFieldProperty.ColorBlue;
		
		else if (strProperty.equals("GREEN"))
			result = CustomizedFieldProperty.ColorGreen;
		
		else if (strProperty.equals("PINK"))
			result = CustomizedFieldProperty.ColorPink;
		
		else if (strProperty.equals("RED"))
			result = CustomizedFieldProperty.ColorRed;
		
		else if (strProperty.equals("TURQUOISE"))
			result = CustomizedFieldProperty.ColorTurquoise;
		
		else if (strProperty.equals("WHITE"))
			result = CustomizedFieldProperty.ColorWhite;
		
		else if (strProperty.equals("YELLOW"))
			result = CustomizedFieldProperty.ColorYellow;
		
		else if (strProperty.equals("INVERTED"))
			result = CustomizedFieldProperty.Inverted;
		
		else
			result = CustomizedFieldProperty.None;
		
		return result;
	}
}
