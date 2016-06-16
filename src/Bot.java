import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

public class Bot {
	int width;
	int height;
	int c[] = new int[11];
	int handHeight, playHeight, enPlayHeight;
	int heroP[] = new int[2];
	int p[] = new int[8];
	int enP[] = new int[8];
	int hero, enHero;
	int lastX, lastY;
	Robot r;

	
	public static void main(String[] args) throws AWTException, InterruptedException, FileNotFoundException, IOException {
		Parser p = new Parser();
		p.parse();
		Bot m = new Bot(p);
		
		//System.out.print(m.c[4]);
		System.out.println(m.enPlayHeight);
		//1080 360 852 740
		//m.moveNaturally(1080, 360, 852, 740);
		
//		for(int z = 0; z < 8; z ++) {
//			System.out.println(m.enP[4]);
//		}
		
		//m.sineWave(100, 100);
		//m.circle(400, 400, 300);
		//m.randomness(0, 0);
		//m.playCard(m.c, 7, m.handHeight);
		//m.endTurn();
		//m.endTurn();
		//m.spellToEnemy(m.c[3], m.enP[1], m.enPlayHeight);
		
//		m.endTurn();		
//		for(int i=1;i<8; i++) {
//		System.out.println(ca.enPlay[i]);	
//		}

	//m.move2(m.c, 1, m.handHeight);
	
	//m.move(m.enP, 1, m.enPlayHeight);
	//m.move2(m.p, 1, m.playHeight);
	//m.move(m.heroP, 0, m.heroP[1]);

		
//	for(int i=1;i< m.numElems(p.hand) + 1; i++) {
//		m.move2(m.c, i, m.handHeight);
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
	//m.playCard(m.c, 3, m.handHeight);
	//m.playCard(m.c, 5, m.handHeight);

	//m.attack(1,1,m.enPlayHeight);
	//m.endTurn();
	}

	
	public Bot(Parser ca) throws IOException, InterruptedException, AWTException {		
		//load game data
		
		r = new Robot();

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
		
		lastX = 0; //width/2;
		lastY = 0; //height/2;
	}

	
	public void heroPower() throws AWTException, InterruptedException {
		Thread.sleep(2500);
		r.mouseMove(heroP[0], heroP[1]);
		Thread.sleep(200);
		r.mousePress(InputEvent.BUTTON1_MASK);
		Thread.sleep(200);
		r.mouseRelease(InputEvent.BUTTON1_MASK);
	}
	
	/**parameters are the target array, the target array index, and the target height*/
	public void heroPowerTarget(int i[], int j, int k) throws AWTException, InterruptedException{
		Thread.sleep(2500);
		r.mouseMove(heroP[0], heroP[1]);
		Thread.sleep(200);
		r.mouseMove(i[j], k);
		Thread.sleep(200);
		r.mousePress(InputEvent.BUTTON1_MASK);
		Thread.sleep(200);
		r.mouseRelease(InputEvent.BUTTON1_MASK);
	}

	/**parameters are the target array, the target array index, and the target height*/
	public void heroPowerFace() throws AWTException, InterruptedException{
		Thread.sleep(2500);
		r.mouseMove(heroP[0], heroP[1]);

		Thread.sleep(200);
		r.mousePress(InputEvent.BUTTON1_MASK);
		Thread.sleep(200);

		moveNaturally(heroP[0], heroP[1], width/2, enHero);

		Thread.sleep(200);
		r.mouseRelease(InputEvent.BUTTON1_MASK);

		lastX = width/2;
		lastY = enHero;
	}
	
	public void endTurn() throws InterruptedException, AWTException{
		Thread.sleep(2500);
		System.out.println("ending turn. Last X is " + lastX + " lastY is " + lastY);
		
		Thread.sleep(200);
		moveNaturally(lastX, lastY, width * 27/32, height/2 - height * 40/800);
		Thread.sleep(200);
		
		Thread.sleep(200);
		r.mousePress(InputEvent.BUTTON1_MASK);
		Thread.sleep(200);
		r.mouseRelease(InputEvent.BUTTON1_MASK);
		
		lastX = width * 27/32;
		lastY = height/2 - height * 40/800;
	}
	
