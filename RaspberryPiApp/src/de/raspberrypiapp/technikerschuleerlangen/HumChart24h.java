package de.raspberrypiapp.technikerschuleerlangen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import de.raspberrypiapp.technikerschuleerlangen.SimpleGestureFilter.SimpleGestureListener;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//Activity fuer die 24 Stunden Luftfeuchtigkeit Chart Activity
public class HumChart24h extends Activity implements SimpleGestureListener{
	public static final int PORT = 1234;
	Socket socket;
	BufferedReader in;
	PrintWriter out;
	String line;
	String[] werte = new String[2];
	boolean i= true;
	String text;
	TextView satz;
	ClientThread clientThread;
	Thread thread;
	private SimpleGestureFilter detector;
	String[] humChart = new String[25];
	int max;
	int min;
	
	// First Create a Graphical View object called mChart.
	private GraphicalView mChart;


	private String[] mMonth = new String[] {
	         "", "" , "03", "", "", "06",

	         "", "" ,"09","","","12" ,"", "" , "15", "", "", "18",

	         "", "" ,"21","","","24"};

	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.chart_layout);
	        OpenChart();
	    }
	 
	 public void onResume() {
		    super.onResume(); 
		    Log.d("MyApp", "Resume!");	      
	 }
	 
	   
	 private void OpenChart()
	    {
		  detector = new SimpleGestureFilter(this,this);
			i=true;
			clientThread = new ClientThread();
			thread = new Thread(clientThread);
			thread.start();	
		 
		 // Define the number of elements you want in the chart.
	     int z[]={0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23};
	     int x[]=new int[25];
	     long endZeitpunkt = System.currentTimeMillis()+10000; // Legt 10 Sekunden als Deathtime fest
	     													   // wenn diese erreicht werden so wird die
															   // Endlosschleife beendet!
	     humChart[0]="NULL";				// Belegt das erste Element mit dem String "NULL" um
											//auf diesen zu pruefen, wenn dieser nicht mehr verhanden ist
											//so bedeutet dies das Werte von Raspberry Pi uebertragen wurden!
	     while(humChart[0].equals("NULL")){ 
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
	     // Ab hier werden die uebertragenen Werte von der DB auf dem Pi umgewandelt in eine Int und geschrieben in
	     // das x[] Array und gleichzeitig der kleinste und groeßte Wert herausgefunden um das Chart
	     // passend zu formatieren.
	     max=Integer.parseInt(humChart[1]); // Wandelt den String vom Array element 2 in eine Int um
	     min=Integer.parseInt(humChart[1]); // um diesen als Ausgangswert für einen Vergleich zu speichern, der in der 
	     									// Zeile 151 benutzt wird um die Kurve mittig vom Anzeigebereich zu platzieren
	     for(int i=1;i<=24;i++){
	    	x[i-1] =Integer.parseInt(humChart[i]);
	    	if (max < x[i-1])
	    		max=x[i-1];
	    	if (min > x[i-1])
	    		min=x[i-1];
	     }
	     

	      // Create XY Series for X Series.
	     XYSeries xSeries=new XYSeries("Luftfeuchtigkeit"); // Legt den Namen für die Kurve fest.
	     

	     //  Adding data to the X Series.
	     for(int i=0;i<z.length;i++)
	     {
	      xSeries.add(z[i],x[i]);
	   
	     }

	        // Create a Dataset to hold the XSeries.
	     
	     XYMultipleSeriesDataset dataset=new XYMultipleSeriesDataset();
	     
	      // Add X series to the Dataset.  
	     dataset.addSeries(xSeries);
	     
	     
	      // Create XYSeriesRenderer to customize XSeries

	     XYSeriesRenderer Xrenderer=new XYSeriesRenderer();
	     Xrenderer.setColor(Color.BLUE);
	     //Xrenderer.setPointStyle(PointStyle.DIAMOND);
	     Xrenderer.setDisplayChartValues(true);
	     Xrenderer.setLineWidth(2);
	     Xrenderer.setFillPoints(true);
	     
	     // Create XYMultipleSeriesRenderer to customize the whole chart

	     XYMultipleSeriesRenderer mRenderer=new XYMultipleSeriesRenderer();
	     
	     mRenderer.setChartTitle("Luftfeuchtigkeit 24h");
	     mRenderer.setXTitle("Stunden");
	     mRenderer.setYTitle("Hygro. % Rel.");
	     mRenderer.setZoomButtonsVisible(true);
	     mRenderer.setXLabels(0);
	     mRenderer.setPanEnabled(true, true);
	     mRenderer.setPanLimits(new double[] { 0, 23, min-5, max+5});
	     mRenderer.setShowGrid(true);
	     mRenderer.setGridColor(Color.BLACK);
	     mRenderer.setApplyBackgroundColor(true);
	     mRenderer.setBackgroundColor(Color.WHITE);
	     mRenderer.setYAxisMin(min-10);
	     mRenderer.setYAxisMax(max+10);
	     mRenderer.setClickEnabled(true);
	     mRenderer.setYLabelsColor(0, Color.RED);
	     mRenderer.setLabelsTextSize(15);
	     mRenderer.setAxisTitleTextSize(15);
	     mRenderer.setXLabelsColor(Color.RED);
	     
	     for(int i=0;i<z.length;i++)
	     {
	      mRenderer.addXTextLabel(i, mMonth[i]);
	     }
	     
	       // Adding the XSeriesRenderer to the MultipleRenderer. 
	     mRenderer.addSeriesRenderer(Xrenderer);
	 
	     
	     LinearLayout chart_container=(LinearLayout)findViewById(R.id.Chart_layout);

	   // Creating an intent to plot line chart using dataset and multipleRenderer
	     
	     mChart=(GraphicalView)ChartFactory.getLineChartView(getBaseContext(), dataset, mRenderer);
	     
	     //  Adding click event to the Line Chart.

	     mChart.setOnClickListener(new View.OnClickListener() {
	   
	   @Override
	   public void onClick(View arg0) {
	    // TODO Auto-generated method stub
	    
	    SeriesSelection series_selection=mChart.getCurrentSeriesAndPoint();
	    
	    if(series_selection!=null)
	    {
	     int series_index=series_selection.getSeriesIndex();
	     
	     String select_series="X Series";
	     if(series_index==0)
	     {
	      select_series="X Series";
	     }else
	     {
	      select_series="Y Series";
	     }
	     
	     String month=mMonth[(int)series_selection.getXValue()];
	     
	     int amount=(int)series_selection.getValue();
	     
	     Toast.makeText(getBaseContext(), select_series+"in" + month+":"+amount, Toast.LENGTH_LONG).show();
	    }
	   }
	  });
	     
	// Add the graphical view mChart object into the Linear layout .
	     chart_container.addView(mChart);
	     
	     
	    }
	 	// Methode fuer den Swipe und Doppelklick Listner
	    public boolean dispatchTouchEvent(MotionEvent me){
	        // Call onTouchEvent of SimpleGestureFilter class
	         this.detector.onTouchEvent(me);
	       return super.dispatchTouchEvent(me);
	    }
	    // Methode fuer den Swipe
	     public void onSwipe(int direction) {
	      
	      switch (direction) {
	      
	      case SimpleGestureFilter.SWIPE_RIGHT : //Wenn nach rechts gewischt wird
	    	  startActivity(new Intent(this,TempChart3d.class));
	    	  											 break;
	      case SimpleGestureFilter.SWIPE_LEFT :  //Wenn nach links gewischt wird
	    
	    	  startActivity(new Intent(this,TempChart24h.class));
	    	  
	    	  											break;
	      }
	      }
	      
	    //Methode fuer den Doppelklick
	     public void onDoubleTap() { // Bei doppelKlick
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
					out.println("HumChart!"); // Gibt den Pi den Befehl die Chart Werte zu uebertragen!
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
						    if(line.contains("Humchart:")){	// Wenn die Nachricht HumChart vom Pi kommt so
						    	Log.d("MyApp", line);		
						    	humChart=line.split(" ");	// wird der String vom gesplittet und in das Array geschrieben
							    out.println("BYE!");		// Gibt den Pi den Hinweis das es die Verbindung beenden kann.
							    out.flush();
						    }											
						}

					}
				} catch (IOException e) {
					
				}
			}
		}

	}

