package team105.units;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import battlecode.common.*;
import team105.Unit;

public class Soldier extends Unit {


    MapLocation initDest;
    Boolean lastMovementForDest = true;
    boolean dirDecisionRight = true;

    public Soldier(RobotController rc) throws GameActionException {
        super(rc);

        //Initialize channelID and increment total number of this RobotType
        channelStartWith = Channel_Soldier;
        initChannelNum(); 

    }


    /**
     * Initialize channelNum AA BBB 
     * Increment total number of this robot type.
     * @throws GameActionException
     */
    public void initChannelNum() throws GameActionException{
        int spawnedOrder = rc.readBroadcast(channelStartWith) + 1;
        rc.broadcast(channelStartWith, spawnedOrder);
        channelID = channelStartWith + spawnedOrder*10;


        MapLocation[] towers = rc.senseEnemyTowerLocations();
        if( spawnedOrder%3 == 1 || towers.length == 0){
            initDest = theirHQ;
        }else{
            initDest = towers[spawnedOrder%towers.length];
        }
        
        if (spawnedOrder%2 == 0){
            dirDecisionRight = false; 
        }
    }

    public void execute() throws GameActionException {
        attackAndSurround();
    }

    public void player6() throws GameActionException {
        attackTower();
        moveAround();
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


    public void attackAndSurround() throws GameActionException{
        RobotInfo nearestEnemy = senseNearestEnemyTank(rc.getType());

        if (nearestEnemy != null) {
            int distanceToEnemy = rc.getLocation().distanceSquaredTo(
                    nearestEnemy.location);
            if (distanceToEnemy <= rc.getType().attackRadiusSquared) {
                if(rc.isWeaponReady() && rc.canAttackLocation(nearestEnemy.location))
                    rc.attackLocation(nearestEnemy.location);
                //attackLeastHealthEnemy();
            } else if (nearestEnemy.type != RobotType.TANK && 
                    nearestEnemy.type != RobotType.LAUNCHER) {
                moveToLocation(nearestEnemy.location);

            } else if(nearestEnemy.type == RobotType.TANK && distanceToEnemy > rc.getType().sensorRadiusSquared){
                moveToLocation(nearestEnemy.location);
            } else if(nearestEnemy.type == RobotType.TANK){ // distanceToEnemy < rc.getType().sensorRadiusSquared
                int numAlliesAroundTank = rc.senseNearbyRobots(nearestEnemy.location, rc.getType().sensorRadiusSquared, myTeam).length;
                numAlliesAroundTank -= rc.senseNearbyRobots(nearestEnemy.location, rc.getType().sensorRadiusSquared, theirTeam).length;
                //System.out.println("polidood");
                //              if(numAlliesAroundTank > 2)
                //                  moveToLocation(nearestEnemy.location);
                if(numAlliesAroundTank > 1 || rc.senseNearbyRobots(nearestEnemy.location, nearestEnemy.type.attackRadiusSquared, myTeam).length > 0)
                    moveToLocation(nearestEnemy.location);
            }

            else if(nearestEnemy.type == RobotType.LAUNCHER && distanceToEnemy > rc.getType().sensorRadiusSquared){
                moveToLocation(nearestEnemy.location);
            } 
            else if(nearestEnemy.type == RobotType.LAUNCHER){ // distanceToEnemy <= rc.getType().sensorRadiusSquared
                int numAlliesAroundTank = rc.senseNearbyRobots(nearestEnemy.location, rc.getType().sensorRadiusSquared, myTeam).length;
                numAlliesAroundTank -= rc.senseNearbyRobots(nearestEnemy.location, rc.getType().sensorRadiusSquared, theirTeam).length;
                //System.out.println("polidood");
                if(numAlliesAroundTank > 4)
                    startAttackingTowersAndHQ();
            }

            else if(nearestEnemy.type == RobotType.TOWER){
                int numAlliesAroundTower = rc.senseNearbyRobots(nearestEnemy.location, 50, myTeam).length;
                numAlliesAroundTower -= rc.senseNearbyRobots(nearestEnemy.location, 50, theirTeam).length;
                if(numAlliesAroundTower > 5)
                    startAttackingTowersAndHQ();
            }
        } else {
            setDestination();
            moveSmart(initDest);
        }
    }



    public void moveSmart(MapLocation destination) throws GameActionException{
        Direction toGo = rc.getLocation().directionTo(initDest);
        if (rc.isCoreReady()){
            int loops = 0;
            while (loops < 8){
                if (rc.canMove(toGo)){
                    rc.move(toGo);
                    break;
                }
                toGo = toGo.rotateLeft();
                loops +=1;
            }                

            }
        }

//    MapLocation tileInFront = rc.getLocation().add(facing);
//
//    if (rc.senseTerrainTile(tileInFront) != TerrainTile.NORMAL
//            || !safeToMove(tileInFront)) {
//        int loops = 0;
//        while (loops < 7) {
//            facing = facing.rotateLeft();
//            if (rc.senseTerrainTile(rc.getLocation().add(facing)) == TerrainTile.NORMAL
//                    && safeToMove(rc.getLocation().add(facing))) {
//                break;
//            }
//            loops++;
//        }
//    }



    private void setDestination() {
        MapLocation[] towers = rc.senseEnemyTowerLocations();

        if (towers.length > 0) {
            MapLocation nearestTower = null;
            int nearestDistance = Integer.MAX_VALUE;
            for (MapLocation tower : towers) {
                if (tower.equals(initDest)){
                    return;
                }

                int distance = rc.getLocation().distanceSquaredTo(
                        tower);
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestTower = tower;
                }
            }
            initDest = nearestTower;
            return;
        }
        initDest = theirHQ;
    }


    public RobotInfo senseNearestEnemyTank(RobotType type) {
        RobotInfo[] enemies = rc.senseNearbyRobots(100, theirTeam);

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
            return nearestRobot;
        }
        return null;
    }


    public void moveAround() throws GameActionException {
        if (rand.nextDouble() < 0.05) {
            if (rand.nextDouble() < 0.5) {
                facing = facing.rotateLeft();
            } else {
                facing = facing.rotateRight();
            }
        }
        MapLocation tileInFront = rc.getLocation().add(facing);

        // check that the direction in front is not a tile that can be attacked
        // by the enemy towers
        MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
        boolean tileInFrontSafe = true;
        for (MapLocation m : enemyTowers) {
            if (m.distanceSquaredTo(tileInFront) <= RobotType.TOWER.attackRadiusSquared) {
                tileInFrontSafe = false;
                break;
            }
        }

        // check that we are not facing off the edge of the map
        if (rc.senseTerrainTile(tileInFront) != TerrainTile.NORMAL
                || !tileInFrontSafe) {
            facing = facing.rotateLeft();
        } else {
            // try to move in the facing direction
            if (rc.isCoreReady() && rc.canMove(facing)) {
                rc.move(facing);
            }
        }
    }


    /**
     * Comparator for the hit points of health of two different robots
     * (Ascending order)
     */
    static class RobotHealthComparator implements Comparator<RobotInfo> {

        @Override
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
