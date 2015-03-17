package org.toilelibre.libe.soundtransform.infrastructure.service.freqs;

import org.toilelibre.libe.soundtransform.infrastructure.service.freqs.PianoFrequency.PianoValues;
import org.toilelibre.libe.soundtransform.model.freqs.AdjustFrequenciesProcessor;

class AdjustFrequenciesToPianoProcessor implements AdjustFrequenciesProcessor {

    @Override
    public float[] adjust(final float[] freqs) {
        final float[] output = new float[freqs.length];
        for (int i = 0; i < freqs.length; i++) {
            output[i] = PianoValues.getNearestNote(freqs[i]).getFrequency();
        }
        return output;
    }

}
