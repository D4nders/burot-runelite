package com.burot;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class AudioNotifier implements Notifier {

    @Override
    public void dispatchNotification(String formattedEventText, AudioSource targetAudioSource, byte[] generatedImageData) {
        if (targetAudioSource == null) {
            return;
        }

        try {
            AudioInputStream activeAudioStream = targetAudioSource.retrieveAudioStream();

            if (activeAudioStream != null) {
                Clip audioPlaybackClip = AudioSystem.getClip();
                audioPlaybackClip.open(activeAudioStream);
                audioPlaybackClip.start();
            }
        } catch (Exception ignoredException) {
        }
    }
}