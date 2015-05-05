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
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;
import de.raspberrypiapp.technikerschuleerlangen.SimpleGestureFilter.SimpleGestureListener;

public class UeberwachungskameraActivity extends Activity implements SimpleGestureListener{
	public static final int PORT = 1234;
	Socket socket;
	BufferedReader in;
	PrintWriter out;
	String line;
	String[] werte = new String[2];
	boolean i= true;
	boolean ack=false;
	String text;
	TextView satz;
	ClientThread clientThread;
	Thread thread;
	private SimpleGestureFilter detector;
	String[] bilder = new String[11];
	WebView webview;
	int position=1;
	
	protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	}
	
	public void onResume() {
	    super.onResume();  // Always call the superclass method first
	    Log.d("MyApp", "Resume!");
        // Detect touched area 
        detector = new SimpleGestureFilter(this,this);
		i=true;
		clientThread = new ClientThread();
		thread = new Thread(clientThread);
		thread.start();	
		webview = new WebView(this);
		webview.clearCache(true);
		webview.setPadding(0, 0, 0, 0);
		webview.setBackgroundColor(Color.BLACK);
		webview.setInitialScale(1);
		WebSettings webSettings = webview.getSettings();
		webSettings.setUseWideViewPort(true);
		setContentView(webview);
		long endZeitpunkt = System.currentTimeMillis()+10000; // Setze die Deathtime auf 10 Sekunden
	     while(!ack){  // Solange vom Pi keine Bilderliste zurueckgekommen ist fuehre diese Endlosschleife aus
	    	 try {
				Thread.sleep(100);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
	 		if(!(endZeitpunkt >= System.currentTimeMillis())){
				onPause();
				break;
	 		}
	     }
		Toast.makeText(this, bilder[1], Toast.LENGTH_SHORT).show(); // Gebe das erste Bild als Info Text auf dem Bildschirm aus
	}
	
	public void onPause() {
	    super.onPause();  // Always call the superclass method first
	    Log.d("MyApp", "Pause!");
	    try{
	    out.println("BYE!"); // Gebe den Pi die Nachricht die Verbindung zu Beenden!
	    out.flush();
	    }
	    catch(NullPointerException e){
	    	Log.e("MyApp", "Out Writer nicht bereit!");
	    }
	    i= false; // setze die Boolen fuer die Endlosschleifen zurueck
	    ack=false;
	}
	
	
 		// Methode fuer den Swipe und Doppelklick Listner
	    public boolean dispatchTouchEvent(MotionEvent me){
	        // Call onTouchEvent of SimpleGestureFilter class
	         this.detector.onTouchEvent(me);
	       return super.dispatchTouchEvent(me);
	    }
	    // Methode fuer den Swipe
	     public void onSwipe(int direction) {
	      String str = "";
	      
	      switch (direction) {
	      
	      case SimpleGestureFilter.SWIPE_RIGHT :
	    	  if(position<10){ // Wenn noch nicht das 10 Bild erreicht ist 
	    		  webview.loadUrl("http://juliangeus.selfhost.eu/bilder/"+bilder[++position]);	// So zeige das Bild an
	    		  str=bilder[position]; // Setze den String fuer die Bilder Information Ausgabe zusammen
	    	  }
	    	  if(position==10){ // Wenn das letzte Bild erreicht ist
	    		  str=bilder[10]; // Setze den String fuer die Bilder Information anzeige zusammen
	    		  Toast.makeText(this, "Ende erreicht!	", Toast.LENGTH_SHORT).show(); // Gebe Info aus das Ende ist erreicht 
	    		  Toast.makeText(this, str, Toast.LENGTH_SHORT).show(); // und Zeige den Namen des Letzten Bildes als Info an
	    	  }
	    	  else{
	    		  Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	    	  }
	    	  											 break;
	      case SimpleGestureFilter.SWIPE_LEFT :  
	    	  if (position==1) // Wenn das letzte Bild geladen ist so gehe zurueck zur Main Activity
				startActivity(new Intent(this,RaspberryPiAppHomeActivity.class));
	    	  else{
	    		  webview.loadUrl("http://juliangeus.selfhost.eu/bilder/"+bilder[--position]); // Lade das vorherige Bild
	    		  str=bilder[position]; // Und setze den String fuer die Bilder Info Ausgabe
	    		  Toast.makeText(this, str, Toast.LENGTH_SHORT).show(); // und gebe die Bilder Info Ausgabe auf dem Bildschirm aus
	    	  }
	    	  											break;
	      }
	      }
	      
		 //Methode fuer den Doppelklick
	     public void onDoubleTap() {
	        //Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
				startActivity(new Intent(this,RaspberryPiAppHomeActivity.class));
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
				out.println("Bilder!"); // Gib den Pi den Befehl die Bilderliste zu uebertragen!
				out.flush();

			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			try {
				while (i) {		// Solange die Activity aktiv ist werden mit dem BufferdReader
								// Nachrichten vom Raspberry Pi abgerufen
					line = in.readLine();
					if (line != null) {
						Log.d("MyApp", line);						
					    if(line.contains("Bilder: ")){// Wenn die Nachricht Bilderliste vom Pi kommt so
					    	ack=true; // Setzt das Boolean fuer die Endlosschleife in der Activity auf true (beendet die Endlosschleife)
					    	Log.d("MyApp", line);
					    	bilder=line.split(" "); // splitte den String und schreibe ihn in das Array
					    	webview.loadUrl("http://juliangeus.selfhost.eu/bilder/"+bilder[1]); // zeige das zuletzt aufgenommene Bild an
					    						    }											
					}

				}
			} catch (IOException e) {
				
			}
		}
	}
}
