import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Parser {
	public int test = 1;
	int myHealth, enHealth = 30;
	String[] hand = new String[11];
	Card[] myHand = new Card[11];
	String[] myPlay = new String[9];
	String[] myPlayHealth = new String[8];
	String[] enPlay = new String[9];
	String[] enPlayHealth = new String[8];
	public Card[] myPlayCards = new Card[9];
	Card[] enPlayCards = new Card[9];
    String previousFirstPlay = "";
    String previousFirstEnPlay ="";
    int MAIN_READY = 0;
    boolean firstPlayer = true;
    String lastAttack = "";
    boolean attackMade = false;
    int lastSimilarNamedAttackerCount = 0;
    int numAttackers = 0;
    HashMap<String,Card> cards = new HashMap<>();
    HashMap <String,Card> cardsByID = new HashMap<>();
    
    /////////
    String file = "C:\\Users\\Erik\\Desktop\\HearthBot\\log.txt";
    /////////

	
    //these scripts transfered the new log files to a new destination
    public Parser() throws IOException, InterruptedException {
    	//String[] cmd = new String[]{"/bin/sh", "/Users/erikorndahl/getLog.sh"};

		//get log file in windows
		ProcessBuilder pb = new ProcessBuilder("C:\\Users\\Erik\\AppData\\Local\\Blizzard\\Hearthstone\\Logs\\getLog.bat");
		pb.redirectError();
		try {
			Process p = pb.start();
			try (InputStream inputStream = p.getInputStream()) {
				int in = -1;
				while ((in = inputStream.read()) != -1) {
					System.out.print((char)in);
				}
			}
			System.out.println("Exited with " + p.waitFor());
		} catch (IOException | InterruptedException ex) {
			ex.printStackTrace();
		}


		//Runtime.getRuntime().exec("cmd /c start C:\\Users\\eorndahl\\AppData\\Local\\Blizzard\\Hearthstone\\Logs\\getLog.bat");
		//Process p = new ProcessBuilder("/Users/erikorndahl/getLog.sh").start();
		//Process pr = Runtime.getRuntime().exec(cmd);
	}
    
    ArrayList<String> names = new ArrayList<String>(); //list of the names of my cards in play

	//will cut off the mulligan phase 
    String lastLine = "TAG_CHANGE Entity=The Innkeeper tag=MULLIGAN_STATE value=INPUT";  
    boolean first = true;

	public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
		//get newest log file...
		
		Parser c = new Parser();
		
		c.parse();
		
		System.out.println(c.myHealth);
		
		System.out.println(c.enHealth);
		
		System.out.print("-----------MY Hand--------\n");
		
		c.printHand();

		System.out.print("-----------My Play--------\n");
				
		c.printMyPlay();
		
		System.out.print("-----------Enemy Play---------\n");
		
		c.printEnPlay();
		
		System.out.print("---------------------\n");
		
		//set the number of the same named card in play		
		//System.out.println(c.cardsByID.get("CS2_147").inPlay);
		

//		c.printCardStats();
//		
//		System.out.print("---------------------\n");
		
//		System.out.println(c.enPlayCards[1].atk);
		
//		System.out.println(c.findTurn());
		
//		System.out.println("\nTurn " + c.findTurn());	
//		System.out.println(c.findTurn());
		
		//String id = "DS1_070";
		//System.out.println(id + " has " + (c.cardsByID.get(id)).hp + " hp and " + (c.cardsByID.get(id)).atk + " attk");
//		String card = "Ironbeak Owl";
//		System.out.println(card + " has " + (c.cards.get(card)).hp + " hp and " + (c.cards.get(card)).atk + " attk " 
//		+ (c.cards.get(card)).cost + " cost");

	}
	
