package postpone.the.inevitable.db;

import java.util.Random;

//Simple object representing each row in the database
public class Level {
	
	//SQLiteHelper.COLUMN_ID
	public final int id;	
	//SQLiteHelper.COLUMN_COMPLETED,
	public boolean completed;
	//SQLiteHelper.COLUMN_BEST_TIME,
	public double time;
	//SQLiteHelper.COLUMN_COMPLETION_DATE, 
	public String completion_date;
	//SQLiteHelper.COLUMN_MAP, 
	public final String map;
	//SQLiteHelper.COLUMN_MAP_SOLUTION, 
	public final String solution;
	//SQLiteHelper.COLUMN_TARGET_TIME, 
	public final double target_time;
	//SQLiteHelper.COLUMN_CURRENCY1, 
	public int currency1;
	//SQLiteHelper.COLUMN_CURRENCY2, 
	public int currency2;
	
	//Construtor. Only way to define an existing level object
	public Level(int id, boolean completed, double time, String completion_date, String map, String solution, double target_time, int currency1, int currency2) {
		this.id = id;
		this.completed = completed;
		this.time = time;
		this.completion_date = completion_date;
		this.map = map;
		this.solution = solution;
		this.target_time = target_time;
		this.currency1 = currency1;
		this.currency2 = currency2;
	}
	
	//constructor used to define a random map;
	public Level() {
		this.id = -1;
		this.completed = false;
		this.time = 0;
		this.completion_date = "";
		this.map = "";
		this.solution = "";
		this.target_time = 0;
		this.currency1 = 0;
		this.currency2 = 0;		
	}
	
	//Representation of the map.
	private int[][] layout = 
	{
	    { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
	    { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
	    { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
	    { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
	    { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
	    { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
	    { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
	    { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
	    { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
	};
	
    // The width of the map.
    public final int Width()
    {
        return 14;
    }

    // The height of the map.
    public final int Height()
    {
    	return 9;
    }

    /// Returns the tile index for the given cell.
    public final int GetIndex(int cellX, int cellY)
    {
        if (cellX < 0 || cellX > Width() - 1 || cellY < 0 || cellY > Height() - 1)
            return 0;

        return layout[cellY][cellX];
    }

    //Create the map
	public void generateMap() throws Exception {
		if (map.length() == 126) {
			for(int i = 0; i < map.length(); i++) {
				int nbr = 0;
				if (map.charAt(i) == '1') {
					nbr = 1;
				}
				if (map.charAt(i) == '2') {
					nbr = 2;
				}
				int row = i / 14;
				int column = i % 14;
					layout[row][column] = nbr;
			}
		}
	}
	
    //Create the map
	public void generateRandomMap() throws Exception {

		
		final Random generator = new Random();
		
		int maximumNbrOfTotemsOnField = generator.nextInt(5);
		int maximumNbrOfStonesOnField = generator.nextInt(26) + 14;
		int placedTotemsOnField = 0;
		int placedStonesOnField = 0;
		
		int stonesPerSquare1 = maximumNbrOfStonesOnField/4;
		int stonesPerSquare2 = maximumNbrOfStonesOnField/4;
		int stonesPerSquare3 = maximumNbrOfStonesOnField/4;
		int stonesPerSquare4 = maximumNbrOfStonesOnField/4;

		int totemsPerSquare1 = 1;
		int totemsPerSquare2 = 1;
		int totemsPerSquare3 = 1;
		int totemsPerSquare4 = 1;
		
		boolean previousHit = false;
		
		if (map.length() == 0) {
			for(int i = 0; i < 126; i++) {
				int nbr = 0;
				final int row = i / 14;
				final int column = i % 14;
				
				if (row != 4) {
					
					int generateStone = 0;
					int generateTotem = 0;
					
					if (row % 2 == 0 && !previousHit) {
						generateStone = generator.nextInt(15);
						generateTotem = generator.nextInt(20);
					}
					else if (previousHit) {
						generateStone = generator.nextInt(20);
						generateTotem = generator.nextInt(30);
					}
					else {
						generateStone = generator.nextInt(4);
						generateTotem = generator.nextInt(7);
					}
					
					if (generateStone == 1 && maximumNbrOfStonesOnField > 0) {
						
						if (row < 4 && column <= 7 && stonesPerSquare1 > 0) {
							maximumNbrOfStonesOnField--;
							nbr = 1;
							placedStonesOnField++;
							previousHit = true;
							stonesPerSquare1--;
						}
						else if (row > 4 && column <= 7 && stonesPerSquare2 > 0) {
							maximumNbrOfStonesOnField--;
							nbr = 1;
							placedStonesOnField++;
							previousHit = true;
							stonesPerSquare2--;
						}
						else if (row < 4 && column >= 8 && stonesPerSquare3 > 0) {
							maximumNbrOfStonesOnField--;
							nbr = 1;
							placedStonesOnField++;
							previousHit = true;
							stonesPerSquare3--;
						}
						else if (row > 4 && column >= 8 && stonesPerSquare4 > 0) {
							maximumNbrOfStonesOnField--;
							nbr = 1;
							placedStonesOnField++;
							previousHit = true;
							stonesPerSquare4--;
						}
					}
					else if (generateTotem == 2 && maximumNbrOfTotemsOnField > 0){

						if (row < 5 && column <= 7 && totemsPerSquare1 > 0) {
							nbr = 2;
							maximumNbrOfTotemsOnField--;
							placedTotemsOnField++;
							previousHit = true;
							totemsPerSquare1--;
						}
						else if (row > 5 && column <= 7 && totemsPerSquare2 > 0) {
							nbr = 2;
							maximumNbrOfTotemsOnField--;
							placedTotemsOnField++;
							previousHit = true;
							totemsPerSquare2--;
						}
						else if (row < 5 && column >= 8 && totemsPerSquare3 > 0) {
							nbr = 2;
							maximumNbrOfTotemsOnField--;
							placedTotemsOnField++;
							previousHit = true;
							totemsPerSquare3--;
						}
						else if (row > 5 && column >= 8 && totemsPerSquare4 > 0) {
							nbr = 2;
							maximumNbrOfTotemsOnField--;
							placedTotemsOnField++;
							previousHit = true;
							totemsPerSquare4--;
						}
						
					}
					else {
						previousHit = false;
					}
				}

				layout[row][column] = nbr;
			}
		}
		
		currency1 = generator.nextInt(40-placedStonesOnField) + 12;
		currency2 = generator.nextInt(6-placedTotemsOnField) +1;
	}
}
