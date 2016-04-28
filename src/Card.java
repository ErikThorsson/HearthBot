
public class Card {
	public int atk, hp, cost, inPlay, EntityID;
	public String id, name;
	
	public Card() {
		atk = -1;
		hp = -1;
		id = "";
		EntityID = -1;
		inPlay = 0;
		cost = 0;
	}
	public Card(int attack, int health, String card_id, int entity_ID, int playNum, int itsCost, String _name){
		atk = attack;
		hp = health; 
		id = card_id;
		EntityID = entity_ID;
		inPlay = playNum;
		name = _name;
		cost = itsCost;
	}
	
	public int getHP() {
		return hp;
	}
}