//	public boolean getFirstPlayer() {
//		return firstPlayer;
//	}
	
	public void parse() throws FileNotFoundException, IOException {
		try(BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line = br.readLine();
		    boolean found = true;
		    //only scan new material unless this is the first scan
		    //if(line.contains(lastLine) || first == true)
	  	    	//found = true;
	    	if(found == true) {
		   
	    		while (line != null) {
	    			getHand(line);
	    			getCardsInPlay(1,line);
	    			getCardsInPlay(2,line);
	    			myHealth(line);
	    			enHealth(line);
	    			shiftCardsLeft();
	    			checkCardHealth(line);
    	    		getMyCardStats(line);
    	    		createPlayObjects();
    	    		createHandObjects();
    	    		computeCombat(line);
    	    		checkForPriestHeal(line);

    				
	    			line = br.readLine();
	    		}
	    		//shiftCardsLeft();
	    	}
	    }
	    first = false;
	}
	
	public Card getCard(String s, Card[] ar, int ID) {

		for(int i=0;i<8;i++) {
			if(ar[i] != null) {
			if(ar[i].name.equals(s) && ar[i].EntityID == ID) {
				return ar[i];
			}
			}
		}
		return null;
	}
	
	public void checkForPriestHeal(String line) {
		if(line.contains("BlockType=PLAY Entity=[name=Lesser Heal")) {
			System.out.println(line);
			String splitT[] = line.split("Target=\\[name=");
			String splitT2[] = splitT[1].split("id=");
			String target = splitT2[0].trim();
			int targetPos = getPosition(splitT[1]);
			String splitTID[] = splitT2[1].split(" ");
			int targetID = Integer.parseInt(splitTID[0]);
			
			//get target player
			String splitT3[] = splitT[1].split("player=");
			String splitT4[] = splitT3[1].split("]");
			int targetPlayer = Integer.parseInt(splitT4[0]);
			
			Card tar = new Card();	
			
			if(targetPlayer == 1) {
				tar = getCard(target,myPlayCards, targetID);
			} else {
				tar = getCard(target,enPlayCards, targetID);
			}
			if(tar != null)
				tar.hp =+ 2;
			}
	}
	
	public void myHealth(String line) {
		if(line.contains("player=1] tag=DAMAGE")) {
			String[] split = line.split("player=1] tag=DAMAGE value=");
			myHealth = 30 - Integer.parseInt(split[1]);
		}
	}
	
	public void enHealth(String line) {
		if(line.contains("player=2] tag=DAMAGE")) {
			String[] split = line.split("player=2] tag=DAMAGE value=");
			enHealth = 30 - Integer.parseInt(split[1]);
		}
	}
	
	public void computeCombat(String line) throws FileNotFoundException, IOException{
			if(line.contains("BlockType=ATTACK Entity=[name=")) {
			String split[] = line.split("BlockType=ATTACK Entity=\\[name=");
			String split2[] = split[1].split("id=");
			String attacker = split2[0].trim();
			int attkPos = getPosition(line);
			String split3[] = split2[1].split(" ");
			int attkID = Integer.parseInt(split3[0]);


			//get target
			String splitT[] = line.split("Target=\\[name=");
			String splitT2[] = splitT[1].split("id=");
			String target = splitT2[0].trim();
			int targetPos = getPosition(splitT[1]);
			String splitTID[] = splitT2[1].split(" ");
			int targetID = Integer.parseInt(splitTID[0]);

			
			//get target player
			String splitT3[] = splitT[1].split("player=");
			String splitT4[] = splitT3[1].split("]");
			int targetPlayer = Integer.parseInt(splitT4[0]);
			
			//System.out.println(attacker + " " + attkPos + " " + target + " " +   + targetPos + " and target is player " + targetPlayer);
//			System.out.print("---------ENCOMBAT------------\n");
//
//			printEnPlay();
//			System.out.print("---------MYCOMBAT------------\n");
//
//			printMyPlay();
//			System.out.print("----------COMBAT END-----------\n\n\n");
			String thisAttack = attacker + " " + attkPos + " " + target + " " + targetPos + " and target is player " + targetPlayer;
			
			Card atk = new Card();
			Card tar = new Card();	
			int atkI;
			int tarI;
			
//			if(numAttackers == 0)
//				numAttackers = numOfAttackers(attacker);
			
//			if(lastAttack.equals(thisAttack) && attackMade == true) {
//				attackMade = false;
//			}

			//the log duplicates the attack printout so we have to take account of this. Make sure this attack hasn't already occured.
			if(!lastAttack.equals(thisAttack)) {
				//System.out.print(thisAttack);
			
			//player 1 is you so if so, make the attacker the enemy player
			if(targetPlayer == 1) {
				atk = getCard(attacker,enPlayCards, attkID);
				tar = getCard(target,myPlayCards, targetID);
			} else {
				atk = getCard(attacker,myPlayCards, attkID);
				tar = getCard(target,enPlayCards, targetID);
			}
			
//
//			
//			System.out.println("ATTACKER " + attacker + ((atk == null) ? "error" : atk.atk) +
//					((tar == null) ? "error" : tar.atk));
//			
			
			if(atk != null && tar != null) {
				//System.out.println("INSIDEEEEEEE");
			//System.out.println("attacker stats " + atk.atk +"/" + atk.hp + "  target stats " + tar.atk +"/" + tar.hp + " id " + tar.EntityID);
			atk.hp = atk.hp - tar.atk;
			tar.hp = tar.hp - atk.atk;
			//System.out.println(" TARGET OF ID " + tar.EntityID + " HAS " + tar.hp + " HP") ;
			}
			
			//resets for a new attacker
//			if(lastSimilarNamedAttackerCount == numAttackers ) {
//				lastSimilarNamedAttackerCount = 0;
//				numAttackers = 0;
				//			}
			}

			lastAttack = thisAttack;

			
//    		System.out.print("----------MY PLAY-----------\n");
//			
//			printMyPlay();
//			
//			System.out.print("-----------ENEMY PLAY---------\n");
//			
//			printEnPlay();
//			
//			System.out.print("---------------------\n");
			
			
//			System.out.println(attacker + " " + atk.atk + "/" + atk.hp + " " + target + " " + tar.atk + "/" + tar.hp) ;
		    }
	}
	
	/** returns the # of if there are the same named attacker*/
	public int numOfAttackers(String s) {
		int count = 0;
		for(int i=0; i< 8; i++) {
			if(hand[i] != null) {
			if(hand[i].equals(s)) {
				count++;
			}
			}
		}
		return count;
	}

	/**Needs to order the card object in line with the String Play array*/
	public void createPlayObjects() {
		for(int i = 1; i<9; i++) {    	
//			//update my cards
			if(myPlay[i] != null) {
	    		//Card c = cardsByID.get(cards.get(myPlay[i]).id);	
	    		Card c = cards.get(myPlay[i]);	
	    		//if the card array doesn't have this card yet
	    		//if(myPlayCards[i] != null)
	    			myPlayCards[i] = c;
			} else {
				myPlayCards[i] = null;
			}
			//now update enemy cards
			if(enPlay[i] != null) {
	    		//Card c = cardsByID.get(cards.get(enPlay[i]).id);	
	    		Card c = cards.get(enPlay[i]);	
	    		enPlayCards[i] = c;
			} else {
				enPlayCards[i] = null;
			}
		} 
	}
	
	public void createHandObjects(){
		for(int i = 0; i< 10; i++) {
				myHand[i] = new Card();
				//create a card object for each string and give a name
				if(hand[i] != null && !hand[i].equals(myHand[i].name)) {
					myHand[i].name = hand[i];
			}
		}
	}
	
	public void updateLog() throws IOException {
    	Process p = new ProcessBuilder("/Users/erikorndahl/getLog.sh").start();
	}
	
	public void printLines() throws IOException {
		try(BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line = br.readLine();
		    while (line != null) {
		    	if (line.contains("name=Novice") && line.contains("Zone")) {
		    		System.out.println(line);
		    }
		        line = br.readLine();
		}
		}
	}
	
	public String playerHealth(int i) {		
		return "";
	}
	
	public void setCardId(String name, String ID, int ent_id) {
		try{
		Card c = new Card();
		c = cardsByID.get(ID);
		c.name = name;
		c.EntityID = ent_id;
		//System.out.println(name + " has " + c.hp + " hp and " + c.atk + " attk");
		cards.put(name, c);
		//cards.put(c.EntityID, c);
		} catch (Exception e) {
			//System.out.println("Could not find card " + name);
		}
	}
	
	public void setCardId(String name, String ID) {
		try{
		Card c = new Card();
		c = cardsByID.get(ID);
		c.id = ID;
		c.name = name;
		//System.out.println(name + " has " + c.hp + " hp and " + c.atk + " attk");
		cards.put(name, c);
		//cards.put(c.EntityID, c);
		} catch (Exception e) {
			//System.out.println("Could not find card " + name);
		}
	}
	
	public String getCardId(String line) {
		try{
		String[] split = line.split("cardId=");
		String[] split2 = split[1].split(" ");
		return split2[0].trim();
		}catch (Exception e) {
			
		}
		return null;
	}
	
	public String getCardStatsID(String line) {
		try{
		String[] split = line.split("CardID=");
		String[] split2 = split[1].split(" ");
		return split2[0].trim();
		}catch (Exception e) {
			
		}
		return null;
	}
	
	public String getEntityID(String line) {
		try{
		String[] split = line.split("id=");
		String[] split2 = split[1].split(" ");
		return split2[0].trim();
		}catch (Exception e) {
			
		}
		return null;
	}
	
	public int getHealth(String line){
		//System.out.println("GETTING HEALTH FOR" + line);
		try {
		String[] split = line.split("tag=HEALTH");
		String[] split2 = split[1].split("value=");
		return Integer.parseInt(split2[1].trim());
		} catch (Exception e) {
			//System.out.println("COULD NOT GET HEALTH VALUE");
		}
		return 0;
	}
	
	public int getCost(String line){
		//System.out.println("GETTING HEALTH FOR" + line);
		try {
		String[] split = line.split("tag=COST");
		String[] split2 = split[1].split("value=");
		return Integer.parseInt(split2[1].trim());
		} catch (Exception e) {
			//System.out.println("COULD NOT GET HEALTH VALUE");
		}
		return 0;
	}
	
	public int getAttack(String line){
		try{
		String[] split = line.split("tag=ATK");
		String[] split2 = split[1].split("value=");
		return Integer.parseInt(split2[1].trim());
	} catch (Exception e) {
		//System.out.println("COULD NOT GET ATTACK VALUE");
	}
	return 0;
	}
	
	/**gets card stats when played*/
	public void getMyCardStats(String line) throws FileNotFoundException, IOException {
		//if this is encountered we need to read the next 3 lines to get stats
		String lastLine;
		String Id = "";
		Card c = new Card();			
		c.id = getCardStatsID(line);
		c.inPlay++;
		
		if(line.contains("SHOW_ENTITY - Updating") || line.contains("FULL_ENTITY -")) {
			try {
			int id = Integer.parseInt(getEntityID(line));
			} catch (Exception e) {
			//System.out.println("ID could not be located");	
			}
			try(BufferedReader br = new BufferedReader(new FileReader(file))) {
			    String line2 = br.readLine();
			    int count = 0;
			    boolean found = false;
					    while (line2 != null) {
							if(line2.equals(line)) {
								//System.out.println(c.id + " ----------");
								found = true;
							}
							
							//found the line for the card stats
							if(found == true) {
								
							//this is for spells with no hp / attk
							if(line2.contains("tag=COST") && count == 1) {
								c.cost = getCost(line2);
								break;
							}
							//this is for weapons with no hp
							if(line2.contains("tag=ATK") && count == 1) {
								c.atk = getAttack(line2);
								line2 = br.readLine();
								c.cost = getCost(line2);
								break;
							}
							
							//some cards have an extra first line
							if(!line2.contains("tag=PREMIUM") && count == 1 && !line2.contains("tag=TRIGGER_VISUAL")  && !line2.contains("tag=EXHAUSTED")) {
								count++;
							}
							
							if(count==2) {
									c.hp = getHealth(line2);
									//System.out.println(c.id + " " + getHealth(line2));
							}
				    		if(count==3) {
				    			c.atk = getAttack(line2);
				    		}
				    		if(count==4) {
				    			c.cost = getCost(line2);
				    		}
							//System.out.println("count " +count + " " + line2);
							count++;
							if(count > 4) {
								//System.out.println("---------");
								break;
						}
						}
		    			line2 = br.readLine();
			    }
		}
			}
		cardsByID.put(c.id, c);
		//System.out.println("_________________");
	}
	
	/**will update card health upon buffs*/
	public void checkCardHealth(String line){
		if (line.contains("name=")  && line.contains("tag=HEALTH") && line.contains("[Power]")) {
			String[] health = line.split("value=");
			String HP = health[1].trim();
			myPlayHealth[getPosition(line)] = HP;
		}
	}
	
	
	public void postAttackHealth(String line) {
		if(line.contains("ACTION_START BlockType=ATTACK")) {
			String attacker = getName(line);
			String[] split = line.split("Target=\\[name=");
			String [] split2 = split[1].split("id");
			String attacked = split2[0].trim();
			//System.out.println(attacker + " attacked " + attacked);
		}
	}
	
	/** gets name of card from the line*/
	public String getName(String s) {
		//get the name of the card
		String[] nameSplit = s.split("name=");
		String[] finalName = null;
		
		Pattern pat = Pattern.compile("name=(.*)id=");
		Matcher m = pat.matcher(s);
		
		if(m.find())  {
			finalName = nameSplit[1].split("id=");
			try{
				finalName = finalName[0].split("]");
			} catch (Exception e) {
				
			}
		}
		return finalName[0].trim();
	}
	
	
	/** gets card position*/
	public int getPosition(String s) {
	String[] pos = new String[2];
	String position = "";
	
	if(s.contains("pos from")) {
		pos = s.split("-> ");
		position = pos[1];
	}else if(s.contains("zonePos")) {
		String[] split = s.split("zonePos=");
		String[] split2 = split[1].split("cardId");
		position = split2[0].trim();
	}
	return Integer.parseInt(position);
	}

	public void printHand() {
	for(int i = 0 ; i < 10 ; i++) {
		if(hand[i] != null)
			System.out.println(hand[i] + " is in position " + i);
		}
	}
	
	public void printMyPlay() {
		
		for(int i = 1 ; i < 9 ; i++) {
			if(myPlay[i] != null) {
				try {
					System.out.println(myPlay[i] + " is in position " + i + " " +  myPlayCards[i].atk + "/" + myPlayCards[i].hp + " id " + myPlayCards[i].EntityID
							+ " cost " + myPlayCards[i].cost);
				} catch (Exception e) {
				System.out.println("\n\nPROBLEM NAME IS " + myPlay[i] +"\n\n");
			}
			}}
		}
	
	public void printCardStats() {
		
		for(int i = 0 ; i < 8 ; i++) {
			Card c = new Card();
			if(myPlay[i] != null) {
			c = cards.get(myPlay[i]);
			System.out.println(myPlay[i] + " " + c.atk + "/" + c.hp);
			}
		}
		
		for(int i = 0 ; i < 8 ; i++) {
			Card c = new Card();
			if(enPlay[i] != null) {
			c = cards.get(enPlay[i]);
			System.out.println(enPlay[i] + " " + c.atk + "/" + c.hp);
			}
		}
		}
	
	public void printEnPlay() {
		
		for(int i = 0 ; i < 8 ; i++) {
			try{
			if(enPlay[i] != null) {
			System.out.println(enPlay[i] + " is in position " + i + " " +  enPlayCards[i].atk + "/" + enPlayCards[i].hp + " id " + enPlayCards[i].EntityID);
			}
			} catch (Exception e) {
				System.out.println("couldn't find " + enPlay[i] + " in hash!");
			}
			}
		
		}
	
	//add method to check if you went first
	public int findTurn() throws FileNotFoundException, IOException {
		try(BufferedReader br = new BufferedReader(new FileReader(file))) {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();
		    String turn = "";
		    boolean firstFound = false;
		    while (line != null) {
		    	
		    	if(line.contains("tag=TURN value=")) {
		    		String t[] = line.split("value=");
		    		turn = t[1];
		    	}
		        line = br.readLine();
		    }
		
		int t = -1;
		try{
		t = Integer.parseInt(turn);
		} catch (Exception e) {
		}
		return t;
		}
	}
	
	public int fileLength() throws FileNotFoundException, IOException {
		try(BufferedReader br = new BufferedReader(new FileReader(file))) {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();
		    int lineCount = 0;
		    while (line != null) {
		    	lineCount++;
		        line = br.readLine();
		    }
		return lineCount;
		}
	}
	
	
	/**shifts cards if one is destroyed in between them**/
	public void shiftCardsLeft(){
		for(int i=0; i < myPlay.length; i++) {
			if(i!= myPlay.length - 1) {
				if(myPlay[i] == null && i != 0 && myPlay[i + 1] != null || 
						myPlay[i] != null && myPlay[i + 1] != null &&
						cards.get(myPlay[i]).inPlay == 1 && myPlay[i + 1].equals(myPlay[i])){
					//System.out.println("killed " + myPlay[i + 1] + " i is " + i + "myPlay[i] is " + myPlay[i]);
//					for(int j = 0 ; j < 8 ; j++) {
//						System.out.println(myPlay[j]);
//						}
					myPlay[i] = myPlay[i+1];
					myPlay[i+1] = null;
				}
			}
		}
		
		for(int i=0; i < enPlay.length; i++) {
			if(i!= enPlay.length - 1) {
				//System.out.println(enPlay[i]);
				if(enPlay[i] == null && i != 0 && enPlay[i + 1] != null || 
						enPlay[i] != null && enPlay[i + 1] != null &&
						cards.get(enPlay[i]).inPlay == 1 && enPlay[i + 1].equals(enPlay[i])) {
							//System.out.println(enPlay[i] + " has a duplicate!!!");
							enPlay[i] = enPlay[i+1];
							enPlay[i+1] = null;
				}
			}
		}
	}
	
	/**shifts cards if one is shifted into position 1**/
	public void shiftCardsLeftToOne(){
			if(myPlay[1] != null && myPlay[2] != null) {
				if(!previousFirstPlay.equals(myPlay[1]) && myPlay[2].equals(myPlay[1])) {
					myPlay[2] = null;
				}
			}
			if(enPlay[1] != null && enPlay[2] != null) {
				if(!previousFirstEnPlay.equals(enPlay[1]) && enPlay[2].equals(enPlay[1])) {
					enPlay[2] = null;
				}
			}
	}
	
