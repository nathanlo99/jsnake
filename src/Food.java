import java.awt.Color;
import java.awt.Graphics;

public class Food implements Entity {
	private Vector2 location; // The location of the food
	private int radius, value; // The value is the amount of segments the player
								// gains when they eat the food
	private boolean generateMoreWhenEaten; // Whether to generate more food when
											// this one gets eaten.
	private Color color; // The color of the food.

	// Default constructor for regenerating, randomly generated (yellow) food.
	public Food() {
		this((float) (Math.random() * MainComponent.width), (float) (Math.random() * MainComponent.height), 9, true, 5,
				Color.YELLOW);
	}

	// Constructor used when generating the food when a player dies
	public Food(float x, float y, int radius, boolean generateMoreWhenEaten, int value, Color color) {
		this.location = new Vector2(x, y);
		this.radius = radius;
		this.value = value;
		this.generateMoreWhenEaten = generateMoreWhenEaten;
		this.color = color;
	}

	public void render(Graphics g) {
		// A piece of food is rendered as a filled circle with a white outline.
		for (Vector2 v : location.getLoopedPositions()) {
			g.setColor(color);
			g.fillOval((int) v.getX(), (int) v.getY(), radius, radius);
			g.setColor(Color.WHITE);
			g.drawOval((int) v.getX(), (int) v.getY(), radius - 1, radius - 1);
		}
	}

	// Attracts the food towards a vector on the screen.
	// Used to attract food towards the player.
	public void attractTowards(Vector2 v) {
		location = location.add(new Vector2((v.getX() - location.getX()) * 0.5f, (v.getY() - location.getY()) * 0.5f));
	}

	public Vector2 getLocation() {
		return location;
	}

	public float getRadius() {
		return radius;
	}

	public int getValue() {
		return value;
	}

	public boolean generateMore() {
		return generateMoreWhenEaten;
	}

	public void update() {
	}
}
