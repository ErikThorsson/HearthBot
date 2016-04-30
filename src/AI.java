import java.awt.AWTException;
import java.awt.Robot;
import java.io.IOException;
import java.util.Arrays;


public class AI {
	boolean first = false;
	int handSize, MAIN_READY;
	
	public static void main(String[] args) throws IOException, AWTException, InterruptedException {
		
		AI a = new AI();	
		
		//a.printCombatCombinations();
		Card[][][] combat = a.combatCombinations();
		int combos[][] = a.combinationsCombinations(combat);
		//a.printComboCombos(combos);
		int b = a.bestCombat(combos);
		//System.out.println(b);
		Parser p = new Parser();
		p.parse();
		
		System.out.println("The best combat moves are...");
		for(int i = 1; i< 7; i++) {
			if(combos[0][i] != -1000) {
				for(int j = 0; j < 7; j++) {
					if(combat[i][combos[b][i]][j] != null)
						System.out.println(combat[i][combos[b][i]][j].name + " attacking " + p.enPlayCards[i].name);
				}
			}
		}
		
		//System.out.print(combat[1][combos[b][1]][0]);
		//Bot bot = new Bot(p);
		//int c = a.findCard(p, combat[1][combos[b][1]][0].EntityID);
		//bot.attack(c, 1, bot.enPlayHeight);
		
		
//		Parser p = new Parser();
//		p.parse();
		//p.printMyPlay();
		
//		a.handSize();
//		a.first = p.firstPlayer;
//		a.MAIN_READY = p.MAIN_READY;
//		int counter = 1;
//
//		
//		while(true) {
//			
//			//every second re-parse the log for turn changes and see if it's your turn;
//			int t = a.isMyTurn();
//			p.updateLog();
//			p.checkTurnChange();
//			Thread.sleep(1000);	
//			
//			//only make a move if you haven't made one yet
//			//this triggers twice... the second time is the real turn start. That's what the counter is for. 
//			if(t == 1 && a.MAIN_READY < p.MAIN_READY) {
//				counter++;
//				System.out.println(counter);
//				a.MAIN_READY = p.MAIN_READY;
//
////				r.mouseMove(50, 50);
////				Thread.sleep(500);
////				r.mouseMove(200, 200);
//				
//				//get turn #.
//				int turn = 0;
//				for(int i = 0; i< 2; i++) {
//					turn = p.findTurn();
//					Thread.sleep(50);
//				}
//				if(counter == 2) {
//					System.out.println("NEXT TURN " + turn);
//					//Thread.sleep(1000);	
//					//this is the code that for the turn. SHould be its own method probably to keep this clean...
//					a.playCurve();
//					counter = 0;
//				}
//			}
//		}
		
		
	}
	
	public int findCard(Parser p, int id) {
		for(int i = 0; i < 8; i++) {
			//System.out.println(p.myPlayCards[i].name + " " + p.myPlayCards[i].EntityID);
			if(p.myPlayCards[i] != null) {
				if(p.myPlayCards[i].EntityID == id)
					return i;
			}
		}
		return -1;
	}
	
	public void printComboCombos(int[][] c) {
		for(int i = 0; i< 100; i++){
			if(c[i][0] != 0 && c[i][1] != -1000)
				System.out.print("\nCombination " + i + "\n");
			for(int j = 0; j<8; j++) {
					System.out.print(c[i][j] + ", ");
			}
		}
	}
	
	public int bestCombat(int[][] c){
		int best = 0;
			for(int i = 0; i< 100; i++){
				if(c[i][0] != -1000) {
					if(c[i][0] > best)
						best = i;
				}	
				}
		return best;
	}
	
	public void printCombatCombinations() throws IOException, InterruptedException {
		
		Parser p = new Parser();
		p.parse();
		
		Card[][][] c = combatCombinations();
        
        for(int i = 0; i < 8; i++) {
        	System.out.println("\nENEMY CARD " + i + "\n");
        	for (int j = 0; j< 50;  j++) {
        			if(c[i][j][0] != null)
        				System.out.println("combination " + j);
        		for( int k = 0; k < 8; k++) {
        			if(c[i][j][k] != null)
        				System.out.print(c[i][j][k].name + ", ");
        		}
    			if(c[i][j][0] != null)
    				System.out.println();
        	}
        }
	}
	
	public int myPlayLength(Card[] play){
		int count = 0;
		for(int i = 0; i < play.length; i++) {
			if(play[i] != null)
				count++;
		}
		return count;
	}
	
	public int enPlayLength(Card[][][] c){
		int count = 0;
		for(int i = 0; i < 8; i++) {
			if(c[i][0][0] != null)
				count++;
		}
		return count;
	}
	
