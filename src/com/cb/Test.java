package com.cb;

import com.cb.checker.AnswerChecker;
import com.cb.server.PuzzleGenerator;

import java.io.IOException;
import java.util.Scanner;

/**
 * Cette classe est pour tester le generateur de grille et le checker de reponse
 * Elle genere une grille n*m et demande a l'utilisateur d'entrer un mot et verifie si ce mot est bien present
 * dans la grille et dans le dictionnaire de mot
 */
public class Test {

    public static void main(String[] args) throws IOException{
        String path = "files/worddle/dicesets/american.diceset";
        String dict = "files/worddle/dictionaries/american-english.dict";
        PuzzleGenerator generator = new PuzzleGenerator(5,5,path);
        char [][] grid = generator.getGrid();
        generator.printGrid();

        AnswerChecker ac = new AnswerChecker();
        ac.setDictionary(dict);

        // read the lines on stdin and check if the words are valid
        try (Scanner sc = new Scanner(System.in))
        {
            while (sc.hasNextLine())
            {
                String word = sc.nextLine().trim();
                if (word.length() > 0)
                {
                    System.out.println("Match connexity? " + ac.checkConnexity(grid, word));
                    System.out.println("Match dictionary? " + ac.checkDictionary(word));
                    System.out.println("Globally match? "+ ac.isValidAnswer(grid, word));
                }
            }
        }
    }
}
