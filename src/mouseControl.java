import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.io.FileNotFoundException;
import java.io.IOException;

public class mouseControl {
	int width;
	int height;
	int c[] = new int[10];
	int handHeight, playHeight, enPlayHeight;
	int heroP[] = new int[2];
	int p[] = new int[8];
	int enP[] = new int[8];

	
	public static void main(String[] args) throws AWTException, InterruptedException, FileNotFoundException, IOException {
		mouseControl m = new mouseControl();
		
		//load game data
		card_hand_pos ca = new card_hand_pos();
		ca.parse();
		
		//get screen dimensions
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		m.width = (int) screenSize.getWidth();
		m.height = (int)screenSize.getHeight();
				
		//initialize positions of cards in hand
		m.handHeight = m.height * 74/80;
		m.computeHand(m.numElems(ca.hand));
	
		//init positions of play card positions
		m.playHeight = m.handHeight - m.height * 3/8;
		
		//init enemy play
		m.enPlayHeight = m.handHeight - m.height * 450/800;

		
		//init hero power
		m.heroP[0] = (m.width * 17/48) + m.width * 300/1280;
		m.heroP[1] = m.height - (m.height * 250/1280);
		
		//sets your board positions
		m.computePlay(m.numElems(ca.myPlay)); //sets positioning based on # of cards in play
		m.computeEnPlay(m.numElems(ca.enPlay)); //sets positioning based on # of cards in play

//		for(int i=1;i<8; i++) {
//			System.out.println(ca.enPlay[i]);	
//			}
	
		//m.move(m.c, 1, m.handHeight);
		m.move(m.enP, 1, m.enPlayHeight);
		//m.move(m.p, 3, m.playHeight);


//		for(int i=1;i<7; i++) {
//			m.move(m.c, i, m.handHeight);
//		}
		
//		for(int i=1;i<3; i++) {
//		m.move(m.enP, i, m.enPlayHeight);
//	}
		
//		for(int i=1;i<8; i++) {
//			System.out.println(m.enP[i]);	
//			}
		
		//System.out.println(m.numElems(ca.hand));
		//m.playCard(m.c, 2, m.handHeight);
		
		//m.move(m.enP, 1,  m.enPlayHeight);
		//m.move(m.c, 3,  m.handHeight);
		//m.spellToEnemy(m.c[1], m.enP[1]);
		
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
		int firstC = (width * 540/1280) - ((width * startInc/1280) * (j-1));
		int inc = ( ((width * 100/1280) ) - ((j-2) * (width * 7/1280)) ); 
		//System.out.println(inc);
		for(int i = 1; i < 10; i++) {
				c[i] += firstC;
				firstC += inc;
		}
	}
	
	/**Gives correct position based on cards in play*/
	public void computeEnPlay(int j) {
		System.out.println(j);
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
			Thread.sleep(1500);
			r.mouseMove(i[j], k);
			Thread.sleep(200);
			r.mousePress(InputEvent.BUTTON1_MASK);
			Thread.sleep(200);
			r.mouseMove(i[j] , handHeight - 300);
			Thread.sleep(200);
			r.mouseRelease(InputEvent.BUTTON1_MASK);
		}
		
		public void move(int i[], int j, int k) throws AWTException, InterruptedException{
			Robot r = new Robot();
			Thread.sleep(2500);
			r.mouseMove(i[j], k);
		}
		
		public void spellToEnemy(int spellHandPos, int enPos) throws AWTException, InterruptedException{
			Robot r = new Robot();
			Thread.sleep(1500);
			r.mouseMove(spellHandPos, handHeight);
			Thread.sleep(200);
			r.mousePress(InputEvent.BUTTON1_MASK);
			Thread.sleep(200);
			r.mouseMove(enPos , enPlayHeight);
			Thread.sleep(200);
			r.mouseRelease(InputEvent.BUTTON1_MASK);
		}
}
