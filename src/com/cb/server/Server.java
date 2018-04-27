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
    public final static String PATH = "files/worddle/dicesets/american.diceset";

    protected static final int NB_PLAYERS=2 ;
    private ServerSocket listen;
    private PuzzleGenerator generator;
    private AnswerChecker answerChecker;
    private Messager messager;
    private NetWorddleOperations netWorddleOperations;
    private NetWorddleGame netWorddleGame;
    private int gameTime;
    protected int playerLimit;
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
    public Server(int port,int n, int m,int time, String dictionnaryPath, String dicesPath) {

        try {
            this.playerLimit = NB_PLAYERS;
            listen = new ServerSocket(port);
            pool = Executors.newFixedThreadPool(playerLimit);
            generator = new PuzzleGenerator(n, m, dicesPath);
            this.gameTime=time;

            playerSessions = new ArrayList<>();
            answerChecker = new AnswerChecker();
            answerChecker.setDictionary(dictionnaryPath);

            messager = new Messager();
            netWorddleGame= new NetWorddleGame(messager,generator,answerChecker,gameTime,playerLimit,n,m);
            netWorddleOperations= new NetWorddleOperations(netWorddleGame);

        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        System.out.println("Listening on port : "+ port);
        this.start(); // appel de la fonction public void run() de la routine
        netWorddleGame.start(); // demarrage du jeux
    }


    public void run() {
            try {
                Socket client;
                while (true) {
                    System.out.println("Waiting for connection...");
                    client = listen.accept();

                    if (playerSessions.size() >= playerLimit) {
                        // refuser le client qui tente de se connecter
                        this.refuseClient(client);
                        System.out.println("Max number of players reached : "+playerSessions.size());
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

    /**
     * Refuser le client quand le nombre de connexion possible est atteint
     * @param socket
     * @throws IOException
     */
    private void refuseClient(Socket socket) throws IOException {
        PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
        out.println("KO/");
        out.close();
        socket.close();
    }


    public static void main(String[] args) {
        String dict = "files/worddle/dictionaries/american-english.dict"; // par defauft
        //String dict = args[4];  decommenté cette ligne pour passer un autre dictionnaire en argument
        int port = Integer.valueOf(args[0]);
        int n = Integer.valueOf(args[1]);
        int m = Integer.valueOf(args[2]);
        int time = Integer.valueOf(args[3]);

        Server s =new Server(port, n, m,time, dict, PATH);
        //s.generator.printGrid();
    }

}
