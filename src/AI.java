import java.awt.AWTException;
import java.awt.Robot;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class AI {
	boolean first = false;
	int handSize, MAIN_READY;
	CardDatabase cDB;
	
	public static void main(String[] args) throws IOException, AWTException, InterruptedException {	
		
		Parser p = new Parser();
		
		//get a new log file each game. Uncomment each game.
		//p.updateLog();
		
		Bot b = new Bot(p);
		p.parse();
		//p.printMyPlay();
		AI a = new AI();
		a.loadDB();
		
		//a.printCombatCombinations();
		//a.printComboCombos(a.combinationsCombinations(a.combatCombinations(null)));
		a.printBestCombat(p);

// ALL OF THIS IS THE MAIN ROBOT LOOP FOR INSIDE THE GAME
//		a.handSize();
//		a.first = p.firstPlayer;
//		a.MAIN_READY = p.MAIN_READY;
//		int counter = 1;
//		
//		while(true) {
//			
//			//every second re-parse the log for turn changes and see if it's your turn;
//			int t = a.isMyTurn();
//			p.checkTurnChange();
//			p.parse();
//			Thread.sleep(2000);	
//			
//			//only make a move if you haven't made one yet
//			//this triggers twice... the second time is the real turn start. That's what the counter is for. 
//			if(t == 1 && a.MAIN_READY < p.MAIN_READY) {
//				counter++;
//				System.out.println(counter);
//				a.MAIN_READY = p.MAIN_READY;
//
//////				r.mouseMove(50, 50);
//////				Thread.sleep(500);
//////				r.mouseMove(200, 200);
//				
//				//get turn #. For whatever reason it would glitch on the first run sometimes. So the loop...
//				int turn = 0;
//				for(int i = 0; i< 2; i++) {
//					turn = p.findTurn();
//					Thread.sleep(50);
//				}
//				
//				//this is the code that for the turn. Should be its own method probably to keep this clean...
//				//there are two turn flags thus the 2 counter count
//				if(counter == 2) {
//					System.out.println("NEXT TURN " + turn);
//					
//					//if there are cards in play lets compute combat moves and make moves
//					if(p.cardsInPlay()) {
//						System.out.println("COMBAT AVAILABLE");
//						a.combat(null, p);
//					}
//					
//					Thread.sleep(1000);	
//					
//					Card played = a.playCurve();
//					//now add the played cards to an array
//					int[] playedCards = new int[8];
//					//initialize
//					for(int i =0; i< 8; i++) {
//						playedCards[i] = -1;
//					}
//					if(played != null)
//						playedCards[0] = a.findCard(p, played.EntityID);
//					
//					//check for charge combat
//					a.combat(playedCards, p);
//					
//					b.endTurn();
//					counter = 0;
//				}
//			}
//		}	
		//System.out.println(a.isMyTurn());
	}
	
	public void loadDB() {
		cDB = new CardDatabase();
	}
	
	public void combat(int[] played, Parser p) throws IOException, InterruptedException, AWTException {		
		
		//gets the list of combat combinations and the list of best combinations of combat combinations
		Card[][][] combat = combatCombinations(played, p);
		
		//int combos[][] = combinationsCombinations(combat);
		//a.printComboCombos(combos);
		//int b = bestCombat(combos);

		int combatValues[][] =  combatCombinValues(combat);
		//printCombatCombValues(combatValues);

		int[]best = pickBestTrades(combatValues, combat);
		printBestTrades(best);
		int[][] bestComs = bestCombinValues(combatValues);
		
		//int combos[][] = combinationsCombinations(combat);
		//printComboCombos(combos);
		
//		int b = bestCombat(combos);
		//System.out.println(b);		
		
		System.out.println("\nThe best combat moves are...");
		for(int i = 0; i< 8; i++) {
			if(best[i] != -1) {
				for(int j = 0; j < 8; j++) {
					if(bestComs[i][0] != -1000)
						if(combat[i][bestComs[i][0]][j] != null && p.enPlayCards[i] !=  null)
							System.out.println(combat[i][bestComs[i][0]][j].name + " attacking " + p.enPlayCards[i].name);
				}
			}
		}
//		System.out.println("The best combat moves are...");
//		for(int i = 1; i< 8; i++) {
//			if(combos[0][i] != -1000) {
//				for(int j = 0; j < 8; j++) {
//					if(combat[i][combos[b][i]][j] != null && p.enPlayCards[i] != null)
//						System.out.println(combat[i][combos[b][i]][j].name + " attacking " + p.enPlayCards[i].name);
//				}
//			}
//		}
		
		Bot bot = new Bot(p);
		int enPlay = p.numEnCardsInPlay();
		System.out.println("en play is " + enPlay);

		if(enPlay > 0) {
		for(int i = 1; i < 8; i++) {
			//find the card that attacks enemy i
			if(best[i] != -1) {
				for(int j = 0; j < 8; j++) {
					if(bestComs[i][0] != -1000) {
						if(combat[i][bestComs[i][0]][j] != null && p.enPlayCards[i] !=  null) {
							System.out.println(combat[i][bestComs[i][0]][j].name + " attacking " + p.enPlayCards[i].name);
							int c = findCard(p, combat[i][bestComs[i][0]][j].EntityID);
							//decide whether to make this attack or if face is better
							//boolean faceOrTrade = faceOrTrade(bestComs, i);
							//if(faceOrTrade == true)
								bot.attack(c, i, bot.enPlayHeight);
//							else
//								bot.attackFace(c);
						}
					}
				}
				}
		}
		} else {
			//if no enemies go face with each card in play.
			for(int i = 0; i< 8; i++) {
				if(p.myPlayCards[i] != null)
					bot.attackFace(i);
			}
		}
	}
	
	public void chargeCombat(Card c) {
		
	}
	
	public boolean faceOrTrade(int[][] comb, int index) throws IOException, InterruptedException {
		
		
		if(comb[index][1] > 20)
			return true;
		else
			return false;
	}
	
	public void printBestCombat(Parser p) throws IOException, InterruptedException {
				
		Card[][][] combat =combatCombinations(null, p);
		
		//printCombatCombinations();

		int combatValues[][] =  combatCombinValues(combat);
		//printCombatCombValues(combatValues);

		int[]best = pickBestTrades(combatValues, combat);
		printBestTrades(best);
		
		int[][] bestComs = bestCombinValues(combatValues);
		
		//int combos[][] = combinationsCombinations(combat);
		//printComboCombos(combos);
		
//		int b = bestCombat(combos);
		//System.out.println(b);		
		
		System.out.println("\nThe best combat moves are...");
		for(int i = 0; i< 8; i++) {
			if(best[i] != -1) {
				for(int j = 0; j < 8; j++) {
					if(bestComs[i][0] != -1000)
						if(combat[i][bestComs[i][0]][j] != null && p.enPlayCards[i] !=  null)
							System.out.println(combat[i][bestComs[i][0]][j].name + " attacking " + p.enPlayCards[i].name);
				}
			}
		}
	}
	
	
	public int findCard(Parser p, int id) {
		for(int i = 0; i < 8; i++) {
			if(p.myPlayCards[i] != null) {
				if(p.myPlayCards[i].EntityID == id)
					return i;
			}
		}
		return -1;
	}
	
	public void printBestTrades(int[] c) {
		for(int i = 0; i< 8; i++){
			 System.out.print(c[i] + " ");
			}
		}
	
	public void printCombatCombValues(int[][] c) {
		for(int i = 0; i< 8; i++){
				System.out.print("\nCombination " + i + "\n");
			for(int j = 0; j<50; j++) {
				if(c[i][j] != -1000)
					System.out.print(c[i][j] + " for enemy " + i + ", ");
			}
		}
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
		
		Card[][][] c = combatCombinations(null, p);
        
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
	public int getTradeValue(Card[][][] c, int cardNum, int combNum, Parser p) throws IOException, InterruptedException {
		int myCardValue = 0;
		
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
			
			System.out.println("ENEMY " + p.enPlayCards[cardNum].name + " " + p.enPlayCards[cardNum].atk + "/"
					+ p.enPlayCards[cardNum].hp + " value " + enemyVal);
		}
		
		int enemHPBeforeSpell = 0;
		int myHP = 0;
		for(int i = 0; i < 8; i++) {
			
			if(c[cardNum][combNum][i] != null & p.enPlayCards[cardNum] != null) {
				myHP = c[cardNum][combNum][i].hp - p.enPlayCards[cardNum].atk;
				
				//my card value = the weighted stats of my alive minions after the trade
				if(myHP > 0) {
					myCardValue += myHP + c[cardNum][combNum][i].atk * 2;
					
				//else if this minion died.. give a bonus if its stats were worse than the enemies weighted ones
				} else {
					int myStats = c[cardNum][combNum][i].atk - p.enPlayCards[cardNum].atk;
					if(myStats < 0 && c[cardNum][combNum][i].spell != 1) {
						//magnifies the value for cards with small atk and health 
						//I did this so the algorithm will prefer trading smaller minions when it can
						myCardValue += myStats * -2 * (p.enPlayCards[cardNum].atk/c[cardNum][combNum][i].atk 
								+ 10 / c[cardNum][combNum][i].hp);
					}
				}
				
				//calculate spells
				if(c[cardNum][combNum][i].spell == 1) {
				 	myCardValue += spellValue(c[cardNum][combNum][i], enemHPBeforeSpell);
				}
				
				System.out.println("MY " + c[cardNum][combNum][i].name + " " + c[cardNum][combNum][i].atk + "/"
						+ c[cardNum][combNum][i].hp + " value of" + myCardValue);
			}
		}
		
		return myCardValue - enemyVal;
	}
	
	/**
	 * Gives the appropriate scaling value to spells.
	 * @param a
	 * @param enHealth
	 * @return
	 */
	public int spellValue(Card a, int enHealth) {
		int val = enHealth - a.atk;
		//if the spell is a perfect amount to finish the kill add a bonus. If the spell is slightly overkill still give a small bonus
		if(val == 0)  {
			val = 15;
		}
		//if spell is way beyond overkill attach a low value to it to it e.g. fireball for 6 to a 1/1
		if(val < 0) {
			val = val * -1;
			if(val > 3)
				val = 5;
			else
				val = 7;
		}
		
		return val;
	}
	
	/**Returns the combined trade values for each combination per enemy card
	 * @throws InterruptedException 
	 * @throws IOException **/
	public int[][] combatCombinValues(Card[][][] c) throws IOException, InterruptedException {
		
		Parser p = new Parser();
		p.parse();
		
		int enLength = enPlayLength(c);
		//System.out.println(enLength);
		int tradeValueSum = 0;
		int[][] cValues = new int[8][50];
		
		//initialize
		for(int i = 0; i< 8; i++) {
			for(int j = 0; j< 50; j++) {
				cValues[i][j] = -1000;
			}
		}

			for(int i= 1; i < 8; i++)  {
				System.out.println("\nEnemy card # " + i + "\n");
				for(int j = 0; j < 50; j++) {
					if(c[i][j][0] != null) {
						System.out.println("\nCombination # " + j + "\n");
						//loop through the cards in the combination and add their trade values
							tradeValueSum = getTradeValue(c, i, j, p);
							System.out.println(tradeValueSum);
							cValues[i][j] = tradeValueSum;
					}
				}
			}
			return cValues;
	}
	
	public int[][]  bestCombinValues(int[][] c) {
		
		int[][] best = new int[8][2];
		int bestV = -1;
		int index = 0;
		
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j<50; j++) {
				if(c[i][j] != -1000 && c[i][j] > bestV) {
					bestV = c[i][j];
					index = j;
				}
			}
			best[i][0] = index;			
			//System.out.println( "!!!!!" + best[i][0]);

			best[i][1] = bestV;
			bestV = -1;
		}
		return best;
	}
	
	/**Returns indexes of the best combat combination for each enemy that don't intersect**/
	public int[] pickBestTrades(int[][] combatValues, Card[][][] c) {		
		
		int[][] best = bestCombinValues(combatValues);
		int[] picked  = new int[8];
		int[] finalPicked  = new int[8];
		//initialize
		for(int i =0; i<8; i++)
			finalPicked[i] = -1;
		
        boolean first = true;

		for(int p = 0; p<8; p++) {
			
	    int cVal = -1;
        int index = -1;
        int enIndex = 0;
         
		//find the largest trade value and then see if it has intersections
		for(int i = 1; i< 8; i++) {
            if(best[i][1] > cVal) {
            	enIndex = i;
            	index = best[i][0];
            	cVal = best[i][1];
            }
		}
		//mark this index as being used 
		picked[enIndex] = 1;
		//System.out.println("enemy " + enIndex + " combination " + index + " has best value of " + cVal);
		
		//add first to the final array of combinations picked
		if(first == true)
			finalPicked[enIndex] = index;
		
		//now see if it has any intersections with already picked enemies
		for(int j = 0; j < 8; j++) {
			//for each combination card see if conflict and if so try to find non-conflicting one
			int conflict = searchForNonConflicting(c, finalPicked,enIndex, index);
				 if(conflict != -1) {
					finalPicked[enIndex] = conflict;
							}
						}
		//now remove the last picked value from best and loop again...
		best[enIndex][0] = 0;
		best[enIndex][1] = 0;
        first = false;
	}
	//now 
		return finalPicked;
}

