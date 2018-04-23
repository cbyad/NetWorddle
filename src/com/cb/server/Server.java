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

/**
 * Serveur de jeu
 */
public class Server extends Thread {

    protected static final int PORT = 2018; // mettre en ligne de commande plus tard
    protected static final int NB_PLAYERS=2 ;
    private ServerSocket listen;
    private PuzzleGenerator generator;
    private AnswerChecker answerChecker;
    private Messager messager;
    private NetWorddleOperations netWorddleOperations;
    private NetWorddleGame netWorddleGame;
    private int gameTime;

    protected int playerLimit; // de base on met a 2 mais pour le rendre plus generique
    private ExecutorService pool;
    private ArrayList<PlayerSession> playerSessions;


    /**
     *
     * @param n  le nombre de lignes de la grille de jeu
     * @param m  le nombre de colonnes de la grille de jeu
     * @param time temps de jeux (en seconde)
     * @param dictionnaryPath le chemin vers le dictionnaire de mot
     * @param dicesPath le chemin vers les dés
     */
    public Server(int n, int m,int time, String dictionnaryPath, String dicesPath) {

        try {
            this.playerLimit = NB_PLAYERS;
            listen = new ServerSocket(PORT);
            pool = Executors.newFixedThreadPool(playerLimit);
            generator = new PuzzleGenerator(n, m, dicesPath);
            this.gameTime=time;

            playerSessions = new ArrayList<>();
            answerChecker = new AnswerChecker();
            answerChecker.setDictionary(dictionnaryPath);

            messager = new Messager();
            netWorddleGame= new NetWorddleGame(messager,generator,answerChecker,gameTime,playerLimit);
            netWorddleOperations= new NetWorddleOperations(netWorddleGame);

        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        System.out.println("En écoute sur le port : "+ PORT);
        this.start(); // appel de la fonction public void run() de la routine
        netWorddleGame.start(); // demarrage du jeux
    }


    public void run() {
            try {
                Socket client;
                while (true) {
                    System.out.println("Attente de connexion...");
                    client = listen.accept();

                    if (playerSessions.size() >= playerLimit) {
                        // refuser le client qui tente de se connecter
                        this.refuseClient(client);
                        System.out.println("Le Nombre max de participants est atteint : "+playerSessions.size());
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
        PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
        out.println("KO/");
        out.close();
        socket.close();
    }



    public static void main(String[] args) {
        String path = "files/worddle/dicesets/american.diceset";
        String dict = "files/worddle/dictionaries/american-english.dict";
       // Scanner sc = new Scanner(System.in);
        //System.out.println("Nombre maximum de joueurs");
        //int nb = sc.nextInt();
        Server s =new Server( 4, 4,120, dict, path);
        s.generator.printGrid();
       // sc.close();
    }

}
