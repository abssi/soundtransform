package org.toilelibre.libe.soundtransform.model.library.note;

import java.util.HashMap;
import java.util.Map;

import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.exception.SoundTransformException;
import org.toilelibre.libe.soundtransform.model.library.pack.SimpleNoteInfo;

final class DefaultSoundToNoteService implements SoundToNoteService {

    private final ADSRHelper      adsrHelper;

    private final FrequencyHelper frequencyHelper;

    public DefaultSoundToNoteService (final ADSRHelper helper1, final FrequencyHelper helper2) {
        this.adsrHelper = helper1;
        this.frequencyHelper = helper2;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.toilelibre.libe.soundtransform.model.library.note.Sound2NoteService
     * #convert
     * (org.toilelibre.libe.soundtransform.model.library.pack.SimpleNoteInfo,
     * org.toilelibre.libe.soundtransform.model.converted.sound.Sound[])
     */
    @Override
    public Note convert (final SimpleNoteInfo noteInfo, final Sound [] channels) throws SoundTransformException {
        final Sound channel1 = channels [0];

        final Map<String, Object> noteInfoValues = new HashMap<String, Object> ();

        noteInfoValues.put (SimpleNoteInfo.ATTACK_KEY, noteInfo.hasAttack () ? noteInfo.getAttack () : 0);
        noteInfoValues.put (SimpleNoteInfo.DECAY_KEY, noteInfo.hasDecay () ? noteInfo.getDecay () : this.adsrHelper.findDecay (channel1, ((Integer) noteInfoValues.get (SimpleNoteInfo.ATTACK_KEY)).intValue ()));
        noteInfoValues.put (SimpleNoteInfo.SUSTAIN_KEY, noteInfo.hasSustain () ? noteInfo.getSustain () : this.adsrHelper.findSustain (channel1, ((Integer) noteInfoValues.get (SimpleNoteInfo.DECAY_KEY)).intValue ()));
        noteInfoValues.put (SimpleNoteInfo.RELEASE_KEY, noteInfo.hasRelease () ? noteInfo.getRelease () : this.adsrHelper.findRelease (channel1));
        noteInfoValues.put (SimpleNoteInfo.FREQUENCY_KEY, noteInfo.hasFrequency () ? noteInfo.getFrequency () : this.frequencyHelper.findFrequency (channels));
        noteInfoValues.put (SimpleNoteInfo.NAME_KEY, noteInfo.getName ());

        return new SimpleNote (new SimpleNoteInfo (noteInfoValues), channels);

    }
}
