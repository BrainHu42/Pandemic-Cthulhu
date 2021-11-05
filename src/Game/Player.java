package Game;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Player extends Card
{
	//has                      getter                  setter
	//Location 					yes						yes
	//start action number		yes						yes
	//actions remaining			yes						yes
	//isInsane					yes						yes
	//sanity					yes						yes
	//name						yes						no
	
	//other stuff that we will worry about later
	private Location location;
	private int startActionNum, actionsRemaining, sanity, shoggothContact;
	private boolean isInsane, special;
	private ArrayList<Player> playerList = new ArrayList<Player>(); //added dec21
    private ArrayList<PlayerCard> hand = new ArrayList<PlayerCard>(); //added jan11
	
	public void setLocation(Location loc){location = loc;}
	public void setStartActionNum (int num){startActionNum = num;}
	public void setActionsRemaining(int num){actionsRemaining = num;}
	public void setSanity(int num){sanity = num;}
	public void setIsInsane(boolean val){isInsane = val;}
	public void setShoggothContact(int contact) {shoggothContact=contact;}
	public void setSpecial(boolean s){special = s;}
	
	public boolean getSpecial(){return special;}
	public Location getLocation(){return location;}
	public int getStartActionNum(){return startActionNum;}
	public int getActionsRemaining(){return actionsRemaining;}
	public int getSanity(){return sanity;}
	public boolean getIsInsane(){return isInsane;}
	public int getShoggothContact() {return shoggothContact;}
	public ArrayList<PlayerCard> getHand(){return hand;}
	public ArrayList<Player> getPlayerList(){return playerList;}
	
	public BufferedImage normalFace, yellowFace, insaneFace, yellowInsaneFace, figure, selectedFigure;
	public BufferedImage getPortrait(){return normalFace;}
	public BufferedImage getYellowPortrait(){return yellowFace;}
	public BufferedImage getInsanePortrait(){return insaneFace;} 
	public BufferedImage getYellowInsanePortrait(){return yellowInsaneFace;}
	public BufferedImage getPawnImage(){return figure;} 
	public BufferedImage getSelectedPawnImage(){return selectedFigure;} 
	
	public static ArrayList<BufferedImage> playerImages;

	public Player()
	{
		populateDeck();
	}
	
	public void populateDeck() 
	{
		Game b = new Game();
		ArrayList<BufferedImage> temp = b.sendImagesToPlayers();
		
		deck.add(new Player(temp.get(0),temp.get(1),temp.get(2),temp.get(3),temp.get(4),temp.get(5),temp.get(6),"DOCTOR",5));
		deck.add(new Player(temp.get(7),temp.get(8),temp.get(9),temp.get(10),temp.get(11),temp.get(12),temp.get(13),"DETECTIVE",4));
		deck.add(new Player(temp.get(14),temp.get(15),temp.get(16),temp.get(17),temp.get(18),temp.get(19),temp.get(20),"HUNTER",4));
		deck.add(new Player(temp.get(21),temp.get(22),temp.get(23),temp.get(24),temp.get(25),temp.get(26),temp.get(27),"DRIVER",4));
		deck.add(new Player(temp.get(28),temp.get(29),temp.get(30),temp.get(31),temp.get(32),temp.get(33),temp.get(34),"REPORTER",4));
		deck.add(new Player(temp.get(35),temp.get(36),temp.get(37),temp.get(38),temp.get(39),temp.get(40),temp.get(41),"OCCULTIST",4));
		deck.add(new Player(temp.get(42),temp.get(43),temp.get(44),temp.get(45),temp.get(46),temp.get(47),temp.get(48),"MAGICIAN", 4));
	}
	
	public Player(BufferedImage reg, BufferedImage yel, BufferedImage insane, BufferedImage yelInsane, 
			BufferedImage img, BufferedImage fig, BufferedImage highFig, String name, int actions)
	{
		super(img, name);
		location = Game.locations.get(0);
		startActionNum = actions;
		actionsRemaining = actions;
		sanity = 4;
		isInsane = false;
		special = true;
		
		normalFace = reg;
		yellowFace = yel;
		insaneFace = insane;
		yellowInsaneFace = yelInsane;
		figure = fig;
		selectedFigure = highFig;
	}
}
