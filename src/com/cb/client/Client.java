package com.cb.client;


import java.io.*;
import java.net.Socket;


/**
 * Client = un joueur de NetWorddle
 */
public class Client {
    protected static final int PORT = 2018;

    public static void main(String[] args) {
        Socket s = null;

        try {
            s = new Socket("localhost", PORT);

            BufferedReader input=new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter output= new PrintWriter(s.getOutputStream(),true);


            System.out.println("Connexion etablie : " + s.getInetAddress() + " port : " + s.getPort());
            String ligne ;

            char c;

            while (true) {
                ligne = "";
                while ((c = (char) System.in.read()) != '\n'){
                    ligne = ligne + c;
                }
                output.println(ligne); // envoie au serveur

                String answer = input.readLine(); // lire la reponse
                if (answer == null) {
                    System.out.println("Connexion terminee");
                    break;
                }
                System.out.println("" + answer);
            }

        } catch (IOException e) {
            System.err.println(e);
        } finally {
            try {
                if (s != null) s.close();
            } catch (IOException e2) {
            }
        }
    }
}



