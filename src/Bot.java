import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Bot {
	int width;
	int height;
	int c[] = new int[10];
	int handHeight, playHeight, enPlayHeight;
	int heroP[] = new int[2];
	int p[] = new int[8];
	int enP[] = new int[8];
	int hero, enHero;

	
	public static void main(String[] args) throws AWTException, InterruptedException, FileNotFoundException, IOException {
		//Bot m = new Bot();
//		m.endTurn();
		
//		for(int i=1;i<8; i++) {
//		System.out.println(ca.enPlay[i]);	
//		}

	//m.move(m.c, 1, m.handHeight);
	//m.move(m.enP, 1, m.enPlayHeight);
	//m.move(m.p, 3, m.playHeight);
	//m.move(m.heroP, 0, m.heroP[1]);


//	for(int i=1;i< m.numElems(ca.hand) + 1; i++) {
//		m.move(m.c, i, m.handHeight);
//	}
	
//	for(int i=1;i<3; i++) {
//	m.move(m.enP, i, m.enPlayHeight);
//}
	
//	for(int i=1;i<8; i++) {
//		System.out.println(m.enP[i]);	
//		}
	
	//System.out.println(m.numElems(ca.hand));
				
	//m.move2(m.enP, 1,  m.enPlayHeight);
	
	//m.move(m.width/2,  m.height * 5/7);
	
	//m.spellToEnemy(m.c[7], m.enP[1]);
	
	//m.heroPower();
	m.playCard(m.c, 3, m.handHeight);
	//m.playCard(m.c, 5, m.handHeight);

	//m.attack(1,1,m.enPlayHeight);
	//m.endTurn();
	}
	
	public Bot(Parser ca) throws IOException, InterruptedException {		
		//load game data
		
		//get screen dimensions
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = (int) screenSize.getWidth();
		height = (int)screenSize.getHeight();
				
		//set hero portrait heights
		enHero = height * 1/7;
		hero = height * 5/7;
		
		//initialize positions of cards in hand
		handHeight = height * 74/80;
		computeHand(numElems(ca.hand));
	
		//init positions of play card positions
		playHeight = handHeight - height * 3/8;
		
		//init enemy play
		enPlayHeight = handHeight - height * 450/800;

		
		//init hero power
		heroP[0] = (width * 17/48) + width * 320/1280;
		heroP[1] = height - (height * 300/1280);
		
		//sets your board positions
		computePlay(numElems(ca.myPlay)); //sets positioning based on # of cards in play
		computeEnPlay(numElems(ca.enPlay)); //sets positioning based on # of cards in play
	}

	
	public void heroPower() throws AWTException, InterruptedException {
		Robot r = new Robot();
		Thread.sleep(2500);
		r.mouseMove(heroP[0], heroP[1]);
		Thread.sleep(200);
		r.mousePress(InputEvent.BUTTON1_MASK);
		Thread.sleep(200);
		r.mouseRelease(InputEvent.BUTTON1_MASK);
	}
	
	/**parameters are the target array, the target array index, and the target height*/
	public void heroPowerTarget(int i[], int j, int k) throws AWTException, InterruptedException{
		Robot r = new Robot();
		Thread.sleep(2500);
		r.mouseMove(heroP[0], heroP[1]);
		Thread.sleep(200);
		r.mouseMove(i[j], k);
		Thread.sleep(200);
		r.mousePress(InputEvent.BUTTON1_MASK);
		Thread.sleep(200);
		r.mouseRelease(InputEvent.BUTTON1_MASK);
	}
	
	public void endTurn() throws InterruptedException, AWTException{
		Robot r = new Robot();
		Thread.sleep(2500);
		r.mouseMove(width * 27/32, height/2 - height * 40/800);
		Thread.sleep(200);
		r.mousePress(InputEvent.BUTTON1_MASK);
		Thread.sleep(200);
		r.mouseRelease(InputEvent.BUTTON1_MASK);
	}
	
	public void attack(int playPos, int enPos, int height) throws AWTException, InterruptedException{
		Robot r = new Robot();
		Thread.sleep(2500);
		r.mouseMove(p[playPos], playHeight);
		Thread.sleep(200);
		r.mousePress(InputEvent.BUTTON1_MASK);
		Thread.sleep(200);
		r.mouseMove(enP[enPos] , height);
		Thread.sleep(200);
		r.mouseRelease(InputEvent.BUTTON1_MASK);
	}
	
	/**parameters are the # of the spell hand position, the target width position, and the target height*/
	public void spellToEnemy(int spellHandPos, int enPos, int height) throws AWTException, InterruptedException{
		Robot r = new Robot();
		Thread.sleep(2500);
		r.mouseMove(spellHandPos, handHeight);
		Thread.sleep(200);
		r.mousePress(InputEvent.BUTTON1_MASK);
		Thread.sleep(200);
		r.mouseMove(enPos , height);
		Thread.sleep(200);
		r.mouseRelease(InputEvent.BUTTON1_MASK);
	}
	
	public int numElems(String[] s) {
		int n = 0;
		for(int i = 0; i<s.length; i++) {
			if(s[i] != null)
				n++;
		}
		return n;
	}
	

	/**Gives correct position based on cards in hand. They shift by a small amount depending on the #*/
	public void computeHand(int j) {
		//System.out.println(j);
		int startInc = 8;
		if(j  > 3)
			startInc = 24;
		if(j >= 7) {
			startInc = 18;
		}
		if(j > 8) {
			startInc = 17;
		}
		int firstC = (width * 540/1280) - ((width * startInc/1280) * (j-1));
		int inc = ( ((width * 100/1280) ) - ((j-2) * (width * 8/1280)) ); 
		//System.out.println(inc);
		for(int i = 1; i < 10; i++) {
				c[i] += firstC;
				firstC += inc;
		}
	}
	
	/**Gives correct position based on cards in play*/
	public void computeEnPlay(int j) {
		//System.out.println(j);
		int firstP = (width/2);
		int firstPos = firstP - ((j -1) * (width * 50/1280));
		
		for(int i = 1; i < 8; i++) {
			enP[i] = firstPos;
			firstPos += (width * 100/1280);
		}
	}
	
	/**Gives correct position based on cards in play*/
	public void computePlay(int j) {
		int firstP = (width / 2);
		int firstPos = firstP - ((j -1) * (width * 50/1280));
		
		for(int i = 1; i < 8; i++) {
			p[i] = firstPos;
			firstPos += width * 100/1280;
		}
	}
	
		public void playCard(int i[], int j, int k) throws AWTException, InterruptedException{
			Robot r = new Robot();
			Thread.sleep(2500);
			System.out.println(i[j] + " " + j);
			r.mouseMove(i[j], k);
			Thread.sleep(200);
			r.mousePress(InputEvent.BUTTON1_MASK);
			Thread.sleep(200);
			r.mouseMove(i[j] , handHeight - 300);
			Thread.sleep(200);
			r.mouseRelease(InputEvent.BUTTON1_MASK);
		}
		
		public void move2(int[] a, int i, int j) throws AWTException, InterruptedException{
			Robot r = new Robot();
			Thread.sleep(2500);
			r.mouseMove(a[i], j);
		}
		
		public void move(int i, int j) throws AWTException, InterruptedException{
			Robot r = new Robot();
			Thread.sleep(2500);
			r.mouseMove(i, j);
		}
}
