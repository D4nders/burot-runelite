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

public class LevelUpEventProcessor extends GameEventProcessor {

    private static final Pattern LEVEL_UP_PATTERN = Pattern.compile("Congratulations, you've just advanced your (.*) level. You are now level (\\d+)\\.");

    private final BurotConfig pluginConfiguration;
    private final ChatboxImageGenerator imageGenerator;

    public LevelUpEventProcessor(List<Notifier> registeredNotifiers, BurotConfig pluginConfiguration) {
        super(registeredNotifiers);
        this.pluginConfiguration = pluginConfiguration;
        this.imageGenerator = new ChatboxImageGenerator();
    }

    @Override
    public String retrieveProcessorName() {
        return "Level Up";
    }

    @Override
    protected boolean isEventEnabled() {
        return pluginConfiguration.notifyLevelUp();
    }

    @Override
    public void simulateEventExecution(String activePlayerName, String activeClanName) {
        if (!isEventEnabled()) {
            return;
        }

        processSanitizedMessage("Congratulations, you've just advanced your Woodcutting level. You are now level 99.", activePlayerName, activeClanName, -1);
        processSanitizedMessage("Congratulations, you've just advanced your Combat level. You are now level 126.", activePlayerName, activeClanName, -1);
    }

    @Override
    protected void processSanitizedMessage(String sanitizedMessageContent, String activePlayerName, String activeClanName, int currentTick) {
        Matcher levelMatcher = LEVEL_UP_PATTERN.matcher(sanitizedMessageContent);

        if (levelMatcher.find()) {
            String extractedSkill = levelMatcher.group(1);
            int extractedLevel = Integer.parseInt(levelMatcher.group(2));

            if (extractedSkill.equalsIgnoreCase("Combat")) {
                if (extractedLevel >= pluginConfiguration.combatLevelThreshold()) {
                    executeCombatNotificationSequence(activePlayerName, activeClanName, extractedLevel);
                }
            } else {
                if (extractedLevel >= pluginConfiguration.skillLevelThreshold()) {
                    executeSkillNotificationSequence(activePlayerName, activeClanName, extractedSkill, extractedLevel);
                }
            }
        }
    }

    private void executeSkillNotificationSequence(String activePlayerName, String activeClanName, String targetSkill, int targetLevel) {
        List<ChatSegment> notificationSegments = buildPlayerClanPrefixSegments(activePlayerName, activeClanName);
        notificationSegments.add(new ChatSegment("has reached level ", Color.BLACK));
        notificationSegments.add(new ChatSegment(String.valueOf(targetLevel), new Color(127, 0, 0)));
        notificationSegments.add(new ChatSegment(" in ", Color.BLACK));
        notificationSegments.add(new ChatSegment(targetSkill + "!", new Color(127, 0, 0)));

        dispatchResolvedNotification(notificationSegments);
    }

    private void executeCombatNotificationSequence(String activePlayerName, String activeClanName, int targetLevel) {
        List<ChatSegment> notificationSegments = buildPlayerClanPrefixSegments(activePlayerName, activeClanName);
        notificationSegments.add(new ChatSegment("has reached Combat level ", Color.BLACK));
        notificationSegments.add(new ChatSegment(String.valueOf(targetLevel) + "!", new Color(127, 0, 0)));

        dispatchResolvedNotification(notificationSegments);
    }

    private void dispatchResolvedNotification(List<ChatSegment> notificationSegments) {
        byte[] renderedImagePayload = imageGenerator.generateChatboxImage(notificationSegments);

        AudioSource eventAudioSource = AudioSourceResolver.resolveAudioSource(
                pluginConfiguration.universalSoundMute(),
                pluginConfiguration.enableLevelUpSound(),
                pluginConfiguration.levelUpSoundPath(),
                "/levelup.wav"
        );

        triggerAllNotifiers("", eventAudioSource, renderedImagePayload);
    }
}