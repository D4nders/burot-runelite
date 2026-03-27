package com.burot;

import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.Color;

public class CollectionLogEventProcessor extends GameEventProcessor {

    private static final Pattern COLLECTION_LOG_DETECTION_PATTERN = Pattern.compile("New item added to your collection log: (.*)");
    private static final Pattern PROGRESS_DETECTION_PATTERN = Pattern.compile("(.*)( \\([0-9,]+/[0-9,]+\\))");

    private final BurotConfig pluginConfiguration;
    private final ChatboxImageGenerator imageGenerator;

    public CollectionLogEventProcessor(List<Notifier> registeredNotifiers, BurotConfig pluginConfiguration) {
        super(registeredNotifiers);
        this.pluginConfiguration = pluginConfiguration;
        this.imageGenerator = new ChatboxImageGenerator();
    }

    @Override
    public String retrieveProcessorName() {
        return "Collection Log";
    }

    @Override
    public void simulateEventExecution(String activePlayerName) {
        String simulatedItemName = "Rotten potato";
        String simulatedProgress = " (291/1699)";
        String configuredSoundPath = pluginConfiguration.collectionLogSoundPath();

        List<ChatSegment> notificationSegments = new ArrayList<>();
        notificationSegments.add(new ChatSegment("[Burot] ", Color.BLUE));
        notificationSegments.add(new ChatSegment(activePlayerName + " ", Color.BLACK));
        notificationSegments.add(new ChatSegment("received a new collection log item: ", Color.BLACK));
        notificationSegments.add(new ChatSegment(simulatedItemName, new Color(127, 0, 0)));
        notificationSegments.add(new ChatSegment(simulatedProgress, Color.BLACK));

        byte[] renderedImagePayload = imageGenerator.generateChatboxImage(notificationSegments);

        triggerAllNotifiers("", configuredSoundPath, renderedImagePayload);
    }

    @Override
    public void evaluateIncomingEvent(ChatMessage incomingChatMessage, String activePlayerName) {
        if (!pluginConfiguration.notifyCollectionLog()) {
            return;
        }

        ChatMessageType incomingMessageType = incomingChatMessage.getType();
        if (incomingMessageType != ChatMessageType.GAMEMESSAGE && incomingMessageType != ChatMessageType.SPAM) {
            return;
        }

        String rawMessageContent = incomingChatMessage.getMessage();
        String sanitizedMessageContent = rawMessageContent.replaceAll("<[^>]+>", "");
        Matcher patternMatcher = COLLECTION_LOG_DETECTION_PATTERN.matcher(sanitizedMessageContent);

        if (patternMatcher.find()) {
            String extractedContent = patternMatcher.group(1);
            String extractedItemName = extractedContent;
            String extractedProgress = "";

            Matcher progressMatcher = PROGRESS_DETECTION_PATTERN.matcher(extractedContent);
            if (progressMatcher.find()) {
                extractedItemName = progressMatcher.group(1);
                extractedProgress = progressMatcher.group(2);
            }

            String configuredSoundPath = pluginConfiguration.collectionLogSoundPath();

            List<ChatSegment> notificationSegments = new ArrayList<>();
            notificationSegments.add(new ChatSegment("[Burot] ", Color.BLUE));
            notificationSegments.add(new ChatSegment(activePlayerName + " ", Color.BLACK));
            notificationSegments.add(new ChatSegment("received a new collection log item: ", Color.BLACK));
            notificationSegments.add(new ChatSegment(extractedItemName, new Color(127, 0, 0)));

            if (!extractedProgress.isEmpty()) {
                notificationSegments.add(new ChatSegment(extractedProgress, Color.BLACK));
            }

            byte[] renderedImagePayload = imageGenerator.generateChatboxImage(notificationSegments);

            triggerAllNotifiers("", configuredSoundPath, renderedImagePayload);
        }
    }
}