package com.sample.test2.form;

public class MainClass {
    public static void main(String[] args){
           Runnable runnable=new Runnable() {
               @Override
               public void run() {
                   new MusicPlayer1();
               }
           };

           Thread thread=new Thread(runnable);
           thread.start();


    }
}
