/**
 * Abstract class that is used as parent for the Player and Bot class
 * @author Robin A. and Zach D.
 */
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public abstract class Actor implements GameObject, KeyListener {
	Game engine;
	boolean[] keys;
	double time;
	double x, y, direction;
	double moveSpeed = 1;
	double rotSpeed = 2;
	Point pos;
	
	/* Return to menu and toggle 2D map
	 * (non-Javadoc)
	 * @see GameObject#update()
	 */
	public void update() {
		if (keys[KeyEvent.VK_ESCAPE]) {
			if (engine.isBot()) {
				engine.setBot(false);
			}
			engine.setIsTitle(true);
		}
		if (keys[KeyEvent.VK_M]) {
			engine.setShowMap(!engine.isShowMap());
			keys[KeyEvent.VK_M] = false;
		}
	}
	
	/**
	 * Rotate left
	 */
	public void turnLeft() {
		direction += rotSpeed;
		direction = normalizeTurn(direction);
	}
	
	/**
	 * Rotate right
	 */
	public void turnRight() {
		direction -= rotSpeed;
		direction = normalizeTurn(direction);
	}
	
	/**
	 * Move forwards
	 */
	public void moveForward() {
		x -= Math.cos(Math.toRadians(direction + 90)) * moveSpeed;
		y += Math.sin(Math.toRadians(direction + 90)) * moveSpeed;
	}
	
	/**
	 * Move backwards
	 */
	public void moveBackwards() {
		x += Math.cos(Math.toRadians(direction + 90)) * moveSpeed;
		y -= Math.sin(Math.toRadians(direction + 90)) * moveSpeed;
	}
	
	/* Return Actor's current position
	 * (non-Javadoc)
	 * @see GameObject#getPos()
	 */
	@Override
	public Point getPos() {
		return pos;
	}
	
	/**
	 * @param x - location of X for Actor
	 * @param y - location of Y for Actor
	 */
	public void setPos(double x, double y) {
		pos = new Point((int)x, (int)y);
	}
	
	/**
	 * Resets the turn to keep it within the bounds of 0-360 degrees
	 * @param angle - angle to normalize
	 * @return - normalized angle
	 */
	public double normalizeTurn(double angle) {
		double a = angle % 360;
		if (a < 0) {
			a += 360;
		}
		return a;
	}
	
	/**
	 * @param time - adds time to current time pool, after converting from milliseconds to seconds
	 */
	protected void addTime(double time) {
		this.time += time/1000;
	}
	
	/**
	 * @return - returns current time player has been in maze for
	 */
	protected double getTime() {
		return time;
	}

	/* Checks to see which key has been pressed, and changes the value of a Boolean array at that key's index to
	 * true
	 * (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent keypress) {
		keys[keypress.getKeyCode()] = true;
	}

	/* Changes keyPressed value to false
	 * (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent keyRelease) {
		keys[keyRelease.getKeyCode()] = false;	
	}
	
	/* Unused
	 * (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent arg0) {}
}