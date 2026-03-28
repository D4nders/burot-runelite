package com.burot.event;

import com.burot.BurotConfig;
import com.burot.audio.AudioSource;
import com.burot.audio.AudioSourceResolver;
import com.burot.notifier.Notifier;
import com.burot.render.ChatSegment;
import com.burot.render.ChatboxImageGenerator;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewRecordEventProcessor extends GameEventProcessor {

    private static final Pattern NEW_RECORD_PATTERN = Pattern.compile("^(.*?)\\s*(?:time|duration):\\s+([\\d:.]+)\\s*\\(new personal best\\)");
    private static final Pattern SEPULCHRE_CONTEXT_PATTERN = Pattern.compile("completed (.*?) of the (.*?)!");
    private static final Pattern COUNT_CONTEXT_PATTERN = Pattern.compile("Your (.*?)\\s*(?:lap|kill|chest|completion) count is:");

    private final BurotConfig pluginConfiguration;
    private final ChatboxImageGenerator imageGenerator;

    private String lastContextMessage = "";

    public NewRecordEventProcessor(List<Notifier> registeredNotifiers, BurotConfig pluginConfiguration) {
        super(registeredNotifiers);
        this.pluginConfiguration = pluginConfiguration;
        this.imageGenerator = new ChatboxImageGenerator();
    }

    @Override
    public String retrieveProcessorName() {
        return "New Record";
    }

    @Override
    protected boolean isEventEnabled() {
        return pluginConfiguration.notifyNewRecord();
    }

    @Override
    public void simulateEventExecution(String activePlayerName, String activeClanName) {
        if (!isEventEnabled()) {
            return;
        }

        processSanitizedMessage("You have completed Floor 3 of the Hallowed Sepulchre! Total completions: 55.", activePlayerName, activeClanName, -1);
        processSanitizedMessage("Floor 3 time: 1:10.20 (new personal best)", activePlayerName, activeClanName, -1);

        processSanitizedMessage("Your Grotesque Guardians kill count is: 420.", activePlayerName, activeClanName, -1);
        processSanitizedMessage("Grotesque Guardians duration: 2:29.40 (new personal best)", activePlayerName, activeClanName, -1);
    }

    @Override
    protected void processSanitizedMessage(String sanitizedMessageContent, String activePlayerName, String activeClanName, int currentTick) {
        if (sanitizedMessageContent.contains("completed") || sanitizedMessageContent.contains("count is:")) {
            lastContextMessage = sanitizedMessageContent;
        }

        Matcher pbMatcher = NEW_RECORD_PATTERN.matcher(sanitizedMessageContent);
        if (pbMatcher.find()) {
            String shortActivity = pbMatcher.group(1).trim();
            String recordTime = pbMatcher.group(2).trim();

            String fullActivity = resolveFullActivityName(shortActivity, lastContextMessage);
            String category = categorizeActivity(fullActivity, lastContextMessage);

            if (isCategoryConfiguredForNotification(category)) {
                executeNotificationSequence(activePlayerName, activeClanName, fullActivity, recordTime);
            }
        }
    }

    private String resolveFullActivityName(String shortActivity, String contextMsg) {
        if (contextMsg == null || contextMsg.isEmpty()) {
            return shortActivity;
        }

        Matcher sepulchreMatcher = SEPULCHRE_CONTEXT_PATTERN.matcher(contextMsg);
        if (sepulchreMatcher.find()) {
            if (shortActivity.equalsIgnoreCase(sepulchreMatcher.group(1).trim())) {
                return sepulchreMatcher.group(2).trim() + " (" + shortActivity + ")";
            }
        }

        Matcher countMatcher = COUNT_CONTEXT_PATTERN.matcher(contextMsg);
        if (countMatcher.find()) {
            String contextActivity = countMatcher.group(1).trim();
            if (!contextActivity.equalsIgnoreCase(shortActivity)) {
                return contextActivity + " (" + shortActivity + ")";
            }
        }

        return shortActivity;
    }

    private String categorizeActivity(String activity, String contextMsg) {
        String normalizedActivity = activity.toLowerCase();
        String normalizedContext = contextMsg != null ? contextMsg.toLowerCase() : "";

        if (normalizedActivity.contains("lap") || normalizedActivity.contains("agility") || normalizedActivity.contains("course") || normalizedActivity.contains("sepulchre")) {
            return "agility";
        } else if (normalizedActivity.contains("chambers of xeric") || normalizedActivity.contains("theatre of blood") || normalizedActivity.contains("tombs of amascut") || normalizedActivity.contains("raid")) {
            return "raids";
        } else if (normalizedActivity.contains("barracuda")) {
            return "barracuda";
        } else if (normalizedContext.contains("kill count") || normalizedActivity.contains("boss") || normalizedActivity.contains("guardian")) {
            return "bosses";
        }

        return "minigame";
    }

    private boolean isCategoryConfiguredForNotification(String category) {
        switch (category) {
            case "agility":
                return pluginConfiguration.notifyRecordAgility();
            case "raids":
                return pluginConfiguration.notifyRecordRaids();
            case "barracuda":
                return pluginConfiguration.notifyRecordBarracuda();
            case "bosses":
                return pluginConfiguration.notifyRecordBosses();
            case "minigame":
            default:
                return pluginConfiguration.notifyRecordMinigame();
        }
    }

    private String resolveAudioResourcePath() {
        if (ThreadLocalRandom.current().nextInt(200) == 0) {
            return "/record_2.wav";
        }
        return "/record_1.wav";
    }

    private void executeNotificationSequence(String activePlayerName, String activeClanName, String targetActivity, String targetTime) {
        List<ChatSegment> notificationSegments = buildPlayerClanPrefixSegments(activePlayerName, activeClanName);
        notificationSegments.add(new ChatSegment("has achieved a new ", Color.BLACK));
        notificationSegments.add(new ChatSegment(targetActivity, new Color(127, 0, 0)));
        notificationSegments.add(new ChatSegment(" personal best: ", Color.BLACK));
        notificationSegments.add(new ChatSegment(targetTime, new Color(127, 0, 0)));

        byte[] renderedImagePayload = imageGenerator.generateChatboxImage(notificationSegments);

        AudioSource eventAudioSource = AudioSourceResolver.resolveAudioSource(
                pluginConfiguration.universalSoundMute(),
                pluginConfiguration.enableRecordSound(),
                pluginConfiguration.recordSoundPath(),
                resolveAudioResourcePath()
        );

        triggerAllNotifiers("", eventAudioSource, renderedImagePayload);
    }
}