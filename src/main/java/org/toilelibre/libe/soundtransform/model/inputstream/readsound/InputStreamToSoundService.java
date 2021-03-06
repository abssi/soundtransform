package org.toilelibre.libe.soundtransform.model.inputstream.readsound;

import java.io.InputStream;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.StreamInfo;
import org.toilelibre.libe.soundtransform.model.logging.EventCode;
import org.toilelibre.libe.soundtransform.model.logging.LogAware;
import org.toilelibre.libe.soundtransform.model.logging.LogEvent.LogLevel;

public interface InputStreamToSoundService<T> extends LogAware<T> {
    public enum TransformInputStreamServiceEventCode implements EventCode {
        CONVERT_INTO_JAVA_OBJECT (LogLevel.INFO, "Converting input into java object"), CONVERT_DONE (LogLevel.INFO, "Done converting the input stream");

        private final String   messageFormat;
        private final LogLevel logLevel;

        TransformInputStreamServiceEventCode (final LogLevel ll, final String mF) {
            this.logLevel = ll;
            this.messageFormat = mF;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.toilelibre.libe.soundtransform.model.inputstream.InputStreamToSound
         * #getLevel()
         */
        @Override
        public LogLevel getLevel () {
            return this.logLevel;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.toilelibre.libe.soundtransform.model.inputstream.InputStreamToSound
         * #getMessageFormat()
         */
        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    Sound fromInputStream (InputStream ais) throws SoundTransformException;

    Sound fromInputStream (InputStream ais, StreamInfo isInfo) throws SoundTransformException;

    StreamInfo getStreamInfo (InputStream ais) throws SoundTransformException;

}