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
	private int observedVars = 5, hiddenVars = 5;
	
	public PlayerCharacteristics(){  }
	
	public void initFilter(){
		setUpInitState();
		setUpUncertainty();
		setUpNoise();
		setUpTransitionMatrix();
		kfilter = new XPFilter(5,5);
		kfilter.init(initState, initUncertainty, sampleNoise, transitionMatrix);		
	}
	
	public static void setUpInitState() {
		//set up initial state vector x
		initState = new Matrix(10, 1);
		// enemies, coins, speed, jumps, bumped bricks
		initState.set(0, 0, dr.getNumKills());
		initState.set(1, 0, dr.getCoinsCollected());
		initState.set(2, 0, dr.getTotalRunTime());
		initState.set(3, 0, dr.getTimesJumped());
		initState.set(4,	0, dr.getBlocksCoinDestroyed() 
					 + dr.getBlocksEmptyDestroyed()
					 + dr.getBlocksPowerDestroyed());

	}
	
	public static void setUpUncertainty() {
		//set up initial uncertaintyVals for P
		double[][] uncertaintyVals = new double[][]{
				{0},{0},{0},{0},{0},
				{1000},{1000},{1000},{1000},{1000}				
		};
		initUncertainty = new Matrix(uncertaintyVals);

	}
	
	public static void setUpNoise() {
		//set up noise constants for measurement samples for R
		double[][] noiseVals = new double[][]{{0},{0},{0},{0},{0}};
		sampleNoise = new Matrix(noiseVals);

	}
	
	public static void setUpTransitionMatrix() {
		//initialize transitionMatrix F
		double[][] F = new double[][]{
				{1,0,0,0,0,0,0,0,0,0},
				{0,1,0,0,0,0,0,0,0,0},
				{0,0,1,0,0,0,0,0,0,0},
				{0,0,0,1,0,0,0,0,0,0},
				{0,0,0,0,1,0,0,0,0,0},
				{0,0,0,0,0,1,0,0,0,0},
				{0,0,0,0,0,0,1,0,0,0},
				{0,0,0,0,0,0,0,1,0,0},
				{0,0,0,0,0,0,0,0,1,0},
				{0,0,0,0,0,0,0,0,0,1},
									  };
		transitionMatrix = new Matrix(F);

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
