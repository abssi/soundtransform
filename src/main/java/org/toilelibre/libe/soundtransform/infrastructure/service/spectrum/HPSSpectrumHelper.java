package org.toilelibre.libe.soundtransform.infrastructure.service.spectrum;

import org.apache.commons.math3.complex.Complex;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumHelper;

public class HPSSpectrumHelper implements SpectrumHelper<Complex []> {

    public HPSSpectrumHelper () {

    }

    /**
     * Find the f0 (fundamental frequency) using the Harmonic Product Spectrum
     *
     * @param fs
     *            spectrum at a specific time
     * @param hpsfactor
     *            number of times to multiply the frequencies together
     * @return a fundamental frequency (in Hz)
     */
    @Override
    public float f0 (final Spectrum<Complex []> fs, final int hpsfactor) {
        return this.freqFromSampleRate (this.getMaxIndex (this.hps (fs, hpsfactor), 0, fs.getState ().length / hpsfactor), fs.getState ().length * 2 / hpsfactor, fs.getSampleRate ());
    }

    @Override
    public float freqFromSampleRate (final float freq, final int sqr2length, final int sampleRate) {
        return (int) (freq * 2.0 * sampleRate / sqr2length);
    }

    @Override
    public int getMaxIndex (final Spectrum<Complex []> fs, final int low, final int high) {
        double max = 0;
        int maxIndex = 0;
        final int reallow = low == 0 ? 1 : low;
        final int realhigh = Math.min (high, fs.getState ().length);
        for (int i = reallow ; i < realhigh ; i++) {
            if (max < fs.getState () [i].abs () && fs.getState () [i].abs () > Math.pow (256, fs.getNbBytes ()) + 1) {
                max = fs.getState () [i].abs ();
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    @Override
    public Spectrum<Complex []> hps (final Spectrum<Complex []> fs, final int factor) {
        final int max = fs.getState ().length / factor;
        final Complex [] result = new Complex [max];
        for (int i = 0 ; i < max ; i++) {
            double val = fs.getState () [i].abs ();
            for (int j = 1 ; j < factor ; j++) {
                if (i * factor < fs.getSampleRate () / 2 && i * factor < fs.getState ().length) {
                    val *= fs.getState () [i * factor].abs ();
                }
            }
            result [i] = new Complex (val);
        }
        return new Spectrum<Complex []> (result, fs.getSampleRate () / factor, fs.getNbBytes ());
    }
}
