package bradleyFinal;

import com.softsynth.jmsl.MusicShape;

public class Quantizer {
	// The input musicshape is already differenriated so its durations are actual durations not time stamps
	void quantize(MusicShape s, double coreDur) {
		for (int i = 0; i < s.size(); i++) {
			double dur = s.get(i, 0);
			int div = (int) Math.round(dur / coreDur);
			double quantizedDur = coreDur * div;
			s.set(quantizedDur, i, 0);
		}
	}

	public static void main(String[] args) {
		// create shape with ragged durs
		MusicShape testShape = new MusicShape(4);
		testShape.add(0.51, 60, .5, 0.2);
		testShape.add(0.41, 63, .5, 0.2);
		testShape.add(1.2, 64, .5, 0.2);
		testShape.add(0.95, 65, .5, 0.2);
		testShape.add(2.41, 66, .5, 0.2);

		new Quantizer().quantize(testShape, 0.25);
		// print same shape after tightening up durs
		testShape.print();
	}
}