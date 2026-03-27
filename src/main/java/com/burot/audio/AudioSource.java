package com.burot.audio;

import java.io.InputStream;

public interface AudioSource {
    InputStream retrieveAudioStream();
}