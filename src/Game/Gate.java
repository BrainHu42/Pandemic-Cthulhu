package Game;
import java.util.ArrayList;

public class Gate extends Location
{
	private boolean isClosed;
	private boolean isSealed;
	
	public boolean getIsClosed() {return isClosed;}
	public boolean getIsSealed() {return isSealed;}
	
	public void setClosed(boolean val) {isClosed = val;}
	public void setSealed(boolean val) {isSealed = val;}
	
	public Gate(String n, int x, int y, String col, boolean gate, boolean bus, 
			ArrayList<String> neigh, ArrayList<String> shuggothAI)
	{
		super(n, x, y, col, gate, bus, neigh, shuggothAI);
	}
}
