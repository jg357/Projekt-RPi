import java.io.*;

/* Klasse: Config
 * 
 * Beschreibung: Beinhaltet Methoden und Variablen die für die Konfigration des Servers zuständig sind.
 * 				 Variablen sind mit Standartwerten vorbelegt. Werte werden aber mit denen aus der externen config.ini überschrieben (wenn vorhanden).
 * 				 Die Konfiguration wird außerdem alle 10 Sekunden neu eingelesen.
 * 
 * Methoden: confRead(), setPirState(), setTriggerState()
 * 
 * Author: Projektgruppe Pi 2015
 */

public class Config implements Runnable {
	
	// Die statischen Variablen für die Config.ini direktiven erzeugen und mit Standartwerten vorbelegen
	
	// Datenbankzugriff
	static String database = "127.0.0.1";
	static String port = "3306";
	static String db_name = "projekt_pi";
	static String user = "root";
	static String pass = "";
	static int db_write = 15;

	// Bewegungssensor
	static int pir_active = 0;
	static int pir_wait = 1;

	// Trigger Variablen
	static int trigger = 0;
	static int power_switch = 1;
	static double max_temp = 25.0;
	static double min_temp = 10.0;
	
	// Allgemein
	static int room = 1;
	static String currentDir = ClassLoader.getSystemClassLoader().getResource(".").getPath();
	
	// Wenn Thread gestartet, werden die Werte der config.ini alle 10 Sekunden neu eingelesen
	public void run() {
		while(true){
			try {
			Thread.sleep(10000);
			} catch (InterruptedException e) {
				System.err.println(sensoren.currentTime() + e.getMessage());
			}
			Config.confRead();
		}
		
	}
	
	// Diese Funktion liest die Direktiven aus der "config.ini" Datei aus und schreibt die Werte in die Variablen
	public static void confRead(){
		String z;
		String[] parts;
		
		File datei = new File(currentDir + "config.ini");
		try {
			BufferedReader in = new BufferedReader(new FileReader(datei));
			
			while((z=in.readLine()) != null){
				if(!(z.equals(""))){
					
					if(z.startsWith("database")){
						parts = z.split(" ");
						database = parts[1];
					}
					if(z.startsWith("port")){
						parts = z.split(" ");
						port = parts[1];
					}
					if(z.startsWith("db_name")){
						parts = z.split(" ");
						db_name = parts[1];
					}
					if(z.startsWith("user")){
						parts = z.split(" ");
						user = parts[1];
					}
					if(z.startsWith("pass")){
						parts = z.split(" ");
						pass = parts[1];
					}
					if(z.startsWith("room")){
						parts = z.split(" ");
						room = Integer.parseInt(parts[1]);
					}
					if(z.startsWith("db_write")){
						parts = z.split(" ");
						db_write = Integer.parseInt(parts[1]);
					}
					if(z.startsWith("pir_active")){
						parts = z.split(" ");
						pir_active = Integer.parseInt(parts[1]);
					}
					if(z.startsWith("pir_wait")){
						parts = z.split(" ");
						pir_wait = Integer.parseInt(parts[1]);
					}
					if(z.startsWith("trigger")){
						parts = z.split(" ");
						trigger = Integer.parseInt(parts[1]);
					}
					if(z.startsWith("min_temp")){
						parts = z.split(" ");
						min_temp = Double.parseDouble(parts[1]);
					}
					if(z.startsWith("max_temp")){
						parts = z.split(" ");
						max_temp = Double.parseDouble(parts[1]);
					}
					if(z.startsWith("power_switch")){
						parts = z.split(" ");
						power_switch = Integer.parseInt(parts[1]);
					}
				}
			}
			in.close();
		} catch (IOException e) {
			System.err.println(sensoren.currentTime() + "Config.ini nicht gefunden!" + e.getMessage());
		}
	}

	// Mit dieser Methode kann der Bewegungssensor an und aus geschaltet werden, Übergabeparameter 0 oder 1 (für Android App)
	public static void setPirState(int state){
		
		File config = new File(currentDir + "config.ini");
        BufferedReader reader = null;
        BufferedWriter writer = null;
        StringBuffer lesepuffer = null;
        
        int change = 0;
        
	    // Lesen
        lesepuffer = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(config));
 
            String zeile = null;
            while((zeile = reader.readLine()) != null) {
            	
            	// Ändern + Zeile Puffern wenn "pir_active" vorhanden
                if(zeile.contains("pir_active")) {
                    if(zeile.contains("pir_active 1") && state == 0) {
                        lesepuffer.append(zeile.replace("1", "0") + System.getProperty("line.separator")); 
                        change = 1;
                    }
                    
                    if(zeile.contains("pir_active 0") && state == 1) {
                        lesepuffer.append(zeile.replace("0", "1") + System.getProperty("line.separator"));
                        change = 1;
                    }
                }else{
                	// Zeile Puffern wenn "Wert" nicht vorhanden
                    lesepuffer.append(zeile + System.getProperty("line.separator"));
                }
            }
        } catch(Exception e) {
            System.out.println("Datei nicht gefunden");
        }
	 
	    // Lesepuffer in Datei schreiben wenn change = 1, also eine Zustandsänderung stattgefunden hat
        if(change == 1){
        	try {
            writer = new BufferedWriter(new FileWriter(config));
     
            writer.write(lesepuffer.toString());
            writer.flush();
	        } catch(IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                if(writer != null) writer.close();
	            } catch(IOException e) {
	            }
	        }
        	Config.pir_active = state;
        }  
	}
	
	// Mit dieser Methode kann der Trigger an und aus geschaltet werden, Übergabeparameter 0 oder 1 (für Android App)
	public static void setTriggerState(int state){
		
		File config = new File(currentDir + "config.ini");
        BufferedReader reader = null;
        BufferedWriter writer = null;
        StringBuffer lesepuffer = null;
        
        int change = 0;
        
	    // Lesen
        lesepuffer = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(config));
 
            String zeile = null;
            while((zeile = reader.readLine()) != null) {
            	
            	// Ändern + Zeile Puffern wenn "trigger" vorhanden
                if(zeile.contains("trigger")) {
                    if(zeile.contains("trigger 1") && state == 0) {
                        lesepuffer.append(zeile.replace("1", "0") + System.getProperty("line.separator")); 
                        change = 1;
                    }
                    
                    if(zeile.contains("trigger 0") && state == 1) {
                        lesepuffer.append(zeile.replace("0", "1") + System.getProperty("line.separator"));
                        change = 1;
                    }
                }else{
                	// Zeile Puffern wenn "Wert" nicht vorhanden
                    lesepuffer.append(zeile + System.getProperty("line.separator"));
                }
            }
        } catch(Exception e) {
            System.out.println("Datei nicht gefunden");
        }
	 
	    // Lesepuffer in Datei schreiben wenn change = 1, also eine Zustandsänderung stattgefunden hat
        if(change == 1){
        	try {
            writer = new BufferedWriter(new FileWriter(config));
     
            writer.write(lesepuffer.toString());
            writer.flush();
	        } catch(IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                if(writer != null) writer.close();
	            } catch(IOException e) {
	            }
	        }
        	Config.trigger = state;
        }  
	}
}
