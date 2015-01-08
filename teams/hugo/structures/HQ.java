package hugo.structures;


import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import hugo.Structure;

public class HQ extends Structure {

    public HQ(RobotController rc) {
        super(rc);
    }

    public void execute() throws GameActionException{
    	// attack
    	// communicate
    	// build beaver
    	
    	try{
	    	if(rc.isCoreReady()){
	    		Direction spawnDir = getSpawnDirection(RobotType.BEAVER);
	    		if(spawnDir != null){
	    			rc.spawn(spawnDir, RobotType.BEAVER);
	    		}
	    	}
	    } 
    	catch (GameActionException e) {
	        e.printStackTrace();
	    }
    	
    	rc.yield();
    }

}
