package Kairat_original;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;

public class RobotPlayer {

    static Direction facing;
    static Random rand;
    static RobotController rc;
    static List<Direction> listOfDirections = Arrays.asList(Direction.EAST,
            Direction.NORTH, Direction.NORTH_EAST, Direction.NORTH_WEST,
            Direction.SOUTH, Direction.SOUTH_EAST, Direction.SOUTH_WEST,
            Direction.WEST);
    static PathExplorer pex;

    public static void run(RobotController myrc) {

        rc = myrc;
        rand = new Random(rc.getID());
        // randomize starting direction
        facing = getRandomDirection();
        pex = new PathExplorer(rc);

        while (true) {
            /*
            if (Clock.getRoundNum() > 1000) {
                System.out.println("LOOK HERE BELOW THIS+++++++++++++");
                System.out.println(pex.aStar(rc.senseHQLocation()));
                System.out.println("LOOK HERE ABOVE THIS+++++++++++++");
            }
            */
            try {
                if (rc.getType() == RobotType.HQ) {
                    attackEnemyWithLowestHealth();
                    if (Clock.getRoundNum() < 500) {
                        spawnUnit(RobotType.BEAVER, true);
                    } else {
                        spawnUnit(RobotType.BEAVER, false);
                    }
                } else if (rc.getType() == RobotType.BEAVER) {
                    attackEnemyWithLowestHealth();
                    mineAndMove();
                    if (Clock.getRoundNum() < 700 && Clock.getRoundNum() > 300) {
                        buildUnit(RobotType.MINERFACTORY);
                        buildUnit(RobotType.BARRACKS);
                    } else if (Clock.getRoundNum() < 1000
                            && Clock.getRoundNum() > 500) {
                        buildUnit(RobotType.HELIPAD);
                    } else if (Clock.getRoundNum() < 1700
                            && Clock.getRoundNum() > 1000) {
                        buildUnit(RobotType.TANKFACTORY);
                    }
                } else if (rc.getType() == RobotType.MINER) {
                    attackEnemyWithLowestHealth();
                    mineAndMove();
                } else if (rc.getType() == RobotType.MINERFACTORY) {
                    attackEnemyWithLowestHealth();
                    if (Clock.getRoundNum() < 900) {
                        spawnUnit(RobotType.BEAVER, true);
                    } else {
                        spawnUnit(RobotType.BEAVER, false);
                    }
                    spawnUnit(RobotType.MINER, true);
                } else if (rc.getType() == RobotType.BARRACKS) {
                    spawnUnit(RobotType.SOLDIER, true);
                } else if (rc.getType() == RobotType.TOWER) {
                    attackEnemyWithLowestHealth();
                } else if (rc.getType() == RobotType.SOLDIER) {
                    attackTower();
                    moveAroundRandomly();
                } else if (rc.getType() == RobotType.HELIPAD) {
                    spawnUnit(RobotType.DRONE, true);
                } else if (rc.getType() == RobotType.DRONE) {
                    attackTower();
                    moveAroundRandomly();
                } else if (rc.getType() == RobotType.TANK) {
                    attackTower();
                    moveAroundRandomly();
                }
                
                if(Clock.getBytecodesLeft() > 1000)
                    transferSupplies();

            } catch (GameActionException e) {
                e.printStackTrace();
            }
            rc.yield();

        }

    }

    private static void transferSupplies() throws GameActionException {
        RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(),
                GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED, rc.getTeam());
        double lowestSupply = rc.getSupplyLevel();
        double transferAmount = 0;
        MapLocation supplyToLocation = null;
        for (RobotInfo ri : nearbyAllies) {
            if (ri.supplyLevel < lowestSupply) {
                lowestSupply = ri.supplyLevel;
                transferAmount = (rc.getSupplyLevel() - ri.supplyLevel) / 2;
                supplyToLocation = ri.location;
            }
        }

