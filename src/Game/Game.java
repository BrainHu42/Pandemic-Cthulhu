/*
 * Digital implementation of Pandemic Cthulhu: 
 * A collaborative game were you and your friends play as different characters and try to save the world from the cultists and shoggoths who want to awaken the Old Ones.
 * The objective of the game to battle through enemies and close all the portals before Cthulhu awakens.
 * 
 * Game Manual can be found at this link:
 * https://images.zmangames.com/filer_public/a9/24/a9249cf7-1f8a-4575-a1a4-f33afc8162b3/en-pandemic-cthulhu-rule-v6.pdf  
 */

package Game;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

//Game States
/* GameState 1 = Use Actions
 * GameState 2 = Trade Player Decision
 * GameState 3 = Highlight Possible Bus Destinations
 * GameState 4 = Ticket Color Decision
 * GameState 6 = Insanity Die Roll (Branches off into 10 or 20)
 * GameState 7 = Hunter Die Roll (If Insane)
 * GameState 10 = Shoggoth Triggered Die Roll
 * GameState 11 = Wait for Continue Click
 * GameState 12 = Wait for Die Roll Click
 * GameState 13 = Shoggoth Decision Click
 * GameState 14 = Draw Card Click
 * GameState 15 = Hospital Or Church Decision Click
 * GameState 16 = Evil Stirs: Die Roll
 * GameState 17 = Evil Stirs: Old One Awakening
 * GameState 18 = Evil Stirs: Shoggoth Appears
 * GameState 19 = Evil Stirs: Cultists Regroup
 * GameState 20 = Player Triggered Die Roll
 * GameState 21 = Wait for Discard Decision
 * GameState 22 = Song of Kadath (Choose Player)
 * GameState 23 = Magician Insanity
 * GameState 29 = Relic Selected
 * GameState 30 = Relic Enlarged
 * GameState 31 = Silver Key
 * GameState 32 = Elder Sign
 * GameState 33 = Last Hourglass
 * GameState 34 = Xaos Mirror
 * GameState 35 = Song of Kadath
 * GameState 36 = Alhazred's Flame
 * GameState 37 = Book of Shadows
 * GameState 38 = Seal of Leng
 * GameState 39 = Yig
 * GameState 40 = Shudde Mell
 * GameState 41 = Tsathoggua
 * GameState 42 = Win Game
 * GameState 43 = Atlach-Nacha
 * GameState 44 = Wait of Old One Click
 * GameState 51 = Lose Game
 */

//Old Ones
/*
 * rat = dagon
 * cow = azathoth
 * tiger = nyarlathotep
 * rabbit = yog-sothoth
 * snake = yig
 * horse = hastur
 * sheep = ithaqua
 * monkey = shudde mell
 * rooster = atlach-nacha
 * dog = tsathoggua
 * pig = shub-niggurath
 * dragon = cthulhu
 */

//Relic Number
/* relic1 = Alien Carving
 * relic2 = Mi-Go Eye
 * relic3 = Bizarre Statue
 * relic4 = Warding Box
 * relic5 = Silver Key
 * relic6 = Elder Sign
 * relic7 = Last Hourglass
 * relic8 = Xaos Mirror
 * relic9 = Song of Kadath
 * relic10 = Alhazred's Flame
 * relic11 = Book of Shadow
 * relic12 = Seal of Leng
 */

public class Game extends JFrame
{
//Instances
	private static Player player;
	private Player currentPlayer;
	private static Location location = new Location();
	private static MyPanel pan;
	private static Random randy = new Random();
	private Player originalPlayer;
	private CultistCard cultistCard;
	private PlayerCard playerCard;
	private OldOne oldOne;
	private OldOne sealedOldOne;
	private PlayerCard selectedRelic;
	private Player wardedBoxPlayer; //Warded Box
	private PlayerCard savedCard; //Mi-Go Eye
	
//Integers
	private int gameState = -1;
	private int prevState = 1;
	private static int summoningRate = 2;
	private static int cultistsRemaining = 26;
	private static int shoggothsRemaining = 3;
	private int rollsRemaining;
	private int shoggothDecisionLeft;
	private int cultistCardsDrawn;
	private int playerCardsDrawn;
	private int loseP1Sanity, loseP2Sanity, loseP3Sanity, loseP4Sanity; //shudde mell
	private int roastedCultists; //Alhazred's Flame
	private int greenDiscard, yellowDiscard, purpleDiscard, redDiscard;
	private int portraitSpacing, portraitWidth, cardSize, playerCount = 4;
	
//ArrayLists
	public static ArrayList<Location> locations = location.getLocations();
	private static ArrayList<Player> players = new ArrayList<Player>();
	private static ArrayList<BufferedImage> cultistCardImages = new ArrayList<BufferedImage>(24);
	private ArrayList<Card> cultistDeck;
	private ArrayList<Card> cultistDiscard = new ArrayList<Card>();
	private static ArrayList<BufferedImage> playerCardImages = new ArrayList<BufferedImage>(4);
	private ArrayList<Card> playerDeck;
	private ArrayList<Card> playerDiscard = new ArrayList<Card>();
	private static ArrayList<BufferedImage> optionImages = new ArrayList<BufferedImage>(10);
	private static ArrayList<BufferedImage> oldOneImages = new ArrayList<BufferedImage>(12);
	private ArrayList<Card> oldOneDeck;
	private ArrayList<String> possibleColors = new ArrayList<String>();
	private ArrayList<OldOne> toBeResolved = new ArrayList<OldOne>();
	private ArrayList<BufferedImage> relicImages = new ArrayList<BufferedImage>();
	private ArrayList<Card> relicDeck = new ArrayList<Card>(11);
	private static ArrayList<BufferedImage> investigatorImages = new ArrayList<BufferedImage>();
	private ArrayList<Card> investigatorDeck = new ArrayList<Card>(49);
	private int[] playerSelection = {0,1,2,3};
//Board
	private GameConstants cons = new GameConstants();
	private int width = cons.getBoardWidth(), height = cons.getBoardHeight();
//GUI
	private Rectangle cultistBtn = new Rectangle(10,10,40,40),
			shoggothBtn = new Rectangle(10,60,40,40), 
			busBtn = new Rectangle(10,110,40,40),
			tradeBtn = new Rectangle(10,160,40,40),
			gateBtn = new Rectangle(10,210,40,40),
			greenGate = new Rectangle(1150,10,40,40), yellowGate = new Rectangle(1200,10,40,40), purpleGate = new Rectangle(1250,10,40,40), redGate = new Rectangle(1300,10,40,40);
	private Location shog1Loc, shog1Choice1, shog1Choice2, shog2Loc, shog2Choice1, shog2Choice2, shog3Loc, shog3Choice1, shog3Choice2;
	private String reasonForLoss;
//Images
	private BufferedImage cultist, board, shoggoth, shoggothX2, shoggothX3, closeBtn, checkBtn, continueBtn, plusBtn, minusBtn, confirmBtn, cancelIcon, sanity, playerCardBack, downArrow, upArrow, winScreen,
	die0, die1, die2, die3, die4, die5, die6, 
	greenX, greenStar, yellowX, yellowStar, purpleX, purpleStar, redX, redStar, chainedLock;
//Fonts
	private Font proper = new Font("Arial",Font.BOLD,20);
	private Font impact = new Font("Impact", Font.BOLD,25);
	private Font YUGE = new Font("Cooper Black", Font.BOLD, 150);
	private Font yuge = new Font("Cooper Black", Font.BOLD, 100);
	private Font bold = new Font("Georgia", Font.BOLD, 25);
	private Font font = new Font("Algerian" , 80, 80);
//Colors
	private Color transYellow = new Color(216,141,31,100);
	private Color purple = new Color(150,50,190);
//Booleans
	private boolean boardSetUp, hunterHasRolled, hunterNeedRoll, magicianInsanity, cultistRoll,
			defeatCultist, defeatShoggoth, takeBus, tradeCard, closeGate,
			takeDiscard /*Reporter*/, moveCultist /*Occultist*/,
			ithaquaActive, sothothActive, yigActive, nyarlathotepActive, azathothActive,
			bizarreStatueActive, miGoEyeActive, wardedBoxActive;
	private boolean[] evilStirs = new boolean[3];

//Entry Point of Program
	public static void main(String[] args)
	{
		Game obj = new Game();
		obj.makeEnvironment();
	}

//Debug Shoggoth AI
	public void printShogPath() {
		for(Location loc : locations) {
			System.out.println(loc.getName());
			for(String str : loc.getShoggothNextLoc()) {
				System.out.print(str+"\t");
			}
			System.out.println();
		}
	}
	
// Creates the window and adds panels and mouse listener
	public void makeEnvironment()
	{
		setTitle("Pandemic Cthulhu");
		loadImages();
		setBoard();
		setBounds(0,0,width,height);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH); 
		setUndecorated(true);
		pan = new MyPanel();
		setContentPane(pan);
		pack();
		setResizable(true);
		setVisible(true);
		pan.getPanel().addMouseListener(new Clicks());
		repaint();
	}

// Loads all assets into memory for quick access
	private void loadImages()
	{
		try {	
			board = ImageIO.read(new File("Assets/cthulhuBoardLoad.png"));
			cultist = ImageIO.read(new File("Assets/cultist.png"));
//			doctorPawn = ImageIO.read(new File("Assets/doctorFigure.png"));
//			lightDoctorPawn = ImageIO.read(new File("Assets/lightDoctorFigure.jpg"));
//			detectivePawn = ImageIO.read(new File("Assets/detectiveFigure.png"));
//			lightDetectivePawn = ImageIO.read(new File("Assets/lightDetectiveFigure.jpg"));
//			hunterPawn = ImageIO.read(new File("Assets/hunterFigure.png"));
//			lightHunterPawn = ImageIO.read(new File("Assets/lightHunterFigure.jpg"));
			shoggoth = ImageIO.read(new File("Assets/Shoggoth.png"));
			shoggothX2 = ImageIO.read(new File("Assets/ShoggothX2.png"));
			shoggothX3 = ImageIO.read(new File("Assets/ShoggothX3.png"));
			closeBtn = ImageIO.read(new File("Assets/Icons/closeBtn.png"));
			checkBtn = ImageIO.read(new File("Assets/Icons/checkBtn.png")); 
			continueBtn = ImageIO.read(new File("Assets/Icons/continueBtn.png"));
			plusBtn = ImageIO.read(new File("Assets/Icons/plusBtn.png"));
			minusBtn = ImageIO.read(new File("Assets/Icons/minusBtn.png"));
			sanity = ImageIO.read(new File("Assets/Icons/insanity.png"));
			playerCardBack = ImageIO.read(new File("Assets/playerCardBack.png"));
			confirmBtn = ImageIO.read(new File("Assets/Icons/confirmBtn.png"));
			greenX = ImageIO.read(new File("Assets/Icons/greenX.png"));
			yellowX = ImageIO.read(new File("Assets/Icons/yellowX.png"));
			purpleX = ImageIO.read(new File("Assets/Icons/purpleX.png"));
			redX = ImageIO.read(new File("Assets/Icons/redX.png"));
			chainedLock = ImageIO.read(new File("Assets/Icons/chainedLock.png"));
			cancelIcon = ImageIO.read(new File("Assets/Icons/cancelIcon.gif"));
			winScreen = ImageIO.read(new File("Assets/youWin.jpg"));
			greenStar = ImageIO.read(new File("Assets/Icons/greenStar.png"));
			yellowStar = ImageIO.read(new File("Assets/Icons/yellowStar.png"));
			purpleStar = ImageIO.read(new File("Assets/Icons/purpleStar.png"));
			redStar = ImageIO.read(new File("Assets/Icons/redStar.png"));
			upArrow = ImageIO.read(new File("Assets/Icons/upArrow80.png"));
			downArrow = ImageIO.read(new File("Assets/Icons/downArrow80.png"));
		
		die0 = ImageIO.read(new File("Assets/die0.png"));
		die1 = ImageIO.read(new File("Assets/die1.png"));
		die2 = ImageIO.read(new File("Assets/die2.png"));
		die3 = ImageIO.read(new File("Assets/die3.png"));
		die4 = ImageIO.read(new File("Assets/die4.png"));
		die5 = ImageIO.read(new File("Assets/die5.png"));
		die6 = ImageIO.read(new File("Assets/die6.png"));
//Doctor
		investigatorImages.add(ImageIO.read(new File("Assets/Players/doctor.png")));
		investigatorImages.add(ImageIO.read(new File("Assets/Players/doctorYellow.png")));
		investigatorImages.add(ImageIO.read(new File("Assets/Players/doctorInsane.png")));
		investigatorImages.add(ImageIO.read(new File("Assets/Players/doctorInsaneYellow.png")));
		investigatorImages.add(ImageIO.read(new File("Assets/Players/DoctorCard.png")));
		investigatorImages.add(ImageIO.read(new File("Assets/Players/doctorFigure.png")));
		investigatorImages.add(ImageIO.read(new File("Assets/Players/doctorFigureSelected.png")));
//Detective
		investigatorImages.add(ImageIO.read(new File("Assets/Players/detective.png")));
		investigatorImages.add(ImageIO.read(new File("Assets/Players/detectiveYellow.png")));
		investigatorImages.add(ImageIO.read(new File("Assets/Players/detectiveInsane.png")));
		investigatorImages.add(ImageIO.read(new File("Assets/Players/detectiveInsaneYellow.png")));
		investigatorImages.add(ImageIO.read(new File("Assets/Players/DetectiveCard.png")));
		investigatorImages.add(ImageIO.read(new File("Assets/Players/detectiveFigure.png")));
		investigatorImages.add(ImageIO.read(new File("Assets/Players/detectiveFigureSelected.png")));
//Hunter
		investigatorImages.add(ImageIO.read(new File("Assets/Players/hunter.png")));
		investigatorImages.add(ImageIO.read(new File("Assets/Players/hunterYellow.png")));
		investigatorImages.add(ImageIO.read(new File("Assets/Players/hunterInsane.png")));
		investigatorImages.add(ImageIO.read(new File("Assets/Players/hunterInsaneYellow.png")));
		investigatorImages.add(ImageIO.read(new File("Assets/Players/HunterCard.png")));
		investigatorImages.add(ImageIO.read(new File("Assets/Players/hunterFigure.png")));
		investigatorImages.add(ImageIO.read(new File("Assets/Players/hunterFigureSelected.png")));
//Driver
		investigatorImages.add(ImageIO.read(new File("Assets/Players/driver.png")));
		investigatorImages.add(ImageIO.read(new File("Assets/Players/driverYellow.png")));
		investigatorImages.add(null);
		investigatorImages.add(null);
		investigatorImages.add(ImageIO.read(new File("Assets/Players/DriverCard.png")));
		investigatorImages.add(ImageIO.read(new File("Assets/Players/driverFigure.png")));
		investigatorImages.add(ImageIO.read(new File("Assets/Players/driverFigureSelected.png")));
//Reporter
		investigatorImages.add(ImageIO.read(new File("Assets/Players/reporter.png")));
		investigatorImages.add(ImageIO.read(new File("Assets/Players/reporterYellow.png")));
		investigatorImages.add(null);
		investigatorImages.add(null);
		investigatorImages.add(ImageIO.read(new File("Assets/Players/ReporterCard.png")));
		investigatorImages.add(ImageIO.read(new File("Assets/Players/reporterFigure.png")));
		investigatorImages.add(ImageIO.read(new File("Assets/Players/reporterFigureSelected.png")));
//Occultist
		investigatorImages.add(ImageIO.read(new File("Assets/Players/occultist.png")));
		investigatorImages.add(ImageIO.read(new File("Assets/Players/occultistYellow.png")));
		investigatorImages.add(null);
		investigatorImages.add(null);
		investigatorImages.add(ImageIO.read(new File("Assets/Players/OccultistCard.png")));
		investigatorImages.add(ImageIO.read(new File("Assets/Players/occultistFigure.png")));
		investigatorImages.add(ImageIO.read(new File("Assets/Players/occultistFigureSelected.png")));
//Magician
		investigatorImages.add(ImageIO.read(new File("Assets/Players/magician.png")));
		investigatorImages.add(ImageIO.read(new File("Assets/Players/magicianYellow.png")));
		investigatorImages.add(null);
		investigatorImages.add(null);
		investigatorImages.add(ImageIO.read(new File("Assets/Players/MagicianCard.png")));
		investigatorImages.add(ImageIO.read(new File("Assets/Players/magicianFigure.png")));
		investigatorImages.add(ImageIO.read(new File("Assets/Players/magicianFigureSelected.png")));
//Green
		cultistCardImages.add(ImageIO.read(new File("Assets/CultistCards/cultistTrainStation.png")));
		cultistCardImages.add(ImageIO.read(new File("Assets/CultistCards/cultistUniversity.png")));
		cultistCardImages.add(ImageIO.read(new File("Assets/CultistCards/cultistPoliceStation.png")));
		cultistCardImages.add(ImageIO.read(new File("Assets/CultistCards/cultistSecretLodge.png")));
		cultistCardImages.add(ImageIO.read(new File("Assets/CultistCards/cultistDiner.png")));
		cultistCardImages.add(ImageIO.read(new File("Assets/CultistCards/cultistPark.png")));
//Yellow
		cultistCardImages.add(ImageIO.read(new File("Assets/CultistCards/cultistCafe.png")));
		cultistCardImages.add(ImageIO.read(new File("Assets/CultistCards/cultistChurch.png")));
		cultistCardImages.add(ImageIO.read(new File("Assets/CultistCards/cultistHistoricInn.png")));
		cultistCardImages.add(ImageIO.read(new File("Assets/CultistCards/cultistFarmstead.png")));
		cultistCardImages.add(ImageIO.read(new File("Assets/CultistCards/cultistSwamp.png")));
		cultistCardImages.add(ImageIO.read(new File("Assets/CultistCards/cultistOldMill.png")));
//Purple
		cultistCardImages.add(ImageIO.read(new File("Assets/CultistCards/cultistJunkyard.png")));
		cultistCardImages.add(ImageIO.read(new File("Assets/CultistCards/cultistPawnShop.png")));
		cultistCardImages.add(ImageIO.read(new File("Assets/CultistCards/cultistFactory.png")));
		cultistCardImages.add(ImageIO.read(new File("Assets/CultistCards/cultistBoardwalk.png")));
		cultistCardImages.add(ImageIO.read(new File("Assets/CultistCards/cultistDocks.png")));
		cultistCardImages.add(ImageIO.read(new File("Assets/CultistCards/cultistHospital.png")));
//Red
		cultistCardImages.add(ImageIO.read(new File("Assets/CultistCards/cultistWoods.png")));
		cultistCardImages.add(ImageIO.read(new File("Assets/CultistCards/cultistGreatHall.png")));
		cultistCardImages.add(ImageIO.read(new File("Assets/CultistCards/cultistMarket.png")));
		cultistCardImages.add(ImageIO.read(new File("Assets/CultistCards/cultistTheater.png")));
		cultistCardImages.add(ImageIO.read(new File("Assets/CultistCards/cultistWharf.png")));
		cultistCardImages.add(ImageIO.read(new File("Assets/CultistCards/cultistGraveyard.png")));
		
		playerCardImages.add(ImageIO.read(new File("Assets/arkham.png")));
		playerCardImages.add(ImageIO.read(new File("Assets/dunwich.png")));
		playerCardImages.add(ImageIO.read(new File("Assets/innsmouth.png")));
		playerCardImages.add(ImageIO.read(new File("Assets/kingsport.png")));
		playerCardImages.add(ImageIO.read(new File("Assets/evilStirs.png")));
		
		optionImages.add(ImageIO.read(new File("Assets/Icons/defeatCultist.png")));
		optionImages.add(ImageIO.read(new File("Assets/Icons/yellowDefeatCultist.png")));
		optionImages.add(ImageIO.read(new File("Assets/Icons/defeatShoggoth.png")));
		optionImages.add(ImageIO.read(new File("Assets/Icons/yellowDefeatShoggoth.png")));
		optionImages.add(ImageIO.read(new File("Assets/Icons/bus.png")));
		optionImages.add(ImageIO.read(new File("Assets/Icons/yellowBus.png")));
		optionImages.add(ImageIO.read(new File("Assets/Icons/tradeCard.png")));
		optionImages.add(ImageIO.read(new File("Assets/Icons/yellowTradeCard.png")));
		optionImages.add(ImageIO.read(new File("Assets/Icons/sealGate.png")));
		optionImages.add(ImageIO.read(new File("Assets/Icons/yellowSealGate.png")));
		
		relicImages.add(ImageIO.read(new File("Assets/Relics/relic1.png")));
		relicImages.add(ImageIO.read(new File("Assets/Relics/relic2.png")));
		relicImages.add(ImageIO.read(new File("Assets/Relics/relic3.png")));
		relicImages.add(ImageIO.read(new File("Assets/Relics/relic4.png")));
		relicImages.add(ImageIO.read(new File("Assets/Relics/relic5.png")));
		relicImages.add(ImageIO.read(new File("Assets/Relics/relic6.png")));
		relicImages.add(ImageIO.read(new File("Assets/Relics/relic7.png")));
		relicImages.add(ImageIO.read(new File("Assets/Relics/relic8.png")));
		relicImages.add(ImageIO.read(new File("Assets/Relics/relic9.png")));
		relicImages.add(ImageIO.read(new File("Assets/Relics/relic10.png")));
		relicImages.add(ImageIO.read(new File("Assets/Relics/relic11.png")));
		relicImages.add(ImageIO.read(new File("Assets/Relics/relic12.png")));
		
//		doctorIcon = ImageIO.read(new File("Assets/Icons/doctorIcon.png"));
//		yellowDoctorIcon = ImageIO.read(new File("Assets/Icons/yellowDoctorIcon.png"));
//		doctorInsaneIcon = ImageIO.read(new File("Assets/Icons/doctorInsaneIcon.png"));
//		yellowDoctorInsaneIcon = ImageIO.read(new File("Assets/Icons/yellowDoctorInsaneIcon.png"));
//		
//		detectiveIcon = ImageIO.read(new File("Assets/Icons/detectiveIcon.png"));
//		yellowDetectiveIcon = ImageIO.read(new File("Assets/Icons/yellowDetectiveIcon.png"));
//		detectiveInsaneIcon = ImageIO.read(new File("Assets/Icons/detectiveInsaneIcon.png"));
//		yellowDetectiveInsaneIcon = ImageIO.read(new File("Assets/Icons/yellowDetectiveInsaneIcon.png"));
//		
//		hunterIcon = ImageIO.read(new File("Assets/Icons/hunterIcon.png"));
//		yellowHunterIcon = ImageIO.read(new File("Assets/Icons/yellowHunterIcon.png"));
//		hunterInsaneIcon = ImageIO.read(new File("Assets/Icons/hunterInsaneIcon.png"));
//		yellowHunterInsaneIcon = ImageIO.read(new File("Assets/Icons/yellowHunterInsaneIcon.png"));
		
		oldOneImages.add(ImageIO.read(new File("Assets/OldOneCards/rat.png"))); //dagon
		oldOneImages.add(ImageIO.read(new File("Assets/OldOneCards/cow.png"))); //azathoth
		oldOneImages.add(ImageIO.read(new File("Assets/OldOneCards/tiger.png"))); //nyarlathotep
		oldOneImages.add(ImageIO.read(new File("Assets/OldOneCards/rabbit.png"))); // yog-sothoth
		oldOneImages.add(ImageIO.read(new File("Assets/OldOneCards/snake.png"))); //yig
		oldOneImages.add(ImageIO.read(new File("Assets/OldOneCards/horse.png"))); //hastur
		oldOneImages.add(ImageIO.read(new File("Assets/OldOneCards/sheep.png"))); //ithaqua
		oldOneImages.add(ImageIO.read(new File("Assets/OldOneCards/monkey.png"))); //shudde mell
		oldOneImages.add(ImageIO.read(new File("Assets/OldOneCards/rooster.png"))); //atlach-nacha
		oldOneImages.add(ImageIO.read(new File("Assets/OldOneCards/dog.png"))); //tsathoggua
		oldOneImages.add(ImageIO.read(new File("Assets/OldOneCards/pig.png"))); //shub-niggurath
		oldOneImages.add(ImageIO.read(new File("Assets/OldOneCards/dragon.png"))); //cthulhu
		oldOneImages.add(ImageIO.read(new File("Assets/OldOneCards/oldOneBack.jpg")));
			}
		catch(Exception e){System.out.println(e);}
	}
	
	public ArrayList<BufferedImage> sendImagesToCultistDeck() {return cultistCardImages;}
	public ArrayList<BufferedImage> sendImagesToPlayerDeck() {return playerCardImages;}
	public ArrayList<BufferedImage> sendImagesToOldOneDeck() {return oldOneImages;}
	public ArrayList<BufferedImage> sendImagesToPlayers() {return investigatorImages;}

    private void shuffleDeck(ArrayList<Card> deck) 
    {
        for (int i=0; i<deck.size(); i++) 
        {
            int rand = randy.nextInt(i+1);
            Card temp = deck.get(i);
            deck.set(i, deck.get(rand));
            deck.set(rand, temp);
        }
    }

