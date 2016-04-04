import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;

public class mouseControl {
	int width;
	int height;
	int c[] = new int[10];
	int handHeight, playHeight;
	int heroP[] = new int[2];
	int p[] = new int[8];
	
	public static void main(String[] args) throws AWTException, InterruptedException {
		mouseControl m = new mouseControl();
		
		//get screen dimensions
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		m.width = (int) screenSize.getWidth();
		m.height = (int)screenSize.getHeight();
		
		//initialize positions of cards in hand
		int firstC = (m.width * 17/48); 
		m.handHeight = m.height * 9/10;
		int inc = 70;
		for(int i = 1; i < 10; i++) {
			m.c[i] = firstC;
			firstC += inc;
		}
	
		//init positions of play card positions
		m.playHeight = m.handHeight - 300;
		
		//init hero power
		m.heroP[0] = (m.width * 17/48) + 70 * 5 - 50;
		m.heroP[1] = m.height - 250;
		
		//sets your baord positions
		card_hand_pos ca = new card_hand_pos();
		m.computePlay(m.numElems(ca.myPlay));
		
//		for(int i=1;i<8; i++) {
//			m.playCard(m.p, i, m.playHeight);
//		}
		
		m.playCard(m.c, 3, m.handHeight);
		//m.playCard(m.heroP, 0, m.heroP[1]);
		
	}
	
	public int numElems(String[] s) {
		int n = 0;
		for(int i = 0; i<s.length; i++) {
			if(s[i] != null)
				n++;
		}
		return n;
	}
	
	/**Gives correct position based on cards in play*/
	public void computePlay(int j) {
		int firstP = (width * 17/48) + 70 * 3 + 30;
		int firstPos = firstP - j * 50;
		
		for(int i = 1; i < 8; i++) {
			p[i] = firstPos;
			//System.out.println(p[i]);
			firstPos += 100;
		}
	}
	
		public void playCard(int i[], int j, int k) throws AWTException, InterruptedException{
			Robot r = new Robot();
			Thread.sleep(1500);
			r.mouseMove(i[j], k);
			Thread.sleep(100);
			r.mousePress(InputEvent.BUTTON1_MASK);
			Thread.sleep(100);
			r.mouseMove(i[j] , handHeight - 300);
			Thread.sleep(100);
			r.mouseRelease(InputEvent.BUTTON1_MASK);
		}
}
