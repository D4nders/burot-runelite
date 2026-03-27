package com.burot.audio;

import java.io.InputStream;

public class DisabledAudioSource implements AudioSource {

    @Override
    public InputStream retrieveAudioStream() {
        return null;
    }
}