package de.raspberrypiapp.technikerschuleerlangen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

// Home Screen Activity des Apps
public class RaspberryPiAppHomeActivity extends Activity {

	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_raspberry_pi_app_home);
	}
	 
	// Button Listner Methode
	public void onButtonClick(View view){  
		if (view.getId() == R.id.btn_zimmerauswertung){ 					// Wenn der Button fuer Zimmerauswertung gedrueckt wird
			startActivity(new Intent(this,ZimmerAuswertungActivity.class)); //so wird die passende Activity gestartet
		}
		if (view.getId() == R.id.btn_steckdosen_ansteuern){
			startActivity(new Intent(this,SteckdosenAnsteuernActivity.class));
		}
		if (view.getId() == R.id.btn_wetter_com){
			startActivity(new Intent(this,WetterComActivity06Uhr.class));
		}
		if (view.getId() == R.id.btn_ueberwachungskamera){
			startActivity(new Intent(this,UeberwachungskameraActivity.class));
		}
		if (view.getId() == R.id.imageView1){
			startActivity(new Intent(this,Settings.class));
		}		
	}
}
