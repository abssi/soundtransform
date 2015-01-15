package org.toilelibre.libe.soundtransform;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.math3.complex.Complex;
import org.junit.Test;
import org.toilelibre.libe.soundtransform.infrastructure.service.play.LineListenerPlaySoundProcessor;
import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.sound.PlaySoundException;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.ConvertAudioFileService;
import org.toilelibre.libe.soundtransform.model.play.PlaySoundProcessor;

public class TestPlaySound {
    private final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();
    private final File        input       = new File (this.classLoader.getResource ("before.wav").getFile ());

    @Test
    public void playBeforeWav () throws SoundTransformException {
        final PlaySoundProcessor<Complex []> ps = new LineListenerPlaySoundProcessor ();
        final ConvertAudioFileService convertAudioFileService = $.create (ConvertAudioFileService.class);
        final InputStream ais = convertAudioFileService.callConverter (this.input);
        try {
            ps.play (ais);
        } catch (final java.lang.IllegalArgumentException iae) {
            if (!"No line matching interface Clip is supported.".equals (iae.getMessage ())) {
                throw iae;
            }
        } catch (final PlaySoundException e) {
            // javax.sound.sampled.LineUnavailableException for some JDK
            // versions
            if (!javax.sound.sampled.LineUnavailableException.class.equals (e.getCause ().getClass ())) {
                throw e;
            }
        }
    }
}
