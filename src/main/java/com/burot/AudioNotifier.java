package com.burot;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class AudioNotifier implements Notifier {

    @Override
    public void dispatchNotification(String formattedEventText, String targetSoundFilePath, byte[] generatedImageData) {
        if (targetSoundFilePath == null || targetSoundFilePath.isEmpty()) {
            return;
        }

        try {
            File targetAudioFile = new File(targetSoundFilePath);

            if (targetAudioFile.exists()) {
                AudioInputStream activeAudioStream = AudioSystem.getAudioInputStream(targetAudioFile);
                Clip audioPlaybackClip = AudioSystem.getClip();

                audioPlaybackClip.open(activeAudioStream);
                audioPlaybackClip.start();
            }
        } catch (Exception ignoredException) {
        }
    }
}