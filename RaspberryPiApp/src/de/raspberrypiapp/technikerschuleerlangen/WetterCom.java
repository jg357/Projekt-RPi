package de.raspberrypiapp.technikerschuleerlangen;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public class WetterCom {
	public static String ausgabe=""; // Statischer String fuer die Wetterausgabe
	
	public WetterCom(){ // Konstruktor: bei Aufruf lade die Html Website von Wetter.com und speichere Sie in einem String
			ausgabe = this.getURLContent("http://api.wetter.com/forecast/weather/city/DE0002318/project/raspberrypitechnikerschuleerlangen/cs/69ecce81f0f776744ae968da9283e5ca");
		
	}
	
	
	public String getURLContent(String p_sURL)
	{
	    URL oURL;
	    URLConnection oConnection;
	    BufferedReader oReader;
	    String sLine;
	    StringBuilder sbResponse;
	    String sResponse = null;

	    try
	    {
	        oURL = new URL(p_sURL);
	        oConnection = oURL.openConnection();
	        oReader = new BufferedReader(new InputStreamReader(oConnection.getInputStream()));
	        sbResponse = new StringBuilder();

	        while((sLine = oReader.readLine()) != null)
	        {
	            sbResponse.append(sLine);
	        }

	        sResponse = sbResponse.toString();
	    }
	    catch(Exception e)
	    {
	        e.printStackTrace();
	    }

	    return sResponse;
	}



}
