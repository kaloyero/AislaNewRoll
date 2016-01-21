package com.aisla.newrolit.customization;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.aisla.newrolit.com.GSColor;
import com.aisla.newrolit.com.ScreenField;
import com.aisla.newrolit.com.ScreenFieldType;


/**
 * CustomizedField class is a screen field   
 * with additional attributes that are 
 * needed for rendering the field into HTML
 * <p>
 * Main objects are the following
 * <p>
 * @author      -
 * @version     1.0
 * @since       1.0
 */
public class CustomizedField {


	private ScreenField greenScreenField = null;
	private String fieldId = "";
    private ControlType controlType = ControlType.Label;
//    private IntegrationSourceTypes integrarionType = IntegrationSourceTypes.None;
//    private IntegrationSource integrationSource = null;
    private String text = "";
	private String className = "";
	private int x = 0;
    private int y = 0;
    private boolean hideField = false;
    private String agentWatermark = "";
    private String [] data;
    private String data2;
    private String data3;
    private List<CustomizedField> childs;

    public List<CustomizedField> getChilds() {
		if(childs == null) {
			childs = new ArrayList<CustomizedField>();
		}
		return childs;
	}

	public void setChilds(List<CustomizedField> childs) {
		this.childs = childs;
	}

	public String getData2() {
		return data2;
	}

	public void setData2(String data2) {
		this.data2 = data2;
	}

	public String getData3() {
		return data3;
	}

	public void setData3(String data3) {
		this.data3 = data3;
	}

	public String [] getData() {
		return data;
	}

	public void setData(String [] data) {
		this.data = data;
	}

