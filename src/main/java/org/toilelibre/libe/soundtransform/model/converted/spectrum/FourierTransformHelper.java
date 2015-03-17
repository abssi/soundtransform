package org.toilelibre.libe.soundtransform.model.converted.spectrum;

import java.io.Serializable;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

public interface FourierTransformHelper<T extends Serializable> {

    public Sound reverse(Spectrum<T> spectrum);

    public Sound transform(AbstractFrequencySoundTransformation<T> st, Sound sound);
}
