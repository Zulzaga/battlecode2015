package firenation.units;

import firenation.units.Drone.RobotHealthComparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;
import firenation.Unit;

public class Drone extends Unit {


    //Few drones would be used for exploring the map. Essential variables for those. 
    private MapLocation exploreToDest = null; // null if it is not an explorer drone!
    public int xMin, yMin, xMax, yMax;
    public MapLocation endCorner1, endCorner2, middle1, middle2;

    //Path exploring variables
    public boolean searchAlongY = true;
    public int searchCoord;
    public int delta;
    public boolean exploredDeadLock = false;
    public boolean freePath = false;
    public int pathTo;
    
    public HashMap<Integer, HashSet<Integer>> inSense = new HashMap<Integer, HashSet<Integer>>();
    public HashSet<MapLocation> reachableSpots = new HashSet<MapLocation>();

    public Drone(RobotController rc) throws GameActionException {
        super(rc);

        // Initialize channelID and increment total number of this RobotType
        channelStartWith = Channel_Drone;


        endCorner2 = new MapLocation(myHQ.x, theirHQ.y);
        endCorner1 = new MapLocation(theirHQ.x, myHQ.y);

        MapLocation centerOfMap = new MapLocation((myHQ.x + theirHQ.x)/2, (myHQ.y + theirHQ.y)/2);
        middle1 = new MapLocation((centerOfMap.x + endCorner1.x)/2, (centerOfMap.y + endCorner1.y)/2);
        middle2 = new MapLocation((centerOfMap.x + endCorner2.x)/2, (centerOfMap.y + endCorner2.y)/2);

        initChannelNum(); 
        
        //differences coordX and coordY.
        if (Math.abs(myHQ.x - theirHQ.x) > Math.abs(myHQ.y - theirHQ.y) ){
            searchAlongY = false;
            searchCoord = rc.getLocation().x;
            if (myHQ.x - theirHQ.x > 0){
                delta = -1;
            }else{
                delta = 1;
            }
            //add free spots along x axis
            int y = rc.getLocation().y-3;
            for (int i = 0; i < 7; i++ ){
                TerrainTile t = rc.senseTerrainTile(new MapLocation(searchCoord, y));
                if (t == TerrainTile.NORMAL){
                    reachableSpots.add(new MapLocation(searchCoord, y));
                }
            }
        }else{
            searchCoord = rc.getLocation().y;
            if (myHQ.y - theirHQ.y > 0){
                delta = -1;
            }else{
                delta = 1;
            }
            
            //add free spots along y axis
            int x = rc.getLocation().x-3;
            for (int i = 0; i < 7; i++ ){
                TerrainTile t = rc.senseTerrainTile(new MapLocation(x, searchCoord));
                if (t == TerrainTile.NORMAL){
                    reachableSpots.add(new MapLocation(x, searchCoord));
                }
            }
            
        }
    }

    /**
     * Initialize channelNum AA BBB
     * 
     * Increment total number of this robot type.
     * 
     * @throws GameActionException
     */
    public void initChannelNum() throws GameActionException{
        int spawnedOrder = rc.readBroadcast(channelStartWith) + 1;
        rc.broadcast(channelStartWith, spawnedOrder);
        channelID = channelStartWith + spawnedOrder*10;
        //first three drones are going to explore map.

        pathTo = spawnedOrder %5; //would be used for broadcasting! BE CAREFUL
        // System.out.println("spawned order: " + spawnedOrder + " type: " + type);


        if( pathTo ==1  ){
            //ourHQ - > theirHQ
            exploreToDest = theirHQ;
        }else if( pathTo ==2 ){
            exploreToDest = endCorner2;
        }else if( pathTo ==3 ){
            exploreToDest = endCorner1;
        }else if( pathTo ==4 ){
            exploreToDest = middle1;
        }else if( pathTo == 0){
            exploreToDest = middle2;
            pathTo = 5;
        }         
    }


    /**
     * Should be called only on explorer drones! If this drone has reached its
     * final destination, it should become
     * 
     * @throws GameActionException
     */
    public void explore() throws GameActionException{
        //System.out.println("destination -- " + exploreToDest);
        if (exploreToDest != null){
            //check if it has reached its destination
            MapLocation currentDest = rc.getLocation();

            int diff = currentDest.distanceSquaredTo(exploreToDest);

            if  ( Math.abs(diff) < 6){
                exploreToDest = theirHQ;
//                System.out.println("here we seee-------" + exploredDeadLock);

                if (!exploredDeadLock){
                    freePath = true;
                    rc.broadcast(Channel_FreePathFound, pathTo); 
                    System.out.println("FOUND FREE PATH TO------ " + pathTo);
                }
            }

            harassToLocation(exploreToDest);
            exploreExpansion();
//            System.out.println("here we seee-------");
        }
        
    }

    public void execute() throws GameActionException {
    	int roundNum = Clock.getRoundNum();
    	if(roundNum < 1800) 
    		explore();
    	else
    		startAttackingTowersAndHQ();
        transferSupplies();
        rc.yield();
    }
    