	/** returns the difference between my attack combination's card value and the enemy's... so we can have a negative score**/
	public int getTradeValue(Card[][][] c, int cardNum, int combNum) throws IOException, InterruptedException {
		int myCardValue = 0;
		Parser p = new Parser();
		p.parse();
		
		//get value of this enemy card
		int enemyVal = 0;
		int enHP = 0;
		
		if(p.enPlayCards[cardNum] != null)
			enHP = p.enPlayCards[cardNum].hp;
		
		if(p.enPlayCards[cardNum] != null) {
			for(int j = 0; j<8; j++) {
				if(c[cardNum][combNum][j] != null) {
					//subtract enemy health for every attack on it from the combination
					enHP -= c[cardNum][combNum][j].atk;
				}
			}
			//only alive minions carry value by weighted stats
			if(enHP > 0) 
				enemyVal = enHP + p.enPlayCards[cardNum].atk * 2;
			else 
				enemyVal = 0;
			//System.out.println("ENEMY " + p.enPlayCards[cardNum].name + " " + p.enPlayCards[cardNum].atk + "/"
			//+ p.enPlayCards[cardNum].hp);
		}
		
		int myHP = 0;
		for(int i = 0; i < 8; i++) {
			if(c[cardNum][combNum][i] != null & p.enPlayCards[cardNum] != null) {
				myHP = c[cardNum][combNum][i].hp - p.enPlayCards[cardNum].atk;
				
				//my card value = the weighted stats of my alive minions after the trade
				if(myHP > 0)
					myCardValue += myHP + c[cardNum][combNum][i].atk * 2;
				//else if this minion died.. give a bonus if its stats were worse than the enemies weighted ones
				else {
					int myStats = c[cardNum][combNum][i].atk - p.enPlayCards[cardNum].atk;
					if(myStats < 0) {
						//magnifies the value for cards with small atk and health 
						//I did this so the algorithm will prefer trading smaller minions when it can
						myCardValue += myStats * -2 * (p.enPlayCards[cardNum].atk/c[cardNum][combNum][i].atk 
								+ 10 / c[cardNum][combNum][i].hp);
					}
				}
				
				//System.out.println("MY " + c[cardNum][combNum][i].name + " " + c[cardNum][combNum][i].atk + "/"
						//+ c[cardNum][combNum][i].hp);
			}
		}
		return myCardValue - enemyVal;
	}
	
