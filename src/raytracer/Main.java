package raytracer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * A small ray-tracer based off of <a href='http://www.cse.chalmers.se/edu/year/2011/course/TDA361/grid.pdf'>http://www.cse.chalmers.se/edu/year/2011/course/TDA361/grid.pdf</a>
 * <br />
 * This class contains the main display of the program, as well as the ray-tracing code
 * @author xBobZx
 *
 */
public class Main extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7600502339309564557L;
	
	/** 
	 * Line that is 3 pixels wide
	 */
	public static final Stroke THICK_3 = new BasicStroke(3);
	/**
	 * Line that is one pixel wide
	 */
	public static final Stroke THICK_1 = new BasicStroke(1);
	
	/**
	 * Fully transparent color
	 */
	public static final Color TRANSPARENT = new Color(0, 0, 0, 0);
	
	/**
	 * The <code>JFrame</code> on which everything is displayed
	 */
	private JFrame frame;
	
	/**
	 * The <code>BufferedImage</code> on which everything will be drawn, before being drawn on to the <code>JFrame</code>
	 */
	private BufferedImage buffer;
	private Graphics2D g2d;
	
	/**
	 * Utility class that draws the lines that make up the grid
	 */
	private Lines lines = new Lines();
	/**
	 * Utility class that draws all selected squares
	 */
	private Grid grid = new Grid();
	
	/**
	 * The size of each unit in pixels
	 */
	private int cell_size = 100;
	
	/**
	 * The x coordinate of the first point
	 */
	private float startX;
	/**
	 * The y coordinate of the first point
	 */
	private float startY;
	
	/**
	 * The x coordinate of the second point
	 */
	private float endX;
	/**
	 * The y coordinate of the second point
	 */
	private float endY;
	
	/**
	 * Whether there is a first point
	 */
	private boolean existsStart = false;
	/**
	 * Whether there is a second point
	 */
	private boolean existsEnd = false;
	
	/**
	 * Whether the first point has been set
	 */
	private boolean setStart = false;
	/**
	 * Whether the second point has been set
	 */
	private boolean setEnd = false;
	
	/**
	 * Whether the buffers, grid and lines should be resized (set to true when the cell size changes)
	 */
	private boolean shouldResize = false;

	public static void main(String[] args)
	{
		new Main();
	}
	
	public Main()
	{
		//This method initializes the frame, the event listeners, the utility classes and the image buffer
		frame = new JFrame("Ray tracer");
		frame.add(this);
		
		//Sets the size of the display
		setSize(800, 800);
		setPreferredSize(getSize());
		frame.pack();
		
		//Centers the frame and makes it get destroyed when the X button is pressed
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Adds the event listeners
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		
		//Update everything to be correctly sized
		resize();
		
		//Display everything
		frame.setVisible(true);
	}
	
	/**
	 * Updates the image buffer, grid and lines to be correctly sized
	 */
	public void resize()
	{
		buffer = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
		g2d = buffer.createGraphics();

		//Sets the clearing color for the buffer to be transparent, and makes it anti-alias everything drawn on to it
		g2d.setBackground(TRANSPARENT);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(Color.BLACK);
		
		//Make any lines drawn on the image buffer be 3 pixels wide
		g2d.setStroke(THICK_3);

		//Initialize the lines and grid to the current size
		lines.resize(buffer.getWidth(), buffer.getHeight(), cell_size);
		grid.resize(buffer.getWidth(), buffer.getHeight());
		shouldResize = false;
	}
	
	/**
	 * When this display needs to be repainted
	 */
	public void paint(Graphics g)
	{
		//Update to the correct size if the image buffer's size is out of sync with the display's size
		if (shouldResize || this.getWidth() != buffer.getWidth() || this.getHeight() != buffer.getHeight())
			resize();
		
		//Clear the filled squares
		grid.clear();
		
		//Clear the image buffer
		g2d.clearRect(0, 0, buffer.getWidth(), buffer.getHeight());
		
		//If the first point exists
		if (existsStart)
		{
			//Draw the point
			g2d.fillOval(toPixelX(startX) - 4, toPixelY(startY) - 4, 8, 8);
			
			//Fill in the square the point is taking up
			grid.fillSquare((int) Math.floor(startX), (int) Math.floor(startY), cell_size);
			
			//If the second point exists
			if (existsEnd)
			{
				//Draw the point
				g2d.fillOval(toPixelX(endX) - 4, toPixelY(endY) - 4, 8, 8);
				
				//Draw the line
				g2d.drawLine(toPixelX(startX), toPixelY(startY), toPixelX(endX), toPixelY(endY));

				//Ray-trace between both points
				raytrace(startX, startY, endX, endY);
			}
		}
		
		//Clear the background to white
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		//Draw the selected squares, and then draw the axis lines above it, and then draw the points and the line between them
		g.drawImage(grid.getImage(), 0, 0, null);
		g.drawImage(lines.getImage(), 0, 0, null);
		g.drawImage(buffer, 0, 0, null);
	}
	
	public void raytrace(float startX, float startY, float endX, float endY)
	{
		//The exact x coordinate of the square that the first point is in
		int x = (int) Math.floor(startX);
		//The exact y coordinate of the square that the first point is in
		int y = (int) Math.floor(startY);
		
		//In which direction is the ray pointing? -1 if the second point is to the left of the first point, 1 otherwise
		int stepX = (endX - startX) < 0 ? -1 : 1;
		//In which direction is the ray pointing? -1 if the second point is below the first point, 1 otherwise
		int stepY = (endY - startY) < 0 ? -1 : 1;
		
		//How much percent of the total x distance does the ray need to travel to enter the next square on the x axis 
		float tMaxX = (stepX < 0 ? startX - x : 1 + x - startX) / Math.abs(endX - startX);
		//How much percent of the total y distance does the ray need to travel to enter the next square on the y axis
		float tMaxY = (stepY < 0 ? startY - y : 1 + y - startY) / Math.abs(endY - startY);
		
		//The percent of the total x distance that one square takes up
		float tDeltaX = 1 / Math.abs(endX - startX);
		//The percent of the total y distance that one square takes up
		float tDeltaY = 1 / Math.abs(endY - startY);
		
		//While the ray-tracer has travelled less than 100% of the total x axis distance and the total y axis distance
		while (tMaxX < 1 || tMaxY < 1)
		{
			//If the ray-tracer has travelled percentually less on the x axis than on the y axis 
			if (tMaxX < tMaxY)
			{
				//Add the percent of the total x distance one square takes up to the total percent travelled on the x axis
				tMaxX += tDeltaX;
				//Move the x coordinate to the next square on the x axis
				x += stepX;
			} else //Otherwise
			{
				//Add the percent of the total y distance one square takes up to the total percent travelled on the y axis
				tMaxY += tDeltaY;
				//Move the y coordinate to the next square on the y axis
				y += stepY;
			}
			//Select this square
			grid.fillSquare(x, y, cell_size);
		}
	}
	
	/**
	 * Converts an x coordinate to the corresponding pixel on the display
	 * @param x - the coordinate to be converted to a pixel location
	 * @return x pixel on the display
	 */
	public int toPixelX(float x)
	{
		return (int) (buffer.getWidth() / 2f + x * cell_size);
	}

	/**
	 * Converts a y coordinate to the corresponding pixel on the display
	 * @param y - the coordinate to be converted to a pixel location
	 * @return y pixel on the display
	 */
	public int toPixelY(float y)
	{
		return (int) (buffer.getHeight() / 2f - y * cell_size);
	}

	/**
	 * Converts a pixel to the corresponding coordinate on it
	 * @param x - the location of the pixel on the display
	 * @return The x coordinate that this pixel contains
	 */
	public float toCoordX(int x)
	{
		return (x - buffer.getWidth() / 2f) / cell_size;
	}

	/**
	 * Converts a pixel to the corresponding coordinate on it
	 * @param y - the location of the pixel on the display
	 * @return The y coordinate that this pixel contains
	 */
	public float toCoordY(int y)
	{
		return (buffer.getHeight() / 2f - y) / cell_size;
	}

	public void mousePressed(MouseEvent e)
	{
		//If the first point hasn't been set yet
		if (!setStart)
		{
			//Set it
			startX = toCoordX(e.getX());
			startY = toCoordY(e.getY());
			existsStart = true;
			setStart = true;
		} else if (!setEnd) //Else if the second point hasn't been set yet
		{
			//Set it
			endX = toCoordX(e.getX());
			endY = toCoordY(e.getY());
			existsEnd = true;
			setEnd = true;
		} else
		{
			//Otherwise, clear all the points
			setStart = false;
			setEnd = false;
			existsStart = false;
			existsEnd = false;
		}
		
		//Tell the UI that it needs to be repainted
		repaint();
	}

	public void mouseExited(MouseEvent e)
	{
		//If the first point hasn't been set yet
		if (!setStart)
			existsStart = false;
		//If the second point hasn't been set yet
		if (!setEnd) //Make sure the hovering point gets removed
			existsEnd = false; //Make sure the hovering point gets removed
		
		//Tell the UI that it needs to be repainted
		repaint();
	}

	public void mouseMoved(MouseEvent arg0)
	{
		//If the first point hasn't been set yet
		if (!setStart)
		{
			//Move the first point to the mouse
			startX = toCoordX(arg0.getX());
			startY = toCoordY(arg0.getY());
			existsStart = true;
		} else if (!setEnd) //Else if the second point hasn't been set yet
		{
			//Move the second point to the mouse
			endX = toCoordX(arg0.getX());
			endY = toCoordY(arg0.getY());
			existsEnd = true;
		}
		
		//Tell the UI that it needs to be repainted
		repaint();
	}
	
	public void mouseDragged(MouseEvent arg0)
	{
		mouseMoved(arg0);
	}

	public void mouseWheelMoved(MouseWheelEvent e)
	{
		//Scroll up to zoom in twice, scroll down to zoom out twice
		cell_size *= Math.pow(2, -e.getPreciseWheelRotation());
		//If the cell size is 0, set it to 1
		if (cell_size == 0)
			cell_size = 1;
		else if (cell_size > Integer.MAX_VALUE >> 1)
			cell_size = Integer.MAX_VALUE >> 1;
		
		//Tell the UI that it needs to be repainted and that the grid and lines need to be resized to fit the new cell size
		shouldResize = true;
		repaint();
	}

	public void mouseEntered(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
	public void mouseClicked(MouseEvent e){}
}
