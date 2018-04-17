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

    public static final String TEXT_FILE_CHARSET = "UTF-8";
    public ArrayList<char[]> dices;
    private char[][] grid;

    public PuzzleGenerator(int n, int m, String path) {
        this.dices = new ArrayList<>();
        loadDices(path);
        grid = generate(n, m);
    }

    public char[][] getGrid() {
        return grid;
    }

    public void setGrid(char[][] grille) {
        this.grid = grille;
    }

    /**
     * @param n
     * @param m
     * @return grille[n][m]
     */
    private char[][] generate(int n, int m) {
        char grille[][] = new char[n][m];
        int nbrDices = dices.size();
        int nbrDicesUtils = n * m;
        ArrayList<char[]> newDices = new ArrayList<>(nbrDicesUtils);
        Random rand = new Random();

        if (nbrDicesUtils <= nbrDices) { // alors on utilise seulement les (n*m) dés
            for (int i = 0; i < nbrDicesUtils; i++)
                newDices.add(this.dices.get(i));
        }

        else { // on reutilise certains dés pour combler le vide
            for (int i = 0; i < nbrDices; i++) //  [0----nbrDices[
                newDices.add(this.dices.get(i));

            for (int i = nbrDices; i < nbrDicesUtils; i++)  // [nbrDices-----n*m[
                newDices.add(i, this.dices.get(rand.nextInt(nbrDices))); //  on duplique aleatoirement les premier dé dans le reste 
        }

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

    /**
     * affichage de la grille
     */
    public void printGrid() {

        for (int i = 0; i < grid.length; i++) {
            System.out.print(" | ");
            for (int j = 0; j < grid[i].length; j++) {
                System.out.print(grid[i][j]);
                System.out.print(" | ");
            }
            System.out.println("");
        }
    }


    /**
     * @param dice
     */
    private void addDice(char[] dice) {
        this.dices.add(dice);

    }

    /**
     * @param path
     */
    private void loadDices(String path) {
        try {
            InputStream flux = new FileInputStream(path);
            InputStreamReader lecture = new InputStreamReader(flux,TEXT_FILE_CHARSET);
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
            throw new IllegalArgumentException("Erreur avec le fichier d'entree");
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
        PuzzleGenerator gen = new PuzzleGenerator(5, 5, path);
        gen.printGrid();

    }

}
