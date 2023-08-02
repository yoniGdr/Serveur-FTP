package org.exemple.demo;

import java.io.*;
import java.net.*;

/**
 * Cette classe représente le point d'entrée du serveur FTP. Elle gère les connexions entrantes 
 * en les acheminant vers une instance de la classe `FTPServer` pour gérer les commandes FTP.
 */
public class StartServeur implements Runnable {

    /**
     * Cette méthode démarre le serveur FTP en définissant le port d'écoute et en acceptant les connexions entrantes. 
     * Pour chaque connexion, une instance de la classe `FTPServer` est créée pour gérer les commandes FTP.
     */
    private ServerSocket serverSocket;
    private Socket clientsocket;
    private static int PORT = 2121;
    public void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Le serveur écoute sur le port " + PORT);
        } catch (IOException e) {
            throw new RuntimeException(e) ;
        }
    }

    private static void closeConnection(ServerSocket serverSocket, Socket clientsocket) throws IOException {
        // Fermeture de la connexion
        clientsocket.close();
        serverSocket.close();
    }

    public Socket getSocket() throws IOException {
        clientsocket = serverSocket.accept(); // Attente de connexions entrantes
        return clientsocket;
    }

    @Override
    public void run() {
        try {
            System.out.println("Connexion établie avec " + clientsocket.getRemoteSocketAddress());
            FTPServer serv = new FTPServer(clientsocket);
            serv.handleCommandManag();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}