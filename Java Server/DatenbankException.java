
public class DatenbankException extends Exception{
	public DatenbankException(String a){
		super("Verbindungsfehler mit Datenbank bei " + a);
	}
}
