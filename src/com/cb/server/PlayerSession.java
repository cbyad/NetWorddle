package com.cb.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Thread s'occupant d'un seul client.
 * Tache delegué par le Serveur pour permette une communication multi-threadée
 */
public class PlayerSession extends Thread {
    private int numQuery =0 ;
    boolean exit = false;
    BufferedReader input ;
    PrintWriter output ;
    public Socket client ;
    protected NetWorddleOperations netWorddleOperations;
    protected NetWorddleGame netWorddleGame;


    /**
     *
     * @param client
     * @param netWorddleGame
     * @param netWorddleOperations
     */
    public PlayerSession(Socket client, NetWorddleGame netWorddleGame , NetWorddleOperations netWorddleOperations){
        this.client=client;
        try {
            input=new BufferedReader(new InputStreamReader(client.getInputStream()));
            output= new PrintWriter(client.getOutputStream(),true);
            this.netWorddleOperations =netWorddleOperations;
            this.netWorddleGame=netWorddleGame;
        } catch (IOException e) {
            System.out.println("Probleme Joueur deconnecté");
            System.out.println(e.getMessage());

        }
        this.start();
    }

    public void run(){
        try{

            String line;
            String response;

            while(!exit){
                line = input.readLine();
                response = queryHandler(line);

                synchronized (netWorddleGame.playersSessionUsername) {
                    netWorddleGame.playersSessionUsername.notify();
                }

                sendMessage(response);
                //System.out.println(exit);
            }
            input.close();
            output.close();
            client.close();
        }

        catch(IOException e){
            System.out.println(e.getMessage());
        }

    }

    /**
     *
     * @param query la requete a traiter
     * @return la reponse adequate
     * @throws IOException
     */
    public synchronized String queryHandler(String query) throws IOException {
        String[] parse = query.split(",");

        switch (parse[0]) {
            case "connect":
                return netWorddleOperations.connexion(this,parse[1],parse[2]);
            case "disconnect": {
                exit = true;
                return netWorddleOperations.deconnexion(this);
            }

            case "proposition" :
                return netWorddleOperations.proposition(this,parse[1]);

            //option ajouté pour connaitre son score
            case "self":
                return netWorddleOperations.getSelfGlobal(this);

            //option ajouté pour connaitre le score global d'un joueur quelconque
            case "global" :
                return netWorddleOperations.getGlobalAny(this,parse[1]);

            case "best" :
                return null;

            case "send" : netWorddleOperations.sendPrivateMessage(this,parse[1],parse[2]);

            default:
                return null ;
        }
    }

    /**
     * Envoyer un message au client et/ou couper la connexion
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException{
        if(message==null) return;

        switch (message){
            case "KO" : {
                output.println(message);
                exit=true;
            }
            default:
                output.println(message);
        }
    }


}
