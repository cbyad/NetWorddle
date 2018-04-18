package com.cb.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Thread s'occupant d'un seul client. Tache delegué par le Serveur pour permette une communication multi-threadée
 */
public class PlayerSession extends Thread {
    private int numQuery =0 ;
    boolean exit = false;
    BufferedReader input ;
    PrintWriter output ;
    public Socket client ;
    private NetWorddleOperations netWorddleOperations;
    private NetWorddleGame netWorddleGame;


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
                response = queryManager(line);

                /*
                synchronized (wordle.players) {
                    worddle.players.notify();
                }
                */
                sendMessage(response);
            }
            client.close();
            input.close();
            output.close();
        }

        catch(IOException e){
            System.out.println(e.getMessage());
        }

    }

    /**
     *
     * @param line la requete a traiter
     * @return la reponse adequate
     * @throws IOException
     */
    public synchronized String queryManager(String line) throws IOException {
        String[] parse = line.split(",");
        switch (parse[0]) {
            case "connect":
                return netWorddleOperations.connexion(this,parse[1],parse[2]);
            case "disconnect":
                return netWorddleOperations.deconnexion(this);

            default:
                return null ;
        }
    }


    public void sendMessage(String message) throws IOException{
        if(message==null) return;
        output.println(message);
    }




}
