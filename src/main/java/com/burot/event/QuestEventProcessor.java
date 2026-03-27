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

public class QuestEventProcessor extends GameEventProcessor {

    private static final Pattern QUEST_COMPLETION_PATTERN = Pattern.compile("Congratulations, you've completed a quest: (.*)");

    private final BurotConfig pluginConfiguration;
    private final ChatboxImageGenerator imageGenerator;

    public QuestEventProcessor(List<Notifier> registeredNotifiers, BurotConfig pluginConfiguration) {
        super(registeredNotifiers);
        this.pluginConfiguration = pluginConfiguration;
        this.imageGenerator = new ChatboxImageGenerator();
    }

    @Override
    public String retrieveProcessorName() {
        return "Quest Completion";
    }

    @Override
    protected boolean isEventEnabled() {
        return pluginConfiguration.notifyQuest();
    }

    @Override
    public void simulateEventExecution(String activePlayerName, String activeClanName) {
        if (!isEventEnabled()) {
            return;
        }

        String simulatedMessageContent = "Congratulations, you've completed a quest: While My Stepsis Sleeps";
        processSanitizedMessage(simulatedMessageContent, activePlayerName, activeClanName, -1);
    }

    @Override
    protected void processSanitizedMessage(String sanitizedMessageContent, String activePlayerName, String activeClanName, int currentTick) {
        Matcher patternMatcher = QUEST_COMPLETION_PATTERN.matcher(sanitizedMessageContent);

        if (patternMatcher.find()) {
            String extractedQuestName = patternMatcher.group(1);
            executeNotificationSequence(activePlayerName, activeClanName, extractedQuestName);
        }
    }

    private void executeNotificationSequence(String activePlayerName, String activeClanName, String targetQuestName) {
        List<ChatSegment> notificationSegments = buildPlayerClanPrefixSegments(activePlayerName, activeClanName);
        notificationSegments.add(new ChatSegment("has completed a quest: ", Color.BLACK));
        notificationSegments.add(new ChatSegment(targetQuestName, Color.BLUE));

        byte[] renderedImagePayload = imageGenerator.generateChatboxImage(notificationSegments);

        AudioSource eventAudioSource = AudioSourceResolver.resolveAudioSource(
                pluginConfiguration.universalSoundMute(),
                pluginConfiguration.enableQuestSound(),
                pluginConfiguration.questSoundPath(),
                "/quest.wav"
        );

        triggerAllNotifiers("", eventAudioSource, renderedImagePayload);
    }
}