package team105.units;

import team105.Unit;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

/*
 * Spawned at Aerospace Lab
 * Very high HP
 * No attack
 * Moves slowly
 * Generates a MISSILE every 6 turns and can store up to 6
 * Super cool
 * 
 * 
 * NORTH - 1;
 * NORTH_EAST - 2
 * EAST - 3
 * SOUTH_EAST - 4
 * SOUTH - 5
 * SOUTH_WEST - 6
 * WEST - 7
 * NORTH_WEST - 8
 */
public class Launcher extends Unit {

    private MapLocation swarmLocation;

    public Launcher(RobotController rc) throws GameActionException {
        super(rc);

        // Initialize channelID and increment total number of this RobotType
        channelStartWith = Channel_Launcher;
        initChannelNum();
        int swarmX = rc.readBroadcast(Channel_Launcher + 1);
        int swarmY = rc.readBroadcast(Channel_Launcher + 2);
        swarmLocation = new MapLocation(swarmX, swarmY);
    }

    public void execute() throws GameActionException {
        // System.out.println(swarmLocation);
        if (Clock.getRoundNum() > 1800) {
            swarmLocation = theirHQ;
        }
        // if (Clock.getRoundNum() < 1000) {
        harassToLocationLauncher(swarmLocation);
        // } else {
        // launcherStartAttackingTowersAndHQ();
        // }
        rc.yield();
    }

    /**
     * SwarmPot strategy for launcher
     */
    public void swarmPotLauncher() throws GameActionException {
        launcherAttackUnit();
        if (rc.getMissileCount() < 6) {
            if (rc.isCoreReady() && rc.canSpawn(facing, RobotType.MISSILE)) {
                rc.spawn(facing, RobotType.MISSILE);
            }
        }
        moveToLocation(swarmLocation);
    }

    private void launcherAttackUnit() throws GameActionException {
        RobotInfo[] enemies = rc.senseNearbyRobots(100, theirTeam);
        RobotInfo[] friends = null;
        MapLocation attackLocation = null;
        if (enemies.length > 0) {
            attackLocation = enemies[0].location;
            friends = rc.senseNearbyRobots(attackLocation, 2, myTeam);
        }
        if (friends != null) {
            Direction d = rc.getLocation().directionTo(attackLocation);
            if (friends.length < 2 && rc.isWeaponReady() && rc.canLaunch(d)) {
                rc.launchMissile(d);
                // directionToIntBroadcast(d);
            }
        }
    }

    private void launcherAttackLoc(MapLocation loc) throws GameActionException {
        Direction d = rc.getLocation().directionTo(loc);
        if (rc.isWeaponReady() && rc.canLaunch(d)) {
            rc.launchMissile(d);
        }
    }

    /**
     * Attack towers if it sees towers, otherwise attack enemy with lowest
     * health
     * 
     * @throws GameActionException
     */
    private void attackTower() throws GameActionException {
        RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(rc.getLocation(), 25,
                rc.getTeam().opponent());

        int numberOfEnemies = nearbyEnemies.length;
        if (numberOfEnemies > 0) {
            MapLocation attackBuildingLocation = null;
            for (RobotInfo enemy : nearbyEnemies) {
                if (enemy.type == RobotType.TOWER) {
                    attackBuildingLocation = enemy.location;
                }
            }

            if (attackBuildingLocation != null) {
                launcherAttackLoc(attackBuildingLocation);
            } else {
                launcherAttackUnit();
            }
        }
    }

    public void launcherStartAttackingTowersAndHQ() throws GameActionException {
        MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();

        MapLocation nearestAttackableTowerSafeFromHQ = nearestAttackableTowerSafeFromHQ(enemyTowers);

        if (nearestAttackableTowerSafeFromHQ != null) {
            attackTower();
            moveToLocationSafeFromHQ(nearestAttackableTowerSafeFromHQ);
        } else if (enemyTowers.length > 0) {
            attackTower();
            moveToLocationNotSafe(enemyTowers[0]);
        } else {
            attackTower();
            moveToLocationNotSafe(theirHQ);
        }
    }

    public MapLocation nearestAttackableTowerSafeFromHQ(
            MapLocation[] enemyTowers) {
        MapLocation towerLocation = null;
        int distance = Integer.MAX_VALUE;
        MapLocation droneLocation = rc.getLocation();

        for (MapLocation location : enemyTowers) {
            int tempDistance = droneLocation.distanceSquaredTo(location);
            if (tempDistance < distance && safelyAttackableFromHQ(location)) {
                distance = tempDistance;
                towerLocation = location;
            }
        }

        return towerLocation;
    }

    public boolean safelyAttackableFromHQ(MapLocation location) {
        return location.distanceSquaredTo(theirHQ) > 1;

    }

