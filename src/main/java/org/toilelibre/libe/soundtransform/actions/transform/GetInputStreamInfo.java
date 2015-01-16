package org.toilelibre.libe.soundtransform.actions.transform;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.actions.Action;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;

public class GetInputStreamInfo extends Action {

    public InputStreamInfo getInputStreamInfo (InputStream ais) throws SoundTransformException{
        return this.transformSound.getInputStreamInfo (ais);
    }
}