package com.cb.client;

/**
 * Tout le protocol supporté par le jeu est encapsulé
 * dans des methodes pour plus de maintenance
 */
public interface ProtocolMessage {
    void toConnect(String username, String password);
    void toDisconnect();
    void toPropose(String word);
    void sendPrivateMessage(String username,String message);
    void getSelfScore();
    void getOtherScore(String username);

}
