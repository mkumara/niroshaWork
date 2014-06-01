package censusBlocks;

public class Vertex {
	
	private double x, y;
	
	public Vertex(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
	
	public String toString() {
		return ("(" + x + "," + y + ")");
	}

}