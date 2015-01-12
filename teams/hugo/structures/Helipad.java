package hugo.structures;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import hugo.Structure;

public class Helipad extends Structure{

    public Helipad(RobotController rc) {
        super(rc);
        // TODO Auto-generated constructor stub
    }
    
    public void execute() throws GameActionException{
    	
    	try{
    		int neededMiner = rc.readBroadcast(217);
    		
    		if(rc.isCoreReady() && neededMiner > 0){
	    		Direction spawnDir = getSpawnDirection(RobotType.DRONE);
	    		if(spawnDir != null){
	    			rc.spawn(spawnDir, RobotType.DRONE);
	    			rc.broadcast(217, neededMiner - 1);
	    		}
	    	}
    	}
    	catch (GameActionException e) {
	        e.printStackTrace();
	    }
    	
    	rc.yield();
    }

}
