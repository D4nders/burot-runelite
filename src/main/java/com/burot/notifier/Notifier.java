package com.burot.notifier;

import com.burot.audio.AudioSource;

public interface Notifier {
    void dispatchNotification(String formattedEventText, AudioSource targetAudioSource, byte[] generatedImageData);
}