
public class Card {
	public int atk, hp, cost, inPlay, EntityID, charge, spell, globalSpell, attacked, heroP;
	public String id, name;
	
	public Card() {
		name = "";
		atk = -1;
		hp = -1;
		id = "";
		EntityID = -1;
		inPlay = 0;
		cost = -1;
		charge = -1;
		spell = -1;
		globalSpell = -1;
		attacked = -1;
		heroP = -1;
	}
	public Card(int attack, int health, String card_id, int entity_ID, int playNum, int itsCost, String _name, int isSpell, int global_spell, int atkd, int heroP){
		atk = attack;
		hp = health; 
		id = card_id;
		EntityID = entity_ID;
		inPlay = playNum;
		name = _name;
		cost = itsCost;
		spell = isSpell;
		globalSpell = global_spell;
		attacked = atkd;
		this.heroP = heroP;
	}

	//for making DBCards to Cards
	public Card(String name,  int spell, int atk, int cost){
		this.atk = atk;
		hp = -1;
		id = "";
		EntityID = -1;
		inPlay = -1;
		this.name = name;
		this.cost = cost;
		this.spell = spell;
		globalSpell = -1;
		attacked = -1;
		heroP = -1;
	}
}
