package com.cb.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PlayerSession extends Thread {
    private Socket client ;
    boolean exit = false;
    BufferedReader input ;
    PrintWriter output ;



    public PlayerSession(Socket client){
        this.client=client;
        try {
            input=new BufferedReader(new InputStreamReader(client.getInputStream()));
            output= new PrintWriter(client.getOutputStream(),true);
            output.println("BIENVENU");
        } catch (IOException e) {
            System.out.println("Probleme Joueur deconnecté");
            System.out.println(e.getMessage());

        }

        this.start();
    }

    public void run(){

    }




}
