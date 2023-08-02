# Projet 2 : Serveur FTP
## Yoni Gaudiere
### 14/02/23
#

## Introduction :

Le logiciel est un serveur FTP basé sur la norme FTP.   
Il permet aux utilisateurs de de télécharger des fichiers à partir d'un ordinateur hôte sur le réseau.   
Les utilisateurs peuvent se connecter au serveur en utilisant un client comme FileZilla, et naviguer dans les répertoires de fichiers disponibles sur le serveur.   

Les concepts clés dans ce logiciel incluent la communication client-serveur, le protocole FTP et les sockets réseau.    
Le logiciel utilise le protocole FTP pour gérer les communications entre le client et le serveur, et les sockets réseau pour gérer les connexions entre les deux.

Le logiciel gère les connexions entrantes en utilisant un socket d'écoute, et traite les commandes FTP envoyées par les clients en utilisant une analyse de chaîne pour identifier les commandes et exécuter les actions appropriées.

## Lancer le projet :

Une fois le projet téléchargé, placez-vous dans la racine du dossier 'src'.
Lancez la commande suivante :

```
java -cp target/mon-serveur-1.0-SNAPSHOT.jar org.exemple.demo.Main

```

Vous pourrez alors vous y connecter avec le port 2121 avec le user : anonymous et le pass : anonymous.

## Architecture :

La classe ClientFTP représente le client FTP qui est capable de se connecter au serveur FTP.    
Il a des variables membres qui définissent l'état de connexion, le nom d'utilisateur et le mot de passe.

La classe StartServeur représente le serveur FTP. Il écoutera sur un port donné (2121) et attendra les connexions entrantes de clients.  
Dès qu'une connexion est établie, une instance de la classe FTPServer est créée pour gérer les commandes du client. 

La classe FTPServer est responsable de la gestion des commandes FTP. Elle a plusieurs méthodes pour gérer différentes commandes telles que handleUserCommand, handlePassCommand, handleListCommand, etc.    

La classe DataServeur représente un serveur de données.     
Il a une instance de ServerSocket qui écoutera sur un port disponible pour les connexions de données entrantes.     
Les méthodes de cette classe permettent d'accepter les connexions de données et de les gérer.

Le projet a donc une architecture client-serveur avec des classes qui gèrent les connexions et les commandes du client et du serveur. 
Les données sont gérées par une classe distincte.


## Code Samples :

La classe FTPServer représente le corps du logiciel, elle gère les différentes commandes FTP qui peuvent être reçues par le serveur. Elle utilise une table de hachage commands pour stocker les méthodes associées à chaque commande.  
Cela va permettre de gérer "proprement" les différentes commandes récu.

