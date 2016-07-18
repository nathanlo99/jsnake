
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Mouse implements MouseListener, MouseMotionListener {

	private static int mx = -1, my = -1, mb = -1; // X, Y, and button pressed

	public static int getB() {
		return mb;
	}

	public static int getX() {
		return mx;
	}

	public static int getY() {
		return my;
	}

	public static String getString() {
		return "(" + mx + ", " + my + ")";
	}
	
	// Whether the mouse is within a rectangle
	public static boolean inRectangle(int x1, int y1, int width, int height) {
		return mx > x1 && mx < x1 + width && my > y1 && my < y1 + height;
	}
	
	public void mouseClicked(MouseEvent e) {

	}

	public void mouseDragged(MouseEvent e) {
		mx = e.getX();
		my = e.getY();
	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	public void mouseMoved(MouseEvent e) {
		mx = e.getX();
		my = e.getY();
	}

	public void mousePressed(MouseEvent e) {
		mb = e.getButton();
	}

	public void mouseReleased(MouseEvent e) {
		mb = -1;
	}

}