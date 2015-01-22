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
        MapLocation[] enemies = rc.senseEnemyTowerLocations();
        if (enemies.length > 0 && Clock.getRoundNum() > 600) {
            swarmLocation = enemies[0];
        }
        // if (Clock.getRoundNum() < 1000) {
        swarmPotLauncher();
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
        RobotInfo[] enemies = rc.senseNearbyRobots(25, theirTeam);
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

    private void directionToIntBroadcast(Direction dir) throws GameActionException {
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
}