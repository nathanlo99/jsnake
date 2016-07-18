import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class MainComponent extends Canvas implements Runnable {

	private static final long serialVersionUID = -7148361401924621095L;

	// Width and height of the game window
	public static int width = 1000, height = 750;
	public static String title = "Snake - Nathan Lo"; // The title of the game
	public static float fpsCap = 30; // The maximum update and frame rate
										// the game will run at

	private boolean hideCursor = true; // Whether to use the custom cursor

	private Thread thread;
	private static JFrame frame;
	private Mouse mouse; // A manager to listen to mouse movements and button
							// presses
	private Keyboard keyboard; // A manager to listen to key presses

	private boolean running = false; // Whether the game has been started and is
										// running

	private int fps; // The FPS the game is currently running at

	private StateManager manager; // Manages the game states (SINGLEPLAYER, MULTIPLAYER, etc).

	public static final Font infoFont = new Font("Menlo", Font.ITALIC | Font.BOLD, 15); // The font used to print information onto the screen

	public MainComponent() {
		// Sets the size of the canvas
		setPreferredSize(new Dimension(width, height));

		// Instantiates input managers and the game
		mouse = new Mouse();
		keyboard = new Keyboard();

		manager = new StateManager();

		addMouseListener(mouse);
		addMouseMotionListener(mouse);
		addKeyListener(keyboard);

		// Instantiates the JFrame, with settings
		frame = new JFrame();
		frame.setResizable(false); // Resizing off
		frame.setTitle(title);
		frame.add(this);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Will exit when
																// the 'X' is
																// pressed
		frame.setLocationRelativeTo(null);
		frame.setVisible(true); // Show the window

		// Make this window the top window
		requestFocus();

		// If the custom cursor is used, set the cursor to a blank image
		if (hideCursor) {
			frame.setCursor(Toolkit.getDefaultToolkit()
					.createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(), ""));
		}

		// Start the game
		start();
	}

	private void render() {
		// Gets the 'Graphics' object
		final BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(2);
			return;
		}

		Graphics g = bs.getDrawGraphics();

		// Clears the screen
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.WHITE);
		g.drawRect(0, 0, width, height);

		// Renders the game
		manager.render(g);
		g.setColor(Color.WHITE);
		g.setFont(infoFont);
		g.drawRect(20, 30, 550, 50);
		g.drawString(getTitle(), 30, 50);
		g.drawString(fps + " FPS / " + fpsCap + " MAX_FPS", 30, 70);

		// Renders the cursor on top of the game
		if (hideCursor) {
			g.setColor(Color.GREEN);
			final int mx = Mouse.getX();
			final int my = Mouse.getY();
			g.drawLine(mx, my - 4, mx, my + 4);
			g.drawLine(mx - 4, my, mx + 4, my);
		}

		// Cleans up resources
		g.dispose();
		bs.show();
	}

	public void run() {
		// The main loop
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		double delta = 0;
		int frames = 0;
		while (running) {
			// The code is written in such a way that the FPS is capped 
			final double ns = 1000000000.0 / fpsCap;
			final long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				update();
				delta--;
				render();
				frames++;
				if (System.currentTimeMillis() - timer > 1000) {
					timer += 1000;
					fps = frames;
					frames = 0;
				}
			}
		}
		stop();
	}

	public synchronized void start() {
		// Starts the display / game thread
		running = true;
		thread = new Thread(this, "Display");
		thread.start();
	}

	public synchronized void stop() {
		// Stops the display / game thread
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// Updates everything
	public void update() {
		manager.update();
		Keyboard.update();
	}

	public static String getTitle() {
		return frame.getTitle();
	}

	public static void setTitle(String title) {
		frame.setTitle(title);
	}

	public static void main(String[] args) {
		new MainComponent();
	}
}