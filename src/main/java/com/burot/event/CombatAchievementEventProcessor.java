package com.burot.event;

import com.burot.BurotConfig;
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

public class CombatAchievementEventProcessor extends GameEventProcessor {

    private static final Pattern TASK_COMPLETION_PATTERN = Pattern.compile("Congratulations, you've completed an? (easy|medium|hard|elite|master|grandmaster) combat task: (.*?)(?: \\(\\d+ points?\\))?\\.");
    private static final Pattern TIER_COMPLETION_PATTERN = Pattern.compile("You've completed enough Combat Achievement tasks to unlock (Easy|Medium|Hard|Elite|Master|Grandmaster) Tier rewards!.*");

    private final BurotConfig pluginConfiguration;
    private final ChatboxImageGenerator imageGenerator;

    public CombatAchievementEventProcessor(List<Notifier> registeredNotifiers, BurotConfig pluginConfiguration) {
        super(registeredNotifiers);
        this.pluginConfiguration = pluginConfiguration;
        this.imageGenerator = new ChatboxImageGenerator();
    }

    @Override
    public String retrieveProcessorName() {
        return "Combat Achievements";
    }

    private AudioSource determineTaskAudioSource() {
        if (pluginConfiguration.universalSoundMute() || !pluginConfiguration.enableCombatTaskSound()) {
            return new DisabledAudioSource();
        }
        String customAudioPath = pluginConfiguration.combatTaskSoundPath();
        if (customAudioPath == null || customAudioPath.trim().isEmpty()) {
            return new InternalAudioSource("/combattask.wav");
        }
        return new FileAudioSource(customAudioPath);
    }

    private AudioSource determineTierAudioSource() {
        if (pluginConfiguration.universalSoundMute() || !pluginConfiguration.enableCombatTierSound()) {
            return new DisabledAudioSource();
        }
        String customAudioPath = pluginConfiguration.combatTierSoundPath();
        if (customAudioPath == null || customAudioPath.trim().isEmpty()) {
            return new InternalAudioSource("/combattier.wav");
        }
        return new FileAudioSource(customAudioPath);
    }

    private boolean isTierConfiguredForNotification(String completedTier) {
        switch (completedTier.toLowerCase()) {
            case "easy":
                return pluginConfiguration.notifyCombatEasy();
            case "medium":
                return pluginConfiguration.notifyCombatMedium();
            case "hard":
                return pluginConfiguration.notifyCombatHard();
            case "elite":
                return pluginConfiguration.notifyCombatElite();
            case "master":
                return pluginConfiguration.notifyCombatMaster();
            case "grandmaster":
                return pluginConfiguration.notifyCombatGrandmaster();
            default:
                return false;
        }
    }

    @Override
    public void simulateEventExecution(String activePlayerName, String activeClanName) {
        if (!pluginConfiguration.notifyCombatAchievement()) {
            return;
        }

        String simulatedTaskContent = "Congratulations, you've completed a medium combat task: Efficient Pest Control (2 points).";
        processRawMessage(simulatedTaskContent, activePlayerName, activeClanName, -1);

        String simulatedTierContent = "You've completed enough Combat Achievement tasks to unlock Medium Tier rewards! You can now claim your rewards from Ghommal.";
        processRawMessage(simulatedTierContent, activePlayerName, activeClanName, -1);
    }

    @Override
    public void evaluateIncomingEvent(ChatMessage incomingChatMessage, String activePlayerName, String activeClanName, int currentTick) {
        if (!pluginConfiguration.notifyCombatAchievement()) {
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

        Matcher taskMatcher = TASK_COMPLETION_PATTERN.matcher(sanitizedMessageContent);
        if (taskMatcher.find()) {
            String extractedTier = taskMatcher.group(1).toLowerCase();
            String extractedTaskName = taskMatcher.group(2);

            if (isTierConfiguredForNotification(extractedTier)) {
                executeTaskNotificationSequence(activePlayerName, activeClanName, extractedTier, extractedTaskName);
            }
            return;
        }

        Matcher tierMatcher = TIER_COMPLETION_PATTERN.matcher(sanitizedMessageContent);
        if (tierMatcher.find()) {
            String extractedTier = tierMatcher.group(1);

            if (pluginConfiguration.notifyCombatTierCompletion()) {
                executeTierNotificationSequence(activePlayerName, activeClanName, extractedTier);
            }
        }
    }

    private String formatTierCapitalization(String rawTier) {
        if (rawTier == null || rawTier.isEmpty()) {
            return rawTier;
        }
        return rawTier.substring(0, 1).toUpperCase() + rawTier.substring(1).toLowerCase();
    }

    private String determineIndefiniteArticle(String targetTier) {
        if (targetTier.equals("easy") || targetTier.equals("elite")) {
            return "an ";
        }
        return "a ";
    }

    private void executeTaskNotificationSequence(String activePlayerName, String activeClanName, String targetTier, String taskName) {
        String indefiniteArticle = determineIndefiniteArticle(targetTier);

        List<ChatSegment> notificationSegments = new ArrayList<>();
        if (activeClanName != null && !activeClanName.isEmpty()) {
            notificationSegments.add(new ChatSegment("[" + activeClanName + "] ", Color.BLUE));
        }

        notificationSegments.add(new ChatSegment(activePlayerName + " ", Color.BLACK));
        notificationSegments.add(new ChatSegment("has completed " + indefiniteArticle, Color.BLACK));
        notificationSegments.add(new ChatSegment(targetTier + " ", new Color(127, 0, 0)));
        notificationSegments.add(new ChatSegment("combat task: ", Color.BLACK));
        notificationSegments.add(new ChatSegment(taskName + ".", new Color(127, 0, 0)));

        byte[] renderedImagePayload = imageGenerator.generateChatboxImage(notificationSegments);
        AudioSource eventAudioSource = determineTaskAudioSource();

        triggerAllNotifiers("", eventAudioSource, renderedImagePayload);
    }

    private void executeTierNotificationSequence(String activePlayerName, String activeClanName, String targetTier) {
        String capitalizedTier = formatTierCapitalization(targetTier);

        List<ChatSegment> notificationSegments = new ArrayList<>();
        if (activeClanName != null && !activeClanName.isEmpty()) {
            notificationSegments.add(new ChatSegment("[" + activeClanName + "] ", Color.BLUE));
        }

        notificationSegments.add(new ChatSegment(activePlayerName + " ", Color.BLACK));
        notificationSegments.add(new ChatSegment("has unlocked the ", Color.BLACK));
        notificationSegments.add(new ChatSegment(capitalizedTier + " ", new Color(127, 0, 0)));
        notificationSegments.add(new ChatSegment("tier of rewards from Combat Achievements!", Color.BLACK));

        byte[] renderedImagePayload = imageGenerator.generateChatboxImage(notificationSegments);
        AudioSource eventAudioSource = determineTierAudioSource();

        triggerAllNotifiers("", eventAudioSource, renderedImagePayload);
    }
}