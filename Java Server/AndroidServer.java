import java.io.*;
import java.net.*;

/* Klasse: AndroidServer
 * 
 * Beschreibung: Stellt eine Socket Verbindung zur Android App her, wertet empfangene Befehle aus
 * 				 und sendet die entsprechenden Daten zurück.
 * 
 * Methoden: say()
 * 
 * Author: Projektgruppe Pi 2015
 */

public class AndroidServer implements Runnable {

	// Server Port definieren
	public static final int PORT = 1234;
	
	// Variablen für Socket Verbindung
	Socket socket;
	ServerSocket server;
	
	Thread threadTalkserver;
	Thread chatProgrammText;
	TalkServer talkServer;

	BufferedReader in;
	PrintWriter out;
	
	String newline = "\n";
	boolean running;
	
	public AndroidServer() {
		try {
			
			// Instanz der inneren Klasse TalkServer erstellen
			talkServer = new TalkServer();
			
			// Thread Objekt erstellen und starten
			threadTalkserver = new Thread(talkServer);
			threadTalkserver.start();
			threadTalkserver.setPriority(Thread.MAX_PRIORITY);
			
			// Socket initialisieren und Input und Output Stream erstellen
			socket = new Socket("127.0.0.1", PORT);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			say("Verbindung zum Server hergestellt!");
			say("Warte auf Smartphone");
		} 
		catch (SocketException f){
			say("SocketException Fehler 0!");
		}
		catch (IOException e) {
			say("Verbindung zum Server fehlgeschlagen!");
			System.out.println("Verbindung zum Server fehlgeschlagen!");
			System.exit(1);
		}
		running = true;
		
		// Serverseitigen Client zum Senden von Informationen zum Smartphone erstellen
		chatProgrammText = new Thread(this);
		chatProgrammText.start();
		chatProgrammText.setPriority(Thread.MAX_PRIORITY);
	}

