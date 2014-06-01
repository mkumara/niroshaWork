package roadNetwork;

import java.util.Iterator;
import java.util.LinkedList;

public class RoadNetwork {

	private LinkedList<Edge> edges;

	private LinkedList<Node> nodes;

	private LinkedList<Road> roads;

	int count = 0;
	
	public RoadNetwork() {
		edges = new LinkedList<Edge>();
		nodes = new LinkedList<Node>();
		roads = new LinkedList<Road>();
	}

	public void addRoad(Road road) {
		this.roads.add(road);
		Iterator<Node> it = road.getNodes().iterator();
		LinkedList<Node> newNodes = new LinkedList<Node>();
		while (it.hasNext()) {
			Node node1 = it.next();
			if (nodes.size() == 0) {
				nodes.add(node1);
			} else {
				Iterator<Node> total = nodes.iterator();
				while (total.hasNext()) {
					Node node2 = total.next();
					if (node1.getX() == node2.getX()
							&& node1.getY() == node2.getY()) {
						count++;
					//	System.out.println("intersection " + count);
					} else {
						newNodes.add(node1);
					}
				}
			}
		}
		it = newNodes.iterator();
		while(it.hasNext()){
			nodes.add(it.next());
		}
		
	}

	public LinkedList<Road> getRoads() {
		return this.roads;
	}

	public int numberOfRoads() {
		return this.roads.size();
	}

	public int numberOfNodes() {
		return this.nodes.size();
	}

}
