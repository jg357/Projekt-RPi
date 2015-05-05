package de.raspberrypiapp.technikerschuleerlangen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class Avg extends Activity {
	public static final int PORT = 1234;
	Socket socket;
	BufferedReader in;
	PrintWriter out;
	String line;
	Thread thread;
	TextView avg_luftfeuchtigkeit;
	TextView avg_temperatur;
	TextView max_luftfeuchtigkeit;
	TextView max_temperatur;
	TextView min_luftfeuchtigkeit;
	TextView min_temperatur;
	boolean i = false;
	String[] werte = new String[7];

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("MyApp", "onCreate! AVG");
	}

	public void onPause() {
		super.onPause(); // Always call the superclass method first
		Log.d("MyApp", "Pause! AVG");
		i = false;
	}

	protected void onResume() {
		super.onResume(); // Always call the superclass method first
		Log.d("MyApp", "onResume! AVG");
		setContentView(R.layout.avg);
		avg_luftfeuchtigkeit = (TextView) findViewById(R.id.avg_txt_field_avg_luftfeuchtigkeit);
		avg_temperatur = (TextView) findViewById(R.id.avg_txt_field_avg_temperatur);
		max_luftfeuchtigkeit = (TextView) findViewById(R.id.avg_txt_field_max_luftfeuchtigkeit);
		max_temperatur = (TextView) findViewById(R.id.avg_txt_field_max_temperatur);
		min_luftfeuchtigkeit = (TextView) findViewById(R.id.avg_txt_field_min_luftfeuchtigkeit);
		min_temperatur = (TextView) findViewById(R.id.avg_txt_field_min_temperatur);
		ClientThread clientThread = new ClientThread();
		thread = new Thread(clientThread);
		i = true;
		thread.start();
		werte[0] = "NULL"; 	// Belegt das erste Element mit dem String "NULL" um
							//auf diesen zu pruefen, wenn dieser nicht mehr verhanden ist
							//so bedeutet dies das Werte von Raspberry Pi uebertragen wurden!
		long endZeitpunkt = System.currentTimeMillis() + 10000; // Legt 10 Sekunden als Deathtime fest
															    // wenn diese erreicht werden so wird die
																// Endlosschleife beendet!
		while (werte[0].equals("NULL")) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!(endZeitpunkt >= System.currentTimeMillis())) {
				onPause();
				break;
			}
		}
		// Ab hier werden die Textzeilen der Android Activity mit Werte vom Pi befüllt.
		avg_luftfeuchtigkeit.setText("AVG Hygro. % Rel.: " + werte[1]);
		avg_temperatur.setText("AVG Thermo. °C: " + werte[2]);
		max_luftfeuchtigkeit.setText("Max. Hygro. % Rel.: " + werte[4]);
		max_temperatur.setText("Max. Thermo. °C: " + werte[3]);
		min_luftfeuchtigkeit.setText("Min. Hygro. % Rel.: " + werte[6]);
		min_temperatur.setText("Min. Thermo. °C: " + werte[5]);
	}

	public void onButtonClick(View view) {
		if (view.getId() == R.id.btn_home) { //Methode des Button Listner -> startet Home Activity
			startActivity(new Intent(this, RaspberryPiAppHomeActivity.class));
		}
	}

	//Thread der fuer die Kommunikation mit dem RaspberryPi zustaendig ist
	class ClientThread implements Runnable {

		public void run() {
			try {
				// InetAddress.getByName("192.168.2.101");
				InetAddress serverAddr = InetAddress.getByName("juliangeus.selfhost.eu");
				socket = new Socket(serverAddr, PORT);
				Log.d("MyApp", "I am here");
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
				out.println("AvgWerte!"); // Gib den Pi den Befehl die AvgWerte zu übermitteln
				out.flush();

			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			try {
				while (i) {  // Solange die Activity aktiv ist werden mit dem BufferdReader
							 // Nachrichten vom Raspberry Pi abgerufen
					line = in.readLine();
					if (line != null) {
						Log.d("MyApp", line);
						if (line.contains("AvgWerte: ")) { // Wenn die Nachricht AvgWerte vom Pi kommt so
							Log.d("MyApp", line);		   // wird der String vom gesplittet und in das Array geschrieben
							werte = line.split(" ");
							out.println("BYE!");  		   // Gibt den Pi den Hinweis das es die Verbindung beenden kann.
							out.flush();
						}
					}
				}
			} catch (IOException e) {
				
			}
		}

	}

}
