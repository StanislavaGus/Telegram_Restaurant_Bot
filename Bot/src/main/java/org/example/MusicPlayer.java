package org.example;

public class MusicPlayer {
    private Music music;

    //IoC (Inversion of Control)
    public MusicPlayer(Music music) {
        this.music = music;
    }

    public void PlayMusic(){
        System.out.println("Playing: " + music.getSong());
    }
}
