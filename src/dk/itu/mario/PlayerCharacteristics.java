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
	static Matrix test;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

	public static void setUpMatrix() {

		test = new Matrix(10, 1);
		// enemies, coins, speed, jumps, bumped bricks
		test.set(0, 0, dr.getNumKills());
		test.set(1, 0, dr.getCoinsCollected());
		test.set(2, 0, dr.getTotalRunTime());
		test.set(3, 0, dr.getTimesJumped());
		test.set(
				4,
				0,
				dr.getBlocksCoinDestroyed() + dr.getBlocksEmptyDestroyed()
						+ dr.getBlocksPowerDestroyed());

	}

	public static void setDR(DataRecorder dare) {
		dr = dare;
		System.out.println("DR SET");

	}

	public static int getEnemies() {
		int temp = dr.getNumKills();
		return temp;
	}

	public static int getCoins() {
		int coinsCollected = dr.getCoinsCollected();
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

}
