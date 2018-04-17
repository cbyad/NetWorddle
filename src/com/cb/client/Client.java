package com.cb.client;


import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
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
            DataInputStream input = new DataInputStream(s.getInputStream());
            PrintStream output = new PrintStream(s.getOutputStream());

            System.out.println("Connexion etablie : " +
                    s.getInetAddress() + " port : " + s.getPort());
            String ligne ;
            char c;

            while (true) {
                ligne = "";
                while ((c = (char) System.in.read()) != '\n'){
                    ligne = ligne + c;
                }
                output.println(ligne);
                output.flush();
                ligne = input.readLine();
                if (ligne == null) {
                    System.out.println("Connexion terminee");
                    break;
                }
                System.out.println("" + ligne);
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



