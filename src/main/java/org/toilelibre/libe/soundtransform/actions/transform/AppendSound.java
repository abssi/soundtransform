package org.toilelibre.libe.soundtransform.actions.transform;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.observer.Observer;

public final class AppendSound extends Action {

    public AppendSound (final Observer... observers) {
        super (observers);
    }

    public Sound [] append (final Sound [] sounds1, final Sound [] sounds2) throws SoundTransformException {
        return this.transformSound.append (sounds1, sounds2);
    }
}