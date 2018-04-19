package com.cb.server;

import com.cb.checker.AnswerChecker;

import java.util.HashMap;

/**
 * Classe principale du jeu de NetWorddle
 */
public class NetWorddleGame extends Thread {

    private PuzzleGenerator generator ;
    private AnswerChecker answerChecker;
    protected Messager messager;
    protected HashMap<PlayerSession,String> playersSessionUsername; // liste des joueurs avec (socket,identifiant)
    protected HashMap<String,String> playersInfo;
    protected char [][] gameGrid;
    protected int gameTime;
    protected HashMap<String,Integer> scores ;
    protected int playersNumbers;
    protected int playerLimit;
    /**
     *
     * @param messager sert de diffuseur de messages aux joueurs en ligne
     * @param generator grille de jeu
     * @param answerChecker checker de proposition
     */
    public NetWorddleGame(Messager messager, PuzzleGenerator generator, AnswerChecker answerChecker,int gameTime,int playerLimit){
        this.messager=messager;
        this.generator=generator;
        this.answerChecker= answerChecker;
        this.gameTime=gameTime;
        playersSessionUsername=new HashMap<>();
        playersInfo=new HashMap<>();
        initScore();
        gameGrid=generator.getGrid();
        playersNumbers=0;
        this.playerLimit=playerLimit;

        messager.start(); // demarrer le messager
    }

    /**
     * Mettre tous les scores a 0
     */
    public void initScore(){
        scores=new HashMap<>();
       playersInfo.keySet().forEach(elt-> scores.put(elt,0));
    }

    public String gridToString(){
        StringBuilder str = new StringBuilder();
        for (int i=0;i<gameGrid.length;i++){
            for (int j =0;j<gameGrid[i].length;j++){
                str.append(gameGrid[i][j]);
                str.append(",");
            }
        }
        return str.deleteCharAt(str.length()-1).toString();
    }

    public void run(){
        while(true){

            synchronized (this){
                try {
                    wait();
                    //attendre d'atteindre le nombre de joueurs avant de commencer la partie
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
                String gameStarted="start"+","+gameTime+","+gridToString();
                messager.messages.add(gameStarted);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
                messager.messages.add("fin");


        }
    }



}
