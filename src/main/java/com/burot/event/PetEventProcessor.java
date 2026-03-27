package com.burot.event;

import com.burot.*;
import com.burot.audio.AudioSource;
import com.burot.audio.DisabledAudioSource;
import com.burot.audio.FileAudioSource;
import com.burot.audio.InternalAudioSource;
import com.burot.notifier.Notifier;
import com.burot.render.ChatSegment;
import com.burot.render.ChatboxImageGenerator;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;

import java.awt.Color;
import java.util.ArrayList;
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

    private AudioSource determineAudioSource() {
        if (pluginConfiguration.universalSoundMute() || !pluginConfiguration.enablePetSound()) {
            return new DisabledAudioSource();
        }

        String customAudioPath = pluginConfiguration.petSoundPath();
        if (customAudioPath == null || customAudioPath.trim().isEmpty()) {
            return new InternalAudioSource("/pet.wav");
        }

        return new FileAudioSource(customAudioPath);
    }

    @Override
    public void simulateEventExecution(String activePlayerName, String activeClanName) {
        if (!pluginConfiguration.notifyPet()) {
            return;
        }

        String simulatedMessageContent = "You have a funny feeling like you're being followed: blobfish at 69 M xp.";
        processRawMessage(simulatedMessageContent, activePlayerName, activeClanName, -1);
    }

    @Override
    public void evaluateIncomingEvent(ChatMessage incomingChatMessage, String activePlayerName, String activeClanName, int currentTick) {
        if (!pluginConfiguration.notifyPet()) {
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
        Matcher patternMatcher = PET_DETECTION_PATTERN.matcher(sanitizedMessageContent);

        if (patternMatcher.find()) {
            if (currentTick != -1) {
                sharedEventState.registerPetDrop(currentTick);
            }

            String extractedPetMessage = patternMatcher.group(1);
            executeNotificationSequence(activePlayerName, activeClanName, extractedPetMessage);
        }
    }

    private void executeNotificationSequence(String activePlayerName, String activeClanName, String petMessageContent) {
        String formattedPetMessage = petMessageContent
                .replace("You have a funny feeling like you're", "has a funny feeling like they're")
                .replace("You feel something weird sneaking into your", "feels something weird sneaking into their");

        List<ChatSegment> notificationSegments = new ArrayList<>();

        if (activeClanName != null && !activeClanName.isEmpty()) {
            notificationSegments.add(new ChatSegment("[" + activeClanName + "] ", Color.BLUE));
        }

        notificationSegments.add(new ChatSegment(activePlayerName + " ", Color.BLACK));
        notificationSegments.add(new ChatSegment(formattedPetMessage, new Color(127, 0, 0)));

        byte[] renderedImagePayload = imageGenerator.generateChatboxImage(notificationSegments);
        AudioSource eventAudioSource = determineAudioSource();

        triggerAllNotifiers("", eventAudioSource, renderedImagePayload);
    }
}