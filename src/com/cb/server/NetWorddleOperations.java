package com.cb.server;


import java.io.IOException;

/**
 * Cette classe traite toutes les operations possible du jeu
 * comme se connecter , se deconnecter, proposer un mot,...
 */
public class NetWorddleOperations {

    private NetWorddleGame netWorddleGame;

    public NetWorddleOperations(NetWorddleGame netWorddleGame) {
        this.netWorddleGame = netWorddleGame;
    }

    /**
     * @param playerSession
     * @param username
     * @param password
     * @return
     */
    public String connexion(PlayerSession playerSession, String username, String password) {

        if (netWorddleGame.playersInfo.containsKey(username)) { // joueur deja present
            if (netWorddleGame.playersInfo.get(username).equals(password)) {
                return "OK"; //(id,mdp)=(ok,ok)
            } else
                return "KO"; //(id,mdp)=(ok,bad)
        } else {
            netWorddleGame.playersSessionUsername.put(playerSession, username);
            netWorddleGame.playersInfo.put(username, password);
            netWorddleGame.playersNumbers++;
            netWorddleGame.scores.put(username, 0);

            synchronized (netWorddleGame.messager) {
            netWorddleGame.messager.messages.add(username + " vient de se connecter");
                netWorddleGame.messager.notify();
            }
            netWorddleGame.messager.playerSessions.add(playerSession);

            //Je ne suis pas sure de ca pour le moment
            if (netWorddleGame.playersNumbers==netWorddleGame.playerLimit){
                //on a atteint de le nombre de joueur voulu on peut debuter la partie
                synchronized (netWorddleGame){
                    netWorddleGame.notify();
                }
            }

            return "NEW";
            // nouveau joueur --> nom connu du system
        }
    }

    /**
     *
     * @param playerSession
     * @return
     */
    public String deconnexion(PlayerSession playerSession) {
        String username = netWorddleGame.playersSessionUsername.get(playerSession);
        String password = netWorddleGame.playersInfo.get(username);

        netWorddleGame.playersSessionUsername.remove(playerSession, username);
        netWorddleGame.playersInfo.remove(username, password);
        netWorddleGame.playersNumbers--;

        netWorddleGame.messager.playerSessions.remove(playerSession);
        synchronized (netWorddleGame.messager){
            netWorddleGame.messager.messages.add(username+" vient de quitter");
            netWorddleGame.messager.notify();
        }

        return "OK";
    }


    public String proposition(PlayerSession playerSession, String word) {
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
