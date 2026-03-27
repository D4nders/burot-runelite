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

public class CollectionLogEventProcessor extends GameEventProcessor {

    private static final Pattern COLLECTION_LOG_DETECTION_PATTERN = Pattern.compile("New item added to your collection log: (.*)");
    private static final Pattern PROGRESS_DETECTION_PATTERN = Pattern.compile("(.*)( \\([0-9,]+/[0-9,]+\\))");

    private final BurotConfig pluginConfiguration;
    private final ChatboxImageGenerator imageGenerator;
    private final SharedEventState sharedEventState;

    public CollectionLogEventProcessor(List<Notifier> registeredNotifiers, BurotConfig pluginConfiguration, SharedEventState sharedEventState) {
        super(registeredNotifiers);
        this.pluginConfiguration = pluginConfiguration;
        this.sharedEventState = sharedEventState;
        this.imageGenerator = new ChatboxImageGenerator();
    }

    @Override
    public String retrieveProcessorName() {
        return "Collection Log";
    }

    private AudioSource determineAudioSource() {
        if (pluginConfiguration.universalSoundMute() || !pluginConfiguration.enableCollectionLogSound()) {
            return new DisabledAudioSource();
        }

        String customAudioPath = pluginConfiguration.collectionLogSoundPath();
        if (customAudioPath == null || customAudioPath.trim().isEmpty()) {
            return new InternalAudioSource("/collectionlog.wav");
        }

        return new FileAudioSource(customAudioPath);
    }

    @Override
    public void simulateEventExecution(String activePlayerName, String activeClanName) {
        if (!pluginConfiguration.notifyCollectionLog()) {
            return;
        }

        String simulatedMessageContent = "New item added to your collection log: Rotten potato (291/1699)";
        processRawMessage(simulatedMessageContent, activePlayerName, activeClanName, -1);
    }

    @Override
    public void evaluateIncomingEvent(ChatMessage incomingChatMessage, String activePlayerName, String activeClanName, int currentTick) {
        if (!pluginConfiguration.notifyCollectionLog()) {
            return;
        }

        if (sharedEventState.isWithinPetDropWindow(currentTick)) {
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
        Matcher patternMatcher = COLLECTION_LOG_DETECTION_PATTERN.matcher(sanitizedMessageContent);

        if (patternMatcher.find()) {
            if (currentTick != -1) {
                sharedEventState.registerCollectionLogDrop(currentTick);
            }

            String extractedContent = patternMatcher.group(1);
            String extractedItemName = extractedContent;
            String extractedProgress = "";

            Matcher progressMatcher = PROGRESS_DETECTION_PATTERN.matcher(extractedContent);
            if (progressMatcher.find()) {
                extractedItemName = progressMatcher.group(1);
                extractedProgress = progressMatcher.group(2);
            }

            List<ChatSegment> notificationSegments = new ArrayList<>();

            if (activeClanName != null && !activeClanName.isEmpty()) {
                notificationSegments.add(new ChatSegment("[" + activeClanName + "] ", Color.BLUE));
            }

            notificationSegments.add(new ChatSegment(activePlayerName + " ", Color.BLACK));
            notificationSegments.add(new ChatSegment("received a new collection log item: ", Color.BLACK));
            notificationSegments.add(new ChatSegment(extractedItemName, new Color(127, 0, 0)));

            if (!extractedProgress.isEmpty()) {
                notificationSegments.add(new ChatSegment(extractedProgress, Color.BLACK));
            }

            byte[] renderedImagePayload = imageGenerator.generateChatboxImage(notificationSegments);
            AudioSource eventAudioSource = determineAudioSource();

            triggerAllNotifiers("", eventAudioSource, renderedImagePayload);
        }
    }
}