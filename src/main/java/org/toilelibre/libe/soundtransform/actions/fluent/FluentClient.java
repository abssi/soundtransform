package org.toilelibre.libe.soundtransform.actions.fluent;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.toilelibre.libe.soundtransform.actions.notes.ImportAPackIntoTheLibrary;
import org.toilelibre.libe.soundtransform.actions.play.PlaySound;
import org.toilelibre.libe.soundtransform.actions.transform.ApplySoundTransform;
import org.toilelibre.libe.soundtransform.actions.transform.ConvertFromInputStream;
import org.toilelibre.libe.soundtransform.actions.transform.ExportAFile;
import org.toilelibre.libe.soundtransform.actions.transform.GetInputStreamInfo;
import org.toilelibre.libe.soundtransform.actions.transform.InputStreamToAudioInputStream;
import org.toilelibre.libe.soundtransform.actions.transform.ToInputStream;
import org.toilelibre.libe.soundtransform.model.converted.SoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.PeakFindWithHPSSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.ShapeSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SoundToSpectrumsSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.SpectrumsToSoundSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.spectrum.Spectrum;
import org.toilelibre.libe.soundtransform.model.exception.ErrorCode;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.inputstream.InputStreamInfo;

public class FluentClient implements FluentClientSoundImported, FluentClientReady, FluentClientWithInputStream, FluentClientWithFile, FluentClientWithFreqs, FluentClientWithSpectrums {
    public enum FluentClientErrorCode implements ErrorCode {

        INPUT_STREAM_NOT_READY ("Input Stream not ready"), INPUT_STREAM_INFO_UNAVAILABLE ("Input Stream info not available"), NOTHING_TO_WRITE ("Nothing to write to a File"), NO_FILE_IN_INPUT ("No file in input"), CLIENT_NOT_STARTED_WITH_A_CLASSPATH_RESOURCE (
                "This client did not read a classpath resouce at the start"), NO_SPECTRUM_IN_INPUT ("No spectrum in input");

        private final String messageFormat;

        FluentClientErrorCode (final String mF) {
            this.messageFormat = mF;
        }

        @Override
        public String getMessageFormat () {
            return this.messageFormat;
        }
    }

    /**
     * Startup the client
     * 
     * @return the client, ready to start
     */
    public static FluentClientReady start () {
        return new FluentClient ();
    }

    private Sound []             sounds;
    private InputStream          audioInputStream;
    private String               sameDirectoryAsClasspathResource;
    private int []               freqs;
    private File                 file;
    private List<Spectrum<?> []> spectrums;

    private FluentClient () {

    }

    @Override
    /**
     * Start over the client : reset the state and the value objects nested in the client
     * @return the client, ready to start
     */
    public FluentClientReady andAfterStart () throws SoundTransformException {
        this.cleanData ();
        return this;
    }

    @Override
    /**
     * Apply one transform and continue with the current imported sound
     * @param st the SoundTransformation to apply
     * @return the client with a sound imported 
     * @throws SoundTransformException if the transform does not work
     */
    public FluentClientSoundImported apply (final SoundTransformation st) throws SoundTransformException {
        final Sound sounds1 [] = new ApplySoundTransform ().apply (this.sounds, st);
        this.cleanData ();
        this.sounds = sounds1;
        return this;
    }

    private void cleanData () {
        this.sounds = null;
        this.audioInputStream = null;
        this.file = null;
        this.freqs = null;
        this.spectrums = null;
    }

    @Override
    /**
     * Shortcut for importToStream ().importToSound () : Conversion from a File to a Sound
     * @return the client, with a sound imported      
     * @throws SoundTransformException if one of the two import fails
     */
    public FluentClientSoundImported convertIntoSound () throws SoundTransformException {
        return this.importToStream ().importToSound ();
    }

    @Override
    /**
     * Shortcut for exportToStream ().writeToClasspathResource (resource) : Conversion from a Sound to a File
     * @param resource a resource that can be found in the classpath
     * @return the client, with a file written 
     * @throws SoundTransformException if one of the two operations fails
     */
    public FluentClientWithFile exportToClasspathResource (final String resource) throws SoundTransformException {
        return this.exportToStream ().writeToClasspathResource (resource);
    }

    @Override
    /**
     * Shortcut for exportToStream ().writeToClasspathResourceWithSiblingResource (resource, siblingResource)
     * @param resource a resource that may or may not exist in the classpath
     * @param siblingResource a resource that can be found in the classpath.
     * @return the client, with a file written 
     * @throws SoundTransformException if one of the two operations fails
     */
    public FluentClientWithFile exportToClasspathResourceWithSiblingResource (final String resource, final String siblingResource) throws SoundTransformException {
        return this.exportToStream ().writeToClasspathResourceWithSiblingResource (resource, siblingResource);
    }

