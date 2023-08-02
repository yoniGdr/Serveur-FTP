package org.exemple.demo;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.text.SimpleDateFormat;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
La classe FTPServer représente un serveur FTP qui peut gérer les commandes d'un client FTP.
Elle implémente les fonctionnalités de base d'un serveur FTP, telles que la gestion des utilisateurs, la navigation des dossiers,
la transfert de fichiers, etc.
@author Yoni Gaudiere
*/
public class FTPServer {

    private Map<String, Consumer<String>> commands = new HashMap<>();
    private BufferedReader reader;
    private PrintWriter writer;
    private ClientFTP client;
    private String workingDirectory;
    private String[] parts;
    private String command;
    private ServerSocket dataServerSocket;
    private InetAddress address;
    private int port;
    private Socket dataConnection;
    private Socket socket;


    public FTPServer(Socket socket) throws IOException{
        this.socket = socket;
        dataServerSocket = new ServerSocket(0);
        address = dataServerSocket.getInetAddress();
        port = dataServerSocket.getLocalPort();

        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new PrintWriter(socket.getOutputStream(), true);
        this.client = new ClientFTP();
        this.workingDirectory = "/";

        commands.put("LIST", this::handleListCommand);
        commands.put("USER", this::handleUserCommand);
        commands.put("PASS", this::handlePassCommand);
        commands.put("PWD",  this::handlePwdCommand);
        commands.put("EPSV", this::handleEpsvCommand);
        commands.put("TYPE", this::handleTypeCommand);
        commands.put("AUTH", this::handleAuthCommand);
        commands.put("SYST", this::handleSystCommand);
        commands.put("FEAT", this::handleFeatCommand);
        commands.put("OPTS",  this::handleOptsCommand);
        commands.put("PASV",  this::handlePasvCommand);
        commands.put("CWD",  this::handleCwdCommand);
        commands.put("STOR",  this::handleStorCommand);
        commands.put("RETR",  this::handleRetrCommand);
        commands.put("QUIT",  this::handleQuitCommand);
        commands.put("PORT",  this::handlePortCommand);
        commands.put("MKD",  this::handleMkdCommand);
        commands.put("DELE",  this::handleDeleCommand);
        commands.put("RMD",  this::handleRmdCommand);
      
    }

    /**
    * La méthode `handleCommand` est responsable de traiter les commandes envoyées par l'utilisateur.
    * Elle analyse la commande et détermine quelle action doit être prise en conséquence.
     * 
    * @param {string} command La commande envoyée par l'utilisateur
    * @param {string} argument L'argument de la commande
    */
    public void handleCommand(String command, String argument) {
        Consumer<String> handler = commands.get(command);
        if (handler != null) {
            handler.accept(argument);
        } else {
            System.out.println("Unknown command: " + command);
        }
    }

    /**
     * La méthode handleCommandManag traite les commandes de gestion des utilisateurs. 
    * Elle vérifie les informations fournies par l'utilisateur et effectue les actions appropriées en conséquence.
    *
    * @param command La commande saisie par l'utilisateur
    * @return Le résultat de la commande exécutée
     */
    public void handleCommandManag() throws IOException {

        writer.println("220 FTP server (vsftpd)");

        String line;

        while ((line = reader.readLine()) != null) {
            System.out.println("Reçu : " + line);
            parts = line.split(" ");
            command = parts[0].toUpperCase();
            System.out.println("command = " + command);
            handleCommand(command, parts.length > 1 ? parts[1] : null);
        }
    }
 


    /**
    * handleUserCommand - Handles the USER command.
    * The USER command is used to specify the username for authentication. The
    * server will reply with a 331 response to indicate that a password is required
    * to complete the authentication process.
    */
    public void handleUserCommand(String argument) {
        client.setUsername(argument);
        writer.println("331 Please specify the password.");
    }

    public void handleAuthCommand(String argument) {
        writer.println("530 Please login with USER and PASS.");
    }

    /**
     * Gère la commande FTP "PASS".
     *
     * La méthode handlePassCommand() est appelée lorsque le client envoie une commande PASS au serveur FTP. 
     * Elle est utilisée pour vérifier les informations d'identification du client pour l'accès au serveur.
     * Si les informations sont valides, le client est autorisé à accéder au serveur. Sinon, un message d'erreur est envoyé au client.
     *
     * @param password Le mot de passe associé au nom d'utilisateur fourni précédemment avec la commande USER.
     */
    public void handlePassCommand(String argument) {
        client.setPassword(argument);

        if (client.getUsername().equals("anonymous") && client.getPassword().equals("anonymous")) {
            client.setConnected(true);
        }

        if (client.isConnected()) {
            writer.println("230 Login successful.");
        } else {
            writer.println("530 Not logged in.");
        }
    }

    public void handleSystCommand(String argument) {
        if (client.isConnected()) {
            writer.println("215 UNIX Type: L8");
        } else {
            writer.println("530 Not logged in.");
        }
    }