    private void directionToIntBroadcast(Direction dir)
            throws GameActionException {
        switch (dir) {
        case NORTH:
            rc.broadcast(Channel_Launcher + 3, 1);
            break;
        case NORTH_EAST:
            rc.broadcast(Channel_Launcher + 3, 2);
            break;
        case EAST:
            rc.broadcast(Channel_Launcher + 3, 3);
            break;
        case SOUTH_EAST:
            rc.broadcast(Channel_Launcher + 3, 4);
            break;
        case SOUTH:
            rc.broadcast(Channel_Launcher + 3, 5);
            break;
        case SOUTH_WEST:
            rc.broadcast(Channel_Launcher + 3, 6);
            break;
        case WEST:
            rc.broadcast(Channel_Launcher + 3, 7);
            break;
        case NORTH_WEST:
            rc.broadcast(Channel_Launcher + 3, 8);
            break;
        }
    }

    public void harassToLocationLauncher(MapLocation ml)
            throws GameActionException {
        RobotInfo nearestEnemy = senseNearestEnemyTank();

        if (nearestEnemy != null) {
            int distanceToEnemy = rc.getLocation().distanceSquaredTo(
                    nearestEnemy.location);
            if (distanceToEnemy <= 50) { // hard coded distance
                int numOfFriends = rc.senseNearbyRobots(nearestEnemy.location,
                        2, myTeam).length;
                Direction d = rc.getLocation().directionTo(
                        nearestEnemy.location);
                if (numOfFriends < 2 && rc.isWeaponReady() && rc.canLaunch(d)) {
                    rc.launchMissile(d);
                }
            } else if (nearestEnemy.type != RobotType.TANK
                    && nearestEnemy.type != RobotType.LAUNCHER) {
                moveToLocation(nearestEnemy.location);
            } else if (nearestEnemy.type == RobotType.TANK
                    && distanceToEnemy > rc.getType().sensorRadiusSquared) {
                moveToLocation(nearestEnemy.location);
            } else if (nearestEnemy.type == RobotType.TANK) { // distanceToEnemy
                                                              // <
                                                              // rc.getType().sensorRadiusSquared
                int numAlliesAroundTank = rc.senseNearbyRobots(
                        nearestEnemy.location,
                        rc.getType().sensorRadiusSquared, myTeam).length;
                numAlliesAroundTank -= rc.senseNearbyRobots(
                        nearestEnemy.location,
                        rc.getType().sensorRadiusSquared, theirTeam).length;
                // System.out.println("polidood");
                // if(numAlliesAroundTank > 2)
                // moveToLocation(nearestEnemy.location);
                if (numAlliesAroundTank > 1
                        || rc.senseNearbyRobots(nearestEnemy.location,
                                nearestEnemy.type.attackRadiusSquared, myTeam).length > 0)
                    moveToLocation(nearestEnemy.location);
            }

            else if (nearestEnemy.type == RobotType.LAUNCHER
                    && distanceToEnemy > rc.getType().sensorRadiusSquared) {
                moveToLocation(nearestEnemy.location);
            } else if (nearestEnemy.type == RobotType.LAUNCHER) { // distanceToEnemy
                                                                  // <=
                                                                  // rc.getType().sensorRadiusSquared
                int numAlliesAroundTank = rc.senseNearbyRobots(
                        nearestEnemy.location,
                        rc.getType().sensorRadiusSquared, myTeam).length;
                numAlliesAroundTank -= rc.senseNearbyRobots(
                        nearestEnemy.location,
                        rc.getType().sensorRadiusSquared, theirTeam).length;
                // System.out.println("polidood");
                if (numAlliesAroundTank > 4)
                    startAttackingTowersAndHQ();
            }

            else if (nearestEnemy.type == RobotType.TOWER) {
                int numAlliesAroundTower = rc.senseNearbyRobots(
                        nearestEnemy.location, 50, myTeam).length;
                numAlliesAroundTower -= rc.senseNearbyRobots(
                        nearestEnemy.location, 50, theirTeam).length;
                if (numAlliesAroundTower > 5)
                    startAttackingTowersAndHQ();
            }
        } else {
            moveToLocation(ml);
        }
    }

    public RobotInfo senseNearestEnemyTank() {
        RobotInfo[] enemies = senseNearbyEnemiesTank();

        if (enemies.length > 0) {
            RobotInfo nearestRobot = null;
            int nearestDistance = Integer.MAX_VALUE;
            for (RobotInfo robot : enemies) {
                int distance = rc.getLocation().distanceSquaredTo(
                        robot.location);
                if (distance < nearestDistance && robot.type != RobotType.HQ
                        && robot.location.distanceSquaredTo(theirHQ) > 10) {
                    nearestDistance = distance;
                    nearestRobot = robot;
                }
            }
            return nearestRobot;
        }
        return null;
    }

    // return all the sensible enemies
    public RobotInfo[] senseNearbyEnemiesTank() {
        return rc.senseNearbyRobots(1000, theirTeam);
    }
}