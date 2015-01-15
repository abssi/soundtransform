package org.toilelibre.libe.soundtransform.infrastructure.service.transforms;

import java.util.Arrays;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.FourierTransformHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SimpleFrequencySoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumHelper;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent;
import org.toilelibre.libe.soundtransform.model.observer.LogEvent.LogLevel;

public class PeakFindWithHPSSoundTransformation extends SimpleFrequencySoundTransformation {

    private double               threshold;
    private int []               loudestfreqs;
    private boolean              note;
    private int                  fsLimit;
    private int                  windowLength;
    private int                  soundLength;
    private final SpectrumHelper spectrumHelper;

    private PeakFindWithHPSSoundTransformation (FourierTransformHelper helper1, SpectrumHelper helper2) {
        super (helper1);
        this.spectrumHelper = helper2;
    }

    public PeakFindWithHPSSoundTransformation (FourierTransformHelper helper1, SpectrumHelper helper2, final boolean note) {
        this (helper1, helper2);
        this.note = note;
        this.threshold = 100;
        this.windowLength = -1;
        this.soundLength = -1;
    }

    public PeakFindWithHPSSoundTransformation (FourierTransformHelper helper1, SpectrumHelper helper2, final double threshold) {
        this (helper1, helper2);
        this.threshold = threshold;
        this.windowLength = -1;
    }

    public PeakFindWithHPSSoundTransformation (FourierTransformHelper helper1, SpectrumHelper helper2, final double threshold, final int windowLength) {
        this (helper1, helper2);
        this.threshold = threshold;
        this.windowLength = windowLength;
    }

    private int bestCandidate (final int [] peaks) {
        int leftEdge = 0;
        while (leftEdge < peaks.length && peaks [leftEdge] <= 30) {
            leftEdge++;
        }
        int rightEdge = leftEdge;
        while (rightEdge < peaks.length && Math.abs ((peaks [rightEdge] - peaks [leftEdge]) * 1.0 / peaks [rightEdge]) * 100.0 < 10) {
            rightEdge++;
        }
        int sum = 0;
        for (int i = leftEdge ; i < rightEdge ; i++) {
            sum += peaks [i];
        }

        return rightEdge == leftEdge ? sum : sum / (rightEdge - leftEdge);
    }

    public int [] getLoudestFreqs () {
        return this.loudestfreqs;
    }

    @Override
    public double getLowThreshold (final double defaultValue) {
        return this.threshold;
    }

    @Override
    public int getWindowLength (final double freqmax) {
        if (this.windowLength != -1) {
            return this.windowLength;
        }
        return (int) Math.pow (2, Math.ceil (Math.log (this.fsLimit) / Math.log (2)));
    }

    @Override
    public Sound initSound (final Sound input) {
        if (this.note) {
            this.threshold = input.getSamples ().length;
            this.fsLimit = input.getSamples ().length;
            this.loudestfreqs = new int [1];
        } else {
            this.loudestfreqs = new int [(int) (input.getSamples ().length / this.threshold) + 1];
            this.fsLimit = input.getSampleRate ();
        }
        this.soundLength = input.getSamples ().length;
        return super.initSound (input);
    }

    @Override
    public Spectrum transformFrequencies (final Spectrum fs, final int offset, final int powOf2NearestLength, final int length, final float soundLevelInDB) {

        final int percent = (int) Math.floor (100.0 * (offset / this.threshold) / (this.soundLength / this.threshold));
        if (percent > Math.floor (100.0 * ((offset - this.threshold) / this.threshold) / (this.soundLength / this.threshold))) {
            this.log (new LogEvent (LogLevel.VERBOSE, "Iteration " + (int) (offset / this.threshold) + " / " + (int) Math.ceil (this.soundLength / this.threshold) + ", " + percent + "%"));
        }
        int f0 = 0;

        if (soundLevelInDB > 30) {
            final int [] peaks = new int [10];
            for (int i = 1 ; i <= 10 ; i++) {
                peaks [i - 1] = this.spectrumHelper.f0 (fs, i);
            }
            Arrays.sort (peaks);
            f0 = this.bestCandidate (peaks);
        }

        this.loudestfreqs [(int) (offset / this.threshold)] = f0;
        return fs;
    }
}