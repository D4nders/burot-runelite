package com.burot.event;

import com.burot.BurotConfig;
import com.burot.SharedEventState;
import com.burot.audio.AudioSource;
import com.burot.audio.AudioSourceResolver;
import com.burot.notifier.Notifier;
import com.burot.render.ChatboxImageGenerator;
import com.burot.render.ChatSegment;

import java.awt.Color;
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

    @Override
    protected boolean isEventEnabled() {
        return pluginConfiguration.notifyValuableDrop();
    }

    @Override
    protected boolean canProcessEvent(int currentTick) {
        return !sharedEventState.isWithinCollectionLogWindow(currentTick);
    }

    @Override
    public void simulateEventExecution(String activePlayerName, String activeClanName) {
        if (!isEventEnabled()) {
            return;
        }

        String simulatedMessageContent = "Valuable drop: Mike Tyson's front teeth (420,000,000 coins)";
        processSanitizedMessage(simulatedMessageContent, activePlayerName, activeClanName, -1);
    }

    @Override
    protected void processSanitizedMessage(String sanitizedMessageContent, String activePlayerName, String activeClanName, int currentTick) {
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
        List<ChatSegment> notificationSegments = buildPlayerClanPrefixSegments(activePlayerName, activeClanName);
        notificationSegments.add(new ChatSegment("received a drop: ", Color.BLACK));
        notificationSegments.add(new ChatSegment(targetItem + " (" + targetValue + " coins).", Color.BLACK));

        byte[] renderedImagePayload = imageGenerator.generateChatboxImage(notificationSegments);

        AudioSource eventAudioSource = AudioSourceResolver.resolveAudioSource(
                pluginConfiguration.universalSoundMute(),
                pluginConfiguration.enableValuableDropSound(),
                pluginConfiguration.valuableDropSoundPath(),
                "/valuabledrop.wav"
        );

        triggerAllNotifiers("", eventAudioSource, renderedImagePayload);
    }
}