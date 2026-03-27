package com.burot.event;

import com.burot.BurotConfig;
import com.burot.SharedEventState;
import com.burot.audio.AudioSource;
import com.burot.audio.AudioSourceResolver;
import com.burot.notifier.Notifier;
import com.burot.render.ChatSegment;
import com.burot.render.ChatboxImageGenerator;

import java.awt.Color;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PetEventProcessor extends GameEventProcessor {

    private static final Pattern PET_DETECTION_PATTERN = Pattern.compile("(You have a funny feeling.*|You feel something weird sneaking into your backpack.*)");

    private final BurotConfig pluginConfiguration;
    private final ChatboxImageGenerator imageGenerator;
    private final SharedEventState sharedEventState;

    public PetEventProcessor(List<Notifier> registeredNotifiers, BurotConfig pluginConfiguration, SharedEventState sharedEventState) {
        super(registeredNotifiers);
        this.pluginConfiguration = pluginConfiguration;
        this.sharedEventState = sharedEventState;
        this.imageGenerator = new ChatboxImageGenerator();
    }

    @Override
    public String retrieveProcessorName() {
        return "Pet Drop";
    }

    @Override
    protected boolean isEventEnabled() {
        return pluginConfiguration.notifyPet();
    }

    @Override
    public void simulateEventExecution(String activePlayerName, String activeClanName) {
        if (!isEventEnabled()) {
            return;
        }

        String simulatedMessageContent = "You have a funny feeling like you're being followed: blobfish at 69 M xp.";
        processSanitizedMessage(simulatedMessageContent, activePlayerName, activeClanName, -1);
    }

    @Override
    protected void processSanitizedMessage(String sanitizedMessageContent, String activePlayerName, String activeClanName, int currentTick) {
        Matcher patternMatcher = PET_DETECTION_PATTERN.matcher(sanitizedMessageContent);

        if (patternMatcher.find()) {
            if (currentTick != -1) {
                sharedEventState.registerPetDrop(currentTick);
            }

            executeNotificationSequence(activePlayerName, activeClanName, patternMatcher.group(1));
        }
    }

    private void executeNotificationSequence(String activePlayerName, String activeClanName, String petMessageContent) {
        String formattedPetMessage = petMessageContent
                .replace("You have a funny feeling like you're", "has a funny feeling like they're")
                .replace("You feel something weird sneaking into your", "feels something weird sneaking into their");

        List<ChatSegment> notificationSegments = buildPlayerClanPrefixSegments(activePlayerName, activeClanName);
        notificationSegments.add(new ChatSegment(formattedPetMessage, new Color(127, 0, 0)));

        byte[] renderedImagePayload = imageGenerator.generateChatboxImage(notificationSegments);

        AudioSource eventAudioSource = AudioSourceResolver.resolveAudioSource(
                pluginConfiguration.universalSoundMute(),
                pluginConfiguration.enablePetSound(),
                pluginConfiguration.petSoundPath(),
                "/pet.wav"
        );

        triggerAllNotifiers("", eventAudioSource, renderedImagePayload);
    }
}