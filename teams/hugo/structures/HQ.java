package hugo.structures;


import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Clock;
import battlecode.common.TerrainTile;
import hugo.Structure;

public class HQ extends Structure {
	
	public MapLocation centerOfMap;
	
	public static int xMin, xMax, yMin, yMax;
    public static int xpos, ypos;
    public static int totalNormal, totalVoid, totalProcessed;
    public static int towerThreat;

    public static double ratio;
    public static boolean isFinished;

    public static int strategy; // 0 = "defend", 1 = "build drones", 2 = "build soldiers"
    
	
	private int neededMiner = 30; 
	private int neededMinerFactory = 6; 
	private int neededTankFactory = 5; 
	
	// Structures
	private int numAerospaceLab = 0; // 201
	private int numBarracks = 0; 
	private int numHandwashStation = 0; 
	private int numHelipad = 0; 
	private int numMinerFactory = 0; // 205
	private int numSupplyDepot = 0; 
	private int numTankFactory = 0; // 207
	private int numTechnologyInstitute = 0; 
	private int numTower = 0; 
	private int numTrainingField = 0; // 210
	
	// Units
	private int numBasher = 0; // 211
	private int numBeaver = 0; 
	private int numCommander = 0;
	private int numComputer = 0;
	private int numDrone = 0;
	private int numLauncher = 0;
	private int numMiner = 0;   // 218
	private int numSoldier = 0;
	private int numTank = 0;   // 219
	
	// STRUCTURE LIMITS
	private final int LIMAEROSPACELAB = 0; 
	private final int LIMBARRACKS = 0; 
	private final int LIMHANDWASHSTATION = 0; 
	private final int LIMHELIPAD = 0; 
	private final int LIMMINERFACTORY = 0; 
	private final int LIMSUPPLYDEPOT = 0; 
	private final int LIMTANKFACTORY = 0; 
	private final int LIMTECHNOLOGYINSTITUTE = 0; 
	private final int LIMTOWER = 0; 
	private final int LIMTRAININGFIELD = 0;
	
	// UNIT LIMITS
	private final int LIMBASHER = 0;
	private final int LIMBEAVER = 10; 
	private final int LIMCOMMANDER = 0;
	private final int LIMCOMPUTER = 0;
	private final int LIMDRONE = 0;
	private final int LIMLAUNCHER = 0;
	private final int LIMMINER = 20;
	private final int LIMSOLDIER = 0;
	private final int LIMTANK = 0;
	
	
	
    public HQ(RobotController rc) {
        super(rc);
        
        centerOfMap = new MapLocation((this.myHQ.x + this.theirHQ.x) / 2,
                (this.myHQ.y + this.theirHQ.y) / 2);
        
        xMin = Math.min(this.myHQ.x, this.theirHQ.x);
        xMax = Math.max(this.myHQ.x, this.theirHQ.x);
        yMin = Math.min(this.myHQ.y, this.theirHQ.y);
        yMax = Math.max(this.myHQ.y, this.theirHQ.y);

        xpos = xMin;
        ypos = yMin;

        totalNormal = totalVoid = totalProcessed = 0;
        towerThreat = 0;
        strategy = 0;
        isFinished = false;
    }

    public void execute() throws GameActionException{
    	// attack
    	// communicate
    	// build beaver
    	
    	try{
	    	if(rc.isCoreReady()){
	    		if(numBeaver < LIMBEAVER){
		    		Direction spawnDir = getSpawnDirection(RobotType.BEAVER);
		    		if(spawnDir != null){
		    			rc.spawn(spawnDir, RobotType.BEAVER);
		    			numBeaver++;
		    		}
	    		}
	    	}
	    	
	    	if(Clock.getRoundNum() > 5){
	    		neededMiner = rc.readBroadcast(218);
	    		neededMinerFactory = rc.readBroadcast(205);
	    		neededTankFactory = rc.readBroadcast(209);
	    	}
	    	
	    	rc.broadcast(218, neededMiner);
	    	rc.broadcast(205, neededMinerFactory);
	    	rc.broadcast(209, neededTankFactory);
	    	
	    	
	    	if (!isFinished) {
                analyzeMap();
                analyzeTowers();
            }
            else {
                chooseStrategy();
            }
	    	
	    	MapLocation rallyPoint;
	        if (Clock.getRoundNum() < 1400) {
	            rallyPoint = centerOfMap;
	        } else {
	            rallyPoint = this.theirHQ;
	        }
	        rc.broadcast(0, rallyPoint.x);
	        rc.broadcast(1, rallyPoint.y);
	    	
	    	transferSupplies();
	    } 
    	
    	catch (GameActionException e) {
	        e.printStackTrace();
	    }
    	
    	rc.yield();
    }
    
    ////////////////////////////////////////////////////////
    
    public void analyzeMap() {
        while (ypos < yMax + 1) {
            TerrainTile t = rc.senseTerrainTile(new MapLocation(xpos, ypos));

            if (t == TerrainTile.NORMAL) {
                totalNormal++;
                totalProcessed++;
            }
            else if (t == TerrainTile.VOID) {
                totalVoid++;
                totalProcessed++;
            }
            xpos++;
            if (xpos == xMax + 1) {
                xpos = xMin;
                ypos++;
            }

            if (Clock.getBytecodesLeft() < 100) {
                return;
            }
        }
        ratio = (double)totalNormal / totalProcessed;
        isFinished = true;
    }
    
    //////////////////////////////////////////////////////////////
    
    public void analyzeTowers() {
        MapLocation[] towers = rc.senseEnemyTowerLocations();
        towerThreat = 0;

        for (int i=0; i<towers.length; ++i) {
            MapLocation towerLoc = towers[i];

            if ((xMin <= towerLoc.x && towerLoc.x <= xMax && yMin <= towerLoc.y && towerLoc.y <= yMax) || towerLoc.distanceSquaredTo(this.theirHQ) <= 50) {
                for (int j=0; j<towers.length; ++j) {
                    if (towers[j].distanceSquaredTo(towerLoc) <= 50) {
                        towerThreat++;
                    }
                }
            }
        }
    }
    
    ///////////////////////////////////////////////////////////////
    
    public void chooseStrategy() throws GameActionException {
        if (towerThreat >= 10) {
            //play defensive
            strategy = 0;
        }
        else {
            if (ratio <= 0.85) {
                //build drones
                strategy = 1;
            }
            else {
                //build soldiers
                strategy = 2;
            }
        }

        rc.broadcast(100, strategy);
    }
    
    

}
