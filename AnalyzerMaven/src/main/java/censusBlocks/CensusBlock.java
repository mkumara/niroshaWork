
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

public class CensusBlock {

	private Vertex[] coordinates; // coordinates of the census block vertices

	private int id; // census block id

	private Location centroid = null; // center of gravity

	private double area = -1;
	
	private double factoredArea;
	
	private int factor = 10000;

	public CensusBlock(int id, Vertex[] coordinates) {
		this.id = id;
		this.coordinates = coordinates;
	//	this.centroid = calculateCentroid();
	}

	public Location getCentroid() {
		return this.centroid;
	}
	
	public void setCentroid(Location centroid) {
		this.centroid = centroid;
	}
	
	public int getID(){
		return this.id;
	}
	



}