    @Override
    /**
     * Shortcut for exportToStream ().writeToFile (file)
     * @param file1 the destination file
     * @return the client, with a file written 
     * @throws SoundTransformException if one of the two operations fails
     */
    public FluentClientWithFile exportToFile (final File file1) throws SoundTransformException {
        return this.exportToStream ().writeToFile (file1);
    }

    @Override
    /**
     * Uses the current imported sound and converts it into an InputStream, ready to be written to a file (or to be read again)
     * @return the client, with an inputStream
     * @throws SoundTransformException if the metadata format object is invalid, or if the sound cannot be converted
     */
    public FluentClientWithInputStream exportToStream () throws SoundTransformException {
        final InputStreamInfo currentInfo = new GetInputStreamInfo ().getInputStreamInfo (this.sounds);
        if (currentInfo == null) {
            throw new SoundTransformException (FluentClientErrorCode.INPUT_STREAM_INFO_UNAVAILABLE, new NullPointerException ());
        }
        final InputStream audioInputStream1 = new ToInputStream ().toStream (this.sounds, currentInfo);
        this.cleanData ();
        this.audioInputStream = audioInputStream1;
        return this;
    }

    @Override
    /**
     * Uses the current available spectrums objects to convert them into a sound (with one or more channels)
     * @return the client, with a sound imported 
     * @throws SoundTransformException if the spectrums are in an invalid format, or if the transform to sound does not work
     */
    public FluentClientSoundImported extractSound () throws SoundTransformException {
        if ((this.spectrums == null) || (this.spectrums.size () == 0) || this.spectrums.get (0).length == 0) {
            throw new SoundTransformException (FluentClientErrorCode.NO_SPECTRUM_IN_INPUT, new IllegalArgumentException ());
        }
        final Sound [] input = new Sound [this.spectrums.size ()];
        for (int i = 0 ; i < input.length ; i++) {
            input [i] = new Sound (null, this.spectrums.get (0) [0].getNbBytes (), this.spectrums.get (0) [0].getSampleRate (), i);
        }
        final Sound [] sounds1 = new ApplySoundTransform ().apply (input, new SpectrumsToSoundSoundTransformation (this.spectrums));
        this.cleanData ();
        this.sounds = sounds1;
        return this;
    }

    @Override
    /**
     * Uses the current input stream object to convert it into a sound (with one or more channels)
     * @return the client, with a sound imported 
     * @throws SoundTransformException the inputStream is invalid, or the convert did not work
     */
    public FluentClientSoundImported importToSound () throws SoundTransformException {
        Sound [] sounds1;
        if (this.audioInputStream != null) {
            sounds1 = new ConvertFromInputStream ().fromInputStream (this.audioInputStream);
        } else {
            throw new SoundTransformException (FluentClientErrorCode.INPUT_STREAM_NOT_READY, new NullPointerException ());
        }
        this.cleanData ();
        this.sounds = sounds1;
        return this;
    }

    @Override
    /**
     * Opens the current file and convert it into an InputStream, ready to be read (or to be written to a file)
     * @return the client, with an inputStream
     * @throws SoundTransformException the current file is not valid, or the conversion did not work
     */
    public FluentClientWithInputStream importToStream () throws SoundTransformException {
        if (this.file == null) {
            throw new SoundTransformException (FluentClientErrorCode.NO_FILE_IN_INPUT, new NullPointerException ());
        }
        final InputStream inputStream = new ToInputStream ().toStream (this.file);
        this.cleanData ();
        this.audioInputStream = inputStream;
        return this;
    }

    @Override
    /**
     * Plays the current audio data and (if needed) convert it temporarily to a sound
     * @return the client, in its current state.
     * @throws SoundTransformException could not play the current audio data
     */
    public FluentClient playIt () throws SoundTransformException {
        if (this.sounds != null) {
            new PlaySound ().play (this.sounds);
        } else if (this.audioInputStream != null) {
            new PlaySound ().play (this.audioInputStream);
        } else if (this.spectrums != null) {
            final List<Spectrum<?> []> savedSpectrums = this.spectrums;
            this.extractSound ();
            new PlaySound ().play (this.sounds);
            this.cleanData ();
            this.spectrums = savedSpectrums;
        } else if (this.file != null) {
            final File f = this.file;
            this.importToStream ();
            new PlaySound ().play (this.audioInputStream);
            this.cleanData ();
            this.file = f;
        }
        return this;
    }

