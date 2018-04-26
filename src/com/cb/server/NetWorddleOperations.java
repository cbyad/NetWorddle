package com.cb.server;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

/**
 * Cette classe traite toutes les opérations possible du jeu
 * comme se connecter , se deconnecter, proposer un mot,...
 */
public class NetWorddleOperations {

    protected NetWorddleGame netWorddleGame;

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

        /**
         * Joueur déja connu par le serveur
         */
        if (netWorddleGame.playersInfo.containsKey(username)) {
            if (netWorddleGame.playersInfo.get(username).equals(password)) {
                return "OK"; //(id,mdp)=(ok,ok)
            } else
                return "KO"; //(id,mdp)=(ok,bad)
        } else {
            netWorddleGame.playersSessionUsername.put(playerSession, username);
            netWorddleGame.playersInfo.put(username, password);
            netWorddleGame.playersNumbers++;
            netWorddleGame.scores.put(username, 0);
            netWorddleGame.wordsAlreadyFound.put(playerSession,new ArrayList<>());

            synchronized (netWorddleGame.messager) {
            netWorddleGame.messager.messages.add(username + " is connected");
                netWorddleGame.messager.notify();
            }
            netWorddleGame.messager.playerSessions.add(playerSession);

            /**
             * on a atteint de le nombre de joueurs voulu (2 dans notre jeux) on peut débuter la partie
             */
            if (netWorddleGame.playersNumbers==netWorddleGame.playerLimit){
                synchronized (netWorddleGame){
                    netWorddleGame.notify();
                }
            }
            return "NEW";
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
        netWorddleGame.wordsAlreadyFound.remove(playerSession);
        netWorddleGame.messager.playerSessions.remove(playerSession);

        synchronized (netWorddleGame.messager){
            netWorddleGame.messager.messages.add(username+" left the game");
            netWorddleGame.messager.notify();
        }
        return "OK";
    }

    /**
     *
     * @param playerSession
     * @param word
     * @return
     */
    public String proposition(PlayerSession playerSession, String word) {
        String notValid= "score,0";

        if (word.length()>1) {
            if (netWorddleGame.answerChecker.isValidAnswer(netWorddleGame.gameGrid,word)){

                /**
                 * Si le mot a deja été trouvé par le meme joueur on ne le rajoute pas
                 */
                boolean isPresent=netWorddleGame.wordsAlreadyFound.get(playerSession).contains(word);
                if (isPresent){
                    return notValid;
                }

                int score =giveScore(word);
                String username = netWorddleGame.playersSessionUsername.get(playerSession);
                netWorddleGame.scores.put(username,netWorddleGame.scores.get(username)+score);
                ArrayList<String> newArray = netWorddleGame.wordsAlreadyFound.get(playerSession);
                newArray.add(word);
                netWorddleGame.wordsAlreadyFound.put(playerSession,newArray);
                return "score"+","+score;
            }
            else return notValid;
        }
        return notValid;

    }

    /**
     *
     * @param playerSession
     * @return le score global cumulé par un soi même
     */
    public String getSelfGlobal(PlayerSession playerSession) {
        String username = netWorddleGame.playersSessionUsername.get(playerSession);
        int globalScore =netWorddleGame.scores.get(username);
        return "score"+","+username+","+globalScore;
    }

    /**
     *
     * @param playerSession
     * @param s
     * @return le score global cumulé par un utilisateur quelconque
     */
    public String getGlobalAny(PlayerSession playerSession, String s) {
        int globalScore =netWorddleGame.scores.get(s);
        return "score"+","+s+","+globalScore;
    }

    /**
     * Recuperer la session du joueur en fonction de son nom
     * @param value
     * @return
     */
    public  PlayerSession getKey(String value){
        for (Map.Entry<PlayerSession,String> entry : netWorddleGame.playersSessionUsername.entrySet()){
            if (Objects.equals(value,entry.getValue()))
                return entry.getKey();
        }
        return null ;
    }

    /**
     * Envoi d'un message privé entre joueur
     * ( -_- on peut tricher avec ça oups! j'ai rien dit :p)
     * @param playerSession
     * @param name
     * @param message
     * @throws IOException
     */
    public void sendPrivateMessage(PlayerSession playerSession,String name, String message) throws IOException {
        PlayerSession cible = getKey(name);
        if (cible!=null){
        cible.sendMessage(message);
        }
    }

    /**
     * Strategie de notation des mots . Juste un coix d'implem on peut la changer
     * @param word
     * @return le score du mot
     */
    public int giveScore(String word) {
        int size = word.length();
        if (size == 3 || size == 4) return 1;
        if (size == 5) return 2;
        if (size == 6) return 3;
        if (size == 7) return 5;
        if (size >= 8) return 11;
        else return 0;
    }

}
