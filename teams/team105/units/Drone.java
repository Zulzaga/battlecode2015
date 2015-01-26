package team105.units;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import team105.Unit;
import team105.units.Drone.RobotHealthComparator;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;

public class Drone extends Unit {

    //3 drones would be used for exploring the map. Essential variables for those. 

    //Path exploring variables
    public int mode = 0; //0= exploring destination on diagonal then theirHQ, 1 = spreading supply
    public boolean onDutyForSupply = false;
    public boolean searchedPath = false; //try to find path to its current destination;
    public boolean freePath = false; //if it is true, it guarantees that there is a way to reach its destination.
    public int pathTo;
    public MapLocation connectionDest;
    public MapLocation aroundEnemyDest;
    public int channel_PathReporting;
    public String path; //just for debugging!
    public MapLocation oreAreaPoint = null;
    public double maxOreArea = 0;
    public int channel_maxOreX; //channelID +1
    public int channel_maxOreY; //channelID +2
    public int channel_maxOreAmount; //channelID +3
    public int channel_callOfSupply;
    public int channel_callOfSupplyX;
    public int channel_callOfSupplyY;
    public Direction toEnemy;
    public MapLocation centerOfMap, endCorner2, endCorner1;
    public double distanceToCenter;
    public MapLocation destination;



    public HashSet<MapLocation> reachableSpots = new HashSet<MapLocation>();

    public Drone(RobotController rc) throws GameActionException {
        super(rc);

        // Initialize channelID and increment total number of this RobotType
        channelStartWith = Channel_Drone;
        supplyUpkeep = 10;

        toEnemy = myHQ.directionTo(theirHQ);
        Direction toRight = toEnemy.rotateRight().rotateRight();

        centerOfMap = new MapLocation((myHQ.x + theirHQ.x) / 2,
                (myHQ.y + theirHQ.y) / 2);
        distanceToCenter = Math.pow(myHQ.distanceSquaredTo(centerOfMap), 0.5);

        endCorner2 = centerOfMap.add(toRight, (int) distanceToCenter).add(
                toEnemy, 2);
        endCorner1 = centerOfMap
                .add(toRight.opposite(), (int) distanceToCenter)
                .add(toEnemy, 2);

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
        channel_maxOreX = channelID + 1;
        channel_maxOreY = channelID + 2;
        channel_maxOreAmount = channelID + 3;

        channel_callOfSupply = channelID + 5;
        channel_callOfSupplyX =  channelID + 6;
        channel_callOfSupplyY = channelID + 7;

        pathTo = spawnedOrder %3; //would be used for broadcasting! BE CAREFUL

        if( pathTo ==1 || spawnedOrder >3 ){
            //ourHQ - > theirHQ
            destination = centerOfMap;
            channel_PathReporting = Channel_PathToCenter;
        }else if( pathTo ==2 ){
            destination = endCorner2;
            channel_PathReporting = Channel_PathToCorner1;
        }else if( pathTo ==0 ){
            destination = endCorner1;
            channel_PathReporting = Channel_PathToCorner2;
        }
    }



    public void execute() throws GameActionException {
        int roundNum = Clock.getRoundNum();
        goAroungWithSpecialMode(destination);
        rc.yield();
    }


    public void goAroungWithSpecialMode(MapLocation ml) throws GameActionException{
        RobotInfo nearestEnemy = senseNearestEnemy(rc.getType()) ;
        if (nearestEnemy != null) {
            int distanceToEnemy = rc.getLocation().distanceSquaredTo(
                    nearestEnemy.location);
            if (distanceToEnemy <= rc.getType().attackRadiusSquared) {
                attack();
                //attackRobot(nearestEnemy.location);
                avoid(nearestEnemy);
            } else {
                if (nearestEnemy.type == RobotType.DRONE) {
                    if (shouldStand) {
                        shouldStand = false; // waited once
                    } else {
                        moveToLocation(nearestEnemy.location);
                        shouldStand = true;
                    }
                } else {
                    avoid(nearestEnemy);
                }
            }
        } else {
            if (mode == 0){
                //expanding map range and exploring path.
                moveToLocationExtandingRange();
            }else{
//                if (!searchedPath){
//                    findShortestPathAstar(rc.getLocation(), 36);
////                    MatrixtoString();
//                    searchedPath = true;
//                }
                //transfering supply
//                provideSupply();
            }
        }
    }

    public void recordMovement(){
        recentPathRecord.add(rc.getLocation());
        if ( recentPathRecord.size() > 10){
            recentPathRecord.remove(0);
        }

    }

    public int numNormalsdAround(MapLocation ml){
        int numNormals = 0;

        TerrainTile loc = rc.senseTerrainTile(ml);
        if (loc.equals(TerrainTile.NORMAL)){
            numNormals +=1;
        }
        for (Direction dir: allDirs){
            loc = rc.senseTerrainTile(ml.add(dir));
            if (loc.equals(TerrainTile.NORMAL)){
                numNormals +=1;
            }
        }
        return numNormals;
    }


    public boolean needSupply(RobotInfo[] myRobots){
        if ( myRobots.length >= 3 ){
            boolean needSupply = false;
            double totalSupply = 0;
            for (RobotInfo rob: myRobots){
                totalSupply += rob.supplyLevel;
            }
            return (totalSupply/myRobots.length < 500); //ave supply level
        }
        return false;
    }


