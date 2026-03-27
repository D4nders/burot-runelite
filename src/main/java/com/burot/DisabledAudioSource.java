package com.burot;

import javax.sound.sampled.AudioInputStream;

public class DisabledAudioSource implements AudioSource {

    @Override
    public AudioInputStream retrieveAudioStream() {
        return null;
    }
}