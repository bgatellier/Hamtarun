package hamtarun.jogging;

import java.util.Date;

import hamtarun.jogging.R;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


/**
 * @author Gatellier Bastien, Pouchepanadin Christian
 * Define the behaviors of the - Run - screen
 * The user can start, pause, resume and stop the timer.
 * After he/she starts the timer, some informations on the run are displayed, like the duration, distance, instant speed and calories lost 
 * When he/she stops the timer, the run is automatically stored in DB (see the HListofrunActivity.java for more details).
 */
public class HRunActivity extends Activity implements View.OnClickListener {
	/*	DB params	*/
	private	SharedPreferences settings;
	private	SharedPreferences settingsWeight;
	private static final String PREFS_NAME = "HRunPrefs";
	private static final String RESUME = "resume";
	private static final String CHRONOSTATE = "chronoState";
	private static final String CHRONOVALUE = "chronoValue";
	private static final String CHRONOPAUSEVALUE = "chronoPauseValue";
	private static final String TOTALDISTANCERUN = "totaldistanceRun";
	private static final String SPEED = "speed";
	private static final String CALORIES = "calories";
	private static final String BTNSTART = "btnStart";
	private static final String BTNSTOP = "btnStop";
	private HListofrunDB running_db;
	
	/*	UI params	*/
	private Button buttonStop;
	private Button buttonStart;
	private Button button_menu_listofrun;
	private Button button_menu_settings;
	private TextView textViewChrono;
	private TextView textViewSpeed;
	private TextView textViewDistance;
	private TextView textViewCalories;
	
	/* Intent params */
	IntentFilter intentFilter;
	private Intent intentService;
	
	/* Parameters */
	private boolean resume = false;
	private int chronoState = -1;
		// -1 = Initial state
		//  0 = Execution
		//  1 = Pause
	private int chronoValue = 0;
	private int chronoPauseValue = 0;
	private float totalDistanceRun = 0;
	private float distanceRun = 0;
	private float speed = 0;
	private float poids = 0;
	private float calories = 0;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        chronoState = -1;
        
        /*	Initialize DB	*/
    	running_db = new HListofrunDB(this);
    			
        
        //---intent to filter for file downloaded intent---
        intentFilter = new IntentFilter();
        intentFilter.addAction("TIMER_OUT");
        //---register the receiver---
        registerReceiver(intentReceiver, intentFilter);
        