    /**
     * If its supply is low, goes back to myHQ for getting supply.
     * Otherwise,
     * 
     * if its transfer supply radius, our robor needs supply. Transfer supply and go its destination.
     * If it is blocked then change its destination.
     * 
     * 
     * 
     */
    public void provideSupply() throws GameActionException{
//        System.out.println("transfer supply " + Clock.getRoundNum());
        double lowestSupply = rc.getSupplyLevel();
        if (lowestSupply < 200 || blocked()){
            changeDestination();
        }else{
            //transfer supply if our robot needs it.
            RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(),
                    GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED, myTeam);
            double transferAmount = 0;
            MapLocation suppliesToThisLocation = null;
            for (RobotInfo ri : nearbyAllies) {
                if (ri.supplyLevel < lowestSupply) {
                    lowestSupply = ri.supplyLevel;
                    transferAmount = (rc.getSupplyLevel() - ri.supplyLevel) / 2;
                    suppliesToThisLocation = ri.location;
                }
            }
            if (suppliesToThisLocation != null && lowestSupply < 700) {
                rc.transferSupplies((int) transferAmount, suppliesToThisLocation);

            }
            //if this is blocked, then change its destination.

        }
        moveToLocation(destination);
//        System.out.println("transfer supply " + Clock.getRoundNum());
    }

    private void changeDestination() throws GameActionException {
        if (rc.getSupplyLevel() < 200 ){
            if (destination.equals(myHQ)){
                destination = theirHQ;
            }
            else destination = myHQ;
        }else{
        MapLocation[] towers = rc.senseEnemyTowerLocations();
        for (MapLocation tower: towers){
            RobotInfo[] myRobots = rc.senseNearbyRobots(tower, 30, myTeam);
            if (needSupply(myRobots)){
                destination = tower;
                return;
            }
        }   
        destination = theirHQ;
        }
    }

    // move to location (Safe!)
    public boolean moveToLocation(MapLocation location) throws GameActionException {
        if (rc.isCoreReady()) {
            Direction dirs[] = getDirectionsTowardAndNext(location);

            for (Direction newDir : dirs) {
                //                System.out.println("Turn before " + Clock.getRoundNum());
                if (rc.canMove(newDir)) {
                    if (!safeToMove2(rc.getLocation().add(newDir))
                            || !safeFromShortShooters(rc.getLocation().add(
                                    newDir))) {
                        continue;
                    } else if (rc.canMove(newDir)) {
                        if( !blocked() ){
                            //                            System.out.println("Turn after " + Clock.getRoundNum());
                            rc.move(newDir);
                            return true;
                        }
                    }
                }
            }
            recordMovement();
        }
        return false;
    }


    // move to destination, avoiding enemies ------ MODE 0, 1;
    /**
     * Move to destination, inclined to direction where more normal terrainTiles exist.
     * If it was circulating somewhere (locked), change its destination. 
     * @throws GameActionException
     */
    public void moveToLocationExtandingRange() throws GameActionException {
        if (rc.isCoreReady()) {
            //Directions where normal exist
            MapLocation currentLoc = rc.getLocation();
            Direction[] dirs;
            Direction towardDest = currentLoc.directionTo(destination);
            MapLocation rightLoc = currentLoc.add(towardDest.rotateRight().rotateRight(), 2);
            MapLocation leftLoc = currentLoc.add(towardDest.rotateLeft().rotateLeft(), 2);
            MapLocation forward = currentLoc.add(towardDest, 2);
            int leftNormals = numNormalsdAround(leftLoc);
            int rightNormals = numNormalsdAround( rightLoc);
            int forwardNormals = numNormalsdAround(forward);
            int maxNormals = Math.max(Math.max(leftNormals, rightNormals), forwardNormals);

            if (currentLoc.distanceSquaredTo(destination) < 5 || blocked()){
                if (destination.equals(theirHQ)){
                    aroundEnemyDest = rc.getLocation(); //reachable point;
                    mode = 1; //stop this execution! 
                    recentPathRecord = new ArrayList<MapLocation>();
                }else{
                    connectionDest = rc.getLocation(); //reachable point;
                    destination = theirHQ;
                    recentPathRecord = new ArrayList<MapLocation>();
                }
            }else{
                if (forwardNormals == maxNormals || leftNormals == rightNormals ){
                    dirs = new Direction[]{ towardDest, towardDest.rotateLeft(), towardDest.rotateRight()};
                }else if ( leftNormals == maxNormals){
                    dirs = new Direction[]{towardDest.rotateLeft(),towardDest, towardDest.rotateRight()};
                }else{
                    dirs = new Direction[]{  towardDest.rotateRight(), towardDest, towardDest.rotateLeft()};
                }

                for (Direction newDir : dirs) {
                    if (rc.canMove(newDir)) {
                        if (!safeToMove2(rc.getLocation().add(newDir))
                                || !safeFromShortShooters(rc.getLocation().add(
                                        newDir))) {
                            continue;
                        } else if (rc.canMove(newDir)) {
                            rc.move(newDir);
                            recordMovement();
                            return;
                        }
                    }
                }
                recordMovement(); //record where it ends.
            }
        }
    }


    // return the nearest enemy robot within radius
    public RobotInfo senseNearestEnemyWithin(int radiusSquared) {
        RobotInfo[] enemies = rc.senseNearbyRobots( radiusSquared, theirTeam);

        if (enemies.length > 0) {
            RobotInfo nearestRobot = null;
            int nearestDistance = Integer.MAX_VALUE;
            for (RobotInfo robot : enemies) {
                int distance = rc.getLocation().distanceSquaredTo(
                        robot.location);
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestRobot = robot;
                }
            }

            if (nearestDistance > radiusSquared){
                return null;
            }
            return nearestRobot;
        }
        return null;
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