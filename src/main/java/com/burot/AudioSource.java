package com.burot;

import javax.sound.sampled.AudioInputStream;

public interface AudioSource {
    AudioInputStream retrieveAudioStream();
}