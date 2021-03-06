package com.example.visualizer;

public class Hex {
	private byte[] data;
	
	public Hex(String s) {
		data = hexStringToByteArray(s);
	}
	
	public Hex(byte[] b) {
		data = b;
	}
	
	public String getHexString() {
		return byteArrayToHexString(data);
	}
	
	public byte[] getBytes() {
		return data;
	}
	
	private static String byteArrayToHexString(byte[] array) {
	    StringBuffer hexString = new StringBuffer();
	    for (byte b : array) {
	      int intVal = b & 0xff;
	      if (intVal < 0x10)
	        hexString.append("0");
	      hexString.append(Integer.toHexString(intVal));
	    }
	    return hexString.toString();    
	}
	
	private static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
}
