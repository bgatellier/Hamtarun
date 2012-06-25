package hamtarun.jogging;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author Gatellier Bastien, Pouchepanadin Christian
 * Define the behaviors of the - List of run - screen
 * List all the run the user did.
 * Each run display its informations, like the date, the duration, the distance, the average speed and the number of calories
 * The user can delete one of them by clicking on it and confirm his/her choice.
 */
public class HListofrunActivity extends Activity implements View.OnClickListener {
	/*	DB params	*/
	private HListofrunDB running_db;

	/*	UI params	*/
	private Button btn_menu_run;
	private Button btn_menu_settings;
	
	private ListAdapter dataSource;
	private ListView list_run;
	private Cursor data;

	public HListofrunActivity() {}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.listofrun);	

		// Get Views from the layout
        btn_menu_run = (Button) findViewById(R.id.btn_menu_run);
        btn_menu_run.setOnClickListener(this);
        btn_menu_settings = (Button) findViewById(R.id.btn_menu_settings);
        btn_menu_settings.setOnClickListener(this);
		list_run = (ListView) findViewById(R.id.list_run);

		// Open the DB and get the list of run
		running_db = new HListofrunDB(this);
		running_db.open();
		data = running_db.selectAll();
		startManagingCursor(data);
		dataSource = new SimpleCursorAdapter(
										this, R.layout.listofrun_details, data,
										new String[]{"startdate","duration","distance","speed","calories"},
										new int[]{R.id.txt_startdate_value,R.id.txt_duration_value,R.id.txt_distance_value,R.id.txt_speed_value,R.id.txt_calories_value}
		);
		
		// Put the results in the ListView
		list_run.setAdapter(dataSource);
		
		// Set a listener on each item of the ListView
		list_run.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> viewParent, View v, int position, final long id) {
				
				// Create the modal alert dialog
        		AlertDialog.Builder adb = new AlertDialog.Builder(HListofrunActivity.this);
        		adb.setTitle("Delete the run ?");
        		
        		// Add an - OK - button to this modal alert dialog
                adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    	// Delete the row in the SQLite DB
                    	running_db.deleteRun((int)id);
                    	
                    	// Get the remaining rows
                    	data.requery();
                    }
                });
         
                // Add and - Cancel - button to this modal alert dialog
                adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}
                });
                
        		adb.show();
        	}
         });
		
	}
	
	@Override
	protected void onDestroy() {
		// Close the DB
		running_db.close();
		
		super.onDestroy();
	}

	public void onClick(View v){
		Intent next_activity;

		switch(v.getId()){
			// Switch activity to HRunActivity (- Run - screen)
			case R.id.btn_menu_run:
				next_activity = new Intent(this, HRunActivity.class);
				startActivity(next_activity);
			break;

			// Switch activity to HSettingsActivity (- Settings - screen)
			case R.id.btn_menu_settings:
				next_activity = new Intent(this, HSettingsActivity.class);
				startActivity(next_activity);
			break;
		}
	}

}