	public void run() {

		String line;
		try {
			while (running) {
				line = in.readLine();
				if (line != null) {
					
					// Mögliche Befehle der App auswerten und entsprechende Daten zurückliefern
					
					if (line.contains("ZimmerauswertungAnalog")) {
						say("ZimmerauswertungAnalog!");
						try {
							// Aktuelle Temperatur und Luftfeuchtigkeit aus Textdatei abrufen
							FileReader fr = new FileReader(
									"/home/pi/Projekt/Java/temp");
							BufferedReader br = new BufferedReader(fr);
							String sA;
							sA = br.readLine();
							String[] tempA;
							String delimiter = " ";
							tempA = sA.split(delimiter);
							out.println("ZimmerauswertungTemp:"+tempA[1]);
							out.flush();
							out.println("ZimmerauswertungFeuchte:"+tempA[0]);
							out.flush();
							say("ZimmerauswertungFeuchte:"+tempA[0]);
							say("ZimmerauswertungTemperatur:"+tempA[1]);
						} catch (ArrayIndexOutOfBoundsException e) {
							say("temp File leer deswegen temp Array ohne Werte!");
						} catch (NullPointerException f) {
							say("TemperaturServer laeuft nicht!");
						}
					}else if (line.contains("TriggerAn!")){
						say("Trigger eingeschaltet!");
						Config.setTriggerState(1);
						out.println("TriggerIstAn!");
					}else if (line.contains("TriggerAus!")){
						say("Trigger ausgeschaltet!");
						Config.setTriggerState(0);
						out.println("TriggerIstAus!");
					}else if (line.contains("PirAn!")){
						say("Bewegungssensor eingeschaltet!");
						Config.setPirState(1);
						out.println("PirIstAn!");
					}else if (line.contains("PirAus!")){
						say("Bewegungssensor ausgeschaltet!");
						Config.setPirState(0);
						out.println("PirIstAus!");
					}else if (line.contains("TempChart!")) {
						say("TempChart übertragen!");
						String tempChart=database.getDayValues("temp", 1);
						out.println("TempChart: "+tempChart);
						say("Tempchart: "+tempChart);
					}else if (line.contains("AvgWerte!")) {
						say("AvgWerte übertragen!");
						String avgWerte=database.getAvgValues();
						out.println("AvgWerte: "+avgWerte);
						say("AvgWerte: "+avgWerte);
					}else if (line.contains("TempChart3d!")) {
						say("TempChart übertragen!");
						String tempChart=database.getDayValues("temp", 3);
						out.println("TempChart3d: "+tempChart);
						say("Tempchart3d: "+tempChart);
					}else if (line.contains("TempChart7d!")) {
						say("TempChart übertragen!");
						String tempChart=database.getDayValues("temp", 7);
						out.println("TempChart7d: "+tempChart);
						say("Tempchart7d: "+tempChart);
					}else if (line.contains("HumChart!")) {
						say("HumChart übertragen!");
						String humChart=database.getDayValues("hum", 1);
						out.println("Humchart: "+humChart);
						say("Humchart: "+humChart);
					}else if (line.contains("HumChart3d!")) {
						say("HumChart3d übertragen!");
						String humChart=database.getDayValues("hum", 3);
						out.println("Humchart3d: "+humChart);
						say("Humchart3d: "+humChart);
					}else if (line.contains("HumChart7d!")) {
						say("HumChart übertragen!");
						String humChart=database.getDayValues("hum", 7);
						out.println("Humchart7d: "+humChart);
						say("Humchart: "+humChart);
					}else if (line.contains("Bilder!")) {
						say("Bilderliste uebertragen!");
						String bilder=database.getPictureNames();
						out.println("Bilder: "+bilder);
					} else if (line.contains("WohnzimmerSteckdoseEin!")) {
						sensoren.switchControl(1, 1);
						say("WohnzimmerSteckdoseEin");
						out.println("WohnzimmerIstEin!");
					} else if (line.contains("WohnzimmerSteckdoseAus!")) {
						 sensoren.switchControl(1, 0);
						say("WohnzimmerSteckdoseAus");
						say("Schaltzeit Steckdose Wohnzimmer übertragen!");
						String schaltzeit = database.getSwitchTime(1);
						out.println("SchaltzeitWohnzimmer: "+schaltzeit);
						say("Schaltzeit Wohnzimmer: "+schaltzeit);
					} else if (line.contains("KuecheSteckdoseEin!")) {
						sensoren.switchControl(2, 1);
						say("KuecheSteckdoseEin");
						out.println("KuecheIstEin!");
					} else if (line.contains("KuecheSteckdoseAus!")) {
						sensoren.switchControl(2, 0);
						say("KuecheSteckdoseAus");
						say("Schaltzeit Steckdose Kueche übertragen!");
						String schaltzeit = database.getSwitchTime(2);
						out.println("SchaltzeitKueche: "+schaltzeit);
						say("Schaltzeit Kueche: "+schaltzeit);
					}else if (line.contains("KinderzimmerSteckdoseEin!")) {
						 sensoren.switchControl(3, 1);
						say("KinderzimmerSteckdoseEin");
						out.println("KinderzimmerIstEin!");
					} else if (line.contains("KinderzimmerSteckdoseAus!")) {
						sensoren.switchControl(3, 0);
						say("KinderzimmerSteckdoseAus");
						String schaltzeit = database.getSwitchTime(3);
						out.println("SchaltzeitKinderzimmer: "+schaltzeit);
						say("Schaltzeit Kinderzimmer: "+schaltzeit);
					}else if (line.contains("SteckdosenAnsteuern!")) {
						say("SteckdosenAnsteuern!");
						
					// Wenn der Befehl BYE! empfangen wird, wird der Server beendet und eine neue Instanz gestartet
					} else if (line.contains("BYE!")) {
						say("BYE!");
						running = false;
						new AndroidServer();
					}
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.err.println(sensoren.currentTime()  + " " +  e.getMessage());
			}

		} catch (IOException e) {
			say("Verbindung zum Server abgebrochen");
		} catch (DatenbankException e) {
			System.err.println(sensoren.currentTime()  + " " +  e.getMessage());
		}
	}

	public void say(String msg) {
		System.out.println("*** " + msg + " ***\n");
	}

	/*
	 * Innere Klasse TalkServer
	 * Baut bei Anfrage von der App eine Verbindung zwischen App und Server auf.
	 */
	
	class TalkServer implements Runnable {
		Thread Talkdienst1 = new Thread();
		Thread Talkdienst2 = new Thread();
		TalkDienst talkdienst1;
		TalkDienst talkdienst2;

		public void run() {
			Socket c1;
			Socket c2;
			// Argumentanzahl überprüfen

			try {
				// Einen für den Server erzeugen
				ServerSocket server = new ServerSocket(1234);
				System.out.println("Der Server laeuft");
				c1 = server.accept();
				c2 = server.accept();
				talkdienst1 = new TalkDienst(c1, c2);
				talkdienst2 = new TalkDienst(c2, c1);
				talkdienst1.start();
				talkdienst2.start();
				talkdienst1.setPriority(Thread.MAX_PRIORITY);
				talkdienst2.setPriority(Thread.MAX_PRIORITY);
				// Endlosschleife
				
				while (true) {
					if (!running) {
						// Für je zwei Clients, die eine Verbindung aufbauen,
						// zwei Talk-Dienst Threads starten
						c1 = server.accept();
						c2 = server.accept();
						talkdienst1 = new TalkDienst(c1, c2);
						talkdienst2 = new TalkDienst(c2, c1);
						talkdienst1.start();
						talkdienst2.start();
						talkdienst1.setPriority(Thread.MAX_PRIORITY);
						talkdienst2.setPriority(Thread.MAX_PRIORITY);
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						System.err.println(sensoren.currentTime()  + " " +  e.getMessage());
					}
				}

			} 
			catch(SocketException f){
				
			}
			
			catch (IOException e) {
				
			}

		}

		class TalkDienst extends Thread {

			Socket c1, c2; // Sockets für die beiden Clients
			BufferedReader in; // Eingabe-Ströme zu den Clients
			PrintWriter out; // Ausgabe-Ströme zu den Clients

			/**
			 * Eingabe- und Ausgabestroeme zu den Clients erzeugen
			 *
			 */
			TalkDienst(Socket sin, Socket sout) {
				// Die Client-Sockets in den Instanzvariablen speichern
				c1 = sin;
				c2 = sout;
				// try-catch-Block beginnen
				try {
					// Den Eingabe-Strom vom anderen Client erzeugen
					in = new BufferedReader(new InputStreamReader(
							c1.getInputStream()));

					// Den Ausgabe-Strom zum anderen Client erzeugen
					out = new PrintWriter(c2.getOutputStream(), true);
					out.println("*** "
							+ "Chatpartner gefunden, es kann losgehen!"
							+ " ***");
					out.flush();
					
				} 
			
			catch (SocketException f){
			say("Socket Exception Fehler3!");	
			}
				catch (IOException e) {
					
				}
			}

			// run-Methode ueberschreiben: Abarbeitung des eigentlichen
			// Protokolls

			public void run() {
				String line;

				try {
					while (true) {
						line = in.readLine();
						if (line != null) {
							out.println(line);
							out.flush();
						}
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							System.err.println(sensoren.currentTime()  + " " +  e.getMessage());
						}
					}
				} 
				catch (SocketException f){
					say("SocketException Fehler 4!");
					say("BYE!");
					running = false;
					new AndroidServer();
				}
				
				catch (IOException e) {
					System.err.println(sensoren.currentTime()  + " " +  e.getMessage());
				}
			}
		}
	}

}
