package hugo.units;

import hugo.Unit;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class Drone extends Unit{

    public Drone(RobotController rc) {
        super(rc);
        // TODO Auto-generated constructor stub
    }
    
    public void execute() throws GameActionException {
    	
    	harassStrategy(theirHQ);
    }
    
    public void harassStrategy(MapLocation ml) throws GameActionException{
    	harassToLocation(ml);  	
    	
        transferSupplies();
        rc.yield();
    }
    
    // harass and move to the location
    public void harassToLocation(MapLocation ml) throws GameActionException{
    	RobotInfo nearestEnemy = senseNearestEnemy(rc.getType());
    	
    	if(nearestEnemy != null){
	    	int distanceToEnemy = rc.getLocation().distanceSquaredTo(nearestEnemy.location);
	    	if(distanceToEnemy <= rc.getType().attackRadiusSquared){
	    		attack();
	    		//attackRobot(nearestEnemy.location);
	    		avoid(nearestEnemy);
	    	}
	    	else{
	    		if(nearestEnemy.type != RobotType.TANK && nearestEnemy.type != RobotType.DRONE){
	    			moveToLocation(nearestEnemy.location);
	    			attack();
	    			//attackRobot(nearestEnemy.location);
	    		}
	    		else{
	    			avoid(nearestEnemy);
	    			attack();
	    			//attackRobot(nearestEnemy.location);
	    		}
	    	}
    	}
    	else{
    		moveToLocation(ml);
    		attack();
    		//attackRobot(nearestEnemy.location);
    	}
    	
    }
    
    public void attackRobot(MapLocation ml) throws GameActionException{
    	if(rc.isWeaponReady()){
    		rc.attackLocation(ml);
    	}
    }
    
    
    // return the nearest enemy robot
    public RobotInfo senseNearestEnemy(RobotType type){
    	RobotInfo[] enemies = senseNearbyEnemies(type);
    	
    	if(enemies.length > 0){
	    	RobotInfo nearestRobot = null;
	    	int nearestDistance = Integer.MAX_VALUE;
	    	for(RobotInfo robot : enemies){
	    		int distance =  rc.getLocation().distanceSquaredTo(robot.location);
	    		if(distance < nearestDistance){
	    			nearestDistance = distance;
	    			nearestRobot = robot;
	    		}
	    	}
	    	return nearestRobot;
    	}    	
		return null;
    }
    
    // return all the sensible enemies
    public RobotInfo[] senseNearbyEnemies(RobotType type){
    	return rc.senseNearbyRobots(type.sensorRadiusSquared, theirTeam);
    }
    
    public void retreatToHQ() throws GameActionException {
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
	
	public void attack() throws GameActionException{
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
		if(rc.isCoreReady()){
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
	
	public void avoidNearestEnemy(RobotType type) throws GameActionException{
		RobotInfo nearest = senseNearestEnemy(type);
    	if(nearest != null){
    		avoid(nearest);
    	}
	}
	
	// run to the opposite direction of the robot
	public void avoid(RobotInfo robot) throws GameActionException{
		if(rc.isCoreReady()){
			Direction oppositeDir = getMoveDir(rc.getLocation().add(rc.getLocation().directionTo(robot.location).opposite()));
			
			if(oppositeDir != null){
				Direction dirs[] = getDirectionsToward(rc.getLocation().add(oppositeDir));
				
				for(Direction newDir : dirs){
			        if (newDir != null) {
			        	if(!safeToMove(rc.getLocation().add(newDir))){
			    			continue;
			    		}
			        	else if(rc.canMove(newDir)){
			        		rc.move(newDir);
			        		break ;
			        	}
			        }
				}
			}
		}
	}

	// if the location is not in range of Towers and HQ
    public boolean safeToMove(MapLocation ml) {
        return safeFromTowers(ml) && safeFromHQ(ml);
        	
    }
    
    // if the location is not in range of Towers
    public boolean safeFromTowers(MapLocation ml){
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
    
    // if the location is not in range of their HQ
    public boolean safeFromHQ(MapLocation location){
    	return location.distanceSquaredTo(theirHQ) > RobotType.HQ.attackRadiusSquared;
    }
	
	
}
