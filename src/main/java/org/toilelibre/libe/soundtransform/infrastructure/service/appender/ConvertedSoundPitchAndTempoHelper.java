package org.toilelibre.libe.soundtransform.infrastructure.service.appender;

import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.PitchSoundTransformation;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.SlowdownSoundTransformation;
import org.toilelibre.libe.soundtransform.infrastructure.service.transforms.SpeedUpSoundTransformation;
import org.toilelibre.libe.soundtransform.model.converted.sound.Sound;
import org.toilelibre.libe.soundtransform.model.converted.sound.SoundPitchAndTempoHelper;

public class ConvertedSoundPitchAndTempoHelper implements SoundPitchAndTempoHelper {

	public Sound pitchAndSetLength (Sound sound, float percent, float lengthInSeconds) {

		Sound result = sound;

		PitchSoundTransformation pitcher = new PitchSoundTransformation (percent);
		if (percent < 98 || percent > 102) {
			result = pitcher.transform (result);
		}
		double factor = sound.getSamples ().length == 0 ? 0 : 1.0 * lengthInSeconds * sound.getSampleRate() / result.getSamples ().length;
		if (factor == 0) {
			return result;
		} else if (factor < 0.98 || factor > 1.02) {
			if (factor < 0.98) {
				SpeedUpSoundTransformation speedup = new SpeedUpSoundTransformation (100, (float) (1 / factor));
				result = speedup.transform (result);

			} else if (factor > 1.02) {
				SlowdownSoundTransformation slowdown = new SlowdownSoundTransformation (100, (float) factor);
				result = slowdown.transform (result);
			}
		}
		return result;
	}
}
