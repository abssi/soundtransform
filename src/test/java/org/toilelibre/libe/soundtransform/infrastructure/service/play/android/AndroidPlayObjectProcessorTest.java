package org.toilelibre.libe.soundtransform.infrastructure.service.play.android;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClientOperation;
import org.toilelibre.libe.soundtransform.ioc.SoundTransformAndroidTest;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;

import android.media.AudioTrack;

@PrepareForTest (AndroidPlayObjectProcessor.class)
public class AndroidPlayObjectProcessorTest extends SoundTransformAndroidTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule ();

    @Test
    public void playAMockedInputStream () throws Exception {
        this.rule.hashCode ();
        final AudioTrack audioTrack = Mockito.mock (AudioTrack.class);
        Mockito.when (audioTrack.getPlaybackHeadPosition ()).thenAnswer (new Answer<Integer> () {
            int i = 0;

            @Override
            public Integer answer (final InvocationOnMock invocation) throws Throwable {
                return Math.min (5, this.i++ / 2);
            }

        });
        PowerMockito.whenNew (AudioTrack.class).withParameterTypes (int.class, int.class, int.class, int.class, int.class, int.class)
                .withArguments (Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class)).thenReturn (audioTrack);
        final InputStream inputStream = FluentClient.start ().withClasspathResource ("before.wav").importToStream ().stopWithInputStream ();
        final StreamInfo streamInfo = FluentClient.start ().withAudioInputStream (inputStream).stopWithStreamInfo ();
        final AndroidPlayObjectProcessor processor = new AndroidPlayObjectProcessor ();
        final Object o = processor.play (inputStream, streamInfo, null, 0);
        boolean waited = false;
        synchronized (o) {
            while (!waited) {
                waited = true;
                o.wait ();
            }
        }
        Mockito.verify (audioTrack, Mockito.times (13)).getPlaybackHeadPosition ();
    }

    @Test
    public void playAMockedSound () throws Exception {
        this.rule.hashCode ();
        final AudioTrack audioTrack = Mockito.mock (AudioTrack.class);
        Mockito.when (audioTrack.getPlaybackHeadPosition ()).thenAnswer (new Answer<Integer> () {
            int i = 0;

            @Override
            public Integer answer (final InvocationOnMock invocation) throws Throwable {
                return Math.min (5, this.i++ / 2);
            }

        });
        PowerMockito.whenNew (AudioTrack.class).withParameterTypes (int.class, int.class, int.class, int.class, int.class, int.class)
                .withArguments (Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class)).thenReturn (audioTrack);
        FluentClient.start ().withClasspathResource ("gpiano3.wav").playIt ().convertIntoSound ().splitIntoSpectrums ().playIt ().extractSound ().playIt ().exportToStream ().playIt ();
        Mockito.verify (audioTrack, Mockito.atLeast (1)).write (Matchers.any (byte [].class), Matchers.anyInt (), Matchers.anyInt ());
    }

    @Test
    public void playAMockedSoundInParallel () throws Exception {
        this.rule.hashCode ();
        final AudioTrack audioTrack = Mockito.mock (AudioTrack.class);
        Mockito.when (audioTrack.getPlaybackHeadPosition ()).thenAnswer (new Answer<Integer> () {
            int i = 0;

            @Override
            public Integer answer (final InvocationOnMock invocation) throws Throwable {
                return Math.min (5, this.i++ / 2);
            }

        });
        PowerMockito.whenNew (AudioTrack.class).withParameterTypes (int.class, int.class, int.class, int.class, int.class, int.class)
                .withArguments (Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class)).thenReturn (audioTrack);
        FluentClient.start ().inParallel (FluentClientOperation.prepare ().playIt ().build (), 10, FluentClient.start ().withClasspathResource ("gpiano3.wav"));
        Mockito.verify (audioTrack, Mockito.atLeast (1)).write (Matchers.any (byte [].class), Matchers.anyInt (), Matchers.anyInt ());
    }

    @Test
    public void playAnUndefinedLengthSoundInParallel () throws Exception {
        this.rule.hashCode ();
        final AudioTrack audioTrack = Mockito.mock (AudioTrack.class);
        Mockito.when (audioTrack.getPlaybackHeadPosition ()).thenAnswer (new Answer<Integer> () {
            int i = 0;

            @Override
            public Integer answer (final InvocationOnMock invocation) throws Throwable {
                return Math.min (5, this.i++ / 2);
            }

        });
        PowerMockito.whenNew (AudioTrack.class).withParameterTypes (int.class, int.class, int.class, int.class, int.class, int.class)
                .withArguments (Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class)).thenReturn (audioTrack);
        FluentClient.start ().inParallel (FluentClientOperation.prepare ().playIt ().build (), 10, 
                FluentClient.start ().withRawInputStream (Thread.currentThread ().getContextClassLoader ().getResourceAsStream ("gpiano3.wav"), 
                        new StreamInfo (1, -1, 2, 8000, false, true, "info")));
        Mockito.verify (audioTrack, Mockito.atLeast (1)).write (Matchers.any (byte [].class), Matchers.anyInt (), Matchers.anyInt ());
    }


    @Test
    public void playRandomBytes () throws Exception {
        for (final int j : new int [] { 1, 2, 4, 5, 6, 8 }) {
            final AudioTrack audioTrack = Mockito.mock (AudioTrack.class);
            Mockito.when (audioTrack.getPlaybackHeadPosition ()).thenAnswer (new Answer<Integer> () {
                int i = 0;

                @Override
                public Integer answer (final InvocationOnMock invocation) throws Throwable {
                    return Math.min (1, this.i++ / 2);
                }

            });
            PowerMockito.whenNew (AudioTrack.class).withParameterTypes (int.class, int.class, int.class, int.class, int.class, int.class)
                    .withArguments (Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class), Matchers.any (int.class)).thenReturn (audioTrack);
            final InputStream inputStream = this.generateRandomBytes ();
            final StreamInfo streamInfo = new StreamInfo (j, 100000, 2, 44100, false, true, null);
            final AndroidPlayObjectProcessor processor = new AndroidPlayObjectProcessor ();
            final Object o = processor.play (inputStream, streamInfo, null, 0);
            boolean waited = false;
            synchronized (o) {
                while (!waited) {
                    waited = true;
                    o.wait ();
                }
            }
            Mockito.verify (audioTrack, Mockito.times (5)).getPlaybackHeadPosition ();
        }
    }

    private InputStream generateRandomBytes () {
        final RandomDataGenerator rdg = new RandomDataGenerator ();
        final byte [] data = new byte [65536];
        for (int i = 0 ; i < data.length ; i++) {
            data [i] = (byte) rdg.nextInt (Byte.MIN_VALUE, Byte.MAX_VALUE);
        }
        return new ByteArrayInputStream (data);
    }
}
