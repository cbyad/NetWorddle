package com.cb.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Thread s'occupant d'un seul client
 */
public class PlayerSession extends Thread {
    boolean exit = false;
    BufferedReader input ;
    PrintWriter output ;
    private Socket client ;
    private NetWorddleOperations netWorddleOperations;
    private PuzzleGenerator generator;



    public PlayerSession(Socket client, PuzzleGenerator generator , NetWorddleOperations netWorddleOperations){
        this.client=client;
        try {
            input=new BufferedReader(new InputStreamReader(client.getInputStream()));
            output= new PrintWriter(client.getOutputStream(),true);
            this.netWorddleOperations =netWorddleOperations;
            this.generator=generator;
            //output.println("BIENVENU");
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

    public synchronized String queryManager(String line) throws IOException {
        String[] parse = line.split(",");
        switch (parse[0]) {
            case "connect":
                return netWorddleOperations.connexion(this,parse[1],parse[2]);

            default:
                return null ;
        }
    }


    public void sendMessage(String message) throws IOException{
        if(message==null) return;
        output.println(message);
    }




}