    public void handleFeatCommand(String argument) {
        if (client.isConnected()) {
            writer.println("211-Features:");
            writer.println(" EPRT");
            writer.println(" EPSV");
            writer.println(" MDTM");
            writer.println(" PASV");
            writer.println(" REST STREAM");
            writer.println(" SIZE");
            writer.println(" TVFS");
            writer.println(" UTF8");
            writer.println("211 End");
        } else {
            writer.println("530 Not logged in.");
        }
    }

    public void handlePwdCommand(String argument) {
        if (client.isConnected() == true) {
            writer.println("257 \"" + workingDirectory + "\" is current directory");
        } else {
            writer.println("530 Not logged in.");
        }
    }

    /**
    * handleEpsvCommand - Handles the EPSV (Extended Passive Mode) FTP command.
    * 
    * This method is used to switch the FTP client into extended passive mode,
    * allowing for IPv6 support. This command sends a response to the client
    * indicating the IP address and port number for the data connection.
    */
    public void handleEpsvCommand(String argument) {
        if (client.isConnected() == false) {
            writer.println("530 Not logged in.");
        }
        else {
            try {
                writer.println("229 Entering Extended Passive Mode (|||" + port +"|)");
                dataConnection = dataServerSocket.accept();
                


            } catch (IOException e) {
                throw new RuntimeException(e) ;
            }
        }
    }

    /**
    handleListCommand() is responsible for handling the LIST FTP command. This command is used to retrieve a list of files and directories in a specified directory on the server.
    @return The response to be sent to the client indicating the success or failure of the LIST command.
    */
    public void handleListCommand(String argument) {
        if (client.isConnected() == false) {
            writer.println("530 Not logged in.");
        }
        else {
            try {
                System.out.println(workingDirectory);
                File currentDirectory = new File(workingDirectory);
                File[] listOfFiles = currentDirectory.listFiles();
                // Envoyer la liste des dossiers au client
                DataOutputStream dataOutputStream = new DataOutputStream(dataConnection.getOutputStream());
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy");
                for (File file : listOfFiles) {
                    StringBuilder sb = new StringBuilder();
                    String permissions = "-rwxrwxrwx";
                    if (file.isDirectory()) {
                        permissions = "d" + permissions.substring(1);
                    }
                    sb.append(permissions + " ");
                    sb.append("1 owner group ");
                    sb.append(file.length() + " ");
                    sb.append(sdf.format(file.lastModified()) + " ");
                    sb.append(file.getName() + "\r\n");
                    dataOutputStream.writeBytes(sb.toString());
                }
                dataOutputStream.flush();
                // Fermer la connexion de données
                dataOutputStream.close();
                dataConnection.close();
                writer.println("226 Transfer complete.");

            } catch (IOException e) {
                throw new RuntimeException(e) ;
            }
        }
    }

    public void handleTypeCommand(String argument) {
        if (client.isConnected() == false) {
            writer.println("530 Not logged in.");
        }
        else {
            String type = argument;
            if (type.equals("A") || type.equals("I")) {
                writer.println("200 Switching to Binary mode.");
            } else {
                writer.println("504 Unsupported type.");
            }
        }
    }

    public void handleOptsCommand(String argument) {
        if (client.isConnected() == false) {
            writer.println("530 Not logged in.");
        }
        else {
            writer.println("200 OPTS command successful.");
        }
    }

    /**
     * handleCwdCommand est la méthode qui gère la commande FTP CWD.
     * Cette commande permet à l'utilisateur de changer le répertoire de travail actuel.
     */
    public void handleCwdCommand(String argument) {

        if (client.isConnected() == false) {
            writer.println("530 Not logged in.");
        }
        else {

            String directory = argument; // /home
            String target = directory.substring(1); // home
            System.out.println("target = " + target);
            File currentDirectory = new File(workingDirectory);
            System.out.println("currentDirectory = " + currentDirectory);
            File[] listOfFiles = currentDirectory.listFiles();
            boolean found = false;

            for (File file : listOfFiles) {
                System.out.println("file.getName() = " + file.getName());

                if (file.getPath().startsWith(target) || file.getPath().startsWith(directory)) {
                    found = true;
                }
            }
            if (workingDirectory == "/"){
                workingDirectory = workingDirectory + target;
            }else {
                workingDirectory = "/" + target;
            }

            System.out.println("workingDirectory = " + workingDirectory);
            writer.println("250 Directory successfully changed.");
        }
    }

