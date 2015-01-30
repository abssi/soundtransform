package org.toilelibre.libe.soundtransform.infrastructure.service.sound2note;

import org.toilelibre.libe.soundtransform.ioc.ApplicationInjector.$;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.transform.PeakFindWithHPSSoundTransformation;
import org.toilelibre.libe.soundtransform.model.library.note.FrequencyHelper;

public class CallHPSFrequencyHelper implements FrequencyHelper {

    @Override
    public int findFrequency (final Sound channel1) {
        double sum = 0;
        int nb = 0;

        final PeakFindWithHPSSoundTransformation<?> peak = $.create (PeakFindWithHPSSoundTransformation.class, true);
        peak.transform (channel1);
        final int [] magnitude = peak.getLoudestFreqs ();

        for (final int element : magnitude) {
            sum += element;
            nb++;
        }
        return (int) (sum / nb);
    }
}
