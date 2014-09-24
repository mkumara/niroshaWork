
/**
 * -----------------------------------------------------------------------------------------
 * -----------------------------------------------------------------------------------------
 *  NAME       TYPE         DATE        DESCRIPTION              
 * -----------------------------------------------------------------------------------------.
 *  Legacy_Documentation		Created.
 * 
 * -----------------------------------------------------------------------------------------
 */



package censusBlocks;

public class Pod {

	public int id;
	public String site;
	public Address addy;
	public String isd;
	public Location loc;
	public String quad;
	private int blocks = 0;
	
	public Pod(int id, String site, Address addy, String isd, Location loc,
			String quad) {
		
		this.id = id;
		this.site = site;
		this.addy = addy;
		this.isd = isd;
		this.loc = loc;
		this.quad = quad;

	}
	
	public void addBlock() {
		blocks++;
	}
	
	public void removeBlock() {
		blocks--;
		if(blocks < 0) {
			System.err.println("Negative number of assigned census blocks!");
		}
	}
	
	public int getNumOfBlocks() {
		return blocks;
	}
	
	public void setLocation(Location loc){
		this.loc = loc;
	}
	public Location getLocation(){
		return this.loc;
	}

}
