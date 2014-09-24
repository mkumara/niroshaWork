
/**
 * -----------------------------------------------------------------------------------------
 * -----------------------------------------------------------------------------------------
 *  NAME       TYPE         DATE        DESCRIPTION              
 * -----------------------------------------------------------------------------------------.
 *  Legacy_Documentation		Created.
 * 
 * -----------------------------------------------------------------------------------------
 */



package roadNetwork;

import java.util.LinkedList;
import java.util.List;

public class Road {
	
	private LinkedList<Node> nodes;
	
	private int roadId;

	public Road(LinkedList<Node> nodes, int roadId){
		this.nodes = nodes;
		this.roadId = roadId;
	}
	
	public LinkedList<Node> getNodes(){
		return this.nodes;
	}
	
	public int getId(){
		return this.roadId;
	}

}
