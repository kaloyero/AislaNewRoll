package com.aisla.newrolit.com;

import java.util.List;

/**
 * DataLowLevelUtils is the low level methods toolkit 
 * <p>
 * Main objects are the following
 * <p>
 * @author      Luis Paradela
 * @version     1.0
 * @since       1.0
 */
public class DataLowLevelUtils {
    public static final Integer CKMOD11 = 0xB140;
    public static final Integer CKMOD10 = 0xB1A0;

    /**
     * Returns a word based on two byte numbers
     * <p>
     * This method always returns immediately. 
     *
     * @param  numberA
     * @param  numberB
     * 
     * @return 	word value
     */    
    public static Integer MAKEWORD(Integer numberA, Integer numberB) {
        return (numberA | (numberB << 8));
    }

    /**
     * Returns a long based on two byte numbers
     * <p>
     * This method always returns immediately. 
     *
     * @param  numberA
     * @param  numberB
     * 
     * @return 	word value
     */    
    public static Integer MAKELONG(Integer numberA, Integer numberB) {
        return numberA | (numberB << 16);
    }

    /**
     * Returns the low part of a long
     * <p>
     * This method always returns immediately. 
     *
     * @param  number
     * 
     * @return 	low word value
     */    
    public static Integer GetLowWord(Integer number)
    {
        return (number & 0xFFFF);
    }

    /**
     * Returns the hi part of a long
     * <p>
     * This method always returns immediately. 
     *
     * @param  number
     * 
     * @return 	hi word value
     */    
    public static Integer GetHiWord(Integer number)
    {
        return (number >> 16) & 0xFFFF;
    }
    
    /**
     * Returns the low byte of a number
     * <p>
     * This method always returns immediately. 
     *
     * @param  number
     * 
     * @return 	low byte
     */    
    public static Integer GetLowByte(Integer number)
    {
        return (number & 0xFF);
    }

    /**
     * Returns the hi byte of a number
     * <p>
     * This method always returns immediately. 
     *
     * @param  number
     * 
     * @return 	hi byte
     */    
    public static Integer GetHiByte(Integer number)
    {
    	return (number >> 8) & 0xFF;
    }
    
    /**
     * Returns a long value of the buffer
     * <p>
     * This method always returns immediately. 
     *
     * @param  buffer values buffer
     * @param  baseIndex buffer position
     * 
     * @return 	long value
     */    
    public static Integer GetLong(List<Integer> buffer, Integer baseIndex)
    {

        Integer a;
        Integer b;
        Integer c;
        Integer d;

        a = buffer.get(baseIndex++);
        b = buffer.get(baseIndex++);
        c = buffer.get(baseIndex++);
        d = buffer.get(baseIndex);

        return ((Integer)(a | (b << 8) | (c << 16) | (d << 24)));


    }

    /**
     * Returns a short value of the buffer
     * <p>
     * This method always returns immediately. 
     *
     * @param  buffer
     * @param  baseIndex buffer position
     * 
     * @return 	short value
     */    
    public static Integer GetShort(List<Integer> buffer, Integer baseIndex)
    {
        Integer a;
        Integer b;

        a = buffer.get(baseIndex++);
        b = buffer.get(baseIndex++);

        return ((Integer)(a | (b << 8)));
    }
    
    /**
     * Returns a boolean from a number
     * <p>
     * This method always returns immediately. 
     *
     * @param  value
     * 
     * @return 	true / false
     */    
    public static boolean GetBoolean(Integer value)
    {
        return value == 0;
    }
    
    /**
     * Returns a right padded string with spaces
     * <p>
     * This method always returns immediately. 
     *
     * @param  s string
     * @param  n padding char amount
     * 
     * @return 	right padded string
     */    
    public static String PadRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);  
   }

    /**
     * Returns a left padded string with spaces
     * <p>
     * This method always returns immediately. 
     *
     * @param  s string
     * @param  n padding char amount
     * 
     * @return 	left padded string
     */    
   public static String PadLeft(String s, int n) {
	   try {
       return String.format("%1$#" + n + "s", s);
	   }
	   catch(Exception exc){
		   String pad = "000000000000" + s;
		   pad = pad.substring(12 + s.length() - n, pad.length()) ;
		   return pad;
	   }
   }    
 
   /**
    * Returns a right padded string with 0
    * <p>
    * This method always returns immediately. 
    *
    * @param  s string
    * @param  n padding char amount
    * 
    * @return 	right padded string
    */    
   public static String PadRightZeros(String s, int n) {
       return PadRight(s, n).replace(" ", "0");  
  }

   /**
    * Returns a left padded string with 0
    * <p>
    * This method always returns immediately. 
    *
    * @param  s string
    * @param  n padding char amount
    * 
    * @return 	left padded string
    */    
  public static String PadLeftZeros(String s, int n) {
	  return PadLeft(s, n).replace(" ", "0");  
  }    
}
