/**
 * Deck. The class represents an abstraction of  the deck with which "Seven and a half" game
 * is played with.
 */
package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;


/**
 *
 * @author simorgh & dzigor92
 */
public class Deck {
    private ArrayList<String> cards;
    
    /**
     * Deck constructor.
     * Called once when server it's created. Following threads should use second constructor.
     * @param file
     * @throws IOException 
     */
    public Deck(File file) throws IOException{
        this.cards = readDeckFile(file);
    } 
    
    /**
     * Deck "New Game" constructor.
     * Called every time a new Client it's connected. Deep copy from original
     * static Deck it's done, then it's shuffled to ensure new deck order.
     * @param cards 
     */
    public Deck(ArrayList <String> cards){
        this.cards = cards;
    }
    
    
    /**
     * Method that reads the deck file and saves the values on the "cards" parameter
     * @param fin
     * @return
     * @throws IOException 
     */
    private ArrayList readDeckFile(File fin) throws IOException {
        ArrayList deck = new ArrayList();
	FileInputStream fis = new FileInputStream(fin);
 
	//Construct BufferedReader from InputStreamReader
	BufferedReader br = new BufferedReader(new InputStreamReader(fis));
 
	String line = null;
	while ((line = br.readLine()) != null) {
		System.out.println("\t- Added card "+line);
                deck.add(line);
	}
        
	br.close();
        return deck; 
    }
    
    /**
     * Shuffles the deck.
     */
    public void shuffle(){
        Collections.shuffle(getCards());
    }

    /**
     * Gets the list of card that the deck contains.
     * @return the cards
     */
    public ArrayList<String> getCards() {
        return cards;
    }
    
   
}
