import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard implements KeyListener {

	public static final int NUM_KEYS = 600; // The maximum number of keys on a keyboard
	private static boolean[] lastPressed = new boolean[NUM_KEYS]; // The keys that were pressed during the last frame.
	private static boolean[] curPressed = new boolean[NUM_KEYS]; // The keys that were pressed during the current frame.
	
	// Refreshes the key states
	public static void update() {
		for (int i = 0; i < NUM_KEYS; i++)
			lastPressed[i] = curPressed[i];
	}
	
	public static boolean keyDown(int keyCode) {
		return lastPressed[keyCode];
	}

	public static boolean keyJustPressed(int keyCode) {
		return curPressed[keyCode] && !lastPressed[keyCode];
	}
	
	public void keyPressed(KeyEvent e) {
		curPressed[e.getKeyCode()] = true;
	}

	public void keyReleased(KeyEvent e) {
		curPressed[e.getKeyCode()] = false;
	}

	public void keyTyped(KeyEvent e) {
		return;
	}

}
