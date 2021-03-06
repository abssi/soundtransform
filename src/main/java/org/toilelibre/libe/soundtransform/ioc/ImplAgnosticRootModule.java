package org.toilelibre.libe.soundtransform.ioc;

import java.util.Map.Entry;

import org.toilelibre.libe.soundtransform.model.converted.sound.SoundAppender;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundPitchAndTempoHelper;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundToStringHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.FourierTransformHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumToCepstrumHelper;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.SpectrumToStringHelper;
import org.toilelibre.libe.soundtransform.model.freqs.AdjustFrequenciesProcessor;
import org.toilelibre.libe.soundtransform.model.freqs.ChangeOctaveProcessor;
import org.toilelibre.libe.soundtransform.model.freqs.CompressFrequenciesProcessor;
import org.toilelibre.libe.soundtransform.model.freqs.FilterFrequenciesProcessor;
import org.toilelibre.libe.soundtransform.model.freqs.ReplaceFrequenciesProcessor;
import org.toilelibre.libe.soundtransform.model.freqs.SurroundInRangeProcessor;
import org.toilelibre.libe.soundtransform.model.inputstream.AudioFileHelper;
import org.toilelibre.libe.soundtransform.model.inputstream.convert.ConvertProcessor;
import org.toilelibre.libe.soundtransform.model.inputstream.convert.ConverterLauncher;
import org.toilelibre.libe.soundtransform.model.inputstream.format.AudioFormatParser;
import org.toilelibre.libe.soundtransform.model.inputstream.readsound.FrameProcessor;
import org.toilelibre.libe.soundtransform.model.inputstream.readsound.InputStreamToByteArrayHelper;
import org.toilelibre.libe.soundtransform.model.library.Library;
import org.toilelibre.libe.soundtransform.model.library.pack.ContextLoader;
import org.toilelibre.libe.soundtransform.model.library.pack.PackConfigParser;
import org.toilelibre.libe.soundtransform.model.library.pack.PackToStringHelper;
import org.toilelibre.libe.soundtransform.model.library.pack.note.ADSRHelper;
import org.toilelibre.libe.soundtransform.model.library.pack.note.FrequencyHelper;
import org.toilelibre.libe.soundtransform.model.play.PlayObjectProcessor;
import org.toilelibre.libe.soundtransform.model.record.RecordSoundProcessor;
import org.toilelibre.libe.soundtransform.model.record.exporter.OutputAsByteArrayOutputStream;
import org.toilelibre.libe.soundtransform.model.record.exporter.OutputAsByteBuffer;

abstract class ImplAgnosticRootModule extends ImplAgnosticFinalAccessor {

    @Override
    protected void declare () {
        super.bind (RecordSoundProcessor.class).to (this.provideRecordSoundProcessor ());
        super.bind (PlayObjectProcessor.class).to (this.providePlaySoundProcessor ());
        super.bind (AudioFileHelper.class).to (this.provideAudioFileHelper ());
        super.bind (ConverterLauncher.class).to (this.provideConverterLauncher ());
        super.bind (ConvertProcessor.class).to (this.provideConvertProcessor ());
        super.bind (AudioFormatParser.class).to (this.provideAudioFormatParser ());
        super.bind (ContextLoader.class).to (this.provideContextLoader ());

        super.bind (InputStreamToByteArrayHelper.class).to (this.provideInputStreamToByteArrayHelper ());
        super.bind (SoundToStringHelper.class).to (this.provideSound2StringHelper ());
        super.bind (PackToStringHelper.class).to (this.providePack2StringHelper ());
        super.bind (SoundAppender.class).to (this.provideSoundAppender ());
        super.bind (SoundPitchAndTempoHelper.class).to (this.provideSoundPitchAndTempoHelper ());
        super.bind (FourierTransformHelper.class).to (this.provideFourierTransformHelper ());
        super.bind (SpectrumToCepstrumHelper.class).to (this.provideSpectrum2CepstrumHelper ());
        super.bind (SpectrumHelper.class).to (this.provideSpectrumHelper ());
        super.bind (SpectrumToStringHelper.class).to (this.provideSpectrumToStringHelper ());
        super.bind (FrameProcessor.class).to (this.provideFrameProcessor ());
        super.bind (ADSRHelper.class).to (this.provideAdsrHelper ());
        super.bind (FrequencyHelper.class).to (this.provideFrequencyHelper ());
        super.bind (PackConfigParser.class).to (this.providePackConfigParser ());
        super.bind (ChangeOctaveProcessor.class).to (this.provideChangeOctaveProcessor ());
        super.bind (AdjustFrequenciesProcessor.class).to (this.provideAdjustFrequenciesProcessor ());
        super.bind (FilterFrequenciesProcessor.class).to (this.provideFilterFrequenciesProcessor ());
        super.bind (ReplaceFrequenciesProcessor.class).to (this.provideReplaceFrequenciesProcessor ());
        super.bind (CompressFrequenciesProcessor.class).to (this.provideCompressFrequenciesProcessor ());
        super.bind (SurroundInRangeProcessor.class).to (this.provideSurroundInRangeFrequenciesProcessor ());

        super.bind (OutputAsByteArrayOutputStream.class).to (this.providerByteArrayOutputStreamExporter ());
        super.bind (OutputAsByteBuffer.class).to (this.providerByteBufferOutputStreamExporter ());

        for (final Entry<Class<? extends Object>, Class<? extends Object>> serviceClassEntry : this.usedImpls.entrySet ()) {
            @SuppressWarnings ("unchecked")
            final TypedBinder<Object> objectTypedBinder = (TypedBinder<Object>) super.bind (serviceClassEntry.getKey ());
            objectTypedBinder.to (serviceClassEntry.getValue ());
        }

        super.bind (Library.class).to (this.provideLibrary ());

    }

    protected abstract AudioFileHelper provideAudioFileHelper ();

    protected abstract AudioFormatParser provideAudioFormatParser ();

    protected abstract ConvertProcessor provideConvertProcessor ();

    protected abstract ContextLoader provideContextLoader ();

    protected abstract PlayObjectProcessor providePlaySoundProcessor ();

    protected abstract RecordSoundProcessor provideRecordSoundProcessor ();

    private Library provideLibrary () {
        return new Library ();
    }

}
