import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;


/**
 * This class allows user to draw a digit which the program identifies.
 * @author Bryan Wang
 * Reference:https://math.hws.edu/eck/cs124/s14/lab7/lab7-files/Grid.java
 *
 */
public class GameGUI extends JPanel implements ActionListener, MouseMotionListener {
	private final static int BOARD_WIDTH = 40;
	private final static int BOARD_HEIGHT = 40;
	private final static int MARGIN = 4;
	private final static int BOX_SIZE = 20;
	
	private static JFrame window; 
	
	private Color[][] gridColour; //the colour of grid[row][column]; if null, it it is transparent
	private Color lineColour; // Colour of grid lines; if null, no lines are drawn.
	
	//menubar
	private static JMenuBar mb = new JMenuBar();
	private static JMenu menu = new JMenu();
	private static JMenuItem reset;
	private static JMenuItem next;
	private static JMenuItem exit;
	
	private final int brushRadius = 11; //in pixels
	
	public static Grid grid;

	
	/**
	 * Creates a panel with a specified number of rows and columns of squares of a certain size.
	 * @param rows  The number of rows of squares.
	 * @param columns  The number of columns of squares.
	 * @param preferredSquareSize  The desired size, in pixels, for the squares. This will
	 *     be used to compute the preferred size of the panel. 
	 */
	public GameGUI() {
		menuBar();
		gridColour = new Color[BOARD_HEIGHT][BOARD_WIDTH]; // Create the array that stores square colors.
		lineColour = Color.BLACK;
		setPreferredSize(new Dimension(BOX_SIZE*BOARD_WIDTH, BOX_SIZE*BOARD_HEIGHT));
		setBackground(Color.GRAY); // Set the background color for this panel.
		addMouseMotionListener(this);     // Mouse actions will call methods in this object.
	}
	
	/**
	 * This creates a window and sets its content to be a panel of type Grid.
	 * @param args
	 */
	public static void main(String[] args) {
		grid = new Grid();
		//GUI
		window = new JFrame("Conway's Game of Life");  // Create a window and names it.
		GameGUI content = new GameGUI();  // 100 by 100 grid of 8px x 8px squares
		window.setContentPane(content);  // Add the Grid panel to the window.
		window.pack(); // Set the size of the window based on the panel's preferred size.
		Dimension screenSize; // A simple object containing the screen's width and height.
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// position for top left corner of the window
		int left = (screenSize.width - window.getWidth()) / 2;
		int top = (screenSize.height - window.getHeight()) / 2;
		window.setLocation(left,top);
		window.setResizable(false);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		
	}
	
	/**
	 * Sets up the menubar
	 */
	private void menuBar() {
		mb = new JMenuBar();
		menu = new JMenu("Menu");

		// menu items
		reset = new JMenuItem("Reset Canvas");
		next = new JMenuItem("Next Step");
		exit = new JMenuItem("Exit");

		// add to action listener for the menu items
		reset.addActionListener(this);
		next.addActionListener(this);
		exit.addActionListener(this);

		window.setJMenuBar(mb); // add menu bar
		mb.add(menu); // add menu to menubar
		menu.add(reset); // add items
		menu.add(next);
		menu.add(exit);

	}
	
	/**
	 * Finds the row numbers for grid squares within brushRadius many pixels from a y-coordinate
	 * @param pixelY a pixel y-coordinate. 
	 * @return The row numbers brushRadius away from pixelY. 
	 */
	private ArrayList<Integer> findRows(int pixelY) {
		ArrayList<Integer> rows = new ArrayList<Integer>();
		for(int i = (int) Math.round(((double)pixelY-brushRadius)/getHeight()*BOARD_HEIGHT); 
				i<= Math.round(((double)pixelY+brushRadius)/getHeight()*BOARD_HEIGHT);i++) {
			rows.add(i);
		}
		return rows;
	}
	
