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
import firenation.Unit;

public class Drone extends Unit {


    //Few drones would be used for exploring the map. Essential variables for those. 
    private MapLocation exploreToDest = null; // null if it is not an explorer drone!
    public int xMin, yMin, xMax, yMax;
    public MapLocation endCorner1, endCorner2, middle1, middle2;

    //Path exploring variables
    public HashMap<Integer, HashSet<Integer>> inSense = new HashMap<Integer, HashSet<Integer>>();

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

        int type = spawnedOrder %5;
        // System.out.println("spawned order: " + spawnedOrder + " type: " + type);


        if( type ==1  ){
            //ourHQ - > theirHQ
            exploreToDest = theirHQ;
        }else if( type ==2 ){
            exploreToDest = endCorner2;
        }else if( type ==3 ){
            exploreToDest = endCorner1;
        }else if( type ==4 ){
            exploreToDest = middle1;
        }else if( type == 0){
            exploreToDest = middle2;
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
            int diff = currentDest.distanceSquaredTo(myHQ) - currentDest.distanceSquaredTo(theirHQ);
            if  ( Math.abs(diff) < 3){
                exploreToDest = theirHQ;
            }
            harassToLocation(exploreToDest);
        }
    }

    public void execute() throws GameActionException {
        explore();

    }

    public void harassStrategy(MapLocation ml) throws GameActionException {
        harassToLocation(ml);

        transferSupplies();
        rc.yield();
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

    public void routineIsFree(){  //let y be decreasing
        Integer minY = Integer.MAX_VALUE;
        Integer maxY = Integer.MIN_VALUE;
        for (int y: inSense.keySet()){
            if ( y>maxY){
                maxY = y;
            }else if( y < minY){
                minY = y;
            }
        }
        
        //increasing y
        for (int y = minY; y < maxY; y++){
            for(int x: inSense.get(y)){
                //check if free
//                if (Terrian)
            }
        }



    }

    public void recordSenseExpansion(){
        MapLocation pos = rc.getLocation();
        int minY = pos.y -3;
        int minX = pos.x -3;
        for (int y = minY; y <= minY +3; y++ ){
            for (int x = minX; y <= minX +3; x++ ){
                HashSet coordX;
                if (inSense.containsKey(y)){
                    coordX = inSense.get(y);
                }else{
                    coordX = new HashSet<Integer>();
                }
                coordX.add(x);
                inSense.put(y, coordX);
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