//	public void checkSpellUse(String line) throws FileNotFoundException, IOException {
//		    	if (line.contains("[name=") && line.contains("zone=GRAVEYARD")) { //if a spell was sent to the graveyard
//		    		String[] nameSplit = line.split("name=");
//		    		String[] s2 = nameSplit[1].split("id");
//		    		
//		    		//now remove spell from hand
//		    		for(int i = 0; i< hand.length; i++) {
//		    			if(hand[i] != null) {
//		    			if(hand[i].equals(s2[0].trim())) {
//		    				hand[i] = null;
//		    			}
//		    		}
//		    		}
//		    	}
//	}
	
	public void checkTurnChange() throws IOException{
		try(BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line = br.readLine();
			//reset main_ready count
			MAIN_READY = 0;
			while (line != null) {
				if(line.contains("tag=NEXT_STEP value=MAIN_READY")) {
					MAIN_READY++;
				}
				line = br.readLine();
			}
		}
	}
	
	public boolean cardsInPlay() {
			int n = 0;
			for(int i = 0; i< myPlay.length; i++) {
				if(myPlay[i] != null)
					n++;
			}
			if(n > 0)
				return true;
			else
				return false;
	}
	
	public int numMyCardsInPlay() {
		int n = 0;
		for(int i = 0; i< myPlay.length; i++) {
			if(myPlay[i] != null)
				n++;
		}
		return n;
}
	
	public int numEnCardsInPlay() {
		int n = 0;
		for(int i = 0; i< enPlay.length; i++) {
			if(enPlay[i] != null) {
				n++;
			}
		}
		return n;
}

	
	/**
	 * Puts new cards into the hand array
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	
	//should rework to parse from the last line 
	public void getHand(String line) throws FileNotFoundException, IOException {
	    	
	    	if (line.contains("[name=") && line.contains("zone=HAND") && line.contains("pos from") && !lastLine.contains("FRIENDLY PLAY") 
	    			|| line.contains("name=") && line.contains("zone=HAND") && line.contains("TRANSITIONING") && !lastLine.contains("FRIENDLY PLAY"))
	    			{ 
	    		
	    		String[] pos = new String[2];
	    		String name = getName(line);
	    		setCardId(name,getCardId(line));
	    		
	    		if(name.equals("The Coin")) {
			    		firstPlayer = false;
	    		}
	    		
	    		if( line.contains("pos from")) {
	    		pos = line.split("-> ");
	    		//System.out.println(s2[0].trim() + " " + pos[1]); 
	    		hand[Integer.parseInt(pos[1])] = name; //global hand array is updated with new cards and card positions
	    		}
	    		
	    		//now remove old card positions if they change or are removed from hand...
	    		if(line.contains("to FRIENDLY PLAY")) { //then the card is moving into play so remove from hand pos
	    			for(int i = 0; i< hand.length; i++) {
	    			if(hand[i] != null) {
	    			if(hand[i].equals(name)) {
	    				//System.out.println("DELETED " + s2[0].trim());
	    				hand[i] = null;
	    			}
	    			}
	    		}
	    		}
	    		//now change the position of card in hand if it's moved
	    		if( line.contains("pos from")) {
	    		for(int i = 0; i< hand.length; i++) {
	    			//if the line moves the card one position left... then delete the card from its old position. This won't delete
	    			//newly added cards with a duplicate before them
	    			if(hand[i] != null) {
	    			if(hand[i].equals(name) && i == Integer.parseInt(pos[1]) + 1 ) {
	    				hand[i] = null;
	    			}
	    			}
	    		}
	    	}
	    			}
	    	
	        lastLine = line;
}

	/**Need to add shifting to cards in play in one dies...Meaning null its old position**/
