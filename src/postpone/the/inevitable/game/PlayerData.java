package postpone.the.inevitable.game;

//Simple calss representing the user. Contains the money of the user etc
public class PlayerData {
	
	public static int gold = 10;
	public static int diamond = 10;
	public static int countDown = 30;
	
	//Buy tower of type 1. Tower blocks only
	public static boolean buyStoneTower() {
		if (gold > 0) {
			gold--;
			return true;
		}
		else return false;
	}

	//Buy tower of type 2. Tower both blocks and freeze
	public static boolean buyTotemTower() {
		if (diamond > 0) {
			diamond--;
			return true;
		}
		else return false;
	}
	
}
