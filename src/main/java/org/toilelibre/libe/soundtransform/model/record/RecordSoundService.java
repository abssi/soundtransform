package org.toilelibre.libe.soundtransform.model.record;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.Service;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.logging.LogAware;

@Service
public interface RecordSoundService<T> extends LogAware<T> {

    InputStream recordRawInputStream (StreamInfo streamInfo, Object stop) throws SoundTransformException;

    InputStream recordLimitedTimeRawInputStream (StreamInfo streamInfo) throws SoundTransformException;

    Sound startRecordingASound (StreamInfo streamInfo, AmplitudeObserver amplitudeObserver, Object stop) throws SoundTransformException;

}