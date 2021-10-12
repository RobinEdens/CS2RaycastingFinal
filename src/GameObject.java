/**
 * GameObject interface for any objects not rendered directly onto the map (Player, enemies, goal, etc.)
 * @author Robin A. and Zach D.
 */
import java.awt.Point;

public interface GameObject {
	
	public void update();
	public Point getPos();
}