    public void startAttackingTowersAndHQ() throws GameActionException{
    	MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
    	
    	MapLocation nearestAttackableTowerSafeFromHQ = nearestAttackableTowerSafeFromHQ(enemyTowers);
    	
    	if(nearestAttackableTowerSafeFromHQ != null){
    		attackTower();
    		moveToLocationSafeFromHQ(nearestAttackableTowerSafeFromHQ);
    	}
    	else if(enemyTowers.length > 0){
    		attackTower();
    		moveToLocationNotSafe(enemyTowers[0]);
    	}
    	else{
    		attackTower();
    		moveToLocationNotSafe(theirHQ);
    	}
    }
    
    public MapLocation nearestAttackableTowerSafeFromHQ(MapLocation[] enemyTowers){
    	MapLocation towerLocation = null;
    	int distance = Integer.MAX_VALUE;
    	MapLocation droneLocation = rc.getLocation();
    	
    	for(MapLocation location : enemyTowers){
    		int tempDistance = droneLocation.distanceSquaredTo(location);
    		if(tempDistance < distance && safelyAttackableFromHQ(location)){
    			distance = tempDistance;
    			towerLocation = location;
    		}
    	}
    	
    	return towerLocation;
    }
    
    public boolean safelyAttackableFromHQ(MapLocation location){
    	return location.distanceSquaredTo(theirHQ) > 1;
    }
    
    public void harassStrategy(MapLocation ml) throws GameActionException {
        harassToLocation(ml);
    }

    public void player6() throws GameActionException {
        attackTower();
        moveAround();
    }

    public void swarmPot() throws GameActionException {
        //        attackLeastHealthEnemy();

        if (rc.isCoreReady()) {
            int rallyX = rc.readBroadcast(0);
            int rallyY = rc.readBroadcast(1);
            MapLocation rallyPoint = new MapLocation(rallyX, rallyY);

            Direction newDir = getMoveDir(rallyPoint);

            if (newDir != null) {
                rc.move(newDir);
            }
        }
    }

    public void exploreExpansion(){
        if (!exploredDeadLock){
        //just along y axis
        //searchCoord previous
        
        //have not moved along y coord
        if (searchCoord == rc.getLocation().y){
            int x = rc.getLocation().x-3;
            for (int i = 0; i < 7; i++ ){
                TerrainTile t = rc.senseTerrainTile(new MapLocation(x, searchCoord));
                if (t == TerrainTile.NORMAL){
                    reachableSpots.add(new MapLocation(x, searchCoord));
                }
            }
        //moved and has to level up reachableSpots  (Assuming drones not going back)  
        }else{
            HashSet<MapLocation> newReachableSpots = new HashSet<MapLocation>();
            for (MapLocation loc: reachableSpots){
                for(int i = -1; i<=1; i++){
                    TerrainTile t = rc.senseTerrainTile(new MapLocation(loc.x + i, searchCoord));
                    if (t == TerrainTile.NORMAL){
                        newReachableSpots.add(new MapLocation(loc.x + i, searchCoord));
                    }
                }
            }
            if (newReachableSpots.size() == 0){
                exploredDeadLock = true;
            }
            reachableSpots = newReachableSpots;
        }
        searchCoord = rc.getLocation().y;
        if (myHQ.y - theirHQ.y > 0){
            delta = -1;
        }else{
            delta = 1;
        }
        
        //add free spots along y axis
        int x = rc.getLocation().x-3;
        for (int i = 0; i < 7; i++ ){
            TerrainTile t = rc.senseTerrainTile(new MapLocation(x, searchCoord));
            if (t == TerrainTile.NORMAL){
                reachableSpots.add(new MapLocation(x, searchCoord));
            }
        }
        }
    }

    /**
     * Attack towers if it sees towers, otherwise attack enemy with lowest
     * health
     * 
     * @throws GameActionException
     */
    private void attackTower() throws GameActionException {
        RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(rc.getLocation(),
                rc.getType().attackRadiusSquared, rc.getTeam().opponent());

        int numberOfEnemies = nearbyEnemies.length;
        if (numberOfEnemies > 0) {
            MapLocation attackBuildingLocation = null;
            for (RobotInfo enemy : nearbyEnemies) {
                if (enemy.type == RobotType.TOWER) {
                    attackBuildingLocation = enemy.location;
                }
            }

            if (attackBuildingLocation != null) {
                if (rc.isWeaponReady()
                        && rc.canAttackLocation(attackBuildingLocation)) {
                    rc.attackLocation(attackBuildingLocation);
                }
            } else {
                Arrays.sort(nearbyEnemies, new RobotHealthComparator());
                if (rc.isWeaponReady()
                        && rc.canAttackLocation(nearbyEnemies[numberOfEnemies - 1].location)) {
                    rc.attackLocation(nearbyEnemies[numberOfEnemies - 1].location);
                }
            }
        }
    }

    /**
     * Comparator for the hit points of health of two different robots
     * (Ascending order)
     */
    static class RobotHealthComparator implements Comparator<RobotInfo> {

        // @Override
        public int compare(RobotInfo o1, RobotInfo o2) {
            if (o1.health > o2.health) {
                return 1;
            } else if (o1.health < o2.health) {
                return -1;
            } else {
                return 0;
            }
        }
    }



}