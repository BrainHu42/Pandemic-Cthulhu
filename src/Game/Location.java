package Game;
import java.util.ArrayList;

public class Location 
{
	/*
	 * fields:
	 * ints xLoc, yLoc, numCultists and numShuggoths
	 * Strings: name and color
	 * booleans: isGate and isBus
	 * ArrayList<String> neighbors and shuggothsNextMove
	 */
	
	
	private int xLoc, yLoc, numCultists, numShoggoths;
	private int numTempShoggoths; // added Jan18
	private String name, color;
	private boolean isGate, isBus;
	
	private ArrayList<String> neighbors, shoggothNextMove;
	private ArrayList<Player> charList; 
	private static ArrayList<Location> locations = new ArrayList<Location>();  		// added dec13
	
	private GameConstants cons = new GameConstants();   						// added dec13
	private int width = cons.getBoardWidth(), height = cons.getBoardHeight();   // added dec13
	
	public Location()															// added dec13
	{
		populateLocationArray();
	}
	
	public Location(String n, int x, int y, String col, boolean gate, boolean bus, 
			ArrayList<String> neigh, ArrayList<String> shuggothAI)
	{
		name = n;
		xLoc = x;
		yLoc = y;
		color = col;
		isGate = gate;
		isBus = bus;
		neighbors = neigh;
		shoggothNextMove = shuggothAI;
		// no parameters for the values below as they will always start out as a default 0 or empty
		numCultists = 0;
		numShoggoths = 0;
		numTempShoggoths = 0;  //jan18
		charList = new ArrayList<Player>();	
	}
	
	// getters
	public int getX() {return xLoc;}
	public int getY() {return yLoc;}
	public boolean getIsGate() {return isGate;}
	public boolean getIsBus() {return isBus;}
	public String getName() {return name;}
	public String getColor() {return color;}
	public int getNumCultists() {return numCultists;}
	public int getNumShoggoths() {return numShoggoths;}
	public int getNumTempShoggoths() {return numTempShoggoths;} //jan18
	public ArrayList<Player> getPlayerList() {return charList;}  //changed dec21
	public ArrayList<String> getNeighbors() {return neighbors;}
	public ArrayList<String> getShoggothNextLoc() {return shoggothNextMove;}
	public ArrayList<Location> getLocations(){return locations;}  // added dec13
	
	//setters... name, xLoc, yLoc, color, isBus, isGate, neighbors and shuggothNextLoc don't change
	// and all three ArrayLists in general have there own way of adding and deleting data
	// so only setters for cultists and shuggoths
	public void setNumCultists(int num){numCultists = num;}
	public void setNumShoggoths(int num){numShoggoths = num;}
	public void setNumTempShoggoths(int num){numTempShoggoths = num;}
	
