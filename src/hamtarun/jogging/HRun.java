package hamtarun.jogging;

/**
 * @author Gatellier Bastien, Pouchepanadin Christian
 * Define a run
 */
public class HRun {
	private int id;
	private int startdate;	// Timestamp : number of seconds since Jan. 1, 1970
	private int duration;	// Seconds
	private float distance;	// Kilometers
	private float calories;	// kCal

	public HRun(int startdate, int duration, float distance, float calories) {
		this.startdate = startdate;
		this.duration = duration;
		this.distance = distance;
		this.calories = calories;
	}

	public int getStartDate() {
		return startdate;
	}

	public void setStartDate(int date) {
		this.startdate = date;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	public int getId() {
		return id;
	}

	public float getCalories() {
		return this.calories;
	}

	public void setCalories(float calories) {
		this.calories = calories;
	}
}
