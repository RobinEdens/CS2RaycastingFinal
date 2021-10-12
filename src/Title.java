/**
 * Title screen that allows users to choose a difficulty level, see shortest path of previous maze generated, view the 
 * help/credits screen, and exit the game
 * @author Robin A. and Zach D.
 */
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Title extends JPanel implements ActionListener {
	
	private GridBagConstraints layout;
	private Font titleFont, buttonFont;
	private JLabel title;
	private Game engine;
	private int width, height;
	private static final long serialVersionUID = 1L;

	/**
	 * @param w - width of JPanel
	 * @param h - height of JPanel
	 * @param eng - Game object for cross-referencing 
	 */
	public Title (int w, int h, Game eng) {
		this.engine = eng;
		this.width = w;
		this.height = h;
		this.setSize(width, height);
		this.setBackground(Color.CYAN);
		this.setLayout(new GridBagLayout());
		this.setDoubleBuffered(true);
		setLayout();
	}
	
	/**
	 * Sets the layout of the Title; sets where the buttons go, adds actionlisteners, alongside sets the name of the game
	 * at the top
	 */
	private void setLayout() {
    	titleFont = new Font("Dialog",Font.BOLD, 48);
        title = new JLabel("Labyrinthian");
        title.setFont(titleFont);
		setGridBag(title, 0, 0, 20, 20, 20, 20, GridBagConstraints.CENTER,GridBagConstraints.NONE);
		buttonFont = new Font("Dialog",Font.PLAIN, 20);
		
		JButton easy = new JButton("Run an easy maze");
		easy.setFont(buttonFont);
		easy.addActionListener(this);
		setGridBag(easy, 0, 1, 15, 15, 15, 15, GridBagConstraints.CENTER,GridBagConstraints.NONE);
		
		JButton hard = new JButton("Run a hard maze");
		hard.setFont(buttonFont);
		hard.addActionListener(this);
		setGridBag(hard, 0, 2, 15, 15, 15, 15, GridBagConstraints.CENTER,GridBagConstraints.NONE);
		
		JButton fastest = new JButton("Fastest route for previous maze");
		fastest.setFont(buttonFont);
		fastest.addActionListener(this);
		setGridBag(fastest, 0, 3, 15, 15, 15, 15, GridBagConstraints.CENTER,GridBagConstraints.NONE);
		
		JButton credits = new JButton("Help/Credits");
		credits.setFont(buttonFont);
		credits.addActionListener(this);
		setGridBag(credits, 0, 4, 15, 15, 15, 15, GridBagConstraints.CENTER,GridBagConstraints.NONE);
		
		JButton exit = new JButton("Exit");
		exit.setFont(buttonFont);
		exit.addActionListener(this);
		setGridBag(exit, 0, 5, 15, 15, 15, 15, GridBagConstraints.CENTER,GridBagConstraints.NONE);
	}
	
    /**
     * Aligns JComponents to their proper positioning on the GridBagConstraints
     * @param comp - component to add
     * @param x - x-value on GridBag
     * @param y - y-value on Gridbag
     * @param top - top insets
     * @param left - left insets
     * @param bot - bottom insets 
     * @param right - right insets
     * @param align - Alignments, if any
     * @param fill - Fill values, if any
     */
    private void setGridBag (JComponent comp, int x, int y,int top, int left, int bot, int right, int align, int fill) {
        layout = new GridBagConstraints();
        layout.insets = new Insets(top, left, bot, right);
        if (fill != GridBagConstraints.NONE)
        	layout.fill = fill;
        if (align != GridBagConstraints.NONE)
        	layout.anchor=align;
        layout.gridx = x;
        layout.gridy = y;
        this.add(comp, layout);
    }
    
    /**
     * Generates the credits field by reading from the readme.txt file 
     * @return - returns JTextArea with credits String properly attached
     * @throws IOException - Exception handler if file not found
     */
    private JTextArea credits() throws IOException {
        JTextArea credits = new JTextArea ();
        credits.setEditable(false);
        credits.setFont(new Font("Dialog",Font.PLAIN, 12));
        credits.append(readFile(new File("readme.txt")));
        return credits;
    }
    
	/**
	 * Reads a file passed to it
	 * @param file - file passed
	 * @return - contents of file
	 * @throws IOException - Exception handler if file not found
	 */
	private String readFile(File file) throws IOException {
		Scanner fileScanner = new Scanner(file);
		String contents = "";
		while (fileScanner.hasNextLine()) {
			String line = fileScanner.nextLine();
			contents += line + "\n";
		}
		fileScanner.close();
		return contents;
	}

    /**
     * Sets whether the title screen is currently active or not, changing any necessary values to activate/deactivate
     * the title screen
     * @param isActive - boolean that would toggle active on/off
     */
    protected void setActive (boolean isActive) {
		Component [] buttons = this.getComponents();
		for (Component button: buttons) {
			button.setVisible(isActive);
			button.setEnabled(isActive);
		}
		this.setEnabled(isActive);
    	this.setVisible(isActive);
    	this.update(this.getGraphics());
    }
    
	/* Checks to see which Button was pressed, and runs whatever logic coincides with each button
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent action) {
		if (action.getActionCommand().equals("Run an easy maze")) {
			engine.newMap(10,10);
			engine.setIsTitle(false);
			this.setActive(false);
		}
		if (action.getActionCommand().equals("Run a hard maze")) {
			engine.newMap(17,17);
			engine.setIsTitle(false);
			this.setActive(false);
		}
		if (action.getActionCommand().equals("Fastest route for previous maze")) {
			if (engine.map != null) {
				engine.newBot();
				engine.setIsTitle(false);
				this.setActive(false);
			} else {
				JOptionPane.showMessageDialog(this, "Please run a map first!");
			}
		}
		if (action.getActionCommand().equals("Help/Credits")) {
			try {
				JOptionPane.showMessageDialog(this, credits(), "Help/Credits", JOptionPane.PLAIN_MESSAGE);
			} catch (HeadlessException | IOException e) {
				e.printStackTrace();
			}
		}
		if (action.getActionCommand().equals("Exit")) {
			engine.stop();
		}
	}
}