	public void attack(int playPos, int enPos, int height) throws AWTException, InterruptedException{
		Thread.sleep(2500);
		System.out.println("attacking");

		Thread.sleep(200);
		moveNaturally(lastX, lastY, p[playPos], playHeight);
		Thread.sleep(200);
		
		r.mousePress(InputEvent.BUTTON1_MASK);
		
		Thread.sleep(200);
		moveNaturally(p[playPos], playHeight, enP[enPos], height);
		Thread.sleep(200);
		
		r.mouseRelease(InputEvent.BUTTON1_MASK);
		
		lastX = enPos;
		lastY = height;
	}
	
	public void attackFace(int myCardIndex) throws InterruptedException {
		Thread.sleep(2500);
		System.out.println("attacking face");

		moveNaturally(lastX, lastY, p[myCardIndex], playHeight);
		Thread.sleep(200);
		
		r.mousePress(InputEvent.BUTTON1_MASK);
		
		Thread.sleep(200);
		moveNaturally(p[myCardIndex], playHeight, width/2, enHero);
		Thread.sleep(200);
		
		r.mouseRelease(InputEvent.BUTTON1_MASK);
		
		lastX = width/2;
		lastY = enHero;
	}
	
	public void spellToFace(int myCardIndex) throws InterruptedException {
		Thread.sleep(2500);
		System.out.println("attacking face");

		moveNaturally(lastX, lastY, c[myCardIndex], handHeight);
		Thread.sleep(200);
		
		r.mousePress(InputEvent.BUTTON1_MASK);
		
		Thread.sleep(200);
		moveNaturally(c[myCardIndex], handHeight, width/2, enHero);
		Thread.sleep(200);
		
		r.mouseRelease(InputEvent.BUTTON1_MASK);
		
		lastX = width/2;
		lastY = enHero;
	}
	
	/**parameters are the # of the spell hand position, the target width position, and the target height*/
	public void spellToEnemy(int spellXPos, int enXPos, int height) throws AWTException, InterruptedException{
		Thread.sleep(2500);
		System.out.println("playing spell");

		Thread.sleep(200);
		moveNaturally(lastX, lastY, spellXPos, handHeight);
		Thread.sleep(200);
		
		r.mousePress(InputEvent.BUTTON1_MASK);
		
		Thread.sleep(200);
		moveNaturally(spellXPos, handHeight, enXPos, height);
		Thread.sleep(200);
		
		r.mouseRelease(InputEvent.BUTTON1_MASK);
		
		lastX = enXPos;
		lastY = height;
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
		//clear the array
		for(int i = 0; i< 11; i++) {
			c[i] = 0;
		}
		int startInc = 8;	
		
		if(j  == 2)
			startInc = 0;
		if(j  > 3)
			startInc = 24;
		if(j >= 7) {
			startInc = 18;
		}
		if(j > 8) {
			startInc = 17;
		}

		int firstC = (width * 540/1280) - ((width * startInc/1280) * (j-1));
		
		if(j  == 1)
			firstC = (width * 600/1280);
		
		int inc = ( ((width * 100/1280) ) - ((j-2) * (width * 8/1280)) ); 
		for(int i = 1; i < 10; i++) {
				c[i] += firstC;
				firstC += inc;
		}
	}
	
	/**Gives correct position based on cards in play*/
	public void computeEnPlay(int j) {
		//clear the array
		for(int i = 0; i< 8; i++) {
			enP[i] = 0;
		}
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
		
		//clear the array
		for(int i = 0; i< 8; i++) {
			p[i] = 0;
		}
		
		int firstP = (width / 2);
		int firstPos = firstP - ((j -1) * (width * 50/1280));
		
		for(int i = 1; i < 8; i++) {
			p[i] = firstPos;
			firstPos += width * 100/1280;
		}
	}
	
