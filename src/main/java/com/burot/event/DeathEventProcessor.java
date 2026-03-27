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

public class DeathEventProcessor extends GameEventProcessor {

    private static final Pattern PVP_DEATH_PATTERN = Pattern.compile("^(?:\\[.*?\\]\\s*)?(.*?)\\s+has been defeated by\\s+(.*?)(?:\\s+in\\s+.*?)?\\s+and lost\\s+\\(([0-9,]+)\\s+coins\\)\\s+worth of loot.*");
    private static final Pattern PVE_DEATH_PATTERN = Pattern.compile("^(?:\\[.*?\\]\\s*)?(.*?)\\s+has been defeated by\\s+(.*?)\\.");

    private final BurotConfig pluginConfiguration;
    private final ChatboxImageGenerator imageGenerator;

    public DeathEventProcessor(List<Notifier> registeredNotifiers, BurotConfig pluginConfiguration) {
        super(registeredNotifiers);
        this.pluginConfiguration = pluginConfiguration;
        this.imageGenerator = new ChatboxImageGenerator();
    }

    @Override
    public String retrieveProcessorName() {
        return "Deaths";
    }

    @Override
    protected boolean isEventEnabled() {
        return pluginConfiguration.notifyDeath();
    }

    @Override
    public void simulateEventExecution(String activePlayerName, String activeClanName) {
        if (!isEventEnabled()) {
            return;
        }

        processSanitizedMessage(activePlayerName + " has been defeated by Mini K in The Wilderness and lost (270,678 coins) worth of loot. Perhaps they should stick to skilling.", activePlayerName, activeClanName, -1);
        processSanitizedMessage(activePlayerName + " has been defeated by a goblin.", activePlayerName, activeClanName, -1);
    }

    @Override
    protected void processSanitizedMessage(String sanitizedMessageContent, String activePlayerName, String activeClanName, int currentTick) {
        Matcher pvpMatcher = PVP_DEATH_PATTERN.matcher(sanitizedMessageContent);
        if (pvpMatcher.find()) {
            if (pluginConfiguration.notifyDeathPlayer()) {
                String defeatedPlayer = pvpMatcher.group(1);
                String killerName = pvpMatcher.group(2);
                String valueLost = pvpMatcher.group(3);
                executePvpNotificationSequence(defeatedPlayer, activeClanName, killerName, valueLost);
            }
            return;
        }

        Matcher pveMatcher = PVE_DEATH_PATTERN.matcher(sanitizedMessageContent);
        if (pveMatcher.find()) {
            if (pluginConfiguration.notifyDeathMonster()) {
                String defeatedPlayer = pveMatcher.group(1);
                String killerName = pveMatcher.group(2);
                executePveNotificationSequence(defeatedPlayer, activeClanName, killerName);
            }
        }
    }

    private void executePvpNotificationSequence(String defeatedPlayer, String activeClanName, String killerName, String valueLost) {
        List<ChatSegment> notificationSegments = buildPlayerClanPrefixSegments(defeatedPlayer, activeClanName);
        notificationSegments.add(new ChatSegment("has been defeated by ", Color.BLACK));
        notificationSegments.add(new ChatSegment(killerName, new Color(127, 0, 0)));
        notificationSegments.add(new ChatSegment(" and lost ", Color.BLACK));
        notificationSegments.add(new ChatSegment(valueLost + " coins", new Color(127, 0, 0)));
        notificationSegments.add(new ChatSegment("!", Color.BLACK));

        dispatchResolvedNotification(notificationSegments);
    }

    private void executePveNotificationSequence(String defeatedPlayer, String activeClanName, String killerName) {
        List<ChatSegment> notificationSegments = buildPlayerClanPrefixSegments(defeatedPlayer, activeClanName);
        notificationSegments.add(new ChatSegment("has been defeated by ", Color.BLACK));
        notificationSegments.add(new ChatSegment(killerName, new Color(127, 0, 0)));
        notificationSegments.add(new ChatSegment(".", Color.BLACK));

        dispatchResolvedNotification(notificationSegments);
    }

    private void dispatchResolvedNotification(List<ChatSegment> notificationSegments) {
        byte[] renderedImagePayload = imageGenerator.generateChatboxImage(notificationSegments);

        AudioSource eventAudioSource = AudioSourceResolver.resolveAudioSource(
                pluginConfiguration.universalSoundMute(),
                pluginConfiguration.enableDeathSound(),
                pluginConfiguration.deathSoundPath(),
                "/death.wav"
        );

        triggerAllNotifiers("", eventAudioSource, renderedImagePayload);
    }
}