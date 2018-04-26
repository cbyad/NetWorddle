package com.cb.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Client => un joueur de NetWorddle (en mode console)
 */
public class Client {
    protected static final int PORT = 2018;
    Socket clientSocket;
    BufferedReader in;
    PrintWriter out;
    Scanner sc = new Scanner(System.in);//pour lire à partir du clavier

    public Client() {

        try {
         /*
         * les informations du serveur ( port et adresse IP ou nom d'hote
         * 127.0.0.1 est l'adresse local de la machine
         */
            clientSocket = new Socket("127.0.0.1", 2018);

            //flux pour envoyer
            out = new PrintWriter(clientSocket.getOutputStream());

            //flux pour recevoir
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            System.out.println("Connection established : " + clientSocket.getInetAddress() + " port : "
                    + clientSocket.getPort());

            Thread send = new Thread(new Runnable() {
                String msg;

                @Override
                public void run() {
                    while (true) {
                        msg = sc.nextLine();
                        out.println(msg);
                        out.flush();
                    }
                }
            });
            send.start();

            Thread receive = new Thread(new Runnable() {
                String msg;

                @Override
                public void run() {
                    try {
                        msg = in.readLine();
                        while (msg != null) {
                            System.out.println(msg);
                            msg = in.readLine();
                        }
                        System.out.println("Server disconnected");
                        out.close();
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            receive.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}




