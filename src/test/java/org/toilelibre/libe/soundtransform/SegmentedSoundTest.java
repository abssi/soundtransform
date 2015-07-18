package org.toilelibre.libe.soundtransform;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.toilelibre.libe.soundtransform.actions.fluent.FluentClient;
import org.toilelibre.libe.soundtransform.model.converted.sound.Channel;
import org.toilelibre.libe.soundtransform.model.converted.sound.SegmentedChannel;
import org.toilelibre.libe.soundtransform.model.converted.sound.SegmentedSound;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class SegmentedSoundTest {

    @Test
    public void aSegmentedSoundAppendsSeveralSoundsInOne () throws SoundTransformException {
        Sound sound1 = FluentClient.start ().withClasspathResource ("piano1c.wav").convertIntoSound ().stopWithSound ();
        Sound sound2 = FluentClient.start ().withClasspathResource ("piano2d.wav").convertIntoSound ().stopWithSound ();
        Sound sound12 = new SegmentedSound (sound1.getFormatInfo (), Arrays.asList (sound1, sound2));
        long [] sound12array = new long [sound1.getSamplesLength () + sound2.getSamplesLength ()];
        sound12.getChannels () [0].copyTo (sound12array);
        Sound sound12Bis = new Sound (new Channel [] {new Channel (sound12array, sound1.getFormatInfo (), 0)});
        Assert.assertEquals (sound1.getChannels () [0].viewSamplesArray ().replace ("]",
                             ", " + sound2.getChannels () [0].viewSamplesArray ().substring (1)
                ), sound12Bis.getChannels () [0].viewSamplesArray ());
    }
    
    @Test
    public void aSegmentedCannotBeDisplayed () throws SoundTransformException {
        Sound sound1 = FluentClient.start ().withClasspathResource ("piano1c.wav").convertIntoSound ().stopWithSound ();
        Sound sound2 = FluentClient.start ().withClasspathResource ("piano2d.wav").convertIntoSound ().stopWithSound ();
        Sound sound12 = new SegmentedSound (sound1.getFormatInfo (), Arrays.asList (sound1, sound2));
        
        Assert.assertEquals (sound12.getChannels () [0].viewSamplesArray (), SegmentedChannel.THIS_CHANNEL_IS_SEGMENTED_AND_CANNOT_BE_DISPLAYED);
        Assert.assertEquals (sound12.getChannels () [0].toString (), SegmentedChannel.THIS_CHANNEL_IS_SEGMENTED_AND_CANNOT_BE_DISPLAYED);
    }
    
    @Test
    public void aSegmentedSoundCopiesAsAnAppendedSound () throws SoundTransformException {
        Sound sound1 = FluentClient.start ().withClasspathResource ("piano1c.wav").convertIntoSound ().stopWithSound ();
        Sound sound2 = FluentClient.start ().withClasspathResource ("piano2d.wav").convertIntoSound ().stopWithSound ();
        Sound sound12 = new SegmentedSound (sound1.getFormatInfo (), Arrays.asList (sound1, sound2));

        long [] sound12array = new long [sound1.getSamplesLength () + sound2.getSamplesLength ()];
        Sound sound12Bis = new Sound (new Channel [] {new Channel (sound12array, sound1.getFormatInfo (), 0)});
        sound12.getChannels () [0].copyTo (sound12Bis.getChannels () [0]);
        
        Assert.assertEquals (sound1.getChannels () [0].viewSamplesArray ().replace ("]",
                             ", " + sound2.getChannels () [0].viewSamplesArray ().substring (1)
                ), sound12Bis.getChannels () [0].viewSamplesArray ());
    }
}
