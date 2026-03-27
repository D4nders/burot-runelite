package com.burot.audio;

import javax.sound.sampled.AudioInputStream;

public interface AudioSource {
    AudioInputStream retrieveAudioStream();
}