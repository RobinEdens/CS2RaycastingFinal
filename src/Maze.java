/**
 * Maze class that allows for generation of a randomized maze, alongside methods to generate a random start/finish point, and find
 * the shortest path between the start to the finish. 
 * @author Robin A. and Zach D.
 */

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;
import java.util.Queue;

public class Maze {
	
	private int width;
	private int height;
	private int[][] maze;
	private Point goal;
	private Point playerStart;
	private HashSet<Point> visited;
	private Map<Point, Point> prev;

	/**
	 * Constructor to create maze; doubling width/height adds walls, then adding 1 makes sure all walls are covered
	 * @param x - height of Maze
	 * @param y - width of Maze 
	 */
	public Maze(int x, int y) {
		this.visited = new HashSet<Point>();
		this.width = x*2+1;
		this.height = y*2+1;
		this.maze = new int[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (i % 2 == 1 && j % 2 == 1) {
					maze[i][j] = 0;
				} else {
					maze[i][j] = 1;
				}
			}
		}
		createMaze(1, 1);
		setPoints();
	}
	
	/**
	 * Recursive function that generates a random maze using a recursive backtracking algorithm
	 * @param cWidth - current Width index
	 * @param cHeight - current Height index
	 */
	private void createMaze(int cWidth, int cHeight) {
		Compass[] direction = Compass.values();
		Collections.shuffle(Arrays.asList(direction));
		for (Compass pointing: direction) {
			Point currentPoint = new Point(cWidth, cHeight);
			visited.add(currentPoint);
			int nextW = cWidth + pointing.genX;
			int nextH = cHeight + pointing.genY;
			Point nextPoint = new Point(nextW, nextH);
			if (arrayBounds(nextW, width) && arrayBounds (nextH, height) && maze[nextW][nextH] == 0 && !visited.contains(nextPoint)) {
				maze[cWidth + pointing.mapX][cHeight + pointing.mapY] = 0;
				createMaze(nextW, nextH);
			}
		} 
	}

	/**
	 * Boolean that makes sure a specified index is valid for the Maze array
	 * @param index - index being checked 
	 * @param max - bounds that index must be smaller than
	 * @return - returns whether index is valid or not
	 */
	private boolean arrayBounds (int index, int max) {
		return (index >= 0 && index < max );
	}

	/**
	 * Sets random points for both the player's start and the exit of the maze; bounds to make sure player and
	 * goal aren't too close isnt precise, but at least it stops direct spawns side-by-side 
	 */
	private void setPoints () {
		ArrayList<Point> emptySpot = new ArrayList<Point>();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
			if (maze[i][j] == 0) {
				emptySpot.add(new Point (i, j));
				}
			}
		}
		Collections.shuffle(emptySpot);
		this.goal = emptySpot.get(0);
		double goalRange = this.goal.getX() + this.goal.getY();
		for (int i = 1; i < emptySpot.size(); i++) {
			Point playerStart = emptySpot.get(i);
			double playerRange = playerStart.getX() + playerStart.getY();
			if (Math.abs(playerRange - goalRange) > width/2)
			this.playerStart = playerStart;
		}
	}
	
	/**
	 * Breath-first traversal that finds the goal relative to the player start, and keeps track of where each 
	 * index was visited from via use of a HashMap. After finding the goal, each Point from the goal backward to
	 * the playerStart is pushed into a Stack, which is then returned for use by the PathfindingBot
	 * @return - returns a Stack with all the Points leading from start to goal, with starting point on top
	 */
	public Stack<Point> shortestPath() {
		resetVisited();
		Stack <Point> shortestPath = new Stack<Point>();
		prev = new HashMap<Point,Point>();
		Compass[] direction = Compass.values();
		Queue <Point> path = new LinkedList<Point>();
		path.add(playerStart);
		while(!path.isEmpty()) {
			Point current = path.poll();
			for (Compass pointing: direction) {
				if (current.equals(goal)) {
					shortestPath.push(current);
					while (prev.containsKey(shortestPath.peek()) && !shortestPath.peek().equals(playerStart)){
						shortestPath.push(prev.get(shortestPath.peek()));
					}
					return shortestPath;
				}
				int nextW = (int)current.getX() + pointing.mapX;
				int nextH = (int)current.getY() + pointing.mapY;
				Point nextPoint = new Point(nextW, nextH);
				if (arrayBounds(nextW, width) && arrayBounds (nextH, height) && maze[nextW][nextH] == 0 && !visited.contains(nextPoint)) {
					path.add(nextPoint);
					visited.add(nextPoint);
					prev.put(nextPoint, current);
				}
			}
		}
		return shortestPath;		
	}
	
	/**
	 * Resets HashSet for use in ShortestPath method
	 */
	public void resetVisited() {
		this.visited = new HashSet<Point>();
	}
	
	/**
	 * @return - returns Goal of map
	 */
	public Point getGoal() {
		return goal;
	}
	
	/**
	 * @return - returns starting point of map
	 */
	public Point getStart () {
		return playerStart;
	} 
	
	/**
	 * @return - returns int array which contains the map
	 */
	public int[][] getMap() {
		return maze;
	}
}