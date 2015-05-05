import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/* Klasse: database
 * 
 * Beschreibung: Beinhaltet Methoden für die Kommunikation mit der Datenbank (durch Perlskripte).
 * 				 Alle Methoden dieser Klasse werfen Exceptions welche auftreten, wenn ein Fehler vom Perlskript zurückgeliefert wird.
 * 				 (DatenbankInsertException, DatenbankSelectException)
 * 
 * Methoden: insertTemp(), insertMovement(), insertRcSwitchTime(), rcSwitchSetState()
 * 			 rcSwitchGetState(), getPictureNames(), getSwitchTime(), getDayValues(), getAvgValues()
 * 
 * Author: Projektgruppe Pi 2015
 */

public class database implements Runnable {
	
	// Thread für den regelmäßigen Datenbankinsert der Temperatur und Luftfeuchtigkeitsdaten
	public void run(){
		while(true){
			
			if(sensoren.temp_ok == 1){
				try {
					insertTemp();
				} catch (DatenbankException e) {
					System.err.println(sensoren.currentTime()  + " " +  e.getMessage());
				}
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.err.println(sensoren.currentTime()  + " " +  e.getMessage());
			}	
		}
	}
	 
	/*-----------------------------------------------------------------------------------------------
	 * ----------------------------Methoden für Datenbankinsert--------------------------------------
	 ----------------------------------------------------------------------------------------------*/
	
	// Schreibt die aktuelle Temperatur und Luftfeuchtigkeit aus den Variablen in der Klasse sensor in die Datenbank
	public void insertTemp() throws DatenbankException{
		String error = null;
		
		try {
			String execute = new String(Config.currentDir + "../PerlGPIO/dbinsert.pl " + Config.database + " " + Config.port + " " + Config.db_name + " " + Config.user + " " + Config.pass + " " + sensoren.hum  + " " + sensoren.temp + " " + Config.room);
			
			Process p = Runtime.getRuntime().exec(execute);
			BufferedReader bri = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			
			error = bri.readLine();
			
			if (error != null){
				System.out.println("error!!");
				throw new DatenbankInsertException();
			}
			
			bri.close();
			
			Thread.sleep(1000 * 60 * Config.db_write);
			
		} catch (IOException e) {
			System.err.println(sensoren.currentTime()  + " " +  e.getMessage());
		} catch (InterruptedException e) {
			System.err.println(sensoren.currentTime()  + " " +  e.getMessage());
		}
	}
	
	// Fügt bei Bewegungserkennung den Bildnamen in die Datenbank ein
	public static void insertMovement(String image_name) throws DatenbankException{
		String error = null;
		
		try {
			String execute = new String(Config.currentDir + "../PerlGPIO/dbinsert.pl " + Config.database + " " + Config.port + " " + Config.db_name + " " + Config.user + " " + Config.pass + " " +   image_name + " " + Config.room);
			Process p = Runtime.getRuntime().exec(execute);
			BufferedReader bri = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			
			error = bri.readLine();
			
			if (error != null){
				throw new DatenbankInsertException();
			}
			
			
			bri.close();

		} catch (IOException e) {
			System.err.println(sensoren.currentTime()  + " " +  e.getMessage());
		}
	}
	
	// Fügt die Schaltzeit der Funksteckdosen in die Datenbank ein
	public static void insertRcSwitchTime(int rcswitch) throws DatenbankException{
		String error = null;
		
		try {
			String execute = new String(Config.currentDir + "../PerlGPIO/dbinsert.pl " + Config.database + " " + Config.port + " " + Config.db_name + " " + Config.user + " " + Config.pass + " " + rcswitch + " " + Config.room + " " + 1 + " " + 1);
			Process p = Runtime.getRuntime().exec(execute);
			BufferedReader bri = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			
			error = bri.readLine();
			
			if (error != null){
				throw new DatenbankInsertException();
			}
			
			bri.close();

		} catch (IOException e) {
			System.err.println(sensoren.currentTime()  + " " +  e.getMessage());
		}
	}
	
	/*-----------------------------------------------------------------------------------------------
	 * ----------------------------Status der Switches erkennen--------------------------------------
	 ----------------------------------------------------------------------------------------------*/
	
	// Den Status des jewiligen Switches aus der Datenbank abfragen
	public static int rcSwitchGetState(int rc) throws DatenbankException{	
		int state = 2;
		String error = null;
		try{
			String execute = new String(Config.currentDir + "../PerlGPIO/rcstate.pl " + Config.database + " " + Config.port + " " + Config.db_name + " " + Config.user + " " + Config.pass  + " " + rc + " " +  2);
			Process p = Runtime.getRuntime().exec(execute);
			
			BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			
			error = bre.readLine();
			String a = bri.readLine();
			
			bri.close();
			bre.close();
			if(error != null){
				throw new DatenbankSelectException();
			}
			
			state = Integer.parseInt(a);
		} catch (IOException e) {
			System.err.println(sensoren.currentTime()  + " " +  e.getMessage());
		} catch (NumberFormatException ne){
			System.err.println(sensoren.currentTime()  + " Fehler beim Datenbankzugriff! " +  ne.getMessage());
		}
		return state;
	}
	
	// Den Status des Switches in der Datenbank ändern
	public static void rcSwitchSetState(int rc, int state) throws DatenbankException{
		String error = null;
		try{
			String execute = new String(Config.currentDir + "../PerlGPIO/rcstate.pl " + Config.database + " " + Config.port + " " + Config.db_name + " " + Config.user + " " + Config.pass  + " " + rc + " " + 1 + " " +  state);
			Process p = Runtime.getRuntime().exec(execute);
			BufferedReader bri = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			
			error = bri.readLine();
			
			if (error != null){
				throw new DatenbankInsertException();
			}
			
		} catch (IOException e) {
			System.err.println(sensoren.currentTime()  + " " +  e.getMessage());
		}
	}
	
