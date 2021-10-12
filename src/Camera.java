/**
 * Camera class that displays tthe game world to the player, either 3D for the actual world, or 2D if the 
 * player is currently looking at the map.
 * @author Robin A.and Zach D.
 */
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Stack;

import javax.swing.JPanel;

public class Camera extends JPanel {

	private static final long serialVersionUID = 1L;
	private Portal goal;
	private BufferedImage image;
	private Game engine;
	private int[][] map;
	private boolean foundGoal;
	private double goalDist;
	private final int FOV = 75;
	private final int WIDTH;
	private final int HEIGHT;
	private final double ANGLE_INC;
	private final double PROJ_DIST;
	private final int CENTER_HEIGHT;
	
	/**
	 * Constructor to create a new Camera object
	 * @param engine - Game runnable for cross referencing objects
	 * @param w - width of frame
	 * @param h - height of frame
	 */
	public Camera(Game engine, int w, int h) {
		this.WIDTH = w;
		this.HEIGHT = h;
		this.engine = engine;
		this.ANGLE_INC = (double)FOV/(double)WIDTH;
		this.CENTER_HEIGHT = HEIGHT/2;
		this.PROJ_DIST = (HEIGHT/2) / Math.tan(Math.toRadians(FOV/2));
		image = new BufferedImage (WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	}
	
	/**
	 * Main draw loop method that draws either the 2D/3D map based off of variables, or draws nothing if the
	 * title screen is currently shown 
	 */
	protected void draw () {
		Graphics g = this.getGraphics();
		Graphics buffG = image.getGraphics();
		if (!engine.isTitle()) {
			if (!engine.isShowMap() && !engine.isBot()) {
				draw3D(buffG, engine.player);
			} else if (engine.isBot() && !engine.isShowMap()){
				draw3D(buffG, engine.bot); 
			} else if (engine.isBot() && engine.isShowMap()){
				draw2D(buffG, engine.bot);
			} else {
				draw2D(buffG, engine.player);
			}
		}

		g.drawImage(image, 0, 0, engine);
		g.dispose();
	}
		
	/**
	 * Draws the 2D map onto the BufferedImage's graphics object
	 * @param g - Graphics object that coincides with the BufferedImage
	 * @param actor - Actor currently running maze
	 */
	private void draw2D(Graphics g, Actor actor) {
	
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		g.setColor(Color.WHITE);
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map.length; j++) {
				if (map[i][j] == 0) {
					g.fillRect((i*16), (j*16), 16, 16);
				}
			}
		}
		if (engine.isBot()) {
			Stack<Point> route = engine.maze.shortestPath();
			g.setColor(Color.GREEN);	
			while (!route.isEmpty()) {
				Point a = route.pop();
				g.fillRect(((int)a.getX()*16), ((int)a.getY()*16), 16, 16);
			}
		}
		g.setColor(Color.RED);
		g.fillOval((int)actor.x/2 - 5, (int)actor.y/2 - 5, 10, 10);
		g.setColor(Color.BLUE);
		Point goal = this.goal.getPos();
		g.fillOval((int)goal.getX() * 16, (int)goal.getY() * 16, 10, 10);
		
//		Old raycasting test in 2D; no need to show on minimap
//		g.setColor(Color.BLUE);
//		for (int i = (WIDTH/2); i > -(WIDTH/2); i--) {
//			double angle = Math.toRadians(engine.player.direction + (i *ANGLE_INC));
//			double sin = Math.sin(angle);
//			double cos = Math.cos(angle);
//			double length = rayCast(engine.player.x, engine.player.y, angle, i, g);		
//			g.drawLine((int)engine.player.x, (int)engine.player.y, (int)(engine.player.x + length*sin), (int)(engine.player.y + length *cos));
//		}
	}

	/**
	 * Draws the 3D world for the player onto the BufferedImage's graphics object
	 * @param g - graphics object of the BufferedImage
	 * @param actor - Actor currently running maze
	 */
	private void draw3D(Graphics g, Actor actor) {
		g.setColor(Color.CYAN);
		g.fillRect(0, 0, WIDTH, HEIGHT/2);
		g.setColor(Color.PINK);
		g.fillRect(0, HEIGHT/2, WIDTH, HEIGHT/2);
		for (int i = -(WIDTH/2); i < (WIDTH/2); i++) {
			double angle = Math.toRadians(actor.direction + (i *ANGLE_INC));
			double length = rayCast(actor.x, actor.y, angle, i, g);
			drawWall(g, length, i+(WIDTH/2), angle, actor);
			foundGoal = false;
		}
		g.setColor(Color.WHITE);
		g.setFont(new Font("Dialog",Font.BOLD, 36));
		g.drawString("Time: " + (int)actor.getTime(), WIDTH/32, HEIGHT/16);
	}
	
	/**
	 * Casts a ray and measures the distance between where the ray was casted and any 
	 * walls that it intersects, returning that value
	 * @param x - X-position of player
	 * @param y - Y-position of player
	 * @param angle - angle of cast ray, relative to player's facing angle
	 * @param i - current location in loop/which pixel column to draw to
	 * @param g - graphics object of BufferedImage
	 * @return - distance between cast ray and nearest wall
	 */
	private double rayCast (double x, double y,double angle, int i, Graphics g) {
		double length = 0;
		double sin = Math.sin(angle);
		double cos = Math.cos(angle);
		int x2, y2;
		do {
			length += .1;
			x2 = (int) (x + length*sin);
			y2 = (int) (y + length*cos);
			if (!foundGoal) {
				Point coordinate = new Point(x2/32, y2/32);
				if (coordinate.equals(goal.getPos())) {
					foundGoal = true;
					goalDist = length;
				}
			}
		} while (map[x2/32][y2/32] != 1);
		return length;
	}
	
	/**
	 * Draws a line that corresponds with relative height of the wall that intercepted the cast ray, and draws
	 * that line onto the screen. After all pixel columns are filled, a pseudo-3d image is created
	 * @param g - Graphics object of BufferedImage
	 * @param dist - distance calculated between cast ray and wall
	 * @param x - X-coordinates where to draw line on Graphics object
	 * @param angle - angle at which the ray was cast
	 * @param actor - Actor currently running maze
	 */
	private void drawWall(Graphics g, double dist, int x, double angle, Actor actor) {
		double relativeAngle = Math.toRadians(actor.direction) - angle;
		double adjDist = dist * Math.cos(relativeAngle);
        double wallHeight = (32*PROJ_DIST / (adjDist));
        int intensity = (int)(adjDist);
        if (intensity > 255) {
        	intensity = 255;
        }
        g.setColor(new Color(255-intensity,0,255-intensity));
        if (foundGoal && (adjDist > goalDist)) {
        	g.drawLine(Math.abs(x-(WIDTH-1)), CENTER_HEIGHT - (int)wallHeight, Math.abs(x-(WIDTH-1)), CENTER_HEIGHT + (int)wallHeight);
        	g.setColor(Color.BLUE);
        	drawGoal(g, x, angle);
        } else {
            g.drawLine(Math.abs(x-(WIDTH-1)), CENTER_HEIGHT - (int)wallHeight, Math.abs(x-(WIDTH-1)), CENTER_HEIGHT + (int)wallHeight);
        }
	}
	
	/**
	 * Draws the goal/portal onto the 3D plane, similarly to the way that the walls are drawn (it's pretty much a small wall)
	 * @param g - Graphics object of BufferedImage
	 * @param x - X-coordinate corresponding with line column to draw
	 * @param angle - relative angle of ray cast
	 */
	private void drawGoal(Graphics g, int x, double angle) {
        double goalHeight = (8*PROJ_DIST / (goalDist));
        g.drawLine(Math.abs(x-(WIDTH-1)), CENTER_HEIGHT - (int)(goalHeight), Math.abs(x-(WIDTH-1)), CENTER_HEIGHT + (int)(goalHeight));
	}

	/**
	 * Sets new map for Camera; used when new map is generated by Game 
	 * @param map - new Map object
	 * @param goal - new goal located within map
	 */
	protected void setMap(int[][] map, Portal goal) {
		this.map = map;
		this.goal = goal;
	}
}