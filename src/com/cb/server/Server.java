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
    private NetWorddleOperations netWorddleOperations;
    private NetWorddleGame netWorddleGame;

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

            this.playerLimit = playerLimit;
            playerSessions = new ArrayList<PlayerSession>();
            answerChecker = new AnswerChecker();
            answerChecker.setDictionary(dictionnaryPath);

            messager = new Messager();
            netWorddleGame= new NetWorddleGame(messager,generator,answerChecker);
            netWorddleOperations= new NetWorddleOperations(netWorddleGame);


        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        System.out.println("En écoute sur le port : "+ PORT);
        this.start(); // appel de la fonction public void run() de la routine
        netWorddleGame.start(); // demarrage du jeux !! ????
    }


    public void run() {
            try {
                Socket client;
                while (true) {
                    System.out.println("Attente de connexion...");
                    client = listen.accept();

                    if (playerSessions.size() > playerLimit) {
                        // refuser le client qui tente de se connecter
                        this.refuseClient(client);
                        System.out.println("Le nomnre max de participants est atteint : "+playerSessions.size());
                    } else {
                        // ci signifie client i avec i appartenant a [0;playerLimit]
                        PlayerSession ci = new PlayerSession(client,netWorddleGame,netWorddleOperations);
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
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        out.println("KO/");
        out.flush();
        out.close();
        socket.close();
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
