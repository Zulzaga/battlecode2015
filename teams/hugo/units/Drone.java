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
    	
    	MapLocation[] myTowers = rc.senseTowerLocations();
    	
    	attack();
    	if(!runAway())
    		moveToTheirHQ();
    	//rushAttackLocation(theirHQ);
    	//defend(myTowers[0]);
    	/*
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
    	*/
    	
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
		
		if(rc.isCoreReady()){
			Direction dirs[] = getDirectionsToward(theirHQ);
			
			//Direction newDir = getMoveDir(rc.getLocation().add(rc.getLocation().directionTo(theirHQ)));
			
			for(Direction newDir : dirs){
		        if (newDir != null) {
		        	if(!safeToMove(rc.getLocation().add(newDir))){
		    			continue;
		    		}
		        	else if(rc.canMove(newDir)){
		        		rc.move(newDir);
		        		return;
		        	}
		        }
			}
		}
	}
	
	private void rushAttackLocation(MapLocation location) throws GameActionException{
		if(rc.isCoreReady()){
			moveToLocation(location);
		}
	}
	
	private void attack() throws GameActionException{
		if(rc.isWeaponReady()){
			RobotInfo[] enemies = getEnemiesInAttackingRange();
			
			if(enemies.length > 0) {
	            attackLeastHealthEnemy(enemies);
	        }
		}
	}
	
	public  RobotInfo[] getEnemiesInAttackingRange() {
        RobotInfo[] enemies = rc.senseNearbyRobots(RobotType.DRONE.attackRadiusSquared, theirTeam);
        return enemies;
    }
	
	private void defend(MapLocation location) throws GameActionException{
		RobotInfo[] enemies = getEnemiesInAttackingRange();
		
		if(enemies.length > 0 && rc.isWeaponReady()) {
            attackLeastHealthEnemy(enemies);
        }
		
		if(rc.isCoreReady()){
			moveToLocation(location);
		}
		
	}

	private void moveToLocation(MapLocation location) throws GameActionException {
		Direction dirs[] = getDirectionsToward(location);
		
		for(Direction newDir : dirs){
	        if (rc.canMove(newDir)) {
	        	if(!safeToMove(rc.getLocation().add(newDir))){
	    			continue;
	    		}
	        	else if(rc.canMove(newDir)){
	        		rc.move(newDir);
	        		return;
	        	}
	        }
		}
	}
	
	private boolean runAway() throws GameActionException{
		if(rc.isCoreReady()){
			
			//RobotInfo[] enemiesSensible = rc.senseNearbyRobots(RobotType.DRONE.sensorRadiusSquared, theirTeam);
			RobotInfo[] enemiesSensible = rc.senseNearbyRobots(RobotType.DRONE.attackRadiusSquared, theirTeam);
		
			if(enemiesSensible.length > 0){
				
				//if(enemiesSensible[0].type == RobotType.BEAVER){
				//	moveToLocation(enemiesSensible[0].location);
				//}
				//else 
				if(rc.getLocation().distanceSquaredTo(enemiesSensible[0].location) > RobotType.DRONE.attackRadiusSquared){
					moveToLocation(enemiesSensible[0].location);
				}
				
				else{
					Direction dirs[] = getDirectionsToward(enemiesSensible[0].location);
					
					for(Direction newDir : dirs){
						if(!safeToMove(rc.getLocation().add(newDir.opposite())))
							continue;
						else if(rc.canMove(newDir.opposite())){
							rc.move(newDir.opposite());
							return true;
						}
					}
				}
				return true;
			}
			else{
				return false; // did not attack
			}
		}
		return false; // did not do anything
	}
	
}
