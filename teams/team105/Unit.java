package team105;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;

public abstract class Unit extends BaseBot {

    protected boolean shouldStand = true;
    public ArrayList<Double> miningRecord = new ArrayList<Double>();
    public ArrayList<MapLocation> lastSteps = new ArrayList<MapLocation>();
    public ArrayList<Integer> lastNumMine = new ArrayList<Integer>();

    protected Direction facing;
    protected boolean armyUnit = false;
    protected int armyChannel;
    protected MapLocation destination = null;

    public ArrayList<MapLocation> recentPathRecord = new ArrayList<MapLocation>();



    public Unit(RobotController rc) {
        super(rc);
        facing = getRandomDirection();
        rand = new Random(rc.getID());

        // emptyMatrix();
        // These directions are general and HQ is likely to order this unit to
        // go forward one of them.


    }


    public Direction getRandomDirection() {
        return Direction.values()[(int) (rand.nextDouble() * 8)];
    }

    public void mineAndMove() throws GameActionException {
        double sensedOre = rc.senseOre(rc.getLocation());
        if (sensedOre > 1) {// there is ore, so try to mine
            if (rc.isCoreReady() && rc.canMine()) {
                rc.mine();
                recordMineAmount(sensedOre);
            }
        } else {// no ore, so look for ore
            moveAround();
        }
    }

