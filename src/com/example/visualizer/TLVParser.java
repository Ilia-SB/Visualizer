package com.example.visualizer;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

public class TLVParser {
	public HashMap<String, byte[]> values;
	private byte[] data;
	private int numTagBytes;
	private int numLenBytes;
	
	public TLVParser(String data, int numTagBytes, int numLenBytes) throws Exception {
		Hex h = new Hex(data);
		this.data = h.getBytes();
		this.numTagBytes = numTagBytes;
		this.numLenBytes = numLenBytes;
		values = new HashMap<String, byte[]>();
		parse();
	}
	
	public TLVParser(byte[] data, int numTagBytes, int numLenBytes) throws Exception {
		this.data = data;
		this.numTagBytes = numTagBytes;
		this.numLenBytes = numLenBytes;
		values = new HashMap<String, byte[]>();
		parse();
	}
	
	private void parse() throws Exception {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
		while(inputStream.available() > numTagBytes + numLenBytes) {
			int tag = 0;
			for(int i=0; i<numTagBytes; i++) {
				tag = tag<<8;
				tag = tag + inputStream.read();
			}
			int length = 0;
			for(int i=0; i<numLenBytes; i++) {
				length = length<<8;
				length = length + inputStream.read(); 
			}
			byte[] value = new byte[length];
			if (inputStream.read(value, 0, length) != -1) {
				if(tag != 0 && length != 0) {
					//Log.v("Parser", String.format("%02X", tag) + " : "+ String.valueOf(length) + " : " + (new Hex(value)).getHexString());
					values.put(Integer.toHexString(tag).toUpperCase(), value);
				}
			} else {
				throw new Exception("Error parcing TLV.");
			}
		}
	}

}
