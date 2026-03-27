package com.burot;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class InternalAudioSource implements AudioSource {

    private final String resourceLocation;

    public InternalAudioSource(String resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    @Override
    public AudioInputStream retrieveAudioStream() {
        try {
            InputStream rawResourceStream = BurotPlugin.class.getResourceAsStream(resourceLocation);
            if (rawResourceStream != null) {
                return AudioSystem.getAudioInputStream(new BufferedInputStream(rawResourceStream));
            }
        } catch (Exception ignoredException) {
        }
        return null;
    }
}