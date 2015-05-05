
/* Klasse: server
 * 
 * Beschreibung: Die Klasse server enthält die main Methode, in der alle Threads gestartet werden.
 * 
 * Methoden: -
 * 
 * Author: Projektgruppe Pi 2015
 */

public class server {
	
	public static void main(String[] args) {
		
		//Einslesen der config.ini Datei
		Config.confRead();
		System.out.println("Konfigurationsdatei eingelesen!");
		
		//Starten des Threads für den Temperatursensor
		sensoren temp = new sensoren(1);
		Thread temp1 = new Thread(temp);
		temp1.start();
		System.out.println("Temperatur Thread gestartet!");
		
		//Starten des Threads für den Trigger
		sensoren trigger = new sensoren(3);
		Thread trigger1 = new Thread(trigger);
		trigger1.start();
		System.out.println("Trigger Thread gestartet!");
		
		//Thread der die Temperatur in den Eingestellten Zeitabständen in die Datenbank schreibt
		database dat_write = new database();
		Thread dat_write_t = new Thread(dat_write);
		dat_write_t.start();
		System.out.println("Datenbankthread gestartet!");
		
		//Starten des Threads für die Bewegungserkennung
		sensoren mov = new sensoren(2);
		Thread mov1 = new Thread(mov);
		mov1.start();
		System.out.println("PIR Thread gestartet!");
		
		//Thread der die Konfigurationsdatei alle 10 Sekunden neu einliest
		Config renew = new Config();
		Thread renewconf = new Thread(renew);
		renewconf.start();
		
		//Thread des Android Servers starten
		AndroidServer app = new AndroidServer();
		Thread app1 = new Thread(app);
		app1.start();
		System.out.println("Android Server gestartet!");
	}

}
