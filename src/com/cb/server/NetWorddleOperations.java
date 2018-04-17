package com.cb.server;


/**
 * Cette classe s'occupe de toutes les operations possible du jeu
 * comme se connecter , se deconnecter, proposer un mot,...
 */
public class NetWorddleOperations {

    private PuzzleGenerator generator ;

    public NetWorddleOperations(PuzzleGenerator generator){
        this.generator=generator;
    }

    public String connexion(PlayerSession playerSession, String  username, String password){
        return "";
    }


    public String proposition(PlayerSession playerSession, String word){
        return "";
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


}
