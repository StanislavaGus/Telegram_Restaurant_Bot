package org.example;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestSpring {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext contex = new ClassPathXmlApplicationContext("applicationContext.xml");
        //in .xml we can change RockMusick->ClassicalMusin. And don't change code.

    //Music music = contex.getBean("musicBean", Music.class);
    //MusicPlayer musicPlayer = new MusicPlayer(music);

        MusicPlayer musicPlayer = contex.getBean("musicPlayer", MusicPlayer.class);
        musicPlayer.PlayMusic();

        contex.close();
    }
}
