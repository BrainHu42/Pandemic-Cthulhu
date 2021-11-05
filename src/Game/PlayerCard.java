package Game;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class PlayerCard extends Card
{ 
	private boolean isSelected;
	
	public PlayerCard(BufferedImage img, String name)
	{
		super(img, name);
		isSelected = false;
	}
	
	public PlayerCard()
	{
		populateDeck();
	}
	
	public void setSelected(boolean val){isSelected = val;}
	public boolean getIsSelected(){return isSelected;}
	
	private void populateDeck() {
		Game b = new Game();
		ArrayList<BufferedImage> temp = b.sendImagesToPlayerDeck();
		
		for(int i=0; i<11; i++)
			deck.add(new PlayerCard(temp.get(0), "green"));
		for(int i=0; i<11; i++)
			deck.add(new PlayerCard(temp.get(1), "yellow"));
		for(int i=0; i<11; i++)
			deck.add(new PlayerCard(temp.get(2), "purple"));
		for(int i=0; i<11; i++)
			deck.add(new PlayerCard(temp.get(3), "red"));
	}
}

