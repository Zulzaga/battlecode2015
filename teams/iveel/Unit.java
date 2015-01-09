package iveel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;

public abstract class Unit extends BaseBot {

    protected Direction facing;

    public Unit(RobotController rc) {
        super(rc);
        facing = getRandomDirection();
        rand = new Random(rc.getID());
    }

    public Direction getRandomDirection() {
        return Direction.values()[(int) (rand.nextDouble() * 8)];
    }

    public void mineAndMove() throws GameActionException {
        if (rc.senseOre(rc.getLocation()) > 1) {// there is ore, so try to mine
            if (rc.isCoreReady() && rc.canMine()) {
                rc.mine();
            }
        } else {// no ore, so look for ore
            moveAround();
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

    public Direction getMoveDir(MapLocation dest) {
        Direction[] dirs = getDirectionsToward(dest);
        for (Direction d : dirs) {
            if (rc.canMove(d)) {
                return d;
            }
        }
        return null;
    }

    public void buildUnit(RobotType type) throws GameActionException {
        if (rc.getTeamOre() > type.oreCost) {
            Direction buildDir = getRandomDirection();
            if (rc.isCoreReady() && rc.canBuild(buildDir, type)) {
                rc.build(buildDir, type);
            }
        }
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

}