        // Get Views from the layout
        buttonStart = (Button) findViewById(R.id.button_run);
        buttonStart.setOnClickListener(this);
        buttonStop = (Button) findViewById(R.id.button_stop);
        buttonStop.setOnClickListener(this);
        buttonStop.setClickable(false);
        button_menu_listofrun = (Button) findViewById(R.id.btn_menu_listofrun);
        button_menu_listofrun.setOnClickListener(this);
        button_menu_settings = (Button) findViewById(R.id.btn_menu_settings);
        button_menu_settings.setOnClickListener(this);
        textViewChrono = (TextView) findViewById(R.id.txt_chrono);
        textViewSpeed = (TextView) findViewById(R.id.txt_speed);
        textViewDistance = (TextView) findViewById(R.id.txt_distance);
        textViewCalories = (TextView) findViewById(R.id.txt_calories);
    }
    
	@Override
	protected void onPause() {
		super.onPause();
		
		//Saving data
		SharedPreferences.Editor editor = settings.edit();
		
		editor.putBoolean(RESUME,resume);
		editor.putInt(CHRONOSTATE,chronoState);
		editor.putInt(CHRONOVALUE,chronoValue);
		editor.putInt(CHRONOPAUSEVALUE,chronoPauseValue);
		editor.putFloat(TOTALDISTANCERUN,totalDistanceRun);
		editor.putFloat(SPEED,speed);
		editor.putFloat(CALORIES,calories);
		editor.putString(BTNSTART, buttonStart.getText().toString());
		editor.putString(BTNSTOP, buttonStop.getText().toString());
		
		editor.commit();
	}

	@Override
	protected void onResume() {
		super.onResume();

		settings = getSharedPreferences(PREFS_NAME, 0);
		settingsWeight = getSharedPreferences(HSettingsActivity.getPrefsName(), 0);
		poids = settingsWeight.getFloat(HSettingsActivity.getWeightCName(),0);
		resume = settings.getBoolean(RESUME, false);
		
		//If the chronometer was onPause
		if (resume){
			chronoState = settings.getInt(CHRONOSTATE, -1);
			chronoValue = settings.getInt(CHRONOVALUE, 0);
			chronoPauseValue = settings.getInt(CHRONOPAUSEVALUE, 0);
			totalDistanceRun = settings.getFloat(TOTALDISTANCERUN, 0);
			speed = settings.getFloat(SPEED, 0);
			buttonStart.setText(settings.getString(BTNSTART, "Start"));
			buttonStop.setText(settings.getString(BTNSTOP, "Stop"));
			calories = settings.getFloat(CALORIES, 0);
			
			if(chronoState != -1)
				buttonStop.setClickable(true);

			// Affichage
			long heures = chronoValue/3600;
			long minutes = chronoValue/60%60;
			long seconds = chronoValue%60;
			String string = "";
			
			if (heures < 10)
				string = "0";
			string = string + heures;
			if (minutes < 10)
				string = string + ":0" + minutes;
			else
				string = string + ":" + minutes;
			if (seconds < 10)
				string = string + ":0" + seconds;
			else
				string = string + ":" + seconds;
			
			textViewChrono.setText(string);
			textViewSpeed.setText(String.valueOf((float)speed));
			textViewDistance.setText(String.valueOf((float)((int)(totalDistanceRun/10))/100));
			textViewCalories.setText(String.valueOf((float)calories));

			
			intentService = new Intent(HRunActivity.this, HService.class);
		}
	}

    @Override
	protected void onDestroy() {
		super.onDestroy();
		
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
	}

    // Receive data from HService concerning the chronometer value and the user location
	private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// If the timer is running, the informations are displayed
			if (chronoState == 0){
				Bundle bundle = intent.getExtras();
				float temp = bundle.getFloat("Distance");
				
				// If the position given by the GPS tool has changed, informations are updated
				if (!(distanceRun == temp)){
					speed = (float)(((int)(bundle.getFloat("Speed")*3600/1000*100)))/100;
					
					distanceRun = temp;
					totalDistanceRun = totalDistanceRun + distanceRun;
					calories = (float) (((int)(poids*totalDistanceRun*1.034*100/1000)))/100;
					
					textViewSpeed.setText(String.valueOf(speed));
					textViewDistance.setText(String.valueOf((float)((int)(totalDistanceRun/10))/100));
					textViewCalories.setText(String.valueOf(calories));
				}
				
				// Timer value is updated
				chronoValue = bundle.getInt("Compteur") - chronoPauseValue;
				long heures = chronoValue/3600;
				long minutes = chronoValue/60%60;
				long seconds = chronoValue%60;
				String string = "";
				
				if (heures < 10)
					string = "0";
				string = string + heures;
				if (minutes < 10)
					string = string + ":0" + minutes;
				else
					string = string + ":" + minutes;
				if (seconds < 10)
					string = string + ":0" + seconds;
				else
					string = string + ":" + seconds;
				
				textViewChrono.setText(string);
				
			}
			else
				chronoPauseValue++;
		}
	};
	
	public void onClick(View v) {
		
		switch(v.getId()){
			case R.id.button_run:
				// Initialization
				switch (chronoState){
					case -1: //If the chronometer is OFF
						chronoState = 0;
						chronoValue = 0;
						chronoPauseValue = 0;
						totalDistanceRun = 0;
						resume = true;
						
						
						intentService = new Intent(HRunActivity.this, HService.class);
						startService(intentService);
						
						buttonStart.setText("Pause");
						buttonStop.setClickable(true);
						
						break;
					case 0: //If the chronometer is running
						chronoState = 1;
						
						buttonStart.setText("Resume");
						break;
					case 1: //If the chronometer is on pause
						chronoState = 0;
						
						buttonStart.setText("Pause");
						break;
				}
			break;
			
			case R.id.button_stop:				
				/*
				 *  When user click on the - Stop - button, data are saved as a new run
				 *  Application has to collect informations from different places and create a new object to store in DB
				 */
			    Date today = new Date();
			    int start_date = (int) (today.getTime()/1000) - chronoValue;
			    TextView tv_distance = (TextView) findViewById(R.id.txt_distance);
			    float distance = Float.parseFloat(tv_distance.getText().toString());
			    
			    
				HRun run = new HRun(
								start_date,
								chronoValue,
								distance,
								calories
				);
				
				running_db.open();
				running_db.insertRun(run);

				//Restart the initial parameters
				chronoState = -1;
				chronoPauseValue = 0;
				calories = 0;
				resume = false;
				
				//Restart the initial display
				textViewChrono.setText("00:00:00");
				textViewSpeed.setText("0.0");
				textViewDistance.setText("0.0");
				textViewCalories.setText("0.0");
				buttonStart.setText("Start");
	        	buttonStop.setClickable(false);
				
				stopService(intentService);
			break;

			// Switch activity to HSettingsActivity (- Settings - screen)
			case R.id.btn_menu_settings:
				Intent next_activity = new Intent(this, HSettingsActivity.class);
				startActivity(next_activity);
			break;

			// Switch activity to HListofrunActivity (- List of run - screen)
			case R.id.btn_menu_listofrun:
				Intent next_activity1 = new Intent(this, HListofrunActivity.class);
				startActivity(next_activity1);
			break;
			
			default:
			break;
		}
	}
}