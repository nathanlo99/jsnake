import java.awt.Graphics;

// Every entity in the game has these two methods
public interface Entity {
	// Updates position, gets input, etc.
	public void update();
	
	// Renders the entity to the screen
	public void render(Graphics g);
}
