package org.exemple.demo;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException {
                StartServeur start = new StartServeur();
                start.startServer();
                while (true){
                    start.getSocket();
                    System.out.println("Nouvelle connexion");
                    Thread thread = new Thread(start);
                    thread.start();
                }
    }
}