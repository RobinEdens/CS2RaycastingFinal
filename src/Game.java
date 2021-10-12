/**
 * Main game loop that handles most of the logic required to run the game, runs until user selects
 * "Exit" on the Title screen
 * @author Robin A. and Zach D.
 */
import java.awt.*;
import java.util.Stack;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import java.io.*;

public class Game extends JFrame implements Runnable {

	private static final long serialVersionUID = 1L;
	private boolean isRunning, isTitle, isShowMap, isBot;
	private Point playerStart;
	private Title titleScreen;
	protected Camera raycast;
	protected Maze maze;
	protected Player player;
	protected Bot bot;
	protected Portal goal;
	protected int[][] map;
	protected Stack<Point> path;

	private final int WIDTH = 1024;
	private final int HEIGHT = 768;
	private final int FPS = 60;

	/**
	 * Creates a new Maze object to generate a new map
	 * @param x - width of map
	 * @param y - height of map
	 */
	protected void newMap(int x, int y) {
		maze = new Maze(x, y);
		this.map = maze.getMap();
		this.playerStart = maze.getStart();
		player = new Player((int)(playerStart.getX()*32) + 16, (int)(playerStart.getY()*32) + 16, 0, map, this, raycast);
		this.goal = new Portal(maze.getGoal(), this, player);
		raycast.setMap(map, goal);
		this.setShowMap(false);
	}
	
	/**
	 * Creates a new bot using the path of the current maze
	 */
	protected void newBot() {
		path = maze.shortestPath();
		bot = new Bot(path, this, raycast);
		this.goal = new Portal(maze.getGoal(), this, bot);
		this.setBot(true);
	}
	
	/**
	 * Initializes everything needed to properly run the program; creates the main JFrame and adds the Title screen 
	 * and Camera object to itself, then starts the game loop
	 */
	private void init() {
		setTitle("3D Maze");
		setSize(WIDTH, HEIGHT);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		this.titleScreen = new Title(WIDTH,HEIGHT, this);
		this.add(titleScreen);
		this.raycast = new Camera(this, WIDTH, HEIGHT);
		this.add(raycast);
		this.map = null; 		
		this.isBot = false;
		setShowMap(false);
		setIsTitle(true);
		setSize(WIDTH, HEIGHT);
		start();
	}

	/* Game loop that causes updates to only happen variant on the FPS set; draws screen, keeps count of time, and 
	 * updates positions of player and how close player is to the goal
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		init();
		playSong();
		while(isRunning) {
			long frameTime = System.currentTimeMillis();
			frameTime = (1000 / FPS) - (System.currentTimeMillis() - frameTime);
			if (isBot) {
				bot.update();
				raycast.draw();
				bot.addTime(frameTime*1.3);
				goal.update();
			} else if (!isTitle) {
				player.update();
				raycast.draw();
				player.addTime(frameTime*1.3);
				goal.update();
			} 
			if (frameTime > 0) {
				try {
					Thread.sleep(frameTime);
				} catch (Exception e) {}
			}
		}
	}
	
	/**
	 * Loops music while program is open!
	 */
	private void playSong() {
		try {
			File music = new File("res/e1m1.wav");
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(music));
			clip.start();
			clip.loop(Clip.LOOP_CONTINUOUSLY);
			Thread.sleep(100);
		} catch (Exception e) {}
	}

	/**
	 * Initializes the game loop variable so the game properly starts
	 */
	private void start() {
		if (isRunning) {
			return;
		}
		isRunning = true;
	}
	
	/**
	 * Stops the game loop from running, which leads to the program closing
	 */
	protected void stop() {
		if (!isRunning) {
			return;
		}
		isRunning = false;
	}

	/**
	 * Returns whether the 2D map is showing or not
	 * @return - isShowMap
	 */
	protected boolean isShowMap() {
		return isShowMap;
	}
	
	/**
	 * Returns whether the title screen is showing or not
	 * @return - isTitle
	 */
	protected boolean isTitle() {
		return isTitle;
	}
	
	/**
	 * Returns whether bot is running or not
	 * @return - isBot
	 */
	protected boolean isBot() {
		return isBot;
	}
	/**
	 * Sets the 2D map in game on/off 
	 * @param isMap - boolean that toggles 2D map on/off
	 */
	protected void setShowMap(boolean isMap) {
		this.isShowMap = isMap;
	}
	
	/**
	 * Toggles whether bot is running on/off
	 * @param isBot - boolean to toggle bot on/off
	 */
	protected void setBot(boolean isBot) {
		this.isBot = isBot;
	}
	
	/**
	 * Sets title screen on/off, and gives priority to either the Camera if false, or Title if true
	 * @param isTitle - boolean that toggles the title screen on/off
	 */
	protected void setIsTitle(boolean isTitle) {
		this.isTitle = isTitle;
		if (isTitle) {
			titleScreen.setActive(true);
			raycast.setEnabled(false);
			raycast.setVisible(false);
			raycast.transferFocus();
			titleScreen.requestFocus();
		} else {
			titleScreen.setEnabled(false);
			raycast.setEnabled(true);
			raycast.setVisible(true);
			titleScreen.transferFocus();
			raycast.requestFocus();
		}
	}
	
	public static void main(String[] args) {
		Game game = new Game();
		game.run();
		System.exit(0);
	}
}