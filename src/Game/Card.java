package Game;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Card 
{
	protected ArrayList<Card> deck = new ArrayList<Card>();
	private String myName;
	private BufferedImage myImage;
	
	public Card(BufferedImage img, String name) {
		myImage = img;
		myName = name;
	}
	
	public Card(){}
	
	public BufferedImage getImage() {return myImage;}
	public String getName() {return myName;}
	public ArrayList<Card> getDeck() {return deck;}
}
