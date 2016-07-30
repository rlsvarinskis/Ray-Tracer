package raytracer;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * This utility class renders the lines that mark each unit
 * @author xBobZx
 *
 */
public class Lines
{
	/**
	 * The image on which the lines are rendered
	 */
	private BufferedImage lines;
	private Graphics2D lines2d;

	/**
	 * 
	 * @param w - The new width
	 * @param h - The new height
	 * @param cell - The amount of pixels one unit takes up
	 */
	public void resize(int w, int h, int cell)
	{
		lines = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		lines2d = lines.createGraphics();
		
		//Sets the clearing color to be transparent, so that other stuff may be drawn under this
		lines2d.setBackground(Main.TRANSPARENT);
		
		//Sets the drawing color to black
		lines2d.setColor(Color.BLACK);
		
		//Draws the lines
		draw(cell);
	}
	
	/**
	 * Draws all the lines on the image
	 * @param cell - The amount of pixels on unit takes up
	 */
	private void draw(int cell)
	{
		//Clears the whole image with a transparent background
		lines2d.clearRect(0, 0, lines.getWidth(), lines.getHeight());
		
		//Divides the width in half
		int centerX = lines.getWidth() >> 1;
		//Divides the height in half
		int centerY = lines.getHeight() >> 1;
		
		//Calculates how many full units can be displayed on this window on each half of the plane
		int w = centerX / cell;
		int h = centerY / cell;
		
		//Loops from the last line that can be displayed on the left side to the last line on the right side
		for (int i = centerX - w * cell; i < lines.getWidth(); i += cell)
		{
			//If this current line contains the origin, make it 3 pixels wide
			if (i == centerX)
				lines2d.setStroke(Main.THICK_3);
			else
				lines2d.setStroke(Main.THICK_1);
			lines2d.drawLine(i, 0, i, lines.getHeight());
		}

		//Loops from the last line that can be displayed on the top to the last line on the bottom
		for (int i = centerY - h * cell; i < lines.getHeight(); i += cell)
		{
			//If this current line contains the origin, make it 3 pixels wide
			if (i == centerY)
				lines2d.setStroke(Main.THICK_3);
			else
				lines2d.setStroke(Main.THICK_1);
			lines2d.drawLine(0, i, lines.getWidth(), i);
		}
	}
	
	/**
	 * @return an image containing the drawn lines
	 */
	public BufferedImage getImage()
	{
		return lines;
	}
}
