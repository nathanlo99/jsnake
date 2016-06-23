import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Game {

	// The players currently playing in the game
	private ArrayList<Player> players = new ArrayList<Player>();
	// The players who were killed last frame
	private ArrayList<Player> playersToRemove = new ArrayList<Player>();
	// The pieces of food currently in the game
	private ArrayList<Food> foods = new ArrayList<Food>();
	// The pieces of food that were eaten in the last frame.
	private ArrayList<Food> foodsToRemove = new ArrayList<Food>();
	// Computers currently playing the game.
	private ArrayList<AI> comps = new ArrayList<AI>();
	// Computers who were killed last frame.
	private ArrayList<AI> compsToRemove = new ArrayList<AI>();

	private ArrayList<String> deathOrder = new ArrayList<String>();
	private ArrayList<Integer> deathScores = new ArrayList<Integer>();

	// Number of pieces of food to randomly generate next frame.
	// (The number of yellow foods that were eaten last frame).
	private int foodsToAdd = 0;

	// Whether the number of players + computers is 0.
	private boolean ended = false;

	public Game(State mode) {
		if (mode == State.DEMO) {
			comps.add(new AI(this, 100, 100, Color.GREEN, 300, 0.15f));
			comps.add(new AI(this, 300, 100, Color.RED, 400, 0.1f));
			comps.add(new AI(this, 500, 100, Color.CYAN, 700, 0.2f));
			comps.add(new AI(this, 700, 100, Color.YELLOW, 700, 0.25f));
			comps.add(new AI(this, 900, 100, Color.ORANGE, 700, 0.30f));
			comps.add(new AI(this, 1100, 100, Color.PINK, 700, 0.35f));
		} else if (mode == State.SINGLE) {
			players.add(new Player(this, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, 300, 300, Color.RED, "Player 1"));
			comps.add(new AI(this, 100, 100, Color.GREEN, 300, 0.15f));
		} else if (mode == State.MULTI) {
			players.add(new Player(this, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, 300, 300, Color.RED, "Nathan"));
			players.add(new Player(this, KeyEvent.VK_A, KeyEvent.VK_D, 500, 500, Color.BLUE, "Player 2"));
		}

		// Food
		for (int i = 0; i < players.size() + comps.size(); i++) {
			foods.add(new Food());
		}
	}

	public void update() {

		// Removes all the things removed in the last frame
		for (Food f : foodsToRemove)
			foods.remove(f);
		for (Player p : playersToRemove) {
			players.remove(p);
			deathOrder.add(p.getName());
			deathScores.add(p.getSegments().size());
		}

		for (AI comp : compsToRemove) {
			comps.remove(comp);
			deathOrder.add(comp.getName());
			deathScores.add(comp.getSegments().size());
		}

		// Generates all the randomly generated foods needed
		for (int i = 0; i < foodsToAdd; i++)
			foods.add(new Food());

		// Resets all counters
		foodsToAdd = 0;
		foodsToRemove.clear();
		playersToRemove.clear();
		compsToRemove.clear();

		// If the game has no more players, end the game
		if (players.size() + comps.size() == 0)
			endGame();

		// Update all the players
		for (Player p : players) {
			p.move();
			p.update();
		}

		// Update all the AI's
		for (AI comp : comps) {
			comp.move();
			comp.update();
		}
		
		// Finds out who's in the lead and displays this in the info box
		int maxScore = 0;
		String lead = "--";
		for (Player p : players) {
			if (playersToRemove.contains(p))
				continue;
			if (p.getSegments().size() > maxScore) {
				lead = p.getName();
				maxScore = p.getSegments().size();
			}
		}
		for (AI p : comps) {
			if (compsToRemove.contains(p))
				continue;
			if (p.getSegments().size() > maxScore) {
				lead = p.getName();
				maxScore = p.getSegments().size();
			}
		}
		MainComponent.setTitle("LEADER: " + lead + " | " + "SCORE: " + maxScore);
	}
	
	// Renders everything (players, food, and AIs)
	public void render(Graphics g) {
		for (Player p : players)
			p.render(g);
		for (Food f : foods)
			f.render(g);
		for (AI comp : comps)
			comp.render(g);
	}
	
	public void spawnFood() {
		foodsToAdd++;
	}

	public void endGame() {
		ended = true;
		// Prints out the winner's name
		System.out.println("WINNER: " + deathOrder.get(deathOrder.size() - 1) + " | SCORE: "
				+ deathScores.get(deathScores.size() - 1));
	}

	public void removeFood(Food f) {
		if (!foodsToRemove.contains(f))
			foodsToRemove.add(f);
	}

	public void removePlayer(Player p) {
		// Before removing a player, I generate a piece of food, the color of
		// the segment, scattered a random amount of pixels away from the
		// original segment
		if (playersToRemove.contains(p) || compsToRemove.contains(p))
			return;

		final ArrayList<Vector2> segments = p.getSegments();
		final int length = segments.size();
		int red = p.getColor().getRed();
		int green = p.getColor().getGreen();
		int blue = p.getColor().getBlue();
		final float dr = (float) (255 - red) / length;
		final float dg = (float) (255 - green) / length;
		final float db = (float) (255 - blue) / length;

		for (Vector2 v : segments) {
			final float dx = (float) Math.random() * 16 - 8;
			final float dy = (float) Math.random() * 16 - 8;
			red += dr;
			green += dg;
			blue += db;
			foods.add(
					new Food(v.getX() + dx, v.getY() + dy, 8, false, 1, new Color((int) red, (int) green, (int) blue)));
		}
		// Now, we can remove the player / AI
		if (p instanceof AI)
			compsToRemove.add((AI) p);
		else
			playersToRemove.add(p);

	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public int getTotalSegments() {
		// Get the total number of segments still in the game
		int result = 0;
		for (Player p : players) {
			if (playersToRemove.contains(p))
				continue;
			result += p.getSegments().size();
		}
		for (AI comp : comps) {
			if (compsToRemove.contains(comp))
				continue;
			result += comp.getSegments().size();
		}
		return result;
	}

	public ArrayList<AI> getComps() {
		return comps;
	}

	public ArrayList<AI> getCompsToRemove() {
		return compsToRemove;
	}

	public ArrayList<Food> getFood() {
		return foods;
	}

	public ArrayList<Player> getPlayersToRemove() {
		return playersToRemove;
	}

	public ArrayList<Food> getFoodsToRemove() {
		return foodsToRemove;
	}

	public boolean getEnded() {
		return ended;
	}
}
