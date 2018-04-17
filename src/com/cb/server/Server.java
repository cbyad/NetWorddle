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
    private ArrayList<PlayerSession> playerSessions;
    private AnswerChecker answerChecker;


    /**
     *
     * @param playerLimit le nombre de joueurs simultanés
     * @param n  le nombre de lignes de la grille de jeu
     * @param m  le nombre de colonnes de la grille de jeu
     * @param dictionnaryPath le chemin vers le dictionnaire de mot
     * @param dicesPath le chemin vers les dés
     */
    public Server(int playerLimit, int n, int m, String dictionnaryPath, String dicesPath) {

        try {
            listen = new ServerSocket(PORT);
            pool = Executors.newFixedThreadPool(playerLimit);
            generator = new PuzzleGenerator(n, m, dicesPath);
            messager = new Messager();
            this.playerLimit = playerLimit;
            playerSessions = new ArrayList<PlayerSession>();
            answerChecker = new AnswerChecker();
            answerChecker.setDictionary(dictionnaryPath);

        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        System.out.println("En écoute sur le port : "+ PORT);
        this.start(); // appel de la fonction public void run() de la routine

    }


    public void run() {
            try {
                Socket client;
                while (true) {
                    System.out.println("Attente de connexion...");
                    client = listen.accept();

                        System.out.println(playerSessions.size());
                    if (playerSessions.size() >= playerLimit) {
                        System.out.println(playerSessions.size());
                        // refuser le client qui tente de se connecter
                        this.refuseClient(client);
                    } else {
                        // ci signifie client i avec i appartenant a [0;playerLimit]
                        PlayerSession ci = new PlayerSession(client);
                        playerSessions.add(ci);
                        pool.execute(ci);
                    }
                }

            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.exit(-1);
            }

    }

    private void refuseClient(Socket socket) throws IOException {
        System.out.println("refus en cours...");
        PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
        out.println("KO/");
        out.flush();
        out.close();
        socket.close();
    }


    private int giveScore(String word) {
        int size = word.length();
        if (size == 3 || size == 4) return 1;
        if (size == 5) return 2;
        if (size == 6) return 3;
        if (size == 7) return 5;
        if (size >= 8) return 11;
        else return 0;
    }


    public static void main(String[] args) {
        String path = "files/worddle/dicesets/american.diceset";
        String dict = "files/worddle/dictionaries/american-english.dict";
        Scanner sc = new Scanner(System.in);
        System.out.println("Nombre maximum de joueurs");
        int nb = sc.nextInt();
        new Server(nb, 4, 5, dict, path);
        sc.close();
    }

}
