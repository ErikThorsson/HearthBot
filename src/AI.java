import java.awt.AWTException;
import java.awt.Robot;
import java.io.IOException;
import java.util.Arrays;


public class AI {
	boolean first = false;
	int handSize, MAIN_READY;
	
	public static void main(String[] args) throws IOException, AWTException, InterruptedException {
		AI a = new AI();	
		Robot r = new Robot();
		Parser p = new Parser();
		p.parse();
		a.handSize();
		a.first = p.firstPlayer;
		a.MAIN_READY = p.MAIN_READY;
		int counter = 1;

		
		while(true) {
			
			//every second re-parse the log for turn changes and see if it's your turn;
			int t = a.isMyTurn();
			p.updateLog();
			p.checkTurnChange();
			Thread.sleep(1000);	
			
			//only make a move if you haven't made one yet
			//this triggers twice... the second time is the real turn start. That's what the counter is for. 
			if(t == 1 && a.MAIN_READY < p.MAIN_READY) {
				counter++;
				System.out.println(counter);
				a.MAIN_READY = p.MAIN_READY;

//				r.mouseMove(50, 50);
//				Thread.sleep(500);
//				r.mouseMove(200, 200);
				
				//get turn #.
				int turn = 0;
				for(int i = 0; i< 2; i++) {
					turn = p.findTurn();
					Thread.sleep(50);
				}
				if(counter == 2) {
					System.out.println("NEXT TURN " + turn);
					//Thread.sleep(1000);	
					//this is the code that for the turn. SHould be its own method probably to keep this clean...
					a.playCurve();
					counter = 0;
				}
			}
		}
		
		
	}
	
	//  ______  ______  ______  ______NEEDED??? ______ ______ ______ ______ ______ ______

	
	public void handSize() throws IOException, InterruptedException {
		int hSize = 0;
		Parser p = new Parser();
		p.parseHand();
		for(int i=0; i< p.hand.length; i++) {
			if(p.hand[i] !=null) {
				hSize++;
			}
		}
		handSize = hSize;
	}
	
	/**Returns a boolean by checking to see if you have drawn a card*/
	public boolean cardsInHandChange() throws IOException, InterruptedException {
		boolean start = false;
		int hSize = 0;
		Parser p = new Parser();
		p.parseHand();
		for(int i=0; i< p.hand.length; i++) {
			if(p.hand[i] !=null) {
				hSize++;
			}
		}
		if(hSize != handSize){
			handSize = hSize;
			return true;
		}
		return false;
	}
	
	//  ______  ______  ______  ______NEEDED??? ______ ______ ______ ______ ______ ______

	/**Plays a card that equals your mana pool or the next highest one*/
	public void playCurve() throws IOException, AWTException, InterruptedException {
		System.out.println("playing curve!");
		Parser p = new Parser();
		Thread.sleep(1000);
		p.parse();

		//get turn #.
		int turn = 0;
//		for(int i = 0; i< 2; i++) {
			turn = p.findTurn();
//			Thread.sleep(50);
//		}
		
		//gets the turn number based on your start position
		if(p.firstPlayer == true) {
			turn = turn/2 + 1;
		} else {
			turn = turn/2;
		}
				
		int[] costs = handCosts(p);
		//for(int i = 0; i< 2; i++) {
			//costs = handCosts(p);
			//Thread.sleep(50);
		//}
		
			int card = -1;
		int cardIndex  = 0;
		
		//looks for a cost that  = the turn
		for(int i = 1; i<9; i++) {
			//System.out.println(p.hand[i] + " has cost " + costs[i]);
			if(costs[i] == turn) {
				card = costs[i];
				cardIndex = i;
			}
		}
		
		//System.out.println("card = " + card);

		//finds highest cost if no cost = turn 
		if(card == -1) {
			for(int i = 1; i<9; i++) {
				if(costs[i] > card && costs[i] < turn) {
					card = costs[i];
					cardIndex = i;
				}
				//System.out.println("highest cost card is" + p.hand[cardIndex] + " with index " + cardIndex + " with cost " + card);
			}
		}

		Bot r = new Bot(p);
//		for(int i = 0; i< 2; i++) {
//			r = new Bot();
//			Thread.sleep(50);
//		}
		
		System.out.println("turn " + turn + " highest playable card is " + p.hand[cardIndex] + " with cost " + card);
		Card c = p.cards.get(p.hand[cardIndex]);
		
		if(card == -1)
			r.endTurn();
		else {
			if(c.atk != -1)
				r.playCard(r.c, cardIndex, r.handHeight);
			else { //it's a spell
				for(int i = 0; i< 8; i++) {
					if(p.enPlay != null)
						r.spellToEnemy(r.c[cardIndex], r.enP[i], r.enPlayHeight);
						break;
				}
			}
			r.endTurn();
		}
		
	}
	
	/**For whatever reason... when I update the log file the findTurn() method returns the wrong output unless I let it run in a loop with 50ms sleep.
	 *  It will consistently return the right answer around the 2nd loop. So we have 100ms of sleep every time this is run*/
	public int isMyTurn() throws IOException, InterruptedException{
		Parser p = new Parser();
		int turn = 0;
		
		for(int i = 0; i< 2; i++) {
			turn = p.findTurn();
			Thread.sleep(50);
		}
		
		
		//System.out.println(turn + " " + p.firstPlayer);
		
		//sets the current turn to see if its different than the last to allow for the the next action		
		if(turn % 2 > 0 && turn != -1 && first == true)
			return 1;
		if(turn % 2 == 0 && turn != -1 && first == false)
			return 1;
		return 0;
	}
	
	/** returns the costs of cards in hand*/
	public int[] handCosts(Parser p){
		int[] costs = new int[9];
		for(int i = 1; i< 9; i++) {
			if(p.hand[i] != null) {
			Card c = new Card();
			c = p.cards.get(p.hand[i]);
			System.out.println(p.hand[i] + " " + c.cost);
			costs[i] = c.cost;
			}
		}
		return costs;
		}
	
	public Parser parse() throws IOException, InterruptedException {
		Parser p = new Parser();
		return p;
	}
}
