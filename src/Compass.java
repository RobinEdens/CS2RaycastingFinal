/**
 * Enum that holds values to make it easier to choose a random path to take within the
 * maze generation algorithm, alongside the shortestPath finder
 * @author Robin A. and Zach D.
 */

public enum Compass {
	N(0, -2, 0, -1, 180),S(0, 2, 0, 1, 0),W(-2, 0, -1, 0, 270),E(2, 0, 1, 0, 90);
	public int genX, genY, mapX, mapY, dir;
	private Compass(int compassX, int compassY, int mapX, int mapY, int dir) {
		this.genX = compassX; this.genY = compassY;
		this.mapX = mapX; this.mapY = mapY; 
		this.dir = dir;
	}
}