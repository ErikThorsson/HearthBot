
public class Card {
	public int atk;
	public int hp;
	public String id;
	
	public Card() {
		atk = 0;
		hp = 0;
		id = "";
	}
	public Card(int a, int h, String i){
		atk = a;
		hp = h; 
		id = i;
	}
	
	public int getHP() {
		return hp;
	}
}
