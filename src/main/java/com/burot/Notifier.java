package com.burot;

public interface Notifier {
    void dispatchNotification(String formattedEventText, AudioSource targetAudioSource, byte[] generatedImageData);
}