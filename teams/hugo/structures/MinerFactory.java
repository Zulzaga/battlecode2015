package hugo.structures;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import hugo.Structure;

public class MinerFactory extends Structure{

    public MinerFactory(RobotController rc) {
        super(rc);
        // TODO Auto-generated constructor stub
    }
    
    public void execute(){
    	try{
    		int neededMiner = rc.readBroadcast(218);
    		
    		if(rc.isCoreReady() && neededMiner > 0){
	    		Direction spawnDir = getSpawnDirection(RobotType.MINER);
	    		if(spawnDir != null){
	    			rc.spawn(spawnDir, RobotType.MINER);
	    			rc.broadcast(218, neededMiner - 1);
	    		}
	    	}
    	}
    	catch (GameActionException e) {
	        e.printStackTrace();
	    }
    	
    	rc.yield();
    }

}
