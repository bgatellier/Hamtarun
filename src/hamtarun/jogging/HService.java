package hamtarun.jogging;

import java.util.Timer;
import java.util.TimerTask;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

/**
 * @author Gatellier Bastien, Pouchepanadin Christian
 * Define the chronometer and the user location
 */
public class HService extends Service {
	private int cptService = 0;
	private float distanceRun = 0;
	private float timePassed = 0;
	private float speed = 0;
	
	private Timer timer = new Timer();
	static final int TIMER_UPDATE_INTERVAL = 1000;
	
	static int LOCATION_UPDATE_INTERVAL = 5000;
	
	private Location location = null;
	private Location lastLocation = null;
	private LocationManager	locationMgr = null;
	private LocationListener onLocationChange = new LocationListener(){ 
		 public void onStatusChanged(String provider, int status, Bundle extras){}
		 public void onProviderEnabled(String provider){}
		 public void onProviderDisabled(String provider){}
		 
		 public void onLocationChanged(Location location2){
			 if (location != null)
				 lastLocation = location;
			 location = location2;
			 
			 if (lastLocation != null){
				 //1 degree north latitude = 60 minutes = 1 nautical miles = 1.15 statute miles = 1.15 * 1852 meters
				 distanceRun = (float) (Math.abs(location.getLatitude() - lastLocation.getLatitude()) * 60 * 1.15 * 1852);
				 timePassed = (float) (Math.abs((location.getTime() - lastLocation.getTime())/1000)); //time in sec
				 speed = (distanceRun/timePassed);
			 }
			 else{
				 timePassed = location.getTime()/1000;
			 }
		 }
	 }; //Send a notification to the LocationManager everytime the position changes
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	 
	@Override
	public void onCreate() {
		locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_INTERVAL, 0,onLocationChange);
			 
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (timer != null){ //delete the timer
			timer.cancel();
		}

		locationMgr.removeUpdates(onLocationChange); //LocationManager doesn't receive notification anymore
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		doSomethingRepeatedly();
		
		return super.onStartCommand(intent, flags, startId);
	}

	private void doSomethingRepeatedly() {
		timer.scheduleAtFixedRate( new TimerTask() {
			public void run() {
				//---send a broadcast to inform the activity
				// that the timer expires
				
				Intent broadcastIntent = new Intent();
				broadcastIntent.setAction("TIMER_OUT");
				broadcastIntent.putExtra("Compteur", cptService);
				broadcastIntent.putExtra("Speed", speed);
				broadcastIntent.putExtra("Distance", distanceRun);
				getBaseContext().sendBroadcast(broadcastIntent);
				
				cptService++;
			}
		}, 0, TIMER_UPDATE_INTERVAL);
	}
}