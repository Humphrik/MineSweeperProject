import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MineSweeper {
	final static int FIELD_SIZE = 8;
	final static int MINE_COUNT = 2;
	final static int WINDOW_SIZE = 900;

	static JFrame frame = new JFrame("MineSweeper");
	static JPanel panel = new JPanel(new GridBagLayout());
	static GridBagConstraints c = new GridBagConstraints();

	static JButton[][] field = new JButton[FIELD_SIZE][FIELD_SIZE];
	static boolean[][] mines = new boolean[FIELD_SIZE][FIELD_SIZE];

	static boolean lost = false;

	public static void main(String[] args) {
		initializeField();
		frame.add(panel); // Adds the panel to the window.
		frame.setSize(WINDOW_SIZE, WINDOW_SIZE);
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void initializeField() { // Sets up the buttons

		for (int i = 0; i < FIELD_SIZE; i++) {
			for (int j = 0; j < FIELD_SIZE; j++) {
				makeButton(i, j);
			}
		}

		for (int i = 0; i < MINE_COUNT; i++) { // Places the Mines
			boolean occupied;
			int localX;
			int localY;
			do {
				occupied = false;
				localX = (int) (Math.random() * FIELD_SIZE);
				localY = (int) (Math.random() * FIELD_SIZE);
				if (mines[localX][localY]) { // Tests for repeated values
					occupied = true;
					System.out.println("OCCUPIED AT: (" + localX + "," + localY + ")");
				} else { // Good to go!
					mines[localX][localY] = true;
				}
			} while (occupied);
			System.out.println("Mine at: (" + localX + "," + localY + ")");
		}

	}

	public static void makeButton(int localX, int localY) {
		field[localX][localY] = new JButton("?");
		JButton button = field[localX][localY];
		int dimensions = WINDOW_SIZE / (FIELD_SIZE * 2 + 2);
		button.setMinimumSize(new Dimension(dimensions, dimensions));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = localX;
		c.gridy = localY;
		c.ipadx = dimensions;
		c.ipady = dimensions;
		c.insets = new Insets(dimensions / 10, dimensions / 10, dimensions / 10, dimensions / 10);
		panel.add(field[localX][localY], c);
		button.addActionListener(new ActionListener() { // Runs specific code
			// when pressed.
			public void actionPerformed(ActionEvent e) {
				sweep(localX, localY, false);
			}
		});
	}

	public static void sweep(int hor, int ver, boolean clearing) {
		int adjacent = 0;
		if (!mines[hor][ver] && field[hor][ver].isEnabled()) {

			if (hor > 0) { // Any mines to the left?
				if (ver > 0) {
					if (mines[hor - 1][ver - 1]) {
						adjacent++;
					}
				}
				if (mines[hor - 1][ver]) {
					adjacent++;
				}
				if (ver < FIELD_SIZE - 1) {
					if (mines[hor - 1][ver + 1]) {
						adjacent++;
					}
				}
			}
			if (hor < FIELD_SIZE - 1) { // Any mines to the right?
				if (ver > 0) {
					if (mines[hor + 1][ver - 1]) {
						adjacent++;
					}
				}
				if (mines[hor + 1][ver]) {
					adjacent++;
				}
				if (ver < FIELD_SIZE - 1) {
					if (mines[hor + 1][ver + 1]) {
						adjacent++;
					}
				}
			}
			if (ver > 0) { // Above?
				if (mines[hor][ver - 1]) {
					adjacent++;
				}
			}
			if (ver < FIELD_SIZE - 1) { // Below?
				if (mines[hor][ver + 1]) {
					adjacent++;
				}
			}

			if (adjacent == 0 && !lost) {
				field[hor][ver].setText("0");
				field[hor][ver].setEnabled(false);
				if (hor > 0) {
					sweep(hor - 1, ver, true);
				}
				if (ver > 0) {
					sweep(hor, ver - 1, true);
				}
				if (ver < FIELD_SIZE - 1) {
					sweep(hor, ver + 1, true);
				}
				if (hor < FIELD_SIZE - 1) {
					sweep(hor + 1, ver, true);
				}
			} else if (!clearing) {
				field[hor][ver].setText(adjacent + "");
				field[hor][ver].setEnabled(false);
				if (!lost) {
					checkWin();
				}
			}
		} else if (mines[hor][ver]) { // If you hit a mine
			field[hor][ver].setBackground(Color.RED);
			field[hor][ver].setText("X");
			if (!lost) {
				gameOver();
			}
		}
	}

	public static void checkWin() {
		int unclicked = 0;
		for (JButton[] row : field) {
			for (JButton button : row) {
				if (button.isEnabled()) {
					unclicked++;
				}
			}
		}
		if (unclicked == MINE_COUNT) {
			for (JButton[] row : field) {
				for (JButton button : row) {
					if (button.isEnabled()) {
						button.setBackground(Color.GREEN);
					}
					button.setEnabled(false);
					panel.setBackground(Color.GRAY);
					frame.setTitle("You Won!");
				}
			}
		}
	}

	public static void gameOver() {
		lost=true;
		for (int i = 0; i < FIELD_SIZE; i++) {
			for (int j = 0; j < FIELD_SIZE; j++) {
				sweep(i, j, false);
			}
		}
		panel.setBackground(Color.GRAY);
		frame.setTitle("Game Over!");
	}
}
