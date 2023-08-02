package org.exemple.demo;

/**
 * La classe ClientFTP est une classe qui représente un client FTP.
 * Cette classe garde une trace de l'état de la connexion du client, du nom d'utilisateur et du mot de passe.
 * Cette classe a également des méthodes pour accéder et mettre à jour ces informations.
 */
public class ClientFTP {

    /* Parameters */
    private boolean isConnected;
    private String username;
    private String password;

    public ClientFTP() {
        isConnected = false;
        username = "";
        password = "";
    }


    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