    @Override
    /**
     * Shapes these loudest frequencies array into a sound and set the converted sound in the pipeline
     * @param packName reference to an existing imported pack (must be invoked before the shapeIntoSound method by using withAPack)
     * @param instrumentName the name of the instrument that will map the freqs object
     * @param isi the wanted format for the future sound
     * @return the client, with a sound imported 
     * @throws SoundTransformException could not call the soundtransform to shape the freqs
     */
    public FluentClientSoundImported shapeIntoSound (final String packName, final String instrumentName, final InputStreamInfo isi) throws SoundTransformException {
        final SoundTransformation soundTransformation = new ShapeSoundTransformation (packName, instrumentName, this.freqs, (int) isi.getFrameLength (), isi.getSampleSize (), (int) isi.getSampleRate ());
        this.cleanData ();
        this.sounds = new ApplySoundTransform ().apply (new Sound [] { new Sound (new long [0], 0, 0, 0) }, soundTransformation);
        return this;
    }

    @Override
    /**
     * Uses the current sound to pick its spectrums and set that as the current data in the pipeline
     * @return the client, with the spectrums
     * @throws SoundTransformException could not convert the sound into some spectrums
     */
    public FluentClientWithSpectrums splitIntoSpectrums () throws SoundTransformException {
        final SoundToSpectrumsSoundTransformation<?> sound2Spectrums = new SoundToSpectrumsSoundTransformation<Object> ();
        new ApplySoundTransform ().apply (this.sounds, sound2Spectrums);
        this.cleanData ();
        this.spectrums = sound2Spectrums.getSpectrums ();
        return this;
    }

    @Override
    /**
     * Stops the client pipeline and returns the obtained file
     * @return a file
     */
    public File stopWithFile () {
        return this.file;
    }

    @Override
    /**
     * Stops the client pipeline and returns the obtained loudest frequencies
     * @return loudest frequencies array
     */
    public int [] stopWithFreqs () {
        return this.freqs;
    }

    @Override
    /**
     * Stops the client pipeline and returns the obtained input stream
     * @return an input stream
     */
    public InputStream stopWithInputStream () {
        return this.audioInputStream;
    }

    @Override
    /**
     * Stops the client pipeline and returns the obtained sound
     * @return a sound value object
     */
    public Sound [] stopWithSounds () {
        return this.sounds;
    }

    @Override
    /**
     * Stops the client pipeline and returns the obtained spectrums
     * @return a list of spectrums for each channel
     */
    public List<Spectrum<?> []> stopWithSpectrums () {
        return this.spectrums;
    }

    @Override
    /**
     * Tells the client to work with a pack. Reads the whole inputStream. A pattern must be followed in the jsonStream to
     * enable the import.
     * @param packName the name of the pack
     * @param jsonStream the input stream
     * @return the client, in its current state. 
     * @throws SoundTransformException the input stream cannot be read, or the json format is not correct, or some sound files are missing
     */
    public FluentClient withAPack (final String packName, final InputStream jsonStream) throws SoundTransformException {
        new ImportAPackIntoTheLibrary ().importAPack (packName, jsonStream);
        return this;
    }

    @Override
    /**
     * Tells the client to work with a pack. Reads the whole inputStream. A pattern must be followed in the jsonStream to
     * enable the import.<br/>
     * Here is the format allowed in the file
     * <pre>
     * {
     *   "instrumentName" : 
     *   {
     *     -1 : "/data/mypackage.myapp/unknownFrequencyFile.wav",
     *    192 : "/data/mypackage.myapp/knownFrequencyFile.wav",
     *    ...
     *   },
     *   ...
     * }
     * </pre>
     * Do not assign the same frequency for two notes in the same instrument. If several notes must have their frequencies
     * detected by the soundtransform lib, set different negative values (-1, -2, -3, ...)
     * @param packName the name of the pack
     * @param jsonContent a string containing the definition of the pack
     * @return the client, in its current state. 
     * @throws SoundTransformException the json content is invalid, the json format is not correct, or some sound files are missing
     */
    public FluentClient withAPack (final String packName, final String jsonContent) throws SoundTransformException {
        new ImportAPackIntoTheLibrary ().importAPack (packName, jsonContent);
        return this;
    }

    @Override
    /**
     * Tells the client to work first with an InputStream. It will not be read yet<br/>
     * The passed inputStream must own a format metadata object. Therefore it must be an AudioInputStream.
     * @param ais the input stream
     * @return the client, with an input stream
     */
    public FluentClientWithInputStream withAudioInputStream (final InputStream ais) {
        this.cleanData ();
        this.audioInputStream = ais;
        return this;
    }

    @Override
    /**
     * Tells the client to work first with a classpath resource. It will be converted in a File
     * @param resource a classpath resource that must exist
     * @return the client, with a file
     * @throws SoundTransformException the classpath resource was not found
     */
    public FluentClientWithFile withClasspathResource (final String resource) throws SoundTransformException {
        this.cleanData ();
        this.file = new File (Thread.currentThread ().getContextClassLoader ().getResource (resource).getFile ());
        this.sameDirectoryAsClasspathResource = this.file.getParent ();
        return this;
    }

