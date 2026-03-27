package com.burot.event;

import com.burot.BurotConfig;
import com.burot.SharedEventState;
import com.burot.audio.AudioSource;
import com.burot.audio.DisabledAudioSource;
import com.burot.audio.FileAudioSource;
import com.burot.audio.InternalAudioSource;
import com.burot.notifier.Notifier;
import com.burot.render.ChatboxImageGenerator;
import com.burot.render.ChatSegment;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValuableDropEventProcessor extends GameEventProcessor {

    private static final Pattern VALUABLE_DROP_PATTERN = Pattern.compile("Valuable drop: (.*?) \\(([0-9,]+) coins\\)");

    private final BurotConfig pluginConfiguration;
    private final ChatboxImageGenerator imageGenerator;
    private final SharedEventState sharedEventState;

    public ValuableDropEventProcessor(List<Notifier> registeredNotifiers, BurotConfig pluginConfiguration, SharedEventState sharedEventState) {
        super(registeredNotifiers);
        this.pluginConfiguration = pluginConfiguration;
        this.sharedEventState = sharedEventState;
        this.imageGenerator = new ChatboxImageGenerator();
    }

    @Override
    public String retrieveProcessorName() {
        return "Valuable Drop";
    }

    private AudioSource determineAudioSource() {
        if (!pluginConfiguration.enableValuableDropSound()) {
            return new DisabledAudioSource();
        }

        String customAudioPath = pluginConfiguration.valuableDropSoundPath();
        if (customAudioPath == null || customAudioPath.trim().isEmpty()) {
            return new InternalAudioSource("/valuabledrop.wav");
        }

        return new FileAudioSource(customAudioPath);
    }

    @Override
    public void simulateEventExecution(String activePlayerName, String activeClanName) {
        if (!pluginConfiguration.notifyValuableDrop()) {
            return;
        }

        String simulatedMessageContent = "Valuable drop: Mike Tyson's front teeth (420,000,000 coins)";
        processRawMessage(simulatedMessageContent, activePlayerName, activeClanName, -1);
    }

    @Override
    public void evaluateIncomingEvent(ChatMessage incomingChatMessage, String activePlayerName, String activeClanName, int currentTick) {
        if (!pluginConfiguration.notifyValuableDrop()) {
            return;
        }

        if (sharedEventState.isWithinCollectionLogWindow(currentTick)) {
            return;
        }

        ChatMessageType incomingMessageType = incomingChatMessage.getType();
        if (incomingMessageType != ChatMessageType.GAMEMESSAGE && incomingMessageType != ChatMessageType.SPAM) {
            return;
        }

        String rawMessageContent = incomingChatMessage.getMessage();
        processRawMessage(rawMessageContent, activePlayerName, activeClanName, currentTick);
    }

    private void processRawMessage(String rawMessageContent, String activePlayerName, String activeClanName, int currentTick) {
        String sanitizedMessageContent = rawMessageContent.replaceAll("<[^>]+>", "");
        Matcher patternMatcher = VALUABLE_DROP_PATTERN.matcher(sanitizedMessageContent);

        if (patternMatcher.find()) {
            String extractedItem = patternMatcher.group(1);
            String extractedValueString = patternMatcher.group(2);

            try {
                long dropValue = Long.parseLong(extractedValueString.replace(",", ""));
                if (dropValue >= pluginConfiguration.valuableDropThreshold()) {
                    executeNotificationSequence(activePlayerName, activeClanName, extractedItem, extractedValueString);
                }
            } catch (NumberFormatException ignoredException) {
            }
        }
    }

    private void executeNotificationSequence(String activePlayerName, String activeClanName, String targetItem, String targetValue) {
        List<ChatSegment> notificationSegments = new ArrayList<>();

        if (activeClanName != null && !activeClanName.isEmpty()) {
            notificationSegments.add(new ChatSegment("[" + activeClanName + "] ", Color.BLUE));
        }

        notificationSegments.add(new ChatSegment(activePlayerName + " ", Color.BLACK));
        notificationSegments.add(new ChatSegment("received a drop: ", Color.BLACK));
        notificationSegments.add(new ChatSegment(targetItem + " (" + targetValue + " coins).", Color.BLACK));

        byte[] renderedImagePayload = imageGenerator.generateChatboxImage(notificationSegments);
        AudioSource eventAudioSource = determineAudioSource();

        triggerAllNotifiers("", eventAudioSource, renderedImagePayload);
    }
}