// Prepares the board and deck for play
	public void setBoard()
	{
		player = new Player();
		investigatorDeck = player.getDeck();
		//shuffleDeck(investigatorDeck);
		oldOne = new OldOne();
		oldOneDeck = oldOne.getDeck();
		//shuffleDeck(oldOneDeck);
		for(int i=0; i<5; i++)
			oldOneDeck.remove(oldOneDeck.size()-1);
		oldOneDeck.add(new OldOne(oldOneImages.get(11), "cthulhu"));
		cultistCard = new CultistCard();
		cultistDeck = cultistCard.getDeck();
		shuffleDeck(cultistDeck);
	}
	 
// Deals proper hand size according to the number of players
	public void dealHands() {
		int handSize=0;
		if(players.size()==2)
			handSize=4;
		else if(players.size()==3)
			handSize=3;
		else if(players.size()==4)
			handSize=2;
		for(Player p : players) {
			for(int i=0; i<handSize; i++)
				p.getHand().add((PlayerCard) playerDeck.remove(0));
			if(p.getName().equals("MAGICIAN"))
				p.getHand().add((PlayerCard) relicDeck.remove(0));
		}
	}
	
// Relics are supplemental cards with powerful effects that are shuffled into the deck
	public void addRelics() {
		for(int i=0; i<12; i++)
			relicDeck.add(new PlayerCard(relicImages.get(i),"relic"+(i+1)));
		shuffleDeck(relicDeck);
		for(int i=0; i<5; i++)
			playerDeck.add(relicDeck.remove(0));
	}
	
// Four Evil Stirs are inserted randomly in each quarter of the deck
	public void addEvilStirs(ArrayList<Card> temp,int first, int last) {
		if(last-first>=playerDeck.size()/4) {
			int middle = (first+last)/2;
			addEvilStirs(temp,first,middle);
			addEvilStirs(temp,middle+1,last);
		}
		else {
			ArrayList<Card> unicorn = new ArrayList<Card>(last-first+1);
			for(int i=first; i<=last; i++) {
				unicorn.add(playerDeck.get(i));
			}
			unicorn.add(new PlayerCard(playerCardImages.get(4),"evilStirs"));
			shuffleDeck(unicorn);
			temp.addAll(unicorn);
		}
	}
	
// Places cultists in different locations at the end of every turn
	public void summoning()
	{
		CultistCard card;
		while(cultistCardsDrawn<summoningRate) {
			card = (CultistCard) cultistDeck.remove(0);
			cultistDiscard.add(0,card);
			cultistCardsDrawn++;
			for(Location loc : locations)
				if(card.getName().equals(loc.getName()))
				{
					if(loc.getNumCultists()==3) {
						triggerOldOne();
						System.out.println("Old One Awakening from Cultists");
						prevState=gameState;
						return;
					}
					else {
						loc.setNumCultists(loc.getNumCultists()+1);
						cultistsRemaining--;
						if(cultistsRemaining < 0) {
							gameState = 51;
							reasonForLoss = "No More Cultists";
							return;
						}
					}
					break;
				}
			if(card.getShogMove()) {
				shoggothsMove();
				if(gameState==13)
					return;
				else if(rollsRemaining>0) {
					gameState=12;
					return;
				}
			}
		}
	}
	
// At the end of every turn, shoggoths will advance one step to the nearest gate
	public void shoggothsMove() {
		shog1Loc = null; shog2Loc = null; shog3Loc = null;
		System.out.println("Shoggoths Moved");
		int shogFound = 0;
		for(Location loc : locations) {
			if(loc.getNumShoggoths()!=0 && shogFound<3-shoggothsRemaining)
			{
				for(int j=0; j<loc.getNumShoggoths(); j++) {
					String nextLoc = loc.getShoggothNextLoc().get(0);
					if(shogFound==0)
						shog1Loc = loc;
					else if(shogFound==1)
						shog2Loc = loc;
					else if (shogFound==2)
						shog3Loc = loc;
					
					if(nextLoc.contains("TRIGGER")) {
						System.out.println("Old One Awakening from Shoggoth");
						triggerOldOne();
					}
					else if(nextLoc.contains("&"))
					{
						String loc1 = nextLoc.substring(nextLoc.indexOf('_')+1, nextLoc.indexOf('&'));
						for(Location path : locations)
							if(path.getName().equals(loc1)) {
								if(shogFound==0)
									shog1Choice1 = path;
								else if(shogFound==1)
									shog2Choice1 = path;
								else if(shogFound==2)
									shog3Choice1 = path;
								break;
							}
						String loc2 = nextLoc.substring(nextLoc.indexOf('&')+1);
						for(Location path  : locations)
							if(path.getName().equals(loc2)) {
								if(shogFound==0)
									shog1Choice2 = path;
								else if(shogFound==1)
									shog2Choice2 = path;
								else if(shogFound==2)
									shog3Choice2 = path;
								break;
							}
						shoggothDecisionLeft++;
					}
					else if(nextLoc.contains(","))
					{
						String loc1 = nextLoc.substring(nextLoc.indexOf('_')+1, nextLoc.indexOf(','));
						for(Location path : locations)
							if(path.getName().equals(loc1)) {
								if(shogFound==0)
									shog1Choice1 = path;
								else if(shogFound==1)
									shog2Choice1 = path;
								else if(shogFound==2)
									shog3Choice1 = path;
								break;
							}
						String loc2 = nextLoc.substring(nextLoc.lastIndexOf('_')+1);
						for(Location path  : locations)
							if(path.getName().equals(loc2)) {
								if(shogFound==0)
									shog1Choice2 = path;
								else if(shogFound==1)
									shog2Choice2 = path;
								else if(shogFound==2)
									shog3Choice2 = path;
								break;
							}
						shoggothDecisionLeft++;
					}
					else {
						nextLoc = nextLoc.substring(nextLoc.indexOf('_')+1);
						for(Location path : locations)
							if(path.getName().equals(nextLoc))
							{
								if(shogFound==0)
									shog1Choice1 = path;
								else if(shogFound==1)
									shog2Choice1 = path;
								else if (shogFound==2)
									shog3Choice1 = path;
								break;
							}
					}
					shogFound++;
				}
			}
		}
		if(shog1Loc!=null)
			System.out.println("Shog1Loc : "+shog1Loc.getName());
		if(shog1Choice1!=null)
			System.out.println("Shog1Choice1 : "+shog1Choice1.getName());
		if(shog1Choice2!=null)
			System.out.println("Shog1Choice2 : "+shog1Choice2.getName());
		System.out.println();
		if(shog2Loc!=null)
			System.out.println("Shog2Loc : "+shog2Loc.getName());
		if(shog2Choice1!=null)
			System.out.println("Shog2Choice1 : "+shog2Choice1.getName());
		if(shog2Choice2!=null)
			System.out.println("Shog2Choice2 : "+shog2Choice2.getName());
		System.out.println();
		if(shog3Loc!=null)
			System.out.println("Shog3Loc : "+shog3Loc.getName());
		if(shog3Choice1!=null)
			System.out.println("Shog3Choice1 : "+shog3Choice1.getName());
		if(shog3Choice2!=null)
			System.out.println("Shog3Choice2 : "+shog3Choice2.getName());
		System.out.println();
		
		if(shog1Loc!=null) {
			if(shog1Choice1!=null && shog1Choice2==null) {
				shog1Choice1.setNumShoggoths(shog1Choice1.getNumShoggoths()+1);
				shog1Loc.setNumShoggoths(shog1Loc.getNumShoggoths()-1);
			}
			if(shog1Choice1==null && shog1Choice2==null) {
				shog1Loc.setNumShoggoths(shog1Loc.getNumShoggoths()-1);
				shoggothsRemaining++;
			}
		}
		if(shog2Loc!=null) {
			if(shog2Choice1!=null && shog2Choice2==null) {
				shog2Choice1.setNumShoggoths(shog2Choice1.getNumShoggoths()+1);
				shog2Loc.setNumShoggoths(shog2Loc.getNumShoggoths()-1);
			}
			if(shog2Choice1==null && shog2Choice2==null) {
				shog2Loc.setNumShoggoths(shog2Loc.getNumShoggoths()-1);
				shoggothsRemaining++;
			}
		}
		if(shog3Loc!=null) {
			if(shog3Choice1!=null && shog3Choice2==null) {
				shog3Choice1.setNumShoggoths(shog3Choice1.getNumShoggoths()+1);
				shog3Loc.setNumShoggoths(shog3Loc.getNumShoggoths()-1);
			}
			if(shog3Choice1==null && shog3Choice2==null) {
				shog3Loc.setNumShoggoths(shog3Loc.getNumShoggoths()-1);
				shoggothsRemaining++;
			}
		}
//Check if Shoggoth Moved To Player
		for(Player p : players) {
			if(!wardedBoxActive) {
				p.setShoggothContact(p.getLocation().getNumShoggoths());
				rollsRemaining +=p.getShoggothContact();
			}
		}
		
		if(shoggothDecisionLeft==0) {
			if(toBeResolved.isEmpty()) {
				shog1Loc = null; shog1Choice1 = null; shog1Choice2 = null;
				shog2Loc = null; shog2Choice1 = null; shog2Choice2 = null;
				shog3Loc = null; shog3Choice1 = null; shog3Choice2 = null;
			}
			else
				gameState = 44;
			
			if(rollsRemaining>0)
				gameState = 12;
		}
		else
			gameState = 13;
	}
	
// Once a gate is closed, the number of cultists in every location within that region is decreased by 1 and the shoggoths will no long move towards there 
	public void closeGate(Gate g) {
		if(!g.getIsClosed()) {
			g.setClosed(true);
			for(Location loc : locations) {
				if(loc.getNumCultists()>0 && loc.getColor().equals(g.getColor())) {
					loc.setNumCultists(loc.getNumCultists()-1);
					cultistsRemaining++;
				}
				for(int i=0; i<loc.getShoggothNextLoc().size(); i++) {
					String str = loc.getShoggothNextLoc().get(i);
					if(str.contains(g.getColor())) {
						if(str.contains(",")) {
							if(str.indexOf(g.getColor())<str.indexOf(','))
								loc.getShoggothNextLoc().set(i, str.substring(str.indexOf(',')+1));
							else
								loc.getShoggothNextLoc().set(i, str.substring(0, str.indexOf(',')));
						}
						else
							loc.getShoggothNextLoc().remove(i);
					}
				}
			}
		}
		if(((Gate)locations.get(5)).getIsClosed() && ((Gate)locations.get(11)).getIsClosed() && ((Gate)locations.get(17)).getIsClosed() && ((Gate)locations.get(23)).getIsClosed()) {
			gameState = 42;
		}
//		System.out.println();
//		printShogPath();
	}
	
// Handles the 4 steps that occur immediately after an evil stirs card is drawn
	public void evilStirs() {
		if(gameState==16 && evilStirs[0]) {
			if(!wardedBoxActive)
				rollsRemaining++;
			if(rollsRemaining>0)
				gameState=6;
			else {
				gameState=17;
				evilStirs[0] = false;
			}
		}
		else if(gameState==17) {
			evilStirs[1] = true;
			triggerOldOne();
			System.out.println("Old One Awakening from Evil Stirs");
			return;
		}
		else if(gameState==18) {
			evilStirs[2] = true;
			CultistCard card = (CultistCard) cultistDeck.remove(cultistDeck.size()-1);
			cultistDiscard.add(0, card);
			for(Location loc : locations)
				if(loc.getName().equals(card.getName())) {
					pan.shoggothSmash = 1;
					pan.destinations.add(0,loc);
					pan.shoggothSize = 400;
					gameState=0;
				}
		}
		else if(gameState==19) {
			shuffleDeck(cultistDiscard);
			cultistDeck.addAll(0, cultistDiscard);
			cultistDiscard.clear();
			gameState=1;
			prevState = gameState;
			tryEndActions();
		}
	}
	
// Resolves all events that occur after a player's turn has ended
	public void tryEndActions()
	{
		if(currentPlayer.getActionsRemaining()<=0 && gameState==1) {
			if(currentPlayer.getIsInsane() && currentPlayer.getName().equals("MAGICIAN") && magicianInsanity) {
				for(PlayerCard card : currentPlayer.getHand())
					if(card.getName().contains("relic")) {
						prevState = gameState;
						gameState = 23;
					}
			}
//Draw Cards
			if(playerCardsDrawn < 2)
				gameState = 14;

			if(gameState==1) {
				if(bizarreStatueActive) {
					bizarreStatueActive = false;
				}
				else if(cultistCardsDrawn<summoningRate) {
					pan.cardsLeft = summoningRate-cultistCardsDrawn;
					gameState=0;
					return;
				}
				if(wardedBoxActive && wardedBoxPlayer==null)
					wardedBoxActive = false;
//NextPlayer
				if(players.indexOf(currentPlayer) < players.size()-1) {
					currentPlayer = players.get(players.indexOf(currentPlayer)+1);
					currentPlayer.setActionsRemaining(currentPlayer.getStartActionNum());
				}
//NextTurn
				else if (players.indexOf(currentPlayer)==players.size()-1) {
					currentPlayer = players.get(0);
					currentPlayer.setActionsRemaining(currentPlayer.getStartActionNum());
				}
				
//Reset Hunter Special
				if(currentPlayer.getName().equals("HUNTER") || currentPlayer.getName().equals("REPORTER")) {
					currentPlayer.setSpecial(true);
				}
//Reset Hunter Insanity Die Roll
				hunterHasRolled = false;
				
				if(wardedBoxActive && wardedBoxPlayer!=null && currentPlayer==wardedBoxPlayer)
					wardedBoxPlayer = null;
				originalPlayer = currentPlayer;
				cultistCardsDrawn=0;
				playerCardsDrawn=0;
				prevState = 1;
			}
		}
		resetBooleans();
	}
	
// Draws a card from player deck and resolves evil stirs
	public void drawCard() {
		if(playerDeck.isEmpty()) {
			gameState = 51;
			reasonForLoss = "No More Player Cards";
			return;
		}
		PlayerCard card = (PlayerCard) playerDeck.remove(0);
		playerCardsDrawn++;
		if(card.getName().equals("evilStirs")) {
			evilStirs[0] = true;
			gameState = 16;
			playerDiscard.add(0,card);
			return;
		}
		else {
			currentPlayer.getHand().add(card);
		}
	}
	
// Determines which card if any that was selected based on X and Y coordinates of mouse
	public PlayerCard trySelectCard(int mX, int mY)
	{
		for(int i=0; i<players.size(); i++) {
			for(int j=0; j<players.get(i).getHand().size(); j++) {
				PlayerCard card = players.get(i).getHand().get(j);
				int gap = ((portraitSpacing-portraitWidth-cardSize)/(players.get(i).getHand().size()));
				if(players.get(i).getHand().size()<3 || (players.size()==2 && players.get(i).getHand().size()<7))
					gap = cardSize;
				if(card.getIsSelected() && ((j==players.get(i).getHand().size()-1 && mX>30+portraitWidth+portraitSpacing*i+j*gap && mX<30+portraitSpacing+portraitSpacing*i+j*gap+cardSize && mY>620 && mY<750) || (mX>30+portraitWidth+portraitSpacing*i+j*gap && mX<30+portraitWidth+portraitSpacing*i+(j+1)*gap && mY>620 && mY<750))) {
					card.setSelected(false);
					return card;
				}
				else if((j==players.get(i).getHand().size()-1 && mX>30+portraitWidth+portraitSpacing*i+j*gap && mX<30+portraitWidth+portraitSpacing*i+j*gap+cardSize && mY>670 && mY<800) || (mX>30+portraitWidth+portraitSpacing*i+j*gap && mX<30+portraitWidth+portraitSpacing*i+(j+1)*gap && mY>670 && mY<800)) {
					card.setSelected(true);
					return card;
				}
			}
		}
		return null;
	}
	
