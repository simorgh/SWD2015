package controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import model.Game;
import utils.Protocol;
import view.Console;

/**
 *
 * @author simorgh & dzigor92
 */
public class Client {
    private static final String ERROR_OPT = "!! Wrong option. Please enter a valid action.";
    private static float topcard = 1.0f;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String nomMaquina, str;
        int port;
        Console console = new Console();
        Game g;
        InetAddress maquinaServidora;
        Socket socket = null;
        Protocol pr;
        
/* TODO: Uncomment to get args info    
        if (args.length < 4 || args.length > 4 ){
            System.out.println("Us: java Client -s <maquina_servidora> -p <port> [-a topcard]");
            System.exit(1);
        }

        nomMaquina = args[0];
        port  = Integer.parseInt(args[1]);  */
        
// <test values>
        nomMaquina = "localhost";
        port = 1234;
// <test values>
        
        try{
            maquinaServidora = InetAddress.getByName(nomMaquina); /* Obtenim la IP de la maquina servidora */
            socket = new Socket(maquinaServidora/*"10.111.66.40"*/, port); /* Obrim una connexio amb el servidor */
            console.showConnection(socket);
            pr = new Protocol(socket);
            g = new Game();
            
            console.printWelcome();
            pr.sendStart();
            int str_bet = pr.recieveStartingBet();
            console.printStartingBet(str_bet);
            
            pr.sendDraw();  /* DRAW command is mandatory after a STARTING_BET is received */
            char [] card = pr.recieveCard();
            console.printNewCard(card);
            g.updatePlayerScore(card[0]);
            
            // game loop
            boolean end = false;
            do{
                char opt;
                if(Client.topcard == 0.0f) opt = console.printInGameOptions(g.getPlayerScore());
                else opt = choseOptionAutoplay(g.getPlayerScore(), Client.topcard);
                switch(opt){
                    case '1': 
                        pr.sendDraw();
                        card = pr.recieveCard();
                        console.printNewCard(card);
                        g.updatePlayerScore(card[0]);
                         
                        if(g.isBusted()){ 
                            pr.recieveBusting();
                            end = true;
                        }
                        break;
                        
                    case '2': 
                        int rise = console.enterRaise();
                        pr.sendAnte(rise);
                        pr.sendDraw();
                        card = pr.recieveCard();
                        console.printNewCard(card);
                        g.updatePlayerScore(card[0]);
                        
                        if(g.isBusted()){ 
                            pr.recieveBusting();
                            end = true;
                        } 
                        break;
                        
                    case '3': 
                        pr.sendPass();
                        end = true;
                        break;
                    default: 
                        console.printError(Client.ERROR_OPT);
                        break;
                }  
            } while(!end);
            
            ArrayList <String> bank_score = pr.recieveBankScore();
            if(bank_score != null) console.printBankScore(bank_score);
            else{
                System.err.print("Error al rebre BKSC!");
            }
            
            int gain = pr.recieveGains();
            console.printGains(gain);
              
        } catch (IOException e) {
            System.out.println("Els errors han de ser tractats correctament en el vostre programa.");
        } finally {
            try {
                if(socket != null) socket.close();
            } catch (IOException ex) {
                System.out.println("Els errors han de ser tractats correctament pel vostre programa");
            } // fi del catch    
        }
    } // fi del main
    
    /**
     * Autoplay option choser. The method implements the behaviour that the automatic Client will
     * follow when topcard option is activated.
     * 
     * @param currentScore Current client's score.
     * @param autoplay The score to reach by client.
     * @return Option to chose. If the desired score is reached, the method will return 'Pass' option. If not, the method will return 'Draw'. 
     */
    
    private static char choseOptionAutoplay(float currentScore, float autoplay){
        char opt = '0';
        if(currentScore >= autoplay) opt = '3';  
        else opt = '1';
        return opt;
    }
    
}
