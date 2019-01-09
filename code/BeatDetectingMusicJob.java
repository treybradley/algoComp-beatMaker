package bradleyFinal;

import com.softsynth.jmsl.JMSL;
import com.softsynth.jmsl.MusicJob;
import com.softsynth.jmsl.MusicShape;

public class BeatDetectingMusicJob extends MusicJob {

	private static final int MAX_BEATS_PER_SECOND = 8;
	public static final double POLLING_RATE = 0.01;  // kind of sample rate 'polling rate'... frequency of checking peaks
	private Peak myPeak;
	final double THRESHOLD = 0.02; // adjust sensitivity to peaks amongst noise
	private double previousTimeStamp = 0;
	MusicShape beats = new MusicShape(1);
	private double recordingStartTime = -1;
	
	boolean isRecording = false; // initialize isRecording to false , but make it true when we click record in the Gui

	public boolean isRecording() {
		return isRecording;
	}

	public void setRecording(boolean isRecording) {
		this.isRecording = isRecording;
			if (isRecording) {
				beats.removeAll(); // start recording from scratch when you hit start recording again
				recordingStartTime = JMSL.now();
			}
	}
	
	public double getElapsedTimeBetweenRecordAndFirstPeak() {
		System.out.println("started recording at : " + recordingStartTime + ", beats:" );
		beats.print();
		return beats.get(0,0) - recordingStartTime;
	}
	
	public void setPeak(Peak myPeak) {
		this.myPeak = myPeak;
	}

	public double repeat(double playTime) {
		if (myPeak != null) {
			double peakValue = myPeak.output.getValue();
			double delta = playTime - previousTimeStamp;
			double timeSinceStartRec = playTime - recordingStartTime;
			if (peakValue > THRESHOLD && delta > (1.0 / MAX_BEATS_PER_SECOND) && isRecording && timeSinceStartRec > 0.1) {
				System.out.println("Detected beat onset value=" + peakValue + ", playTime=" + playTime);
				beats.add(playTime);
				previousTimeStamp = playTime;
			}
		}
		return playTime + POLLING_RATE;
	}

	public MusicShape getBeats() {
		return beats;
	}
}