package com.example.visualizer;

import java.io.IOException;
import java.nio.ByteBuffer;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;

public class NfcReader extends AsyncTask<Tag, Integer, Person> {
	IsoDep card = null;
	Context context;
	
	public NfcReader(Context context) {
		this.context = context;
	}
	
	@Override
	protected Person doInBackground(Tag... tags) {
		Person person = new Person();
	
		String command;
		Hex response;
		
		try {
			card = IsoDep.get(tags[0]);
			if(card.isConnected()) {
				card.close();
			}
			card.connect();
			
			//Select AID
			response = transceive("00A4040008A00000048703C000");
			
			//Read text data len
			response = transceive("80300100040002000100");

			//Read text block 
			command = "8030010004000300" + response.getHexString().substring(0, 2) + "00";
			response = transceive(command);
			
			//Parse text block
			TLVParser p = new TLVParser(response.getBytes(), 2, 1);
			
			person.secondName = new String(p.values.get("DA01"), "windows-1251");
			person.name = new String(p.values.get("DA02"), "windows-1251");
			person.patronym = new String(p.values.get("DA03"), "windows-1251");
			
			//Read image block len
			response = transceive("80300400040002000200");
			int len = ((response.getBytes()[0] & 0xFF) << 8) + (response.getBytes()[1] & 0xFF);
			
			//Read image block
			ByteBuffer buff = ByteBuffer.allocate(len);
			int offset = 4, bytesToRead;
			while (len > 0) {
				if (len > 0xDC) {
					bytesToRead = 0xDC;
				} else {
					bytesToRead = len;
				}
				command = "8030040004" + String.format("%04X", offset) +
								String.format("%04X", bytesToRead) + "00"; 
				response = transceive(command);
				offset += bytesToRead;
				len -= bytesToRead;
				buff.put(response.getBytes(), 0, bytesToRead);
			}
			
			final BitmapFactory.Options options = new BitmapFactory.Options();
			DisplayMetrics displayMetrics = new DisplayMetrics();
		    //options.inJustDecodeBounds = true;
		    options.inDensity = DisplayMetrics.DENSITY_MEDIUM;
		    options.inTargetDensity = displayMetrics.densityDpi;
		    options.inScaled = true;
			person.bm = BitmapFactory.decodeByteArray(buff.array(), 0, buff.array().length, options);
			
			} catch(IOException e) {
				if (card != null) {
					try {
						card.close();
					} catch (IOException e1) {
						//Nothing to do
					}
				}
			} catch (Exception e) {
				Log.v("Reader", e.getMessage());
			}
		
		return person;
	}
	
	@Override
	protected void onPostExecute(Person person) {
		((NfcReadCompleteListener)context).onNfcTaskComplete(person);
	}
	
	private Hex transceive(String command) throws IOException {
		Hex h = new Hex(command);
		Hex response = new Hex(card.transceive(h.getBytes()));
		if (!response.getHexString().endsWith("9000")) {
			throw new IOException("APDU error.");
		}
		return response;
	}

}
