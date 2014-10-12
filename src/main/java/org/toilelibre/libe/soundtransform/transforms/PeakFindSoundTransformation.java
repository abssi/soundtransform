package org.toilelibre.libe.soundtransform.transforms;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.toilelibre.libe.soundtransform.objects.Spectrum;
import org.toilelibre.libe.soundtransform.objects.Sound;
import org.toilelibre.libe.soundtransform.pda.SpectrumHelper;

public class PeakFindSoundTransformation extends NoOpFrequencySoundTransformation {

	private double	      threshold;
	private List<Integer>	loudestfreqs;
	private boolean	      note;
	private int	          fsLimit;
	private int           windowLength;

	public PeakFindSoundTransformation (boolean note) {
		this.note = note;
		this.threshold = 100;
		this.windowLength = -1;
	}

	public PeakFindSoundTransformation (double threshold) {
		this.threshold = threshold;
		this.windowLength = -1;
	}

	public PeakFindSoundTransformation (double threshold, int windowLength) {
		this.threshold = threshold;
		this.windowLength = windowLength;
	}

	@Override
	public Sound initSound (Sound input) {
		this.loudestfreqs = new LinkedList<Integer> ();
		if (this.note) {
			this.threshold = input.getSamples ().length;
			this.fsLimit = input.getSamples ().length;
		} else if (this.windowLength != -1){
			this.fsLimit = input.getSampleRate ();
		}
		return super.initSound (input);
	}

	@Override
	protected double getLowThreshold (double defaultValue) {
		return this.threshold;
	}

	@Override
	protected int getWindowLength (double freqmax) {
		if (this.windowLength != -1){
			return this.windowLength;
		}
		return (int) Math.pow (2, Math.ceil (Math.log (this.fsLimit) / Math.log (2)));
	}

	public List<Integer> getLoudestFreqs () {
		return this.loudestfreqs;
	}

	@Override
	public Spectrum transformFrequencies (Spectrum fs, int offset, int powOf2NearestLength, int length) {

		int [] peaks = new int [10];
		for (int i = 1; i <= 10; i++) {
			peaks [i - 1] = SpectrumHelper.f0 (fs, i);
		}
		Arrays.sort (peaks);
		int f0 = this.bestCandidate (peaks);

		this.loudestfreqs.add (f0);
		return fs;
	}

	private int bestCandidate (int [] peaks) {
		int leftEdge = 0;
		while (leftEdge < peaks.length && peaks [leftEdge] <= 30) {
			leftEdge++;
		}
		int rightEdge = leftEdge;
		while (rightEdge < peaks.length && Math.abs ( (peaks [rightEdge] - peaks [leftEdge]) * 1.0 / peaks [rightEdge]) * 100.0 < 10) {
			rightEdge++;
		}
		int sum = 0;
		for (int i = leftEdge; i < rightEdge; i++) {
			sum += peaks [i];
		}

		return (rightEdge == leftEdge ? sum : sum / (rightEdge - leftEdge));
	}
}
