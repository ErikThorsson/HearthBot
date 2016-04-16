import java.awt.AWTException;
import java.io.IOException;
import java.util.Arrays;


public class AI {
	public static void main(String[] args) throws IOException, AWTException, InterruptedException {
		AI a = new AI();
		//Parser p = new Parser();
		//a.playCurve();
		
		while(true) {
			int t = a.isMyTurn();
			if(t == 1) {
				a.playCurve();
			}
			Thread.sleep(500);
		}
	}
	
	public void playCurve() throws IOException, AWTException, InterruptedException {
		Parser p = new Parser();
		p.parse();
		int turn = p.findTurn();
		
		if(p.firstPlayer == true) {
			turn = turn/2 + 1;
		} else {
			turn = turn/2;
		}
		
		int[] costs = handCosts(p);
		int card = -1;
		for(int i = 0; i<8; i++) {
			System.out.println(p.hand[i] + " has cost " + costs[i]);
			if(costs[i] == turn) {
				card = i;
			}
		}
		if(card == -1) {
			for(int i = 0; i<8; i++) {
				if(costs[i] > card) {
					card = i;
				}
			}
		}
		
		Bot r = new Bot();
		//System.out.println("turn " + turn + " printing " + p.hand[card] + " with cost " + card);

		if(card == -1)
			r.endTurn();
		else {
			r.playCard(r.c, card, r.handHeight);
			r.endTurn();
		}
	}
	
	public int isMyTurn() throws IOException{
		Parser p = new Parser();
		int turn = p.findTurn();
		if(turn % 2 > 0 && turn != -1 && p.firstPlayer == true)
			return 1;
		if(turn % 2 == 0 && turn != -1 && p.firstPlayer == false)
			return 1;
		return 0;
	}
	
	/** returns a sorted array of the costs of cards in hand*/
	public int[] handCosts(Parser p){
		int[] costs = new int[8];
		for(int i = 0; i< 8; i++) {
			if(p.hand[i] != null) {
			Card c = new Card();
			c = p.cards.get(p.hand[i]);
			System.out.println(p.hand[i] + " " + c.cost);
			costs[i] = c.cost;
			}
		}
		Arrays.sort(costs);
		return costs;
		}
	
	public Parser parse() throws IOException {
		Parser p = new Parser();
		return p;
	}
}
