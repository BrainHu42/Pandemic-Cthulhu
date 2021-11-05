package Game;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class CultistCard extends Card
{
	private boolean shogMove;
	
	public CultistCard(BufferedImage img, String name)
	{
		super(img, name);
		shogMove = false;
	}

	public CultistCard(BufferedImage img, String name, boolean sMove)
	{
		super(img, name);
		shogMove = sMove;
	}

	public CultistCard()
	{
		populateDeck();
	}
	
	public boolean getShogMove(){return shogMove;}
	
	private void populateDeck()
	{
		Game b = new Game();
		ArrayList<BufferedImage> temp = b.sendImagesToCultistDeck();
		
		deck.add(new CultistCard(temp.get(0), "trainStation"));
		deck.add(new CultistCard(temp.get(1), "university", true));
		deck.add(new CultistCard(temp.get(2), "policeStation"));
		deck.add(new CultistCard(temp.get(3), "secretLodge", true));
		deck.add(new CultistCard(temp.get(4), "diner"));
		deck.add(new CultistCard(temp.get(5), "park"));
		
		deck.add(new CultistCard(temp.get(6), "cafe"));
		deck.add(new CultistCard(temp.get(7), "church"));
		deck.add(new CultistCard(temp.get(8), "historicInn", true));
		deck.add(new CultistCard(temp.get(9), "farmstead"));
		deck.add(new CultistCard(temp.get(10), "swamp", true));
		deck.add(new CultistCard(temp.get(11), "oldMill"));
		
		deck.add(new CultistCard(temp.get(12), "junkyard"));
		deck.add(new CultistCard(temp.get(13), "pawnShop", true));
		deck.add(new CultistCard(temp.get(14), "factory"));
		deck.add(new CultistCard(temp.get(15), "boardwalk"));
		deck.add(new CultistCard(temp.get(16), "docks"));
		deck.add(new CultistCard(temp.get(17), "hospital", true));
		
		deck.add(new CultistCard(temp.get(18), "woods", true));
		deck.add(new CultistCard(temp.get(19), "greatHall", true));
		deck.add(new CultistCard(temp.get(20), "market"));
		deck.add(new CultistCard(temp.get(21), "theater"));
		deck.add(new CultistCard(temp.get(22), "wharf"));
		deck.add(new CultistCard(temp.get(23), "graveyard"));
	}
}