	/** Returns a combat combination for every enemy card given all available combinations
	 * O(n^7)...
	 * @throws InterruptedException 
	 * @throws IOException **/
	public int[][] combinationsCombinations(Card[][][] c) throws IOException, InterruptedException {
		
		int enLength = enPlayLength(c);
		//System.out.println(enLength);
		int tradeValueSum = 0;
		int[][] combination = new int[100][9];
		//initialize int array
		for(int o = 0; o< 100; o++) {
			combination[o][0] = -1000;
		}
		int combCount = 0;

			//get first combination for enemy card 1 and find all combinations that work with it
			//there can be up to 50 combinations just to make sure all cases are covered
			for(int j = 0; j < 50; j++) {
				//compute this combinations trade value
				if(c[1][j][0] != null) {
				//System.out.println("Combination # " + j + "\n");
				//System.out.println("BLHAHAHA " + c[1][0][0].name);
				tradeValueSum += getTradeValue(c, 1, j);
				} else {
					continue;
				}
				//now compute the next combination for enemy cards (combination 1 of en card 1 with combination 1 of en card 2 
				// and so on if this combination can be used (hasn't already been used)
				int tradeValueL1 = tradeValueSum; //record the tradeValue for the last segment to reset this during each loop
				
				for(int k = 0; k < 50; k++) {
					if(c[2][k][0] != null && overlappingCombinations(c, 1, j, 2, k) != true) {
						tradeValueSum += getTradeValue(c, 2, k);
						//System.out.println("value of combinations up to enemy card 2 " + tradeValueSum);
						
						//put the values of each combination in the array numbered by each enemy card
						if(enLength == 2) {
							combCount++;
						for(int z = 0; z< 8; z++) {
							if(z == 0)
								combination[combCount][z] = tradeValueSum;
							if(z == 1)
								combination[combCount][z] = j;
							if(z == 2)
								combination[combCount][z] = k;
							}
						}
						
						
						int tradeValueL2 = tradeValueSum;//reset lastTradValue to the next loop					
						//do this for all possible combinations so 7 times total...
						for(int l = 0; l < 50; l++) {
							if(c[3][l][0] != null && overlappingCombinations(c, 1, j, 3, l) != true
									&& overlappingCombinations(c, 2, k, 3, l) != true) {
									tradeValueSum += getTradeValue(c, 3, l);
									//System.out.println("value of combinations up to enemy card 3 " +tradeValueSum);
									//put the values of each combination in the array numbered by each enemy card
									for(int z = 1; z< 8; z++) {
										if(z == 1)
											combination[combCount][z] = j;
										if(z == 2)
											combination[combCount][z] = k;
										if(z == 3)
											combination[combCount][z] = l;
									}
							}
							tradeValueSum = tradeValueL2; //reset to what it was before this loop
					}
				}
					//reset trade value sum for next iteration from loop 1
					tradeValueSum = tradeValueL1;
			}

					//do this for all possible combinations so 7 times total...
//					for(int l = 0; l < 50; l++) {
//						if(c[3][l][0] != null && overlappingCombinations(c, 1, j, 3, l) != true
//								&& overlappingCombinations(c, 2, k, 3, l) != true) {
//								tradeValueSum += getTradeValue(c, 3, l);		
//							}
//						for(int m = 0; m < 50; m++) {
//							if(c[4][m][0] != null && overlappingCombinations(c, 1, j, 4, m) != true
//									&& overlappingCombinations(c, 2, k, 4, m) != true
//									&& overlappingCombinations(c, 3, l, 4, m) != true) {
//									tradeValueSum += getTradeValue(c, 4, m);
//									System.out.println(tradeValueSum);
//									combCount++;
//									//put the values of each combination in the array numbered by each enemy card
//									for(int z = 1; z< 8; z++) {
//										if(z == 1)
//											combination[combCount][z] = j;
//										if(z == 2)
//											combination[combCount][z] = k;
//										if(z == 3)
//											combination[combCount][z] = l;
//										if(z == 4)
//											combination[combCount][z] = m;
//									}
//									//reset trade value sum for next iteration
//									tradeValueSum = 0;
//										
//							}
//				for(int k = 0; k < 50; k++) {
//					if(c[2][k][0] != null && overlappingCombinations(c, 1, j, 2, k) != true) {
//						tradeValueSum += getTradeValue(c, 2, k);		
//						}
//				for(int k = 0; k < 50; k++) {
//					if(c[2][k][0] != null && overlappingCombinations(c, 1, j, 2, k) != true) {
//						tradeValueSum += getTradeValue(c, 2, k);		
//						}
//				for(int k = 0; k < 50; k++) {
//					if(c[2][k][0] != null && overlappingCombinations(c, 1, j, 2, k) != true) {
//						tradeValueSum += getTradeValue(c, 2, k);		
//						}
//				}
//				}
//				}
//				}
//				}
//				}
			}
		
		return combination;
	}
	
	public boolean overlappingCombinations(Card[][][] c, int firstXIndex, int firstYIndex, int secondXIndex, int secondYIndex) {
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++){
				//this equals function might NOT WORK
				if(c[firstXIndex][firstYIndex][i] != null && c[secondXIndex][secondYIndex][j] != null) {
					if (c[firstXIndex][firstYIndex][i].equals(c[secondXIndex][secondYIndex][j]))
							return true;
				}
			}
		}
		return false;
	}
	
	/** Shifts the cards right and the last to the second position.
	 * Used to get all combinations of car
	 * @param c
	 * @return
	 */
	public Card[] shiftRight(Card[] c) {
		return null;
	}
	
	/** For now just weight by 2 x Atk 1 x HP **/
	public int cardValue(Card c) {
		int val = 0;
		val += c.atk * 2;
		val += c.hp;
		return val;
	}
	
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
	
	/**
	 * Returns a 3D array of combinations available for your cards in play for each enemy card on the field
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public Card[][][] combatCombinations() throws IOException, InterruptedException {
		
		Parser p = new Parser();
		p.parse();
		
		//int cNum = getCombinationNum(p.myPlay.length);
		int counter = 0;
		Card[][][] combinations = new Card[8][50][8];
		
		//System.out.println (p.myPlayCards[1].atk);
		
		//loop for every enemy card
		for(int i = 0; i < p.enPlay.length; i++) {
			
			Card[] myPlay = p.myPlayCards.clone();			
			
			int enHP = 0;
			if(p.enPlayCards[i] != null)
				enHP = p.enPlayCards[i].hp;	
			else
				continue;
				
			//System.out.println("enemy HP is " + enHP);
			
			int pLength = myPlayLength(myPlay);
			
			//fixes bug for only 1 card in play
			if(pLength == 1)
				pLength = 2;

			//create and add every combination that can kill the enemy
			for( int z = 0; z < pLength - 1; z++) {
				
				//System.out.println("shifting in play # " + z);
				
				/**after first iteration shift the arrays minus the first element to the right in circular fashion
				this will allow for every permutation calculation*/
				if(z >= 1) {
					
					Card lastIndex = myPlay[1];
					
					for(int x = 1; x < pLength; x++) {
						
						Card currentXplusOne = null;
						int lastCardInPlay = -1;
						
						//save the index you are about to delete (shift)
						if(x + 1 < pLength)  
							currentXplusOne = myPlay[x + 1];
						else
							lastCardInPlay = x;
						
						
						//if shifting last element, shift it to the 2nd element
						if(x == lastCardInPlay) {
							myPlay[2] = myPlay[x + 1];
							myPlay[x+1] = lastIndex;
							continue; 
						}
						
						//if first swap swap index 1 with 2
						if(x == 1)
							myPlay[2] = myPlay[1];
						
						//else swap the next index with the last
						else
							myPlay[x + 1] = lastIndex;
						
						//save the index you are about to delete (shift)
						lastIndex = currentXplusOne;
						
					}
				}
				
				//print array to see if its sane