// Determines which player if any that was selected based on X and Y coordinates of mouse
	public Player trySelectPlayer(int mX, int mY)
	{
		for(int i=0; i<players.size(); i++) {
			if(mX>portraitSpacing*i && mX<portraitSpacing*i+portraitWidth && mY>765-portraitWidth && mY<765) {
				return players.get(i);
			}
		}
		return null;
	}

// Changes current active player based on turn cycle
	public void changePlayer(int whichPlayer, int direction)
	{
		int playerNum = playerSelection[whichPlayer];
		if(playerNum>=0) {
			if(direction==1) {
				while(hasPlayer(playerNum)) {
					if(playerNum==6)
						playerNum=0;
					else
						playerNum++;
				};
			}
			else if(direction==-1) {
				while(hasPlayer(playerNum)) {
					if(playerNum==0)
						playerNum=6;
					else
						playerNum--;
				}
			}
		}
		playerSelection[whichPlayer] = playerNum;
	}
	
	public boolean hasPlayer(int playerNum)
	{
		for(int x : playerSelection)
		{
			if(x == playerNum)
				return true;
		}
		return false;
	}
	
// Determines if the relic can be used based on the current state of the game
	public boolean canUseRelic(PlayerCard relic) {
		boolean can = false;
		if(relic.getName().equals("relic6")) { //Elder Sign
			for(Location loc : locations) {
				if(loc.getIsGate() && ((Gate)loc).getIsClosed() && !((Gate)loc).getIsSealed()) {
					can = true;
					break;
				}	
			}
		}
		else if(relic.getName().equals("relic7")) { //Last Hourglass
			for(Card card : playerDiscard)
				if(!card.getName().contains("relic")) {
					can = true;
					break;
				}
		}
		else if(relic.getName().equals("relic8")) { //Xaos Mirror
			if(!currentPlayer.getHand().isEmpty()) {
				for(Player p : players)
					if(p!=currentPlayer && !p.getHand().isEmpty()) {
						can = true;
						break;
					}		
			}
		}
		else if(relic.getName().equals("relic9")) { //Song of Kadath
			for(Player p : players)
				if(p.getSanity()<4) {
					can = true;
					break;
				}
		}
		else if(relic.getName().equals("relic10")) { //Alhazred's Flame
			for(Location loc : locations)
				if(loc.getNumCultists()>0 || loc.getNumShoggoths()>0) {
					can = true;
					break;
				}
		}
		else if(relic.getName().equals("relic12")) { //Seal of Leng
			if(ithaquaActive || sothothActive || yigActive || nyarlathotepActive || azathothActive)
				can = true;
		}
		else {
			can = true;
		}
		return can;
	}
	
// Resolves the unique effect of the relic (usually triggers another game state)
	public void useRelic(PlayerCard relic) {
		gameState = prevState;
		if(canUseRelic(relic)) {
			if(relic.getName().equals("relic1")) { //Alien Carving
				originalPlayer.setActionsRemaining(originalPlayer.getActionsRemaining()+3);
				if(!wardedBoxActive)
					gameState = 12;
				else
					currentPlayer = originalPlayer;
			}
			else if(relic.getName().equals("relic2")) { //Mi-Go Eye
				miGoEyeActive = true;
				if(!wardedBoxActive)
					gameState = 12;
				else
					currentPlayer = originalPlayer;
			}
			else if(relic.getName().equals("relic3")) { //Bizarre Statue
				bizarreStatueActive = true;
				if(!wardedBoxActive)
					gameState = 12;
				else
					currentPlayer = originalPlayer;
			}
			else if(relic.getName().equals("relic4")) { //Warding Box
				wardedBoxActive = true;
				wardedBoxPlayer = currentPlayer;
			}
			else if(relic.getName().equals("relic5")) { //Silver Key
				gameState = 31;
			}
			else if(relic.getName().equals("relic6")) { //Elder Sign
				gameState = 32;
			}
			else if(relic.getName().equals("relic7")) { //Last Hourglass
				gameState = 33;
			}
			else if(relic.getName().equals("relic8")) { //Xaos Mirror
				gameState = 34;
			}
			else if(relic.getName().equals("relic9")) { //Song of Kadath
				 gameState = 35;
			}
			else if(relic.getName().equals("relic10")) { //Alhazred's Flame
				gameState = 36;
			}
			else if(relic.getName().equals("relic11")) { //Book of Shadow
				gameState = 37;
			}
			else if(relic.getName().equals("relic12")) { //Seal of Leng
				pan.getScroll().getViewport().setViewPosition(new Point(0,0));
				gameState = 38;
			}
		}
		else {
			if(!wardedBoxActive)
				gameState = 12;
			else
				currentPlayer = originalPlayer;
		}
	}
	
// Pauses game so that the effect of the old one can be resolved
	public void triggerOldOne() {
		for(Card oldOne : oldOneDeck) {
			if(!((OldOne) oldOne).getIsActive()) {
		//		((OldOne) oldOne).setActive(true);
				Card temp = oldOne;
				while(toBeResolved.contains(temp))
					temp = oldOneDeck.get(oldOneDeck.indexOf(temp)+1);
				toBeResolved.add((OldOne) temp);
				gameState = 44;
				break;
			}
			
		}
	}
	
// Once all effects from the old one has been resolved, remove it from the stack
	public void removeOldOne(String oldOne) {
		for(OldOne old : toBeResolved)
			if(old.getName().equals(oldOne)) {
				toBeResolved.remove(old);
				break;
			}
	}
	
// Resolves the unique effect of the old one currently on top of the stack
	public void activateOldOne(OldOne oldOne) {
		if(oldOneDeck.indexOf(oldOne)==3)
			summoningRate = 3;
/*Dagon*/
		if(oldOne.getName().equals("dagon")) {
			if(!oldOne.getIsActive()) {
				pan.destinations.add(locations.get(5));
				pan.destinations.add(locations.get(11));
				pan.destinations.add(locations.get(17));
				pan.destinations.add(locations.get(23));
			}
			if(!pan.destinations.isEmpty()) {
				pan.cultistsToDraw = 1;
				pan.vector[0] = pan.destinations.get(0).getX()-1350;
				pan.vector[1] = pan.destinations.get(0).getY()-10;
				double factor = 5/Math.sqrt(Math.pow(pan.vector[0], 2)+Math.pow(pan.vector[1], 2));
				pan.vector[0] *= factor;
				pan.vector[1] *= factor;
				pan.cultX = 1350;
				pan.cultY = 10;
			}
			gameState = 0;
		}
/*Hastur*/	
		else if(oldOne.getName().equals("hastur")) {
			if(!oldOne.getIsActive()) {
				Card cult = cultistDeck.remove(cultistDeck.size()-1);
				for(Location loc : locations) {
					if(loc.getName().equals(cult.getName())) {
						pan.destinations.add(0,loc);
						pan.shoggothSmash++;
						pan.shoggothSize = 400;
						break; 
					}
				}
			}
			gameState=0;
		}
/*Azathoth*/
		else if(oldOne.getName().equals("azathoth")) {
			azathothActive = true;
			cultistsRemaining -= 3;
			if(cultistsRemaining<0) {
				gameState = 51;
				reasonForLoss = "No More Cultists";
				return;
			}
			removeOldOne("azathoth");
		}
/*Shub-Niggurath*/
		else if(oldOne.getName().equals("shub-niggurath")) {
			pan.cardsLeft = 4;
			gameState=0;
		}
/*Shudde Mell*/
		else if(oldOne.getName().equals("shudde mell")) {
			gameState = 40;
		}
		else if(oldOne.getName().equals("tsathoggua")) {
			gameState = 41;
		}
/*Atlach-Nacha*/
		else if(oldOne.getName().equals("atlach-nacha")) {
			gameState = 43;
		}
/*Nyarlathotep*/
		else if(oldOne.getName().equals("nyarlathotep")) {
			nyarlathotepActive = true;
			removeOldOne("nyarlathotep");
		}
/*Yog-Sothoth*/	
		else if(oldOne.getName().equals("yog-sothoth")) {
			sothothActive = true;
			removeOldOne("yog-sothoth");
		}
/*Yig*/
		else if(oldOne.getName().equals("yig")) {
			yigActive = true;
			removeOldOne("yig");
		}
/*Ithaqua*/
		else if(oldOne.getName().equals("ithaqua")) {
			ithaquaActive = true;
			removeOldOne("ithaqua");
		}
/*Cthulhu*/
		else if(oldOne.getName().equals("cthulhu")) {
			gameState = 51;
			reasonForLoss = "Cthulhu Awakens";
		}
		
		((OldOne) oldOne).setActive(true);
	}
	
// Updates discard pile graphics
	public void resetPlayerDiscard() {
		greenDiscard = 0; yellowDiscard = 0; purpleDiscard = 0; redDiscard = 0;
		for(Card card : playerDiscard) {
			if(card.getName().equals("green"))
				greenDiscard++;
			else if(card.getName().equals("yellow"))
				yellowDiscard++;
			else if(card.getName().equals("purple"))
				purpleDiscard++;
			else if(card.getName().equals("red"))
				redDiscard++;
			((PlayerCard) card).setSelected(false);
		}
	}
	
// If all players are insane, then the game is lost
	public boolean allPlayersAreInsane() {
		for(Player p : players) {
			if(p.getSanity()>0)
				return false;
		}
		return true;
	}
	
// Determines what actions the player can take after all end of turn events have been handled
	public void resetBooleans() {
		defeatCultist = false; defeatShoggoth = false; takeBus = false; tradeCard = false; closeGate = false; moveCultist = false; takeDiscard = false;
		
		if(currentPlayer.getLocation().getNumCultists()!=0)
			defeatCultist = true;
		if(currentPlayer.getLocation().getNumShoggoths()!=0 && (currentPlayer.getActionsRemaining()>=3 || (currentPlayer.getName().equals("HUNTER") && currentPlayer.getSpecial()))) {
			defeatShoggoth = true;
		}
		if(currentPlayer.getLocation().getIsBus() && !(currentPlayer.getName().equals("REPORTER") && currentPlayer.getIsInsane())) {
			for(PlayerCard card : currentPlayer.getHand())
				if(!card.getName().contains("relic")) {
					takeBus = true;
					break;
				}
		}
		if(currentPlayer.getLocation().getPlayerList().size()>1) {
			outer:for(Player p : currentPlayer.getLocation().getPlayerList()) {
				for(PlayerCard card : p.getHand())
					if(card.getName().contains("relic") || card.getName().equals(currentPlayer.getLocation().getColor())) {
						tradeCard = true;
						break outer;
					}
			}
		}
		if(currentPlayer.getLocation().getIsGate()) {
			int counter=0;
			for(PlayerCard card : currentPlayer.getHand()) 
				if(card.getName().equals(currentPlayer.getLocation().getColor()))
					counter++;

			if(counter>=5 || (counter>=4 && miGoEyeActive) || (currentPlayer.getName().equals("DETECTIVE") && (counter>=4 || (counter>=3 && miGoEyeActive)))) {
				if(yigActive) {
					if(currentPlayer.getLocation().getColor().equals("green")) {
						for(PlayerCard card : currentPlayer.getHand())
							if(card.getName().equals("yellow") || card.getName().equals("purple")) {
								closeGate = true;
								break;
							}
					}
					else if(currentPlayer.getLocation().getColor().equals("yellow")) {
						for(PlayerCard card : currentPlayer.getHand())
							if(card.getName().equals("green") || card.getName().equals("red")) {
								closeGate = true;
								break;
							}
					}
					else if(currentPlayer.getLocation().getColor().equals("purple")) {
						for(PlayerCard card : currentPlayer.getHand())
							if(card.getName().equals("green") || card.getName().equals("red")) {
								closeGate = true;
								break;
							}
					}
					else if(currentPlayer.getLocation().getColor().equals("red")) {
						for(PlayerCard card : currentPlayer.getHand())
							if(card.getName().equals("yellow") || card.getName().equals("purple")) {
								closeGate = true;
								break;
							}
					}
				}
				else
					closeGate = true;
			}
		}
		if(currentPlayer.getName().equals("REPORTER") && currentPlayer.getIsInsane() && currentPlayer.getSpecial()) {
			if((currentPlayer.getLocation().getColor().equals("green") && greenDiscard!=0) || (currentPlayer.getLocation().getColor().equals("yellow") && yellowDiscard!=0)
					|| (currentPlayer.getLocation().getColor().equals("purple") && purpleDiscard!=0) || (currentPlayer.getLocation().getColor().equals("red") && redDiscard!=0))
				takeDiscard = true;
		}
	}
	
	public boolean gateSealed(Location loc) {
		if((loc.getColor().equals("green") && ((Gate)locations.get(5)).getIsSealed() ||
				loc.getColor().equals("yellow") && ((Gate)locations.get(11)).getIsSealed() ||
				loc.getColor().equals("purple") && ((Gate)locations.get(17)).getIsSealed() ||
				loc.getColor().equals("red") && ((Gate)locations.get(23)).getIsSealed()))
			return true;
		return false;
	}

	
//GRAPHICS CLASS
//Repeatedly draws all game objects and animations based on their xLoc and yLoc fields
	private class MyPanel extends JPanel
	{
		private double theta=4.7, shoggothGrow, totalRolls, currentFace, currentRoll;
		private ArrayList<Location> destinations = new ArrayList<Location>();
		private int cardsLeft=7, cultistsToDraw, shoggothSmash, shoggothSize;
		private float cultX, cultY;
		private float[] vector = new float[2];
		private BufferedImage dieFace = die0;
		private JPanel gamePanel;
		private JPanel oldOnesPanel;
		private JScrollPane scroll;
		private JPanel panel;
		
		public JScrollPane getScroll() {return scroll;}
		public JPanel getPanel() {return panel;}
		
		public MyPanel()
		{
			panel = new JPanel(new BorderLayout(5,5));
			
			gamePanel = new JPanel() {
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					try {drawStuff(g);} catch (InterruptedException e) {e.printStackTrace();}
				}
			};
			gamePanel.setBackground(Color.black);
			gamePanel.setPreferredSize(new Dimension(width+730,height+1000));
	//		gamePanel.setBounds(0,200,width,height-18);
			panel.add(gamePanel, BorderLayout.CENTER);
			
			oldOnesPanel = new JPanel() {
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					try {drawOldOnes(g);} catch (InterruptedException e) {e.printStackTrace();}
				}
			};
			oldOnesPanel.setBackground(Color.white);
			oldOnesPanel.setPreferredSize(new Dimension(width, 204));
			oldOnesPanel.setBounds(0,0,width,204);
			panel.add(oldOnesPanel, BorderLayout.NORTH);

			scroll = new JScrollPane(panel);
			scroll.setBounds(0, 0, width, height);
			scroll.setPreferredSize(new Dimension(width,height));
			scroll.getVerticalScrollBar().setPreferredSize (new Dimension(0,0));
			scroll.getVerticalScrollBar().setUnitIncrement(7);
			scroll.getHorizontalScrollBar().setUnitIncrement(7);
			scroll.getViewport().setViewPosition(new Point(0,209));
			add(scroll, BorderLayout.CENTER);
		}
		
		public void drawOldOnes(Graphics g) throws InterruptedException
		{
			for(int i=0; i<oldOneDeck.size(); i++) {
				if(((OldOne) oldOneDeck.get(i)).getIsActive())
					g.drawImage(oldOneDeck.get(i).getImage(), 100+180*i, 0, null);
				else
					g.drawImage(oldOneImages.get(12), 100+180*i, 0, null);
				if(sealedOldOne!=null && sealedOldOne.getName().equals(oldOneDeck.get(i).getName()))
					g.drawImage(chainedLock, 110+180*i, 50, 140, 140, null);
			}
		}
		
