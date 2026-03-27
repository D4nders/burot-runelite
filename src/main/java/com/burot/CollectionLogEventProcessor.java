package com.burot;

import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CollectionLogEventProcessor extends GameEventProcessor {

    private static final Pattern COLLECTION_LOG_DETECTION_PATTERN = Pattern.compile("New item added to your collection log: (.*)");

    private final BurotConfig pluginConfiguration;

    public CollectionLogEventProcessor(List<Notifier> registeredNotifiers, BurotConfig pluginConfiguration) {
        super(registeredNotifiers);
        this.pluginConfiguration = pluginConfiguration;
    }

    @Override
    public String retrieveProcessorName() {
        return "Collection Log";
    }

    @Override
    public void simulateEventExecution(String activePlayerName) {
        String discordFormattedMessage = activePlayerName + " obtained a new collection log item: **Simulated Dev Item**";
        String configuredSoundPath = pluginConfiguration.collectionLogSoundPath();

        triggerAllNotifiers(discordFormattedMessage, configuredSoundPath);
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
        Matcher patternMatcher = COLLECTION_LOG_DETECTION_PATTERN.matcher(rawMessageContent);

        if (patternMatcher.find()) {
            String extractedItemName = patternMatcher.group(1);
            String discordFormattedMessage = activePlayerName + " obtained a new collection log item: **" + extractedItemName + "**";
            String configuredSoundPath = pluginConfiguration.collectionLogSoundPath();

            triggerAllNotifiers(discordFormattedMessage, configuredSoundPath);
        }
    }
}