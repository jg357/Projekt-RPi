package de.raspberrypiapp.technikerschuleerlangen;

import android.app.Activity;
import android.os.Bundle;

//Activity fuer das Analoge Instument
public class ThermometerActivity extends Activity {

	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thermometer);
    }
}