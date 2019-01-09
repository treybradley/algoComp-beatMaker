package bradleyFinal;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
//import java.io.File;
//import java.io.FileNotFoundException;

import javax.swing.JButton;
import javax.swing.JFrame;

import com.jsyn.Synthesizer;
import com.jsyn.devices.AudioDeviceManager;
import com.jsyn.unitgen.Add;
import com.jsyn.unitgen.LineIn;
import com.jsyn.unitgen.LineOut;
//import com.jsyn.util.WaveRecorder;
import com.softsynth.jmsl.JMSL;
import com.softsynth.jmsl.JMSLMixerContainer;
import com.softsynth.jmsl.JMSLRandom;
import com.softsynth.jmsl.MusicShape;
import com.softsynth.jmsl.ParallelCollection;
import com.softsynth.jmsl.jsyn2.JSynMusicDevice;
import com.softsynth.jmsl.jsyn2.JSynUnitVoiceInstrument;

import jsynvoices.DrumSampleIns;
import jsynvoices.SawAttack;

public class PeakFollowerGui extends JFrame implements ActionListener {
	// define components re: signals, like line in (to receive user signal), line out, beat detector (uses peak detector), etc.
	Peak myPeak;
	LineOut out;
	LineIn in;
//	WaveRecorder recorder;
	BeatDetectingMusicJob beatDetectingMusicJob;
	Synthesizer synth;
	double quantizeValue = -1;
	ParallelCollection col = new ParallelCollection();
	// define jframe components
	JButton recButton;
	JButton stopRecordButton;
	JButton playButton;
	JButton stopPlayButton;
	JButton eraseButton;

	public BeatDetectingMusicJob getBeatDetectingMusicJob() {
		return beatDetectingMusicJob;
	}

	public void setBeatDetectingMusicJob(BeatDetectingMusicJob beatDetectingMusicJob) {
		this.beatDetectingMusicJob = beatDetectingMusicJob;
	}

	// build and add buttons to jframe. Make buttons action listeners
	void build() {
		setLayout(new FlowLayout());
		add(recButton = new JButton("Start Record"));
		add(stopRecordButton = new JButton("Stop Recording"));
		add(playButton = new JButton("Play"));
		add(stopPlayButton = new JButton("Stop Play"));
		add(eraseButton = new JButton("Erase"));
		recButton.addActionListener(this);
		stopRecordButton.addActionListener(this);
		playButton.addActionListener(this);
		stopPlayButton.addActionListener(this);
		eraseButton.addActionListener(this);

		stopRecordButton.setEnabled(false);
		stopPlayButton.setEnabled(false);
	}

	// communicate action listener buttons from jframe with beat detecting Music job
	public void actionPerformed(ActionEvent e) {
		System.out.println("Click");
		Object source = e.getSource(); // get source to perform corresponding button's action

		if (source == recButton) {
			System.out.println("Record Starting setRecording True");
			if (beatDetectingMusicJob != null) {
				beatDetectingMusicJob.setRecording(true);
			}
			stopRecordButton.setEnabled(true);
			recButton.setEnabled(false);
		}

		if (source == stopRecordButton) {
			double timestopRecordingTimeStamp = JMSL.now();
			System.out.println(
					"Record Stopping setRecording False. Get shape from music job and put in parallel collection");
			if (beatDetectingMusicJob != null) {
				beatDetectingMusicJob.setRecording(false);
				double[] pitches = null;
				MusicShape myBeats = beatDetectingMusicJob.getBeats();
//				myBeats.print();
				MusicShape performableShape = new MusicShape(4);
				double amp = 0.9;
				
				if (shapeCount % 4 == 0) {
					performableShape.setInstrument(sample); // first shape is high hat (sample instrument corresponding to pitch = 80)					
					pitches = new double[1];
					pitches[0] = DrumSampleIns.HIHAT;
					amp = 0.3;
				} else if (shapeCount % 4 == 1) {
					performableShape.setInstrument(sample); // set 2nd shape to bass drum (sample instrument corresponding to pitch 35)
					pitches = new double[1];
					pitches[0] = DrumSampleIns.KICK;
				} else if (shapeCount % 4 == 2) {
					performableShape.setInstrument(sample); // set 3rd shape to snare drum (sample instrument corresponding to pitch 40)
					pitches = new double[1];
					pitches[0] = DrumSampleIns.SNARE;
				} else if (shapeCount % 4 == 3) {
					performableShape.setInstrument(ins1); // set ins2 - unit voice instrument for tone component. sub w/ any voice
					pitches = new double[2];
					pitches[0] = 42;
					pitches[1] = 52;
				}
				shapeCount++;

				for (int i = 0; i < myBeats.size(); i++) {
					double durValue = myBeats.get(i, 0); // i elements for dimension 0 (duration)
					double pitch = pitches[JMSLRandom.choose(pitches.length)]; // dimension 1 add pitch, defined above in each condition
					performableShape.add(durValue, pitch, amp, 0.3);
				}

				if (myBeats.size() > 0) {
					double elapsedTime = timestopRecordingTimeStamp - myBeats.get(0, 0);
					performableShape.differentiate(elapsedTime, 0); // from time stamps to durations
//					performableShape.print();

					// quantize here based on the first element in the first shape... adjust division value in quantizeValue below (now 8)
					if (col.size() == 0) {
						double elapsedTimeBetweenRecordAndFirstPeak = beatDetectingMusicJob
								.getElapsedTimeBetweenRecordAndFirstPeak();
						quantizeValue = elapsedTimeBetweenRecordAndFirstPeak / 8;
						System.out.println("quantizeValue = " + quantizeValue);
					}

					new Quantizer().quantize(performableShape, quantizeValue);

					col.add(performableShape);
					if (col.size() % 4 == 2) {
						// this is the snare
						double startDelayTime = performableShape.get(0, 0);
						performableShape.remove(0);
						performableShape.setStartDelay(startDelayTime);
						System.out.println("delayed first element for snare " + startDelayTime);
					} else {
						System.out.println("NOT messed with first element");
					}
//					System.out.println("***&&*&***** col.size()=" + col.size());
					performableShape.print();
				} else {
					System.out.println("No Beats Recorded ... Record Something");
				}
			}
			recButton.setEnabled(true);
			stopRecordButton.setEnabled(false);
		}

		if (source == playButton) {
			System.out.println("Playback get Music Shape from Job and Playback");
			if (beatDetectingMusicJob != null) {
				col.setRepeats(1000);
				col.launch(JMSL.now());
				playButton.setEnabled(false);
				stopPlayButton.setEnabled(true);
				col.print();
			}
		}

		if (source == stopPlayButton) {
			System.out.println("Stop Playing");
			col.finishAll();
			playButton.setEnabled(true);
			stopPlayButton.setEnabled(false);
		}

		if (source == eraseButton) {
			col.finishAll();
			col.removeAll();
			System.out.println("Clear beats");
			shapeCount = 0; // bring shape count to zero so we can start with first instrument
		}
	}

