
public class Card {
	public int atk, hp, cost, inPlay;
	public String id;
	
	public Card() {
		atk = 0;
		hp = 0;
		id = "";
		inPlay = 0;
		cost = 0;
	}
	public Card(int a, int h, String i, int p, int c){
		atk = a;
		hp = h; 
		id = i;
		inPlay = p;
		cost = c;
	}
	
	public int getHP() {
		return hp;
	}
}