	/**
	 * Finds the column numbers for grid squares within brushRadius many pixels from a x-coordinate
	 * @param pixelX a pixel x-coordinate. 
	 * @return The column numbers corresponding to pixelY. 
	 */
	private ArrayList<Integer> findColumns(int pixelX) {
		ArrayList<Integer> cols = new ArrayList<Integer>();
		for(int i = (int) Math.round(((double)pixelX-brushRadius)/getHeight()*BOARD_WIDTH); 
				i<= Math.round(((double)pixelX+brushRadius)/getHeight()*BOARD_WIDTH);i++) {
			cols.add(i);
		}
		return cols;
	}
 
	private void updateGrid() {
		for (int row = 0; row < BOARD_HEIGHT; row++) {
			for (int col = 0; col < BOARD_WIDTH; col++) {
				if(grid.gameBoard[MARGIN+row][MARGIN+col]) {
					gridColour[row][col] = Color.BLACK;
				}
				else {
					gridColour[row][col] = null;
				}
			}
		}
	}

	/**
	 * Draws the grid of squares and grid lines (if the colour isn't null).
	 */
	protected void paintComponent(Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0,0,getWidth(),getHeight());
		double cellWidth = (double)getWidth() / BOARD_WIDTH;
		double cellHeight = (double)getHeight() / BOARD_HEIGHT;
		for (int row = 0; row < BOARD_HEIGHT; row++) {
			for (int col = 0; col < BOARD_WIDTH; col++) {
				if (gridColour[row][col] != null) {
					int x1 = (int)(col*cellWidth);
					int y1 = (int)(row*cellHeight);
					int x2 = (int)((col+1)*cellWidth);
					int y2 = (int)((row+1)*cellHeight);
					g.setColor(gridColour[row][col]);
					g.fillRect( x1, y1, (x2-x1), (y2-y1) );
				}
			}
		}
		if (lineColour != null) {
			g.setColor(lineColour);
			for (int row = 1; row < BOARD_HEIGHT; row++) {
				int y = (int)(row*cellHeight);
				g.drawLine(0,y,getWidth(),y);
			}
			for (int col = 1; col < BOARD_HEIGHT; col++) {
				int x = (int)(col*cellWidth);
				g.drawLine(x,0,x,getHeight());
			}
		}
	}
	
	/**
	 * Turns the grid squares where the user clicks (and holds) black.
	 */
	@Override
	public void mouseDragged(MouseEvent evt) {
		// the rows and columns in the grid of squares where the user clicked.
		ArrayList<Integer> rows = findRows(evt.getY() );
		ArrayList<Integer> cols = findColumns(evt.getX());
		for(int row = 0; row <rows.size();row++) {
			for(int col = 0; col < cols.size();col++) {
				if(gridColour[rows.get(row)][cols.get(col)]==null) {
					gridColour[rows.get(row)][cols.get(col)] = Color.BLACK;
				}
				else {
					gridColour[rows.get(row)][cols.get(col)] = null;
				}
				
				grid.gameBoard[MARGIN+rows.get(row)][MARGIN+cols.get(col)] = !grid.gameBoard[MARGIN+rows.get(row)][MARGIN+cols.get(col)];
				System.out.println(rows.get(row) + " " + cols.get(col));
			}
		}
		
		repaint(); // Causes the panel to be redrawn, by calling the paintComponent method.
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		// resets the canvas
		if (event.getSource() == reset) {
			grid.wipeGrid();
			for(int row = 0; row < BOARD_HEIGHT; row++) {
				for(int col = 0; col < BOARD_WIDTH; col++) {
					gridColour[row][col] = null;
				}
			}
			updateGrid();
			repaint();
		}
		// recognizes the digit
		else if (event.getSource() == next) {
			grid.updateGrid();
			updateGrid();
			repaint();
		}
		// exits 
		else if (event.getSource() == exit) {
			System.exit(0);
		}
		
	}

	
} 