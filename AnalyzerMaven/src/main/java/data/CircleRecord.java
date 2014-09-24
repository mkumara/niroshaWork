
/**
 * -----------------------------------------------------------------------------------------
 * -----------------------------------------------------------------------------------------
 *  NAME       TYPE         DATE        DESCRIPTION              
 * -----------------------------------------------------------------------------------------.
 *  Legacy_Documentation		Created.
 * 
 * -----------------------------------------------------------------------------------------
 */



package data;

public class CircleRecord {

	private int circleId;
	private int subdivisionId;
	private int blockId;
	private double intensity;
	
	public void setCircleId(int circleId){
		this.circleId = circleId;
	}
	
	public int getCircleId(){
		return this.circleId;
	}
	public void setSubdivisonId(int subdivisionId){
		this.subdivisionId = subdivisionId;
	}
	
	public int getSubdivisionId(){
		return this.subdivisionId;
	}
	public void setBlockId(int blockId){
		this.blockId = blockId;
	}
	
	public int getBlockId(){
		return this.blockId;
	}
	
}
