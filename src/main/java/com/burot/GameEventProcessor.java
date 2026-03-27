package com.burot;

import net.runelite.api.events.ChatMessage;
import java.util.List;

public abstract class GameEventProcessor {

    private final List<Notifier> registeredNotifiers;

    public GameEventProcessor(List<Notifier> registeredNotifiers) {
        this.registeredNotifiers = registeredNotifiers;
    }

    public abstract String retrieveProcessorName();

    public abstract void simulateEventExecution(String activePlayerName, String activeClanName);

    public abstract void evaluateIncomingEvent(ChatMessage incomingChatMessage, String activePlayerName, String activeClanName);

    protected void triggerAllNotifiers(String notificationText, AudioSource eventAudioSource, byte[] generatedImageData) {
        for (Notifier currentNotifier : registeredNotifiers) {
            currentNotifier.dispatchNotification(notificationText, eventAudioSource, generatedImageData);
        }
    }
}