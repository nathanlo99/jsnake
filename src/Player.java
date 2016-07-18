import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;

public class Player implements Entity {
	// The keyboard codes for this player to go left and right.
	private int leftKey, rightKey;
	// The length of the snake.
	private int length;
	// The radius of the snake.
	protected int radius = 8;
	// The color of the player.
	protected Color color;
	// The locations of the previous segments of the snake.
	protected ArrayList<Vector2> previous;
	// The direction that the player is heading right now.
	protected Vector2 direction;
	protected Game game;

	protected String name;

	protected static final float SPEED = 10.0f; // Traveling speed
	protected static final float ROT_SPEED = 6.0f; // Rotating speed
	private static final float MAGNET_RADIUS = 8.0f; // The distance food will attract towards you
	protected static final float turnRadius = (float) (SPEED / (2f * Math.sin(Math.toRadians(ROT_SPEED / 2f)))); // The turn radius of the snake
	
	private static int[] reservedKeys = { KeyEvent.VK_P, KeyEvent.VK_Q, KeyEvent.VK_R, KeyEvent.VK_UP, KeyEvent.VK_DOWN };
	
	public static final Font nameFont = new Font("Menlo", Font.ITALIC | Font.BOLD, 12);

	public Player(Game game, int leftKey, int rightKey, float x, float y, Color color, String name) {
		this.game = game;
		
		// Crash if someone alters the code in a way that assigns a reserved key
		if (Arrays.binarySearch(reservedKeys, leftKey) != -1 || Arrays.binarySearch(reservedKeys, rightKey) != -1) {
			System.err.println("ERROR: Tried to give a player a reserved key");
			System.exit(1);
		}
		
		this.leftKey = leftKey;
		this.rightKey = rightKey;
		this.length = 5;
		this.name = "[PLAYER] " + name;
		
		// Starts the player off at point (x, y).
		this.previous = new ArrayList<Vector2>();
		for (int i = 0; i < length; i++) {
			this.previous.add(new Vector2(x, y - SPEED * i));
		}
		
		this.color = color;
		// The snake starts off going downwards
		this.direction = new Vector2(0, SPEED);
	}

	public void render(Graphics g) {
		// Renders the snake with a gradient going from its color to white.
		float red = (float) color.getRed();
		float green = (float) color.getGreen();
		float blue = (float) color.getBlue();
		final float dr = (float) (255 - red) / previous.size();
		final float dg = (float) (255 - green) / previous.size();
		final float db = (float) (255 - blue) / previous.size();
		for (int i = 0; i < previous.size(); i++) {
			Vector2 v = previous.get(i);
			red += dr;
			green += dg;
			blue += db;
			Color c = new Color((int) red, (int) green, (int) blue);
			for (Vector2 pos : v.getLoopedPositions()) {
				int x = (int) pos.getX();
				int y = (int) pos.getY();
				// Draw each player as a sequence of circles
				g.setColor(c);
				g.fillOval(x, y, radius, radius);
				g.setColor(Color.WHITE);
				g.drawOval(x, y, radius - 1, radius - 1);
			}
		}
		g.setColor(Color.WHITE);
		g.setFont(nameFont);
		g.drawString(name, (int) previous.get(0).getX() + 2, (int) previous.get(0).getY() - 2);
	}

	public void move() {
		// Get keyboard input and change direction
		boolean left = Keyboard.keyDown(leftKey);
		boolean right = Keyboard.keyDown(rightKey);
		if (left == right) // If both keys are down, don't do anything
			return;
		if (Keyboard.keyDown(leftKey))
			direction = direction.rotate(ROT_SPEED);
		if (Keyboard.keyDown(rightKey))
			direction = direction.rotate(-ROT_SPEED);
	}

	public void update() {
		// Update segment list
		if (length > previous.size())
			previous.add(new Vector2(0, 0));
		for (int i = previous.size() - 2; i >= 0; i--)
			previous.set(i + 1, previous.get(i));

		Vector2 head = previous.get(1).add(direction);

		float adjustedX = head.getX();
		float adjustedY = head.getY();
		if (adjustedX < 0)
			adjustedX += MainComponent.width;
		if (adjustedX > MainComponent.width)
			adjustedX -= MainComponent.width;
		if (adjustedY < 0)
			adjustedY += MainComponent.height;
		if (adjustedY > MainComponent.height)
			adjustedY -= MainComponent.height;
		previous.set(0, new Vector2(adjustedX, adjustedY));

		// MAGNETIZE FOOD
		for (Food f : game.getFood()) {
			if (f.getLocation().distanceTo(head) <= radius + MAGNET_RADIUS) {
				f.attractTowards(head);
			}
		}

		// Check if the head intersected things
		// FOOD
		for (Food f : game.getFood()) {
			if (game.getFoodsToRemove().contains(f))
				continue;
			if (head.distanceTo(f.getLocation()) <= (f.getRadius() + radius) / 2) {
				length += f.getValue();
				game.removeFood(f);
				if (f.generateMore())
					game.spawnFood();
			}
		}

		// OTHER PLAYERS
		for (Player p : game.getPlayers()) {
			if (p.equals(this) || game.getPlayersToRemove().contains(p))
				continue;
			for (Vector2 segment : p.previous) {
				if (head.distanceTo(segment) <= (p.radius + radius) / 2)
					game.removePlayer(this);
			}
		}

		// AI'S
		for (AI comp : game.getComps()) {
			if (comp.equals(this) || game.getCompsToRemove().contains(comp))
				continue;
			for (Vector2 segment : comp.getSegments()) {
				if (head.distanceTo(segment) <= (comp.radius + radius) / 2)
					game.removePlayer(this);
			}
		}

		// ITSELF
		for (int i = 2; i < previous.size(); i++) {
			if (head.distanceTo(previous.get(i)) <= radius)
				game.removePlayer(this);
		}
	}

	public ArrayList<Vector2> getSegments() {
		return previous;
	}

	public Color getColor() {
		return color;
	}

	public String getName() {
		return name;
	}
}
