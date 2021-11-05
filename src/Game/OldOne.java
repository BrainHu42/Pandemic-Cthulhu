package Game;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class OldOne extends Card
{
	private boolean isActive;
	
	public void setActive(boolean bool) {isActive = bool;}
	public boolean getIsActive() {return isActive;}
	
	public OldOne(BufferedImage img, String name) 
	{
		super(img, name);
		isActive=false;
	}
	
	public OldOne() {
		populateDeck();
	}
	
	public void populateDeck()
	{
		Game b = new Game();
		ArrayList<BufferedImage> temp = b.sendImagesToOldOneDeck();
		
		deck.add(new OldOne(temp.get(7), "shudde mell"));
		deck.add(new OldOne(temp.get(0), "dagon"));
		deck.add(new OldOne(temp.get(1), "azathoth"));
		deck.add(new OldOne(temp.get(2), "nyarlathotep"));
		deck.add(new OldOne(temp.get(3), "yog-sothoth"));
		deck.add(new OldOne(temp.get(4), "yig"));
		deck.add(new OldOne(temp.get(5), "hastur"));
		deck.add(new OldOne(temp.get(6), "ithaqua"));
		deck.add(new OldOne(temp.get(8), "atlach-nacha"));
		deck.add(new OldOne(temp.get(9), "tsathoggua"));
		deck.add(new OldOne(temp.get(10), "shub-niggurath"));
	}

}