//				System.out.println("CARD ARRAY!!!\n");
//				for(int v = 0 ; v < 8 ; v++) {
//					if(myPlay[v] != null) {
//						try {
//							System.out.println(myPlay[v].name + " is in position " + v + " " +  myPlay[v].atk + 
//									"/" + myPlay[v].hp + " id " + myPlay[v].EntityID);
//						} catch (Exception e) {
//						System.out.println("\n\nPROBLEM NAME IS " + myPlay[v].name +"\n\n");
//					}
//					}}
//				System.out.println("\n\nCARD ARRAY!!!\n");

				
				/**this n^2 block will try adding myPlay[1] + myPlay[2] and so on until it gets an added attack
				 value that is >= enemy health. The combination will be stored in a 3D array
				 combinations[enemy card][combination #][first card in combination]*/
				 
				for(int j = 1; j < p.myPlay.length; j++) {
					
					//System.out.println("checking combinations of my Card in position " + j);
					//reset the added attk every combination
					int addedAttk = 0;

					for(int k =  j; k < myPlay.length; k++) {															
						
						//System.out.println("k loop " + k + " j is " + j + " " + myPlay[k].name);
						//if we run into null because all are tested
						if(myPlay[k] == null) {
							break;
						}
						
						if(addedAttk < enHP) {
						
							
							//System.out.println(myPlay[j].atk);
							
							addedAttk += myPlay[k].atk;
							
							//put a card object into the combination # for the enemy card
							combinations[i][counter][k-j] = myPlay[k];
//							System.out.println("j is " + j + " k is " + k + " " + myPlay[k].name + " with attk " + myPlay[k].atk + 
//									" into indices " + i + " " + counter +  " " + (k-j));

					
						} else {
							//increment the combination counter if one has been found
							counter++;
							break;
						}
				}
					//move the counter even if the target hasn't been reached
					if(combinations[i][counter][0] != null)
						counter++;
			}
				if(myPlay[3] == null) { //won't operate for 2 or less cards
					break;
				}
		}
			counter = 0;
	}
		
		return combinations;
	}
	
//	public int getCombinationNum(int myPlayCardNumber) {
//		
//		int cNum = 0;
//		int n = myPlayCardNumber;
//		int r = myPlayCardNumber;
//		int nFac = 0;
//		 
//		//we want the permutations of every possible card combination
//		for(int i = r; i > 0; i--) {
//			
//			//equation for each permutation # is n! / (n-k)!
//			//calculate n!
//			if (nFac == 0) { // on get n! once
//				nFac = n;
//				int facCount = n;
//			for(int j = 0; j < facCount; j++) {
//				//System.out.println("nFac " + nFac);
//				nFac =+ nFac * (facCount-1);
//				//System.out.println("nFac " + nFac);
//				facCount--;
//			}
//			}
//			
//			int rFac = i;
//			int rFacTemp = i;
//			int rFacCount = r - 1;
//			
//			//System.out.println("rFac " + rFac + " rFacCount " + rFacCount);
//
//			//get r!
//			for(int j = 0; j < rFacCount; j++) {
//				rFac *= (rFacTemp-1);
//				rFacCount--;
//			}
//			//System.out.println("rFac " + rFac);
//			
//			//calculate (n-r)!
//			int divisor = n-r;
//			int divisorTemp = divisor;
//			
//			if(divisor == 0)
//				divisor = 1; //since 0! = 1
//			
//			//System.out.println("n " + n + " k " + k);
//			
//			int counter = divisor - 1;
//			//System.out.println("divisor is " + divisor + "divisor count is " + counter);
//
//			for(int l = 0; l < counter; l++) {
//				divisor *= (divisorTemp-1);
//				counter--;
//			}
//			cNum += nFac / (divisor * rFac);
//			//System.out.println("nFac " + nFac + " div " + divisor + " rFac " + rFac);
//			//System.out.println(cNum);
//			r--;
//		}
//			
//		return cNum;
//	}
	
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
