import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/* Klasse: sensoren
 * 
 * Beschreibung: Beinhaltet Methoden, die die Kommunikation mit den Sensoren ermöglichen (durch Aufruf von Perl Skripten).
 * 				 Von dieser Klasse können die Threads gestartet werden, die mit Sensorwerten arbeiten.
 * 
 * Methoden: getTemp(), switchControl(), getMovement(), currentTime(), generateImageName()
 * 
 * Author: Projektgruppe Pi 2015
 */

public class sensoren implements Runnable {
	
	// Statische Variablen mit aktueller Temperatur und Luftfeuchtigkeit
	static double temp;
	static double hum;
	
	// Status der Trigger Funksteckdose
	static int power_stat = 0;
	
	// Wird auf 1 gesetzt sobald der Wert des Temperatursensors ausgelesen wurde
	static int temp_ok = 0;
	
	int sensor;
	
	// Datei mit aktuellen Temperatur und Luftfeuchtigkeitswerten erzeugen
	File datei = new File(Config.currentDir + "temp");
	
	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
	
	// Konstruktor (Werte für "sensor" siehe run Methode)
	public sensoren(int sensor){
		this.sensor = sensor;
	}
	
	
	public void run(){
		
		// Wenn bei Objekterzeugung Sensor 1 (Temperatur/Luftfeuchtigkeit) gewählt wurde
		// Holt jede Sekunde die neuen Werte des Temperatursensors
		//------------------------------------------------------------------------------
		
		if (sensor == 1){
			while(true){
				try {
					
					String a = this.getTemp();
					String[] parts = a.split(" ");
					
					if(!(a.equals("1 ")) && !(a.equals("error")) && !(Double.parseDouble(parts[0]) > 100) && !(Double.parseDouble(parts[0]) < 0)){
						
						BufferedWriter out = new BufferedWriter(new FileWriter(datei));
						out.write(a);
						out.close();
						
						hum = Double.parseDouble(parts[0]);
						temp = Double.parseDouble(parts[1]);
						
						temp_ok = 1;
						
					}else{
						System.err.println(sensoren.currentTime() + " Lesefehler DHT22 Sensor!");
					}
					Thread.sleep(1000);
					
				} catch (IOException e) {
					System.err.println(sensoren.currentTime() + ": Error: run-Methode, IO-Exception " + e.getMessage());
				} catch (InterruptedException ie) {
					System.err.println(sensoren.currentTime() +  ie.getMessage());
				} catch (ArrayIndexOutOfBoundsException ae){
					System.err.println(sensoren.currentTime() + ": Error: run-Methode, Kein gültiger Sensor Wert " + ae.getMessage());
				}catch (Exception ex){
					System.err.println(sensoren.currentTime() +  ex.getMessage());
				}
			}
		}
		
		// Wenn bei Objekterzeugung Sensor 2 (Bewegungserkennung) gewählt wurde
		// Schreibt bei Bewegungserkennung die Zeit in die Datenbank
		//------------------------------------------------------------------------------
		
		if (sensor == 2){
			
			while(true){
				
				// Prozess still legen, wenn der Bewegungssensor in der Konfigurationsdatei ausgeschaltet ist
				while(Config.pir_active == 0){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						System.err.println(sensoren.currentTime()  + " " +  e.getMessage());
					}
				}
				
				// Methode für Bewegungssensor aufrufen
				int a = getMovement();
				
				// Bei erkannter Bewegung Daten in die Datenbank schreiben
				if (a == 1 && Config.pir_active == 1){
					System.out.println(sensoren.currentTime() +  " Bewegung erkannt!");
					try {
						database.insertMovement(generateImageName());
					} catch (DatenbankException e) {
						System.err.println(sensoren.currentTime() + " " + e.getMessage());
					}
				}
				
				try {
					Thread.sleep(1000 * 60 * Config.pir_wait);
				} catch (InterruptedException e) {
					System.err.println(sensoren.currentTime() + " " + e.getMessage());
				}
			}
		}
		
		// Wenn bei Objekterzeugung Sensor 3 (Trigger Temperatur) gewählt wurde
		// Schaltet die eingestelle Funksteckdose bei Unter- oder Überschreiten einer eingestellten Temperaturschwelle
		//------------------------------------------------------------------------------
		
		if (sensor == 3){
			
			// Schlafen legen wenn in config deaktiviert
			while(Config.trigger == 0){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					System.err.println(sensoren.currentTime() + " " + e.getMessage());
				}
			}
			
			// Steckdose initial ausschalten, da Zustand zu begin des Programms nicht bekannt
			sensoren.switchControl(Config.power_switch, 0);
			power_stat = 0;
			