    /**
    Handles the STOR command, which uploads a file from the client to the server.
    @param argument the file name to be uploaded.
    @throws RuntimeException if there is an I/O error while transferring the file.
    */
    public void handleStorCommand(String argument) {
        if (client.isConnected() == false) {
            writer.println("530 Not logged in.");
            return;
        }
        try {
        FileOutputStream output;
		String path = workingDirectory + "/" + argument;
        System.out.println("path :" + path);
		output =new FileOutputStream(path);

		writer.println("150 File status okay about to open data connectionr \r\n");
		byte[] buffer1 = new byte[1024];

        
        InputStream input = dataConnection.getInputStream();
		int i =0;
        while ((i=input.read(buffer1))!=-1) {
					 
            output.write(buffer1,0,i);
        }
        writer.println("226 Closing data connection,file transfer successful .\r\n");
        output.close();
        dataConnection.close();

        } catch (IOException e) {
            throw new RuntimeException(e) ;
        }
    }
    /**
     * handleRetrCommand est la méthode qui gère la commande FTP RETR.
     * Cette commande permet à l'utilisateur de télécharger un fichier du serveur vers le client.
     */
    public void handleRetrCommand(String argument) {
        try {
        FileInputStream inputst ;
        String path = workingDirectory+"\\"+argument; 
        System.out.println(path); 
        File file = new File(path);
        file.createNewFile();
        if(file.exists() && !file.isDirectory()) {// verifie si le fichier existe et n'est pas un repertoire
            inputst = new FileInputStream(path);
            writer.println("150 File status okay about to open data connectionr \r\n");
            byte[] buffer = new byte[4096];
            int i=0;
            while ((i=inputst.read(buffer))!=-1) {
                OutputStream output = this.dataConnection.getOutputStream();
                output.write(buffer, 0, i);
                
            }
            writer.println(" 226 Closing data connection,file transfer successful .\r\n");
            inputst.close();
            this.dataConnection.close();
        }
        else{
            writer.println("550 access denied\r\n");
        }
    }catch (IOException e) {
        e.printStackTrace();
    }
    }

   /**
     * handlePasvCommand() - Méthode pour gérer la commande FTP PASV.
     * 
     * Cette méthode gère la logique derrière la commande FTP PASV, qui permet 
     * au client de se connecter à un port passif pour transmettre des données. 
     * 
     * @return true si la commande a réussi, false sinon.
     */
    public void handlePasvCommand(String argument) {
        if (client.isConnected() == false) {
            writer.println("530 Not logged in.");
        }
        else {
            try {
                // Créer une socket de données
                dataServerSocket = new ServerSocket(0);
                // Obtenir le port de la socket de données
                int port = dataServerSocket.getLocalPort();
                // Obtenir l'adresse IP de la socket de données
                String ip = InetAddress.getLocalHost().getHostAddress();
                // Obtenir les 4 octets de l'adresse IP
                String[] ipParts = ip.split("\\.");
                // Obtenir les 2 octets du port
                int part1 = port / 256;
                int part2 = port % 256;
                // Envoyer la réponse au client
                writer.println("227 Entering Passive Mode (" + ipParts[0] + "," + ipParts[1] + "," + ipParts[2] + "," + ipParts[3] + "," + part1 + "," + part2 + ").");
                // Accepter la connexion de données
                dataConnection = dataServerSocket.accept();
            } catch (IOException e) {
                throw new RuntimeException(e) ;
            }
        }
    }

    public void handlePortCommand(String argument) {
        if (client.isConnected() == false) {
            writer.println("530 Not logged in.");
        }
        else {
            try {
                // Obtenir les 6 paramètres de la commande PORT
                String[] parts = argument.split(",");
                // Obtenir l'adresse IP du client
                String ip = parts[0] + "." + parts[1] + "." + parts[2] + "." + parts[3];
                // Obtenir le port du client
                int port = Integer.parseInt(parts[4]) * 256 + Integer.parseInt(parts[5]);
                // Créer une socket de données
                dataConnection = new Socket(ip, port);
                // Envoyer la réponse au client
                writer.println("200 PORT command successful.");
            } catch (IOException e) {
                throw new RuntimeException(e) ;
            }
        }
    }

    public void handleQuitCommand(String argument) {
        if (client.isConnected() == false) {
            writer.println("530 Not logged in.");
        }
        else {
            try {
                writer.println("221 Goodbye.");
                dataConnection.close();
                dataServerSocket.close();
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e) ;
            }
        }
    }

    public void handleMkdCommand(String argument) {
        if (client.isConnected() == false) {
            writer.println("530 Not logged in.");
        }
        else {
            try {
                String path = workingDirectory+"\\"+argument; 
                File file = new File(path);
                file.mkdir();
                writer.println("257 \"" + argument + "\" directory created.");
            } catch (Exception e) {
                throw new RuntimeException(e) ;
            }
        }
    }

    public void handleDeleCommand(String argument) {
        if (client.isConnected() == false) {
            writer.println("530 Not logged in.");
        }
        else {
            try {
                String path = workingDirectory+"\\"+argument; 
                File file = new File(path);
                file.delete();
                writer.println("250 File successfully deleted.");
            } catch (Exception e) {
                throw new RuntimeException(e) ;
            }
        }
    }

    public void handleRmdCommand(String argument) {
        if (client.isConnected() == false) {
            writer.println("530 Not logged in.");
        }
        else {
            try {
                String path = workingDirectory+"\\"+argument; 
                File file = new File(path);
                file.delete();
                writer.println("250 Directory successfully removed.");
            } catch (Exception e) {
                throw new RuntimeException(e) ;
            }
        }
    }


    
}