// Main draw function that calls all the other draw functions that handle specific aspects of the game
		public void drawStuff(Graphics g) throws InterruptedException
		{
			if(gameState!=21)
				for(Player p : players)
					if(p.getHand().size()>7) {
						if(p.getName().equals("MAGICIAN") && !p.getIsInsane()) {
							if(p.getHand().size()>8) {
								prevState = gameState;
								gameState=21;
								currentPlayer = p;
								break;
							}
						}
						else {
							prevState = gameState;
							gameState=21;
							currentPlayer = p;
							break;
						}
					}
			if(gameState==0)
				Thread.sleep(5);
			else if(gameState==6)
				Thread.sleep(210);
			else
				Thread.sleep(60);
			if(gameState==-1)
				drawChooseInvestigator(g);
			else if(gameState==42)
				drawWin(g);
			else if(gameState==51)
				drawGameOver(g);
			else {
				g.drawImage(board, 0, 0, width,(int)(height*.8),null);
				drawLocations(g);
				drawCultistCards(g);
				drawPlayerCards(g);
				drawHands(g);
				drawDie(g);
				drawLight(g);
				drawCultists(g);
				drawPlayers(g);
				drawShoggoths(g);
				drawOptions(g);
				drawPlayerInfo(g);
				drawSetUp(g);
			}
			repaint();
		}
		
		public void drawWin(Graphics g) throws InterruptedException {
			g.drawImage(winScreen, 0, 0, width, height,null);
			g.setColor(Color.black);
			g.setFont(YUGE);
			g.drawString("Congratulations!", 50, 350);
			g.drawString("You Didn't Lose", 100, 540);
		}
		
		public void drawGameOver(Graphics g) throws InterruptedException {
			g.setFont(YUGE);
			g.setColor(Color.white);
			g.drawString("You LOST", 350, 250);
			g.setFont(yuge);
			g.drawString(reasonForLoss, 475-reasonForLoss.length()*12, 400);
		}
		
		public void drawChooseInvestigator(Graphics g) throws InterruptedException {
			if(gameState==-1)
			{
				g.setColor(Color.white);
				for(int i=0; i<playerSelection.length; i++) {
					if(playerSelection[i]>=0) {
						g.drawImage(investigatorDeck.get(playerSelection[i]).getImage(), 54+i*356, 120, 250, 360, null);
						g.drawImage(upArrow, 139+i*356, 40, null);
						g.drawImage(downArrow, 139+i*356, 480, null);
					}
					else {
						g.fillRect(54+i*356, 120, 250, 360);
					}
					if(i >= 2)
					{
						if(playerCount > i)
							g.drawImage(checkBtn, 264+i*356, 440, 40, 40, null);
						else
							g.drawImage(closeBtn, 264+i*356, 440, 40, 40, null);
					}
				}
				g.setFont(font);
				g.drawRect(560, 700, 300, 100);
				g.drawString("SUBMIT", 570, 780);
			}
		}
		
		public void drawSetUp(Graphics g) throws InterruptedException
		{
			if(gameState==0)
			{
	//			System.out.println(cardsLeft);
				float offSet=0;
				for(Location loc : locations)
				{
					if(loc.getNumCultists()==1)
						offSet = 0;
					else if(loc.getNumCultists()==2)
						offSet = (float) 185.3;
					else if(loc.getNumCultists()==3)
						offSet = (float) 90.05;
	
					for(int i=0; i<loc.getNumCultists(); i++)
					{
						if(i==0)
							g.drawImage(cultist, loc.getX()+(int) (30*Math.cos(theta)), loc.getY()+(int) (30*Math.sin(theta)), 40, 40, null);
						if(i==1)
							g.drawImage(cultist, loc.getX()+(int) (30*Math.cos(theta+offSet)), loc.getY()+(int) (30*Math.sin(theta+offSet)), 40, 40, null);
						if(i==2)
							g.drawImage(cultist, loc.getX()+(int) (30*Math.cos(theta+2*offSet)), loc.getY()+(int) (30*Math.sin(theta+2*offSet)), 40, 40, null);
					}
				}
				for(Location loc : locations) {
					if(loc.getNumShoggoths()==1)
						g.drawImage(shoggoth,(int) (loc.getX()-shoggothGrow/2),(int) (loc.getY()-7-shoggothGrow),(int) (50+shoggothGrow),(int) (50+shoggothGrow), null);
					else if(loc.getNumShoggoths()==2)
						g.drawImage(shoggothX2,(int) (loc.getX()-shoggothGrow/2),(int) (loc.getY()-7-shoggothGrow),(int) (50+shoggothGrow),(int) (50+shoggothGrow), null);
					else if(loc.getNumShoggoths()==3)
						g.drawImage(shoggothX3,(int) (loc.getX()-shoggothGrow/2),(int) (loc.getY()-7-shoggothGrow),(int) (50+shoggothGrow),(int) (50+shoggothGrow), null);
				}
//Populates Destinations if Needed
				if(cultY!=1350 && cultY!=10 && destinations.isEmpty() && cultistsToDraw<=0) {
					if(cultistDeck.isEmpty()) {
						cultistDeck.addAll(cultistDiscard);
						cultistDiscard.clear();
						shuffleDeck(cultistDeck);
					}
					else if(cardsLeft>0){
						if(!toBeResolved.isEmpty()) {
							if(toBeResolved.get(toBeResolved.size()-1).getName().equals("shub-niggurath")) {
								for(Location loc : locations)
									if(loc.getName().equals(cultistDeck.get(cultistDeck.size()-1).getName())) {
										destinations.add(0,loc);
										cultistsToDraw = 1;
										cultistDiscard.add(0,cultistDeck.remove(cultistDeck.size()-1));
										break;
									}
								if(shoggothSmash==0) {
									vector[0] = destinations.get(0).getX()-1350;
									vector[1] = destinations.get(0).getY()-10;
									double factor = 5/Math.sqrt(Math.pow(vector[0], 2)+Math.pow(vector[1], 2));
									vector[0] *= factor;
									vector[1] *= factor;
									cultX = 1350;
									cultY = 10;
								}
							}
						}
						else {
							for(Location loc : locations)
								if(loc.getName().equals(cultistDeck.get(0).getName())) {
									
									if(boardSetUp) {
										if(cardsLeft==1) {
											shoggothSize = 400;
											shoggothSmash++;
											cardsLeft--;
											destinations.add(0,loc);
										}
										else {
											cultistsToDraw = cardsLeft/2;
											destinations.add(0,loc);
										}
									}
									else {
										cultistsToDraw = 1;
										destinations.add(0,loc);
									}
									if(shoggothSmash==0) {
										vector[0] = destinations.get(0).getX()-1350;
										vector[1] = destinations.get(0).getY()-10;
										double factor = 5/Math.sqrt(Math.pow(vector[0], 2)+Math.pow(vector[1], 2));
										vector[0] *= factor;
										vector[1] *= factor;
										cultX = 1350;
										cultY = 10;
									}
									cultistDiscard.add(0,cultistDeck.remove(0));
									break;
								}
						}
					}
	//				System.out.println(cardsLeft);
				}
//Exit Set Up 
				if(destinations.isEmpty() && shoggothSize<=0 && cultistsToDraw<=0) {
					if(boardSetUp) {
						boardSetUp=false;
						originalPlayer = currentPlayer;
						if(rollsRemaining>0)
							gameState=12;
					}
					else if(hunterNeedRoll) {
						hunterNeedRoll = false;
						hunterHasRolled=true;
						if(rollsRemaining>0)
							gameState=12;
						else {
							currentPlayer = originalPlayer;
							gameState=1;
						}
					}
					else if(cultistRoll) {
						cultistRoll = false;
						currentPlayer = originalPlayer;
						if(players.get(0).getShoggothContact()>0 || players.get(1).getShoggothContact()>0 || (players.size()>=3 && players.get(2).getShoggothContact()>0) || (players.size()>=4 && players.get(3).getShoggothContact()>0)) {
							gameState=12;
							for(Player p : players)
								if(p.getShoggothContact()>0) {
									currentPlayer = p;
									break;
								}
						}
						else if(rollsRemaining>0)
							gameState=12;
						else if(shoggothDecisionLeft!=0) {
							gameState=13;
						}
						else if(!toBeResolved.isEmpty() && toBeResolved.get(toBeResolved.size()-1).getName().equals("hastur")) {
							removeOldOne("hastur");
							shoggothsMove();
							if(rollsRemaining==0 && gameState!=44 && shoggothDecisionLeft==0) {
								if(evilStirs[0]) {
									gameState=17;
									evilStirs[0] = false;
								}
								else if(evilStirs[1]) {
									evilStirs[1] = false;
									gameState = 18;
								}
								else if(evilStirs[2]) {
									evilStirs[2] = false;
									 gameState=19;
								}
								else
									gameState = 1;
							}
						}
						else if(cardsLeft>0 || cultistsToDraw>0) {
							gameState=0;
						}
						else {
							if(evilStirs[0]) {
								gameState=17;
								evilStirs[0] = false;
							}
							else if(evilStirs[1]) {
								evilStirs[1] = false;
								gameState = 18;
							}
							else if(evilStirs[2]) {
								evilStirs[2] = false;
								gameState=19;
							}
							else
								gameState=1;
						}
					}
					else if(!toBeResolved.isEmpty()) {
						if(toBeResolved.get(toBeResolved.size()-1).getName().equals("hastur")) {
							removeOldOne("hastur");
							shoggothsMove();
						}
						else if(toBeResolved.get(toBeResolved.size()-1).getName().equals("shub-niggurath"))
							removeOldOne("shub-niggurath");
						else if(toBeResolved.get(toBeResolved.size()-1).getName().equals("dagon"))
							removeOldOne("dagon");
						
						if(!toBeResolved.isEmpty()) {
							gameState = 44;
						}
						if(rollsRemaining==0 && gameState!=44 && shoggothDecisionLeft==0) {
							if(evilStirs[0]) {
								gameState=17;
								evilStirs[0] = false;
							}
							else if(evilStirs[1]) {
								evilStirs[1] = false;
								gameState = 18;
							}
							else if(evilStirs[2]) {
								evilStirs[2] = false;
								gameState=19;
							}
							else
								gameState = prevState;
						}
						if(rollsRemaining>0)
							gameState=12;
						if(cultistCardsDrawn>0)
							cardsLeft = summoningRate - cultistCardsDrawn;
					}
					else if(evilStirs[2]) {
						gameState = 19;
						evilStirs[2] = false;
					}
					else if(rollsRemaining==0)
						gameState=1;
					tryEndActions();
				}
//Shoggoth Smashes
				else if(shoggothSize<=0 && shoggothSmash>0) {
					if(!gateSealed(destinations.get(0))) {
						destinations.get(0).setNumShoggoths(destinations.get(0).getNumShoggoths()+1);
						shoggothsRemaining--;
					}
					if(shoggothsRemaining < 0) {
						gameState = 51;
						reasonForLoss = "No More Shoggoths";
						return;
					}
					shoggothSmash--;
					if(!destinations.get(0).getPlayerList().isEmpty()) {
						for(Player p : destinations.get(0).getPlayerList()) {
							if(!wardedBoxActive) {
								p.setShoggothContact(p.getShoggothContact()+1);
								rollsRemaining += p.getShoggothContact();
							}
						}
					}
					destinations.remove(0);
				}
//Reaches Destination
				else if(cultX>destinations.get(0).getX()-20 && cultX<destinations.get(0).getX()+20 && cultY>destinations.get(0).getY()-20 && cultY<destinations.get(0).getY()+20) {
					if(!gateSealed(destinations.get(0))) {
						if(destinations.get(0).getNumCultists()==3) {
							prevState=gameState;
							triggerOldOne();
							System.out.println(cultistsToDraw+"\t"+shoggothSize+"\t"+cardsLeft);
							System.out.println("Old One Awakening from Cultists");
						}
						else {
							destinations.get(0).setNumCultists(destinations.get(0).getNumCultists()+1);
							cultistsRemaining--;
						}
					}
					cultistsToDraw--;
					if(cultistsRemaining<0) {
						gameState = 51;
						reasonForLoss = "No More Cultists";
						return;
					}
					if(cultistsToDraw<=0) {
						cultistsToDraw = 0;
						destinations.remove(0);
						if(cardsLeft>0) {
							cardsLeft--;
							if(!cultistRoll && !hunterNeedRoll && !boardSetUp && (toBeResolved.isEmpty() || !toBeResolved.get(toBeResolved.size()-1).getName().equals("shub-niggurath"))) {
								cultistCardsDrawn++;
								if(!cultistDiscard.isEmpty() && ((CultistCard)cultistDiscard.get(0)).getShogMove()) {
									shoggothsMove();
									if(rollsRemaining>0)
										gameState=12;
								}
							}
						}
						if(!destinations.isEmpty() && !toBeResolved.isEmpty() && toBeResolved.get(toBeResolved.size()-1).getName().equals("dagon"))
							cultistsToDraw = 1;
					}
					if(cultistsToDraw!=0 && !destinations.isEmpty()) {
						vector[0] = destinations.get(0).getX()-1350;
						vector[1] = destinations.get(0).getY()-10;
						double factor = 5/Math.sqrt(Math.pow(vector[0], 2)+Math.pow(vector[1], 2));
						vector[0] *= factor;
						vector[1] *= factor;
						cultX = 1350;
						cultY = 10;
					}
				}
//Shoggoth Animation
				else if(shoggothSmash>0) {
					g.drawImage(shoggoth,(int) (destinations.get(0).getX()-4-shoggothSize/2),(int) (destinations.get(0).getY()-13-shoggothSize), (int) (55+shoggothSize), (int) (55+shoggothSize), null);
					shoggothSize -= 2;
				}
//Cultist Animation
				else if(cultistsToDraw>0) {
					cultX += 2*vector[0];
					cultY += 2*vector[1];
					g.drawImage(cultist, Math.round(cultX), Math.round(cultY), 40, 40, null);
				}
			}
		}
		
		public void drawLocations(Graphics g) throws InterruptedException
		{
			for(Location loc : locations)
				if(loc.getIsGate()) {
					if(((Gate)loc).getIsSealed()) {
						if(loc.getColor().equals("green"))
							g.drawImage(greenStar, loc.getX(), loc.getY(), 40, 40, null);
						else if(loc.getColor().equals("yellow"))
							g.drawImage(yellowStar, loc.getX(), loc.getY(), 40, 40, null);
						else if(loc.getColor().equals("purple"))
							g.drawImage(purpleStar, loc.getX(), loc.getY(), 40, 40, null);
						else if(loc.getColor().equals("red"))
							g.drawImage(redStar, loc.getX(), loc.getY(), 40, 40, null);
					}
					else if(((Gate)loc).getIsClosed()) {
						if(loc.getColor().equals("green"))
							g.drawImage(greenX, loc.getX(), loc.getY(), 40, 40, null);
						else if(loc.getColor().equals("yellow"))
							g.drawImage(yellowX, loc.getX(), loc.getY(), 40, 40, null);
						else if(loc.getColor().equals("purple"))
							g.drawImage(purpleX, loc.getX(), loc.getY(), 40, 40, null);
						else if(loc.getColor().equals("red"))
							g.drawImage(redX, loc.getX(), loc.getY(), 40, 40, null);
					}
				}
		}
		
		public void drawHands(Graphics g) throws InterruptedException
		{
			for(int i=0; i<players.size(); i++)
				for(int j=0; j<players.get(i).getHand().size(); j++) {
					PlayerCard card = players.get(i).getHand().get(j);
					int gap = j*((portraitSpacing-portraitWidth-cardSize)/(players.get(i).getHand().size()));
					if(players.get(i).getHand().size()<3 || (players.size()==2 && players.get(i).getHand().size()<7))
						gap = cardSize*j;
					if(card.getIsSelected())
						g.drawImage(card.getImage(), 30+portraitWidth+portraitSpacing*i+gap, 725-cardSize, cardSize, 10*cardSize/7, null);
					else
						g.drawImage(card.getImage(), 30+portraitWidth+portraitSpacing*i+gap, 765-cardSize, cardSize, 10*cardSize/7, null);
				}
		}
		
		public void drawPlayerCards(Graphics g) throws InterruptedException
		{
			int num;
			int layer;
			for(int i=0; i<playerDeck.size(); i++)
			{
				num = i%11;
				layer = i/11;
				g.drawImage(playerDeck.get(i).getImage(), 200+84*num, height+layer*150, 84, 120, null);
			}
			for(int j=0; j<playerDiscard.size(); j++)
			{
				num = j%10;
				layer = j/10;
				g.drawImage(playerDiscard.get(j).getImage(), 200+84*num, height+600+layer*120, 84, 120, null);
			}
			g.drawImage(playerCardBack, 0, 351, 105, 150, null);
			g.setFont(impact);
			g.setColor(Color.white);
			g.drawString(""+playerDeck.size(), 80, 475);
			g.setColor(Color.green);
			g.drawRect(1256, 475, 154, 30);
			g.fillRect(1410-15*greenDiscard, 475, 15*greenDiscard, 30);
			g.setColor(Color.yellow);
			g.drawRect(1256, 507, 154, 30);
			g.fillRect(1410-15*yellowDiscard, 507, 15*yellowDiscard, 30);
			g.setColor(purple);
			g.drawRect(1256, 539, 154, 30);
			g.fillRect(1410-15*purpleDiscard, 539, 15*purpleDiscard, 30);
			g.setColor(Color.red);
			g.drawRect(1256, 571, 154, 30);
			g.fillRect(1410-15*redDiscard, 571, 15*redDiscard, 30);
			
			g.setFont(bold);
			g.setColor(Color.white);
			g.drawString(""+greenDiscard, 1260, 495);
			g.drawString(""+yellowDiscard, 1260, 527);
			g.drawString(""+purpleDiscard, 1260, 559);
			g.drawString(""+redDiscard, 1260, 591);
			
			if(miGoEyeActive)
				g.drawImage(relicImages.get(1), 1154, 478, 84, 120, null);
			if(!cultistDiscard.isEmpty())
				g.drawImage(cultistDiscard.get(0).getImage(), 1270, 250, 140, 200, null);
			else if(bizarreStatueActive)
				g.drawImage(relicImages.get(2), 1270, 250, 140, 200, null);
		} 
		
		public void drawPlayers(Graphics g) throws InterruptedException
		{
			for(Location loc : locations)	
			{
				int numPlayer = loc.getPlayerList().size();
				for(int i=numPlayer-1; i>=0; i--)
				{
					Player p = loc.getPlayerList().get(i);
					BufferedImage img = null; 
					if(currentPlayer==p)
						img = p.getSelectedPawnImage();
					else
						img = p.getPawnImage();
					
					if(numPlayer==1)
						g.drawImage(img, loc.getX()-10, loc.getY()-55, 60, 100, null);
					else if(numPlayer==2)
					{
						if(i==0)
							g.drawImage(img, loc.getX()-20, loc.getY()-70, 60, 100, null);
						else if(i==1)
							g.drawImage(img, loc.getX()+10, loc.getY()-50, 60, 100, null);
					}
					else if(numPlayer==3)
					{
						if(i==0)
							g.drawImage(img, loc.getX()+10, loc.getY()-65, 60, 100, null);
						else if(i==1)
							g.drawImage(img, loc.getX()-10, loc.getY()-50, 60, 100, null);
						else if(i==2)
							g.drawImage(img, loc.getX()-30, loc.getY()-60, 60, 100, null);
					}
					else if(numPlayer==4)
					{
						if(i==0)
							g.drawImage(img, loc.getX()-10, loc.getY()-50, 60, 100, null);
						else if(i==1)
							g.drawImage(img, loc.getX()-30, loc.getY()-60, 60, 100, null);
						else if(i==2)
							g.drawImage(img, loc.getX()+10, loc.getY()-65, 60, 100, null);
						else if(i==3)
							g.drawImage(img, loc.getX()-15, loc.getY()-75, 54, 95, null);
					}
				}
			}
			for(int i=0; i<players.size(); i++) {
				if(gameState==22 || (gameState==2 && players.get(i)!=currentPlayer && currentPlayer.getLocation().getPlayerList().contains(players.get(i)))) {
					if(players.get(i).getIsInsane() && players.get(i).getYellowInsanePortrait()!=null)
						g.drawImage(players.get(i).getYellowInsanePortrait(), portraitSpacing*i, 765-portraitWidth, portraitWidth, portraitWidth, null);
					else
						g.drawImage(players.get(i).getYellowPortrait(), portraitSpacing*i, 765-portraitWidth, portraitWidth, portraitWidth, null);
				}
				else if(players.get(i).getIsInsane() && players.get(i).getInsanePortrait()!=null)
					g.drawImage(players.get(i).getInsanePortrait(), portraitSpacing*i, 765-portraitWidth, portraitWidth, portraitWidth, null);
				else
					g.drawImage(players.get(i).getPortrait(), portraitSpacing*i, 765-portraitWidth, portraitWidth, portraitWidth, null);
			}
		}
		
		public void drawCultists(Graphics g) throws InterruptedException
		{
			if(gameState>0) {
				float offSet=0;
				for(Location loc : locations)
				{
					if(loc.getNumCultists()==1)
						offSet = 0;
					else if(loc.getNumCultists()==2)
						offSet = (float) 185.3;
					else if(loc.getNumCultists()==3)
						offSet = (float) 90.05;
	
					for(int i=0; i<loc.getNumCultists(); i++)
					{
						if(i==0)
							g.drawImage(cultist, loc.getX()+(int) (30*Math.cos(theta)), loc.getY()+(int) (30*Math.sin(theta)), 40, 40, null);
						if(i==1)
							g.drawImage(cultist, loc.getX()+(int) (30*Math.cos(theta+offSet)), loc.getY()+(int) (30*Math.sin(theta+offSet)), 40, 40, null);
						if(i==2)
							g.drawImage(cultist, loc.getX()+(int) (30*Math.cos(theta+2*offSet)), loc.getY()+(int) (30*Math.sin(theta+2*offSet)), 40, 40, null);
					}
				}
				theta = (theta+0.2)%360000000;
			}
			g.setColor(Color.black);
			g.fillRect(1350, 0, 60, 60);
			g.drawImage(cultist, 1350, 10, 50, 50, null);
			g.setColor(Color.white);
			g.setFont(impact);
			g.drawString(""+cultistsRemaining, 1384, 24);
		}
		
		public void drawShoggoths(Graphics g) throws InterruptedException
		{
			if(gameState>0) {
				for(Location loc : locations) {
					if(loc.getNumShoggoths()==1)
						g.drawImage(shoggoth,(int) (loc.getX()-shoggothGrow/2),(int) (loc.getY()-5-shoggothGrow),(int) (50+shoggothGrow),(int) (50+shoggothGrow), null);
					else if(loc.getNumShoggoths()==2)
						g.drawImage(shoggothX2,(int) (loc.getX()-shoggothGrow/2),(int) (loc.getY()-5-shoggothGrow),(int) (50+shoggothGrow),(int) (50+shoggothGrow), null);
					else if(loc.getNumShoggoths()==3)
						g.drawImage(shoggothX3,(int) (loc.getX()-shoggothGrow/2),(int) (loc.getY()-5-shoggothGrow),(int) (50+shoggothGrow),(int) (50+shoggothGrow), null);
				}
				shoggothGrow = Math.abs(45*Math.sin(theta/2));
			}
		}
		
		public void drawCultistCards(Graphics g) throws InterruptedException
		{
			int num;
			int layer;
			for(int i=0; i<cultistDeck.size(); i++)
			{
				num = i%6;
				layer = i/6;
				g.drawImage(cultistDeck.get(i).getImage(), 120*num+1435, 150+layer*172, 120, 172, null);
			}
			for(int j=0; j<cultistDiscard.size(); j++)
			{
				num = j%6;
				layer = j/6;
				g.drawImage(cultistDiscard.get(j).getImage(), 120*num+1435, height-8+layer*172, 120, 172, null);
			}
			if(!cultistDiscard.isEmpty())
				g.drawImage(cultistDiscard.get(0).getImage(), 1270, 255, 126, 180, null);
		}
		
		public void drawOptions(Graphics g) {
			if(gameState==1 && defeatCultist)
				g.drawImage(optionImages.get(1), 0, 0, 50, 50, null);
			else
				g.drawImage(optionImages.get(0), 0, 0, 50, 50, null);
			
			if(gameState==1 && defeatShoggoth)
				g.drawImage(optionImages.get(3), 0, 50, 50, 50, null);
			else
				g.drawImage(optionImages.get(2), 0, 50, 50, 50, null);
			
			if(gameState==1 && takeBus)
				g.drawImage(optionImages.get(5), 0, 100, 50, 50, null);
			else
				g.drawImage(optionImages.get(4), 0, 100, 50, 50, null);

			if((gameState==1 && tradeCard) || gameState==34)
				g.drawImage(optionImages.get(7), 0, 150, 50, 50, null);
			else
				g.drawImage(optionImages.get(6), 0, 150, 50, 50, null);
			
			if(gameState==1 && closeGate)
				g.drawImage(optionImages.get(9), 0, 200, 50, 50, null);
			else
				g.drawImage(optionImages.get(8), 0, 200, 50, 50, null);

			if(!((Gate)locations.get(5)).getIsClosed()) {
				g.setColor(Color.green);
				((Graphics2D)g).fill(greenGate);
			}
			if(!((Gate)locations.get(11)).getIsClosed()) {
				g.setColor(Color.yellow);
				((Graphics2D)g).fill(yellowGate);
			}
			if(!((Gate)locations.get(17)).getIsClosed()) {
				g.setColor(purple);
				((Graphics2D)g).fill(purpleGate);
			}
			if(!((Gate)locations.get(23)).getIsClosed()) {
				g.setColor(Color.red);
				((Graphics2D)g).fill(redGate);
			}
		}
		
		public void drawDie(Graphics g) throws InterruptedException
		{
			if(gameState==6)
			{
				totalRolls = randy.nextInt(6)+15;
				currentFace = randy.nextInt(6) + 1;
				if(hunterNeedRoll)
					gameState = 7;
				else
					gameState = 20;
				for(Player p : players)
					if(p.getShoggothContact()>0)
						gameState = 10;
			}
			else if(gameState==7) {
				int toAdd = randy.nextInt(4)+1;
				if(toAdd==3)
					toAdd=5;
				currentFace+=toAdd;
				if(currentFace>6)
					currentFace = currentFace-6;
				currentRoll++;
				
				if(currentRoll > totalRolls)
				{
					currentRoll = 0;
/*Place 1 Cultist*/	if(currentFace == 4 || currentFace == 2 || currentFace==1) {
						prevState = gameState;
						cultistsToDraw = 1;
						destinations.add(currentPlayer.getLocation());
						gameState = 0;
						vector[0] = destinations.get(0).getX()-1350;
						vector[1] = destinations.get(0).getY()-10;
						double factor = 5/Math.sqrt(Math.pow(vector[0], 2)+Math.pow(vector[1], 2));
						vector[0] *= factor;
						vector[1] *= factor;
						cultX = 1350;
						cultY = 10;
//						currentPlayer.getLocation().setNumCultists(currentPlayer.getLocation().getNumCultists()+1);
//						cultistsRemaining--;
//						if(cultistsRemaining < 0) {
//							gameState = 51;
//							reasonForLoss = "No More Cultists";
//							return;
//						}
					}
					else {
						hunterNeedRoll = false;
						hunterHasRolled=true;
						if(rollsRemaining>0)
							gameState=12;
						else {
							currentPlayer = originalPlayer;
							gameState=1;
						}
					}
				}
			}
			else if(gameState==10)
			{
				for(Player p : players)
					if(p.getShoggothContact()>0)
					{
						currentPlayer = p;
						gameState = 20;
						p.setShoggothContact(p.getShoggothContact()-1);
						break;
					}
			}
			else if(gameState==20) //Player Consent
			{
				int toAdd = randy.nextInt(4)+1;
				if(toAdd==3)
					toAdd=5;
				currentFace+=toAdd;
				if(currentFace>6)
					currentFace = currentFace-6;
				currentRoll++;
				
				if(currentRoll > totalRolls)
				{
/*Lose 1 Sanity*/	if(currentFace == 4 || currentFace == 2) {
						currentPlayer.setSanity(currentPlayer.getSanity()-1);
						if(allPlayersAreInsane()) {
							gameState = 51;
							reasonForLoss = "All Players are Insane";
							return;
						}
						if(currentPlayer.getSanity() < 0)
							currentPlayer.setSanity(0);
					}
/*Lose 2 Sanity*/	else if(currentFace == 1 && !currentPlayer.getIsInsane()) {
						currentPlayer.setSanity(currentPlayer.getSanity()-2);
						if(allPlayersAreInsane()) {
							gameState = 51;
							reasonForLoss = "All Players are Insane";
							return;
						}
						if(currentPlayer.getSanity() < 0)
							currentPlayer.setSanity(0);
					}
/*2 Cultists*/		else if(currentFace == 5 && !gateSealed(currentPlayer.getLocation())) {
//						Location loc = currentPlayer.getLocation();
//						int num = loc.getNumCultists();
//						if(num == 0 || num == 1)
//						{
						if(currentPlayer.getLocation().getNumCultists()==3)
							cultistsToDraw = 1;
						else
							cultistsToDraw = 2;
						prevState = gameState;
						destinations.add(currentPlayer.getLocation());
						gameState = 0;
						vector[0] = destinations.get(0).getX()-1350;
						vector[1] = destinations.get(0).getY()-10;
						double factor = 5/Math.sqrt(Math.pow(vector[0], 2)+Math.pow(vector[1], 2));
						vector[0] *= factor;
						vector[1] *= factor;
						cultX = 1350;
						cultY = 10;
						cultistRoll = true;
					}
					if(currentPlayer.getSanity() <= 0) //Rolled Die. Check for Insantiy
					{
						if(!currentPlayer.getIsInsane())
						{
							currentPlayer.setIsInsane(true);
							currentPlayer.setStartActionNum(currentPlayer.getStartActionNum()-1);
							if(currentPlayer.getActionsRemaining()>0)
								currentPlayer.setActionsRemaining(currentPlayer.getActionsRemaining()-1);
							if(currentPlayer.getName().equals("HUNTER"))
								currentPlayer.setSpecial(false);
						}
						if(allPlayersAreInsane()) {
							gameState = 51;
							reasonForLoss = "All Players are Insane";
							return;
						}
					}
					currentRoll = 0;
					rollsRemaining--;
					if(!cultistRoll) {
						currentPlayer = originalPlayer;
						if(players.get(0).getShoggothContact()>0 || players.get(1).getShoggothContact()>0 || (players.size()>=3 && players.get(2).getShoggothContact()>0) || (players.size()>=4 && players.get(3).getShoggothContact()>0)) {
							gameState=12;
							for(Player p : players)
								if(p.getShoggothContact()>0) {
									currentPlayer = p;
									break;
								}
						}
						else if(rollsRemaining>0)
							gameState=12;
						else if(shoggothDecisionLeft!=0) {
							gameState=13;
						}
						else if(!toBeResolved.isEmpty()) {
							if(toBeResolved.get(toBeResolved.size()-1).getName().equals("hastur")) {
								removeOldOne("hastur");
								shoggothsMove();
								if(rollsRemaining==0 && gameState!=44 && shoggothDecisionLeft==0) {
									if(evilStirs[0]) {
										gameState=17;
										evilStirs[0] = false;
									}
									else if(evilStirs[1]) {
										evilStirs[1] = false;
										gameState = 18;
									}
									else if(evilStirs[2]) {
										evilStirs[2] = false;
										gameState=19;
									}
									else if(!toBeResolved.isEmpty()) {
										if(toBeResolved.get(toBeResolved.size()-1).getName().equals("dagon"))
											gameState = 0;
										else
											gameState = 44;
									}
									else
										gameState = 1;
								}
							}
						}
						else if(cardsLeft>0 || cultistsToDraw>0) {
							gameState=0;
						}
						else {
							if(evilStirs[0]) {
								gameState=17;
								evilStirs[0] = false;
							}
							else if(evilStirs[1]) {
								evilStirs[1] = false;
								gameState = 18;
							}
							else if(evilStirs[2]) {
								evilStirs[2] = false;
								gameState=19;
							}
							else {
								gameState=1;
								tryEndActions();
							}
						}
					}
				}
			}
			if(currentFace==1)
				dieFace = die1;
			else if(currentFace==2)
				dieFace = die2;
			else if(currentFace==3)
				dieFace = die3;
			else if(currentFace==4)
				dieFace = die4;
			else if(currentFace==5)
				dieFace = die5;
			else if(currentFace==6)
				dieFace = die6;
			g.drawImage(dieFace, 0, 250, (int)(die0.getWidth()*0.5), (int)(die0.getHeight()*0.5), null);
			if(wardedBoxActive)
				g.drawImage(cancelIcon, 0, 250, (int)(die0.getWidth()*0.5), (int)(die0.getHeight()*0.5), null);
		}
		
		public void drawPlayerInfo(Graphics g)
		{
			g.setColor(Color.white);
			g.setFont(proper);
			g.drawString("Actions Remaining : "+currentPlayer.getActionsRemaining(), 1435, 20);
			g.drawString("Sanity Remaining : "+currentPlayer.getSanity(), 1435, 40);
//			g.drawString("Current Game State : "+gameState, 60, 20);
			g.drawString("Current Game State : "+gameState, 1435, 60);
			g.drawString("Rolls Remaining : "+rollsRemaining, 1435, 80);
			g.drawString("Cultists Remaining : "+cultistsRemaining, 1435, 100);
			g.drawString("Shoggoths Remaining : "+shoggothsRemaining, 1435, 120);
			g.drawString("Shoggoth Decisions Left : "+shoggothDecisionLeft, 1435, 140);
			g.drawString("Cultist Cards Drawn : "+cultistCardsDrawn, 1750, 20);
			g.drawString("Shoggoth Contact "+currentPlayer.getShoggothContact(), 1750, 40);
			g.drawString("Player Cards Drawn : "+playerCardsDrawn, 1750, 60);
			g.drawString("Summoning Rate : "+summoningRate, 5, 530);
				for(int i=0; i<players.size(); i++) {
					if(players.get(i)==currentPlayer) {
						g.setColor(Color.yellow);
						int x = portraitWidth+portraitSpacing*i;
						for(int j=0; j<players.get(i).getStartActionNum(); j++) {
							g.drawRect(x,742-j*25,20,20);
						}
						for(int k=0; k<players.get(i).getActionsRemaining(); k++) {
							g.fillRect(x,742-k*25, 20, 20);
						}
					}
					for(int m=0; m<players.get(i).getSanity(); m++) {
						g.drawImage(sanity, 10+portraitSpacing*i+m*30, 775, 25, 25, null);
					}
				}
			
			if(gameState==3 && !possibleColors.isEmpty()) {
				for(int i=0; i<players.size(); i++)
					if(currentPlayer==players.get(i)) {
						g.setColor(Color.white);
						g.drawString("Choose Card Color", portraitSpacing*i+20, 624);
						for(int j=0; j<possibleColors.size(); j++) {
							String color = possibleColors.get(j);
							if(color.equals("green"))
								g.setColor(Color.green);
							else if(color.equals("yellow"))
								g.setColor(Color.yellow);
							else if(color.equals("purple"))
								g.setColor(purple);
							else if(color.equals("red"))
								g.setColor(Color.red);
							g.fillRect(200+portraitSpacing*i+35*j+20, 600, 35, 35);
						}
						break;
					}
			}
			else if(gameState==30) {
				g.drawImage(selectedRelic.getImage(),525,77,350,500,null);
				g.drawImage(closeBtn, 839, 76, 40, 40, null);
				g.drawImage(checkBtn, 524, 76, 40, 40, null);
			}
			else if(gameState==16 || gameState==17 || gameState==18 || gameState==19) {
				g.drawImage(playerCardImages.get(4), 525, 77, 350, 500, null);
				g.drawImage(continueBtn, 572, 575, 250, 50, null);
				g.setColor(transYellow);
				if(gameState==16)
					g.fillRect(573, 380, 239, 20);
				else if(gameState==17)
					g.fillRect(573, 400, 230, 20);
				else if(gameState==18)
					g.fillRect(573, 418, 248, 55);
				else if(gameState==19)
					g.fillRect(573, 473, 255, 55);
			}
			else if(gameState==40) {
				g.setColor(Color.white);
				g.setFont(bold);
				if(players.size()>=2) {
					g.drawString(""+loseP1Sanity, portraitSpacing/2, 640);
					if(loseP1Sanity<players.get(0).getSanity())
						g.drawImage(plusBtn, portraitSpacing/2-32, 620, 25, 25, null);
					if(loseP1Sanity>0)
						g.drawImage(minusBtn, portraitSpacing/2+18, 621, 25, 25, null);
					
					g.drawString(""+loseP2Sanity, (portraitSpacing*3)/2, 640);
					if(loseP2Sanity<players.get(1).getSanity())
						g.drawImage(plusBtn, (portraitSpacing*3)/2-32, 620, 25, 25, null);
					if(loseP2Sanity>0)
						g.drawImage(minusBtn, (portraitSpacing*3)/2+18, 621, 25, 25, null);
				}
				if(players.size()>=3) {
					g.drawString(""+loseP3Sanity, (portraitSpacing*5)/2, 640);
					if(loseP3Sanity<players.get(2).getSanity())
						g.drawImage(plusBtn, (portraitSpacing*5)/2-32, 620, 25, 25, null);
					if(loseP3Sanity>0)
						g.drawImage(minusBtn, (portraitSpacing*5)/2+18, 621, 25, 25, null);
				}
				if(players.size()>=4) {
					g.drawString(""+loseP4Sanity, (portraitSpacing*7)/2, 640);
					if(loseP4Sanity<players.get(3).getSanity())
						g.drawImage(plusBtn,(portraitSpacing*7)/2-32, 620, 25, 25, null);
					if(loseP4Sanity>0)
						g.drawImage(minusBtn, (portraitSpacing*7)/2+18, 621, 25, 25, null);
				}
//				g.drawString(""+loseP1Sanity, 288, 640);
//				g.drawString(""+loseP2Sanity, 753, 640);
//				g.drawString(""+loseP3Sanity, 1219, 640);
//				if(loseP1Sanity<players.get(0).getSanity())
//					g.drawImage(plusBtn, 256, 620, 25, 25, null);
//				if(loseP1Sanity>0)
//					g.drawImage(minusBtn, 306, 621, 25, 25, null);
//				if(loseP2Sanity<players.get(1).getSanity())
//					g.drawImage(plusBtn, 722, 620, 25, 25, null);
//				if(loseP2Sanity>0)
//					g.drawImage(minusBtn, 772, 621, 25, 25, null);
//				if(loseP3Sanity<players.get(2).getSanity())
//					g.drawImage(plusBtn, 1188, 620, 25, 25, null);
//				if(loseP3Sanity>0)
//					g.drawImage(minusBtn, 1238, 621, 25, 25, null);
			}
			else if(gameState==44 && !toBeResolved.isEmpty()) {
				g.drawImage(toBeResolved.get(toBeResolved.size()-1).getImage(), 525, 77, 350, 500, null);
				g.drawImage(continueBtn, 572, 575, 250, 50, null);
			}
			else if(gameState==43) {
				if(currentPlayer==players.get(0)) {
					g.drawImage(plusBtn, 230, 620, 25, 25, null);
					g.drawImage(cultist, 255, 615, 40, 40, null);
					if(!currentPlayer.getIsInsane()) {
						g.drawImage(minusBtn, 320, 620, 25, 25, null);
						g.drawImage(sanity, 345, 615, 40, 40, null);
					}
				}
				else if(currentPlayer==players.get(1)) {
					g.drawImage(plusBtn, 705, 620, 25, 25, null);
					g.drawImage(cultist, 730, 615, 40, 40, null);
					if(!currentPlayer.getIsInsane()) {
						g.drawImage(minusBtn, 785, 620, 25, 25, null);
						g.drawImage(sanity, 810, 615, 40, 40, null);
					}
				}
				else if(currentPlayer==players.get(2)) {
					g.drawImage(plusBtn, 1160, 620, 25, 25, null);
					g.drawImage(cultist, 1185, 615, 40, 40, null);
					if(!currentPlayer.getIsInsane()) {
						g.drawImage(minusBtn, 1255, 620, 25, 25, null);
						g.drawImage(sanity, 1280, 615, 40, 40, null);
					}
				}
			}
			else if(gameState==37) {
				g.setColor(Color.black);
				g.fillRect(400, 280, 580, 220);
				g.drawImage(confirmBtn, 600, 498, 180, 60, null);
				for(int i=0; i<4; i++)
					if(playerDeck.size()>i)
						g.drawImage(playerDeck.get(i).getImage(), 410+140*i, 290, 140, 200, null);
			}
			else if(gameState==35) {
				g.setFont(bold);
				g.setColor(Color.white);
				if(currentPlayer==players.get(0)) {
					g.drawString("All For One", 160, 620);
					g.setColor(Color.yellow);
					g.drawString("One For All", 257, 650);
				}
				else if(currentPlayer==players.get(1)) {
					g.drawString("All For One", 626, 620);
					g.setColor(Color.yellow);
					g.drawString("One For All", 723, 650);
				}
				else if(currentPlayer==players.get(2)) {
					g.drawString("All For One", 1092, 620);
					g.setColor(Color.yellow);
					g.drawString("One For All", 1189, 650);
				}
			}
			else if(gameState==15) {
				g.setColor(Color.white);
				if(currentPlayer==players.get(0)) {
					g.drawString("Insanity Cured!", 215, 620);
					g.drawString("Click Hospital or Church", 180, 650);
				}
				else if(currentPlayer==players.get(1)) {
					g.drawString("Insanity Cured!", 660, 620);
					g.drawString("Click Hospital or Church", 625, 650);
				}
				else if(currentPlayer==players.get(2)) {
					g.drawString("Insanity Cured!", 1140, 620);
					g.drawString("Click Hospital or Church", 1105, 650);
				}
			}
			else if(gameState==21) {
				g.setColor(Color.white);
				if(currentPlayer==players.get(0)) {
					g.drawString("Discard Down to 7", 180, 650);
				}
				else if(currentPlayer==players.get(1)) {
					g.drawString("Discard Down to 7", 625, 650);
				}
				else if(currentPlayer==players.get(2)) {
					g.drawString("Discard Down to 7", 1105, 650);
				}
			}
			else if(gameState==34) {
				g.setColor(Color.white);
				g.drawString("Select Two Cards then Click Trade Button", 480, 628);
			}
			else if(gameState==13) {
				g.setColor(Color.white);
				g.drawString("Click Shoggoth's Next Location", 500, 628);
			}
		}
		
		public void drawLight(Graphics g)
		{
			g.setColor(Color.yellow);
			if(gameState==11) {
				for(Location loc : locations)
					if(loc.getIsGate() && loc!=currentPlayer.getLocation() && !((Gate)loc).getIsClosed() && !((Gate)loc).getIsSealed())
						g.fillArc(loc.getX(), loc.getY(), 40, 40, 0, 360);
			}
			else if(gameState==12) {
				g.setColor(transYellow);
				g.fillArc(0, 250, 100, 100, 0, 360);
			}
			else if(gameState==13) {
				if(shog1Loc!=null && shog1Choice1!=null && shog1Choice2!=null) {
					g.fillArc(shog1Choice1.getX(), shog1Choice1.getY(), 40, 40, 0, 360);
					g.fillArc(shog1Choice2.getX(), shog1Choice2.getY(), 40, 40, 0, 360);
				}
				else if(shog2Loc!=null && shog2Choice1!=null && shog2Choice2!=null) {
					g.fillArc(shog2Choice1.getX(), shog2Choice1.getY(), 40, 40, 0, 360);
					g.fillArc(shog2Choice2.getX(), shog2Choice2.getY(), 40, 40, 0, 360);
				}
				else if(shog3Loc!=null && shog3Choice1!=null && shog3Choice2!=null) {
					g.fillArc(shog3Choice1.getX(), shog3Choice1.getY(), 40, 40, 0, 360);
					g.fillArc(shog3Choice2.getX(), shog3Choice2.getY(), 40, 40, 0, 360);
				}
			}
			else if(gameState==32) {
				for(Location loc : locations)
					if(loc.getIsGate() && ((Gate)loc).getIsClosed() && !((Gate)loc).getIsSealed()) {
						g.fillArc(loc.getX(), loc.getY(), 40, 40, 0, 360);
					}
			}
			else if(gameState==4) {
				for(PlayerCard card : currentPlayer.getHand()) {
					for(Location loc : locations) {
						if(currentPlayer.getLocation()!=loc && !currentPlayer.getLocation().getNeighbors().contains(loc.getName()) && (loc.getColor().equals(card.getName()) || currentPlayer.getLocation().getColor().equals(card.getName()))) {
							g.fillArc(loc.getX(), loc.getY(), 40, 40, 0, 360);
						}
					}
				}
			}
			else if(gameState==14) {
				g.setColor(transYellow);
				g.fillRect(0, 351, 105, 150);
			}
			else if(gameState==15) {
				for(Location loc : locations)
					if(loc.getName().equals("church") || loc.getName().equals("hospital"))
						g.fillArc(loc.getX(), loc.getY(), 40, 40, 0, 360);
			}
			else if(gameState==33) {
				g.setColor(transYellow);
				if(greenDiscard>0)
					g.fillRect(1256, 475, 154, 30);
				if(yellowDiscard>0)
					g.fillRect(1256, 507, 154, 30);
				if(purpleDiscard>0)
					g.fillRect(1256, 539, 154, 30);
				if(redDiscard>0)
					g.fillRect(1256, 571, 154, 30);
			}
			else if(gameState==1 && takeDiscard) {
				g.setColor(transYellow);
				if(currentPlayer.getLocation().getColor().equals("green"))
					g.fillRect(1256, 475, 154, 30);
				else if(currentPlayer.getLocation().getColor().equals("yellow"))
					g.fillRect(1256, 507, 154, 30);
				else if(currentPlayer.getLocation().getColor().equals("purple"))
					g.fillRect(1256, 539, 154, 30);
				else if(currentPlayer.getLocation().getColor().equals("red"))
					g.fillRect(1256, 571, 154, 30);
			}
		}
	}
	
