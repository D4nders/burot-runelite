package com.burot.audio;

import com.burot.BurotPlugin;

import java.io.BufferedInputStream;
import java.io.InputStream;

public class InternalAudioSource implements AudioSource {

    private final String resourceLocation;

    public InternalAudioSource(String resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    @Override
    public InputStream retrieveAudioStream() {
        try {
            InputStream rawResourceStream = BurotPlugin.class.getResourceAsStream(resourceLocation);
            if (rawResourceStream != null) {
                return new BufferedInputStream(rawResourceStream);
            }
        } catch (Exception ignoredException) {
        }
        return null;
    }
}