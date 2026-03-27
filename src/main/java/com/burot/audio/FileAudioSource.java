package com.burot.audio;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class FileAudioSource implements AudioSource {

    private final String fileSystemPath;

    public FileAudioSource(String fileSystemPath) {
        this.fileSystemPath = fileSystemPath;
    }

    @Override
    public InputStream retrieveAudioStream() {
        try {
            File targetAudioFile = new File(fileSystemPath);
            if (targetAudioFile.exists()) {
                return new FileInputStream(targetAudioFile);
            }
        } catch (Exception ignoredException) {
        }
        return null;
    }
}