public void getCardsInPlay(int p, String line) throws FileNotFoundException, IOException {

	    String[] boardPosition = new String[7];
	  //[Power] GameState.DebugPrintPower() -     TAG_CHANGE Entity=[name=Murloc Raider id=53 zone=PLAY zonePos=1 cardId=CS2_168 player=2] tag=ZONE value=GRAVEYARD
	    	if (line.contains("name=") && line.contains("player=" + p) && line.contains("zone=PLAY")
	    			&& !line.contains("Hero") && line.contains("dstPos=")
	    			&& !line.contains("dstPos=0") && !line.contains("zonePos=0") 
	    			
	    			|| line.contains("name=") 
	    			&& line.contains("zone=GRAVEYARD")
	    			&& !line.contains("Hero") && line.contains("dstPos=") && !line.contains("zonePos=0") 
	    			&& !line.contains("dstPos=0")
	    	
	    			||line.contains("name=")
	    		     && line.contains("tag=ZONE value=GRAVEYARD")
	    		     && line.contains("zone=PLAY")
	    		     && line.contains("TAG_CHANGE Entity=")) 
	    	{
    			
	    		//System.out.println(line);
	    		
	    		//get the name of the card
	    		String[] nameSplit = line.split("name=");
	    		String[] finalName = null;
	    		String destroyed = "";
	    		
	    		Pattern pat = Pattern.compile("name=(.*)id=");
	    		Matcher m = pat.matcher(line);
	    		
	    		if(m.find())  {
	    			finalName = nameSplit[1].split("id=");
	    			try{
	    				finalName = finalName[0].split("]");
	    			} catch (Exception e) {
	    				
	    			}
	    		}
	    		
	    		//get the position
	    		String[] position = line.split("dstPos=");
	    		int pos = -1;
	    		if(position.length > 1) {
	    			//System.out.println(position[1].replace("]", ""));
	    			pos = Integer.parseInt(position[1].replace("]", "").trim());
	    		}
	    		
	    		if(pos == -1) {
		    		String[] pos2 = line.split("zonePos=");
		    		String[] pos3 = pos2[1].split("cardId");
		    		pos = Integer.parseInt(pos3[0].trim());
	    		}
	    		
	    		//get entity ID
	    		int ID = Integer.parseInt(getEntityID(line));
	    			    		
	    		//get name...
	    		String cardName = finalName[0].trim();
	    		//System.out.println(line + "_____" + cardName);
	    		
	    		//check to see if it was destroyed
	    		if(line.contains("TO_BE_DESTROYED") || line.contains("zone=GRAVEYARD") && !line.contains("tag=NUM_ATTACKS_THIS_TURN")
	    				|| line.contains("tag=ZONE value=GRAVEYARD")) {
	    			destroyed = (cardName);
	    			//System.out.println(cardName + " DESTROYED");
	    		}
	    		
	    		//add name to card position
	    	    if(pos != -1 && !line.contains("GRAVEYARD")) {
		    	   //System.out.println("ADDING CARD TO PLAY " + cardName + " at position " + pos);
		    	    
		    	    //if the card is shifting left. Might cancel adding cards before other cards....don;t know the output yet
		    	    if(pos + 1 < 8) {
	    	    	if(myPlay[pos+1] == cardName) {
		    	    	myPlay[pos+1] = null;
		    	    }
		    	    }
	    	    if(p==1) {
	    	    		//set card ID in the hashmap... will be used for getting card stats
	    	    		//System.out.println(line);
	 		    	  // System.out.println("ADDING CARD TO PLAY " + cardName + " at position " + pos);
	    	    		setCardId(cardName,getCardId(line), ID);
	    	    		//System.out.println("Card name and ID are set to " + cardName + " and " + getCardId(line));	    	    			
	    	    		
	    	    		//sets stats for new card and updates # of cards in play
	    	    		//getMyCardStats(line);
	    	    		
	    	    		myPlay[pos] = cardName;
	    	    		//check for shift to first position
	    	    		shiftCardsLeftToOne();
	    	    		previousFirstPlay = cardName;
	    	    	} else {
	    	    		
	    	    		//System.out.println(line);
	    	    		//System.out.println("Card name and ID are set to " + cardName + " and " + getCardId(line));
	    	    		setCardId(cardName,getCardId(line), ID);
	    	    		//getMyCardStats(line);
	    	    		enPlay[pos] = cardName;
	    	    		shiftCardsLeftToOne();
	    	    		previousFirstEnPlay = cardName;
	    	    	}
	    	    }
	    		   	    	    	
	    	   //now check to see if any cards were destroyed and eliminate their array position
	    	    if(p == 1) {
	    	   for(int j = 0; j < myPlay.length; j++) {
	    	    	if(myPlay[j] != null) {
	    			   if(myPlay[j].equals(destroyed)) {
	    		    	    //System.out.println("REMOVING CARD " + myPlay[j] + " position " + j);
	    				   	if(j == pos) {
	    				   	myPlay[j] = null;
	    	    			destroyed = ""; //remove the destroyed card from destroyed...
	    		    	
	    	    			//method for if first card is destroyed
	    	    			if(pos == 1) 
	    	    	    	    shiftCardsLeftToOne();
	    				   	}
	    			   }
	    	    	}
	    	    }
	    	} else {
	    		for(int j = 0; j < enPlay.length; j++) {
		    	    	if(enPlay[j] != null) {
		    			   if(enPlay[j].equals(destroyed)) {
		    	    			//System.out.println("Removing " + enPlay[j]); 
		    				   if(j == pos) { 
		    				   enPlay[j] = null;
		    	    			destroyed = ""; //remove the destroyed card from destroyed...
		    		    	    //shifts cards to the left if one is killed to its left
		    		    	   //shiftCardsLeft();
		    	    			if(pos == 1) 
		    	    	    	    shiftCardsLeftToOne();
		    				   }
		    			   }
		    	    	}
		    	    	}
		    	    }
	    }
}

