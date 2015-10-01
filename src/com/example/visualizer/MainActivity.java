package com.example.visualizer;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements NfcReadCompleteListener {
	NfcAdapter nfcAdapter;
	PendingIntent pendingIntent;
	Boolean processNfc = true;
	
	ImageView imgPhoto;
	TextView tvName, tvSecondName, tvPatronym, tvTapACard;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		
		imgPhoto = (ImageView) this.findViewById(R.id.imgPhoto);
		tvName = (TextView) this.findViewById(R.id.tvName);
		tvSecondName = (TextView) this.findViewById(R.id.tvSecondName);
		tvPatronym = (TextView) this.findViewById(R.id.tvPatronym);
		tvTapACard = (TextView) this.findViewById(R.id.tvTapACard);
		showDetails(false);
		
		initNFC();
		handleNfcIntent(getIntent());
	}
	
	@Override
	public void onPause()
    {
	    super.onPause();
	    nfcAdapter.disableForegroundDispatch(this);
	}
    
    @Override
    public void onResume()
    {
	    super.onResume();
    	nfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[0], new String[0][0]);
	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
    @Override
    public void onNewIntent(Intent intent)
    {
        setIntent(intent);
        handleNfcIntent(intent);
    }
	
    private void initNFC() {
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null)
        {
            Toast.makeText(getApplicationContext(), "NFC not available", Toast.LENGTH_SHORT).show();
        }
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
	}
    
    private void handleNfcIntent(Intent intent)
    {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag == null || !processNfc) {
        	return;
        }
        NfcReader reader = new NfcReader(this);
        reader.execute(tag);
    }
    
	private void showDetails(boolean visible) {
		if (visible) {
			imgPhoto.setVisibility(View.VISIBLE);
			tvName.setVisibility(View.VISIBLE);
			tvSecondName.setVisibility(View.VISIBLE);
			tvPatronym.setVisibility(View.VISIBLE);
			tvTapACard.setVisibility(View.GONE);
			//processNfc = false;
		} else {
			imgPhoto.setVisibility(View.GONE);
			tvName.setVisibility(View.GONE);
			tvSecondName.setVisibility(View.GONE);
			tvPatronym.setVisibility(View.GONE);
			tvTapACard.setVisibility(View.VISIBLE);
			//processNfc = true;
		}
	}

	@Override
	public void onNfcTaskComplete(Person person) {
		tvName.setText(person.name);
		tvSecondName.setText(person.secondName);
		tvPatronym.setText(person.patronym);
		imgPhoto.setImageBitmap(person.bm);
		showDetails(true);
	}
}
