package com.cb.server;

import com.cb.checker.AnswerChecker;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends Thread {

    protected static final int PORT = 2018; // mettre en ligne de commande plus tard
    private ServerSocket listen;
    private PuzzleGenerator generator;
    private Messager messager;

    protected int playerLimit; // de base on met a 2 mais pour le rendre plus generique
    private ExecutorService pool;
    private ArrayList<PlayerSession> playerList;
    private AnswerChecker answerChecker;


    public Server(int playerLimit, int n, int m, String dictionnaryPath, String dicesPath) {

        try {
            listen = new ServerSocket(PORT);
            pool = Executors.newFixedThreadPool(playerLimit);
            generator = new PuzzleGenerator(n, m, dicesPath);
            messager = new Messager();
            this.playerLimit = playerLimit;
            playerList = new ArrayList<>();
            answerChecker = new AnswerChecker();
            answerChecker.setDictionary(dictionnaryPath);

        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        this.start(); // appel de la fonction public void run() de la routine

    }


    @Override
    public void run() {
        while (true) {
            try {
                Socket client;
                System.out.println("Attente de connexion...");
                client = listen.accept();

                if (playerList.size() >= playerLimit) {
                    // refuser le client qui tente de se connecter
                    refuseClient(client);
                } else {
                    // ci signifie client i avec i appartenant a [0;playerLimit]
                    PlayerSession ci = new PlayerSession();
                    playerList.add(ci);
                    pool.execute(ci);
                }


            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.exit(-1);
            }
        }

    }

    private void refuseClient(Socket socket) throws IOException {
        PrintWriter out  = new PrintWriter(socket.getOutputStream());
        out.println("REFUSER/");
        out.flush();
        out.close();
    }

    public static void main(String[] args) {
        String path = "files/worddle/dicesets/american.diceset";
        String dict = "files/worddle/dictionaries/american-english.dict";
        Scanner sc = new Scanner( System.in );
        System.out.println("Nombre maximum de joueurs");
        int nb = sc.nextInt();
        new Server(nb,4,5,dict,path);
        sc.close();
    }

}
