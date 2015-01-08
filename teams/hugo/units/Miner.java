package hugo.units;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import hugo.Unit;

public class Miner extends Unit {

    public Miner(RobotController rc) {
        super(rc);
        // TODO Auto-generated constructor stub
    }

    public void execute() throws GameActionException{
    	try{
    		if(rc.isCoreReady()){
				
				/* in every 5 turns, beaver randomly moves
				if(Clock.getRoundNum() % 5 == 0){
					randomlyMove();
				} */
    			if(rc.senseOre(rc.getLocation()) > 0 && Clock.getRoundNum() % 20 != 0)
    				mine();
    			else
    				randomlyMove();
    		}
    	}
    	catch (GameActionException e) {
	        e.printStackTrace();
	    }
    	
    	rc.yield();
    }
    
    private void mine() throws GameActionException {
		if (rc.senseOre(rc.getLocation()) > 0) {
            if (rc.canMine()) {
                rc.mine();
            }
        }
	}

	private void randomlyMove() throws GameActionException {
		Direction newDir = getMoveDir(new MapLocation((int)(100*rand.nextDouble()*myHQ.x), (int)(rand.nextDouble()*myHQ.y)));

        if (newDir != null) {
            rc.move(newDir);
        }
	}
    
}
