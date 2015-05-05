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
import android.widget.Toast;

//Activity fuer die Settings (erreichbar ueber Home Activity RaspberryPi Picture klicken)
public class Settings extends Activity {
	public static final int PORT = 1234;
	Socket socket;
	BufferedReader in;
	PrintWriter out;
	String line;
	Thread thread;
	boolean i = false;
	boolean triggerEin =false;
	boolean triggerAus =false;
	boolean pirAus =false;
	boolean pirEin =false;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    Log.d("MyApp", "onCreate! Settings");
	}
	public void onPause() {
	    super.onPause();  // Always call the superclass method first
	    Log.d("MyApp", "Pause! Settings");	
	    out.println("BYE!");
	    out.flush();
	    i=false;
	}
	
	protected void onResume() {
	    super.onResume();  // Always call the superclass method first
	    Log.d("MyApp", "onResume! Settings");
	    setContentView(R.layout.settings);
		ClientThread clientThread = new ClientThread();
		thread = new Thread(clientThread);
		i=true;
	    thread.start();
	}
	
	// Button Listner Methode
	public void onButtonClick(View view) {
		if (view.getId() == R.id.btn_bewegungsmelder_aus){ // Aktion für Button: Bewegungsmelder aus
			out.println("PirAus!"); // Schickt am Pi den Befehl den Bewegungsmelder auszusschalten
			out.flush();
			Log.d("MyApp", "Bewegungsmelder aus!"); 
			// Wartet bis vom Pi die Bestätigung zurück kommt das der Befehl ausgeführt wurde.
			while(!pirAus){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Toast.makeText(this, "Bewegungsmelder ist aus!", Toast.LENGTH_SHORT).show(); // Gibt einen Bestätigungstext auf dem Screen aus!
			pirAus=false; // Setzt das boolean wieder zurück für den nächsten Durchgang
		}	
		if (view.getId() == R.id.btn_bewegungsmelder_ein){ // Aktion für Button: Bewegungsmelder ein
			out.println("PirAn!");	// Schickt am Pi den Befehl den Bewegungsmelder einzuschalten
			out.flush();
			Log.d("MyApp", "Bewegungsmelder ein!");
			// Wartet bis vom Pi die Bestätigung zurück kommt das der Befehl ausgeführt wurde.
			while(!pirEin){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Toast.makeText(this, "Bewegungsmelder ist an!", Toast.LENGTH_SHORT).show();  // Gibt einen Bestätigungstext auf dem Screen aus!
			pirEin=false; // Setzt das boolean wieder zurück für den nächsten Durchgang
		}
		if (view.getId() == R.id.btn_trigger_aus){  // Aktion für Button: Steckdosen Temperatur Trigger aus
			out.println("TriggerAus!"); // Schickt am Pi den Befehl den Trigger auszusschalten
			out.flush();
			Log.d("MyApp", "TriggerAus!");
			// Wartet bis vom Pi die Bestätigung zurück kommt das der Befehl ausgeführt wurde.
			while(!triggerAus){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Toast.makeText(this, "Trigger ist aus!", Toast.LENGTH_SHORT).show(); // Gibt einen Bestätigungstext auf dem Screen aus!
			triggerAus=false; // Setzt das boolean wieder zurück für den nächsten Durchgang
		}
		if (view.getId() == R.id.btn_trigger_ein){  // Aktion für Button: Steckdosen Temperatur Trigger ein
			out.println("TriggerAn!"); // Schickt am Pi den Befehl den Trigger anzuschalten
			out.flush();
			Log.d("MyApp", "TriggerAn!");
			// Wartet bis vom Pi die Bestätigung zurück kommt das der Befehl ausgeführt wurde.
			while(!triggerEin){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Toast.makeText(this, "Trigger ist ein!", Toast.LENGTH_SHORT).show();  // Gibt einen Bestätigungstext auf dem Screen aus!
			triggerEin=false;  // Setzt das boolean wieder zurück für den nächsten Durchgang
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
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			try {
				while (i) {		// Solange die Activity aktiv ist werden mit dem BufferdReader
								// Nachrichten vom Raspberry Pi abgerufen
					line = in.readLine();
					if (line != null) { // Wenn eine Nachricht vom Pi kommt die...
						Log.d("MyApp", line);						
					    if(line.contains("TriggerIstAn!")){ // TrigerIstAn! enthält dann..
					    	Log.d("MyApp", line);
					    	triggerEin=true;				//setze das Boolean triggerEin auf true um dies in der
					    									//Activity auszuwerten und auszugeben!
					    }
					    if(line.contains("TriggerIstAus!")){ // Wenn die Nachricht TriggerIstAus! kommt dann...
					    	Log.d("MyApp", line);
					    	triggerAus=true;				//setze das Boolean triggerAus auf true um dies in der
															//Activity auszuwerten und auszugeben!
					    }
					    if(line.contains("PirIstAn!")){		// Wenn die Nachricht PirIstAn! kommt dann...
					    	Log.d("MyApp", line);
					    	pirEin=true;					//setze das Boolean triggerAus auf true um dies in der
															//Activity auszuwerten und auszugeben!
					    }
					    if(line.contains("PirIstAus!")){	// Wenn die Nachricht PirIstAus! kommt dann...
					    	Log.d("MyApp", line);
					    	pirAus=true;					//setze das Boolean triggerAus auf true um dies in der
															//Activity auszuwerten und auszugeben!
					    }				    
					    
					}

				}
			}catch (IOException e) {
					
				}
			
		}

		}

	}

