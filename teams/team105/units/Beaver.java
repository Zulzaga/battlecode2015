package team105.units;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import team105.Unit;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;

/*
 * Spawned at HQ
 * Only unit that can build structures
 * One of two units that can mine ore [0.2, 3].
 * Has a weak attack at short range
 * Frequently hosed
 * 
 * 
 * 
 * 
 */
public class Beaver extends Unit {
    
    public Beaver(RobotController rc) throws GameActionException {
        super(rc);
        // Initialize channelID and increment total number of this RobotType
        channelStartWith = Channel_Beaver;
        initChannelNum();
        supplyUpkeep = 10;
    }

    public void execute() throws GameActionException {
        // basicDistributedConstruction();
    	improvedStrategy1();
    	/*
        hugoDroneStrategySmallMap();
        // kairatCombinedStrategyPart1();
        kairatCombinedStrategyPart2();
        transferSupplies();
		*/
    	
        rc.yield();
    }
    
    public void improvedStrategy1(){
    	try{
    		attackLeastHealthEnemy();
    		
    		if (rc.isCoreReady()) {
	    		int turn = Clock.getRoundNum();
	    		double teamOre = rc.getTeamOre();
	    		
//	    		
	    		if(rc.readBroadcast(Channel_MinerFactory) < 1){
	    			buildUnit(RobotType.MINERFACTORY, Channel_MinerFactory);
	    		}
	    		
	    		else if (rc.readBroadcast(Channel_Helipad) < 1){
	    			buildUnit(RobotType.HELIPAD, Channel_Helipad);
	    		}
	    		
	    		//else if(rc.readBroadcast(Channel_Barracks) < 1 && rc.readBroadcast(Channel_Helipad) != 0){
	    		else if(rc.readBroadcast(Channel_Barracks) != 0){
	    			buildUnit(RobotType.BARRACKS, Channel_Barracks);
	    		}
	    		else if(rc.readBroadcast(Channel_TankFactory) != 0){
	    			buildUnit(RobotType.TANKFACTORY, Channel_TankFactory);
	    		} else if (rc.readBroadcast(Channel_AerospaceLab) < 2 && rc.readBroadcast(Channel_Helipad) > 0){
	    		    buildUnit(RobotType.AEROSPACELAB, Channel_AerospaceLab);
	    		}
	    		else if(rc.readBroadcast(Channel_SupplyDepot) < 8){
	    			buildUnit(RobotType.SUPPLYDEPOT, Channel_SupplyDepot);
	    		}
	    		else if(rc.readBroadcast(Channel_TankFactory) < 3 || rc.getTeamOre() > 1500){
	    			buildUnit(RobotType.TANKFACTORY, Channel_TankFactory);
	    		}
	    		else if(rc.getTeamOre() > 500){
	    			buildUnit(RobotType.SUPPLYDEPOT, Channel_SupplyDepot);
	    		}
	    		
	    		
    		}
    		
	    } catch (GameActionException e) {
	        e.printStackTrace();
	    }
    }
    
    public synchronized static void incrementNumStructure(RobotController rc, int channel) throws GameActionException{
        int spawnedOrder = rc.readBroadcast(channel) + 1;
        rc.broadcast(channel, spawnedOrder);
    }

    public void hugoDroneStrategySmallMap() throws GameActionException {

        try {
            attackLeastHealthEnemy();
            if (rc.isCoreReady()) {
                int roundNum = Clock.getRoundNum();
                if ((roundNum < 10 || (roundNum > 400 && roundNum < 1300))
                        && rc.getTeamOre() >= 300
                        && rc.readBroadcast(Channel_Helipad) < 1) {
                    Direction newDir = getBuildDirection(RobotType.HELIPAD);
                    if (newDir != null) {
                        rc.build(newDir, RobotType.HELIPAD);
                    }
                }

                else if (roundNum > 150 && roundNum < 1000
                        && rc.getTeamOre() >= 500
                        && rc.readBroadcast(Channel_MinerFactory) < 1) {
                    Direction newDir = getBuildDirection(RobotType.MINERFACTORY);
                    if (newDir != null) {
                        rc.build(newDir, RobotType.MINERFACTORY);
                    }
                } else {
                    mineAndMove();
                }
            }
        } catch (GameActionException e) {
            e.printStackTrace();
        }

    }

    public void kairatCombinedStrategyPart1() {

        try {
            attackLeastHealthEnemy();
            if (rc.isCoreReady()) {
                int turn = Clock.getRoundNum();

                if (turn < 10 && rc.getTeamOre() >= 300
                        && rc.readBroadcast(Channel_Helipad) < 3) {
                    Direction newDir = getBuildDirection(RobotType.HELIPAD);
                    if (newDir != null
                            && rc.canBuild(newDir, RobotType.HELIPAD)) {
                        rc.build(newDir, RobotType.HELIPAD);
                    }
                }

                else if (turn > 150 && turn < 300 && rc.getTeamOre() >= 500) {
                    Direction newDir = getBuildDirection(RobotType.MINERFACTORY);
                    if (newDir != null
                            && rc.canBuild(newDir, RobotType.MINERFACTORY)) {
                        rc.build(newDir, RobotType.MINERFACTORY);
                    }
                }
            }
        } catch (GameActionException e) {
            e.printStackTrace();
        }

    }


    /**
     * if this can, start building a given particular structure and increment its number.
     * Otherwise, move. 
     * @param type
     * @param channelForNumStructure
     * @throws GameActionException
     */
    public void buildUnit(RobotType type, int channelForNumStructure) throws GameActionException {
        if (rc.getTeamOre() > type.oreCost) {
            MapLocation buildLoc = getBuildLocationChess();
            
            if(buildLoc == null){
            	moveAround();
            }
            else{
	        	Direction buildDir = rc.getLocation().directionTo(buildLoc);
	        	
	        	// can build at the location now
	            if(rc.getLocation().distanceSquaredTo(buildLoc) <= 2){
		            if (rc.isCoreReady() && rc.canBuild(buildDir, type)) {
		                incrementNumStructure(rc,channelForNumStructure );
		                rc.build(buildDir, type);
		            }
	        	}
		        else{ // cannot build, have to move there
		        	moveToLocation(buildLoc);
		        }
            }
            
        }
    }

	private MapLocation getBuildLocationChess() throws GameActionException {
		MapLocation curLocation = rc.getLocation();
		MapLocation[] locations = MapLocation.getAllMapLocationsWithinRadiusSq(curLocation, rc.getType().sensorRadiusSquared);
		
		MapLocation nearestChess = null;
		int minDist = Integer.MAX_VALUE;
		
		for(MapLocation loc : locations){
			if(loc.distanceSquaredTo(curLocation) < minDist && 
					(loc.x + loc.y - myHQ.x - myHQ.y) % 2 == 0 &&
					rc.senseTerrainTile(loc) == TerrainTile.NORMAL &&
					!rc.isLocationOccupied(loc)) {
				minDist = loc.distanceSquaredTo(curLocation);
				nearestChess = loc;
			}
		}
		
		return nearestChess;
	}
    
    
}
