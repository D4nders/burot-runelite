package com.burot.audio;

public class AudioSourceResolver {

    public static AudioSource resolveAudioSource(boolean universalMuteEnabled, boolean featureAudioEnabled, String customFileSystemPath, String internalResourcePath) {
        if (universalMuteEnabled || !featureAudioEnabled) {
            return new DisabledAudioSource();
        }

        if (customFileSystemPath == null || customFileSystemPath.trim().isEmpty()) {
            return new InternalAudioSource(internalResourcePath);
        }

        return new FileAudioSource(customFileSystemPath);
    }
}