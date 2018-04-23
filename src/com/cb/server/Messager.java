package com.cb.server;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Envoyer les informations aux joueurs comme des messages ...
 */
public class Messager extends Thread {
    ArrayList<PlayerSession> playerSessions;
    ArrayList<String> messages;

    public Messager() {
        playerSessions = new ArrayList<>();
        messages = new ArrayList<>();
    }

    public void run() {
        while (true) {
            try {
                /**
                 * attente de notification avant d'envoyer le message
                 */
                synchronized (this) {
                    wait();
                }
                while (!messages.isEmpty()) {
                    sendMessageToAll(messages.remove(0));
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Envoi message a tous les joueurs
     * @param message
     */
    public void sendMessageToAll(String message){
        playerSessions.forEach(player -> {
            try {
                player.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


}
