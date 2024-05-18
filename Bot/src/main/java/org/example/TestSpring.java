package org.example;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestSpring {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext contex = new ClassPathXmlApplicationContext("applicationContext.xml");

    UserBean userBean = contex.getBean("userBean", UserBean.class);
    System.out.println(userBean.getName());
    contex.close();
    }
}
