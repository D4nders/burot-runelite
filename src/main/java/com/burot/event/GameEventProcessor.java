package com.burot.event;

import com.burot.notifier.Notifier;
import com.burot.audio.AudioSource;
import com.burot.render.ChatSegment;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public abstract class GameEventProcessor {

    private final List<Notifier> registeredNotifiers;

    public GameEventProcessor(List<Notifier> registeredNotifiers) {
        this.registeredNotifiers = registeredNotifiers;
    }

    public abstract String retrieveProcessorName();

    public abstract void simulateEventExecution(String activePlayerName, String activeClanName);

    protected abstract boolean isEventEnabled();

    protected abstract void processSanitizedMessage(String sanitizedMessageContent, String activePlayerName, String activeClanName, int currentTick);

    protected boolean canProcessEvent(int currentTick) {
        return true;
    }

    public void evaluateIncomingEvent(ChatMessage incomingChatMessage, String activePlayerName, String activeClanName, int currentTick) {
        if (!isEventEnabled() || !canProcessEvent(currentTick)) {
            return;
        }

        ChatMessageType incomingMessageType = incomingChatMessage.getType();
        if (incomingMessageType != ChatMessageType.GAMEMESSAGE && incomingMessageType != ChatMessageType.SPAM) {
            return;
        }

        String sanitizedMessageContent = incomingChatMessage.getMessage().replaceAll("<[^>]+>", "");
        processSanitizedMessage(sanitizedMessageContent, activePlayerName, activeClanName, currentTick);
    }

    protected List<ChatSegment> buildPlayerClanPrefixSegments(String activePlayerName, String activeClanName) {
        List<ChatSegment> prefixSegments = new ArrayList<>();

        if (activeClanName != null && !activeClanName.isEmpty()) {
            prefixSegments.add(new ChatSegment("[" + activeClanName + "] ", Color.BLUE));
        }

        prefixSegments.add(new ChatSegment(activePlayerName + " ", Color.BLACK));

        return prefixSegments;
    }

    protected void triggerAllNotifiers(String notificationText, AudioSource eventAudioSource, byte[] generatedImageData) {
        for (Notifier currentNotifier : registeredNotifiers) {
            currentNotifier.dispatchNotification(notificationText, eventAudioSource, generatedImageData);
        }
    }
}