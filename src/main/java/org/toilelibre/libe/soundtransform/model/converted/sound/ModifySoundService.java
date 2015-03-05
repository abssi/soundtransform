package org.toilelibre.libe.soundtransform.model.converted.sound;

import org.toilelibre.libe.soundtransform.model.converted.FormatInfo;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;

public class ModifySoundService {
    enum ModifySoundServiceErrorCode implements ErrorCode {
        DIFFERENT_NUMBER_OF_CHANNELS ("Could not append two sounds : Different number of channels (%1d and %2d)");

        private String messageFormat;

        ModifySoundServiceErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }

    }

    private final SoundAppender soundAppender;

    public ModifySoundService (final SoundAppender soundAppender1) {
        this.soundAppender = soundAppender1;
    }

    public Sound [] append (final Sound [] sounds1, final Sound [] sounds2) throws SoundTransformException {
        if (sounds1.length != sounds2.length) {
            throw new SoundTransformException (ModifySoundServiceErrorCode.DIFFERENT_NUMBER_OF_CHANNELS, new IllegalArgumentException (), sounds1.length, sounds2.length);
        }
        final Sound [] result = new Sound [sounds1.length];

        for (int i = 0 ; i < sounds1.length ; i++) {
            result [i] = this.soundAppender.append (sounds1 [i], sounds2 [i]);
        }
        return result;
    }

    public Sound [] changeFormat (final Sound [] input, final FormatInfo formatInfo) {
        return this.changeFormat (input, formatInfo.getSampleSize (), (int) formatInfo.getSampleRate ());
    }

    private Sound [] changeFormat (final Sound [] input, final int sampleSize, final int sampleRate) {
        final Sound [] result = new Sound [input.length];
        for (int i = 0 ; i < input.length ; i++) {
            result [i] = input [i];
            result [i] = this.soundAppender.changeNbBytesPerSample (result [i], sampleSize);
            result [i] = this.soundAppender.resizeToSampleRate (result [i], sampleRate);
        }
        return result;
    }

}
