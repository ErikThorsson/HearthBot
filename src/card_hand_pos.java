import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class card_hand_pos {
	String[] hand = new String[11];
	String[] myPlay = new String[8];
	String[] myPlayHealth = new String[8];
	String[] enPlay = new String[8];
	String[] enPlayHealth = new String[8];
    String previousFirstPlay = "";
    String previousFirstEnPlay ="";
    HashMap<String,Card> cards = new HashMap<>();
    HashMap <String,Card> cardsByID = new HashMap<>();
	
    public card_hand_pos() throws IOException {
    	String[] myHand = hand;
    	String[] play = myPlay;
    	String[] cmd = new String[]{"/bin/sh", "/Users/erikorndahl/getLog.sh"};
		Process pr = Runtime.getRuntime().exec(cmd);
	}
    
//    public String[] hand() {
//    	return this.hand;
//    }
    
    ArrayList<String> names = new ArrayList<String>(); //list of the names of my cards in play

	//will cut off the mulligan phase 
    String lastLine = "TAG_CHANGE Entity=The Innkeeper tag=MULLIGAN_STATE value=INPUT";  
    boolean first = true;

	public static void main(String[] args) throws FileNotFoundException, IOException {
		//get newest log file...
		
		card_hand_pos c = new card_hand_pos();
		
		c.parse();
		
		c.printHand();

		System.out.print("---------------------\n");
				
		c.printMyPlay();
		
		System.out.print("---------------------\n");
		
		c.printEnPlay();
		
		System.out.print("---------------------\n");

		c.printMyPlayHealth();
		
		
//		System.out.println("\nTurn " + c.findTurn());	
//		c.printLines();
		
		//String id = "DS1_070";
		//System.out.println(id + " has " + (c.cardsByID.get(id)).hp + " hp and " + (c.cardsByID.get(id)).atk + " attk");
		//String card = "Novice Engineer";
		//System.out.println(card + " has " + (c.cards.get(card)).hp + " hp and " + (c.cards.get(card)).atk + " attk");

	}
	
	public void printLines() throws IOException {
		try(BufferedReader br = new BufferedReader(new FileReader("/Users/erikorndahl/Desktop/log.txt"))) {
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
	
	public void setCardId(String name, String ID) {
		Card c = new Card();
		c = cardsByID.get(ID);
		//System.out.println(name + " has " + c.hp + " hp and " + c.atk + " attk");
		cards.put(name, c);
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
		
		if(line.contains("SHOW_ENTITY - Updating Entity=")) {

			try(BufferedReader br = new BufferedReader(new FileReader("/Users/erikorndahl/Desktop/log.txt"))) {
			    String line2 = br.readLine();
			    int count = 0;
			    boolean found = false;
					    while (line2 != null) {
							if(line2.equals(line)) {
								//System.out.println(c.id + " ----------");
								found = true;
							}
							//some cards have an extra first line
							if(!line2.contains("tag=PREMIUM") && count == 1)
								count++;
							if(found == true) {
								if(count==2) {
									c.hp = getHealth(line2);
									//System.out.println(c.id + " " + getHealth(line2));
							}
				    		if(count==3) {
				    			c.atk = getAttack(line2);
				    		}
							//System.out.println("count " +count + " " + line2);
							count++;
							if(count > 3) {
								//System.out.println("---------");
								break;
						}
						}
		    			line2 = br.readLine();
//		    		}
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
		String[] nameSplit = s.split("name=");
		String[] s2 = nameSplit[1].split("id");
		return s2[0].trim();
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
		System.out.println(hand[i] + " is in position " + i);
		}
	}
	
	public void printMyPlay() {
		
		for(int i = 0 ; i < 8 ; i++) {
			System.out.println(myPlay[i] + " is in position " + i);
			}
		}
	
	public void printMyPlayHealth() {
		
		for(int i = 0 ; i < 8 ; i++) {
			System.out.println(myPlay[i] + " has " + myPlayHealth[i] + " health.");
			}
		}
	
	public void printEnPlay() {
		
		for(int i = 0 ; i < 8 ; i++) {
			try{
			System.out.println(enPlay[i] + " is in position " + i + " with " + cards.get(enPlay[i]).inPlay + " in play");
			} catch (Exception e) {
				System.out.println("couldn't find " + enPlay[i] + " in hash!");
			}
			}
		
		}
	
	public String findTurn() throws FileNotFoundException, IOException {
		try(BufferedReader br = new BufferedReader(new FileReader("/Users/erikorndahl/Desktop/log.txt"))) {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();
		    String turn = "";
		    while (line != null) {
		    	if(line.contains("tag=TURN value=")) {
		    		String t[] = line.split("value=");
		    		turn = t[1];
		    	}
		        line = br.readLine();
		    }
		return turn;
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
	
	public void checkSpellUse(String line) throws FileNotFoundException, IOException {
		    	if (line.contains("[name=") && line.contains("zone=GRAVEYARD")) { //if a spell was sent to the graveyard
		    		String[] nameSplit = line.split("name=");
		    		String[] s2 = nameSplit[1].split("id");
		    		
		    		//now remove spell from hand
		    		for(int i = 0; i< hand.length; i++) {
		    			if(hand[i] != null) {
		    			if(hand[i].equals(s2[0].trim())) {
		    				hand[i] = null;
		    			}
		    		}
		    		}
		    	}
	}
	
	public void parse() throws FileNotFoundException, IOException {
		try(BufferedReader br = new BufferedReader(new FileReader("/Users/erikorndahl/Desktop/log.txt"))) {
		    String line = br.readLine();
		    boolean found = true;
		    //only scan new material unless this is the first scan
		    if(line.contains(lastLine) || first == true)
	  	    	found = true;
	    	if(found == true) {
		   
	    		while (line != null) {
	    			getHand(line);
	    			checkSpellUse(line);
	    			getCardsInPlay(1,line);
	    			getCardsInPlay(2,line);
	    			checkCardHealth(line);
    	    		getMyCardStats(line);
	    			line = br.readLine();
	    		}
	    		shiftCardsLeft();
	    	}
	    }
	    first = false;
	}

	
	/**
	 * Puts new cards into the hand array
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void getHand(String line) throws FileNotFoundException, IOException {
	    	
	    	if (line.contains("[name=") && line.contains("zone=HAND") && line.contains("pos from") && !lastLine.contains("FRIENDLY PLAY") 
	    			|| line.contains("name=") && line.contains("zone=HAND") && line.contains("TRANSITIONING") && !lastLine.contains("FRIENDLY PLAY"))
	    			{ 
	    		
	    		String[] pos = new String[2];
	    		String name = getName(line);
	    		
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
	    	
	    	if (line.contains("name=") && line.contains("player=" + p) && line.contains("zone=PLAY")
	    			&& !line.contains("Hero") && line.contains("dstPos=")
	    			&& !line.contains("dstPos=0") && !line.contains("zonePos=0") || line.contains("name=") 
	    			&& line.contains("player=" + p) && line.contains("zone=GRAVEYARD")
	    			&& !line.contains("Hero") && line.contains("dstPos=") && !line.contains("zonePos=0") 
	    			&& !line.contains("dstPos=0"))  {
    			
	    		//System.out.println(line);
	    		
	    		//get the name of the card
	    		String[] nameSplit = line.split("name=");
	    		String[] finalName = null;
	    		ArrayList<String> destroyed = new ArrayList();
	    		
	    		
	    		Pattern pat = Pattern.compile("name=(.*)id=");
	    		Matcher m = pat.matcher(line);
	    		if(m.find())  {
		    		//System.out.println(line);
	    			finalName = nameSplit[1].split("id");
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
	    			    		
	    		//get name...
	    		String cardName = finalName[0].trim();
	    		//System.out.println(line + "_____" + cardName);
	    		
	    		//check to see if it was destroyed
	    		if(line.contains("TO_BE_DESTROYED") || line.contains("zone=GRAVEYARD") && !line.contains("tag=NUM_ATTACKS_THIS_TURN")) {
	    			destroyed.add(cardName);
	    			//System.out.println(cardName + " DESTROYED");
	    		}
	    		
	    		//add name to card position
	    	    if(pos != -1 && !line.contains("GRAVEYARD")) {
		    	   //System.out.println("ADDING CARD TO PLAY " + cardName + " at position " + pos);
		    	    
		    	    //if the card is shifting left. Might cancel adding cards before other cards....don;t know the output yet
		    	    if(myPlay[pos+1] == cardName) {
		    	    	myPlay[pos+1] = null;
		    	    }
	    	    	if(p==1) {
	    	    		//set card ID in the hashmap... will be used for getting card stats
	    	    		//System.out.println(line);
	    	    		setCardId(cardName,getCardId(line));
	    	    		//System.out.println("Card name and ID are set to " + cardName + " and " + getCardId(line));
	    	    		myPlay[pos] = cardName;
	    	    		if(pos == 1)
	    	    			previousFirstPlay = cardName;
	    	    	}
	    	    	else {
	    	    		//System.out.println(line);
	    	    		//System.out.println("Card name and ID are set to " + cardName + " and " + getCardId(line));
	    	    		setCardId(cardName,getCardId(line));
	    	    		enPlay[pos] = cardName;
	    	    		previousFirstEnPlay = cardName;
	    	    	}
	    	    }
	    		   	    	    	
	    	   //now check to see if any cards were destroyed and eliminate their array position
	    	    if(p == 1) {
	    	   for(int j = 0; j < myPlay.length; j++) {
	    		   for(int i = 0 ; i < destroyed.size(); i++) {
	    	    	if(myPlay[j] != null) {
	    			   if(myPlay[j].equals(destroyed.get(i))) {
	    		    	    //System.out.println("REMOVING CARD" + myPlay[j]);
	    	    			//System.out.println("Removing " + myPlay[j]); 
	    				    myPlay[j] = null;
	    	    			destroyed.remove(i); //remove the destroyed card from destroyed...
	    		    	    //shifts cards to the left if one is killed to its left
	    		    	    //shiftCardsLeft();
	    	    			//method for if first card is destroyed
	    	    			if(pos == 1) 
	    	    	    	    shiftCardsLeftToOne();
	    			   }
	    	    	}
	    	    	}
	    	    }
	    	} else {
	    		for(int j = 0; j < enPlay.length; j++) {
	    		   for(int i = 0 ; i < destroyed.size(); i++) {
		    	    	if(enPlay[j] != null) {
		    			   if(enPlay[j].equals(destroyed.get(i))) {
		    	    			//System.out.println("Removing " + enPlay[j]); 
		    				    enPlay[j] = null;
		    	    			destroyed.remove(i); //remove the destroyed card from destroyed...
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
}