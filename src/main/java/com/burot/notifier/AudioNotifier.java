package com.burot.notifier;

import com.burot.BurotConfig;
import com.burot.audio.AudioSource;
import net.runelite.client.audio.AudioPlayer;

import java.io.InputStream;

public class AudioNotifier implements Notifier {

    private final AudioPlayer audioPlayer;
    private final BurotConfig pluginConfiguration;

    public AudioNotifier(AudioPlayer audioPlayer, BurotConfig pluginConfiguration) {
        this.audioPlayer = audioPlayer;
        this.pluginConfiguration = pluginConfiguration;
    }

    @Override
    public void dispatchNotification(String formattedEventText, AudioSource targetAudioSource, byte[] generatedImageData) {
        if (targetAudioSource == null || audioPlayer == null) {
            return;
        }

        int currentVolume = pluginConfiguration.notificationVolume();
        if (currentVolume <= 0) {
            return;
        }

        float calculatedGain = 20f * (float) Math.log10(currentVolume / 100f);

        try {
            InputStream activeAudioStream = targetAudioSource.retrieveAudioStream();

            if (activeAudioStream != null) {
                audioPlayer.play(activeAudioStream, calculatedGain);
            }
        } catch (Exception ignoredException) {
        }
    }
}