package jsynvoices;

import java.io.File;
import com.softsynth.jmsl.JMSL;
import com.softsynth.jmsl.JMSLMixerContainer;
import com.softsynth.jmsl.MusicShape;
import com.softsynth.jmsl.jsyn2.JSynMusicDevice;
import com.softsynth.jmsl.jsyn2.SamplePlayingInstrument;

public class DrumSampleIns extends SamplePlayingInstrument {
	public static final int KICK = 35;
	public static final int HIHAT = 80;
	public static final int SNARE = 40;
	public DrumSampleIns() {
		// change to your sample directory
		setDirectory(new File("/Users/trey/Documents/Samples/logic"));
		// load in samples here
		addSamplePitch("hihat.aiff", HIHAT); // hihat sound
		addSamplePitch("snare.aiff", SNARE); // snare sound
		addSamplePitch("bass.aiff", KICK); // new bass sound
		buildFromAttributes();
	}

	public static void main(String[] args) {
		java.awt.Frame f = new java.awt.Frame("JSyn2 SamplePlayingInstrument");
		f.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				JMSL.closeMusicDevices();
				System.exit(0);
			}
		});
		f.setSize(300, 200);
		f.setVisible(true);

		JMSL.clock.setAdvance(0.2);

		JSynMusicDevice dev = JSynMusicDevice.instance();
		dev.open();

		DrumSampleIns ins = new DrumSampleIns();

		JMSLMixerContainer mixer = new JMSLMixerContainer();
		mixer.addInstrument(ins);
		mixer.start();

		MusicShape drumShape = new MusicShape(4);
		drumShape.add(0.3, 80, 0.5, 0.2);
		drumShape.add(0.6, 35, 0.5, 0.2);
		drumShape.add(0.7, 40, 0.5, 0.2);
		drumShape.setRepeats(16);
		drumShape.setInstrument(ins);
		drumShape.launch(JMSL.now());
	}
}