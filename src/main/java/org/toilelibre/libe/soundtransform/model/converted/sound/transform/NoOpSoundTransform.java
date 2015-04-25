package org.toilelibre.libe.soundtransform.model.converted.sound.transform;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;

/**
 * Produces an exact copy of the input sound
 *
 */
public class NoOpSoundTransform implements SoundTransform<Sound, Sound> {

    public NoOpSoundTransform () {
    }

    private Sound noop (final Sound sound) {
        final long [] data = sound.getSamples ();

        // same array in newdata
        final long [] newdata = new long [data.length];

        System.arraycopy (data, 0, newdata, 0, data.length);

        return new Sound (newdata, sound.getFormatInfo (), sound.getChannelNum ());
    }

    @Override
    public Sound transform (final Sound input) {
        return this.noop (input);
    }
}