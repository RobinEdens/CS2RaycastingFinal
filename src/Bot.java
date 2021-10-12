/**
 * Bot class that auto runs a maze based off of the shortest path available for that maze
 * @author Robin A. and Zach D.
 */

import java.awt.Component;
import java.awt.Point;
import java.util.Stack;

public class Bot extends Actor {
	
	private Stack<Point> route;
	private Point nextPos;
	private double nextDir;
	private int centerMoves;
	private boolean turnLeft, turnRight, center;
	
	/**
	 * Constructor
	 * @param route - Stack that contains all points bot must visit
	 * @param engine - Game class
	 * @param t - Component to add Keylistener to
	 */
	public Bot(Stack<Point> route, Game engine, Component t) {
		this.time = 0;
		this.centerMoves = 0;
		this.engine = engine;
		this.route = route;
		this.pos = route.pop();
		this.x = ((pos.getX()*32) + 16);
		this.y = ((pos.getY()*32) + 16);
		this.direction = findFacing();
		this.keys = new boolean[255];
		setNextPos();
		nextDir = findFacing();
		t.addKeyListener(this);
	}
	
	/* Makes moves for bot based off current position and next position to visit
	 * (non-Javadoc)
	 * @see GameObject#update()
	 */
	@Override
	public void update() {
		super.update();
		if (direction != nextDir && !center) {
			if (normalizeTurn(direction + 90) == nextDir && !turnLeft && !turnRight) {
				turnLeft = true;
			} else {
				turnRight = true;
			}
			if (turnLeft) {
				this.turnLeft();
			} else {
				this.turnRight();
			}
		}
		if (!pos.equals(nextPos) && direction == nextDir) {
			this.moveForward();
			this.setPos((int)x/32, (int)y/32);
		} 
		if (pos.equals(nextPos) && !center) {
			if (nextPos != null) {
				center = true;
				setNextPos();
				nextDir = findFacing();
				turnLeft = false; turnRight = false;
			} else {
				System.out.println("goal!");
				engine.setBot(false);
			}
		}
		if (center) {
			if (centerMoves < 16) {
				this.moveForward();
				centerMoves++;
			} else {
				center = false;
				centerMoves = 0;
			}
		}
	}
	 
	/**
	 * Checks to see which direction the bot must turn to head to the next position, if at all
	 * @return - angle to face
	 */
	private int findFacing() {
		Compass[] direction = Compass.values();
		for (Compass pointing: direction) {
			Point compPoint = new Point((int)pos.getX()+pointing.mapX, (int)pos.getY()+pointing.mapY);
			if (compPoint.equals(nextPos)) {
				return pointing.dir;
			}
		}
		return 0;
	}
	
	/**
	 * Sets next position for bot to check if one available, else sets to null
	 */
	private void setNextPos() {
		if (!route.isEmpty()) {
			nextPos = route.pop();
		} else {
			nextPos = null;
		}
	}
}