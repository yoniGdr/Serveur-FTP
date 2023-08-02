package org.exemple.demo;

import java.io.*;
import java.net.*;

/**
 * La classe DataServeur est utilisée pour gérer les connexions de données entre le serveur FTP et un client FTP. 
 * Elle comprend des méthodes pour créer un socket de serveur de données, obtenir son adresse IP et son numéro de port, 
 * accepter une connexion de données, fermer la connexion de données et obtenir la connexion de données.
 */
public class DataServeur {

    private ServerSocket dataServerSocket;
    private InetAddress address;
    private int port;
    private Socket dataConnection;


    public DataServeur() throws IOException{
        dataServerSocket = new ServerSocket(0);
        address = dataServerSocket.getInetAddress();
        port = dataServerSocket.getLocalPort();
    }

    public ServerSocket getDataSocket() {
        return dataServerSocket;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public void SetAcceptDataConnection() throws IOException{
        dataConnection = dataServerSocket.accept();
    }

    public void closeDataConnection() throws IOException{
        dataConnection.close();
    }

    public Socket getDataConnection() throws IOException{
        return dataConnection;
    }



}
