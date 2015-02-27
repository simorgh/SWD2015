/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;

/**
 *
 * @author simorgh & dzigor92
 */

public class Game {
    private Deck deck;
    private ArrayList handBank;
    private ArrayList handPlayer;
    private float playerScore;
    private float bankScore;
    private int gains;
    private int bet;
    
    private int cont;
    
    /**
     * Class constructor 
     * 
     * @param deck 
     */
    public Game(Deck deck){
        this.deck = deck;
        this.handBank = new ArrayList();
        this.handPlayer = new ArrayList();
        this. gains = 0;
        this.bet = 0;
        
        this.cont = 0;
        
    }

    /**
     * @return the deck
     */
    public Deck getDeck() {
        return deck;
    }

    /**
     * @return the handBank
     */
    public ArrayList getHandBank() {
        return handBank;
    }

    /**
     * @return the handPlayer
     */
    public ArrayList getHandPlayer() {
        return handPlayer;
    }

    /**
     * @return the gain
     */
    public int getGain() {
        return gains;
    }

    /**
     * @param gain the gain to set
     */
    public void setGain(int gain) {
        this.gains = gain;
    }

    /**
     * @return the bet
     */
    public int getBet() {
        return bet;
    }

    /**
     * @param bet the bet to set
     */
    public void setBet(int bet) {
        this.bet = bet;
    }
    
    /**
     * Calculates the value of the card 
     * @param D The draw card 
     * @return The value of the card
     */
    private float getCardValue(char D){
        if (Character.isDigit(D)) return (float)Character.getNumericValue(D);
        else{
            return (float)0.5;
        }      
    }
    
    /**
     * Adds the value of the drawn card to player's score
     * @param value 
     */
    public void updatePlayerScore(char D){
        float value = this.getCardValue(D);
        this.playerScore += value;
    }
    
    /**
     * Adds the value of the drawn card to bank's score 
     * @param value 
     */
    public void updateBankScore(char D){
        float value = this.getCardValue(D);
        this.bankScore+=value;
    }
    
    /**
     * Raises the bet
     * @param value 
     */
    public void raiseBet(int value){
        this.bet+= value;
    }

    /**
     * @return the playerScore
     */
    public float getPlayerScore() {
        return playerScore;
    }

    /**
     * @return the bankScore
     */
    public float getBankScore() {
        return bankScore;
    }
    
    public void playBank(){
        char[] card;
        
        //Player hand busted
        //Bank will draw once and pass
        if (this.playerScore > 7.5f){
            card = this.drawCard();
            this.handBank.add(card);
            this.updateBankScore(card[0]);
        }
        
        //Player reached 7.5 score
        //Bank will draw until he reaches 7.5 or gets busted
        else if(this.playerScore == 7.5f) {
            while(this.bankScore < this.playerScore){
                card = this.drawCard();
                this.handBank.add(card);
                this.updateBankScore(card[0]);
            }
        }
        
        //Player drew less than 7.5
        //bank will draw until he gets a higher score (or gets busted)
        else {
            while(this.bankScore <= this.playerScore){
                card = this.drawCard();
                this.handBank.add(card);
                this.updateBankScore(card[0]);
            }
        }
    }

    
        
    /**
     * Draws a card from indicated position
     * @return 
     */
    public char[] drawCard(){
        char[] card = new char[2];
        card[0] =  this.deck.getCards().get(cont).charAt(0);
        card[1] =  this.deck.getCards().get(cont).charAt(1);
        this.cont++;
        return card;
    }
    
    public boolean playerWins(){
        return (playerScore > bankScore && playerScore <= 7.5f);
    }
    
}
