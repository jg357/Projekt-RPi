package de.raspberrypiapp.technikerschuleerlangen;

import de.raspberrypiapp.technikerschuleerlangen.SimpleGestureFilter.SimpleGestureListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class WetterComActivity23Uhr extends Activity implements SimpleGestureListener {
	String line;
	Thread thread;
	TextView wetter_date_23_00;
	TextView wetter_vorhersage_23_00;
	TextView wetter_niederschlagswahrscheinlichkeit_23_00;
	TextView wetter_minimal_temp_23_00;
	TextView wetter_maximal_temp_23_00;
	TextView wetter_wind_geschwindigkeit_23_00;
	TextView ueberschrift_23_00;
	String zeitraum ="";
	String wetterprognose = "";
	String niederschlagswahr = "";
	String minimaltemp = "";
	String maximaltemp = "";
	String windgeschw = "";
	WetterCom wetterCom;
	boolean a;
	boolean i = false;
	private SimpleGestureFilter detector;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("MyApp", "onCreate! Wetter23Uhr");	
        detector = new SimpleGestureFilter(this,this);
	}
	
	public void onPause() {
	    super.onPause();  // Always call the superclass method first
	    Log.d("MyApp", "Pause! Wetter23Uhr");	
	    i=false;
	    a=false;
	}
	
	protected void onResume() {
		a=true;
	    super.onResume();  // Always call the superclass method first
	    Log.d("MyApp", "onResume! Wetter23Uhr");
		long endZeitpunkt = System.currentTimeMillis()+10000;
	    setContentView(R.layout.wetter_com_23_uhr);
		wetter_date_23_00 = (TextView) findViewById(R.id.wetter_com_txt_field_date_23_00);
		wetter_vorhersage_23_00 = (TextView) findViewById(R.id.wetter_com_txt_field_vorhersage_23_00);
		wetter_niederschlagswahrscheinlichkeit_23_00 = (TextView) findViewById(R.id.wetter_com_txt_field_niederschlagswahrscheinlichkeit_23_00);
		wetter_minimal_temp_23_00 = (TextView) findViewById(R.id.wetter_com_txt_field_minimal_temp_23_00);
		wetter_maximal_temp_23_00 = (TextView) findViewById(R.id.wetter_com_txt_field_maximal_temp_23_00);
		wetter_wind_geschwindigkeit_23_00 = (TextView) findViewById(R.id.wetter_com_txt_field_windgeschwindigkeit_23_00);
		ueberschrift_23_00 = (TextView) findViewById(R.id.wetter_willkommen_23_00);
		ClientThread clientThread = new ClientThread();
		thread = new Thread(clientThread);
	    thread.start();
		while(a){// Endlosschleife laeuft bis alle Wetterdaten erhalten und ausgegeben wurden
		if (zeitraum.contains("Zeitraum:")){// Wenn der String der Website zerlegt wurde und fuer die Ausgabe formatiert
			wetter_date_23_00.setText(zeitraum);
			i=true;
		}
		
		if(wetterprognose.contains("Wetterprognose:")){// Wenn der String der Website zerlegt wurde und fuer die Ausgabe formatiert
			wetter_vorhersage_23_00.setText(wetterprognose);// dann gebe diese Daten als Textzeile auf der Activity aus
		}
		if(niederschlagswahr.contains("Niederschlagswahr.:")){// Wenn der String der Website zerlegt wurde und fuer die Ausgabe formatiert
			wetter_niederschlagswahrscheinlichkeit_23_00.setText(niederschlagswahr);// dann gebe diese Daten als Textzeile auf der Activity aus
		}
		if(minimaltemp.contains("Minimaltemperatur:")){// Wenn der String der Website zerlegt wurde und fuer die Ausgabe formatiert
			wetter_minimal_temp_23_00.setText(minimaltemp);// dann gebe diese Daten als Textzeile auf der Activity aus
		}
		if(maximaltemp.contains("Maximaltemperatur:")){// Wenn der String der Website zerlegt wurde und fuer die Ausgabe formatiert
			wetter_maximal_temp_23_00.setText(maximaltemp);// dann gebe diese Daten als Textzeile auf der Activity aus
		}
		if(windgeschw.contains("Windgeschwindigkeit:")&& i==true){// Wenn der String der Website zerlegt wurde und fuer die Ausgabe formatiert
			wetter_wind_geschwindigkeit_23_00.setText(windgeschw);// dann gebe diese Daten als Textzeile auf der Activity aus
			a=false;// Setze das Boolen auf false um die Endlosschleife zu beenden
		}
		if(!(endZeitpunkt >= System.currentTimeMillis())){// Wenn die Deathtime erreicht wurde so Beende die Endlosschleife
			onPause();
			onResume();
			break;
		}
		
	    }
	
	}
	
	public void onButtonClick(View view) {// Button Listner Methode
		if (view.getId() == R.id.btn_home){// Oeffnet bei Buttonklick die Main Activity
			startActivity(new Intent(this,RaspberryPiAppHomeActivity.class));
		}
		if (view.getId() == R.id.btn_wetter_17_00){// Offnet die Vorhersage fuer den vorherigen Zeitraum
			startActivity(new Intent(this,WetterComActivity17Uhr.class));
		}
			
		}
 	//Thread der fuer die Kommunikation mit der Website zustaendig ist
	class ClientThread implements Runnable {

		@Override
		public void run() {
			wetterCom = new WetterCom();
			String wetter = WetterCom.ausgabe;
			String date = "";
			String w_23_00s = "";
			String pc_23_00s ="";
			String tn_23_00s ="";
			String tx_23_00s ="";
			String ws_23_00s ="";
			char bearbeitet;
			int w_23_00;
			int pc_23_00;
			int tn_23_00;
			int tx_23_00;
			int ws_23_00;
			int dateIndex;
			int anfang;
			
			anfang = wetter.indexOf("<time value=\"23:00\">");

			dateIndex = (13 + (wetter.indexOf("<date value=")));
			for (int i = 0; i < 10; i++) {
				bearbeitet = wetter.charAt(dateIndex + i);
				date += bearbeitet;
			}
			zeitraum = ("Zeitraum: " + date);


			w_23_00 = (3 + (wetter.indexOf("<w>", anfang)));
			for (int i = 0; i < 3; i++) {
				bearbeitet = wetter.charAt(w_23_00 + i);
				if (bearbeitet != '<' && bearbeitet != '/' && bearbeitet != '/')
				w_23_00s += bearbeitet;
			}
			switch (Integer.parseInt(w_23_00s)) {
			case 0:
				wetterprognose="Wetterprognose: klar";
				
				break;
			case 1:
				wetterprognose="Wetterprognose: leicht bewoelkt";
				
				break;
			case 2:
				wetterprognose="Wetterprognose: wolkig";
				
				break;
			case 3:
				wetterprognose="Wetterprognose: bedeckt";
				
				break;
			case 4:
				wetterprognose="Wetterprognose: Nebel";
				
				break;
			case 5:
				wetterprognose="Wetterprognose: Spruehregen";
				
				break;
			case 6:
				wetterprognose="Wetterprognose: Regen";
				
				break;
			case 7:
				wetterprognose="Wetterprognose: Schnee";
				
				break;
			case 8:
				wetterprognose="Wetterprognose: Schauer";
				
				break;
			case 9:
				wetterprognose="Wetterprognose: Gewitter";
				
				break;
			case 10:
				wetterprognose="Wetterprognose: leicht bewoelkt";
				
				break;
			case 20:
				wetterprognose="Wetterprognose: wolkig";
				
				break;
			case 30:
				wetterprognose="Wetterprognose: bedeckt";
				
				break;
			case 40:
				wetterprognose="Wetterprognose: Nebel";
				
				break;
			case 45:
				wetterprognose="Wetterprognose: Nebel mit Reifbildung";
				
				break;
			case 49:
				wetterprognose="Wetterprognose: Nebel mit Reifbildung";
				
				break;
			case 50:
				wetterprognose="Wetterprognose: Spruehregen";
				
				break;
			case 51:
				wetterprognose="Wetterprognose: leichter Spruehregen";
				
				break;
			case 53:
				wetterprognose="Wetterprognose: Spruehregen";
				
				break;
			case 55:
				wetterprognose="Wetterprognose: starker Spruehregen";
				
				break;
			case 56:
				wetterprognose="Wetterprognose: leichter Spruehregen, gefrierend";
				
				break;
			case 57:
				wetterprognose="Wetterprognose: starker Spruehregen, gefrierend";
				
				break;
			case 60:
				wetterprognose="Wetterprognose: leichter Regen";
				
				break;
			case 61:
				wetterprognose="Wetterprognose: leichter Regen";
				
				break;
			case 63:
				wetterprognose="Wetterprognose: maessiger Regen";
				
				break;
			case 65:
				wetterprognose="Wetterprognose: starker Regen";
				
				break;
			case 66:
				wetterprognose="Wetterprognose: leichter Regen, gefrierend";
				
				break;
			case 67:
				wetterprognose="Wetterprognose: maessiger oder starker Regen, gefrierend";
				
				break;
			case 68:
				wetterprognose="Wetterprognose: leichter Schnee-Regen";
				
				break;
			case 69:
				wetterprognose="Wetterprognose: starker Schnee-Regen";
				
				break;
			case 70:
				wetterprognose="Wetterprognose: leichter Scheefall";
				
				break;
			case 71:
				wetterprognose="Wetterprognose: leichter Schneefall";
				
				break;
			case 73:
				wetterprognose="Wetterprognose: maessiger Schneefall";
				
				break;
			case 75:
				wetterprognose="Wetterprognose: starker Schneefall";
				
				break;
			case 80:
				wetterprognose="Wetterprognose: leichter Regen - Schauer";
				
				break;
			case 81:
				wetterprognose="Wetterprognose: Regen - Schauer";
				
				break;
			case 82:
				wetterprognose="Wetterprognose: starker Regen - Schauer";
				
				break;
			case 83:
				wetterprognose="Wetterprognose: leichter Schnee / Regen - Schauer";
				
				break;
			case 84:
				wetterprognose="Wetterprognose: leichter Schnee / Regen - Schauer";
				
				break;
			case 85:
				wetterprognose="Wetterprognose: leichter Schnee - Schauer";
				
				break;
			case 86:
				wetterprognose="Wetterprognose: maessiger oder starker Schnee -Schauer";
				
				break;
			case 90:
				wetterprognose="Wetterprognose: Gewitter";
				
				break;
			case 95:
				wetterprognose="Wetterprognose: leichtes Gewitter";
				
				break;
			case 96:
				wetterprognose="Wetterprognose: starkes Gewitter";
				
				break;
			case 999:
				wetterprognose="Wetterprognose: keine Angaben";
				
				break;
			default:
				wetterprognose="Wetterprognose: Wettercode nicht definiert";
			}
			pc_23_00 = (4 + (wetter.indexOf("<pc>", anfang)));
			for (int i = 0; i < 2; i++) {
				bearbeitet = wetter.charAt(pc_23_00 + i);
				if (bearbeitet != '<' && bearbeitet != '/')
				pc_23_00s += bearbeitet;
			}
			niederschlagswahr=("Niederschlagswahr.: " + pc_23_00s + "%");
			
			
			tn_23_00 = (4 + (wetter.indexOf("<tn>", anfang)));
			for (int i = 0; i < 2; i++) {
				bearbeitet = wetter.charAt(tn_23_00 + i);
				if (bearbeitet != '<' && bearbeitet != '/')
				tn_23_00s += bearbeitet;
			}
			minimaltemp=("Minimaltemperatur: " + tn_23_00s + " Grad");
			
			
			tx_23_00 = (4 + (wetter.indexOf("<tx>", anfang)));
			for (int i = 0; i < 2; i++) {
				bearbeitet = wetter.charAt(tx_23_00 + i);
				if (bearbeitet != '<' && bearbeitet != '/')
				tx_23_00s += bearbeitet;
			}
			maximaltemp=("Maximaltemperatur: " + tx_23_00s + " Grad");
			
			
			ws_23_00 = (4 + (wetter.indexOf("<ws>", anfang)));
			for (int i = 0; i < 3; i++) {
				bearbeitet = wetter.charAt(ws_23_00 + i);
				if (bearbeitet != '<' && bearbeitet != '/')
				ws_23_00s += bearbeitet;
			}
			windgeschw=("Windgeschwindigkeit: " + ws_23_00s + " km/h");
			


		}

	}
	
 	// Methode fuer den Swipe und Doppelklick Listner
	    public boolean dispatchTouchEvent(MotionEvent me){
	        // Call onTouchEvent of SimpleGestureFilter class
	         this.detector.onTouchEvent(me);
	       return super.dispatchTouchEvent(me);
	    }
	    // Methode fuer den Swipe
	     public void onSwipe(int direction) {
	      
	      switch (direction) {
	      
	      case SimpleGestureFilter.SWIPE_RIGHT : //Wisch nach rechts (zurueck zur Main Activity)
				startActivity(new Intent(this,RaspberryPiAppHomeActivity.class));
	    	                                             break;
	      case SimpleGestureFilter.SWIPE_LEFT :  //Wenn nach link gewischt wird (zurueck zur vorherigen Vorhersage)
	    	  startActivity(new Intent(this,WetterComActivity17Uhr.class));
	                                                     break;
	      
	      }
	     }
	      
		 //Methode fuer den Doppelklick
	     public void onDoubleTap() {
				startActivity(new Intent(this,RaspberryPiAppHomeActivity.class));
	     }
	
}




