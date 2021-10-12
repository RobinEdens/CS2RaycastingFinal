/**
 * Portal object to keep track of the current Goal for the maze, alongside check to see if the Player
 * has currently reached the goal.
 * @author Robin A. and Zach D.
 */
import java.awt.Point;
import javax.swing.JOptionPane;

public class Portal implements GameObject {
	
	private Actor actor;
	private Point loc;
	private Game engine;
	
	/**
	 * Constructor
	 * @param goal - Point that contains goal x,y coordinates
 	 * @param engine - main Game object for cross-referencing
 	 * @param actor - Actor currently running maze
	 */
	public Portal (Point goal, Game engine, Actor actor) {
		this.actor = actor;
		this.loc = goal;
		this.engine = engine;
	}
	
	/* Checks to see if player has currently made it to goal, and gives a congratulatory message and returns to title if so
	 * (non-Javadoc)
	 * @see GameObject#update()
	 */
	@Override
	public void update() {
		if (this.getPos().distance(actor.getPos()) == 0) {
			String message = "";
			if (engine.isBot()) {
				message = "Bot completion of map in " + (int)actor.getTime() + " seconds.";
				if (engine.player.completed) {
					message += " Your time was " + (int)engine.player.getTime() + " seconds.";
					if (engine.player.getTime() > actor.getTime()) {
						message += " Looks like you were slower than the bot!";
					} else {
						message += " You beat the bot! Great job!";
					}
				}
				engine.setBot(false);
			} else {
				message = "You did it! You finished the map in " + (int)actor.getTime() + " seconds!";
				engine.player.setCompleted(true);
				System.out.println("You're winner");
			}
			JOptionPane.showMessageDialog(engine, message, "Congratulations!!", JOptionPane.PLAIN_MESSAGE);
			engine.setIsTitle(true);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {}
		}
	}

	/* Returns position of Goal
	 * (non-Javadoc)
	 * @see GameObject#getPos()
	 */
	@Override
	public Point getPos() {
		return loc;
	}
}