	/*-----------------------------------------------------------------------------------------------
	 * ----------------------------Daten fuer Android Server----------------------------------------
	 ----------------------------------------------------------------------------------------------*/
	
	// Liefert einen String mit allen Bildernamen aud der Datenbank zurück (für Android App)
	public static String getPictureNames() throws DatenbankException{
		String line = null;
		String error = null;
		try{
			String execute = new String(Config.currentDir + "../PerlGPIO/dbselect.pl " + Config.database + " " + Config.port + " " + Config.db_name + " " + Config.user + " " + Config.pass  + " " + " " + 1);
			Process p = Runtime.getRuntime().exec(execute);
			BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			
			error = bre.readLine();
			line = bri.readLine();
			
			bri.close();
			
			if(error != null){
				throw new DatenbankSelectException();
			}
			
			bre.close();
			
		} catch (IOException e) {
			System.err.println(sensoren.currentTime()  + " " +  e.getMessage());
		}
		return line;
	}
	
	// Liefert die Einschaltzeit einer Steckdose der letzten 7 Tage zurück
	public static String getSwitchTime(int rc) throws DatenbankException{
		String line = null;
		String error = null;
		try{
			String execute = new String(Config.currentDir + "../PerlGPIO/dbselect.pl " + Config.database + " " + Config.port + " " + Config.db_name + " " + Config.user + " " + Config.pass  + " " + " " + 2 + " " + rc);
			Process p = Runtime.getRuntime().exec(execute);
			BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			
			error = bre.readLine();
			line = bri.readLine();
			
			bri.close();
			
			if(error != null){
				throw new DatenbankSelectException();
			}
			
			bre.close();
			
		} catch (IOException e) {
			System.err.println(sensoren.currentTime()  + " " +  e.getMessage());
		}
		return line;
	}
	
	// Liefert einen String mit 24 gerundeten durschnitwerten zurück, für 1, 3 und 7 Tagen (für Android App)
	public static String getDayValues(String temp_hum, int tage) throws DatenbankException{
		String line = null;
		String error = null;
		String line_int = null;
		try{
			String execute = null; 
			
			// Je nach anzahl der Tage das Perl Skript mit anderen Übergabeparameter aufrufen
			if(tage == 1){
				if(temp_hum.equals("temp")){
					execute = new String(Config.currentDir + "../PerlGPIO/dbselect.pl " + Config.database + " " + Config.port + " " + Config.db_name + " " + Config.user + " " + Config.pass  + " " + 3 + " " + 1);
				}else{
					execute = new String(Config.currentDir + "../PerlGPIO/dbselect.pl " + Config.database + " " + Config.port + " " + Config.db_name + " " + Config.user + " " + Config.pass  + " " + 3 + " " + 2);
				}
			}
			if(tage == 3){
				if(temp_hum.equals("temp")){
					execute = new String(Config.currentDir + "../PerlGPIO/dbselect.pl " + Config.database + " " + Config.port + " " + Config.db_name + " " + Config.user + " " + Config.pass  + " " + 4 + " " + 1);
				}else{
					execute = new String(Config.currentDir + "../PerlGPIO/dbselect.pl " + Config.database + " " + Config.port + " " + Config.db_name + " " + Config.user + " " + Config.pass  + " " + 4 + " " + 2);
				}
			}
			if(tage == 7){
				if(temp_hum.equals("temp")){
					execute = new String(Config.currentDir + "../PerlGPIO/dbselect.pl " + Config.database + " " + Config.port + " " + Config.db_name + " " + Config.user + " " + Config.pass  + " " + 6 + " " + 1);
				}else{
					execute = new String(Config.currentDir + "../PerlGPIO/dbselect.pl " + Config.database + " " + Config.port + " " + Config.db_name + " " + Config.user + " " + Config.pass  + " " + 6 + " " + 2);
				}
			}
			
			Process p = Runtime.getRuntime().exec(execute);
			BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			
			error = bre.readLine();
			line = bri.readLine();
			
			bri.close();
			bre.close();
			if(error != null){
				throw new DatenbankSelectException();
			}
			
			String[] parts = line.split(" ");
			
			// Die Werte in Double wandeln , runden und wieder an einen String hängen
			for(int a = 0; a < parts.length; a++){
				double d = Double.parseDouble(parts[a]);
				Integer i = (int) Math.round(d);
				
				if (line_int == null){
					line_int = i.toString();
				}else{
					line_int = line_int + " " + i.toString(); 
				}
			}
			
			
		} catch (IOException e) {
			System.err.println(sensoren.currentTime()  + " " +  e.getMessage());
		}
		return line_int;
	}
	
	// Liefert die Durschnitts-, Maximal- und Minimalwerte des Vortages zurück
	public static String getAvgValues() throws DatenbankException{
		String line = null;
		String error = null;
		try{
			String execute = new String(Config.currentDir + "../PerlGPIO/dbselect.pl " + Config.database + " " + Config.port + " " + Config.db_name + " " + Config.user + " " + Config.pass  + " " + " " + 5);
			Process p = Runtime.getRuntime().exec(execute);
			BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			
			error = bre.readLine();
			line = bri.readLine();
			
			bri.close();
			
			if(error != null){
				throw new DatenbankSelectException();
			}
			
			bre.close();
			
		} catch (IOException e) {
			System.err.println(sensoren.currentTime()  + " " +  e.getMessage());
		}
		return line;
	}
}