	public String getFieldId() {
		return fieldId;
	}

	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}

	public ScreenField getGreenScreenField() {
		return greenScreenField;
	}

	public void setGreenScreenField(ScreenField greenScreenField) {
		this.greenScreenField = greenScreenField;
	}

	public ControlType getControlType() {
		return controlType;
	}

	public void setControlType(ControlType controlType) {
		this.controlType = controlType;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean getHideField() {
		return hideField;
	}

	public void setHideField(boolean hideField) {
		this.hideField = hideField;
	}

	public String getAgentWatermark() {
		return agentWatermark;
	}

	public void setAgentWatermark(String agentWatermark) {
		this.agentWatermark = agentWatermark;
	}
//	public IntegrationSourceTypes getIntegrarionType() {
//		return integrarionType;
//	}
//
//	public void setIntegrarionType(IntegrationSourceTypes integrarionType) {
//		this.integrarionType = integrarionType;
//	}
//
//	public IntegrationSource getIntegrationSource() {
//		return integrationSource;
//	}
//
//	public void setIntegrationSource(IntegrationSource integrationSource) {
//		this.integrationSource = integrationSource;
//	}
	


	public CustomizedField() {
		this.greenScreenField = new ScreenField();
		this.childs = new ArrayList<CustomizedField>();
	}
	
	/**
	 * A ScreenField object may be passed to the constructor (to copy its attributes).  
	 * <p>
	 * All ScreenFields are parsed as CustomizedFields before rendering. The original
	 * ScreenField object is kept in the greenScreenField attribute of this class.
	 *
	 * @param  ScreenField to be parsed as a CustomizedField
	 * 
	 * @see ScreenField
	 */    
	public CustomizedField(ScreenField screenField)
	{
		//load properties from screen field

		this.greenScreenField = screenField;
		this.text = screenField.getText();
		this.x = CustomizationManager.Instance().convertXCoord(screenField.getCol());
		this.y = CustomizationManager.Instance().convertYCoord(screenField.getRow());
		this.fieldId = getFieldId(String.valueOf(x), String.valueOf(y));

		if(screenField.getFieldType() == ScreenFieldType.Label || screenField.getFieldType() == ScreenFieldType.ProtectedInput) 
		{
			this.controlType = ControlType.Label;
		}
		if(screenField.getFieldType() == ScreenFieldType.Input)
		{
			this.controlType = ControlType.InputText;
		}

	}
	
	/**
	 * Method used to equalize a CustomizedField's attributes with another's.   
	 * <p>
	 * All ScreenFields are parsed as CustomizedFields before rendering. The original
	 * ScreenField object is kept in the greenScreenField attribute of this class.
	 * 
	 * @return 	cloned CustomizedField
	 */    
	public CustomizedField clone()
	{
		CustomizedField newField = new CustomizedField();
		
//		newField.setIntegrationSource(integrationSource);
		newField.setChilds(childs);
		newField.setData(data);
		newField.setData2(data2);
		newField.setData3(data3);
		newField.setAgentWatermark(agentWatermark);
		newField.setClassName(className);
		newField.setControlType(controlType);
		newField.setFieldId(fieldId);
		newField.setGreenScreenField(greenScreenField.clone());
		newField.setHideField(hideField);
		newField.setText(text);
		newField.setX(x);
		newField.setY(y);
		
		return newField;
	}

	public static String getFieldId(String x, String y)
	{
    	if(y.length() == 1) {
    		y = "0" + y;
    	}

    	if(x.length() == 1) {
    		x = "00" + x;
    	}

    	if(x.length() == 2) {
    		x = "0" + x;
    	}
    	
    	return y + x;

	}

	public void printDebugInfo() {
		StringBuilder data = new StringBuilder();
		ScreenField screenField = getGreenScreenField();
		data.append("Col:");
		data.append(screenField.getCol1Based());

		data.append(" Row:");
		data.append(screenField.getRow1Based());

		data.append(" Text:");
		data.append(screenField.getText());

		//System.out.println(data);
	}

	/**
	 * Provides a string with the attributes of the object.
	 * <p>
	 * the following attributes are listed (separated by spaces):
	 * <ul>
	 * <li>Col: value</li>
	 * <li>Row: value</li>
	 * <li>Agent Watermark: value</li>
	 * <li>Control Type: value</li>
	 * <li>Text: value</li>
	 * <li>Custom Text: value</li>
	 * <li>Visible: value</li>
	 * <li>Childs count: value</li>
	 * <li>Source Name: value</li>
	 * </ul>
	 * <p>

	 * 
	 * @return 	A string with the name and current value of the key attributes of this class.
	 */    
	public String printDebugInfoCustomized() {
		StringBuilder data = new StringBuilder();
		ScreenField screenField = getGreenScreenField();
		data.append("Col:");
		data.append(screenField.getCol1Based());

		data.append(" Row:");
		data.append(screenField.getRow1Based());

		data.append(" Agent Watermark:");
		data.append(this.agentWatermark);

		data.append(" Control Type:");
		data.append(this.getControlType());

		data.append(" Text:");
		data.append(screenField.getText());
		
		data.append(" Custom Text:");
		data.append(this.getText());

		data.append(" Visible:");
		data.append(screenField.getVisible());

		data.append(" Childs count:");
		data.append(this.getChilds().size());

//		data.append(" Source Name:");
//		if(this.getIntegrationSource() != null)
//			data.append(this.getIntegrationSource().getName());

		return data.toString();
	}
	
	
	/**
	 * Gets a list of all the field properties, obtained from the green
	 * screen field.
	 * 
	 * @return
	 * 		List of CustomizedFieldProperty, where each property is obtained
	 * 		from the greenScreenField
	 */
	public Set<CustomizedFieldProperty> getFieldProperties()
	{
		Set<CustomizedFieldProperty> result = new HashSet<CustomizedFieldProperty>(20);
		
		// Check for colors
		GSColor gsColor = this.getGreenScreenField().getColor();
		CustomizedFieldProperty property = CustomizedFieldProperty.GetPropertyFromGreenScreenColor(gsColor);
		result.add(property);
		
		// Check if field has inverted colors
		if (this.greenScreenField.isInverted())
			result.add(CustomizedFieldProperty.Inverted);
		
		// *** Other properties may be added later
		return result;
	}
	
	
	/**
	 * Converts a list of CustomizedField's to a String, by joining all field's texts.
	 * The list must be sorted by column, and all fields should have the same row.
	 *   
	 * @param fields
	 * 		List of CustomizedField's. All CustomizedField's must have the same row,
	 * 		and the list must be sorted by column.
	 * 
	 * @return
	 * 		String containing all the list's texts
	 */
	public static String ConvertFieldsToLine(List<CustomizedField> fields)
	{
		StringBuilder result = new StringBuilder();
		
		int previousCol = -1;
		int currCol;
		
		for (CustomizedField field : fields)
		{
			currCol = field.getGreenScreenField().getCol();
			
			// Add blank spaces if necessary
			if (currCol > previousCol + 1)
			{
				for (int i = previousCol + 1; i < currCol; i++)
					result.append(" ");
			}
			
			result.append(field.getGreenScreenField().getText());
			previousCol = currCol;
		}
		
		return result.toString();
	}
}
