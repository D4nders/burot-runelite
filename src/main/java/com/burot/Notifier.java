package com.burot;

public interface Notifier {
    void dispatchNotification(String formattedEventText, String targetSoundFilePath);
}
