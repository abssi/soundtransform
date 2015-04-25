package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
/**
 * Transforms a sound into a list of spectrums. Useful to display a visualizer.
 * 
 */
public class SoundToSpectrumsSoundTransform extends SimpleFrequencySoundTransform<Serializable> {

    private static final int                      TWO = 2;
    private int                                   step;
    private int                                   channel;
    private final List<Spectrum<Serializable> []> spectrums;
    private int                                   index;

    /**
     * Default constructor
     */
    public SoundToSpectrumsSoundTransform () {
        super ();
        this.spectrums = new ArrayList<Spectrum<Serializable> []> ();
    }

    @SuppressWarnings ("unchecked")
    private Spectrum<Serializable> [] generateSpectrumArray (final int spectrumsSize) {
        return new Spectrum [spectrumsSize];
    }

    public List<Spectrum<Serializable> []> getSpectrums () {
        return this.spectrums;
    }

    @Override
    public double getStep (final double defaultValue) {
        return this.step;
    }

    @Override
    public Sound initSound (final Sound input) {
        this.index = 0;
        this.channel = input.getChannelNum ();
        int roundedSize = SoundToSpectrumsSoundTransform.TWO;
        while (input.getSampleRate () > roundedSize) {
            roundedSize *= SoundToSpectrumsSoundTransform.TWO;
        }
        this.step = roundedSize;
        final int spectrumsSize = (int) Math.ceil (input.getSamplesLength () * 1.0 / roundedSize);
        this.spectrums.add (this.generateSpectrumArray (spectrumsSize));
        return super.initSound (input);
    }

    @Override
    public Spectrum<Serializable> transformFrequencies (final Spectrum<Serializable> fs) {
        this.spectrums.get (this.channel) [this.index++] = fs;
        return fs;
    }
}