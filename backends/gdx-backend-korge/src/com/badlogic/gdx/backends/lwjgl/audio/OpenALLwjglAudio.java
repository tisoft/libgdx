package com.badlogic.gdx.backends.lwjgl.audio;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;

public class OpenALLwjglAudio implements Audio, Disposable {
    public OpenALLwjglAudio(int audioDeviceSimultaneousSources, int audioDeviceBufferCount, int audioDeviceBufferSize) {

    }

    public OpenALLwjglAudio() {

    }

    @Override
    public AudioDevice newAudioDevice(int samplingRate, boolean isMono) {
        return null;
    }

    @Override
    public AudioRecorder newAudioRecorder(int samplingRate, boolean isMono) {
        return null;
    }

    @Override
    public Sound newSound(FileHandle fileHandle) {
        return null;
    }

    @Override
    public Music newMusic(FileHandle file) {
        return null;
    }

    @Override
    public void dispose() {

    }

    public void update() {

    }

    public void registerSound(String snd_, Class<Snd_> snd_class) {

    }
}