		public void playCard(int i[], int j, int k) throws AWTException, InterruptedException{
			Thread.sleep(2500);
			
			Thread.sleep(200);
			moveNaturally(lastX, lastY, i[j], k);
			Thread.sleep(200);
			
			Thread.sleep(200);
			r.mousePress(InputEvent.BUTTON1_MASK);
			Thread.sleep(200);
			
			moveNaturally(i[j], k, i[j], k - 300);
			Thread.sleep(200);
			
			r.mouseRelease(InputEvent.BUTTON1_MASK);
			
			lastX = i[j];
			lastY = k - 300;
		}
		
		public void move2(int[] a, int i, int j) throws AWTException, InterruptedException{
			Thread.sleep(2500);
			r.mouseMove(a[i], j);
		}
		
		public void move(int i, int j) throws AWTException, InterruptedException{
			Thread.sleep(2500);
			r.mouseMove(i, j);
		}
		
		public void moveNaturally(int beginX, int beginY, int endX, int endY) throws InterruptedException{
			
			double slope =  1;
			
			System.out.println(beginX + " " + beginY + " " + endX + " " + endY);

			while(beginY != endY || beginX != endX) {

				Thread.sleep(5);
				
				//if there is no x movement then the slope is 1 or -1
				if(endX - beginX == 0) {
					slope = 1;
				}
				//recalc slope to account for drift since we're rounding for pixels
				else {
					slope =  (double)(endY - beginY) /  (double)(endX - beginX);
				}
								
				//check to see the slope direction
				if(beginY != endY && beginX > endX || beginY > endY && beginX == endX) {
					slope *= -1;
				}
				
				//System.out.println("SLOPE" + slope + " " + beginX + " " + beginY);

				r.mouseMove(beginX, beginY);
				
				//check x direction
				if(beginX - endX != 0) {
					if(beginX < endX)
						beginX++;
					else
						beginX--;
				}
				
				beginY = beginY + (int)slope;
				
			}
			
			lastX = beginX;
			lastY= beginY;
		}
		
		public void sineWave(int x, int y) throws InterruptedException{
			
			r.mouseMove(x, y);
			
			for(int i = 0; i < 180; i++) {
				Thread.sleep(5);
				double d = Math.sin(Math.toRadians(i));
				d = d * 100;
				System.out.println(d);
				x++;
				r.mouseMove(x, (int)d + 100);
				lastX = x;
				lastY = (int) d + 100;
			}
		}
		
		public void circle(int x, int y, int radius) throws InterruptedException{
						
			int initialX = x;
			//reset the x coord to the outside of the circle
			x = radius;
						
			r.mouseMove(x, y);
			
			for(int i = 0; i < radius * 2; i++) {
				
				//get y val
				double yVal = Math.sqrt(Math.pow(radius, 2) - Math.pow(x, 2));
				System.out.print(yVal);

				Thread.sleep(5);
				r.mouseMove(initialX + x, y + (int)yVal);
				
				//subtract x for 2*r
				x--;
				
				lastX = x;
				lastY = (int) yVal;
			}
		}
		
		public void randomness(int beginX, int beginY) throws InterruptedException {
			
			long timer = System.currentTimeMillis();
			long timeNow = System.currentTimeMillis();
			
			while((timeNow - timer) < 5000) {
				
				System.out.println(timeNow - timer);

				Random r = new Random();
				int rX = r.nextInt(800);
				int rY = r.nextInt(1200);
				int straightOrCurve = r.nextInt(2);
				
				if(straightOrCurve == 1) {
					moveNaturally(beginX, beginY, rX, rY);
					beginX = rX;
					beginY = rY;
				} else {
					sineWave(lastX, lastY);
				}
				timeNow = System.currentTimeMillis();
			}
		}
}
