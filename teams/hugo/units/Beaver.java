package hugo.units;

import battlecode.common.Clock;
import battlecode.common.DependencyProgress;
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
	    		else if(rc.getTeamOre() > 300){
	    			int strategy = rc.readBroadcast(100);
	    			
	    			int neededFactory = 0;
	    			
                    RobotType toBuild = RobotType.BARRACKS;
                    if (strategy == 1) {
                    	toBuild = RobotType.HELIPAD;
                    	Direction newDir = getBuildDirection(toBuild);
	                    if (newDir != null) {
	                        rc.build(newDir, toBuild);
	                    }
                    }
                    else if (strategy == 0) {
                        if (rc.checkDependencyProgress(RobotType.BARRACKS) == DependencyProgress.DONE) {
                            toBuild = RobotType.TANKFACTORY;
                            neededFactory = rc.readBroadcast(209); 
                        }
                        else{
                        	neededFactory = 1;
                        }
                    }
                    else{
                    	neededFactory = 1;
                    }
                    
                    if(neededFactory > 0){
	                    Direction newDir = getBuildDirection(toBuild);
	                    if (newDir != null) {
	                        rc.build(newDir, toBuild);
	                        
	                        if(toBuild == RobotType.TANKFACTORY)
		                    	rc.broadcast(209, neededFactory - 1);
	                    }
	                    
	                    
                    }
                    
	    		}
	    		else{
	    			if(rc.senseOre(rc.getLocation()) > 0 && Clock.getRoundNum() % 10 != 0)
	    				mine();
	    			else{
	    				int roundNum = Clock.getRoundNum();
	    				
	    				if(roundNum % 3 == 0)
	    					moveAwayFromHQ();
	    				else{
	    					randomlyMove();
	    				}
	    			}
	    				
	    		}
    		}
    		
    		transferSupplies();
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
		Direction newDir = getMoveDir(new MapLocation((int)(2*rand.nextDouble()*myHQ.x), (int)(2*rand.nextDouble()*myHQ.y)));

        if (newDir != null) {
            rc.move(newDir);
        }
	}
	
	private void moveAwayFromHQ() throws GameActionException {
		
		Direction newDir = getMoveDir(rc.getLocation().add(rc.getLocation().directionTo(myHQ).opposite()));
		//Direction newDir = rc.getLocation().subtract(Direction.)
				//getMoveDir(new MapLocation((int)(2*rand.nextDouble()*myHQ.x), (int)(2*rand.nextDouble()*myHQ.y)));

        if (newDir != null) {
            rc.move(newDir);
        }
	}
	
}
