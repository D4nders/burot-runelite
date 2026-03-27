package com.burot;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;

public class FileAudioSource implements AudioSource {

    private final String fileSystemPath;

    public FileAudioSource(String fileSystemPath) {
        this.fileSystemPath = fileSystemPath;
    }

    @Override
    public AudioInputStream retrieveAudioStream() {
        try {
            File targetAudioFile = new File(fileSystemPath);
            if (targetAudioFile.exists()) {
                return AudioSystem.getAudioInputStream(targetAudioFile);
            }
        } catch (Exception ignoredException) {
        }
        return null;
    }
}