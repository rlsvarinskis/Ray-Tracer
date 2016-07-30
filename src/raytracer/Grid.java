package raytracer;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * This utility class draws all selected squares on to an image, then lets other classes access it
 * @author xBobZx
 *
 */
public class Grid
{
	/**
	 * The image that the selected squares will be drawn on
	 */
	private BufferedImage grid;
	private Graphics2D grid2d;

	/**
	 * Resizes the image to be correct
	 * @param w - The new width
	 * @param h - The new height
	 */
	public void resize(int w, int h)
	{
		grid = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		grid2d = grid.createGraphics();

		//Sets the clearing color to be transparent
		grid2d.setBackground(Main.TRANSPARENT);
		
		//Sets the drawing color to be yellow
		grid2d.setColor(Color.YELLOW);
	}
	
	/**
	 * Clears the background
	 */
	public void clear()
	{
		grid2d.clearRect(0, 0, grid.getWidth(), grid.getHeight());
	}
	
	/**
	 * Fills a square with yellow
	 * @param x - The x coordinate of the square
	 * @param y - The y coordinate of the square
	 * @param cell - The size of one unit
	 */
	public void fillSquare(int x, int y, int cell)
	{
		grid2d.fillRect(grid.getWidth() / 2 + x * cell, grid.getHeight() / 2 - (y + 1) * cell, cell, cell);
	}
	
	/**
	 * @return an image with the filled squares
	 */
	public BufferedImage getImage()
	{
		return grid;
	}
}
