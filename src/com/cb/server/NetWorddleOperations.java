package com.cb.server;


import java.io.IOException;

/**
 * Cette classe s'occupe de toutes les operations possible du jeu
 * comme se connecter , se deconnecter, proposer un mot,...
 */
public class NetWorddleOperations {

    private NetWorddleGame netWorddleGame ;

    public NetWorddleOperations(NetWorddleGame netWorddleGame){
        this.netWorddleGame=netWorddleGame;
    }

    public String connexion(PlayerSession playerSession, String  username, String password){
        if (netWorddleGame.players.containsKey(playerSession)){ // joueur deja present
                if (netWorddleGame.players.containsValue(username)){

                    if (netWorddleGame.playersInfo.containsKey(username) &&
                            netWorddleGame.playersInfo.get(username).equals(password)){
                        return "OK";
                        //(identifiant,mot de passe) =(ok,ok)
                    }

                    else if (netWorddleGame.playersInfo.containsKey(username) &&
                            !netWorddleGame.playersInfo.get(username).equals(password)){
                        //(identifiant,mot de passe)=(ok,bad)

                        return  "KO";
                    }
            }

        }

            netWorddleGame.players.put(playerSession,username);
            netWorddleGame.playersInfo.put(username,password);
            return "NEW";
            // nouveau joueur --> nom connu du system

    }

    public String deconnexion(PlayerSession playerSession){
        String answer = null;
        if (netWorddleGame.players.containsKey(playerSession)){
            //String name
            netWorddleGame.players.keySet().remove(playerSession);
            answer="OK";
        }
        return answer;
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
