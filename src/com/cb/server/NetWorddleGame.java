package com.cb.server;

import com.cb.checker.AnswerChecker;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Classe principale du jeu de NetWorddle
 */
public class NetWorddleGame extends Thread {

    private PuzzleGenerator generator ;
    private AnswerChecker answerChecker;
    private Messager messager;
    public HashMap<PlayerSession,String> players; // liste des joueurs avec (socket,identifiant)
    public HashMap<String,String> playersInfo;
    char [][] gameGrid;
    int time= 0;
    public HashMap<String,Integer> scores ;
    int playersNumbers =0 ;
    /**
     *
     * @param messager sert de diffuseur de message aux joueurs en ligne
     * @param generator grille de jeu
     * @param answerChecker checker de proposition
     */
    public NetWorddleGame(Messager messager, PuzzleGenerator generator, AnswerChecker answerChecker){
        this.messager=messager;
        this.generator=generator;
        this.answerChecker= answerChecker;
        players=new HashMap<>();
        playersInfo=new HashMap<>();
        initScore();
        gameGrid=generator.getGrid();

        messager.start(); // demarrer le messager
    }

    /**
     * Mettre tous les scores a 0
     */
    public void initScore(){
        scores=new HashMap<>();
       playersInfo.keySet().forEach(elt-> scores.put(elt,0));
    }

    public void run(){
        while(true){

        }
    }



}
