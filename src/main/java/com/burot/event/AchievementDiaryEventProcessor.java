package com.burot.event;

import com.burot.BurotConfig;
import com.burot.audio.AudioSource;
import com.burot.audio.AudioSourceResolver;
import com.burot.notifier.Notifier;
import com.burot.render.ChatSegment;
import com.burot.render.ChatboxImageGenerator;

import java.awt.Color;
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

    @Override
    protected boolean isEventEnabled() {
        return pluginConfiguration.notifyAchievementDiary();
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
        if (!isEventEnabled()) {
            return;
        }

        String simulatedMessageContent = "Congratulations! You have completed all of the hard tasks in the Ardougne area.";
        processSanitizedMessage(simulatedMessageContent, activePlayerName, activeClanName, -1);
    }

    @Override
    protected void processSanitizedMessage(String sanitizedMessageContent, String activePlayerName, String activeClanName, int currentTick) {
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

        List<ChatSegment> notificationSegments = buildPlayerClanPrefixSegments(activePlayerName, activeClanName);
        notificationSegments.add(new ChatSegment("completed the ", Color.BLACK));
        notificationSegments.add(new ChatSegment(capitalizedTier + " ", new Color(127, 0, 0)));
        notificationSegments.add(new ChatSegment(targetArea + " ", new Color(127, 0, 0)));
        notificationSegments.add(new ChatSegment("diary.", Color.BLACK));

        byte[] renderedImagePayload = imageGenerator.generateChatboxImage(notificationSegments);

        AudioSource eventAudioSource = AudioSourceResolver.resolveAudioSource(
                pluginConfiguration.universalSoundMute(),
                pluginConfiguration.enableDiarySound(),
                pluginConfiguration.diarySoundPath(),
                "/diary.wav"
        );

        triggerAllNotifiers("", eventAudioSource, renderedImagePayload);
    }
}