    @Override
    /**
     * Tells the client to work first with a file. It will not be read yet
     * @param file source file
     * @return the client, with a file
     */
    public FluentClientWithFile withFile (final File file1) {
        this.cleanData ();
        this.file = file1;
        return this;
    }

    @Override
    /**
     * Tells the client to work first with a loudest frequencies integer array. It will not be used yet
     * @param freqs1 the loudest frequencies integer array
     * @return the client, with a loudest frequencies integer array
     */
    public FluentClientWithFreqs withFreqs (final int [] freqs1) {
        this.cleanData ();
        this.freqs = freqs1;
        return this;
    }

    @Override
    /**
     * Tells the client to work first with a byte array InputStream or any readable DataInputStream. 
     * It will be read and transformed into an AudioInputStream<br/>
     * The passed inputStream must not contain any metadata piece of information.
     * @param is the input stream
     * @param isInfo the audio format (named "InputStreamInfo")
     * @return the client, with an input stream
     * @throws SoundTransformException the input stream cannot be read, or the conversion did not work
     */
    public FluentClientWithInputStream withRawInputStream (final InputStream is, final InputStreamInfo isInfo) throws SoundTransformException {
        this.cleanData ();
        this.audioInputStream = new InputStreamToAudioInputStream ().transformRawInputStream (is, isInfo);
        return this;
    }

    @Override
    /**
     * Tells the client to work first with a sound object
     * @param sounds1 the sound object
     * @return the client, with an imported sound
     */
    public FluentClientSoundImported withSounds (final Sound [] sounds1) {
        this.cleanData ();
        this.sounds = sounds1;
        return this;
    }

    @Override
    /**
     * Tells the client to work first with a spectrum formatted sound.<br/>
     * The spectrums inside must be in a list (each item must correspond to a channel)
     * The spectrums are ordered in an array in chronological order
     * @param spectrums the spectrums
     * @return the client, with the spectrums
     */
    public FluentClientWithSpectrums withSpectrums (final List<Spectrum<?> []> spectrums) {
        this.cleanData ();
        this.spectrums = spectrums;
        return this;
    }

    @Override
    /**
     * Writes the current InputStream in a classpath resource in the same folder as a previously imported classpath resource.
     * Caution : if no classpath resource was imported before, this operation will not work. Use writeToClasspathResourceWithSiblingResource instead
     * @param resource a classpath resource.
     * @return the client, with a file
     * @throws SoundTransformException there is no predefined classpathresource directory, or the file could not be written
     */
    public FluentClientWithFile writeToClasspathResource (final String resource) throws SoundTransformException {
        if (this.sameDirectoryAsClasspathResource == null) {
            throw new SoundTransformException (FluentClientErrorCode.CLIENT_NOT_STARTED_WITH_A_CLASSPATH_RESOURCE, new IllegalAccessException ());
        }
        return this.writeToFile (new File (this.sameDirectoryAsClasspathResource + "/" + resource));
    }

    @Override
    /**
     * Writes the current InputStream in a classpath resource in the same folder as a the sibling resource.
     * @param resource a classpath resource that may or may not exist yet
     * @param siblingResource a classpath resource that must exist
     * @return the client, with a file
     * @throws SoundTransformException no such sibling resource, or the file could not be written
     */
    public FluentClientWithFile writeToClasspathResourceWithSiblingResource (final String resource, final String siblingResource) throws SoundTransformException {
        final InputStream is = this.audioInputStream;
        this.withClasspathResource (siblingResource);
        this.cleanData ();
        this.audioInputStream = is;
        return this.writeToFile (new File (this.sameDirectoryAsClasspathResource + "/" + resource));
    }

    @Override
    /**
     * Writes the current InputStream in a file
     * @param file1 the destination file
     * @return the client, with a file
     * @throws SoundTransformException The file could not be written
     */
    public FluentClientWithFile writeToFile (final File file1) throws SoundTransformException {
        if (this.audioInputStream != null) {
            new ExportAFile ().writeFile (this.audioInputStream, file1);
        }
        this.cleanData ();
        this.file = file1;
        return this;
    }

    /**
     * Will invoke a soundtransform to find the loudest frequencies of the sound, chronologically
     * Caution : the origin sound will be lost, and it will be impossible to revert this conversion.
     * When shaped into a sound, the new sound will only sounds like the instrument you shaped the freqs with
     * 
     * @return the client, with a loudest frequencies integer array
     * @throws SoundTransformException
     *             if the convert fails
     */
    @Override
    public FluentClientWithFreqs findLoudestFrequencies () throws SoundTransformException {
        PeakFindWithHPSSoundTransformation<?> peakFind = new PeakFindWithHPSSoundTransformation<Object> (100);
        new ApplySoundTransform ().apply (this.sounds, peakFind);
        this.cleanData ();
        this.freqs = peakFind.getLoudestFreqs ();
        return this;
    }

}