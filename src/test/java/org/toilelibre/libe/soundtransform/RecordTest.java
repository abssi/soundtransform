package org.toilelibre.libe.soundtransform;

import org.junit.Test;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformTest;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;

public class RecordTest extends SoundTransformTest {

    @Test
    public void recordTwoSeconds () throws SoundTransformException{
        Sound [] sounds = FluentClient.start().withLimitedTimeRecordedInputStream(new StreamInfo (2, 100000, 2, 48000, false, true, null)).importToSound().stopWithSounds();
        sounds.hashCode ();
    }
}