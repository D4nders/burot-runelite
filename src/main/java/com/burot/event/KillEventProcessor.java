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

public class KillEventProcessor extends GameEventProcessor {

    private static final Pattern PVP_KILL_PATTERN = Pattern.compile("^(?:\\[.*?\\]\\s*)?(.*?)\\s+has defeated\\s+(.*?)\\s+and received\\s+\\(([0-9,]+)\\s+coins\\)\\s+worth of loot.*");
    private static final Pattern PVE_KILL_PATTERN = Pattern.compile("^(?:\\[.*?\\]\\s*)?(.*?)\\s+has defeated\\s+(.*?)\\.");

    private final BurotConfig pluginConfiguration;
    private final ChatboxImageGenerator imageGenerator;

    public KillEventProcessor(List<Notifier> registeredNotifiers, BurotConfig pluginConfiguration) {
        super(registeredNotifiers);
        this.pluginConfiguration = pluginConfiguration;
        this.imageGenerator = new ChatboxImageGenerator();
    }

    @Override
    public String retrieveProcessorName() {
        return "Kills";
    }

    @Override
    protected boolean isEventEnabled() {
        return pluginConfiguration.notifyKill();
    }

    @Override
    public void simulateEventExecution(String activePlayerName, String activeClanName) {
        if (!isEventEnabled()) {
            return;
        }

        processSanitizedMessage(activePlayerName + " has defeated Baja Outlaw and received (469,540 coins) worth of loot!", activePlayerName, activeClanName, -1);
        processSanitizedMessage(activePlayerName + " has defeated the Great Olm.", activePlayerName, activeClanName, -1);
    }

    @Override
    protected void processSanitizedMessage(String sanitizedMessageContent, String activePlayerName, String activeClanName, int currentTick) {
        Matcher pvpMatcher = PVP_KILL_PATTERN.matcher(sanitizedMessageContent);
        if (pvpMatcher.find()) {
            if (pluginConfiguration.notifyKillPlayer()) {
                String killerPlayer = pvpMatcher.group(1);
                String defeatedName = pvpMatcher.group(2);
                String valueGained = pvpMatcher.group(3);
                executePvpNotificationSequence(killerPlayer, activeClanName, defeatedName, valueGained);
            }
            return;
        }

        Matcher pveMatcher = PVE_KILL_PATTERN.matcher(sanitizedMessageContent);
        if (pveMatcher.find()) {
            if (pluginConfiguration.notifyKillRaidBoss()) {
                String killerPlayer = pveMatcher.group(1);
                String defeatedName = pveMatcher.group(2);

                if (isRaidBoss(defeatedName)) {
                    executePveNotificationSequence(killerPlayer, activeClanName, defeatedName);
                }
            }
        }
    }

    private boolean isRaidBoss(String targetName) {
        String lower = targetName.toLowerCase();
        return lower.contains("olm") || lower.contains("verzik") || lower.contains("warden");
    }

    private void executePvpNotificationSequence(String killerPlayer, String activeClanName, String defeatedName, String valueGained) {
        List<ChatSegment> notificationSegments = buildPlayerClanPrefixSegments(killerPlayer, activeClanName);
        notificationSegments.add(new ChatSegment("has defeated ", Color.BLACK));
        notificationSegments.add(new ChatSegment(defeatedName, new Color(127, 0, 0)));
        notificationSegments.add(new ChatSegment(" and received ", Color.BLACK));
        notificationSegments.add(new ChatSegment(valueGained + " coins", new Color(127, 0, 0)));
        notificationSegments.add(new ChatSegment("!", Color.BLACK));

        dispatchResolvedNotification(notificationSegments);
    }

    private void executePveNotificationSequence(String killerPlayer, String activeClanName, String defeatedName) {
        List<ChatSegment> notificationSegments = buildPlayerClanPrefixSegments(killerPlayer, activeClanName);
        notificationSegments.add(new ChatSegment("has defeated ", Color.BLACK));
        notificationSegments.add(new ChatSegment(defeatedName, new Color(127, 0, 0)));
        notificationSegments.add(new ChatSegment(".", Color.BLACK));

        dispatchResolvedNotification(notificationSegments);
    }

    private void dispatchResolvedNotification(List<ChatSegment> notificationSegments) {
        byte[] renderedImagePayload = imageGenerator.generateChatboxImage(notificationSegments);

        AudioSource eventAudioSource = AudioSourceResolver.resolveAudioSource(
                pluginConfiguration.universalSoundMute(),
                pluginConfiguration.enableKillSound(),
                pluginConfiguration.killSoundPath(),
                "/kill.wav"
        );

        triggerAllNotifiers("", eventAudioSource, renderedImagePayload);
    }
}