public int searchForNonConflicting(Card[][][] c, int[] f, int xIndex, int yIndex){
	int index = -1;
	//check to see if any possible combinations conflict with already picked ones
	for(int u = 0; u< 8; u++) {
		for(int i = 0; i < 50; i++) {
			if(f[u]!= -1) {
			if(overlappingCombinations(c, u, f[u], xIndex, i) != true && c[u][f[u]][0] != null && c[xIndex][i][0] != null) {
				//System.out.println(u + " " + f[u] + " and " + xIndex + " " + i + "don't conflict!");
					index = i;
		}
			}
	}
	}
	return index;
}
public boolean isPicked(int[] p, int index) {
	for(int z = 0; z< 8; z++) {
		if(p[z] == 1 && z == index)
			return true;
	}
	return false;
}
	
	/** Returns a combat combination for every enemy card given all available combinations
	 * O(n^7)...
	 * @throws InterruptedException 
	 * @throws IOException **/
	public int[][] combinationsCombinations(Card[][][] c) throws IOException, InterruptedException {
		
		Parser p = new Parser();
		p.parse();
		
		int enLength = enPlayLength(c);
		//System.out.println(enLength);
		int tradeValueSum = 0;
		int[][] combination = new int[100][9];
		//initialize int array
		for(int o = 0; o < 100; o++) {
			for(int j = 0; j < 9; j++)
				combination[o][j] = -1;
		}
		int combCount = 0;

			//get first combination for enemy card 1 and find all combinations that work with it
			//there can be up to 50 combinations just to make sure all cases are covered
			for(int j = 0; j < 50; j++) {
				//compute this combinations trade value
				if(c[1][j][0] != null) {
				System.out.println("\nCombination # " + j + "\n");
				//System.out.println("BLHAHAHA " + c[1][0][0].name);
				tradeValueSum += getTradeValue(c, 1, j, p);
				} else {
					continue;
				}
				//now compute the next combination for enemy cards (combination 1 of en card 1 with combination 1 of en card 2 
				// and so on if this combination can be used (hasn't already been used)
				int tradeValueL1 = tradeValueSum; //record the tradeValue for the last segment to reset this during each loop
				
				for(int k = 0; k < 50; k++) {
					if(c[2][k][0] != null && overlappingCombinations(c, 1, j, 2, k) != true) {
						tradeValueSum += getTradeValue(c, 2, k, p);
						System.out.println("value of combinations up to enemy card 2 " + tradeValueSum);
						
						//put the values of each combination in the array numbered by each enemy card
						if(enLength == 3) {
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
									tradeValueSum += getTradeValue(c, 3, l, p);
									
									System.out.println("value of combinations up to enemy card 3 " +tradeValueSum);
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
//		System.out.println("_______["+firstXIndex+ "]"+"["+firstYIndex+ "]_______"); 
//		
//		for(int i = 0; i < 8; i++) {
//			if(c[firstXIndex][firstYIndex][i] != null)
//				System.out.println(c[firstXIndex][firstYIndex][i].name);
//		}
//		
//		System.out.println("_______["+secondXIndex+ "]"+"["+secondYIndex+ "]_______"); 
//		for(int i = 0; i < 8; i++) {
//			if(c[secondXIndex][secondYIndex][i] != null)
//				System.out.println(c[secondXIndex][secondYIndex][i].name);
//		}

		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++){
				if( c[firstXIndex][firstYIndex][i] != null && c[secondXIndex][secondYIndex][j] != null)
					//System.out.println(c[firstXIndex][firstYIndex][i].name + " compared to " + c[secondXIndex][secondYIndex][j].name); 
				if(c[firstXIndex][firstYIndex][i] != null && c[secondXIndex][secondYIndex][j] != null) {
					//System.out.println(c[firstXIndex][firstYIndex][i].name + " compared to " + c[secondXIndex][secondYIndex][j].name); 
					if (c[firstXIndex][firstYIndex][i].EntityID == (c[secondXIndex][secondYIndex][j].EntityID)) {
							//System.out.println(c[firstXIndex][firstYIndex][i].name + " is the same as " + c[secondXIndex][secondYIndex][j].name); 
							return true;
					}
				}
			}
		}
		return false;
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
	 * if new cards were played...take them out of our combat array UNLESS they have charge=
	 */
	
	public Card[] chargeCheck(int[] justPlayed, Parser p) {
		//so make new array and only take out the appropriate cards
		Card myPlay[] = new Card[8];
		
		for(int z = 0; z< 8; z++) {
			for(int g = 0; g< 8; g++) {
			//if this is a card that was just played
				if(justPlayed[g] != -1) {
					//if the played card index doesn't = z the old value goes into the new play array 
					//put the card back in unless it was just played
					if(z != justPlayed[g])
						myPlay[z] = p.myPlayCards[z];
			//put in charge minions by searching the card database
			if(cDB.cards.get(p.myPlayCards[z].name) != null) {
				if(cDB.cards.get(p.myPlayCards[z].name).charge == 1) {
					System.out.println("CHARGER");
					myPlay[z] = p.myPlayCards[z];
				}
			}
			
			}
				}
		}
		return myPlay;
	}
	
	/**
	 * returns arrayList of spells in hand and gives them appropriate stats
	 */
		public ArrayList<Card> checkSpells(Card[] c) {
			ArrayList<Card> s = new ArrayList();
			
			for(int i =0; i< c.length; i++) {
				if(c[i] != null) {
					//get card from database to check if its a spell
					DBCard db = cDB.cards.get(c[i].name);
					if(db != null) {
						if(db.spell == 1) {
							c[i].spell = 1;
							c[i].atk = db.atk;
							s.add(c[i]);
						}
					}
				}
		}
			return s;
	}
		
	/**
	 * Combines spells with cards in play
	 */
		
		public Card[] combineSpells(Card[] c, Parser p) {
			ArrayList<Card> spells = checkSpells(p.myHand);
			Card[] combined = new Card[c.length + spells.size()];
			int endOfPlay = 1;
			
			for(int i =1; i< c.length; i++) {
				if(c[i] != null) {
					combined[i] = c[i];
					System.out.println("Added " + c[i].name);
					endOfPlay++;
				}
			}
			
			for(int i = endOfPlay, j =0; i< spells.size() + endOfPlay; i++, j++) {
				combined[i] = spells.get(j);
				System.out.println("Added " + spells.get(j).name);
			}
			
			return combined;
			
		}
	
	/**
	 * Returns a 3D array of combinations available for your cards in play for each enemy card on the field
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public Card[][][] combatCombinations(int[] justPlayed, Parser p) throws IOException, InterruptedException {
		
		int counter = 0;
		Card[][][] combinations = new Card[8][50][8];
				
		//loop for every enemy card
		for(int i = 0; i < p.enPlay.length; i++) {
			
			Card[] myPlay = p.myPlayCards.clone();	
			
			//if new cards were played...take them out of our combat array UNLESS they have charge
			if(justPlayed != null) {
				myPlay = chargeCheck(justPlayed, p);
			}
			
			//get the hp of each enemy card
			int enHP = 0;
			if(p.enPlayCards[i] != null)
				enHP = p.enPlayCards[i].hp;	
			else
				continue;
			
			//get length of playable cards (includes spells and cards in play)
			myPlay = combineSpells(myPlay, p);
			
			for(int o = 0; o < myPlay.length; o++) {
				if(myPlay[o] != null)
					System.out.println("PLAY " + myPlay[o].name);
			}
					
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
				 
				for(int j = 1; j < myPlay.length; j++) {
					
					//System.out.println("checking combinations of my Card in position " + j);
					//reset the added attk every combination
					int addedAttk = 0;
					int comboNumber = 0;

					for(int k =  j; k < myPlay.length; k++) {															
						//System.out.println("k loop " + k + " j is " + j + " " + myPlay[k].name);
						//if we run into null because all are tested
						if(myPlay[k] == null) {
							break;
						}
						
						if(addedAttk < enHP) {
						
							
							//System.out.println("this combination kills " +  p.enPlayCards[i].name + "  " + myPlay[j].name);
							
							addedAttk += myPlay[k].atk;
							
							//put a card object into the combination # for the enemy card
							combinations[i][counter][comboNumber] = myPlay[k];
//							System.out.println("j is " + j + " k is " + k + " " + myPlay[k].name + " with attk " + myPlay[k].atk + 
//									" into indices " + i + " " + counter +  " " + (k-j));
						
						//if the target has been killed...
						} else {
							//increment the combination counter if one has been found
							counter++;
							break;
						}
						comboNumber++;
				}
					//move the counter even if the target hasn't been reached
					//remove combos if it wasn't killed though
					combinations = checkForKill(combinations, p, counter, i, j, myPlay);
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
	
	public Card[][][] checkForKill(Card[][][] combinations, Parser p, int counter, int i, int j, Card [] myPlay) {
		int kill = p.enPlayCards[i].hp;
		int comboNumber = 0;
		//delete combinations that don't kill the target
		for(int l = j; l < myPlay.length; l++) {
			if(combinations[i][counter][comboNumber] != null)  {
				kill -= combinations[i][counter][comboNumber].atk;
//				System.out.println(combinations[i][counter][comboNumber].name + " hits " + p.enPlayCards[i].name +
//						" kill val is " + kill);
				comboNumber++;
			}
		}
		comboNumber = 0;
		//if enemy not killed... clear all combinations here
		if(kill > 0) {
			for(int l = j; l < myPlay.length; l++) {
					combinations[i][counter][comboNumber] = null;
					comboNumber++;
			}
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
	public Card playCurve() throws IOException, AWTException, InterruptedException {
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
			for(int i = 1; i<8; i++) {
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
		
			if(c != null) {
			if(c.atk != -1)
				r.playCard(r.c, cardIndex, r.handHeight);
			else { //it's a spell
				for(int i = 0; i< 8; i++) {
					if(p.enPlay != null)
						r.spellToEnemy(r.c[cardIndex], r.enP[i], r.enPlayHeight);
						break;
				}
			}
		}
		return p.myPlayCards[cardIndex];
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
