package com.burot.notifier;

import com.burot.audio.AudioSource;
import net.runelite.client.audio.AudioPlayer;

import java.io.InputStream;

public class AudioNotifier implements Notifier {

    private final AudioPlayer audioPlayer;

    public AudioNotifier(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }

    @Override
    public void dispatchNotification(String formattedEventText, AudioSource targetAudioSource, byte[] generatedImageData) {
        if (targetAudioSource == null || audioPlayer == null) {
            return;
        }

        try {
            InputStream activeAudioStream = targetAudioSource.retrieveAudioStream();

            if (activeAudioStream != null) {
                audioPlayer.play(activeAudioStream, 0f);
            }
        } catch (Exception ignoredException) {
        }
    }
}