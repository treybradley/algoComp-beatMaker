package bradleyFinal;

import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.UnitVoice;
import com.jsyn.unitgen.PeakFollower;
import com.jsyn.ports.UnitInputPort;
import com.softsynth.shared.time.TimeStamp;
import com.jsyn.unitgen.PassThrough;
import com.jsyn.unitgen.Circuit;

public class Peak extends Circuit implements UnitVoice {
    // Declare units and ports.
    PassThrough mInputPassThrough;
    public UnitInputPort input;
    PeakFollower mPeakFollower;
    PassThrough mOutputPassThrough;
    public UnitOutputPort output;

    // Declare inner classes for any child circuits.
    public Peak() {
        // Create unit generators.
        add(mInputPassThrough = new PassThrough());
        addPort(input = mInputPassThrough.input, "input");
        add(mPeakFollower = new PeakFollower());
        add(mOutputPassThrough = new PassThrough());
        addPort(output = mOutputPassThrough.output, "output");
        // Connect units and ports.
        mInputPassThrough.output.connect(mPeakFollower.input);
        mPeakFollower.output.connect(mOutputPassThrough.input);
        // Setup
        input.setup(0.0, 0.0, 1.0);
        mPeakFollower.halfLife.set(0.001);
    }

    public void noteOn(double frequency, double amplitude, TimeStamp timeStamp) {
    }

    public void noteOff(TimeStamp timeStamp) {
    }
    
    public UnitOutputPort getOutput() {
        return output;
    }
}