			// Warten bis der erste Wert vom Temperatursenosr gelesen wurde
			while(temp_ok == 0){
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					System.err.println(sensoren.currentTime() + " " + e.getMessage());
				}
			}
			
			while(true){
				// Bei Unterschreiten einschalten
				if (temp < Config.min_temp && Config.trigger == 1){
					if(power_stat == 0){
						sensoren.switchControl(Config.power_switch, 1);
						System.out.println(sensoren.currentTime() + " Minimale Temperatur unterschritten!");
						power_stat = 1;
					}
					
				}
				// Bei überschreiten ausschalten
				if (temp > Config.max_temp && Config.trigger == 1){
					if(power_stat == 1){
						sensoren.switchControl(Config.power_switch, 0);
						System.out.println(sensoren.currentTime() + " Maximale Temperatur erreicht!");
						power_stat = 0;
					}
					
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					System.err.println(sensoren.currentTime()  + " " +  e.getMessage());
				}
			}
			
		}
	}
	
	/*-----------------------------------------------------------------------------------------------
	 * ----------------------------Temperatur/Luftfeuchtigkeit---------------------------------------
	 ----------------------------------------------------------------------------------------------*/
	 
	// Ruft das Perl Skript auf und gibt dessen Ausgabe zurück
	public String getTemp(){
		String line = null;
		try {
			
			Process p = Runtime.getRuntime().exec(Config.currentDir + "../PerlGPIO/sensoren.pl 1");
			BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			line = bri.readLine();
			
			bri.close();
			
		} catch (IOException e) {
			System.err.println(sensoren.currentTime() + ": Methode getTemp(); " + e.getMessage());
			return "error";
		}
		return line;
	}
	 
	/*-----------------------------------------------------------------------------------------------
	 * ----------------------------Steckdosen Switch-------------------------------------------------
	 ----------------------------------------------------------------------------------------------*/
	
	// Ruft das Perlskript zum schalten der Steckdosen auf und schreibt den Wert in die Datenbank
	// Übergabeparameter [Schaltcode][Status(1/0)]
	public synchronized static void switchControl(int rcswitch, int state){
		try {
			
			//Runtime.getRuntime().exec(Config.currentDir + "../PerlGPIO/module/driver/send 10110 " + rcswitch + " " + state);
			Runtime.getRuntime().exec(Config.currentDir + "../PerlGPIO/sensoren.pl 3 " + rcswitch + " " + state);
			int state_db = 0;
			try {
				// den Status aus der Datenbank abfragen
				state_db = database.rcSwitchGetState(rcswitch);
			} catch (DatenbankException e1) {
				System.err.println(sensoren.currentTime()  + " " +  e1.getMessage());
			}
			
			// Wenn der Status an war und jetzt auf aus geschalten wurde, Werte in die Datenbank schreiben
			if(state_db == 1 && state == 0){
				try {
					database.insertRcSwitchTime(rcswitch);
				} catch (DatenbankException e) {
					System.err.println(sensoren.currentTime() + " " + e.getMessage());
				}
			}
			
			try {
				// Den Status in der Datenbank auf neuen Wert setzen
				database.rcSwitchSetState(rcswitch, state);
			} catch (DatenbankException e) {
				System.err.println(sensoren.currentTime()  + " " +  e.getMessage());
			}
			
		} catch (IOException e) {
			System.err.println(sensoren.currentTime() + " " + e.getMessage());
		}
	}
	 
	/*-----------------------------------------------------------------------------------------------
	 * ----------------------------Bewegungserkennung------------------------------------------------
	 ----------------------------------------------------------------------------------------------*/
	
	// Ruft das Perlskript zur Bewegungserkennung auf und wartet bis eine Bewegung erkant wurde
	// Rückgabewerte: 1 bei Bewegungserkennung, 0 bei Fehler
	public int getMovement(){
		try {
			
			String line;
		
			Process p = Runtime.getRuntime().exec(Config.currentDir + "../PerlGPIO/sensoren.pl 2");
			BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
			line = bri.readLine();
			
			if(line.startsWith("Bewegung")){	
				return 1;
			}
			
		} catch (IOException e) {
			System.err.println(sensoren.currentTime() + " " + e.getMessage());
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				System.err.println(sensoren.currentTime() + " " + e.getMessage());
			}
		}

		return 0;
	}
	
	
	// Methode, welche die aktuelle Zeit und Datum zurückliefert
	public static String currentTime (){
		Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
	    return sdf.format(cal.getTime());
	}
	
	// Methode die den Namen für die Bilddatei zurückliefert
	public String generateImageName (){
		String date_time = currentTime();
		String[] parts = date_time.split(" ");
		
		String[] parts_time = parts[1].split(":");
		
		String name = parts[0] + "_" + parts_time[0] + "-" + parts_time[1] + "-" + parts_time[2] + ".jpeg";
		
		return name;
	}
}
