
public class Card {
	public int atk, hp, cost, inPlay;
	public String id, name;
	
	public Card() {
		atk = -1;
		hp = -1;
		id = "";
		inPlay = 0;
		cost = 0;
	}
	public Card(int a, int h, String i, int p, int c, String n){
		atk = a;
		hp = h; 
		id = i;
		inPlay = p;
		name = n;
		cost = c;
	}
	
	public int getHP() {
		return hp;
	}
}
