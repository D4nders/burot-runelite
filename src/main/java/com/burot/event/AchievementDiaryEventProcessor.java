package com.burot.event;

import com.burot.BurotConfig;
import com.burot.render.ChatSegment;
import com.burot.render.ChatboxImageGenerator;
import com.burot.notifier.Notifier;
import com.burot.audio.AudioSource;
import com.burot.audio.DisabledAudioSource;
import com.burot.audio.FileAudioSource;
import com.burot.audio.InternalAudioSource;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AchievementDiaryEventProcessor extends GameEventProcessor {

    private static final Pattern DIARY_COMPLETION_PATTERN = Pattern.compile("Congratulations! You have completed all of the (easy|medium|hard|elite) tasks in the (.*) area.");

    private final BurotConfig pluginConfiguration;
    private final ChatboxImageGenerator imageGenerator;

    public AchievementDiaryEventProcessor(List<Notifier> registeredNotifiers, BurotConfig pluginConfiguration) {
        super(registeredNotifiers);
        this.pluginConfiguration = pluginConfiguration;
        this.imageGenerator = new ChatboxImageGenerator();
    }

    @Override
    public String retrieveProcessorName() {
        return "Achievement Diary";
    }

    private AudioSource determineAudioSource() {
        if (pluginConfiguration.universalSoundMute() || !pluginConfiguration.enableDiarySound()) {
            return new DisabledAudioSource();
        }

        String customAudioPath = pluginConfiguration.diarySoundPath();
        if (customAudioPath == null || customAudioPath.trim().isEmpty()) {
            return new InternalAudioSource("/diary.wav");
        }

        return new FileAudioSource(customAudioPath);
    }

    private boolean isTierConfiguredForNotification(String completedTier) {
        switch (completedTier.toLowerCase()) {
            case "easy":
                return pluginConfiguration.notifyDiaryEasy();
            case "medium":
                return pluginConfiguration.notifyDiaryMedium();
            case "hard":
                return pluginConfiguration.notifyDiaryHard();
            case "elite":
                return pluginConfiguration.notifyDiaryElite();
            default:
                return false;
        }
    }

    @Override
    public void simulateEventExecution(String activePlayerName, String activeClanName) {
        if (!pluginConfiguration.notifyAchievementDiary()) {
            return;
        }

        String simulatedMessageContent = "Congratulations! You have completed all of the hard tasks in the Ardougne area.";
        processRawMessage(simulatedMessageContent, activePlayerName, activeClanName);
    }

    @Override
    public void evaluateIncomingEvent(ChatMessage incomingChatMessage, String activePlayerName, String activeClanName, int currentTick) {
        if (!pluginConfiguration.notifyAchievementDiary()) {
            return;
        }

        ChatMessageType incomingMessageType = incomingChatMessage.getType();
        if (incomingMessageType != ChatMessageType.GAMEMESSAGE && incomingMessageType != ChatMessageType.SPAM) {
            return;
        }

        String rawMessageContent = incomingChatMessage.getMessage();
        processRawMessage(rawMessageContent, activePlayerName, activeClanName);
    }

    private void processRawMessage(String rawMessageContent, String activePlayerName, String activeClanName) {
        String sanitizedMessageContent = rawMessageContent.replaceAll("<[^>]+>", "");
        Matcher patternMatcher = DIARY_COMPLETION_PATTERN.matcher(sanitizedMessageContent);

        if (patternMatcher.find()) {
            String extractedTier = patternMatcher.group(1);
            String extractedArea = patternMatcher.group(2);

            if (isTierConfiguredForNotification(extractedTier)) {
                executeNotificationSequence(activePlayerName, activeClanName, extractedTier, extractedArea);
            }
        }
    }

    private String formatTierCapitalization(String rawTier) {
        if (rawTier == null || rawTier.isEmpty()) {
            return rawTier;
        }
        return rawTier.substring(0, 1).toUpperCase() + rawTier.substring(1).toLowerCase();
    }

    private void executeNotificationSequence(String activePlayerName, String activeClanName, String targetTier, String targetArea) {
        String capitalizedTier = formatTierCapitalization(targetTier);

        List<ChatSegment> notificationSegments = new ArrayList<>();

        if (activeClanName != null && !activeClanName.isEmpty()) {
            notificationSegments.add(new ChatSegment("[" + activeClanName + "] ", Color.BLUE));
        }

        notificationSegments.add(new ChatSegment(activePlayerName + " ", Color.BLACK));
        notificationSegments.add(new ChatSegment("completed the ", Color.BLACK));
        notificationSegments.add(new ChatSegment(capitalizedTier + " ", new Color(127, 0, 0)));
        notificationSegments.add(new ChatSegment(targetArea + " ", new Color(127, 0, 0)));
        notificationSegments.add(new ChatSegment("diary.", Color.BLACK));

        byte[] renderedImagePayload = imageGenerator.generateChatboxImage(notificationSegments);
        AudioSource eventAudioSource = determineAudioSource();

        triggerAllNotifiers("", eventAudioSource, renderedImagePayload);
    }
}