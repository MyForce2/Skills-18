package bots;
import pirates.*;
public class MothershipHandler {
	
	
	private int defenders;
	private Mothership mothership;
	
	public Mothership getMothership() {
		return mothership;
	}
	
	public void setMothership(Mothership mothership) {
		this.mothership = mothership;
	}
	
	public void setDefenders(int defenders) {
		this.defenders = defenders;
	}
	
	public MothershipHandler(Mothership mothership){
		this.mothership = mothership;
		this.defenders = 0;
	}
	/**
	 * @return the defenders
	 */
	public int getDefenders() {
		return defenders;
	}
	/**
	 * @param defenders the defenders to set
	 */
	public void addDefender() {
		this.defenders += 1;
	}
	
}
