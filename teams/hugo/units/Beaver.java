package hugo.units;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import hugo.Unit;

public class Beaver extends Unit {

    public Beaver(RobotController rc) {
        super(rc);
    }
    
    public void execute() throws GameActionException{
    	try{
    		if(rc.isCoreReady()){
	    		// attack();
    			int neededMinerFactory = rc.readBroadcast(205);
	    		if(rc.getTeamOre() > 500 && neededMinerFactory > 0){
	    			build(RobotType.MINERFACTORY);
	    			rc.broadcast(205, neededMinerFactory - 1);
	    		}
				
				/* in every 5 turns, beaver randomly moves
				if(Clock.getRoundNum() % 5 == 0){
					randomlyMove();
				} */
	    		else{
	    			if(rc.senseOre(rc.getLocation()) > 0 && Clock.getRoundNum() % 10 != 0)
	    				mine();
	    			else
	    				randomlyMove();
	    		}
    		}
    	}
    	catch (GameActionException e) {
	        e.printStackTrace();
	    }
    	
    	rc.yield();
    }

	private void build(RobotType type) throws GameActionException {

		if (rc.getTeamOre() > type.oreCost) {
			Direction newDir = getBuildDirection(type);
	        if (newDir != null) {
	            rc.build(newDir, type);
	    		
	        }
		}
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
