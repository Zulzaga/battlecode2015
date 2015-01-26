package team105.units;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import team105.Unit;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class Tank extends Unit {
    /*
     * 
     * T4 T2 TOW ER! T3 T1
     * 
     * towerX, towerY
     * 
     * T1 --> towerX + 1, towerY + 1; T2 --> towerX + 1, towerY - 1; T3 -->
     * towerX - 1, towerY + 1; T4 --> towerY - 1, towerY - 1;
     * 
     * Channel 50
     */

    private boolean rightHand = true;
    private boolean headTheirHQ = false;
    private boolean reachedEnemy = false;
    private boolean reachedInitialDest = false;
    private MapLocation movingLocation;
    private boolean triedExploredPath = false;
    private boolean followingCriticalPath = false;
    private ArrayList<MapLocation> criticalPathPoints = new ArrayList<MapLocation>();
    private HashSet<MapLocation> movementHistory = new HashSet<MapLocation>();
    private Direction toEnemy;
    private double distanceToCenter;

    public MapLocation centerOfMap, endCorner2, endCorner1;

    public Tank(RobotController rc) throws GameActionException {
        super(rc);

        // Initialize channelID and increment total number of this RobotType
        channelStartWith = Channel_Tank;
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
        supplyUpkeep = 15;
    }

    //    public void execute() throws GameActionException {
    //        int numOfTowers = rc.senseTowerLocations().length;
    //
    //        if (Clock.getRoundNum() < 1200) {
    //            harassToLocationTank(theirHQ);
    //            /*
    //            if (rc.readBroadcast(this.channelID) != 1) {
    //                for (int i = 1; i <= numOfTowers; i++) {
    //                    int towerChannel = Channel_Tower + i * 10;
    //                    int numOfTanks = rc.readBroadcast(towerChannel + 2);
    //                    if (numOfTanks < 5) {
    //                        int posX = rc.readBroadcast(towerChannel);
    //                        int posY = rc.readBroadcast(towerChannel + 1);
    //                        movingLocation = new MapLocation(posX + 1, posY);
    //                        Direction movingDirection = getMoveDir(movingLocation);
    //                        if (rc.isCoreReady() && rc.canMove(movingDirection)) {
    //                            rc.move(movingDirection);
    //                            rc.broadcast(towerChannel + 2, numOfTanks + 1);
    //                            rc.broadcast(this.channelID, 1);
    //                        }
    //                    } else {
    //                        swarmPotTank();
    //                    }
    //                }
    //            } else {
    //                attackLeastHealthEnemy();
    //                Direction movingDirection = getMoveDir(movingLocation);
    //                if (rc.isCoreReady() && rc.canMove(movingDirection)) {
    //                    rc.move(movingDirection);
    //                }
    //            }
    //             */
    //        } else if (Clock.getRoundNum() < 1700) {
    //            MapLocation nearestTowerSafeFromHQ = nearestAttackableTowerSafeFromHQ(
    //                    rc.senseEnemyTowerLocations());
    //            harassToLocationTank(nearestTowerSafeFromHQ);
    //        } else {
    //            startAttackingTowersAndHQ();
    //        }
    //    }


    public void execute() throws GameActionException {
        if (Clock.getRoundNum() < 1400) {
            combinedHarassingAndSearching();
        } else {
            startAttackingTowersAndHQ();                   
        }

        //        System.out.println(Clock.getRoundNum());
    }

    /**
     * Nevermind this code, but do not delete it. It might be useful for final
     * submission
     * 
     * @throws GameActionException
     */
    public void execute1() throws GameActionException {
        int numOfTowers = rc.senseTowerLocations().length;

        for (int i = 1; i <= numOfTowers; i++) {
            int towerChannel = Channel_Tower + i * 10;
            MapLocation positionToGo = getTowerChannelInfo(towerChannel);
            Direction moveDirectionForTank = getMoveDir(positionToGo);
            if (rc.isCoreReady() && rc.canMove(moveDirectionForTank)) {
                rc.move(moveDirectionForTank);
            }
        }
    }

    /**
     * Get information about the locations of the ally towers and how many tank
     * protect it
     * 
     * @param channel
     * @return
     * @throws GameActionException
     */
    private MapLocation getTowerChannelInfo(int channel)
            throws GameActionException {
        MapLocation positionToPut = null;
        int tankNum = 0;
        for (int k = 3; k < 10 && (k % 2) == 1; k++) {
            tankNum++;
            if (rc.readBroadcast((channel + k)) != 1) {
                int putX = rc.readBroadcast(channel);
                int putY = rc.readBroadcast(channel + 1);
                positionToPut = new MapLocation(putX, putY);
                rc.broadcast(channel + k, 1);
            }
        }
        if (positionToPut != null) {
            return getPositionForTank(positionToPut, tankNum);
        } else {
            return null;
        }
    }

    /**
     * Get the position around the tower where we should put our tank
     * 
     * @param towerLocation
     * @param tankNum
     * @return
     */
    private MapLocation getPositionForTank(MapLocation towerLocation,
            int tankNum) {
        int towerX = towerLocation.x;
        int towerY = towerLocation.y;
        if (tankNum == 1) {
            return new MapLocation(towerX + 1, towerY + 1);
        } else if (tankNum == 2) {
            return new MapLocation(towerX + 1, towerY - 1);
        } else if (tankNum == 3) {
            return new MapLocation(towerX - 1, towerY + 1);
        } else {
            return new MapLocation(towerX - 1, towerY - 1);
        }
    }

    /**
     * SwarmPot strategy for tanks
     */
    public void swarmPotTank() throws GameActionException {

        attackLeastHealthEnemy();
        if (Clock.getRoundNum() < 1650) {
            if (rc.isWeaponReady() && rc.isCoreReady()) {

                int rallyX = rc.readBroadcast(0);
                int rallyY = rc.readBroadcast(1);
                MapLocation rallyPoint = new MapLocation(rallyX, rallyY);

                Direction newDir = getMoveDir(rallyPoint);

                if (newDir != null) {
                    rc.move(newDir);
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

    public void combinedHarassingAndSearching() throws GameActionException{
        if (!reachedEnemy){
            if (rc.getLocation().distanceSquaredTo(destination) < 1){
                //reached destination
                reachedEnemy = true;
                harassToLocationTank(); //attack nearest enemy
            }else{
                RobotInfo nearestEnemy = senseNearestButMayFarEnemyTank(rc.getType()); //Don't care if nearest enemy is far away.
                if (nearestEnemy != null){
                    //if there is any enemy
                    if (nearestEnemy.type != RobotType.DRONE){
                        reachedEnemy = true;
                        harassToLocationTank();
                    }else{
                        moveToLocationWithMovementRecords(destination);
                    }
                    
                }else{
                    //can go to destination
                    moveToLocationWithMovementRecords(destination);
                }
            }
        }else{
            harassToLocationTank(); //attack nearest enemy
        }
    }

    public void harassToLocationTank() throws GameActionException {

        RobotInfo nearestEnemy = senseNearestEnemyTank(rc.getType());

        MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
        //nearest tower if there is at least a tower. Otherwise, enemyHQ.
        MapLocation theirTower = nearestAttackableTowerOrHQ(enemyTowers);
        int alliesAroundTower = 0;

        if(rc.getLocation().distanceSquaredTo(theirTower) < 51){
            alliesAroundTower = rc.senseNearbyRobots(theirTower, 50, myTeam).length;
            alliesAroundTower -= rc.senseNearbyRobots(theirTower, 50, theirTeam).length;
        }

        if(alliesAroundTower > 5){
            startAttackingTowersAndHQ();
        }
        else if (nearestEnemy != null) {
            int distanceToEnemy = rc.getLocation().distanceSquaredTo(
                    nearestEnemy.location);
            if (distanceToEnemy <= rc.getType().attackRadiusSquared) {
                if(rc.isWeaponReady() && rc.canAttackLocation(nearestEnemy.location))
                    rc.attackLocation(nearestEnemy.location);
                //attackLeastHealthEnemy();
            } else if (nearestEnemy.type != RobotType.MISSILE && nearestEnemy.type != RobotType.TANK && 
                    nearestEnemy.type != RobotType.LAUNCHER && nearestEnemy.type != RobotType.TOWER) {
                moveToLocation(nearestEnemy.location);

            } else if(nearestEnemy.type == RobotType.TANK && distanceToEnemy > rc.getType().sensorRadiusSquared){
                moveToLocation(nearestEnemy.location);
            } else if(nearestEnemy.type == RobotType.TANK){ // distanceToEnemy < rc.getType().sensorRadiusSquared
                int numAlliesAroundTank = rc.senseNearbyRobots(nearestEnemy.location, rc.getType().sensorRadiusSquared, myTeam).length;
                numAlliesAroundTank -= rc.senseNearbyRobots(nearestEnemy.location, rc.getType().sensorRadiusSquared, theirTeam).length;
                //System.out.println("polidood");
                //            	if(numAlliesAroundTank > 2)
                //            		moveToLocation(nearestEnemy.location);
                if(numAlliesAroundTank > 1 || rc.senseNearbyRobots(nearestEnemy.location, nearestEnemy.type.attackRadiusSquared, myTeam).length > 0)
                    moveToLocation(nearestEnemy.location);
            }

            else if(nearestEnemy.type == RobotType.LAUNCHER && distanceToEnemy > rc.getType().sensorRadiusSquared){
                moveToLocation(theirTower);
            } 
            else if(nearestEnemy.type == RobotType.LAUNCHER || nearestEnemy.type == RobotType.MISSILE){ // distanceToEnemy <= rc.getType().sensorRadiusSquared
                avoid(nearestEnemy);

            }

            else if(nearestEnemy.type == RobotType.TOWER){
                int numAlliesAroundTower = rc.senseNearbyRobots(nearestEnemy.location, 50, myTeam).length;
                numAlliesAroundTower -= rc.senseNearbyRobots(nearestEnemy.location, 50, theirTeam).length;
                if(numAlliesAroundTower > 5)
                    startAttackingTowersAndHQ();
            }

        } else {
            moveToLocation(theirTower);
        }
    }

    // move to location
    public boolean moveToLocation(MapLocation ml)
            throws GameActionException {
        if (rc.isCoreReady()) {
            Direction dirs[] = getDirectionsToward(ml);

            for (Direction newDir : dirs) {
                if (rc.canMove(newDir)) {
                    if (!safeToMove2(rc.getLocation().add(newDir))
                            || !safeFromShortShooters(rc.getLocation().add(
                                    newDir))) {
                        continue;
                    } else if (rc.canMove(newDir)) { //sometimes it happens in next turn
                        rc.move(newDir);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean moveToLocationWithMovementRecords(MapLocation ml) throws GameActionException{

        if (rc.isCoreReady()) {
            recordMovement();
            if (blocked()){
                clearMovementRecords();
            }

            Direction toDest = rc.getLocation().directionTo(ml);
            Direction[] dirs = new Direction[] { toDest, toDest.rotateLeft(), toDest.rotateRight(),
                    toDest.rotateLeft().rotateLeft(),
                    toDest.rotateRight().rotateRight(),
            };

            for (Direction newDir : dirs) {
                MapLocation newLoc = rc.getLocation().add(newDir);
                if (!movementHistory.contains(newLoc)){
                    if (rc.canMove(newDir)) {
                        if (!safeToMove2(newLoc)
                                || !safeFromShortShooters(newLoc)) {
                            continue;
                        } else if (rc.canMove(newDir)) { //sometimes it happens in next turn
                            rc.move(newDir);
                            movementHistory.add(newLoc);
                            recordMovement();
                            return true;
                        }
                    }
                }
            }


            dirs = new Direction[] { 
                    toDest.opposite().rotateRight(),
                    toDest.opposite().rotateLeft(),
                    toDest.opposite()
            };

            for (Direction newDir : dirs) {
                MapLocation newLoc = rc.getLocation().add(newDir);
                if (rc.canMove(newDir)) {
                    if (!safeToMove2(newLoc)
                            || !safeFromShortShooters(newLoc)) {
                        continue;
                    } else if (rc.canMove(newDir)) { //sometimes it happens in next turn
                        rc.move(newDir);
                        movementHistory.add(newLoc);
                        recordMovement();
                        return true;
                    }
                }
            }

        }

        return false;
    }


    /**
     * Initialize channelNum AA BBB
     * 
     * Increment total number of this robot type.
     * 
     * @throws GameActionException
     */
    public void initChannelNum() throws GameActionException {
        int spawnedOrder = rc.readBroadcast(channelStartWith) + 1;
        rc.broadcast(channelStartWith, spawnedOrder);
        channelID = channelStartWith + spawnedOrder * 10;

        //        MapLocation[] towers = rc.senseEnemyTowerLocations();
        //        destination = theirHQ;
        //                if( spawnedOrder <= 5){ 
        //                    destination = theirHQ;
        //                }else if ( spawnedOrder <= 15){
        //                    if (spawnedOrder%2 == 1){
        //                        destination = nearestAttackableTowerSafeFromHQ(rc.senseEnemyTowerLocations(), endCorner1);
        //                    }else{
        //                        destination = nearestAttackableTowerSafeFromHQ(rc.senseEnemyTowerLocations(), endCorner2);
        //                    }
        //                }else if( spawnedOrder % 20 >15){
        //                    destination = theirHQ;
        //                }else if(spawnedOrder % 20 < 5){
        //                    destination = nearestAttackableTowerSafeFromHQ(rc.senseEnemyTowerLocations(), endCorner1);
        //                }else if(spawnedOrder % 20 < 10){
        //                    destination = nearestAttackableTowerSafeFromHQ(rc.senseEnemyTowerLocations(), endCorner2);
        //                }else{// 10-15
        //                    destination = theirHQ;
        //                }

        destination = nearestAttackableTowerSafeFromHQ(rc.senseEnemyTowerLocations());
//
//        if( spawnedOrder%12 <= 6){ 
//            destination = theirHQ;
//        }else{
//            destination = nearestAttackableTowerSafeFromHQ(rc.senseEnemyTowerLocations());
//        }

    }



    public void clearMovementRecords(){
        lastSteps = new ArrayList<MapLocation>();
        movementHistory = new HashSet<MapLocation>();
    }

    private void setCriticalPathPoints(int channelPath) throws GameActionException {
        int x = rc.readBroadcast(channelPath);
        int y = rc.readBroadcast(channelPath + 1);
        while (x != 0 && y !=0){
            criticalPathPoints.add(new MapLocation(x,y));
            x = rc.readBroadcast(channelPath);
            y = rc.readBroadcast(channelPath + 1);
            System.out.println(channelPath + "  x -" + x + " y-"  + y);
        }
    }

    public RobotInfo senseNearestEnemyTank(RobotType type) {
        RobotInfo[] enemies = senseNearbyEnemiesTank(type);

        if (enemies.length > 0) {
            RobotInfo nearestRobot = null;
            int nearestDistance = Integer.MAX_VALUE;
            for (RobotInfo robot : enemies) {
                int distance = rc.getLocation().distanceSquaredTo(
                        robot.location);
                if (distance < nearestDistance && 
                        robot.type != RobotType.HQ &&
                        robot.location.distanceSquaredTo(theirHQ) > 10) {
                    nearestDistance = distance;
                    nearestRobot = robot;
                }
            }
        }
        return null;
    }


    public RobotInfo senseNearestButMayFarEnemyTank(RobotType type){
        RobotInfo[] enemies = senseNearbyEnemiesTank(type);

        if (enemies.length > 0) {
            RobotInfo nearestRobot = null;
            int nearestDistance = Integer.MAX_VALUE;
            for (RobotInfo robot : enemies) {
                int distance = rc.getLocation().distanceSquaredTo(
                        robot.location);
                if (distance < nearestDistance && 
                        robot.type != RobotType.HQ &&
                        robot.location.distanceSquaredTo(theirHQ) > 10) {
                    nearestDistance = distance;
                    nearestRobot = robot;
                }
            }
            if (nearestDistance < 60){
                return nearestRobot;
            }
        }
        return null;
    }

    // return all the sensible enemies
    public RobotInfo[] senseNearbyEnemiesTank(RobotType type) {
        return rc.senseNearbyRobots(1000, theirTeam);
    }

    public MapLocation nearestAttackableTowerOrHQ(
            MapLocation[] enemyTowers) {
        MapLocation towerLocation = null;
        int distance = Integer.MAX_VALUE;

        for (MapLocation location : enemyTowers) {
            int tempDistance = rc.getLocation().distanceSquaredTo(location);
            if (tempDistance < distance && safelyAttackableFromHQ(location)) {
                distance = tempDistance;
                towerLocation = location;
            }
        }

        if ( towerLocation != null){
            return towerLocation;
        }else{
            return theirHQ;
        }


    }



}
