package com.cb.server;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Classe pour la generation aleatoire de la grille de jeu
 * n*m
 */
public class PuzzleGenerator {

    public ArrayList<char[]> dices;
    public char[][] grille;

    public PuzzleGenerator(int n, int m, String path) {
        this.dices = new ArrayList<>();
        loadDices(path);
        grille = generate(n, m);
    }

    /**
     * @param n
     * @param m
     * @return grille[n][m]
     */
    public char[][] generate(int n, int m) {
        char grille[][] = new char[n][m];
        int nbrDices = dices.size();
        int nbrDicesUtils = n * m;
        ArrayList<char[]> newDices = new ArrayList<>(nbrDicesUtils);

        if (nbrDicesUtils <= nbrDices) { // alors on utilise seulement les (n*m) dés
            for (int i = 0; i < nbrDicesUtils; i++)
                newDices.add(this.dices.get(i));
        }

        else { // on reutilise certains dés pour combler le vide
            for (int i = 0; i < nbrDices; i++) //  [0----nbrDices]
                newDices.add(this.dices.get(i));

            for (int i = nbrDices; i < nbrDicesUtils; i++)  // [nbrDices-----n*m]
                newDices.add(i, this.dices.get(0)); // juste le premier est dupliquer a tous on peut faire un rand mais meme principe
        }

        Random rand = new Random();
        Collections.shuffle(newDices); // on melange les dés pour reduire le biais
        int cmpt = 0; // increment pour le nombre de dé dans dices

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int pickIndex = rand.nextInt(newDices.get(0).length); //on tire une face du dé aleatoirement
                grille[i][j] = newDices.get(cmpt)[pickIndex]; // on l'insere dans la case [i][j] de la grille
                cmpt++;
            }
        }
        return grille;
    }


    public void printGrid() {

        for (int i = 0; i < grille.length; i++) {
            System.out.print("|");
            for (int j = 0; j < grille[i].length; j++) {
                System.out.print(grille[i][j]);
                System.out.print("|");
            }
            System.out.println("");
        }
    }


    /**
     * @param dice
     */
    public void addDice(char[] dice) {
        this.dices.add(dice);

    }


    /**
     * @param path
     */
    public void loadDices(String path) {
        try {
            InputStream flux = new FileInputStream(path);
            InputStreamReader lecture = new InputStreamReader(flux);
            BufferedReader buff = new BufferedReader(lecture);

            String ligne;
            while ((ligne = buff.readLine()) != null) {
                String tmp = ligne;
                char[] dice = new char[tmp.length()];
                for (int i = 0; i < tmp.length(); i++) {
                    dice[i] = tmp.charAt(i);
                }
                addDice(dice);
            }
            buff.close();

        } catch (Exception e) {
            throw new IllegalArgumentException("erreur avec le fichier d'entree");
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < dices.size(); i++) {
            for (int j = 0; j < dices.get(i).length; j++)
                str.append(dices.get(i)[j]);
            str.append("\n");
        }
        return str.toString();
    }

    // teste du generateur
    public static void main(String[] args) {

        String path = "files/worddle/dicesets/american.diceset";
        PuzzleGenerator gen = new PuzzleGenerator(4, 4, path);
        gen.printGrid();

    }

}
