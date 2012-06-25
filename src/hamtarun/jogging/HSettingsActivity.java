package hamtarun.jogging;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author Gatellier Bastien, Pouchepanadin Christian
 * Define the behaviors of the - Settings - screen
 * User can precise his/her weight, so the application would be allowed
 * to calculate the amount of calories lost in a run, according to the distance and a coefficient.
 * This property is stored with the SharedPreferences method for data storage
 */
public class HSettingsActivity extends Activity implements View.OnClickListener {
	/*	DB params	*/
	private static final String PREFS_NAME = "HSettingsPrefs";
	private static final String WEIGHT_CNAME = "weight";	/*	Column of DB	*/
	private static	SharedPreferences settings;

	/*	UI params	*/
	private Button btn_update;
	private EditText edit_weight;
	private Button button_menu_run;
	private Button button_menu_listofrun;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		// Get informations from the preferences
		settings = getSharedPreferences(PREFS_NAME,0);
		
		// Get Views from the layout
		btn_update = (Button) findViewById(R.id.btn_update);
		btn_update.setOnClickListener(this);
		button_menu_run = (Button) findViewById(R.id.btn_menu_run);
		button_menu_run.setOnClickListener(this);
		button_menu_listofrun = (Button) findViewById(R.id.btn_menu_listofrun);
		button_menu_listofrun.setOnClickListener(this);
		
		// Initialize weight with the value stored in the preferences, or 0 if it doesn't exist
		edit_weight = (EditText) findViewById(R.id.config_weight);
		edit_weight.setText(String.valueOf(settings.getFloat(WEIGHT_CNAME, 0)));
	}
	
	public static String getPrefsName() {
		return PREFS_NAME;
	}

	public static String getWeightCName() {
		return WEIGHT_CNAME;
	}
	
	public void onClick(View v) {
		Intent next_activity;
		
		switch(v.getId()){
			case R.id.btn_update:
				// Update the settings
				SharedPreferences.Editor editor = settings.edit();
				editor.putFloat(WEIGHT_CNAME,Float.parseFloat(edit_weight.getText().toString()));
				editor.commit();

				// Switch activity to HRunActivity (- Run - screen)
				next_activity = new Intent(this, HRunActivity.class);
				startActivity(next_activity);
			break;
			
			// Switch activity to HRunActivity (- Run - screen)
			case R.id.btn_menu_run:
				next_activity = new Intent(this, HRunActivity.class);
				startActivity(next_activity);
			break;

			// Switch activity to HListofrunActivity (- List of run - screen)
			case R.id.btn_menu_listofrun:
				next_activity = new Intent(this, HListofrunActivity.class);
				startActivity(next_activity);
			break;
				
			default:
			break;
		}
	}

}
