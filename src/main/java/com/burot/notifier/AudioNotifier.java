package com.burot.notifier;

import com.burot.audio.AudioSource;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class AudioNotifier implements Notifier {

    private Clip activePlaybackClip;

    @Override
    public void dispatchNotification(String formattedEventText, AudioSource targetAudioSource, byte[] generatedImageData) {
        if (targetAudioSource == null) {
            return;
        }

        try {
            AudioInputStream activeAudioStream = targetAudioSource.retrieveAudioStream();

            if (activeAudioStream != null) {
                if (activePlaybackClip != null) {
                    if (activePlaybackClip.isRunning()) {
                        activePlaybackClip.stop();
                    }
                    activePlaybackClip.close();
                }

                activePlaybackClip = AudioSystem.getClip();
                activePlaybackClip.open(activeAudioStream);
                activePlaybackClip.start();
            }
        } catch (Exception ignoredException) {
        }
    }
}