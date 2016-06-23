import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

public class StateManager {

	private Game game;
	private State state = State.MENU, lastState;

	private static final Font pauseFont = new Font("Menlo", Font.BOLD | Font.ITALIC, 20);
	private static final Font menuFont = new Font("Menlo", Font.BOLD | Font.ITALIC, 50);
	private static final Font instructionTitleFont = new Font("Menlo", Font.BOLD | Font.ITALIC, 30);
	private static final Font instructionBodyFont = new Font("Menlo", Font.BOLD | Font.ITALIC, 24);
	private static final Font lockedFont = new Font("Menlo", Font.BOLD | Font.ITALIC, 16);
	
	private boolean singlePlayerUnlocked = false;
	private boolean multiPlayerUnlocked = false;
	private boolean instructionsRead = false;
	private boolean demoWatched = false;

	public void update() {
		if (state == State.NONE) {
			System.err.println("BUG: Game state was set to NONE");
		}

		if (Keyboard.keyDown(KeyEvent.VK_UP)) {
			MainComponent.fpsCap += 0.1;
		} else if (Keyboard.keyDown(KeyEvent.VK_DOWN)) {
			MainComponent.fpsCap -= 0.1;
		}

		if (instructionsRead) {
			singlePlayerUnlocked = true;
			multiPlayerUnlocked = true;
		}

		if (state == State.MENU) {
			if (Mouse.getB() != 1)
				return;
			if (Mouse.inRectangle(240, 295, 300, 35) && singlePlayerUnlocked) {
				state = State.SINGLE;
				game = new Game(state);
				return;
			} else if (Mouse.inRectangle(250, 345, 300, 35) && multiPlayerUnlocked) {
				state = State.MULTI;
				game = new Game(state);
				return;
			} else if (Mouse.inRectangle(260, 395, 300, 35)) {
				state = State.INSTRUCTIONS;
				return;
			} else if (Mouse.inRectangle(270, 445, 300, 35)) {
				state = State.DEMO;
				game = new Game(state);
				demoWatched = true;
				return;
			} else if (Mouse.inRectangle(280, 495, 300, 35)) {
				System.exit(0);
			}

		} else if (state == State.INSTRUCTIONS) {
			instructionsRead = true;
			if (Mouse.getB() == 1 && Mouse.inRectangle(100, 615, 300, 35)) {
				state = State.MENU;
			}
			return;
		} else if (state == State.PAUSE) {

			if (Keyboard.keyJustPressed(KeyEvent.VK_P)) {
				state = lastState;
				lastState = State.NONE;
				return;
			}
			if (Keyboard.keyJustPressed(KeyEvent.VK_Q)) {
				state = State.MENU;
				return;
			}
			if (Keyboard.keyJustPressed(KeyEvent.VK_R)) {
				state = lastState;
				lastState = State.NONE;
				game = new Game(state);
				return;
			}
		} else {
			if (game == null) {
				System.err.println("BUG: Game is null while state is not MENU");
				System.exit(200);
			}
			if (Keyboard.keyJustPressed(KeyEvent.VK_P)) {
				lastState = state;
				state = State.PAUSE;
				return;
			}
			game.update();
			if (game.getEnded()) {
				state = State.MENU;
				game = null;
				return;
			}
		}
	}

	public void render(Graphics g) {
		if (state == State.MENU) {
			g.setFont(menuFont);
			g.drawString(MainComponent.title, 200, 250);
			g.setFont(pauseFont);
			g.drawString("SINGLEPLAYER", 250, 320);
			g.drawString("MULTIPLAYER", 260, 370);

			g.drawString("INSTRUCTIONS", 270, 420);
			g.drawString("DEMO", 280, 470);
			g.drawString("QUIT", 290, 520);
			if (!singlePlayerUnlocked) {
				g.setColor(Color.DARK_GRAY);
				g.fillRect(243, 298, 294, 29);
				g.setFont(lockedFont);
				g.setColor(Color.LIGHT_GRAY);
				g.drawString("READ INSTRUCTIONS TO UNLOCK", 250, 319);
				g.setColor(Color.WHITE);
				g.setFont(pauseFont);
			}
			if (!multiPlayerUnlocked) {
				g.setColor(Color.DARK_GRAY);
				g.fillRect(253, 348, 294, 29);
				g.setFont(lockedFont);
				g.setColor(Color.LIGHT_GRAY);
				g.drawString("READ INSTRUCTIONS TO UNLOCK", 260, 369);
				g.setColor(Color.WHITE);
				g.setFont(pauseFont);
			}
			g.drawRect(240, 295, 300, 35);
			g.drawRect(250, 345, 300, 35);
			
			if (!instructionsRead) {
				g.setColor(Color.RED);
				g.drawRect(258, 393, 304, 39);
				g.setColor(Color.WHITE);
			}
			g.drawRect(260, 395, 300, 35); // INSTRUCTIONS
			
			if (!demoWatched) {
				g.setColor(Color.RED);
				g.drawRect(268, 443, 304, 39);
				g.setColor(Color.WHITE);
			}
			
			g.drawRect(270, 445, 300, 35); // DEMO
			g.drawRect(280, 495, 300, 35); // QUIT
		} else if (state == State.PAUSE) {
			game.render(g);
			g.setFont(pauseFont);
			g.setColor(Color.WHITE);
			g.drawString("[ PAUSED - Press P to unpause | Q to quit | R to restart ]", 100, 150);
		} else if (state == State.INSTRUCTIONS) {
			g.setFont(instructionTitleFont);
			g.drawLine(500, 100, 500, 700);
			g.drawString("SINGLEPLAYER", 100, 150);
			g.drawString("MULTIPLAYER", 550, 150);

			g.setFont(instructionBodyFont);
			g.drawString("PLAYER 1 CONTROLS:", 100, 200);
			g.drawString("ROTATE LEFT: LEFT ARROW", 110, 230);
			g.drawString("ROTATE RIGHT: RIGHT ARROW", 110, 260);

			g.drawString("PLAYER 1 CONTROLS:", 550, 200);
			g.drawString("ROTATE LEFT: LEFT ARROW", 560, 230);
			g.drawString("ROTATE RIGHT: RIGHT ARROW", 560, 260);

			g.drawString("PLAYER 2 CONTROLS:", 550, 300);
			g.drawString("ROTATE LEFT: A", 560, 330);
			g.drawString("ROTATE RIGHT: D", 560, 360);

			g.setFont(pauseFont);
			g.drawString("BACK TO MENU", 110, 640);
			g.drawRect(100, 615, 300, 35);
		} else {
			game.render(g);
		}
	}
}