        if (supplyToLocation != null) {
            rc.transferSupplies((int) transferAmount, supplyToLocation);
        }

    }

    private static void buildUnit(RobotType type) throws GameActionException {
        if (rc.getTeamOre() > type.oreCost) {
            Direction buildDirection = getRandomDirection();
            if (rc.isCoreReady() && rc.canBuild(buildDirection, type)) {
                rc.build(buildDirection, type);
            }
        }
    }

    /**
     * method that will make a current rc attack the other robot with the lowest
     * health, that can be reached of course
     * 
     * @throws GameActionException
     */
    private static void attackEnemyWithLowestHealth()
            throws GameActionException {
        RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(rc.getLocation(),
                rc.getType().attackRadiusSquared, rc.getTeam().opponent());

        // List<RobotInfo> enemies = Arrays.asList(nearbyEnemies);
        // Collections.sort(enemies, new HealthComparator());
        // enemies.sort(new HealthComparator());
        int numberOfEnemies = nearbyEnemies.length;
        if (numberOfEnemies > 0) {
            Arrays.sort(nearbyEnemies, new RobotHealthComparator());
            if (rc.isWeaponReady()
                    && rc.canAttackLocation(nearbyEnemies[numberOfEnemies - 1].location)) {
                rc.attackLocation(nearbyEnemies[numberOfEnemies - 1].location);
            }
        }
    }

    private static void attackTower() throws GameActionException {
        RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(rc.getLocation(),
                rc.getType().attackRadiusSquared, rc.getTeam().opponent());

        // List<RobotInfo> enemies = Arrays.asList(nearbyEnemies);
        // Collections.sort(enemies, new HealthComparator());
        // enemies.sort(new HealthComparator());
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
     * Method that will spawn a robot in the direction that has most amount of
     * ore around the building
     * 
     * @param type
     *            type of the robot to be generated
     * @throws GameActionException
     */
    private static void spawnUnitOreCollector(RobotType type)
            throws GameActionException {

        MapLocation currentLocation = rc.getLocation();
        Direction richDirection = Direction.EAST;
        double oreAmount = rc.senseOre(currentLocation.add(richDirection));
        for (Direction d : listOfDirections) {
            if (rc.senseOre(currentLocation.add(d)) > oreAmount) {
                richDirection = d;
            }
        }

        if (rc.isCoreReady() && rc.canSpawn(richDirection, type)) {
            rc.spawn(richDirection, type);
        }

        // Map<Direction, MapLocation> locationsAround =
        // getLocationsAround(currentLocation);
        // OreAmountComparator oac = new OreAmountComparator(locationsAround);
        // TreeMap<Direction, MapLocation> sortedLocationsAround = new
        // TreeMap<Direction, MapLocation>(
        // oac);
        // for (Map.Entry<Direction, MapLocation> entry : sortedLocationsAround
        // .entrySet()) {
        // if (rc.isCoreReady() && rc.canSpawn(entry.getKey(), type)) {
        // rc.spawn(entry.getKey(), type);
        // break;
        // }
        // }
    }

    /**
     * Method that gets MapLocations around the current MapLocation of the robot
     * 
     * @param currentLocation
     *            current MapLocation of the robot
     * @return
     */
    private static Map<Direction, MapLocation> getLocationsAround(
            MapLocation currentLocation) {
        Map<Direction, MapLocation> locationsAround = new HashMap<Direction, MapLocation>();
        for (Direction d : listOfDirections) {
            MapLocation location = currentLocation.add(d);
            locationsAround.put(d, location);
        }
        return locationsAround;
    }

    /**
     * spawn a new unit on the grid in the random direction
     * 
     * @param type
     *            type of the robot to be generated
     * @throws GameActionException
     */
    private static void spawnUnit(RobotType type, boolean check)
            throws GameActionException {
        if (check == true) {
            Direction spawnDirection = getRandomDirection();
            if (rc.isCoreReady() && rc.canSpawn(spawnDirection, type)) {
                rc.spawn(spawnDirection, type);
            }
        }
    }

    /**
     * Method that generates random directions for the robot
     * 
     * @return new random direction
     */
    private static Direction getRandomDirection() {
        return Direction.values()[(int) (rand.nextDouble() * 8)];
    }

    /**
     * Method that commands the robot to mine if the current position has amount
     * of ore > 1, otherwise makes the robot move around
     * 
     * @throws GameActionException
     */
    private static void mineAndMove() throws GameActionException {
        if (rc.senseOre(rc.getLocation()) > 1) {
            if (rc.isCoreReady() && rc.canMine()) {
                rc.mine();
            }
        } else {
            moveAround();
        }

    }

    /**
     * Method that make the robot move around the map in random direction, if
     * the direction does not have obstacle
     * 
     * @throws GameActionException
     */
    private static void moveAround() throws GameActionException {
        if (rand.nextDouble() < 0.05) {
            if (rand.nextDouble() < 0.5) {
                facing = facing.rotateRight();
            } else {
                facing = facing.rotateLeft();
            }
        }

        if (rand.nextDouble() < 0.5) {
            faceNormalLeft();
        } else {
            faceNormalRight();
        }

        if (rc.isCoreReady() && rc.canMove(facing)) {
            rc.move(facing);
        }
    }

    /**
     * Method that would make the robot rotate left until it reaches the
     * direction that does not have any obstacle in front of it, and the
     * position cannot be attacked by the tower of the enemy.
     */
    private static void faceNormalLeft() {
        MapLocation tileInFront = rc.getLocation().add(facing);

        if (rc.senseTerrainTile(tileInFront) != TerrainTile.NORMAL
                || !safeToMove(tileInFront)) {
            int loops = 0;
            while (loops < 7) {
                facing = facing.rotateLeft();
                if (rc.senseTerrainTile(rc.getLocation().add(facing)) == TerrainTile.NORMAL
                        && safeToMove(rc.getLocation().add(facing))) {
                    break;
                }
                loops++;
            }
        }
    }

    /**
     * Method that would make the robot rotate right until it reaches the
     * direction that does not have any obstacle in front of it, and the
     * position cannot be attacked by the tower of the enemy.
     */
    private static void faceNormalRight() {
        MapLocation tileInFront = rc.getLocation().add(facing);

        if (rc.senseTerrainTile(tileInFront) != TerrainTile.NORMAL
                || !safeToMove(tileInFront)) {
            int loops = 0;
            while (loops < 7) {
                facing = facing.rotateRight();
                if (rc.senseTerrainTile(rc.getLocation().add(facing)) == TerrainTile.NORMAL
                        && safeToMove(rc.getLocation().add(facing))) {
                    break;
                }
                loops++;
            }
        }
    }

    /**
     * Method that would make the robot rotate left until it reaches the
     * direction that does not have any obstacle in front of it. Does not avoid
     * enemy towers, so that they can attack them
     */
    private static void faceNormalDoNotAvoidTowersLeft() {
        MapLocation tileInFront = rc.getLocation().add(facing);

        if (rc.senseTerrainTile(tileInFront) != TerrainTile.NORMAL) {
            List<Direction> possibleDirections = new ArrayList<Direction>();
            int loops = 0;
            while (loops < 7) {
                facing = facing.rotateLeft();
                if (rc.senseTerrainTile(rc.getLocation().add(facing)) == TerrainTile.NORMAL) {
                    possibleDirections.add(facing);
                }
                loops++;
            }
            facing = possibleDirections.get(rand.nextInt(possibleDirections
                    .size()));
        }
    }

    /**
     * Method that would make the robot rotate right until it reaches the
     * direction that does not have any obstacle in front of it. Does not avoid
     * enemy towers, so that attacking units can destroy enemy towers
     */
    private static void faceNormalDoNotAvoidTowersRight() {
        MapLocation tileInFront = rc.getLocation().add(facing);

        if (rc.senseTerrainTile(tileInFront) != TerrainTile.NORMAL) {
            List<Direction> possibleDirections = new ArrayList<Direction>();
            int loops = 0;
            while (loops < 7) {
                facing = facing.rotateRight();
                if (rc.senseTerrainTile(rc.getLocation().add(facing)) == TerrainTile.NORMAL) {
                    possibleDirections.add(facing);
                }
                loops++;
            }
            facing = possibleDirections.get(rand.nextInt(possibleDirections
                    .size()));
        }
    }

    /**
     * Move around randomly do not avoid enemy towers (for soldiers to attack
     * towers)
     * 
     * @throws GameActionException
     */
    private static void moveAroundRandomly() throws GameActionException {
        if (rand.nextDouble() < 0.05) {
            if (rand.nextDouble() < 0.5) {
                facing = facing.rotateLeft();
            } else {
                facing = facing.rotateRight();
            }
        }

        if (rand.nextDouble() < 0.5) {
            faceNormalDoNotAvoidTowersLeft();
        } else {
            faceNormalDoNotAvoidTowersRight();
        }

        if (rc.isCoreReady() && rc.canMove(facing)) {
            rc.move(facing);
        }
    }

    /**
     * Checks if MapLocation ml can be attacked by other towers
     * 
     * @param ml
     *            MapLocation of the RobotController
     * @return true if safe to move, otherwise false
     */
    private static boolean safeToMove(MapLocation ml) {
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

    /**
     * Comparator for the amount of ore at two different MapLocations
     * (descending order)
     */
    static class OreAmountComparator implements Comparator<Direction> {

        Map<Direction, MapLocation> map;

        public OreAmountComparator(Map<Direction, MapLocation> map) {
            this.map = map;
        }

        @Override
        public int compare(Direction o1, Direction o2) {
            if (rc.senseOre(map.get(o1)) > rc.senseOre(map.get(o2))) {
                return -1;
            } else {
                return 1;
            }
        }

    }
}