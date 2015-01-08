package hugo.units;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import hugo.Unit;

public class Beaver extends Unit {

    public Beaver(RobotController rc) {
        super(rc);
    }
    
    public void execute() throws GameActionException{
    	try{
    		// attack();
        	// mine();
    		if(rc.isCoreReady()){
    			move();
    		}
    	}
    	catch (GameActionException e) {
	        e.printStackTrace();
	    }
    	
    	rc.yield();
    }

	private void move() throws GameActionException {
		Direction newDir = getMoveDir(new MapLocation((int)(2*rand.nextDouble()*myHQ.x), (int)(2*rand.nextDouble()*myHQ.y)));

        if (newDir != null) {
            rc.move(newDir);
        }
	}
}