	public void populateLocationArray()  //altered dec13
	{
		ArrayList<String> neighbors = new ArrayList<String>();  //we will reuse these
		ArrayList<String> shugPath = new ArrayList<String>();
		
//TRAIN STATION = index 0 
		neighbors.add("university");
		neighbors.add("cafe");

		shugPath.add("green_university");
		shugPath.add("yellow_cafe");
		shugPath.add("purple_university");
		shugPath.add("red_cafe");
		
		ArrayList<String> neigh0 = new ArrayList<String>();
		neigh0.addAll(neighbors);
		ArrayList<String> shog0 = new ArrayList<String>();
		shog0.addAll(shugPath);
		
		Location tsGreen = new Location("trainStation", (int)(width*203/1430), (int)(height*157/870), "green", false, true, neigh0, shog0);    
		locations.add(tsGreen);	
		
		neighbors.clear();
		shugPath.clear();
		
//UNIVERSITY = index 1 
		neighbors.add("trainStation");
		neighbors.add("policeStation");
		neighbors.add("park");
		
		shugPath.add("green_park");
		shugPath.add("yellow_trainStation");
		shugPath.add("purple_policeStation&park");
		shugPath.add("red_trainStation");
		
		ArrayList<String> neigh1 = new ArrayList<String>();
		neigh1.addAll(neighbors);
		ArrayList<String> shog1 = new ArrayList<String>();
		shog1.addAll(shugPath);
		
		Location uGreen = new Location("university", (int)(width*290/1430), (int)(height*105/870), "green", false, false, neigh1, shog1);    
		locations.add(uGreen);
		
		neighbors.clear();
		shugPath.clear();
		
//POLICE STATION = index 2
		neighbors.add("university");
		neighbors.add("park");
		neighbors.add("secretLodge");
				
		shugPath.add("green_park");
		shugPath.add("yellow_university,purple_secretLodge");
		shugPath.add("red_university");
		
		ArrayList<String> neigh2 = new ArrayList<String>();
		neigh2.addAll(neighbors);
		ArrayList<String> shog2 = new ArrayList<String>();
		shog2.addAll(shugPath);
				
		Location psGreen = new Location("policeStation", (int)(width*463/1430), (int)(height*95/870), "green", false, false, neigh2, shog2);    
		locations.add(psGreen);
				
		neighbors.clear();
		shugPath.clear();
		
//SECRET LODGE = index 3
		neighbors.add("diner");
		neighbors.add("policeStation");
		neighbors.add("park");
		
		shugPath.add("green_park");
		shugPath.add("purple_diner");
		shugPath.add("yellow_park&policeStation");
		shugPath.add("red_diner");
		
		ArrayList<String> neigh3 = new ArrayList<String>();
		neigh3.addAll(neighbors);
		ArrayList<String> shog3 = new ArrayList<String>();
		shog3.addAll(shugPath);
		
		Location slGreen = new Location("secretLodge", (int)(width*435/1430), (int)(height*202/870), "green", false, false, neigh3, shog3);    
		locations.add(slGreen);
		
		neighbors.clear();
		shugPath.clear();
		
//Diner = index 4
		neighbors.add("secretLodge");
		neighbors.add("junkyard");
	
		shugPath.add("green_secretLodge");
		shugPath.add("purple_junkyard");
		shugPath.add("yellow_secretLodge");
		shugPath.add("red_junkyard");
		
		ArrayList<String> neigh4 = new ArrayList<String>();
		neigh4.addAll(neighbors);
		ArrayList<String> shog4 = new ArrayList<String>();
		shog4.addAll(shugPath);
	
		Location dGreen = new Location("diner", (int)(width*580.3/1430), (int)(height*175/870), "green", false, true, neigh4, shog4);    
		locations.add(dGreen);
	
		neighbors.clear();
		shugPath.clear();
		
//PARK = index 5
		neighbors.add("university");
		neighbors.add("policeStation");
		neighbors.add("secretLodge");
		
		shugPath.add("green_TRIGGER");
		shugPath.add("yellow_university,purple_secretLodge");
		shugPath.add("red_university");
		
		ArrayList<String> neigh5 = new ArrayList<String>();
		neigh5.addAll(neighbors);
		ArrayList<String> shog5 = new ArrayList<String>();
		shog5.addAll(shugPath);
		
		Gate pGreen = new Gate("park", (int)(width*333/1430), (int)(height*244/870), "green", true, false, neigh5, shog5);    
		locations.add(pGreen);
		
		neighbors.clear();
		shugPath.clear();
		
//CAFE = index 6
		neighbors.add("church");
		neighbors.add("trainStation");
		
		shugPath.add("yellow_church");
		shugPath.add("green_trainStation");
		shugPath.add("red_church");
		shugPath.add("purple_trainStation");
		
		ArrayList<String> neigh6 = new ArrayList<String>();
		neigh6.addAll(neighbors);
		ArrayList<String> shog6 = new ArrayList<String>();
		shog6.addAll(shugPath);
		
		Location cYellow = new Location("cafe", (int)(width*290/1430), (int)(height*360/870), "yellow", false, false, neigh6, shog6);    
		locations.add(cYellow);
		
		neighbors.clear();
		shugPath.clear();
		
//CHURCH = index 7
		neighbors.add("cafe");
		neighbors.add("oldMill");
		neighbors.add("historicInn");
		neighbors.add("farmstead");
		
		shugPath.add("yellow_oldMill");
		shugPath.add("green_cafe");
		shugPath.add("red_farmstead");
		shugPath.add("purple_farmstead");
	
		ArrayList<String> neigh7 = new ArrayList<String>();
		neigh7.addAll(neighbors);
		ArrayList<String> shog7 = new ArrayList<String>();
		shog7.addAll(shugPath);
	
		Location chYellow = new Location("church", (int)(width*383.4/1430), (int)(height*410/870), "yellow", false, false, neigh7, shog7);    
		locations.add(chYellow);
	
		neighbors.clear();
		shugPath.clear();
		
//FARMSTEAD = index 8
		neighbors.add("church");
		neighbors.add("historicInn");
		neighbors.add("swamp");
		
		shugPath.add("yellow_church");
		shugPath.add("green_church,red_swamp");
		shugPath.add("purple_swamp");
	
		ArrayList<String> neigh8 = new ArrayList<String>();
		neigh8.addAll(neighbors);
		ArrayList<String> shog8 = new ArrayList<String>();
		shog8.addAll(shugPath);
	
		Location fYellow = new Location("farmstead", (int)(width*391.8/1430), (int)(height*525/870), "yellow", false, false, neigh8, shog8);    
		locations.add(fYellow);
	
		neighbors.clear();
		shugPath.clear();
		
//SWAMP = index 9
		neighbors.add("greatHall");
		neighbors.add("farmstead");
	
		shugPath.add("yellow_farmstead");
		shugPath.add("red_greatHall");
		shugPath.add("green_farmstead,purple_greatHall");

		ArrayList<String> neigh9 = new ArrayList<String>();
		neigh9.addAll(neighbors);
		ArrayList<String> shog9 = new ArrayList<String>();
		shog9.addAll(shugPath);

		Location sYellow = new Location("swamp", (int)(width*521/1430), (int)(height*499/870), "yellow", false, false, neigh9, shog9);    
		locations.add(sYellow);

		neighbors.clear();
		shugPath.clear();
		
//HISTORIC INN = index 10
		neighbors.add("farmstead");
		neighbors.add("church");
				
		shugPath.add("yellow_church");
		shugPath.add("green_church");
		shugPath.add("red_farmstead");
		shugPath.add("purple_farmstead");
			
		ArrayList<String> neigh10 = new ArrayList<String>();
		neigh10.addAll(neighbors);
		ArrayList<String> shog10 = new ArrayList<String>();
		shog10.addAll(shugPath);
	
		Location hiYellow = new Location("historicInn", (int)(width*564.98/1430), (int)(height*367.3/870), "yellow", false, true, neigh10, shog10);    
		locations.add(hiYellow);
			
		neighbors.clear();
		shugPath.clear();
	
//OLD MILL = index 11
		neighbors.add("church");
		
		shugPath.add("yellow_TRIGGER");
		shugPath.add("green_church");
		shugPath.add("red_church");
		shugPath.add("purple_church");
				
		ArrayList<String> neigh11 = new ArrayList<String>();
		neigh11.addAll(neighbors);
		ArrayList<String> shog11 = new ArrayList<String>();
		shog11.addAll(shugPath);
				
		Gate omYellow = new Gate("oldMill", (int)(width*245.2/1430), (int)(height*454.9/870), "yellow", true, false, neigh11, shog11);    
		locations.add(omYellow);
				
		neighbors.clear();
		shugPath.clear();
		
//DOCKS = index 12
		neighbors.add("woods");
		neighbors.add("boardwalk");

		shugPath.add("purple_boardwalk");
		shugPath.add("red_woods");
		shugPath.add("yellow_woods");
		shugPath.add("green_boardwalk");

		ArrayList<String> neigh12 = new ArrayList<String>();
		neigh12.addAll(neighbors);
		ArrayList<String> shog12 = new ArrayList<String>();
		shog12.addAll(shugPath);

		Location dRed = new Location("docks", (int)(width*1115/1430), (int)(height*297/870), "purple", false, false, neigh12, shog12);    
		locations.add(dRed);

		neighbors.clear();
		shugPath.clear();
		
//BOARDWALK = index 13
		neighbors.add("docks");
		neighbors.add("factory");
			
		shugPath.add("purple_factory");
		shugPath.add("red_docks");
		shugPath.add("green_factory");
		shugPath.add("yellow_docks");

		ArrayList<String> neigh13 = new ArrayList<String>();
		neigh13.addAll(neighbors);
		ArrayList<String> shog13 = new ArrayList<String>();
		shog13.addAll(shugPath);

		Location bRed = new Location("boardwalk", (int)(width*1274/1430), (int)(height*184/870), "purple", false, false, neigh13, shog13);    
		locations.add(bRed);

		neighbors.clear();
		shugPath.clear();

//PAWN SHOP = index 14
		neighbors.add("factory");
		neighbors.add("junkyard");
		neighbors.add("hospital");
			
		shugPath.add("purple_hospital");
		shugPath.add("green_junkyard");
		shugPath.add("red_factory");
		shugPath.add("yellow_junkyard&factory");

		ArrayList<String> neigh14 = new ArrayList<String>();
		neigh14.addAll(neighbors);
		ArrayList<String> shog14 = new ArrayList<String>();
		shog14.addAll(shugPath);

		Location pshRed = new Location("pawnShop", (int)(width*941/1430), (int)(height*263/870), "purple", false, false, neigh14, shog14);    
		locations.add(pshRed);

		neighbors.clear();
		shugPath.clear();
		
//JUNKYARD = index 15
		neighbors.add("pawnShop");
		neighbors.add("diner");
			
		shugPath.add("purple_pawnShop");
		shugPath.add("green_diner");
		shugPath.add("red_pawnShop,yellow_diner");

		ArrayList<String> neigh15 = new ArrayList<String>();
		neigh15.addAll(neighbors);
		ArrayList<String> shog15 = new ArrayList<String>();
		shog15.addAll(shugPath);

		Location jRed = new Location("junkyard", (int)(width*796/1430), (int)(height*157/870), "purple", false, false, neigh15, shog15);    
		locations.add(jRed);

		neighbors.clear();
		shugPath.clear();
		
//FACTORY = index 16
		neighbors.add("boardwalk");
		neighbors.add("pawnShop");
		neighbors.add("hospital");
			
		shugPath.add("purple_hospital");
		shugPath.add("green_pawnShop");
		shugPath.add("red_boardwalk");
		shugPath.add("yellow_boardwalk");

		ArrayList<String> neigh16 = new ArrayList<String>();
		neigh16.addAll(neighbors);
		ArrayList<String> shog16 = new ArrayList<String>();
		shog16.addAll(shugPath);

		Location fRed = new Location("factory", (int)(width*1086/1430), (int)(height*165/870), "purple", false, true, neigh16, shog16);    
		locations.add(fRed);

		neighbors.clear();
		shugPath.clear();
		
//HOSPITAL = index 17
		neighbors.add("factory");
		neighbors.add("pawnShop");
			
		shugPath.add("purple_TRIGGER");
		shugPath.add("green_pawnShop");
		shugPath.add("red_factory");
		shugPath.add("yellow_factory");

		ArrayList<String> neigh17 = new ArrayList<String>();
		neigh17.addAll(neighbors);
		ArrayList<String> shog17 = new ArrayList<String>();
		shog17.addAll(shugPath);

		Gate hRed = new Gate("hospital", (int)(width*985/1430), (int)(height*88/870), "purple", true, false, neigh17, shog17);    
		locations.add(hRed);

		neighbors.clear();
		shugPath.clear();
		
//GREAT HALL = index 18
		neighbors.add("swamp");
		neighbors.add("woods");
		neighbors.add("market");
	
		shugPath.add("red_market");
		shugPath.add("yellow_swamp");
		shugPath.add("purple_woods");
		shugPath.add("green_swamp");

		ArrayList<String> neigh18 = new ArrayList<String>();
		neigh18.addAll(neighbors);
		ArrayList<String> shog18 = new ArrayList<String>();
		shog18.addAll(shugPath);

		Location ghRed = new Location("greatHall", (int)(width*724/1430), (int)(height*470/870), "red", false, false, neigh18, shog18);    
		locations.add(ghRed);

		neighbors.clear();
		shugPath.clear();
		
//WOODS = index 19
		neighbors.add("greatHall");
		neighbors.add("market");
		neighbors.add("docks");
	
		shugPath.add("red_market");
		shugPath.add("purple_docks");
		shugPath.add("yellow_greatHall");
		shugPath.add("green_greatHall&docks");

		ArrayList<String> neigh19 = new ArrayList<String>();
		neigh19.addAll(neighbors);
		ArrayList<String> shog19 = new ArrayList<String>();
		shog19.addAll(shugPath);

		Location wRed = new Location("woods", (int)(width*738/1430), (int)(height*332/870), "red", false, false, neigh19, shog19);    
		locations.add(wRed);

		neighbors.clear();
		shugPath.clear();
		
//THEATER = index 20
		neighbors.add("market");
	
		shugPath.add("red_market");
		shugPath.add("yellow_market");
		shugPath.add("purple_market");
		shugPath.add("green_market");

		ArrayList<String> neigh20 = new ArrayList<String>();
		neigh20.addAll(neighbors);
		ArrayList<String> shog20 = new ArrayList<String>();
		shog20.addAll(shugPath);

		Location tRed = new Location("theater", (int)(width*927/1430), (int)(height*506/870), "red", false, false, neigh20, shog20);    
		locations.add(tRed);

		neighbors.clear();
		shugPath.clear();

//WHARF = index 21
		neighbors.add("market");
		neighbors.add("graveyard");
			
		shugPath.add("red_graveyard");
		shugPath.add("yellow_market");
		shugPath.add("purple_market");
		shugPath.add("green_market");

		ArrayList<String> neigh21 = new ArrayList<String>();
		neigh21.addAll(neighbors);
		ArrayList<String> shog21 = new ArrayList<String>();
		shog21.addAll(shugPath);

		Location whRed = new Location("wharf", (int)(width*1057/1430), (int)(height*429.3/870), "red", false, false, neigh21, shog21);    
		locations.add(whRed);

		neighbors.clear();
		shugPath.clear();
		
//MARKET = index 22
		neighbors.add("woods");
		neighbors.add("greatHall");
		neighbors.add("theater");
		neighbors.add("wharf");
			
		shugPath.add("red_wharf");
		shugPath.add("yellow_greatHall,purple_woods");
		shugPath.add("green_greatHall");
		
		ArrayList<String> neigh22 = new ArrayList<String>();
		neigh22.addAll(neighbors);
		ArrayList<String> shog22 = new ArrayList<String>();
		shog22.addAll(shugPath);

		Location mRed = new Location("market", (int)(width*883.5/1430), (int)(height*394/870), "red", false, true, neigh22, shog22);    
		locations.add(mRed);

		neighbors.clear();
		shugPath.clear();
		
//GRAVEYARD= index 23
		neighbors.add("wharf");
		
		shugPath.add("red_TRIGGER");
		shugPath.add("yellow_wharf");
		shugPath.add("purple_wharf");
		shugPath.add("green_wharf");

		ArrayList<String> neigh23 = new ArrayList<String>();
		neigh23.addAll(neighbors);
		ArrayList<String> shog23 = new ArrayList<String>();
		shog23.addAll(shugPath);

		Gate gRed = new Gate("graveyard", (int)(width*1029/1430), (int)(height*585/870), "red", true, false, neigh23, shog23);    
		locations.add(gRed);

		neighbors.clear();
		shugPath.clear();
	}
}
