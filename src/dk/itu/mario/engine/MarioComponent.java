package dk.itu.mario.engine;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;

import dk.itu.mario.PlayerCharacteristics;
import dk.itu.mario.level.Level;
import dk.itu.mario.level.MyLevel;
import dk.itu.mario.scene.LevelScene;
import dk.itu.mario.scene.LevelSceneTest;
import dk.itu.mario.scene.LoseScene;
import dk.itu.mario.scene.Scene;
import dk.itu.mario.scene.WinScene;

import dk.itu.mario.MarioInterface.LevelInterface;
import dk.itu.mario.engine.sonar.FakeSoundEngine;
import dk.itu.mario.engine.sonar.SonarSoundEngine;
import dk.itu.mario.engine.sprites.Mario;
import dk.itu.mario.engine.sprites.SpriteTemplate;

public class MarioComponent extends JComponent implements Runnable,
		KeyListener, FocusListener, MouseListener {
	private static final long serialVersionUID = 739318775993206607L;

	public static final int TICKS_PER_SECOND = 24;

	public static final int EVOLVE_VERSION = 4;
	public static final int GAME_VERSION = 4;

	private boolean running = false;
	private int width, height;
	private GraphicsConfiguration graphicsConfiguration;
	private Scene scene;
	private SonarSoundEngine sound;
	private boolean focused = false;
	private boolean useScale2x = false;
	private boolean isCustom = false;
	private int renderPastThisPoint = 5 * 16;
	private Scale2x scale2x = new Scale2x(320, 240);

	private double openTime;

	public MarioComponent(int width, int height, boolean isCustomized) {
		addFocusListener(this);
		addMouseListener(this);
		addKeyListener(this);

		this.setFocusable(true);
		this.setEnabled(true);
		this.width = width;
		this.height = height;
		this.isCustom = isCustomized;

		Dimension size = new Dimension(width, height);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);

		try {
			sound = new SonarSoundEngine(64);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			sound = new FakeSoundEngine();
		}
		this.setFocusable(true);

		LevelScene.bothPlayed = false;

		openTime = System.nanoTime();
	}

	private void toggleKey(int keyCode, boolean isPressed) {
		if (keyCode == KeyEvent.VK_LEFT) {
			scene.toggleKey(Mario.KEY_LEFT, isPressed);
		}
		if (keyCode == KeyEvent.VK_RIGHT) {
			scene.toggleKey(Mario.KEY_RIGHT, isPressed);
		}
		if (keyCode == KeyEvent.VK_DOWN) {
			scene.toggleKey(Mario.KEY_DOWN, isPressed);
		}
		if (keyCode == KeyEvent.VK_UP) {
			scene.toggleKey(Mario.KEY_UP, isPressed);
		}
		if (keyCode == KeyEvent.VK_A) {
			scene.toggleKey(Mario.KEY_SPEED, isPressed);
		}
		if (keyCode == KeyEvent.VK_S) {
			scene.toggleKey(Mario.KEY_JUMP, isPressed);
		}
		if (keyCode == KeyEvent.VK_ENTER) {
			scene.toggleKey(Mario.KEY_ENTER, isPressed);
		}
		if (keyCode == KeyEvent.VK_F1) {
			useScale2x = !useScale2x;
		}
		if (keyCode == KeyEvent.VK_1)
			weights[0] = 1;
		if (keyCode == KeyEvent.VK_2)
			weights[1] = 1;
		if (keyCode == KeyEvent.VK_3)
			weights[2] = 1;
		if (keyCode == KeyEvent.VK_4)
			weights[3] = 1;
		if (keyCode == KeyEvent.VK_5)
			weights[4] = 1;

		if (isPressed && keyCode == KeyEvent.VK_P) {
			System.out.println("P was pressed");
			// System.out.println(this.randomLevel.mario.x);
			// this.randomLevel.mario.y = 10;
			for (int i = 0; i < weights.length; i++)
				this.weights[i] = Math.random();
			// System.out.println(PlayerCharacteristics.getEnemies());
			// ((MyLevel) this.randomLevel.level).clear(0, 10);
			// ((MyLevel) this.randomLevel.level).buildStraightCustom(0, 10,
			// true);
			// ((MyLevel) this.randomLevel.level).fixWallsCustom(0, 10);

			// ((MyLevel) this.randomLevel.level).buildHillStraightCustom(0,
			// 10);

			// for (int i = 0; i < this.randomLevel.level.getMap().length; i++)
			// for (int j = 0; j < this.randomLevel.level.getMap()[i].length;
			// j++)
			// this.randomLevel.level.setBlock(i, 200, (byte) 0);
			;// this.randomLevel.level.getMap()[i][10] =
				// Level.HILL_FILL;

			// byte[][] temp = this.randomLevel.level.getMap();
			int x = 0;

			System.out.println(this.randomLevel.mario.x);// = 0;
		}
		if (isPressed && keyCode == KeyEvent.VK_C) {
			System.out.println("C was pressed");
			((MyLevel) this.randomLevel.level).clear(10, 1);
			count += 100;
		}
		if (isPressed && keyCode == KeyEvent.VK_ESCAPE) {
			try {
				System.exit(1);
			} catch (Exception e) {
				System.out.println("Unable to exit.");
			}
		}
	}

	int count = 0;

	public void paint(Graphics g) {
		super.paint(g);
	}

	public void update(Graphics g) {
	}

	public void start() {
		if (!running) {
			running = true;
			new Thread(this, "Game Thread").start();
		}
	}

	public void stop() {
		Art.stopMusic();
		running = false;
	}

	public void run() {
		for (int i = 0; i < weights.length; i++)
			weights[i] = .5;

		graphicsConfiguration = getGraphicsConfiguration();

		Art.init(graphicsConfiguration, sound);

		VolatileImage image = createVolatileImage(320, 240);
		Graphics g = getGraphics();
		Graphics og = image.getGraphics();
		int lastTick = -1;
		int renderedFrames = 0;
		int fps = 0;

		long startTime = System.nanoTime();

		float time = (System.nanoTime() - startTime) / 1000000000f;
		float now = time;
		float averagePassedTime = 0;

		boolean naiveTiming = true;
		if (isCustom)
			toCustomGame();
		else
			toRandomGame();

		float correction = 0f;
		if (System.getProperty("os.name") == "Mac OS X")
			;

		while (running) {
			Art.stopMusic();

			float lastTime = time;
			time = (System.nanoTime() - startTime) / 1000000000f;
			float passedTime = time - lastTime;

			if (passedTime < 0)
				naiveTiming = false; // Stop relying on nanotime if it starts
										// skipping around in time (ie running
										// backwards at least once). This
										// sometimes happens on dual core amds.
			averagePassedTime = averagePassedTime * 0.9f + passedTime * 0.1f;

			if (naiveTiming) {
				now = time;
			} else {
				now += averagePassedTime;
			}

			int tick = (int) (now * TICKS_PER_SECOND);

			if (lastTick == -1)
				lastTick = tick;

			while (lastTick < tick) {
				scene.tick();

				lastTick++;

				if (lastTick % TICKS_PER_SECOND == 0) {
					fps = renderedFrames;
					renderedFrames = 0;
				}
			}

			float alpha = (float) (now * TICKS_PER_SECOND - tick);
			sound.clientTick(alpha);

			int x = (int) (Math.sin(now) * 16 + 160);
			int y = (int) (Math.cos(now) * 16 + 120);

			og.setColor(Color.WHITE);
			og.fillRect(0, 0, 320, 240);
			// ADDING STUFF TO AUTORENDER
			int marioX = (int) this.randomLevel.mario.x;
			if (marioX >= 280 * 16) {
				this.randomLevel.mario.x = 0;
				this.randomLevel.mario.y = 10;
				this.renderPastThisPoint = 20 * 16;
			}
			if (this.randomLevel.mario.x < 16 * 5)
				this.renderPastThisPoint = 16 * 5;
			if (marioX >= this.renderPastThisPoint) {
				System.out.println("enem weights: " + this.weights[0]);
				System.out.println("coin weight: " + this.weights[1]);
				System.out.println("jump weight: " + this.weights[2]);
				System.out.println("run weight: " + this.weights[3]);

				System.out.println("blocks weight: " + this.weights[4]);

				int numOfTiles = 20;

				int[] lastValues = PlayerCharacteristics.getLastValues();
				int[] currValues = PlayerCharacteristics.getCurrValues();
				if (marioX > 16 * 30)
					determineWeights(currValues, lastValues,
							renderPastThisPoint / 16 - numOfTiles);
				this.randomLevel.level.weights = weights;
				PlayerCharacteristics.saveLastValues();
				// System.out.println("Current: " + this.randomLevel.mario.x);
				int temp = this.renderPastThisPoint;
				int random = 0;
				Random rand = new Random();
				while (this.renderPastThisPoint - temp < numOfTiles * 16) {
					random = rand.nextInt(3);
					if (weights[2] > .8 && weights[0] > .8)
						random = rand.nextInt(4);
					((MyLevel) this.randomLevel.level).clear(
							(int) renderPastThisPoint / 16 + numOfTiles,
							numOfTiles);

					int tempBuff = 0;
					if (random == 0)
						tempBuff = 16 * ((MyLevel) this.randomLevel.level)
								.buildHillStraightCustom(
										(int) renderPastThisPoint / 16
												+ numOfTiles, numOfTiles);
					else if ((random == 1) || (random == 2))
						tempBuff = 16 * ((MyLevel) this.randomLevel.level)
								.buildStraightCustom((int) renderPastThisPoint
										/ 16 + numOfTiles, numOfTiles, false);
					else if (random == 3)
						tempBuff = 16 * ((MyLevel) this.randomLevel.level)
								.buildTubesCustom((int) renderPastThisPoint
										/ 16 + numOfTiles, numOfTiles);
					((MyLevel) this.randomLevel.level).fixWallsCustom(
							(int) renderPastThisPoint / 16 + numOfTiles,
							numOfTiles);
					this.renderPastThisPoint += tempBuff;

				}

				System.out.println("CHANGES MADE");
			}
			//
			scene.render(og, alpha);

			if (!this.hasFocus() && tick / 4 % 2 == 0) {
				String msg = "CLICK TO PLAY";

				drawString(og, msg, 160 - msg.length() * 4 + 1, 110 + 1, 0);
				drawString(og, msg, 160 - msg.length() * 4, 110, 7);
			}
			og.setColor(Color.BLACK);

			if (width != 320 || height != 240) {

				if (useScale2x) {
					g.drawImage(scale2x.scale(image), 0, 0, null);
				} else {
					g.drawImage(image, 0, 0, 640, 480, null);

				}
			} else {
				g.drawImage(image, 0, 0, null);
			}

			renderedFrames++;

			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
			}
		}

		Art.stopMusic();
	}

	double[] weights = new double[5];

	private void determineWeights(int[] currValues, int[] lastValues, int xo) {
		if (xo < 0)
			return;
		int expectedJumps = 4;
		// kills, coins, jumped, 0, blocks
		int[] coinsblocks = getDetails(xo);
		int actualcoins = coinsblocks[0];
		int actualblocks = coinsblocks[1];
		int actualenems = getNumEnems(xo);

		if ((currValues[0] - lastValues[0]) > 3 * actualenems / 4)
			weights[0] = weights[0] * 1.2;
		else if ((currValues[0] - lastValues[0]) < 1 * actualenems / 4)
			weights[0] = weights[0] * .8;
		if ((currValues[1] - lastValues[1]) > 3 * actualcoins / 4)
			weights[1] = weights[1] * 1.2;
		else if ((currValues[1] - lastValues[1]) < 1 * actualcoins / 4)
			weights[1] = weights[1] * .8;

		double jumpWeight = 1;
		if (currValues[3] - lastValues[3] > 1)
			jumpWeight = .8;

		jumpWeight = .1;
		if ((currValues[2] - lastValues[2]) > 1 * jumpWeight)
			weights[2] = weights[2] * 1.2;
		else if ((currValues[1] - lastValues[1]) < .5 * jumpWeight)
			weights[2] = weights[2] * .8;
		weights[2] = 1;
		for (int i = 0; i < weights.length; i++) {
			if (weights[i] > .9)
				weights[i] = .9;
			if (weights[i] < .1)
				weights[i] = .1;
		}
	}

	public int getNumEnems(int xo) {
		int maxLength = 20;
		int count = 0;
		SpriteTemplate[][] st = randomLevel.level.getSpriteTemplate();
		for (int i = xo; i < maxLength + xo; i++)
			for (int j = 0; j < randomLevel.level.getSpriteTemplate()[0].length; j++)
				if (randomLevel.level.getSpriteTemplate()[i][j] != null)
					count++;
		return count;
	}

	public int[] getDetails(int xo) {
		int maxLength = 20;
		int numCoins = 0;
		int numBlocks = 0;
		for (int j = xo; j < maxLength + xo; j++)
			for (int i = 0; i < this.randomLevel.level.getMap()[0].length; i++) {
				if (this.randomLevel.level.getMap()[j][i] == Level.COIN)
					numCoins++;
				byte[][] map = this.randomLevel.level.getMap();
				if (map[j][i] == Level.BLOCK_COIN
						| map[j][i] == Level.BLOCK_EMPTY
						| map[j][i] == Level.BLOCK_POWERUP)
					numBlocks++;
			}
		return new int[] { numCoins, numBlocks };
	}

	private void drawString(Graphics g, String text, int x, int y, int c) {
		char[] ch = text.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			g.drawImage(Art.font[ch[i] - 32][c], x + i * 8, y, null);
		}
	}

	public void keyPressed(KeyEvent arg0) {
		toggleKey(arg0.getKeyCode(), true);
	}

	public void keyReleased(KeyEvent arg0) {
		toggleKey(arg0.getKeyCode(), false);
	}

	public void keyTyped(KeyEvent arg0) {
	}

	public void focusGained(FocusEvent arg0) {
		focused = true;
	}

	public void focusLost(FocusEvent arg0) {
		focused = false;
	}

	public void levelWon() {

	}

	public static final int OPTIMIZED_FIRST = 0;
	public static final int MINIMIZED_FIRST = 1;

	private LevelScene randomLevel;

	/**
	 * Part of the fun increaser
	 */
	public void toRandomGame() {
		randomLevel = new LevelSceneTest(graphicsConfiguration, this,
				new Random().nextLong(), 0, 0, false);

		Mario.fire = false;
		Mario.large = false;
		Mario.coins = 0;
		Mario.lives = 3;

		randomLevel.init();
		randomLevel.setSound(sound);
		scene = randomLevel;

	}

	public void toCustomGame() {

		randomLevel = new LevelSceneTest(graphicsConfiguration, this,
				new Random().nextLong(), 0, 0, true);

		Mario.fire = false;
		Mario.large = false;
		Mario.coins = 0;
		Mario.lives = 3;

		randomLevel.init();
		randomLevel.setSound(sound);
		scene = randomLevel;

	}

	public void lose() {
		scene = new LoseScene();
		scene.setSound(sound);
		scene.init();
	}

	public void win() {
		scene = new WinScene();
		scene.setSound(sound);
		scene.init();
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	public void mouseReleased(MouseEvent e) {

		while (!hasFocus()) {
			System.out.println("FORCE IT");
			requestFocus();
		}
	}

	/**
	 * Must return the actual fill of the viewable components
	 */
	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}

}
