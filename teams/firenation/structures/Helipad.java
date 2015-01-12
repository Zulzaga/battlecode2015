package firenation.structures;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import firenation.Structure;

public class Helipad extends Structure {

    public Helipad(RobotController rc) throws GameActionException {
        super(rc);
        
        
        //Initialize channelID and increment total number of this RobotType
        channelStartWith = Channel_Helipad;
        initChannelNum();  
    }

    public void execute() throws GameActionException {
<<<<<<< HEAD
    	try{
    		if(rc.isCoreReady()){
	    		Direction spawnDir = getSpawnDirection(RobotType.DRONE);
	    		if(spawnDir != null){
	    			rc.spawn(spawnDir, RobotType.DRONE);
	    		}
	    	}
    	}
    	catch (GameActionException e) {
	        e.printStackTrace();
	    }
    	
    	rc.yield();
=======
        spawnUnit(RobotType.DRONE);
>>>>>>> 28fea41e9fc1c2a4267a9ff401601ee628a00781
    }

}