	int shapeCount = 0; // initialize shape count

	DrumSampleIns sample; // bring in drum sample instrument and unit voice instrument
	JSynUnitVoiceInstrument ins1;

	JMSLMixerContainer mixer;

	void initMixer() {
		mixer = new JMSLMixerContainer();
		mixer.start();
	}

	// use instrument in Music Shape
	void initInstrument() {
		sample = new DrumSampleIns();
		ins1 = new JSynUnitVoiceInstrument(4, SawAttack.class.getName());
		// add instruments to mixer
		mixer.addInstrument(sample);
		mixer.addInstrument(ins1);
		// import instruments as shapes
		MusicShape ss1 = new MusicShape(sample.getDimensionNameSpace());
		MusicShape si1 = new MusicShape(ins1.getDimensionNameSpace());
		ss1.print();
		si1.print();
	}

	void buildSound() {
		JMSL.clock.setAdvance(.3);
		JSynMusicDevice dev = JSynMusicDevice.instance();
		dev.setInputDeviceID(AudioDeviceManager.USE_DEFAULT_DEVICE);
		dev.setOutputDeviceID(AudioDeviceManager.USE_DEFAULT_DEVICE);
		dev.open();
		initMixer();
		initInstrument();

		synth = dev.getSynthesizer();
		synth.add(out = new LineOut());
		synth.add(in = new LineIn());
		synth.add(myPeak = new Peak());

		out.start();

		// connect LineIn to the PeakDetector
		in.output.connect(0, myPeak.input, 0);
		in.output.connect(1, myPeak.input, 0);

		// test with impulse osc
//		com.jsyn.unitgen.ImpulseOscillator imopulseOsc = new com.jsyn.unitgen.ImpulseOscillator();
//		synth.add(imopulseOsc);
//		imopulseOsc.frequency.set(1);
//		imopulseOsc.amplitude.set(0.9);
//		imopulseOsc.output.connect(0, myPeak.input, 0);
//		imopulseOsc.output.connect(out.input);

		Add addunit = new Add();
		synth.add(addunit);
		addunit.start();
		myPeak.output.connect(addunit.inputA);

//		File waveFile = new File("/Users/trey/Desktop/peak_recording.wav");
		// Default is stereo, 16 bits.
//		try {
//			recorder = new WaveRecorder(synth, waveFile);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		in.output.connect(0, recorder.getInput(), 0);
//		myPeak.output.connect(0, recorder.getInput(), 1);
	}

	void buildMusicJob() {
		beatDetectingMusicJob = new BeatDetectingMusicJob();
		beatDetectingMusicJob.setRepeats(Integer.MAX_VALUE);
		beatDetectingMusicJob.setPeak(myPeak);
		beatDetectingMusicJob.launch(JMSL.now());
	}

	// build out gui interface, make
	public static void main(String[] args) {
		PeakFollowerGui gui = new PeakFollowerGui();
		gui.build();
		gui.pack();
		gui.setVisible(true);
		gui.buildSound();
		gui.buildMusicJob();
		gui.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}
}