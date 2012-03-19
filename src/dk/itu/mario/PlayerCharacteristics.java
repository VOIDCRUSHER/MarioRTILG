/**
 * 
 */
package dk.itu.mario;

import Jama.Matrix;
import dk.itu.mario.engine.DataRecorder;

/**
 * @author dkz
 * 
 */
public class PlayerCharacteristics {
	static private DataRecorder dr;
	static Matrix initState;
	static Matrix initUncertainty;
	static Matrix sampleNoise;
	static Matrix transitionMatrix;
	static XPFilter kfilter;
	static private int observedVars = 5, hiddenVars = 5;
	static Matrix measureMatrix;
	static boolean hasInit = false;
	static Matrix sampleMatrix;
	static int[] lastValues;

	public PlayerCharacteristics() {
	}

	public static void initFilter() {
		setUpInitState();
		setUpUncertainty();
		setUpNoise();
		setUpTransitionMatrix();
		kfilter = new XPFilter(5, 5);
		kfilter.init(initState, initUncertainty, sampleNoise, transitionMatrix);

	}

	public static void setUpInitState() {
		measureMatrix = new Matrix(5, 1);
		sampleMatrix = new Matrix(5, 1);

		// set up initial state vector x
		initState = new Matrix(10, 1);
		// enemies, coins, speed, jumps, bumped bricks
		initState.set(0, 0, dr.getNumKills());
		initState.set(1, 0, dr.getCoinsCollected());
		initState.set(2, 0, dr.getTotalRunTime());
		initState.set(3, 0, dr.getTimesJumped());
		initState.set(
				4,
				0,
				dr.getBlocksCoinDestroyed() + dr.getBlocksEmptyDestroyed()
						+ dr.getBlocksPowerDestroyed());
		initState.set(5, 0, .2);
		initState.set(6, 0, .2);
		initState.set(7, 0, .2);
		initState.set(8, 0, .2);
		initState.set(9, 0, .2);
	}

	public static void update() {
		if (!hasInit) {
			initFilter();
			hasInit = true;
		}
		// enemies, coins, speed, jumps, bumped bricks
		measureMatrix.set(0, 0, dr.getNumKills());
		measureMatrix.set(1, 0, dr.getCoinsCollected());
		measureMatrix.set(2, 0, dr.getTotalRunTime());
		measureMatrix.set(3, 0, dr.getTimesJumped());
		measureMatrix.set(
				4,
				0,
				dr.getBlocksCoinDestroyed() + dr.getBlocksEmptyDestroyed()
						+ dr.getBlocksPowerDestroyed());
		sampleMatrix = kfilter.sample(measureMatrix);

	}

	public static void setUpUncertainty() {
		// set up initial uncertaintyVals for P

		double[][] uncertaintyVals = new double[][] { { 0.01 }, { 0.01 },
				{ 0.01 }, { 0.01 }, { .01 }, { 1000 }, { 1000 }, { 1000 },
				{ 1000 }, { 1000 } };

		initUncertainty = new Matrix(uncertaintyVals);

	}

	public static void setUpNoise() {
		// set up noise constants for measurement samples for R
		double[][] noiseVals = new double[][] { { 0 }, { 0 }, { 0 }, { 0 },
				{ 0 } };
		sampleNoise = new Matrix(noiseVals);

	}

	public static void setUpTransitionMatrix() {
		// initialize transitionMatrix F

		double[][] F = new double[][] { { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 1, 0, 0, 0, 0, dr.getCoinsCollected(), 0, 0, 0 },
				{ 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 1, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 1, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 1, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 } };

		transitionMatrix = new Matrix(F);

	}

	public static void setDR(DataRecorder dare) {
		dr = dare;
		System.out.println("DR SET");

	}

	public static int getEnemies() {
		update();

		// int temp = dr.getNumKills();

		// System.out.println("Enemy prediction: " + sampleMatrix.get(5, 0));

		return 0;
	}

	public static int getCoins() {
		update();
		int coinsCollected = dr.getCoinsCollected();

		System.out.println(sampleMatrix.get(6, 0));

		return coinsCollected;
	}

	public static int getSpeed() {
		int timeRan = dr.getTimesRun();
		return timeRan;
	}

	public static int getJumps() {
		int jumps = dr.getTimesJumped();
		return jumps;
	}

	public static void saveLastValues() {
		if (lastValues == null)
			lastValues = new int[5];
		lastValues[0] = dr.getNumKills();
		lastValues[1] = dr.getCoinsCollected();
		lastValues[2] = dr.getTotalJumpTime();
		lastValues[3] = dr.getTotalRunTime();
		lastValues[4] = dr.getBlocksCoinDestroyed()
				+ dr.getBlocksEmptyDestroyed() + dr.getBlocksPowerDestroyed();
	}

	public static int[] getLastValues() {
		if (lastValues != null)
			return lastValues;
		else
			return new int[5];
	}

	public static int[] getCurrValues() {
		int[] currValues = new int[5];
		currValues[0] = dr.getNumKills();
		currValues[1] = dr.getCoinsCollected();
		currValues[2] = dr.getTotalJumpTime();
		currValues[3] = dr.getTotalRunTime();
		currValues[4] = dr.getBlocksCoinDestroyed()
				+ dr.getBlocksEmptyDestroyed() + dr.getBlocksPowerDestroyed();
		return currValues;
	}
}