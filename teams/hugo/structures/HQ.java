package hugo.structures;


import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Clock;
import hugo.Structure;

public class HQ extends Structure {
	
	private int neededMiner = 30; 
	private int neededMinerFactory = 6; 
	
	// Structures
	private int numAerospaceLab = 0; // 201
	private int numBarracks = 0; 
	private int numHandwashStation = 0; 
	private int numHelipad = 0; 
	private int numMinerFactory = 0; // 205
	private int numSupplyDepot = 0; 
	private int numTankFactory = 0; 
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
	    	
	    	if(Clock.getRoundNum() > 1){
	    		neededMiner = rc.readBroadcast(218);
	    		neededMinerFactory = rc.readBroadcast(205);
	    	}
	    	
	    	rc.broadcast(218, neededMiner);
	    	rc.broadcast(205, neededMinerFactory);
	    } 
    	
    	catch (GameActionException e) {
	        e.printStackTrace();
	    }
    	
    	rc.yield();
    }

}
