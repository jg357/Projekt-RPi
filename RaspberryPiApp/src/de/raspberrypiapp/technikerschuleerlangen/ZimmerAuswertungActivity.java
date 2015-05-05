package de.raspberrypiapp.technikerschuleerlangen;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ZimmerAuswertungActivity extends Activity {
	TextView ueberschrift;


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public void onPause() {
	    super.onPause();  // Always call the superclass method first
	    Log.d("MyApp", "Pause!");
	}
	
	public void onResume() {
	    super.onResume();  // Always call the superclass method first
	    Log.d("MyApp", "Resume!");	
	    setContentView(R.layout.zimmer_auswertung);
		ueberschrift = (TextView) findViewById(R.id.zimmer_willkommen);
	}


	public void onButtonClick(View view) { //Button Listner Methode
		
		if (view.getId() == R.id.btn_momentanwerte) { // Wenn Button gedrueckt wurde dann...
			startActivity(new Intent(this,ThermometerActivity.class)); // Starte die passende Activity	
		}	
		
		if (view.getId() == R.id.btn_langzeitauswertung) {  // Wenn Button gedrueckt wurde dann...	
			startActivity(new Intent(this,TempChart24h.class)); // Starte die passende Activity		
		}	
		
		if (view.getId() == R.id.btn_avg) {  // Wenn Button gedrueckt wurde dann...
			startActivity(new Intent(this,Avg.class)); // Starte die passende Activity		
		}
	}


}
