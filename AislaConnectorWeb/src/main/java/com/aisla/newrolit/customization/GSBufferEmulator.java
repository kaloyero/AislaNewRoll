package com.aisla.newrolit.customization;

import com.aisla.newrolit.com.ScreenElementType;
import com.aisla.newrolit.com.ScreenField;

import com.aisla.newrolit.connections.ConnectionData;
import com.aisla.newrolit.enumerator.AcentosEnum;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GSBufferEmulator {

	private int[] gsBuffer = new int[3696];
	private int screenWidth = 80;
	private int screenHeight = 24;
	private int oldScreenWidth = 80;
	private ConnectionData connectionDataLoc = null;
	List<ScreenField> procScreenFields = new ArrayList<ScreenField>();

	public List<ScreenField> getProcessedScreenFields(Iterator<ScreenField> gsFields, ConnectionData connectionData) {
		Boolean unlocked = false;
		procScreenFields.clear();
		connectionDataLoc = connectionData;

		while (gsFields.hasNext()) {
			ScreenField screenField = gsFields.next();
			// SimpleLogger.log4jClass.debug("Convert Fields pre Buffer
			// emulator: " + screenField.getLogData());

			if (screenField.getType().equals(ScreenElementType.HostField)) {
				addToBuffer(screenField);
				// System.out.println("HostField : " + screenField.getText());
			}
			if (screenField.getType().equals(ScreenElementType.LibraryName)) {
			}
			if (screenField.getType().equals(ScreenElementType.MemberName)) {
			}
			if (screenField.getType().equals(ScreenElementType.FormName)) {
			}
			if (screenField.getType().equals(ScreenElementType.ClearDisplayArea)) {
				// System.out.println("ClearDisplayArea");
				if (screenField.getLinearPosition(screenWidth) == 0
						&& ((screenField.getLength() == screenHeight * screenWidth)
								|| (screenField.getLength() == (screenHeight) * screenWidth))) {
					clearEntireDisplay();
				} else {
					clearDisplayArea(screenField);
				}
			}

			if (screenField.getType().equals(ScreenElementType.RollUp)) {
				// System.out.println("RollUp");
				rollUp(screenField);
			}

			if (screenField.getType().equals(ScreenElementType.lockKeyboard)) {
			}
			if (screenField.getType().equals(ScreenElementType.UnlockKeyboard)) {
				unlocked = true;
				convertGSBufferToHostFields(screenField);
			}
			if (screenField.getType().equals(ScreenElementType.ClearInput)) {
				// System.out.println("ClearInput field: " +
				// screenField.getText());
			}
			if (screenField.getType().equals(ScreenElementType.EnableKeys)) {
			}
			if (screenField.getType().equals(ScreenElementType.NonTransmitKeys)) {
			}
			if (screenField.getType().equals(ScreenElementType.SaveDisplay)) {
			}
			if (screenField.getType().equals(ScreenElementType.BeginRestore)) {
				this.restoreBuffer(screenField);
			}
			if (screenField.getType().equals(ScreenElementType.Set24x80)) {
				this.set24x80(screenField);
			}
			if (screenField.getType().equals(ScreenElementType.Set27x132)) {
				this.set27x132(screenField);
			}
			if (screenField.getType().equals(ScreenElementType.StatusText)) {
				// System.out.println("StatusText");
			}
			if (screenField.getType().equals(ScreenElementType.HelpFormName)) {
			}
			if (screenField.getType().equals(ScreenElementType.EndHelp)) {
			}
			if (screenField.getType().equals(ScreenElementType.ErrorNotification)) {
			}
			if (screenField.getType().equals(ScreenElementType.Terminate)) {
			}
		}

		if (!unlocked) {
			ScreenField screenField = new ScreenField();
			screenField.setType(ScreenElementType.UnlockKeyboard);
			convertGSBufferToHostFields(screenField);
		}

		return procScreenFields;
	}

	private void set27x132(ScreenField screenField) {
		screenHeight = 27;
		screenWidth = 132;
		if (oldScreenWidth != screenWidth && screenWidth != 0) {
			this.clearEntireDisplay();
		}
		oldScreenWidth = screenWidth;
	}

	private void set24x80(ScreenField screenField) {
		screenHeight = 25;
		screenWidth = 80;
		if (oldScreenWidth != screenWidth && screenWidth != 0) {
			this.clearEntireDisplay();
		}
		oldScreenWidth = screenWidth;
	}

	private void addToBuffer(ScreenField screenField) {
		try {
			int i = 0;
			int previousAttribute = 0;
			if (screenField.getLength() == 0) {
				return;
			}

			// Check previous attribute
			int position = screenField.getLinearPosition(screenWidth);
			for (i = position; i >= 0; i--) {
				if (gsBuffer[i] == 0) {
					break;
				}
				if (gsBuffer[i] < 0) {
					previousAttribute = gsBuffer[i];
					break;
				}
			}

			if (screenField.getAttributes() == -2147483648) {
				screenField.setAttributes(0);
			}

			gsBuffer[position - 1] = screenField.getAttributes() * -1;

			String text = screenField.getText();

			byte[] bytes = text.getBytes("UTF-8");
			String Cdata = "Inicio de Sesión";
			String nueva = "áóíúéñÑÁÉÍÓÚ";
			byte sByte[] = Cdata.getBytes();
			byte sByte2[] = nueva.getBytes();

			Cdata = new String(sByte, "UTF-8");
			System.out.println(Cdata);
			for (i = position; i < position + screenField.getLength(); i++) {
				gsBuffer[i] = bytes[i - position];
			}

			if (((previousAttribute * -1) & screenField._atrReverseImage) == screenField._atrReverseImage) {
				previousAttribute = 0x20 * -1;
			}

			if (((position + screenField.getLength() - 1) < (screenWidth * screenHeight - 1))
					&& gsBuffer[position + screenField.getLength()] > 0 && previousAttribute < 0) {
				gsBuffer[position + screenField.getLength()] = previousAttribute;
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		// SimpleLogger.writegsBufferLine(gsBuffer, screenWidth, screenHeight,
		// connectionDataLoc.isWriteLog());
	}

	private void clearEntireDisplay() {
		for (int i = 0; i < gsBuffer.length; i++) {
			gsBuffer[i] = 0;
		}
	}

	private void clearDisplayArea(ScreenField screenField) {
		int position = screenField.getLinearPosition(screenWidth);
		int length = screenField.getLength();
		for (int i = position; i < position + length; i++) {
			if (gsBuffer[i] > 0) {
				gsBuffer[i] = 0;
			}
		}
	}

	private void restoreBuffer(ScreenField screenField) {
		int index = 0;
		int gsIndex = 0;
		int filtElemen = 0;
		String element = "";
		String inputs = "";
		String screen = "";

		String inputData = "";
		int column = 0;
		int row = 0;
		int lenght = 0;
		String attribute = "";

		index = 0;
		gsIndex = 0;
		screen = screenField.getText();

		if (screen.indexOf("{INPUTSDESCRIPTION}") > -1) {
			inputs = screen.substring(screen.indexOf("{INPUTSDESCRIPTION}") + 19, screen.indexOf("{SCREENDESCRIPTION}"))
					.replace("{SCREENDESCRIPTION}", "");
			screen = screen.substring(screen.indexOf("{SCREENDESCRIPTION}") + 19, screen.length());
		}

		int bufferLength = gsBuffer.length;
		while (gsIndex < bufferLength && index < screen.length()) {
			element = screen.substring(index, index + 2);
			if (element.startsWith("[")) {
				index++;
				element = screen.substring(index, index + 2);
				gsBuffer[gsIndex] = -Integer.valueOf(element, 16);
				index++;
			} else {
				filtElemen = Integer.valueOf(element, 16);
				if (filtElemen <= 127) {
					gsBuffer[gsIndex] = Integer.valueOf(element, 16);
				} else {
					gsBuffer[gsIndex] = 0;
				}
			}
			index += 2;
			gsIndex += 1;
		}

		if (screen.indexOf("{INPUTSDESCRIPTION}") > -1) {
			for (index = 0; index < inputs.length(); index += 24) {
				inputData = inputs.substring(index, index + 24);
				row = Integer.valueOf(inputData.substring(0, 4), 16);
				column = Integer.valueOf(inputData.substring(4, 8), 16);
				lenght = Integer.valueOf(inputData.substring(8, 12), 16);
				attribute = inputData.substring(18, 20);
				attribute = attribute + "01" + "24";
				gsIndex = (row - 1) * screenWidth + (column - 2);
				gsBuffer[gsIndex] = -Integer.valueOf(element, 16);

				for (int i = 1; i < lenght; ++i) {
					if (gsBuffer[gsIndex + i] == 0) {
						gsBuffer[gsIndex + i] = 32;
					}
				}
			}
		}
	}

	private void rollUp(ScreenField screenField) {
		int firstRow = screenField.getLinearPosition(screenWidth);
		int lastRow = firstRow + screenField.getLength();
		int rollCount = screenField.getAttributes();
		int rowStartPosition = 0;

		int offset = rollCount * screenWidth;
		int offset2 = 0;
		if (offset > screenWidth * screenHeight) {
			offset = screenWidth * screenHeight;
		}

		for (int i = firstRow; i <= lastRow; ++i) {
			rowStartPosition = (i - 1) * screenWidth;
			for (int j = rowStartPosition; j <= rowStartPosition + screenWidth; j++) {
				offset2 = j - offset;
				if (offset2 < 0) {
					offset2 = 0;
				}
				gsBuffer[offset2] = gsBuffer[j];
			}
		}
	}

	private void convertGSBufferToHostFields(ScreenField screenField) {
		StringBuilder fieldData = new StringBuilder();
		int lastPosition = 0;
		int lastAttribute = 0;

		try {
			for (int i = 0; i < screenHeight * screenWidth; i++) {
				{
					System.out.print(gsBuffer[i] + " ");
				}
				if (esAcento(gsBuffer[i]) && gsBuffer[i] != 0) {
					if (fieldData.length() == 0) {
						lastPosition = i;
					}
					byte[] bytes = new byte[1];
					bytes[0] = (byte) gsBuffer[i];
					if (bytes[0]!=-61){
						if (esCaracterEspecial(bytes[0])){
							fieldData.append(AcentosEnum.getLetraAcento(bytes[0]));
						}else{
							fieldData.append(new String(bytes, "UTF-8"));
						}
						
					}
					 

				}
				String temp1 = fieldData.toString();

				if ((((i + 1) % screenWidth) == 0 || (fieldData.length() + (lastPosition % screenWidth) == screenWidth)
						|| (gsBuffer[i] == 0 && fieldData.length() > 0)) && fieldData.length() > 0) {

					if (fieldData.length() + (lastPosition % screenWidth) > screenWidth) {
						String fieldDataAux = fieldData.toString();
						int excess = fieldData.length() + (lastPosition % screenWidth) - screenWidth;
						fieldDataAux = fieldDataAux.substring(0, fieldDataAux.length() - excess);
						fieldData = new StringBuilder();
						fieldData.append(fieldDataAux);
					}

					if (fieldData.length() + (lastPosition % screenWidth) <= screenWidth) {
						ScreenField field = new ScreenField();
						field.setLinearPosition(lastPosition);
						field.setCol(lastPosition % screenWidth);
						field.setRow(Integer.valueOf(lastPosition / screenWidth));
						field.setAttributes(lastAttribute);
						field.setText(fieldData.toString());
						field.setLength(fieldData.length());
						field.setType(ScreenElementType.HostField);
						procScreenFields.add(field);
						lastPosition = i;
						fieldData = new StringBuilder();
					}
				}
				if (!esAcento(gsBuffer[i]) || gsBuffer[i] == 0) {

					if (fieldData.length() > 0) {
						if (fieldData.length() + (lastPosition % screenWidth + 1) <= screenWidth) {
							ScreenField field = new ScreenField();
							field.setLinearPosition(lastPosition);
							field.setCol(lastPosition % screenWidth);
							field.setRow(Integer.valueOf(lastPosition / screenWidth));
							field.setAttributes(lastAttribute);
							field.setText(fieldData.toString());
							field.setLength(fieldData.length());
							field.setType(ScreenElementType.HostField);
							procScreenFields.add(field);
						}
					}

					lastAttribute = gsBuffer[i] * -1;
					lastPosition = i + 1;
					fieldData = new StringBuilder();
				}
			}
			procScreenFields.add(screenField);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public boolean esAcento(int valor) {
		if (valor < 0) {
			if ((valor == -61) || (valor == -95) || (valor == -77) || (valor == -83) || (valor == -70) || (valor == -87)
					|| (valor == -79) || (valor == -111) || (valor == -127) || (valor == -119) || (valor == -115)
					|| (valor == -109) || (valor == -102)) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}
	public boolean esCaracterEspecial(int valor) {

			if ((valor == -61) || (valor == -95) || (valor == -77) || (valor == -83) || (valor == -70) || (valor == -87)
					|| (valor == -79) || (valor == -111) || (valor == -127) || (valor == -119) || (valor == -115)
					|| (valor == -109) || (valor == -102)) {
				return true;
			} else {
				return false;
			}
		
	}
}
