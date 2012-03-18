/**
 * 
 */
package dk.itu.mario;

import dk.itu.mario.engine.DataRecorder;

/**
 * @author dkz
 * 
 */
public class PlayerCharacteristics {
	static private DataRecorder dr;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

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
