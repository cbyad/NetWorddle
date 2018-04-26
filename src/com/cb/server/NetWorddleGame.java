package com.cb.server;

import com.cb.checker.AnswerChecker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Classe principale du jeu de NetWorddle
 */
public class NetWorddleGame extends Thread {

    protected PuzzleGenerator generator;
    protected AnswerChecker answerChecker;
    protected Messager messager;
    protected HashMap<String, String> playersInfo;
    protected HashMap<PlayerSession, String> playersSessionUsername; // liste des joueurs avec (socket,identifiant)
    protected HashMap<PlayerSession,ArrayList<String>> wordsAlreadyFound;
    protected HashMap<String, Integer> scores;
    protected char[][] gameGrid;
    protected int gameTime;
    protected int playersNumbers;
    protected int playerLimit;
    protected boolean endGame;
    protected int n;
    protected int m;

    /**
     * @param messager      sert de diffuseur de messages aux joueurs en ligne
     * @param generator     grille de jeu
     * @param answerChecker verificateur de proposition
     */
    public NetWorddleGame(Messager messager, PuzzleGenerator generator, AnswerChecker answerChecker, int gameTime, int playerLimit,int n,int m) {
        this.messager = messager;
        this.generator = generator;
        this.answerChecker = answerChecker;
        this.gameTime = gameTime;
        playersSessionUsername = new HashMap<>();
        playersInfo = new HashMap<>();
        initScore();
        gameGrid = generator.getGrid();
        playersNumbers = 0;
        this.playerLimit = playerLimit;
        endGame = false;
        wordsAlreadyFound= new HashMap<>();
        this.n=n;
        this.m=m;
        messager.start(); // demarrer le messager
    }

    /**
     * Mettre tous les scores a 0
     */
    public void initScore() {
        scores = new HashMap<>();
        playersInfo.keySet().forEach(player -> scores.put(player, 0));
    }

    /**
     * Convertir la grille char[][] en chaine de caractere pour respecter le protocole de
     * communication
     * @return
     */
    public String gridToString() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < gameGrid.length; i++) {
            for (int j = 0; j < gameGrid[i].length; j++) {
                str.append(gameGrid[i][j]);
                str.append(",");
            }
        }
        return str.deleteCharAt(str.length() - 1).toString();
    }

    public long toMilliSecond(){
        return gameTime*1000;
    }

    public void gameEndedMessage(){
        PlayerSession p1 = (PlayerSession) playersSessionUsername.keySet().toArray()[0];
        PlayerSession p2 = (PlayerSession) playersSessionUsername.keySet().toArray()[1];

        String username1 = playersSessionUsername.get(p1);
        String username2 = playersSessionUsername.get(p2);

        StringBuilder msg1 = new StringBuilder();
        StringBuilder msg2 = new StringBuilder();


        if (!wordsAlreadyFound.get(p2).isEmpty()) {
            wordsAlreadyFound.get(p2).stream().filter(word -> word.length() > 2).forEach(word -> {
                msg1.append(word);
                msg1.append("/");
                msg1.append(p2.netWorddleOperations.giveScore(word));
                msg1.append(",");
            });
            int lastChar1 = msg1.length() - 1;
            msg1.deleteCharAt(lastChar1);
        }

        if (!wordsAlreadyFound.get(p1).isEmpty()) {
            wordsAlreadyFound.get(p1).stream().filter(word -> word.length() > 2).forEach(word -> {
                msg2.append(word);
                msg2.append("/");
                msg2.append(p1.netWorddleOperations.giveScore(word));
                msg2.append(",");
            });

            int lastChar2 = msg2.length() - 1;
            msg2.deleteCharAt(lastChar2);
        }

        String finalMsg1 ="stop"+","+msg1.toString();
        String finalMsg2 ="stop"+","+msg2.toString();

        try {
            p1.netWorddleOperations.sendPrivateMessage(p1,username2,finalMsg1);
            p2.netWorddleOperations.sendPrivateMessage(p2,username1,finalMsg2);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run() {
        while (!endGame) {

            synchronized (this) {
                try {
                    /**
                     * attendre d'atteindre le nombre de joueurs avant de commencer la partie
                     */
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String gameStarted = "start" + "," + gameTime + ","+n+ ","+m+","+gridToString();

            /**
             * On envoie la grille de jeu, le temps aux differents joueurs
             */
            synchronized (messager) {
            messager.messages.add(gameStarted);
            messager.notify();
            }

            /**
             * Tant que le temps de jeux n'est pas écoulé, on reste a l'ecoute des propositons les propositions
             */
            try {
                Thread.sleep(toMilliSecond());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            /**
             * Parcourir toutes les sessions de joueurs et leur envoyer un message
             * qui correspond au score des adversaires
             */

            gameEndedMessage();

            synchronized (messager) {
                messager.messages.add("game ended ");
                messager.notify();
            }
            endGame=true;
            playersSessionUsername.keySet().stream().forEach(p-> p.exit=true);
        }
    }


}
