/**
 * Player class that keeps track of user KeyPresses and updates any necessary values based off of which keys the
 * user presses
 * @author Robin A. and Zach D.
 */
import java.awt.Component;
import java.awt.event.KeyEvent;

public class Player extends Actor  {
	
	double startX, startY;
	private int[][] map;
	public boolean completed;

	/**
	 * Constructor
	 * @param x - player's starting X value
	 * @param y - player's starting Y value
	 * @param direction - starting  direction player is facing
	 * @param map - int array with coinciding map
	 * @param engine - Game object for crossreferencing 
	 * @param t - Component that keylistener is placed on; should always be a Camera object
	 */
	protected Player (int x, int y, int direction, int[][] map, Game engine, Component t) {
		this.time = 0;
		this.completed = false;
		this.x = x;
		this.startX = x;
		this.y = y;
		this.startY = y;
		this.setPos(x/32,  y/32);
		this.direction = direction;
		this.map = map;
		this.engine = engine;
		this.keys = new boolean[255];
		t.addKeyListener(this);
	}
	
	/* Runs player logic based off of whether certain keys are pressed or not
	 * (non-Javadoc)
	 * @see GameObject#update()
	 */
	public void update() {
		if (keys[KeyEvent.VK_W] || keys[KeyEvent.VK_UP]) {
			double prevX, prevY;
			prevX = x; prevY = y;
			this.moveForward();			
			if (map[(int)x/32][(int)y/32] == 1) {
				x = prevX;
				y = prevY;
			} else {
				this.setPos(x/32, y/32);
			}
		}
		if (keys[KeyEvent.VK_S] || keys[KeyEvent.VK_DOWN]) {
			double prevX, prevY;
			prevX = x; prevY = y;
			this.moveBackwards();			
			if (map[(int)x/32][(int)y/32] == 1) {
				x = prevX;
				y = prevY;
			} else {
				this.setPos(x/32, y/32);
			}
		}
		if (keys[KeyEvent.VK_A] || keys[KeyEvent.VK_LEFT]) {
			this.turnLeft();
		}
		if (keys[KeyEvent.VK_D] || keys[KeyEvent.VK_RIGHT]) {
			this.turnRight();
		}
		if (keys[KeyEvent.VK_R]) {
			x=startX; y=startY; direction=0;
			this.time = 0;
		}
		super.update();
	}
	
	/**
	 * Checks whether player completed map or not
	 * @param completed - check if completed
	 */
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
}