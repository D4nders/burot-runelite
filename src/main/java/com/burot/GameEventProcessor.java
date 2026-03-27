package com.burot;

import net.runelite.api.events.ChatMessage;

import java.util.List;

public abstract class GameEventProcessor {

    private final List<Notifier> registeredNotifiers;

    public GameEventProcessor(List<Notifier> registeredNotifiers) {
        this.registeredNotifiers = registeredNotifiers;
    }

    public abstract String retrieveProcessorName();

    public abstract void simulateEventExecution(String activePlayerName);

    public abstract void evaluateIncomingEvent(ChatMessage incomingChatMessage, String activePlayerName);

    protected void triggerAllNotifiers(String notificationText, String associatedSoundPath) {
        for (Notifier currentNotifier : registeredNotifiers) {
            currentNotifier.dispatchNotification(notificationText, associatedSoundPath);
        }
    }
}