    public void recordMineAmount(double ore) {
        miningRecord.add(ore);
        if (miningRecord.size() > 10) {
            miningRecord.remove(0);
        }
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
     * Method that would make the robot rotate right until it reaches the
     * direction that does not have any obstacle in front of it. Does not avoid
     * enemy towers, so that attacking units can destroy enemy towers
     */
    private void faceNormalDoNotAvoidTowersRight() {
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
     * Method that make the robot move around the map in random direction, if
     * the direction does not have obstacle
     * 
     * @throws GameActionException
     */
    protected void moveAroundAlways() throws GameActionException {
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
    private void faceNormalLeft() {
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
    private void faceNormalRight() {
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
     * Checks if MapLocation ml can be attacked by other towers
     * 
     * @param ml
     *            MapLocation of the RobotController
     * @return true if safe to move, otherwise false
     */
    private boolean safeToMove(MapLocation ml) {
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


    public Direction getMoveDir(MapLocation dest) {
        Direction[] dirs = getDirectionsToward(dest);
        for (Direction d : dirs) {
            if (rc.canMove(d)) {
                return d;
            }
        }
        return null;
    }

    public Direction getBuildingDirectionRetreat(RobotType type) {
        Direction[] dirs = getDirectionsToward(theirHQ);
        for (Direction d : dirs) {
            if (rc.canBuild(d, type)) {
                return d;
            }
        }
        return null;
    }

    public Direction getBuildDirection(RobotType type) {
        Direction[] dirs = getDirectionsToward(theirHQ);
        for (Direction d : dirs) {
            if (rc.canBuild(d, type)) {
                return d;
            }
        }
        return null;
    }

    /**
     * SwarmPot example
     * 
     * Gather until having more than 500 ore. Then move toward enemyHQ
     * 
     * @throws GameActionException
     */
    public void swarmPot() throws GameActionException {
        if (rc.isCoreReady()) {
            if (rc.getTeamOre() < 500) {
                // mine
                if (rc.senseOre(rc.getLocation()) > 0) {
                    rc.mine();
                } else {
                    Direction newDir = getMoveDir(this.theirHQ);

                    if (newDir != null) {
                        rc.move(newDir);
                    }
                }
            } else {
                // build barracks
                Direction newDir = getBuildDirection(RobotType.BARRACKS);
                if (newDir != null) {
                    rc.build(newDir, RobotType.BARRACKS);
                }
            }
        }
    }

    /**
     * Move around randomly do not avoid enemy towers (for soldiers to attack
     * towers)
     * 
     * @throws GameActionException
     */
    private void moveAroundRandomly() throws GameActionException {
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
     * Method that would make the robot rotate left until it reaches the
     * direction that does not have any obstacle in front of it. Does not avoid
     * enemy towers, so that they can attack them
     */
    private void faceNormalDoNotAvoidTowersLeft() {
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

    // if the location is not in range of Towers and HQ
    public boolean safeToMove2(MapLocation ml) {
        return safeFromTowers(ml) && safeFromHQ(ml);
    }

    // if the location is not in range of Towers
    public boolean safeFromTowers(MapLocation ml) {
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

    // if the location is not in range of their HQ
    public boolean safeFromHQ(MapLocation location) {
        int numEnemyTowers = rc.senseEnemyTowerLocations().length;
        if (numEnemyTowers >= 5)
            return (location.add(location.directionTo(theirHQ)))
                    .distanceSquaredTo(theirHQ) > RobotType.HQ.sensorRadiusSquared;
        else if (numEnemyTowers >= 2)
            return location.distanceSquaredTo(theirHQ) > RobotType.HQ.sensorRadiusSquared;
        else
            return location.distanceSquaredTo(theirHQ) > RobotType.HQ.attackRadiusSquared;
    }

    // if the location is safe from other structures
    public boolean safeFromShortShooters(MapLocation ml) {

        RobotInfo[] enemiesFromLocation = rc.senseNearbyRobots(ml,
                RobotType.SOLDIER.attackRadiusSquared, theirTeam);
        return enemiesFromLocation.length == 0;
    }

    // move to location
    public boolean moveToLocation(MapLocation location)
            throws GameActionException {
        if (rc.isCoreReady()) {
            Direction dirs[] = getDirectionsToward(location);

            for (Direction newDir : dirs) {
                if (rc.canMove(newDir)) {
                    if (!safeToMove2(rc.getLocation().add(newDir))
                            || !safeFromShortShooters(rc.getLocation().add(
                                    newDir))) {
                        continue;
                    } else if (rc.canMove(newDir)) {
                        rc.move(newDir);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    // move to location for drone
    public void moveToLocationExtandingRange(MapLocation location)
            throws GameActionException {
        if (rc.isCoreReady()) {
            // emptyMatrix();
            // Directions where normal exist
            MapLocation currentLoc = rc.getLocation();

            // markStartMatrix(currentLoc);

            ArrayList<Direction> dirs = new ArrayList<Direction>();
            Direction towardDest = currentLoc.directionTo(location);

            Direction toRight = towardDest.rotateRight().rotateRight();
            Direction toLeft = towardDest.rotateLeft().rotateLeft();

            TerrainTile forward = rc.senseTerrainTile(currentLoc
                    .add(towardDest));

            if (forward.equals(TerrainTile.NORMAL)) {
                dirs.add(towardDest);
            } else if (forward.equals(TerrainTile.VOID)) {

                int rightSideNormals = 0;
                int leftSideNormals = 0;

                // Right side locations
                MapLocation right1 = currentLoc.add(towardDest.rotateRight());
                MapLocation right2 = right1.add(towardDest);

                MapLocation right3 = right1.add(toRight);
                MapLocation right5 = right3.add(toRight);
                MapLocation right7 = right5.add(toRight);

                MapLocation right4 = right2.add(toRight);
                MapLocation right6 = right4.add(toRight);
                MapLocation right8 = right6.add(toRight);

                MapLocation[] rightSideLocs = new MapLocation[] { right1,
                        right2, right3, right4, right5, right6 };// right7,
                                                                 // right8

                // Left side locations
                MapLocation left1 = currentLoc.add(towardDest.rotateLeft());
                MapLocation left2 = left1.add(towardDest);

                MapLocation left3 = left1.add(toLeft);
                MapLocation left5 = left3.add(toLeft);
                MapLocation left7 = left5.add(toLeft);

                MapLocation left4 = left2.add(toLeft);
                MapLocation left6 = left4.add(toLeft);
                MapLocation left8 = left6.add(toLeft);

                MapLocation[] leftSideLocs = new MapLocation[] { left1, left2,
                        left3, left4, left5, left6 }; // left7, left8

                for (MapLocation r : rightSideLocs) {
                    // markSpecMartrix(r);
                    if (isNormalTerrain(r)) {
                        rightSideNormals += 1;
                    }
                }

                for (MapLocation l : leftSideLocs) {
                    // markSpecMartrix(l);
                    if (isNormalTerrain(l)) {
                        leftSideNormals += 1;
                    }
                }

                if (rightSideNormals > leftSideNormals) {
                    dirs.add(towardDest.rotateLeft());
                    dirs.add(toLeft);

                } else if (rightSideNormals > leftSideNormals) {
                    dirs.add(towardDest.rotateRight());
                    dirs.add(toRight);
                }
                dirs.add(toRight);
                dirs.add(toLeft);
            } else if (forward.equals(TerrainTile.OFF_MAP)) {
                destination = null;
                dirs.add(getMoveDir(theirHQ));
            }

            // MatrixtoString();
            for (Direction newDir : dirs) {
                if (rc.canMove(newDir)) {
                    if (!safeToMove2(rc.getLocation().add(newDir))
                            || !safeFromShortShooters(rc.getLocation().add(
                                    newDir))) {
                        continue;
                    } else if (rc.canMove(newDir)) {
                        rc.move(newDir);
                        // System.out.println("MOVING!!!");
                        return;
                    }
                }
            }

            // System.out.println("no way to move");
        }
    }

    public void moveToLocationNotSafe(MapLocation location)
            throws GameActionException {
        if (rc.isCoreReady()) {
            Direction dirs[] = getDirectionsToward(location);

            for (Direction newDir : dirs) {
                if (rc.canMove(newDir)) {
                    rc.move(newDir);
                    return;
                }
            }
        }
    }

    public void moveToLocationSafeFromHQ(MapLocation location)
            throws GameActionException {

        if (rc.isCoreReady()) {
            Direction dirs[] = getDirectionsToward(location);

            for (Direction newDir : dirs) {
                if (rc.canMove(newDir)) {
                    if (!safeFromHQ(rc.getLocation().add(newDir))) {
                        continue;
                    } else if (rc.canMove(newDir)) {
                        rc.move(newDir);
                        return;
                    }
                }
            }
        }
    }

    // run to the opposite direction of the robot
    public void avoid(RobotInfo robot) throws GameActionException {
        if (rc.isCoreReady()) {
            Direction oppositeDir = getMoveDir(rc.getLocation().add(
                    rc.getLocation().directionTo(robot.location).opposite()));

            if (oppositeDir != null) {
                Direction dirs[] = getDirectionsToward(rc.getLocation().add(
                        oppositeDir));

                for (Direction newDir : dirs) {
                    if (newDir != null) {
                        if (!safeToMove2(rc.getLocation().add(newDir))) {
                            continue;
                        } else if (rc.canMove(newDir)) {
                            rc.move(newDir);
                            break;
                        }
                    }
                }
            }
        }
    }

    // attack enemy
    public void attack() throws GameActionException {
        attackLeastHealthEnemy();
    }

    // harass and move to the location
    public void harassToLocation(MapLocation ml) throws GameActionException {
        RobotInfo nearestEnemy = senseNearestEnemy(rc.getType());

        if (nearestEnemy != null) {
            int distanceToEnemy = rc.getLocation().distanceSquaredTo(
                    nearestEnemy.location);
            if (distanceToEnemy <= rc.getType().attackRadiusSquared) {
                attack();
                // attackRobot(nearestEnemy.location);
                avoid(nearestEnemy);
            } else {

                if (nearestEnemy.type == RobotType.DRONE) {
                    if (shouldStand) {
                        shouldStand = false; // waited once
                    } else {
                        moveToLocation(nearestEnemy.location);
                        shouldStand = true;
                    }
                } else if (nearestEnemy.type != RobotType.TANK) {

                    moveToLocation(nearestEnemy.location);
                    // attack();
                    // attackRobot(nearestEnemy.location);

                } else {
                    avoid(nearestEnemy);
                    // attack();
                    // attackRobot(nearestEnemy.location);
                }
            }
        } else {
            moveToLocationExtandingRange(ml);
            // attackRobot(nearestEnemy.location);
        }

    }

    // return the nearest enemy robot
    public RobotInfo senseNearestEnemy(RobotType type) {
        RobotInfo[] enemies = senseNearbyEnemies(type);

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
            return nearestRobot;
        }
        return null;
    }

    // return all the sensible enemies
    public RobotInfo[] senseNearbyEnemies(RobotType type) {
        return rc.senseNearbyRobots(type.sensorRadiusSquared, theirTeam);
    }

    public RobotInfo[] getEnemiesInAttackingRange() {
        RobotInfo[] enemies = rc.senseNearbyRobots(
                RobotType.DRONE.attackRadiusSquared, theirTeam);
        return enemies;
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
    public static class RobotHealthComparator implements Comparator<RobotInfo> {

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

    public void destroyNearestTower() {

    }

    public void startAttackingTowersAndHQ() throws GameActionException {
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

    public void moveAndRecordLocation(MapLocation location)
            throws GameActionException {
        Direction dirs[] = getDirectionsToward(location);

        for (Direction newDir : dirs) {
            if (rc.canMove(newDir)) {
                if (!safeToMove2(rc.getLocation().add(newDir))
                        || !safeFromShortShooters(rc.getLocation().add(newDir))) {
                    continue;
                } else if (rc.canMove(newDir)) {
                    rc.move(newDir);
                    return;
                }
            }
        }
        recordMovement();
    }

    public void recordMovement() {
        recentPathRecord.add(rc.getLocation());
        if (recentPathRecord.size() > 10) {
            recentPathRecord.remove(0);
        }

    }

    public boolean blocked() {
        int repetition = 0;
        for (int i = 0; i < recentPathRecord.size(); i++) {
            if (recentPathRecord.get(i).equals(rc.getLocation())) {
                repetition += 1;
            }
        }
        if (repetition > 3) {
            // System.out.println("locked!") ;
            return true;
        }
        return false;
    }
}
