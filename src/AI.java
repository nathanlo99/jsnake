import java.awt.Color;
import java.awt.Graphics;

public class AI extends Player implements Entity {

	private Vector2 target, avoid;
	private int tooClose;
	private float greed;

	public AI(Game game, float x, float y, Color color, int tooClose, float greed) {
		super(game, 0, 0, x, y, color, ""); // (Key values are unused, and name
											// will be reassigned)
		this.tooClose = tooClose;
		this.greed = greed;
		this.name = "[CPU] (" + tooClose + ", " + greed + ")";
	}

	private void updateTarget() {
		// Finds the closest piece of food on the map
		target = null;
		float minDist = Float.POSITIVE_INFINITY;
		for (Food f : game.getFood()) {
			if (game.getFoodsToRemove().contains(f))
				continue;
			for (Vector2 pos : f.getLocation().getLoopedPositions()) {
				final float dist = blindDistance(pos);
				if (dist < minDist) {
					minDist = dist;
					target = pos;
				}
			}
		}
	}

	private Vector2[] getEnemies() {
		// Gets all the segments of the things to avoid
		int numEnemies;
		int start = (int) (360 / ROT_SPEED);
		if (previous.size() > start)
			numEnemies = game.getTotalSegments() - start;
		else
			numEnemies = game.getTotalSegments() - previous.size();

		Vector2[] enemies = new Vector2[9 * numEnemies];
		int index = 0;
		for (Player p : game.getPlayers()) {
			if (game.getPlayersToRemove().contains(p))
				continue;
			for (Vector2 pos : p.getSegments()) {
				for (Vector2 v : pos.getLoopedPositions())
					enemies[index++] = v;
			}
		}
		for (AI p : game.getComps()) {
			if (game.getCompsToRemove().contains(p) || this.equals(p))
				continue;
			for (Vector2 pos : p.getSegments()) {
				for (Vector2 v : pos.getLoopedPositions())
					enemies[index++] = v;
			}
		}
		for (int i = start; i < previous.size(); i++) {
			for (Vector2 v : previous.get(i).getLoopedPositions())
				enemies[index++] = v;
		}
		return enemies;
	}

	private void updateAvoid() {
		// Finds the closest thing to avoid
		avoid = null;
		float minDist = tooClose;
		for (Vector2 v : getEnemies()) {
			if (v == null) continue;
			final float dist = distance(v);
			if (dist < minDist) {
				minDist = dist;
				avoid = v;
			}
		}
	}

	private float blindDistance(Vector2 thing) {
		final Vector2 head = previous.get(0);
		// There is a "blind-spot" for every snake, and if the target is within
		// that blind spot,
		// return INFINITY for the distance to it, so it doesn't chase its tail
		// forever.
		final float turnRadius = (float) (SPEED / (2 * Math.sin(Math.toRadians(ROT_SPEED / 2))));
		if (thing.distanceTo(head) <= 2 * turnRadius) {
			final Vector2 rightSide = direction.rotate(-90).makeLength(turnRadius).add(head);
			final Vector2 leftSide = direction.rotate(90).makeLength(turnRadius).add(head);
			if (thing.distanceTo(rightSide) < turnRadius || thing.distanceTo(leftSide) < turnRadius)
				return Float.POSITIVE_INFINITY;
		}
		return head.distanceTo(thing);
	}

	private float distance(Vector2 thing) {
		// Normal distance (Euclidian distance)
		return previous.get(0).distanceTo(thing);
	}

	private void moveTowardsTarget() {
		// Rotate towards the target
		// (Cross product magic)
		if (target != null)
			direction = direction.rotate(Math.copySign(ROT_SPEED, target.sub(previous.get(0)).cross(direction)));
	}

	private void avoidEnemy() {
		// Rotate away from the thing to avoid
		// (More cross product magic)
		if (avoid != null)
			direction = direction.rotate(Math.copySign(ROT_SPEED, -avoid.sub(previous.get(0)).cross(direction)));
	}

	public void render(Graphics g) {
		super.render(g);
		
		// Renders the green and red lines for AI's 
		if (target != null) {
			g.setColor(Color.GREEN);
			g.drawLine((int) previous.get(0).getX(), (int) previous.get(0).getY(), (int) target.getX(),
					(int) target.getY());
			final Vector2 temp = target.add(previous.get(0)).mul(0.5f);
			g.drawString("" + (int) distance(target), (int) temp.getX() + 2, (int) temp.getY() - 2);
		}

		if (avoid != null) {
			g.setColor(Color.RED);
			g.drawLine((int) previous.get(0).getX(), (int) previous.get(0).getY(), (int) avoid.getX(),
					(int) avoid.getY());
			Vector2 temp = avoid.add(previous.get(0)).mul(0.5f);
			g.drawString("" + (int) distance(avoid), (int) temp.getX() + 2, (int) temp.getY() - 2);
		}
	}

	public void move() {
		updateTarget();
		updateAvoid();
		// If there's nothing to avoid, or the AI becomes greedy, move towards target
		if (avoid == null || Math.random() < greed)
			moveTowardsTarget();
		else
			// Otherwise, avoid the enemy
			avoidEnemy();
	}

}