//CLICKS CLASS
//Handles all mouse input from user and determines action based on X and Y coordinates
	private class Clicks extends MouseAdapter
	{
		private int mouseX = -10, mouseY = -10;
		private Location selectedLocation;
		private Card selectedCard;
		
		public void mousePressed(MouseEvent e)
		{
			mouseX = e.getX();
			mouseY = e.getY()-212;
			System.out.println("("+mouseX+","+mouseY+")");
			if(e.getButton()==MouseEvent.BUTTON3) {
//				for(PlayerCard card : possibleCards) {
//					System.out.print(card.getName()+"\t");
//				}
//				System.out.println();
//				for(Player p : players) {
//					System.out.println(p.getLocation().getName());
//				}
//				for(Player p : players)
//					for(PlayerCard card : p.getHand())
//						if(card.getName().contains("relic"))
//							System.out.println(card.getIsSelected());
				pan.getScroll().getViewport().setViewPosition(new Point(0,209));
//				if(sealedOldOne!=null)
//					System.out.println(sealedOldOne.getName());
//				for(OldOne old : toBeResolved) {
//					System.out.print(old.getName()+"\t");
//				}
//				System.out.println();
//				System.out.println("GameState : "+gameState + "\t"+"Previous State : "+prevState);
//				System.out.println(pan.cultistsToDraw+"\t"+pan.shoggothSize+"\t"+pan.cardsLeft);
//				for(String str : possibleColors)
//					System.out.print(str+"\t");
//				System.out.println();
				for(int i=0; i<playerSelection.length; i++)
					System.out.print(playerSelection[i]+"\t");
				System.out.println();
				
				if(!players.isEmpty())
					for(Player p : players)
						System.out.print(p.getName()+"\t");
				System.out.println();
				System.out.println(playerCount);
//				return;
			}
//			for(Location loc : pan.destinations)
//				System.out.print(loc.getName());
//			System.out.println();
//			if(pan.destination!=null)
//				System.out.println(pan.destination.getName());

//Choose Investigator
			if(gameState==-1) {
				for(int i=0; i<playerSelection.length; i++) {
					if(playerSelection[i]>=0 && mouseX>(139+i*356) && mouseX<(219+i*356)) {
						if(mouseY>40 && mouseY<120) {
							changePlayer(i,1);
						}
						else if(mouseY>480 && mouseY<560) {
							changePlayer(i,-1);
						}
						break;
					}
					else if(mouseX>(264+i*356) && mouseX<(304+i*356) && mouseY>440 && mouseY<480) {
						if(i==2 && playerCount==3 && playerSelection[3]==-1) {
							playerCount--;
							playerSelection[2] = -1;
						}
						else if(i==2 && playerCount==2 && playerSelection[2]==-1) {
							playerCount++;
							int temp = 0;
							while(hasPlayer(temp))
								temp++;
							playerSelection[2] = temp;
						}
						else if(i==3 && playerCount==3 && playerSelection[2]>=0) {
							playerCount++;
							int temp = 0;
							while(hasPlayer(temp))
								temp++;
							playerSelection[3] = temp;
						}
						else if(i==3 && playerCount==4 && playerSelection[3]>=0) {
							playerCount--;
							playerSelection[3] = -1;
						}
						break;
					}
				}
				if(mouseX>560 && mouseX<860 && mouseY>700 && mouseY<800) {
					gameState = 1;
					for(int i=0; i<playerSelection.length; i++) {
						if(playerSelection[i]>=0) {
							players.add((Player) investigatorDeck.get(playerSelection[i]));
						}
					}
					
					portraitSpacing = 1420/players.size();
					portraitWidth = 136-5*players.size();
					cardSize = 96-2*players.size();
					
					for(Player p : players)
						locations.get(0).getPlayerList().add(p);
					gameState=0;
					currentPlayer = players.get(0);
					playerCard = new PlayerCard();
					playerDeck = playerCard.getDeck();
					addRelics();
					shuffleDeck(playerDeck);
					dealHands();
					ArrayList<Card> newDeck = new ArrayList<Card>(playerDeck.size()+4);
					addEvilStirs(newDeck,0,playerDeck.size()-1);
					playerDeck = newDeck;
					boardSetUp = true;
				}
//				if(mouseX>340 && mouseX<690 && mouseY>250 && mouseY<750) {
//					players.add((Player) investigatorDeck.get(0));
//					investigatorDeck.remove(0);
//				}
//				else if(mouseX>790 && mouseX<1140 && mouseY>250 && mouseY<750) {
//					players.add((Player) investigatorDeck.get(1));
//					investigatorDeck.remove(1);
//				}
//				if(players.size()==3) {
//					for(Player p : players)
//						locations.get(0).getPlayerList().add(p);
//					gameState=0;
//					currentPlayer = players.get(0);
//					playerCard = new PlayerCard();
//					playerDeck = playerCard.getDeck();
//					addRelics();
//					shuffleDeck(playerDeck);
//					dealHands();
//					ArrayList<Card> newDeck = new ArrayList<Card>(playerDeck.size()+4);
//					addEvilStirs(newDeck,0,playerDeck.size()-1);
//					playerDeck = newDeck;
//					boardSetUp = true;
//				}
				return;
			}
			
//Confirm Relic
			if(gameState==30) {
				if(mouseX>839 && mouseX<879 && mouseY>76 && mouseY<116) {
					gameState=prevState;
					currentPlayer = originalPlayer;
				}
				else if(mouseX>524 && mouseX<564 && mouseY>76 && mouseY<116) {
					if(prevState!=14 || !selectedRelic.getName().equals("relic1")) {
						boolean can = true;
						if(gameState==14 && selectedRelic.getName().equals("relic9")) {
							for(Player p : players)
								if(p.getIsInsane()) {
									can = false;
									break;
								}
						}
						if(can) {
							currentPlayer.getHand().remove(selectedRelic);
							playerDiscard.add(0,selectedRelic);
							useRelic(selectedRelic);
							if(!wardedBoxActive && !(currentPlayer.getIsInsane() && currentPlayer.getName().equals("MAGICIAN")))
								rollsRemaining++;
						}
					}
					return;
				}
			}
//Enlarge Relic
			if(e.getButton()==MouseEvent.BUTTON3 && (gameState==1 || gameState==12 || gameState==13 || gameState==14 || gameState==21)) {
				for(int i=0; i<players.size(); i++) {
					for(int j=0; j<players.get(i).getHand().size(); j++) {
						PlayerCard card = players.get(i).getHand().get(j);
						int gap = 250/(players.get(i).getHand().size());
						if(players.get(i).getHand().size()<3)
							gap = 91;
						
						if(card.getName().contains("relic") && ((j==players.get(i).getHand().size()-1 && mouseX>155+portraitSpacing*i+j*gap && mouseX<155+portraitSpacing*i+j*gap+91 && mouseY>670 && mouseY<800) 
								|| (mouseX>155+portraitSpacing*i+j*gap && mouseX<155+portraitSpacing*i+(j+1)*gap && mouseY>670 && mouseY<800))) {
							if((!card.getName().equals("relic1") || gameState!=14) && (players.get(i)==currentPlayer || !sothothActive)) {
								currentPlayer = players.get(i);
								prevState=gameState;
								gameState=30;
								selectedRelic = card;
								return;
							}
						}
					}
				}
			}
/*EvilStirs*/
			if(gameState==16 || gameState==17 || gameState==18 || gameState==19) {
				if(mouseX>572 && mouseX<822 && mouseY>575 && mouseY<625) {
					evilStirs();
					return;
				}
			}
/*Last Hourglass*/
			if(gameState==33) {
				if(mouseX>1256 && mouseX<1410) {
					if(mouseY>475 && mouseY<505) {
						for(Card card : playerDiscard)
							if(card.getName().equals("green")) {
								currentPlayer.getHand().add((PlayerCard) card);
								playerDiscard.remove(card);
								break;
							}
					}
					else if(mouseY>507 && mouseY<537) {
						for(Card card : playerDiscard)
							if(card.getName().equals("yellow")) {
								currentPlayer.getHand().add((PlayerCard) card);
								playerDiscard.remove(card);
								break;
							}
					}
					else if(mouseY>539 && mouseY<569) {
						for(Card card : playerDiscard)
							if(card.getName().equals("purple")) {
								currentPlayer.getHand().add((PlayerCard) card);
								playerDiscard.remove(card);
								break;
							}
					}
					else if(mouseY>571 && mouseY<601) {
						for(Card card : playerDiscard)
							if(card.getName().equals("red")) {
								currentPlayer.getHand().add((PlayerCard) card);
								playerDiscard.remove(card);
								break;
							}
					}
					gameState = prevState;
					if(rollsRemaining>0)
						gameState=12;
					else
						currentPlayer = originalPlayer;
					resetPlayerDiscard();
				}
			}
/*Song of Kadath*/
			else if(gameState==35) {
				for(int i=0; i<players.size(); i++)
					if(currentPlayer==players.get(i)) {
						if(mouseX>160+portraitSpacing*i && mouseX<305+portraitSpacing*i && mouseY>600 && mouseY<620) {
							gameState = 22;
						}
						else if(mouseX>257+portraitSpacing*i && mouseX<402+portraitSpacing*i && mouseY>630 && mouseY<650) {
							for(Player p : players) {
								if(p.getIsInsane()) {
									p.setIsInsane(false);
									p.setStartActionNum(currentPlayer.getStartActionNum()+1);
									p.setSanity(1);
									if(currentPlayer==p)
										p.setActionsRemaining(p.getActionsRemaining()+1);
									if(currentPlayer.getName().equals("HUNTER"))
										currentPlayer.setSpecial(true);
								}
								else {
									if(p.getSanity()<4)
										p.setSanity(p.getSanity()+1);
								}
							}
							gameState = prevState;
							if(rollsRemaining>0)
								gameState = 12;
							else
								currentPlayer = originalPlayer;
						}
						break;
					}
			}
			else if(gameState==22) {
				Player selectedPlayer = trySelectPlayer(mouseX,mouseY);
				
				if(selectedPlayer!=null) {
					if(selectedPlayer.getIsInsane()) {
						selectedPlayer.setIsInsane(false);
						selectedPlayer.setStartActionNum(currentPlayer.getStartActionNum()+1);
						if(currentPlayer==selectedPlayer)
							selectedPlayer.setActionsRemaining(selectedPlayer.getActionsRemaining()+1);
						if(currentPlayer.getName().equals("HUNTER"))
							currentPlayer.setSpecial(true);
					}
					selectedPlayer.setSanity(4);
					gameState = prevState;
					if(rollsRemaining>0)
						gameState = 12;
					else 
						currentPlayer = originalPlayer;
				}
			}
/*Elder Sign*/
			else if(gameState==32) {
				for(Location loc : locations)
					if(loc.getIsGate() && ((Gate)loc).getIsClosed() && !((Gate)loc).getIsSealed() && mouseX>loc.getX() && mouseX<loc.getX()+40 && mouseY>loc.getY() && mouseY<loc.getY()+40) {
						((Gate)loc).setSealed(true);
						gameState = prevState;
						if(rollsRemaining>0)
							gameState = 12;
						else
							currentPlayer = originalPlayer;
					}
			}
/*Xaos Mirror*/
			else if(gameState==34) {
				if(tradeBtn.contains(mouseX,mouseY)) {
					ArrayList<PlayerCard> selectedCards = new ArrayList<PlayerCard>();
					Player other = null;
					boolean can = false;
					for(Player p : players) {
						for(PlayerCard card : p.getHand())
							if(card.getIsSelected()) {
								if(p!=currentPlayer) {
									selectedCards.add(0,card);
									other = p;
								}
								else {
									selectedCards.add(card);
									can = true;
								}
							}
					}
					for(PlayerCard card : selectedCards)
						System.out.println(card.getName());
					if(selectedCards.size()==2 && can) {
						currentPlayer.getHand().set(currentPlayer.getHand().indexOf(selectedCards.get(1)), selectedCards.get(0));
						if(other!=null)
							other.getHand().set(other.getHand().indexOf(selectedCards.get(0)), selectedCards.get(1));
						selectedCards.get(0).setSelected(false);
						selectedCards.get(1).setSelected(false);
						gameState = prevState;
						if(rollsRemaining>0)
							gameState=12;
						else
							currentPlayer = originalPlayer;
					}
				}
				else {
					trySelectCard(mouseX,mouseY);
				}
			}
/*Alhazred's Flame*/
			else if(gameState==36) {
				for(Location loc : locations)
					if(mouseX>loc.getX() && mouseX<loc.getX()+40 && mouseY>loc.getY() && mouseY<loc.getY()+40) {
						if(loc.getNumShoggoths()>0 && roastedCultists==0) {
							shoggothsRemaining++;
							loc.setNumShoggoths(loc.getNumShoggoths()-1);
							gameState = prevState;
							if(rollsRemaining>0)
								gameState=12;
							else 
								currentPlayer = originalPlayer;
							break;
						}
						else if(loc.getNumCultists()>0) {
							cultistsRemaining++;
							loc.setNumCultists(loc.getNumCultists()-1);
							roastedCultists++;
							break;
						}
					}
				boolean noTarget = true;
				for(Location loc : locations)
					if(loc.getNumCultists()>0 || loc.getNumShoggoths()>0) {
						noTarget = false;
						break;
					}
				if(roastedCultists==4 || noTarget) {
					gameState = prevState;
					if(rollsRemaining>0)
						gameState=12;
					else
						currentPlayer = originalPlayer;
				}
			}
/*Seal of Leng*/
			else if(gameState==38) {
				for(int i=0; i<oldOneDeck.size(); i++) {
					if(mouseX>100+180*i && mouseX<100+180*i+140 && mouseY>-210 && mouseY<-10)
						if(((OldOne) oldOneDeck.get(i)).getIsActive()) {
							boolean used = false;
							if(oldOneDeck.get(i).getName().equals("ithaqua")) {
								used = true;
								ithaquaActive = false;
								sealedOldOne = (OldOne) oldOneDeck.get(i);
							}
							else if(oldOneDeck.get(i).getName().equals("yog-sothoth")) {
								used = true;
								sothothActive = false;
								sealedOldOne = (OldOne) oldOneDeck.get(i);
							}
							else if(oldOneDeck.get(i).getName().equals("yig")) {
								used = true;
								yigActive = false;
								sealedOldOne = (OldOne) oldOneDeck.get(i);
							}
							else if(oldOneDeck.get(i).getName().equals("nyarlathotep")) {
								used = true;
								nyarlathotepActive = false;
								sealedOldOne = (OldOne) oldOneDeck.get(i);
							}
							else if(oldOneDeck.get(i).getName().equals("azathoth")) {
								used = true;
								azathothActive = false;
								cultistsRemaining = cultistsRemaining + 3;
								sealedOldOne = (OldOne) oldOneDeck.get(i);
							}
							if(used) {
								gameState = prevState;
								if(rollsRemaining>0)
									gameState=12;
								else
									currentPlayer = originalPlayer;
								pan.getScroll().getViewport().setViewPosition(new Point(0,209));
							}
							break;
						}
				}
			}
/*Silver Key*/
			else if(gameState==31) {
				for(Location loc : locations)
					if(mouseX>loc.getX() && mouseX<loc.getX()+40 && mouseY>loc.getY() && mouseY<loc.getY()+40) {
						originalPlayer.getLocation().getPlayerList().remove(originalPlayer);
						loc.getPlayerList().add(originalPlayer);
						originalPlayer.setLocation(loc);
						gameState = prevState;
						if(wardedBoxActive)
							rollsRemaining += loc.getNumShoggoths();
						if(rollsRemaining>0)
							gameState=12;
						else 
							currentPlayer = originalPlayer;
						break;
					}
			}
/*Book of Shadows*/
			else if(gameState==37) {
				if(mouseX>600 && mouseX<780 && mouseY>498 && mouseY<558) {
					gameState=prevState;
					if(rollsRemaining>0)
						gameState=12;
					else 
						currentPlayer = originalPlayer;
				}
				else {
					for(int i=0; i<4; i++)
						if(mouseX>410+140*i && mouseX<410+140*(i+1) && mouseY>290 && mouseY<490) {
							selectedCard = playerDeck.get(i);
							break;
						}
				}
			}
/*Old One Confirm*/
			else if(gameState==44) {
				if(mouseX>572 && mouseX<822 && mouseY>575 && mouseY<625) {
					activateOldOne(toBeResolved.get(toBeResolved.size()-1));
					if(gameState!=43 && !toBeResolved.isEmpty() && toBeResolved.get(toBeResolved.size()-1).getName().equals("atlach-nacha")) {
//NextPlayer
						if(players.indexOf(currentPlayer) < players.size()-1)
							currentPlayer = players.get(players.indexOf(currentPlayer)+1);
						else if (players.indexOf(currentPlayer)==players.size()-1)
							currentPlayer = players.get(0);
						if(currentPlayer==originalPlayer) {
							removeOldOne("atlach-nacha");
							
							if(toBeResolved.isEmpty()) {
								if(evilStirs[0]) {
									gameState=17;
									evilStirs[0] = false;
								}
								else if(evilStirs[1]) {
									evilStirs[1] = false;
									gameState = 18;
								}
								else if(evilStirs[2]) {
									evilStirs[2] = false;
									gameState=19;
								}
								else
									gameState=prevState;
							}
							else if(!toBeResolved.isEmpty() && toBeResolved.get(toBeResolved.size()-1).getName().equals("dagon"))
								gameState = 0;
							else
								gameState=44;
						}
						else gameState=43;
					}
					else if(toBeResolved.isEmpty() && gameState!=0 && gameState!=41 && gameState!=40 && gameState!=43) {
						if(boardSetUp) {
							gameState=0;
						}
						else if(evilStirs[0]) {
							gameState = 17;
							evilStirs[0] = false;
						}
						else if(evilStirs[1]) {
							gameState = 18;
							evilStirs[1] = false;
						}
						else if(rollsRemaining>0)
							gameState = 12;
						else
							gameState=prevState;
					}
				}
			}
/*Shudde Mell*/
			else if(gameState==40) {
				if(mouseY>620 && mouseY<645) {
					if(mouseX>portraitSpacing/2-32 && mouseX<portraitSpacing/2-7 && loseP1Sanity<players.get(0).getSanity())
						loseP1Sanity++;
					else if(mouseX>portraitSpacing/2+18 && mouseX<portraitSpacing/2+43 && loseP1Sanity>0)
						loseP1Sanity--;
					else if(mouseX>portraitSpacing*3/2-32 && mouseX<portraitSpacing*3/2-7 && loseP2Sanity<players.get(1).getSanity())
						loseP2Sanity++;
					else if(mouseX>portraitSpacing*3/2+18 && mouseX<portraitSpacing*3/2+43 && loseP2Sanity>0)
						loseP2Sanity--;
					else if(players.size()>=3 && mouseX>portraitSpacing*5/2-32 && mouseX<portraitSpacing*5/2-7 && loseP3Sanity<players.get(2).getSanity())
						loseP3Sanity++;
					else if(players.size()>=3 && mouseX>portraitSpacing*5/2+18 && mouseX<portraitSpacing*5/2+43 && loseP3Sanity>0)
						loseP3Sanity--;
					else if(players.size()>=4 && mouseX>portraitSpacing*7/2-32 && mouseX<portraitSpacing*7/2-7 && loseP4Sanity<players.get(3).getSanity())
						loseP4Sanity++;
					else if(players.size()>=4 && mouseX>portraitSpacing*7/2+18 && mouseX<portraitSpacing*7/2+43 && loseP4Sanity>0)
						loseP4Sanity--;
				}
				if(players.size()+1==(loseP1Sanity + loseP2Sanity + loseP3Sanity + loseP4Sanity)) {
					players.get(0).setSanity(players.get(0).getSanity()-loseP1Sanity);
					players.get(1).setSanity(players.get(1).getSanity()-loseP2Sanity);
					if(players.size()>=3)
						players.get(2).setSanity(players.get(2).getSanity()-loseP3Sanity);
					if(players.size()>=4)
						players.get(3).setSanity(players.get(3).getSanity()-loseP4Sanity);
					removeOldOne("shudde mell");
					if(allPlayersAreInsane()) {
						gameState = 51;
						reasonForLoss = "All Players are Insane";
						return;
					}
					if(toBeResolved.isEmpty()) {
						if(boardSetUp) {
							gameState=0;
						}
						else if(evilStirs[0]) {
							gameState=17;
							evilStirs[0] = false;
						}
						else if(evilStirs[1]) {
							evilStirs[1] = false;
							gameState = 18;
						}
						else if(evilStirs[2]) {
							evilStirs[2] = false;
							gameState=19;
						}
						else
							gameState=prevState;
					}
					else if(!toBeResolved.isEmpty() && toBeResolved.get(toBeResolved.size()-1).getName().equals("dagon"))
						gameState = 0;
					else
						gameState=44;
				}
				for(Player p : players)
					if(p.getSanity() <= 0) //Check for Insantiy
					{
						p.setIsInsane(true);
						p.setStartActionNum(p.getStartActionNum()-1);
						if(p.getActionsRemaining()>0)
							p.setActionsRemaining(p.getActionsRemaining()-1);
						if(p.getName().equals("HUNTER"))
							p.setSpecial(false);
						if(allPlayersAreInsane()) {
							gameState = 51;
							return;
						}
					}
			}
/*Tsathoggua*/
			else if(gameState==41) {
				trySelectCard(mouseX,mouseY);
				int counter = 0;
				for(Player p : players)
					for(PlayerCard card : p.getHand())
						if(card.getIsSelected())
							counter++;
				if(counter==players.size()) {
					for(Player p : players)
						for(int i=0; i<p.getHand().size(); i++)
							if(p.getHand().get(i).getIsSelected()) {
								playerDiscard.add(0, p.getHand().get(i));
								p.getHand().remove(i);
								i--;
							}
					resetPlayerDiscard();
					removeOldOne("tsathoggua");
					if(toBeResolved.isEmpty()) {
						if(boardSetUp) {
							gameState=0;
						}
						else if(evilStirs[0]) {
							gameState=17;
							evilStirs[0] = false;
						}
						else if(evilStirs[1]) {
							evilStirs[1] = false;
							gameState = 18;
						}
						else if(evilStirs[2]) {
							evilStirs[2] = false;
							gameState=19;
						}
						else
							gameState=prevState;
					}
					else if(!toBeResolved.isEmpty() && toBeResolved.get(toBeResolved.size()-1).getName().equals("dagon"))
						gameState = 0;
					else
						gameState=44;
				}
			}
/*Atlach-Nacha*/
			else if(gameState==43) {
				if(mouseY>620 && mouseY<645) {
					if((mouseX>230 && mouseX<255 && currentPlayer==players.get(0)) 
						|| (mouseX>705 && mouseX<730 && currentPlayer==players.get(1)) || (mouseX>1160 && mouseX<1185 && currentPlayer==players.get(2))) {
						if(!gateSealed(currentPlayer.getLocation())) {
							if(currentPlayer.getLocation().getNumCultists()==3) {
								triggerOldOne();
								System.out.println("Old One Awakening from Cultists: Atlach-Nacha");
							}
							else {
								currentPlayer.getLocation().setNumCultists(currentPlayer.getLocation().getNumCultists()+1);
								cultistsRemaining--;
							}
						}
						
						if(currentPlayer==players.get(0))
							currentPlayer = players.get(1);
						else if(currentPlayer==players.get(1))
							currentPlayer = players.get(2);
						else if(currentPlayer==players.get(2))
							currentPlayer = players.get(0);
						
						if(currentPlayer==originalPlayer) {
							removeOldOne("atlach-nacha");
							if(toBeResolved.isEmpty()) {
								if(boardSetUp) {
									gameState=0;
								}
								else if(evilStirs[0]) {
									gameState=17;
									evilStirs[0] = false;
								}
								else if(evilStirs[1]) {
									evilStirs[1] = false;
									gameState = 18;
								}
								else if(evilStirs[2]) {
									evilStirs[2] = false;
									gameState=19;
								}
								else
									gameState=prevState;
							}
							else if(!toBeResolved.isEmpty() && toBeResolved.get(toBeResolved.size()-1).getName().equals("dagon"))
								gameState = 0;
							else
								gameState = 44;
						}
					}
					else if((mouseX>320 && mouseX<345 && currentPlayer==players.get(0)) 
						|| (mouseX>785 && mouseX<810 && currentPlayer==players.get(1)) || (mouseX>1255 && mouseX<1280 && currentPlayer==players.get(2))) {
						if(currentPlayer.getSanity()>0) {
							currentPlayer.setSanity(currentPlayer.getSanity()-1);
							if(currentPlayer.getSanity() <=0 && !currentPlayer.getIsInsane()) {
								currentPlayer.setIsInsane(true);
								currentPlayer.setStartActionNum(currentPlayer.getStartActionNum()-1);
								if(currentPlayer.getActionsRemaining()>0)
									currentPlayer.setActionsRemaining(currentPlayer.getActionsRemaining()-1);
								if(currentPlayer.getName().equals("HUNTER"))
									currentPlayer.setSpecial(false);
							}
							if(allPlayersAreInsane()) {
								gameState = 51;
								reasonForLoss = "All Players are Insane";
								return;
							}
							
							if(currentPlayer==players.get(0))
								currentPlayer = players.get(1);
							else if(currentPlayer==players.get(1))
								currentPlayer = players.get(2);
							else if(currentPlayer==players.get(2))
								currentPlayer = players.get(0);
							
							if(currentPlayer==originalPlayer) {
								removeOldOne("atlach-nacha");
								if(toBeResolved.isEmpty()) {
									if(boardSetUp) {
										gameState=0;
									}
									else if(evilStirs[0]) {
										gameState=17;
										evilStirs[0] = false;
									}
									else if(evilStirs[1]) {
										evilStirs[1] = false;
										gameState = 18;
									}
									else if(evilStirs[2]) {
										evilStirs[2] = false;
										gameState=19;
									}
									else
										gameState=prevState;
								}
								else if(!toBeResolved.isEmpty() && toBeResolved.get(toBeResolved.size()-1).getName().equals("dagon"))
									gameState = 0;
								else
									gameState=44;
							}
						}
					}
				}
			}
/*Draw Card Click*/
			else if(gameState==14) {
				if(mouseX>0 && mouseX<105 && mouseY>351 && mouseY<501) {
					drawCard();
					if(gameState!=16 && playerCardsDrawn==2) {
						gameState = 1;
						tryEndActions();
					}
				}
			}
/*Church or Hospital Decision Click*/
			else if(gameState==15) {
				for(Location loc : locations) 
					if((loc.getName().equals("church") || loc.getName().equals("hospital")) && mouseX>loc.getX() && mouseX<loc.getX()+40 && mouseY>loc.getY() && mouseY<loc.getY()+40) {
						currentPlayer.getLocation().getPlayerList().remove(currentPlayer);
						loc.getPlayerList().add(currentPlayer);
						currentPlayer.setIsInsane(false);
						currentPlayer.setLocation(loc);
						currentPlayer.setStartActionNum(currentPlayer.getStartActionNum()+1);
						currentPlayer.setSanity(4);
						currentPlayer.setActionsRemaining(currentPlayer.getActionsRemaining()+1);
						if(currentPlayer.getName().equals("HUNTER"))
							currentPlayer.setSpecial(true);
						gameState = 1;
						break;
					}
			}
			else if(gameState==2) { //Choose Player Trade
				Player selectedPlayer = trySelectPlayer(mouseX, mouseY);
				if(selectedPlayer!=null) {
					System.out.println(selectedPlayer.getName());
					for(PlayerCard card : currentPlayer.getHand())
						if(card.getIsSelected() & (card.getName().equals(currentPlayer.getLocation().getColor()) || card.getName().contains("relic"))) {
							currentPlayer.getHand().remove(card);
							selectedPlayer.getHand().add(card);
							card.setSelected(false);
							if(selectedPlayer.getName().equals("DETECTIVE") && selectedPlayer.getIsInsane() && currentPlayer.getActionsRemaining()>1)
								currentPlayer.setActionsRemaining(currentPlayer.getActionsRemaining()-2);
							else if(!selectedPlayer.getName().equals("MAGICIAN"))
								currentPlayer.setActionsRemaining(currentPlayer.getActionsRemaining()-1);
							gameState=1;
							break;
						}
				}
			}
			else if(gameState==4) { //Choose Bus Location
				if(busBtn.contains(mouseX,mouseY))
					gameState=1;
				else
					for(Location loc : locations) {
						if(mouseX >= loc.getX() && mouseX <= loc.getX()+40 && mouseY >= loc.getY() && mouseY <= loc.getY() + 40) {
							selectedLocation = loc;
							for(PlayerCard card : currentPlayer.getHand()) {
								if(currentPlayer.getLocation()!=loc && !currentPlayer.getLocation().getNeighbors().contains(loc.getName()) && (loc.getColor().equals(card.getName()) || currentPlayer.getLocation().getColor().equals(card.getName()))) {
									if(card.getIsSelected()) {
										boolean can = true;
										for(int i=currentPlayer.getHand().indexOf(card); i<currentPlayer.getHand().size(); i++) {
											if(currentPlayer.getHand().get(i).getIsSelected()) {
												can = false;
												break;
											}
										}
										if(can) {
											playerDiscard.add(0,card);
											currentPlayer.getHand().remove(card);
											resetPlayerDiscard();
											currentPlayer.getLocation().getPlayerList().remove(currentPlayer);
											loc.getPlayerList().add(currentPlayer);
											currentPlayer.setLocation(loc);
											currentPlayer.setActionsRemaining(currentPlayer.getActionsRemaining()-1);
											possibleColors.clear();
											
/*HunterLocEnter*/							if(!hunterHasRolled && currentPlayer.getName().equals("HUNTER") && currentPlayer.getIsInsane() && currentPlayer.getLocation().getNumCultists()==0) {
												hunterNeedRoll = true;
												for(Player p : players) 
													if(p.getName().equals("HUNTER"))
														currentPlayer=p;
												gameState=12;
											}
	
/*CheckShoggoth*/							if(currentPlayer.getLocation().getNumShoggoths()!=0 && !wardedBoxActive) {
												gameState=12;
												rollsRemaining += currentPlayer.getLocation().getNumShoggoths();
											}
											tryEndActions();
											return;
										}
									}
									if(!possibleColors.contains(card.getName()) && possibleColors.size()<2)
										possibleColors.add(card.getName());
								}
							}
							if(possibleColors.size()>1) {
								gameState=3;
							}
							else if(possibleColors.size()==1) {
								for(PlayerCard card : currentPlayer.getHand())
									if(card.getName().equals(possibleColors.get(0))) {
										playerDiscard.add(0,card);
										currentPlayer.getHand().remove(card);
										resetPlayerDiscard();
										break;
									}
								currentPlayer.getLocation().getPlayerList().remove(currentPlayer);
								loc.getPlayerList().add(currentPlayer);
								currentPlayer.setLocation(loc);
								currentPlayer.setActionsRemaining(currentPlayer.getActionsRemaining()-1);
								possibleColors.clear();
								
								gameState=1;
/*HunterLocEnter*/				if(!hunterHasRolled && currentPlayer.getName().equals("HUNTER") && currentPlayer.getIsInsane() && currentPlayer.getLocation().getNumCultists()==0) {
									hunterNeedRoll = true;
									for(Player p : players) 
										if(p.getName().equals("HUNTER"))
											currentPlayer=p;
									gameState=12;
								}
								
/*CheckShoggoth*/				if(currentPlayer.getLocation().getNumShoggoths()!=0 && !wardedBoxActive) {
									gameState=12;
									rollsRemaining += currentPlayer.getLocation().getNumShoggoths();
								}
							}
							break;
						}
					}
			}
			else if(gameState==3) { //Choose Card Color
				PlayerCard card = trySelectCard(mouseX,mouseY);
				if(card!=null && !possibleColors.isEmpty()) {
					for(String col : possibleColors) {
						if(col.equals(card.getName())) {
							if(currentPlayer.getLocation().getIsBus()) {
								currentPlayer.getHand().remove(card);
								playerDiscard.add(0,card);
								resetPlayerDiscard();
								currentPlayer.getLocation().getPlayerList().remove(currentPlayer);
								selectedLocation.getPlayerList().add(currentPlayer);
								currentPlayer.setLocation(selectedLocation);
								currentPlayer.setActionsRemaining(currentPlayer.getActionsRemaining()-1);
								possibleColors.clear();
								
/*HunterLocEnter*/				if(!hunterHasRolled && currentPlayer.getName().equals("HUNTER") && currentPlayer.getIsInsane() && currentPlayer.getLocation().getNumCultists()==0) {
									hunterNeedRoll = true;
									for(Player p : players) 
										if(p.getName().equals("HUNTER"))
											currentPlayer=p;
									gameState=12;
								}
								
								if(currentPlayer.getLocation().getNumShoggoths()!=0 && !wardedBoxActive) {
									gameState=12;
									rollsRemaining += currentPlayer.getLocation().getNumShoggoths();
								}
								else 
									gameState=1;
							}
							else if(currentPlayer.getLocation().getIsGate() && yigActive) {
								if(possibleColors.contains(currentPlayer.getLocation().getColor())) {
									possibleColors.remove(card.getName());
									if(!card.getName().equals(currentPlayer.getLocation().getColor()))
										possibleColors.remove(currentPlayer.getLocation().getColor());
									savedCard = card;
								}
								else if(possibleColors.size()==2){
									currentPlayer.getHand().remove(card);
									possibleColors.remove(card.getName());
									playerDiscard.add(0,card);
									resetPlayerDiscard();
								}
								
								if(possibleColors.size()==1) {
									int counter = 0;
/*CloseGate*/						if(currentPlayer.getName().equals("DETECTIVE"))
										counter = 4;
									else
										counter = 5;
									if(savedCard !=null && savedCard.getName().equals(currentPlayer.getLocation().getColor()))
										counter--;
									
									for(int i=0; i<currentPlayer.getHand().size(); i++) {
										if(counter>0 && currentPlayer.getHand().get(i)!=savedCard && currentPlayer.getHand().get(i).getName().equals(currentPlayer.getLocation().getColor())) {
											playerDiscard.add(currentPlayer.getHand().remove(i));
											counter--;
											i--;
										}
									}
									resetPlayerDiscard();
									if(savedCard!=null) {
										savedCard = null;
										miGoEyeActive = false;
									}
									possibleColors.clear();
									closeGate((Gate)currentPlayer.getLocation());
									currentPlayer.setActionsRemaining(currentPlayer.getActionsRemaining()-1);
									if(((Gate)locations.get(5)).getIsClosed() && ((Gate)locations.get(11)).getIsClosed() && ((Gate)locations.get(17)).getIsClosed() && ((Gate)locations.get(23)).getIsClosed()) {
										gameState = 42;
										return;
									}
									else
										gameState=1;
									
									if(currentPlayer.getIsInsane())
										gameState = 15;
								}	
							}
							break;
						}
					}
				}
			}
//Click Shoggoth Decision			
			else if(gameState==13) { //GameState = 13
				Location decision = null;
				if(shog1Loc!=null && shog1Choice1!=null && shog1Choice2!=null) {
					if(mouseX>shog1Choice1.getX() && mouseX<shog1Choice1.getX()+40 && mouseY>shog1Choice1.getY() && mouseY<shog1Choice1.getY()+40) {
						shog1Choice1.setNumShoggoths(shog1Choice1.getNumShoggoths()+1);
						shog1Loc.setNumShoggoths(shog1Loc.getNumShoggoths()-1);
						shoggothDecisionLeft--;
						decision = shog1Choice1;
						shog1Loc = null; shog1Choice1 = null; shog1Choice2 = null;
					}
					else if(mouseX>shog1Choice2.getX() && mouseX<shog1Choice2.getX()+40 && mouseY>shog1Choice2.getY() && mouseY<shog1Choice2.getY()+40) {
						shog1Choice2.setNumShoggoths(shog1Choice2.getNumShoggoths()+1);
						shog1Loc.setNumShoggoths(shog1Loc.getNumShoggoths()-1);
						shoggothDecisionLeft--;
						decision = shog1Choice2;
						shog1Loc = null; shog1Choice1 = null; shog1Choice2 = null;
					}
				}
				else if(shog2Loc!=null && shog2Choice1!=null && shog2Choice2!=null) {
					if(mouseX>shog2Choice1.getX() && mouseX<shog2Choice1.getX()+40 && mouseY>shog2Choice1.getY() && mouseY<shog2Choice1.getY()+40) {
						shog2Choice1.setNumShoggoths(shog2Choice1.getNumShoggoths()+1);
						shog2Loc.setNumShoggoths(shog2Loc.getNumShoggoths()-1);
						shoggothDecisionLeft--;
						decision = shog2Choice1;
						shog2Loc = null; shog2Choice1 = null; shog2Choice2 = null;
					}
					else if(mouseX>shog2Choice2.getX() && mouseX<shog2Choice2.getX()+40 && mouseY>shog2Choice2.getY() && mouseY<shog2Choice2.getY()+40) {
						shog2Choice2.setNumShoggoths(shog2Choice2.getNumShoggoths()+1);
						shog2Loc.setNumShoggoths(shog2Loc.getNumShoggoths()-1);
						shoggothDecisionLeft--;
						decision = shog2Choice2;
						shog2Loc = null; shog2Choice1 = null; shog2Choice2 = null;
					}
				}
				else if(shog3Loc!=null && shog3Choice1!=null && shog3Choice2!=null) {
					if(mouseX>shog3Choice1.getX() && mouseX<shog3Choice1.getX()+40 && mouseY>shog3Choice1.getY() && mouseY<shog3Choice1.getY()+40) {
						shog3Choice1.setNumShoggoths(shog3Choice1.getNumShoggoths()+1);
						shog3Loc.setNumShoggoths(shog3Loc.getNumShoggoths()-1);
						shoggothDecisionLeft--;
						decision = shog3Choice1;
						shog3Loc = null; shog3Choice1 = null; shog3Choice2 = null;
					}
					else if(mouseX>shog3Choice2.getX() && mouseX<shog3Choice2.getX()+40 && mouseY>shog3Choice2.getY() && mouseY<shog3Choice2.getY()+40) {
						shog3Choice2.setNumShoggoths(shog3Choice2.getNumShoggoths()+1);
						shog3Loc.setNumShoggoths(shog3Loc.getNumShoggoths()-1);
						shoggothDecisionLeft--;
						decision = shog3Choice2;
						shog3Loc = null; shog3Choice1 = null; shog3Choice2 = null;
					}
				}
				for(Player p : players)
/*CheckShoggoth*/	if(decision!=null && p.getLocation()==decision && !wardedBoxActive) {
						gameState=12;
						currentPlayer.setShoggothContact(currentPlayer.getLocation().getNumShoggoths());
						rollsRemaining += currentPlayer.getLocation().getNumShoggoths();
						return;
					}
				if(shoggothDecisionLeft==0) {
					if(cultistCardsDrawn<summoningRate)
						gameState=0;
					else if(!toBeResolved.isEmpty() && toBeResolved.get(toBeResolved.size()-1).getName().equals("hastur")) {
						removeOldOne("hastur");
						if(rollsRemaining==0 && gameState!=44 && shoggothDecisionLeft==0) {
							if(evilStirs[0]) {
								gameState=17;
								evilStirs[0] = false;
							}
							else if(evilStirs[1]) {
								evilStirs[1] = false;
								gameState = 18;
							}
							else if(evilStirs[2]) {
								evilStirs[2] = false;
								gameState=19;
							}
							else
								gameState = 1;
						}
					}
					else if(!toBeResolved.isEmpty()) {
						if(toBeResolved.get(toBeResolved.size()-1).getName().equals("dagon"))
							gameState = 0;
						else
							gameState = 44;
					}
					else
						gameState = 1;
				}
			}
//Click Discsard Card		
			else if(gameState==21) {
				currentPlayer.getHand().remove(trySelectCard(mouseX,mouseY));
				if(currentPlayer.getHand().size()<=7 || (!currentPlayer.getIsInsane() && currentPlayer.getName().equals("MAGICIAN") && currentPlayer.getHand().size()<=8)) {
					gameState=prevState;
					currentPlayer = originalPlayer;
				}
			}
			
/*ClickDie*/else if(mouseX>0 && mouseX<100 && mouseY>250 && mouseY<350 && gameState==12) { //GameState #12
					gameState=6;
			}
			
			else if(gameState==1 && currentPlayer.getActionsRemaining()!=0) { //GameState #1
				trySelectCard(mouseX,mouseY);
				
//Turn Options
				if(cultistBtn.contains(mouseX, mouseY) && defeatCultist) //Cultist Button
				{
/*SpecialHunter*/	if(currentPlayer.getName().equals("HUNTER")) {
						cultistsRemaining += currentPlayer.getLocation().getNumCultists();
						currentPlayer.getLocation().setNumCultists(0);
						currentPlayer.setActionsRemaining(currentPlayer.getActionsRemaining()-1);
					}
/*DefeatCultist*/	else {
						cultistsRemaining++;
						currentPlayer.getLocation().setNumCultists(currentPlayer.getLocation().getNumCultists()-1);
						currentPlayer.setActionsRemaining(currentPlayer.getActionsRemaining()-1);
					}
				}
				else if(shoggothBtn.contains(mouseX, mouseY) && defeatShoggoth) //Shoggoth Button
				{
/*SpecialHunter*/	if(currentPlayer.getName().equals("HUNTER") && currentPlayer.getSpecial()) {
						shoggothsRemaining++;
						currentPlayer.getLocation().setNumShoggoths(currentPlayer.getLocation().getNumShoggoths()-1);
						currentPlayer.setActionsRemaining(currentPlayer.getActionsRemaining()-1);
						currentPlayer.setSpecial(false);
						if(!relicDeck.isEmpty())
							currentPlayer.getHand().add((PlayerCard) relicDeck.remove(0));
					}
/*DefeatShoggoth*/	else {
						shoggothsRemaining++;
						currentPlayer.getLocation().setNumShoggoths(currentPlayer.getLocation().getNumShoggoths()-1);
						currentPlayer.setActionsRemaining(currentPlayer.getActionsRemaining()-3);
						if(!relicDeck.isEmpty())
							currentPlayer.getHand().add((PlayerCard) relicDeck.remove(0));
					}
				}

/*TakeBus*/		else if(busBtn.contains(mouseX,mouseY) && takeBus)
					gameState=4;
				
/*TradeCard*/	else if(tradeBtn.contains(mouseX,mouseY) && tradeCard) {
					outer:for(Player p : currentPlayer.getLocation().getPlayerList()) {
						for(PlayerCard card : p.getHand()) {
							if(card.getName().contains("relic") || card.getName().equals(currentPlayer.getLocation().getColor())) {
								if(card.getIsSelected()) {
/*GiveCard*/						if(currentPlayer==p) {
										if(!(currentPlayer.getIsInsane() && currentPlayer.getName().equals("MAGICIAN") && card.getName().contains("relic"))) {
											if(currentPlayer.getLocation().getPlayerList().size()>2) {
												gameState=2;
												break outer;
											}
											else if(currentPlayer.getLocation().getPlayerList().size()>1) {
												for(Player p1 : currentPlayer.getLocation().getPlayerList())
													if(p1!=p) {
														if((p1.getName().equals("DETECTIVE") || currentPlayer.getName().equals("DETECTIVE")) && p1.getIsInsane()) {
															if(currentPlayer.getActionsRemaining()>1) {
																currentPlayer.getHand().remove(card);
																p1.getHand().add(card);
																card.setSelected(false);
																currentPlayer.setActionsRemaining(currentPlayer.getActionsRemaining()-2);
															}
														}
														else {
															currentPlayer.getHand().remove(card);
															p1.getHand().add(card);
															card.setSelected(false);
															if(!p1.getName().equals("MAGICIAN"))
																currentPlayer.setActionsRemaining(currentPlayer.getActionsRemaining()-1);
														}
														gameState=1;
														break;
													}
												break outer;
											}
										}
									}
/*TakeCard*/						else {
										if((p.getName().equals("DETECTIVE") || currentPlayer.getName().equals("DETECTIVE")) && p.getIsInsane()) {
											if(currentPlayer.getActionsRemaining()>1) {
												p.getHand().remove(card);
												currentPlayer.getHand().add(card);
												card.setSelected(false);
												currentPlayer.setActionsRemaining(currentPlayer.getActionsRemaining()-2);
											}
										}
										else {
											p.getHand().remove(card);
											currentPlayer.getHand().add(card);
											card.setSelected(false);
											if(!p.getName().equals("MAGICIAN"))
												currentPlayer.setActionsRemaining(currentPlayer.getActionsRemaining()-1);
										}
										gameState=1;
										break outer;
									}
								}
							}
						}
					}
				}
				
				else if(gateBtn.contains(mouseX,mouseY) && closeGate) //Gate Button
				{
					if(yigActive) {
						if(currentPlayer.getLocation().getColor().equals("green") || currentPlayer.getLocation().getColor().equals("red")) {
							for(PlayerCard card : currentPlayer.getHand())
								if(card.getName().equals("yellow") || card.getName().equals("purple")) {
									
									if(!possibleColors.contains(card.getName()) && possibleColors.size()<2)
										possibleColors.add(card.getName());
								}
						}
						else if(currentPlayer.getLocation().getColor().equals("yellow") || currentPlayer.getLocation().getColor().equals("purple")) {
							for(PlayerCard card : currentPlayer.getHand())
								if(card.getName().equals("green") || card.getName().equals("red")) {
									
									if(!possibleColors.contains(card.getName()) && possibleColors.size()<2)
										possibleColors.add(card.getName());
								}
						}
						if(miGoEyeActive) {
							int counter = 0;
							for(PlayerCard card : currentPlayer.getHand())
								if(card.getName().equals(currentPlayer.getLocation().getColor()))
									counter++;
							if(!(counter>4 || (currentPlayer.getName().equals("DETECTIVE") && counter>3))) 
								possibleColors.clear();
							possibleColors.add(currentPlayer.getLocation().getColor());
						}
						
						if(possibleColors.size()>1) {
							gameState=3;
						}
						else if(possibleColors.size()==1){
/*CloseGate*/					int counter=0;
							if(currentPlayer.getName().equals("DETECTIVE"))
								counter=4;
							else
								counter=5;
							if(miGoEyeActive) {
								counter--;
								miGoEyeActive = false;
							}
							
							for(PlayerCard card : currentPlayer.getHand())
								if(card.getName().equals(possibleColors.get(0))) {
									currentPlayer.getHand().remove(card);
									break;
								}
							for(int i=0; i<currentPlayer.getHand().size(); i++) {
								if(counter>0 && currentPlayer.getHand().get(i).getName().equals(currentPlayer.getLocation().getColor())) {
									playerDiscard.add(currentPlayer.getHand().remove(i));
									counter--;
									i--;
								}
							}
							resetPlayerDiscard();
							possibleColors.clear();
							closeGate((Gate)currentPlayer.getLocation());
							if(((Gate)locations.get(5)).getIsClosed() && ((Gate)locations.get(11)).getIsClosed() && ((Gate)locations.get(17)).getIsClosed() && ((Gate)locations.get(23)).getIsClosed()) {
								gameState = 42;
								return;
							}
							else
								currentPlayer.setActionsRemaining(currentPlayer.getActionsRemaining()-1);
							
							if(currentPlayer.getIsInsane())
								gameState = 15;
						}
					}
					else {
/*CloseGate*/			int counter=0;
						if(currentPlayer.getName().equals("DETECTIVE"))
							counter=4;
						else
							counter=5;
						if(miGoEyeActive) {
							counter--;
							miGoEyeActive = false;
						}
						
						for(int i=0; i<currentPlayer.getHand().size(); i++) {
							if(counter>0 && currentPlayer.getHand().get(i).getName().equals(currentPlayer.getLocation().getColor())) {
								playerDiscard.add(currentPlayer.getHand().remove(i));
								counter--;
								i--;
							}
						}
						resetPlayerDiscard();
						possibleColors.clear();
						closeGate((Gate) currentPlayer.getLocation());
						if(((Gate)locations.get(5)).getIsClosed() && ((Gate)locations.get(11)).getIsClosed() && ((Gate)locations.get(17)).getIsClosed() && ((Gate)locations.get(23)).getIsClosed()) {
							gameState = 42;
							return;
						}
						else
							currentPlayer.setActionsRemaining(currentPlayer.getActionsRemaining()-1);
						
						if(currentPlayer.getIsInsane())
							gameState = 15;
					}
				}
//ReporterSpecial
				else if(takeDiscard && mouseX>1256 && mouseX<1410 && mouseY>475 && mouseY<601) {
					if(mouseY>475 && mouseY<505 && currentPlayer.getLocation().getColor().equals("green")) {
						for(Card card : playerDiscard)
							if(card.getName().equals("green")) {
								currentPlayer.getHand().add((PlayerCard) card);
								playerDiscard.remove(card);
								break;
							}
						currentPlayer.setSpecial(false);
					}
					else if(mouseY>507 && mouseY<537 && currentPlayer.getLocation().getColor().equals("yellow")) {
						for(Card card : playerDiscard)
							if(card.getName().equals("yellow")) {
								currentPlayer.getHand().add((PlayerCard) card);
								playerDiscard.remove(card);
								break;
							}
						currentPlayer.setSpecial(false);
					}
					else if(mouseY>539 && mouseY<569 && currentPlayer.getLocation().getColor().equals("purple")) {
						for(Card card : playerDiscard)
							if(card.getName().equals("purple")) {
								currentPlayer.getHand().add((PlayerCard) card);
								playerDiscard.remove(card);
								break;
							}
						currentPlayer.setSpecial(false);
					}
					else if(mouseY>571 && mouseY<601 && currentPlayer.getLocation().getColor().equals("red")) {
						for(Card card : playerDiscard)
							if(card.getName().equals("red")) {
								currentPlayer.getHand().add((PlayerCard) card);
								playerDiscard.remove(card);
								break;
							}
						currentPlayer.setSpecial(false);
					}
					resetPlayerDiscard();
				}
//Close Gates
				else if(greenGate.contains(mouseX,mouseY))
					closeGate((Gate) locations.get(5)); //Park
				else if(yellowGate.contains(mouseX,mouseY))
					closeGate((Gate) locations.get(11)); //OldMill
				else if(purpleGate.contains(mouseX,mouseY))
					closeGate((Gate) locations.get(17)); //Hospital
				else if(redGate.contains(mouseX,mouseY))
					closeGate((Gate) locations.get(23)); //Graveyard
				
/*ClickMove*/	else {
					outer: for(Location loc : locations) {
						if(mouseX >= loc.getX() && mouseX <= loc.getX()+40 && mouseY >= loc.getY() && mouseY <= loc.getY() + 40) {
							selectedLocation = loc;
/*DriverSpecial*/			if(loc!= currentPlayer.getLocation() && currentPlayer.getName().equals("DRIVER")) {
								for(String str1 : loc.getNeighbors())
									if((!currentPlayer.getIsInsane() && str1.equals(currentPlayer.getLocation().getName())) || currentPlayer.getLocation().getNeighbors().contains(str1)) {
										currentPlayer.getLocation().getPlayerList().remove(currentPlayer);
										loc.getPlayerList().add(currentPlayer);
										currentPlayer.setLocation(loc);
										currentPlayer.setActionsRemaining(currentPlayer.getActionsRemaining()-1);
										
/*CheckShoggoth*/						if(loc.getNumShoggoths()!=0 && !wardedBoxActive) {
											gameState=12;
											rollsRemaining += currentPlayer.getLocation().getNumShoggoths();
										}
										break outer;
									}
							}
/*MoveNeighbor*/			if(loc.getNeighbors().contains(currentPlayer.getLocation().getName()))
							{
								if(currentPlayer.getLocation().getNumCultists()<2 || !ithaquaActive) {
									currentPlayer.getLocation().getPlayerList().remove(currentPlayer);
									loc.getPlayerList().add(currentPlayer);
									currentPlayer.setLocation(loc);
									currentPlayer.setActionsRemaining(currentPlayer.getActionsRemaining()-1);
									
/*HunterLocEnter*/					if(!hunterHasRolled && currentPlayer.getName().equals("HUNTER") && currentPlayer.getIsInsane() && currentPlayer.getLocation().getNumCultists()==0) {
										hunterNeedRoll = true;
										for(Player p : players) 
											if(p.getName().equals("HUNTER"))
												currentPlayer=p;
										gameState=12;
									}
/*CheckShoggoth*/					if(loc.getNumShoggoths()!=0 && !wardedBoxActive) {
										gameState=12;
										rollsRemaining += currentPlayer.getLocation().getNumShoggoths();
									}
								}
								break;
							}
							else if(loc==currentPlayer.getLocation()) {
/*DefeatShoggoth*/				if(defeatShoggoth) {
/*SpecialHunter*/					if(currentPlayer.getName().equals("HUNTER") && currentPlayer.getSpecial()) {
										shoggothsRemaining++;
										currentPlayer.getLocation().setNumShoggoths(currentPlayer.getLocation().getNumShoggoths()-1);
										currentPlayer.setActionsRemaining(currentPlayer.getActionsRemaining()-1);
										if(!relicDeck.isEmpty())
											currentPlayer.getHand().add((PlayerCard) relicDeck.remove(0));
										currentPlayer.setSpecial(false);
									}
									else {
										shoggothsRemaining++;
										currentPlayer.getLocation().setNumShoggoths(loc.getNumShoggoths()-1);
										currentPlayer.setActionsRemaining(currentPlayer.getActionsRemaining()-3);
										if(!relicDeck.isEmpty())
											currentPlayer.getHand().add((PlayerCard) relicDeck.remove(0));
										break;
									}
								}
/*DefeatCultist*/				else if(defeatCultist)
								{
/*SpecialHunter*/					if(currentPlayer.getName().equals("HUNTER")) {
										cultistsRemaining += currentPlayer.getLocation().getNumCultists();
										currentPlayer.getLocation().setNumCultists(0);
										currentPlayer.setActionsRemaining(currentPlayer.getActionsRemaining()-1);
									}
									else {
										cultistsRemaining++;
										currentPlayer.getLocation().setNumCultists(currentPlayer.getLocation().getNumCultists()-1);
										currentPlayer.setActionsRemaining(currentPlayer.getActionsRemaining()-1);
									}
									break;
								}
							}
/*MoveGate*/				else if(!nyarlathotepActive && loc.getIsGate() && !((Gate)loc).getIsClosed() && currentPlayer.getLocation().getIsGate() && !((Gate)(currentPlayer.getLocation())).getIsClosed()) {
								currentPlayer.getLocation().getPlayerList().remove(currentPlayer);
								loc.getPlayerList().add(currentPlayer);
								currentPlayer.setLocation(loc);
								currentPlayer.setActionsRemaining(currentPlayer.getActionsRemaining()-1);
								if(!wardedBoxActive)
									rollsRemaining++;
								
/*HunterLocEnter*/				if(!hunterHasRolled && currentPlayer.getName().equals("HUNTER") && currentPlayer.getIsInsane() && currentPlayer.getLocation().getNumCultists()==0) {
									hunterNeedRoll = true;
									for(Player p : players) 
										if(p.getName().equals("HUNTER"))
											currentPlayer=p;
								}

/*CheckShoggoth*/				if(currentPlayer.getLocation().getNumShoggoths()!=0 && !wardedBoxActive) {
									rollsRemaining += currentPlayer.getLocation().getNumShoggoths();
								}
								if(rollsRemaining>0)
									gameState=12;
							}
/*MoveBus*/					else if(takeBus) {
								if(loc.getIsBus() && currentPlayer.getName().equals("REPORTER")) {
									currentPlayer.getLocation().getPlayerList().remove(currentPlayer);
									loc.getPlayerList().add(currentPlayer);
									currentPlayer.setLocation(loc);
									currentPlayer.setActionsRemaining(currentPlayer.getActionsRemaining()-1);
									
/*CheckShoggoth*/					if(currentPlayer.getLocation().getNumShoggoths()!=0 && !wardedBoxActive) {
										gameState=12;
										rollsRemaining += currentPlayer.getLocation().getNumShoggoths();
									}
									break outer;
								}
								for(PlayerCard card : currentPlayer.getHand()) {
									if(loc.getColor().equals(card.getName()) || currentPlayer.getLocation().getColor().equals(card.getName()) || currentPlayer.getName().equals("REPORTER")) {
										if(card.getIsSelected()) {
											playerDiscard.add(0,card);
											currentPlayer.getHand().remove(card);
											resetPlayerDiscard();
											currentPlayer.getLocation().getPlayerList().remove(currentPlayer);
											loc.getPlayerList().add(currentPlayer);
											currentPlayer.setLocation(loc);
											currentPlayer.setActionsRemaining(currentPlayer.getActionsRemaining()-1);
											possibleColors.clear();
											
/*HunterLocEnter*/							if(!hunterHasRolled && currentPlayer.getName().equals("HUNTER") && currentPlayer.getIsInsane() && currentPlayer.getLocation().getNumCultists()==0) {
												hunterNeedRoll = true;
												for(Player p : players) 
													if(p.getName().equals("HUNTER"))
														currentPlayer=p;
												gameState=12;
											}

/*CheckShoggoth*/							if(currentPlayer.getLocation().getNumShoggoths()!=0 && !wardedBoxActive) {
												gameState=12;
												rollsRemaining += currentPlayer.getLocation().getNumShoggoths();
											}
											break outer;
										}
										if(!possibleColors.contains(card.getName()) && possibleColors.size()<2)
											possibleColors.add(card.getName());
									}
								}
								if(possibleColors.size()>1) {
									gameState=3;
								}
								else if(possibleColors.size()==1) {
									for(PlayerCard card : currentPlayer.getHand())
										if(card.getName().equals(possibleColors.get(0))) {
											playerDiscard.add(0,card);
											currentPlayer.getHand().remove(card);
											resetPlayerDiscard();
											break;
										}
									currentPlayer.getLocation().getPlayerList().remove(currentPlayer);
									loc.getPlayerList().add(currentPlayer);
									currentPlayer.setLocation(loc);
									currentPlayer.setActionsRemaining(currentPlayer.getActionsRemaining()-1);
									possibleColors.clear();
								
/*HunterLocEnter*/					if(!hunterHasRolled && currentPlayer.getName().equals("HUNTER") && currentPlayer.getIsInsane() && currentPlayer.getLocation().getNumCultists()==0) {
										hunterNeedRoll = true;
										for(Player p : players) 
											if(p.getName().equals("HUNTER"))
												currentPlayer=p;
										gameState=12;
									}

/*CheckShoggoth*/					if(currentPlayer.getLocation().getNumShoggoths()!=0 && !wardedBoxActive) {
										gameState=12;
										rollsRemaining += currentPlayer.getLocation().getNumShoggoths();
									}
								}
								break;
							}
						}
					}
				}
			}
			tryEndActions();
		}
		
		public void mouseReleased(MouseEvent e)
		{
			mouseX = e.getX();
			mouseY = e.getY()-212;
			//System.out.println("("+mouseX+","+mouseY+")");
			if(gameState==37 && selectedCard!=null) {
				System.out.println("test");
				for(int i=0; i<4; i++)
					if(mouseX>410+140*i && mouseX<410+140*(i+1) && mouseY>290 && mouseY<490) {
						playerDeck.remove(selectedCard);
						playerDeck.add(i,selectedCard);
						selectedCard = null;
						break;
					}
			}
		}
	}
}