public void parseHand() throws FileNotFoundException, IOException {
	try(BufferedReader br = new BufferedReader(new FileReader(file))) {
	String line = br.readLine();
	while(line != null) {
	if (line.contains("[name=") && line.contains("zone=HAND") && line.contains("pos from") && !lastLine.contains("FRIENDLY PLAY") 
			|| line.contains("name=") && line.contains("zone=HAND") && line.contains("TRANSITIONING") && !lastLine.contains("FRIENDLY PLAY"))
			{ 
		
		String[] pos = new String[2];
		String name = getName(line);
		setCardId(name,getCardId(line));
		
		if(name.equals("The Coin")) {
	    		firstPlayer = false;
		}
		
		if( line.contains("pos from")) {
		pos = line.split("-> ");
		//System.out.println(s2[0].trim() + " " + pos[1]); 
		hand[Integer.parseInt(pos[1])] = name; //global hand array is updated with new cards and card positions
		}
		
		//now remove old card positions if they change or are removed from hand...
		if(line.contains("to FRIENDLY PLAY")) { //then the card is moving into play so remove from hand pos
			for(int i = 0; i< hand.length; i++) {
			if(hand[i] != null) {
			if(hand[i].equals(name)) {
				//System.out.println("DELETED " + s2[0].trim());
				hand[i] = null;
			}
			}
		}
		}
		//now change the position of card in hand if it's moved
		if( line.contains("pos from")) {
		for(int i = 0; i< hand.length; i++) {
			//if the line moves the card one position left... then delete the card from its old position. This won't delete
			//newly added cards with a duplicate before them
			if(hand[i] != null) {
			if(hand[i].equals(name) && i == Integer.parseInt(pos[1]) + 1 ) {
				hand[i] = null;
			}
			}
		}
	}
			}
	line = br.readLine();
}
	}
}

}