import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class card_hand_pos {
	String[] hand = new String[11];
	String[] myPlay = new String[8];
	String[] enPlay = new String[8];
    Stack myBoardState = new Stack(); //stack on cards from my play
    Stack enBoardState = new Stack(); //stack on cards from my play
    String previousFirstPlay = "";
    String previousFirstEnPlay ="";
	
    public card_hand_pos() {
    	String[] myHand = hand;
    	String[] play = myPlay;
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
		String[] cmd = new String[]{"/bin/sh", "/Users/erikorndahl/getLog.sh"};
		Process pr = Runtime.getRuntime().exec(cmd);
		
		card_hand_pos c = new card_hand_pos();
	
		c.getHand();
		c.checkSpellUse();
		c.printHand();

		System.out.print("---------------------\n");
				
		c.getCardsInPlay(1);
		c.printMyPlay();
		
		System.out.print("---------------------\n");
		
		c.getCardsInPlay(2);
		c.printEnPlay();
		
		//c.printLines();
	}
	
	public void printLines() throws IOException {
		try(BufferedReader br = new BufferedReader(new FileReader("/Users/erikorndahl/Desktop/log.txt"))) {
		    String line = br.readLine();
		    while (line != null) {
		    	if (line.contains("name=Sir") && line.contains("zone=HAND") && line.contains("pos from") || 
		    			line.contains("name=Sir") && line.contains("zone=HAND") && line.contains("TRANSITIONING")) {  
		    	//System.out.println(line);
		    }
		        line = br.readLine();
		}
		}
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
	
	public void printEnPlay() {
		
		for(int i = 0 ; i < 8 ; i++) {
			System.out.println(enPlay[i] + " is in position " + i);
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
				if(myPlay[i] == null && i != 0 && myPlay[i + 1] != null) {
					myPlay[i] = myPlay[i+1];
					myPlay[i+1] = null;
				}
			}
		}
		
		for(int i=0; i < enPlay.length; i++) {
			if(i!= enPlay.length - 1) {
				if(enPlay[i] == null && i != 0 && enPlay[i + 1] != null) {
					enPlay[i] = enPlay[i+1];
					enPlay[i+1] = null;
				}
			}
		}
	}
	
	/**shifts cards if one is shifted into position 1**/
	public void shiftCardsLeftToOne(){
				if(previousFirstPlay != myPlay[1] && myPlay[2] != myPlay[1]) {
					myPlay[2] = null;
				}
		
				if(previousFirstEnPlay != enPlay[1] && enPlay[2] != enPlay[1]) {
					enPlay[2] = null;
				}
	}
	
	public void checkSpellUse() throws FileNotFoundException, IOException {
		try(BufferedReader br = new BufferedReader(new FileReader("/Users/erikorndahl/Desktop/log.txt"))) {
		    String line = br.readLine();
		    boolean found = true;
		    while (line != null) {
		    	//only scan new material unless this is the first scan
		    	if(line.contains(lastLine) || first == true)
		  	    	found = true;
		    	if(found == true) {
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
		    	
		        lastLine = line;
		    	}
		        line = br.readLine();
		    }
		    first = false;
		}
	}
	/**
	 * Puts new cards into the hand array
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void getHand() throws FileNotFoundException, IOException {
	try(BufferedReader br = new BufferedReader(new FileReader("/Users/erikorndahl/Desktop/log.txt"))) {
	    String line = br.readLine();
	    boolean found = true;
	    while (line != null) {
	    	//only scan new material unless this is the first scan
	    	if(line.contains(lastLine) || first == true)
	  	    	found = true;
	    	if(found == true) {
	    	if (line.contains("[name=") && line.contains("zone=HAND") && line.contains("pos from") && !lastLine.contains("FRIENDLY PLAY") 
	    			//)
	    			|| line.contains("name=") && line.contains("zone=HAND") && line.contains("TRANSITIONING") && !lastLine.contains("FRIENDLY PLAY"))
	    			{ 
	    		
	    		//if(line.contains("name=Sir"))
	    				//System.out.println(line);
	    		
	    		String[] s2 = new String[2];
	    		String[] nameSplit = new String[2];
	    		String[] pos = new String[2];
	    		nameSplit = line.split("name=");
	    		s2 = nameSplit[1].split("id");
	    		
	    		if( line.contains("pos from")) {
	    		pos = line.split("-> ");
	    		//System.out.println(s2[0].trim() + " " + pos[1]); 
	    		hand[Integer.parseInt(pos[1])] = s2[0].trim(); //global hand array is updated with new cards and card positions
	    		}
	    		
	    		//now remove old card positions if they change or are removed from hand...
	    		if(line.contains("to FRIENDLY PLAY")) { //then the card is moving into play so remove from hand pos
	    			for(int i = 0; i< hand.length; i++) {
	    			if(hand[i] != null) {
	    			if(hand[i].equals(s2[0].trim())) {
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
	    			if(hand[i].equals(s2[0].trim()) && i == Integer.parseInt(pos[1]) + 1 ) {
	    				hand[i] = null;
	    			}
	    			}
	    		}
	    	}
	    			}
	    	
	        lastLine = line;
	    	}
	        line = br.readLine();
	    }
	    first = false;
	}
}

	/**Need to add shifting to cards in play in one dies...Meaning null its old position**/
public void getCardsInPlay(int p) throws FileNotFoundException, IOException {
	try(BufferedReader br = new BufferedReader(new FileReader("/Users/erikorndahl/Desktop/log.txt"))) {
	    String line = br.readLine();
	    String[] boardPosition = new String[7];
	    
	    while (line != null) {
	    	
	    	if (line.contains("name=") && line.contains("player=" + p) && line.contains("zone=PLAY")
	    			&& !line.contains("Hero") && line.contains("dstPos=")
	    			&& !line.contains("dstPos=0") && !line.contains("zonePos=0") || line.contains("name=") 
	    			&& line.contains("player=" + p) && line.contains("zone=GRAVEYARD")
	    			&& !line.contains("Hero") && line.contains("dstPos=") && !line.contains("zonePos=0") 
	    			&& !line.contains("dstPos=0"))  {
    			
	    		//get the name of the card
	    		String[] nameSplit = line.split("name=");
	    		String[] finalName;
	    		ArrayList<String> destroyed = new ArrayList();
	    		
	    		//there may be more we're missing...
	    		if(line.contains("pos from") || line.contains("=powerTask=[]") || line.contains(" TAG_CHANGE") || line.contains("[Zone] ZoneChangeList.Finish()")) 
		    		finalName = nameSplit[1].split("id");
	    		else
	    			finalName = nameSplit[1].split("]");
	    		
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
	    		
	    		//System.out.println(finalName[0].trim() + " " + position2[0]);
	    		
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
	    	    		myPlay[pos] = cardName;
	    	    		previousFirstPlay = cardName;
	    	    	}
	    	    	else {
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
	    	    			System.out.println("Removing " + myPlay[j]); 
	    				    myPlay[j] = null;
	    	    			destroyed.remove(i); //remove the destroyed card from destroyed...
	    			   }
	    	    	}
	    	    	}
	    	    }
	    	} else {
	    		for(int j = 0; j < enPlay.length; j++) {
	    		   for(int i = 0 ; i < destroyed.size(); i++) {
		    	    	if(enPlay[j] != null) {
		    			   if(enPlay[j].equals(destroyed.get(i))) {
		    	    			System.out.println("Removing " + enPlay[j]); 
		    				    enPlay[j] = null;
		    	    			destroyed.remove(i); //remove the destroyed card from destroyed...

		    			   }
		    	    	}
		    	    	}
		    	    }
	    	}
	    	    //shifts cards to the left if one is killed to its left
	    	    shiftCardsLeft();
	    	    shiftCardsLeftToOne();;
	    }
	    	
	        line = br.readLine();
	    }
	}
}
}