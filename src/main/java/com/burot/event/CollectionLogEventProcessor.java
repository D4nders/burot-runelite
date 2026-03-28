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
import java.util.concurrent.ThreadLocalRandom;
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

    @Override
    protected boolean isEventEnabled() {
        return pluginConfiguration.notifyCollectionLog();
    }

    @Override
    protected boolean canProcessEvent(int currentTick) {
        return !sharedEventState.isWithinPetDropWindow(currentTick);
    }

    @Override
    public void simulateEventExecution(String activePlayerName, String activeClanName) {
        if (!isEventEnabled()) {
            return;
        }

        String simulatedMessageContent = "New item added to your collection log: Rotten potato (291/1699)";
        processSanitizedMessage(simulatedMessageContent, activePlayerName, activeClanName, -1);
    }

    @Override
    protected void processSanitizedMessage(String sanitizedMessageContent, String activePlayerName, String activeClanName, int currentTick) {
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

            executeNotificationSequence(activePlayerName, activeClanName, extractedItemName, extractedProgress);
        }
    }

    private String resolveAudioResourcePath() {
        if (ThreadLocalRandom.current().nextInt(50) == 0) {
            return "/collectionlog_4.wav";
        }
        return "/collectionlog_" + (ThreadLocalRandom.current().nextInt(3) + 1) + ".wav";
    }

    private void executeNotificationSequence(String activePlayerName, String activeClanName, String extractedItemName, String extractedProgress) {
        List<ChatSegment> notificationSegments = buildPlayerClanPrefixSegments(activePlayerName, activeClanName);
        notificationSegments.add(new ChatSegment("received a new collection log item: ", Color.BLACK));
        notificationSegments.add(new ChatSegment(extractedItemName, new Color(127, 0, 0)));

        if (!extractedProgress.isEmpty()) {
            notificationSegments.add(new ChatSegment(extractedProgress, Color.BLACK));
        }

        byte[] renderedImagePayload = imageGenerator.generateChatboxImage(notificationSegments);

        AudioSource eventAudioSource = AudioSourceResolver.resolveAudioSource(
                pluginConfiguration.universalSoundMute(),
                pluginConfiguration.enableCollectionLogSound(),
                pluginConfiguration.collectionLogSoundPath(),
                resolveAudioResourcePath()
        );

        triggerAllNotifiers("", eventAudioSource, renderedImagePayload);
    }
}