
public class Vector2 {
	private float x, y;

	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float length() {
		return (float) Math.hypot((float)x, (float)y);
	}
	
	public Vector2 add(Vector2 other) {
		return new Vector2(x + other.x, y + other.y);
	}
	
	public Vector2 sub(Vector2 other) {
		return new Vector2(x - other.x, y - other.y);
	}
	
	public Vector2 mul(float r) {
		return new Vector2(x * r, y * r);
	}

	public float distanceTo(Vector2 other) {
		return sub(other).length();
	}

	public float cross(Vector2 other) {
		return x * other.y - y * other.x;
	}
	
	public Vector2 makeLength(float length) {
		return this.mul(length / length());
	}
	
	public Vector2 rotate(float amount) {
		final float cs = (float) Math.cos(Math.toRadians(-amount));
		final float sn = (float) Math.sin(Math.toRadians(-amount));
		return new Vector2(x * cs - y * sn, x * sn + y * cs);
	}
	
	public Vector2[] getLoopedPositions() {
		Vector2[] result = new Vector2[9];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				result[3 * i + j] = new Vector2(x + (i - 1) * MainComponent.width, y + (j - 1) * MainComponent.height);
			}
		}
		return result;
	}
	
	public String toString() {
		return "V2 ( " + x + " | " + y + ")";
	}

}
