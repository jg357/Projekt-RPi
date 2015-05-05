package de.raspberrypiapp.technikerschuleerlangen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//Activity fuer Steckdose anssteuern!
public class SteckdosenAnsteuernActivity extends Activity {
	private Spinner spinner1;
	public static final int PORT = 1234;
	Socket socket;
	BufferedReader in;
	PrintWriter out;
	String line;
	boolean i= true;
	String text;
	TextView satz;
	ClientThread clientThread;
	Thread thread;
	String[] schaltzeitWohnzimmer ={"NULL","NULL"};
	String[] schaltzeitKueche ={"NULL","NULL"};
	String[] schaltzeitKinderzimmer ={"NULL","NULL"};
	boolean wohnzimmerEin=false;
	boolean kuecheEin=false;
	boolean kinderzimmerEin=false;
	


	
	protected OnItemSelectedListener onSpinnerItemSelect = new OnItemSelectedListener() {

		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// wann immer ein Spinner geändert wird (etwas ausgewählt),
			// passe den Satz an
			updatePhrase();
		}

		public void onNothingSelected(AdapterView<?> parent) {
			// wann immer ein Spinner geändert wird (Nichts ausgewählt),
			// passe den Satz an
			updatePhrase();
		}

		public void updatePhrase() {
			text = (String) spinner1.getSelectedItem();		// faengt den Eintrag aus dem ausgewählten Menue eintrag ab
		}													// um diesen Auswerten zu koennen.

	};
		
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public void onPause() {
	    super.onPause();  // Always call the superclass method first
	    Log.d("MyApp", "Pause!");
	    try{
	    out.println("BYE!"); // sendet den Pi den Hinweis die Verbindung zu beenden.
	    out.flush();
	    }
	    catch(NullPointerException e){
	    	Log.e("MyApp", "Out Writer nicht bereit!");
	    }
	    i= false; // setzt das Activity boolean auf false (beendet die Endlosschleife im Kommunikationsthread)
	}
	
	
	public void onResume() {
	    super.onResume();  // Always call the superclass method first
	    Log.d("MyApp", "Resume!");	
	    setContentView(R.layout.steckdosen_steuerung);
		spinner1 = (Spinner) findViewById(R.id.sp_steckdose);
		spinner1.setOnItemSelectedListener(onSpinnerItemSelect);
		i=true;
		clientThread = new ClientThread();
		thread = new Thread(clientThread);
		thread.start();		
	}
	
	//Button Listner Methode	
	public void onButtonClick(View view){
		if (view.getId() == R.id.btn_steckdose_ein){ // Wenn der Button Steckdose ein geklickt wurde
			if (text.contains("Wohnzimmer")){ 		 // und wenn im Menue Wohnzimmer ausgewaehlt ist
			    try{
					out.println("WohnzimmerSteckdoseEin!"); // sende den Pi den Befehl 
					out.flush();
				    }
				    catch(NullPointerException e){
				    	Log.e("MyApp", "Out Writer nicht bereit!");
				    	satz.setText("RaspberryPi nicht erreichbar!");
				    }

				Log.d("MyApp", "WohnzimmerSteckdoseEin!");
				// Wartet solange bis vom Pi die Bestätigung zurückkommt das der Befehl ausgeführt wurde.
				while(!wohnzimmerEin){
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Toast.makeText(this, "Wohnzimmer Steckdose ist ein!", Toast.LENGTH_SHORT).show(); // Gibt eine Bestätigung auf dem Screen aus.
				wohnzimmerEin=false; // setzt das Boolean wieder zurück für den nächsten Durchgang
			}
			if (text.contains("Kueche")){	// Wenn der Menue Eintrag Kueche ausgewaehlt wurde 
				try{						// und der Button Steckdose ein gedrückt wurde...
					out.println("KuecheSteckdoseEin!");	// Schicke den Pi den Befehl KuecheSteckdoseEin!
					out.flush();
				    }
				    catch(NullPointerException e){
				    	Log.e("MyApp", "Out Writer nicht bereit!");
				    	satz.setText("RaspberryPi nicht erreichbar!");	
				    }
				Log.d("MyApp", "KuecheSteckdoseEin!");	
				// Wartet solange bis vom Pi die Bestätigung zurückkommt das der Befehl ausgeführt wurde.
				while(!kuecheEin){
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Toast.makeText(this, "Kueche Steckdose ist ein!", Toast.LENGTH_SHORT).show(); // Gibt eine Bestätigung auf dem Screen aus.
				kuecheEin=false;	// setzt das Boolean wieder zurück für den nächsten Durchgang
			}
			if (text.contains("Kinderzimmer")){ // Wenn im Menue Kinderzimmer ausgewaehlt wurde und
				try{							// der Button Steckdose ein gedrueckt wurde dann...
					out.println("KinderzimmerSteckdoseEin!"); // Sende den Pi den Befehl KinderzimmerSteckdoseEin!
					out.flush();
				    }
				    catch(NullPointerException e){
				    	Log.e("MyApp", "Out Writer nicht bereit!");
				    	satz.setText("RaspberryPi nicht erreichbar!");	
				    }
				Log.d("MyApp", "KinderzimmerSteckdoseEin!");
				// Wartet solange bis vom Pi die Bestätigung zurückkommt das der Befehl ausgeführt wurde.
				while(!kinderzimmerEin){
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Toast.makeText(this, "Kinderzimmer Steckdose ist ein!", Toast.LENGTH_SHORT).show();// Gibt eine Bestätigung auf dem Screen aus.
				kinderzimmerEin=false; // setzt das Boolean wieder zurück für den nächsten Durchgang
			}
		}
		
		if (view.getId() == R.id.btn_steckdose_aus){ // Wenn der Button Steckdose aus gedrückt wurde (Button Listner Methode)
			if (text.contains("Wohnzimmer")){ // Wenn im Menue Wohnzimmer ausgewählt war
				try{
					out.println("WohnzimmerSteckdoseAus!");  //Schicke den Befehl an das Pi
					out.flush();
				    }
				    catch(NullPointerException e){
				    	Log.e("MyApp", "Out Writer nicht bereit!");
				    	satz.setText("RaspberryPi nicht erreichbar!");	
				    }
			Log.d("MyApp", "WohnzimmerSteckdoseAus!");	
			// Wartet solange bis vom Pi die Bestätigung zurückkommt das der Befehl ausgeführt wurde.
			while(schaltzeitWohnzimmer[0].equals("NULL")){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Toast.makeText(this, "Wonzimmer Steckdose ist aus!", Toast.LENGTH_SHORT).show(); // Gibt eine Bestätigung auf den Bildschirm aus
			String s= ("Schaltzeit Wohnzimmer: " +schaltzeitWohnzimmer[1]);  // Setzt den String fuer die Schaltdauerausgabe zusammen
			Toast.makeText(this, s, Toast.LENGTH_SHORT).show(); // Und gibt die Schaltdauer aus
			schaltzeitWohnzimmer[0]="NULL"; //Setzt das Array wieder auf default Wert fuer den naechsten Durchlauf
			schaltzeitWohnzimmer[1]="NULL";
			}
			if (text.contains("Kueche")){ //Wenn im Menue Kueche ausgewaehlt war und der Button Stefckdose aus gedrueckt wurde...
				try{
					out.println("KuecheSteckdoseAus!"); // dann schicke den Befehl an das Pi
					out.flush();
				    }
				    catch(NullPointerException e){
				    	Log.e("MyApp", "Out Writer nicht bereit!");
				    	satz.setText("RaspberryPi nicht erreichbar!");	
				    }
				Log.d("MyApp", "KuecheSteckdoseAus!");
				// Wartet solange bis die Bestaetigung des Pi zusammen mit der Schaltzeit zureuck kommt
				while(schaltzeitKueche[0].equals("NULL")){
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Toast.makeText(this, "Kueche Steckdose ist aus!", Toast.LENGTH_SHORT).show(); // Gibt eine Bestaetigung auf dem Screen aus
				String s= ("Schaltzeit Kueche: " +schaltzeitKueche[1]); // Setzt den String fuer die Schaltdauerausgabe zusammen
				Toast.makeText(this, s, Toast.LENGTH_SHORT).show();// Und gibt die Schaltdauer aus
				schaltzeitKueche[0]="NULL";//Setzt das Array wieder auf default Wert fuer den naechsten durchlauf
				schaltzeitKueche[1]="NULL";
			}
			if (text.contains("Kinderzimmer")){ // Wenn im Menue Kinderzimmer ausgewaehlt war und der Button Steckdose aus gedrueckt wurde...
				try{
					out.println("KinderzimmerSteckdoseAus!");// dann gib den Pi den befehl
					out.flush();
				    }
				    catch(NullPointerException e){
				    	Log.e("MyApp", "Out Writer nicht bereit!");
				    	satz.setText("RaspberryPi nicht erreichbar!");	
				    }
				Log.d("MyApp", "KinderzimmerSteckdoseAus!");
				//Wartet bis die Bestaetigung vom Pi zurueckkommt
				while(schaltzeitKinderzimmer[0].equals("NULL")){
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Toast.makeText(this, "Kinderzimmer Steckdose ist aus!", Toast.LENGTH_SHORT).show(); // Gibt eine Bestaetigung auf dem Screen aus
				String s= ("Schaltzeit Kinderzimmer: " +schaltzeitKinderzimmer[1]); // Setzt den String fuer die Schaltdauerausgabe zusammen
				Toast.makeText(this, s, Toast.LENGTH_SHORT).show();//Und gebe die Schaltdauer auf dem Bildschirm aus
				schaltzeitKinderzimmer[0]="NULL"; //Setzt das Array wieder auf default Wert fuer den naechsten durchlauf
				schaltzeitKinderzimmer[1]="NULL";
			}
		}
		
	}
	
 	//Thread der fuer die Kommunikation mit dem RaspberryPi zustaendig ist
	class ClientThread implements Runnable {

		
		public void run() {
			try {
				//InetAddress serverAddr = InetAddress.getByName("192.168.2.101");
				InetAddress serverAddr = InetAddress.getByName("juliangeus.selfhost.eu");
				socket = new Socket(serverAddr, PORT);
				Log.d("MyApp", "I am here");
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
				out.println("SteckdosenAnsteuern!");
				out.flush();
				while (i) {		// Solange die Activity aktiv ist werden mit dem BufferdReader
								// Nachrichten vom Raspberry Pi abgerufen
					line = in.readLine();
					if (line != null) {
						Log.d("MyApp", line);						
					    if(line.contains("SchaltzeitWohnzimmer: ")){ //Wenn die Schaltzeit vom Pi zurueck kommt
					    	Log.d("MyApp", line);
					    	schaltzeitWohnzimmer=line.split(" "); //dann splitte den String und schreibe ihm in das Array
					    }
					    if(line.contains("SchaltzeitKueche: ")){ //Wenn die Schaltzeit vom Pi zurueck kommt
					    	Log.d("MyApp", line);
					    	schaltzeitKueche=line.split(" "); //dann splitte den String und schreibe ihm in das Array
					    }
					    if(line.contains("SchaltzeitKinderzimmer: ")){ //Wenn die Schaltzeit vom Pi zurueck kommt
					    	Log.d("MyApp", line);
					    	schaltzeitKinderzimmer=line.split(" ");//dann splitte den String und schreibe ihm in das Array
					    }
					    if(line.contains("WohnzimmerIstEin!")){ // Wenn die Bestaetigung vom Pi zurueck kommt
					    	Log.d("MyApp", line);
					    	wohnzimmerEin=true;  //dann setze das Boolean auf true um die endlosschleife in der Activity zu beenden
					    }
					    if(line.contains("KuecheIstEin!")){// Wenn die Bestaetigung vom Pi zurueck kommt
					    	Log.d("MyApp", line);
					    	kuecheEin=true;//dann setze das Boolean auf true um die endlosschleife in der Activity zu beenden
					    }
					    if(line.contains("KinderzimmerIstEin!")){// Wenn die Bestaetigung vom Pi zurueck kommt
					    	Log.d("MyApp", line);
					    	kinderzimmerEin=true;//dann setze das Boolean auf true um die endlosschleife in der Activity zu beenden
					    }		    
					    
					}
				}

			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}
	}
}
