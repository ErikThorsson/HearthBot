
public class Card {
	public int atk;
	public int hp;
	public String id;
	public int inPlay;
	
	public Card() {
		atk = 0;
		hp = 0;
		id = "";
		inPlay = 0;
	}
	public Card(int a, int h, String i, int p){
		atk = a;
		hp = h; 
		id = i;
		inPlay = p;
	}
	
	public int getHP() {
		return hp;
	}
}
