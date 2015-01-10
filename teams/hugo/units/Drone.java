package hugo.units;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import hugo.Unit;

public class Drone extends Unit{

    public Drone(RobotController rc) {
        super(rc);
        // TODO Auto-generated constructor stub
    }
    
    public void execute() throws GameActionException {
        //swarmPot();
    	RobotInfo[] enemies = getEnemiesInAttackingRange();
    	
    	
    	
    	if(rc.isCoreReady()){
    		// if enemies are around, retreat back
    		if(enemies.length > 0){
    			retreatToHQ();
    		}
    		
    		// advance to their HQ
    		else{
    			if(rc.getLocation().distanceSquaredTo(theirHQ) > 2)
    				moveToTheirHQ();
    			//moveAwayFromHQ();
    		}
    	}
    	
    	// If possible, attack
    	if (rc.isWeaponReady()) {
            attackLeastHealthEnemy(enemies);
        }
    	
        transferSupplies();
        rc.yield();
    }
    
    private void retreatToHQ() throws GameActionException {
    	//Direction newDir = getMoveDir(rc.getLocation().add(rc.getLocation().directionTo(myHQ)));
    	
    	Direction dirs[] = getDirectionsToward(myHQ);
    	
    	boolean moved = false;
    	
    	for(Direction newDir : dirs){
	        if (newDir != null) {
	        	if(!safeToMove(rc.getLocation().add(newDir))){
	        		continue;
	        	}
	        	else{
	        		rc.move(newDir);
	        		moved = true;
	        	}
	        }
    	}
    	
    	if(!moved){
    		moveAwayFromHQ();
    	}
    	
	}
    
    
    private boolean safeToMove(MapLocation ml) {
        MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
        boolean tileInFrontSafe = true;
        for (MapLocation m : enemyTowers) {
            if (m.distanceSquaredTo(ml) <= RobotType.TOWER.attackRadiusSquared) {
                tileInFrontSafe = false;
                break;
            }
        }
        return tileInFrontSafe;
    }
    
	private void moveAwayFromHQ() throws GameActionException {
		
		Direction oppositeDir = getMoveDir(rc.getLocation().add(rc.getLocation().directionTo(myHQ).opposite()));
		//Direction newDir = rc.getLocation().subtract(Direction.)
				//getMoveDir(new MapLocation((int)(2*rand.nextDouble()*myHQ.x), (int)(2*rand.nextDouble()*myHQ.y)));
		
		Direction dirs[] = getDirectionsToward(rc.getLocation().add(oppositeDir));
		
		for(Direction newDir : dirs){
	        if (newDir != null) {
	        	if(!safeToMove(rc.getLocation().add(newDir))){
	    			continue;
	    		}
	        	else{
	        		rc.move(newDir);
	        	}
	        }
		}
		
		// do not move
	}
	
	private void moveToTheirHQ() throws GameActionException {
		
		Direction dirs[] = getDirectionsToward(theirHQ);
		
		//Direction newDir = getMoveDir(rc.getLocation().add(rc.getLocation().directionTo(theirHQ)));
		
		for(Direction newDir : dirs){
	        if (newDir != null) {
	        	if(!safeToMove(rc.getLocation().add(newDir))){
	    			continue;
	    		}
	        	else{
	        		rc.move(newDir);
	        	}
	        }
		}
		
	}
}
