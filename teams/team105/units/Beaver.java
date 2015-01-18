package team105.units;

import java.util.ArrayList;

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
	    		
	    		if(rc.readBroadcast(Channel_MinerFactory) < 1){
	    			buildUnit(RobotType.MINERFACTORY);
	    		}
	    		/*
	    		else if (rc.readBroadcast(Channel_Helipad) == 0){
	    			buildUnit(RobotType.HELIPAD);
	    		}
	    		*/
	    		//else if(rc.readBroadcast(Channel_Barracks) < 1 && rc.readBroadcast(Channel_Helipad) != 0){
	    		else if(rc.readBroadcast(Channel_Barracks) < 1){
	    			buildUnit(RobotType.BARRACKS);
	    		}
	    		else if(rc.readBroadcast(Channel_TankFactory) < 2){
	    			buildUnit(RobotType.TANKFACTORY);
	    		}
	    		else if(rc.readBroadcast(Channel_SupplyDepot) < 8){
	    			buildUnit(RobotType.SUPPLYDEPOT);
	    		}
	    		else if(rc.readBroadcast(Channel_TankFactory) < 3 || rc.getTeamOre() > 1500){
	    			buildUnit(RobotType.TANKFACTORY);
	    		}
	    		else if(rc.getTeamOre() > 500){
	    			buildUnit(RobotType.SUPPLYDEPOT);
	    		}
    		}
    		
	    } catch (GameActionException e) {
	        e.printStackTrace();
	    }
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

    public void kairatCombinedStrategyPart2() throws GameActionException {
        int turn = Clock.getRoundNum();

        if (turn % 2 == 0) {
            if (turn >= 200 && turn < 700
                    && rc.readBroadcast(Channel_Barracks) < 1) {
                buildUnit(RobotType.BARRACKS);
            } else if (turn >= 600 && rc.readBroadcast(Channel_Tank) < 3) {
                buildUnit(RobotType.TANKFACTORY);
            }

        } else if (turn % 3 == 0) {
            if (turn >= 1800 && rc.readBroadcast(Channel_HandwashStation) < 3) {
                buildUnit(RobotType.HANDWASHSTATION);
            } else if (turn > 500 && turn < 1500
                    && rc.readBroadcast(Channel_Helipad) < 3) {
                buildUnit(RobotType.HELIPAD);
            }
        }

        // if not building anything
        if (Math.random() < 0.9) {
            mineAndMove();
        } else {
            moveAroundAlways();
        }
    }

    /**
     * Build structures depending on time turn if there is enough ore.
     * Otherwise, mine or move.
     * 
     * @throws GameActionException
     */
    public void basicDistributedConstruction() throws GameActionException {

        // * 7. Barrack 4; 200-700 -125
        // * 8. Miner factory 3; 0-300 -100
        // * 11. Tank factory 4; 700-1200 -120

        // * 9. HandwashStation 2; 1000-1300 - 150

        // * 10. Helipad 2; 500-1000 - 250
        // * 12. Aerospace lab 2; 1000-1700 -350

        int turn = Clock.getRoundNum();

        // try build structures in particular time steps if there is enough
        // amount of ore.
        if (turn % 20 == 0) {
            if (turn <= 300 && rc.readBroadcast(8) < 4) {
                buildUnit(RobotType.MINERFACTORY);
            } else if (turn >= 200 && turn < 700 && rc.readBroadcast(7) < 5) {
                buildUnit(RobotType.BARRACKS);
            } else if (turn >= 700 && turn <= 1200 && rc.readBroadcast(11) < 4) {
                buildUnit(RobotType.TANK);
            }

        } else if (turn % 33 == 0) {
            if (turn >= 1000 && turn <= 1300 && rc.readBroadcast(9) < 3) {
                buildUnit(RobotType.HANDWASHSTATION);

            } else if (turn % 49 == 0) {
                if (turn <= 500 && turn < 1000 && rc.readBroadcast(2) < 3) {
                    buildUnit(RobotType.HELIPAD);
                } else if (turn >= 1000 && turn < 1700
                        && rc.readBroadcast(12) < 3) {
                    buildUnit(RobotType.AEROSPACELAB);
                }
            }
        }

        // if building nothing.
        if (Math.random() < 0.3) {
            mineAndMove();
        } else {
            moveAroundAlways();
        }

        // swarmPot() ;

    }

    /**
     * Transfer supply, player 6 example
     * 
     * @throws GameActionException
     */
    public void player6() throws GameActionException {
        attackEnemyZero();
        if (Clock.getRoundNum() < 700) {
            buildUnit(RobotType.MINERFACTORY);
        } else {
            buildUnit(RobotType.BARRACKS);
        }
        mineAndMove();
        transferSupplies();

    }
    
    public void buildUnit(RobotType type) throws GameActionException {
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
