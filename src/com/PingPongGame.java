package com;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PingPongGame {
	public static void main(String[] args) {
		JFrame frame = new JFrame("Pong Game");
		GamePanel gamePanel = new GamePanel();
		frame.add(gamePanel);
		frame.setResizable(false);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}

@SuppressWarnings("serial")
class GamePanel extends JPanel implements Runnable, KeyListener {
	static final int WIDTH = 800;
	static final int HEIGHT = 600;
	private static final int WIN_SCORE = 5;

	private Thread gameThread;
	private Paddle player1;
	private Paddle player2;
	private Ball ball;
	private int score1;
	private int score2;

	public GamePanel() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(Color.BLACK);
		addKeyListener(this);
		setFocusable(true);

		player1 = new Paddle(20, HEIGHT / 2 - 50);
		player2 = new Paddle(WIDTH - 40, HEIGHT / 2 - 50);
		resetBall();

		gameThread = new Thread(this);
		gameThread.start();
	}

	@Override
	public void run() {
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;

		while (true) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			if (delta >= 1) {
				update();
				repaint();
				delta--;
			}
		}
	}

	private void update() {
		if (!checkWinCondition()) {
			player1.move();
			player2.move();
			ball.move();
			checkCollision();
		}
	}

	private void checkCollision() {
		if (ball.getBounds().intersects(player1.getBounds()) || ball.getBounds().intersects(player2.getBounds())) {
			ball.reverseX();
		}

		if (ball.getY() <= 0 || ball.getY() >= HEIGHT - 30) {
			ball.reverseY();
		}

		if (ball.getX() <= 0) {
			score2++;
			resetBall();
		} else if (ball.getX() >= WIDTH - 30) {
			score1++;
			resetBall();
		}
	}

	private boolean checkWinCondition() {
		if (score1 >= WIN_SCORE || score2 >= WIN_SCORE) {
			String winner = score1 >= WIN_SCORE ? "Player 1" : "Player 2";
			JOptionPane.showMessageDialog(this, winner + " wins the game!");
			score1 = 0;
			score2 = 0;
			resetBall();
			return true;
		}
		return false;
	}

	private void resetBall() {
		ball = new Ball(WIDTH / 2 - 10, HEIGHT / 2 - 10);
		if (Math.random() < 0.5)
			ball.reverseX();
		if (Math.random() < 0.5)
			ball.reverseY();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}

	private void draw(Graphics g) {
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", Font.BOLD, 30));

		// Draw center line
		g.drawLine(WIDTH / 2, 0, WIDTH / 2, HEIGHT);

		player1.draw(g);
		player2.draw(g);
		ball.draw(g);

		g.drawString("Player 1: " + score1, 20, 30);
		g.drawString("Player 2: " + score2, WIDTH - 180, 30);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_W)
			player1.setUp(true);
		if (key == KeyEvent.VK_S)
			player1.setDown(true);
		if (key == KeyEvent.VK_UP)
			player2.setUp(true);
		if (key == KeyEvent.VK_DOWN)
			player2.setDown(true);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_W)
			player1.setUp(false);
		if (key == KeyEvent.VK_S)
			player1.setDown(false);
		if (key == KeyEvent.VK_UP)
			player2.setUp(false);
		if (key == KeyEvent.VK_DOWN)
			player2.setDown(false);
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
}

class Paddle {
	private static final int WIDTH = 20;
	private static final int HEIGHT = 100;
	private int x, y;
	private int ySpeed;
	private boolean up, down;

	public Paddle(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void move() {
		if (up)
			ySpeed = -5;
		else if (down)
			ySpeed = 5;
		else
			ySpeed = 0;

		y += ySpeed;
		y = Math.max(0, Math.min(y, GamePanel.HEIGHT - HEIGHT));
	}

	public void draw(Graphics g) {
		g.fillRect(x, y, WIDTH, HEIGHT);
	}

	public Rectangle getBounds() {
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}

	public void setUp(boolean up) {
		this.up = up;
	}

	public void setDown(boolean down) {
		this.down = down;
	}
}

class Ball {
	private int x, y;
	private int xSpeed = 5;
	private int ySpeed = 5;
	private static final int SIZE = 20;

	public Ball(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void move() {
		x += xSpeed;
		y += ySpeed;
	}

	public void reverseX() {
		xSpeed *= -1;
	}

	public void reverseY() {
		ySpeed *= -1;
	}

	public void draw(Graphics g) {
		g.fillOval(x, y, SIZE, SIZE);
	}

	public Rectangle getBounds() {
		return new Rectangle(x, y, SIZE, SIZE);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}