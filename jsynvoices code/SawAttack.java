package jsynvoices;

/**************
** WARNING - this code automatically generated by Syntona.
** The real source is probably a Syntona patch.
** Do NOT edit this file unless you copy it to another directory and change the name.
** Otherwise it is likely to get clobbered the next time you
** export Java source code from Syntona.
**
** Syntona is available from: http://www.softsynth.com/syntona/
*/

import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.UnitVoice;
import com.jsyn.unitgen.VariableRateMonoReader;
import com.jsyn.unitgen.SawtoothOscillator;
import com.jsyn.ports.UnitInputPort;
import com.softsynth.shared.time.TimeStamp;
import com.jsyn.unitgen.PassThrough;
import com.jsyn.unitgen.PinkNoise;
import com.jsyn.data.SegmentedEnvelope;
import com.jsyn.unitgen.FilterBandPass;
import com.jsyn.unitgen.Circuit;

public class SawAttack extends Circuit implements UnitVoice {
    // Declare units and ports.
    PassThrough mFrequencyPassThrough;
    public UnitInputPort frequency;
    PassThrough mAmplitudePassThrough;
    public UnitInputPort amplitude;
    PassThrough mOutputPassThrough;
    public UnitOutputPort output;
    SegmentedEnvelope mSegEnv;
    VariableRateMonoReader mMonoRdr;
    PinkNoise mPinkNoise;
    FilterBandPass mBandPass;
    SawtoothOscillator mSawOsc;

    // Declare inner classes for any child circuits.

    public SawAttack() {
        // Create unit generators.
        add(mFrequencyPassThrough = new PassThrough());
        addPort(frequency = mFrequencyPassThrough.input, "frequency");
        add(mAmplitudePassThrough = new PassThrough());
        addPort(amplitude = mAmplitudePassThrough.input, "amplitude");
        add(mOutputPassThrough = new PassThrough());
        addPort( output = mOutputPassThrough.output, "output");
        double[] mSegEnvData = {
            0.003993055555555556, 1.0,
            0.053125, 0.0,
        };
        mSegEnv = new SegmentedEnvelope( mSegEnvData );
        add(mMonoRdr = new VariableRateMonoReader());
        add(mPinkNoise = new PinkNoise());
        add(mBandPass = new FilterBandPass());
        add(mSawOsc = new SawtoothOscillator());
        // Connect units and ports.
        mFrequencyPassThrough.output.connect(mSawOsc.frequency);
        mAmplitudePassThrough.output.connect(mMonoRdr.amplitude);
        mMonoRdr.output.connect(mPinkNoise.amplitude);
        mMonoRdr.output.connect(mSawOsc.amplitude);
        mPinkNoise.output.connect(mBandPass.input);
        mBandPass.output.connect(mOutputPassThrough.input);
        mSawOsc.output.connect(mBandPass.input);
        // Setup
        frequency.setup(40.0, 293.6647679174076, 8000.0);
        amplitude.setup(0.0, 0.5, 1.0);
        mMonoRdr.rate.set(1.0);
        mBandPass.frequency.set(1858.961008);
        mBandPass.amplitude.set(1.0);
        mBandPass.Q.set(1.3857139900000002);
    }

    public void noteOn(double frequency, double amplitude, TimeStamp timeStamp) {
        this.frequency.set(frequency, timeStamp);
        this.amplitude.set(amplitude, timeStamp);
        mMonoRdr.dataQueue.queueOn( mSegEnv, timeStamp);
    }

    public void noteOff(TimeStamp timeStamp) {
        mMonoRdr.dataQueue.queueOff( mSegEnv, false, timeStamp);
    }
    
    public UnitOutputPort getOutput() {
        return output;
    }
}