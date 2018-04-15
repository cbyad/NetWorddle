package com.cb.checker;

import java.io.*;
import java.util.*;

/**
 * A class to check the validity of words proposed during a Worddle game session.
 * 
 * This is free software released under the Apache license v2 
 *
 * @author chilowi at u-pem.fr
 * @license Apache license v2 <https://www.apache.org/licenses/LICENSE-2.0>
 */
public class AnswerChecker
{
	public static final String TEXT_FILE_CHARSET = "UTF-8";
	
	/** The currently used dictionary */
	private Set<String> dictionary = null;
	
	/**
	 * Set a dictionary that will be used to check the words
	 */
	public void setDictionary(Set<String> dictionary)
	{
		this.dictionary = dictionary;
	}
	
	/** Load the lines from a file to a collection */
	private static void loadLines(String filepath, Collection<String> dictionary) throws IOException
	{
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), TEXT_FILE_CHARSET)))
		{
			String line = br.readLine();
			while (line != null)
			{
				dictionary.add(line.trim().toLowerCase()); // convert to lowercase and remove useless starting and trailing whitespaces
				line = br.readLine();
			}
		}
	}
	
	/**
	 * Set a dictionary (loading it from a file; each line being a word of the dictionary)
	 */
	public void setDictionary(String filepath) throws IOException
	{
		Set<String> dictionary = new HashSet<>();
		loadLines(filepath, dictionary);
		setDictionary(dictionary);
	}
	
	/**
	 * Recursive method to test if a word is obtainable following the adjacent cells of the grid
	 */
	private static boolean checkConnexity(char[][] grid, String word, HashSet<Integer> path, int startRow, int startCol)
	{
		if (path.size() == 0) 
		{
			// first call
			for (int i = 0; i < grid.length; i++)
				for (int j = 0; j < grid[i].length; j++)
					if (grid[i][j] == word.charAt(0))
					{
						path.add((i << 16) + j);
						if (checkConnexity(grid, word, path, i, j)) return true;
						path.remove((i << 16) + j);
					}
		} else
		{
			// recursive call
			if (path.size() == word.length()) return true; // check was successful
			int i = startRow, j = startCol;
			for (int x = Math.max(0, i - 1); x < Math.min(grid.length, i + 2); x++)
				for (int y = Math.max(0, j - 1); y < Math.min(grid[0].length, j + 2); y++)
				{
					int w = (x << 16) + y;
					if (word.charAt(path.size()) == grid[x][y] && ! path.contains(w))
					{
						path.add(w);
						if (checkConnexity(grid, word, path, x, y)) return true;
						path.remove(w);
					}
				}
		}
		return false;
	}
	
	/**
	 * Check if the word is theoritically obtainable following the adjacent cells of the grid
	 */
	public static boolean checkConnexity(char[][] grid, String word)
	{
		return checkConnexity(grid, word, new HashSet<Integer>(), -1, -1);
	}
	
	/**
	 * Check if the word is present in the current dictionary.
	 * If no dictionary is defined, returns true in all cases.
	 */
	public boolean checkDictionary(String word)
	{
		return dictionary == null || dictionary.contains(word.toLowerCase());
	}
	
	/** 
	 * Check if a worddle answer is correct for the given grid
	 * Perform a connectivity check and a dictionary check
	 */
	public boolean isValidAnswer(char[][] grid, String word)
	{
		return checkConnexity(grid, word) && checkDictionary(word);
	}
	
	private static char[][] transformGrid(Collection<String> c)
	{
		char[][] tab = new char[c.size()][];
		int i = 0;
		for (String s: c)
		{
			String s2 = s.replaceAll("[^a-zA-Z]", "");
			if (s2.length() > 0)
			{
				tab[i] = s2.trim().toCharArray();
				if (i > 0 && tab[i].length != tab[0].length)
					throw new RuntimeException("The line #" + i + " has not the same number of chars");
				i++;
			}	
		}
		return tab;
	}
	
	public static void main(String[] args) throws IOException
	{
		if (args.length < 2)
		{
			System.out.println("Usage: java AnswerCheck pathToGrid pathToDictionary\nThen enter on the standard input the words to check (one by line)");
			System.exit(-1);
		}
		// Arg 0: UTF-8 text file containing the grid (line by line)
		List<String> grid1 = new ArrayList<>();
		loadLines(args[0], grid1);
		// transform grid1 to char[][]
		char[][] grid2 = transformGrid(grid1);
		// print the grid
		for (char[] line: grid2)
			System.out.println(Arrays.toString(line));
		// Arg 1: dictionary file
		AnswerChecker ac = new AnswerChecker();
		ac.setDictionary(args[1]);
		// read the lines on stdin and check if the words are valid
		try (Scanner sc = new Scanner(System.in))
		{
			while (sc.hasNextLine())
			{
				String word = sc.nextLine().trim();
				if (word.length() > 0)
				{
					System.out.println("Match connexity? " + checkConnexity(grid2, word));
					System.out.println("Match dictionary? " + ac.checkDictionary(word));
					System.out.println("Globally match? "+ ac.isValidAnswer(grid2, word));
				}
			}
		}
	}
}
	
	
