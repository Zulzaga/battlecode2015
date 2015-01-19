package team105;

import java.util.Arrays;
import java.util.List;

import team105.BaseBot;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public abstract class Structure extends BaseBot {

    private static List<Direction> listOfDirections = Arrays.asList(
            Direction.EAST, Direction.NORTH, Direction.NORTH_EAST,
            Direction.NORTH_WEST, Direction.SOUTH, Direction.SOUTH_EAST,
            Direction.SOUTH_WEST, Direction.WEST);

    public Structure(RobotController rc) {
        super(rc);
        supplyUpkeep = 0;
    }

    public void beginningOfTurn() {
        if (rc.senseEnemyHQLocation() != null) {
            theirHQ = rc.senseEnemyHQLocation();
        }
    }

    public void endOfTurn() throws GameActionException {

    }

    public void go() throws GameActionException {
        beginningOfTurn();
        execute();
        endOfTurn();
    }

    public void execute() throws GameActionException {

    }

    public void transferSupplies() throws GameActionException {
        //structures always 0 then never calls drone.
        RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(),
                GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED, rc.getTeam());
        double lowestSupply = rc.getSupplyLevel();
        double transferAmount = 0;
        MapLocation suppliesToThisLocation = null;
        for (RobotInfo ri : nearbyAllies) {
            //prefer drones which gone spread it.
            if ( ri.type == RobotType.DRONE && ri.supplyLevel < 10000){
                transferAmount = (rc.getSupplyLevel() - ri.supplyLevel) / 2;
                rc.transferSupplies((int) transferAmount, ri.location);
                break;
            }else{
                //transfer to lowest supplied one
                if (ri.supplyLevel < lowestSupply) {
                    lowestSupply = ri.supplyLevel;
                    transferAmount = (rc.getSupplyLevel() - ri.supplyLevel) / 2;
                    suppliesToThisLocation = ri.location;
                }
            }
            if (suppliesToThisLocation != null) {
                rc.transferSupplies((int) transferAmount, suppliesToThisLocation);
            }
        }
    }

    /**
     * 
     * @param type
     * @return
     */
    public Direction getSpawnDirection(RobotType type) {
        Direction[] dirs = getDirectionsToward(theirHQ);
        for (Direction d : dirs) {
            if (rc.canSpawn(d, type)) {
                return d;
            }
        }

        for (Direction d : dirs) {
            if (rc.canSpawn(d.opposite(), type)) {
                return d.opposite();
            }
        }

        return null;
    }

    public void spawnUnit(RobotType type) throws GameActionException {
        Direction randomDir = getRandomDirection();
        if (rc.isCoreReady() && rc.canSpawn(randomDir, type)) {
            rc.spawn(randomDir, type);
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
    private void spawnUnitOreCollector(RobotType type)
            throws GameActionException {

        MapLocation currentLocation = rc.getLocation();
        Direction richDirection = Direction.EAST;
        double oreAmount = rc.senseOre(currentLocation.add(richDirection));
        for (Direction d : listOfDirections) {
            if ((rc.senseOre(currentLocation.add(d)) > oreAmount)
                    && rc.canSpawn(richDirection, type)) {
                richDirection = d;
            }
        }

        if (rc.isCoreReady()) {
            rc.spawn(richDirection, type);
        }
    }


    /**
     * 
     * @param type
     * @return
     */
    public Direction getSpawnDirection(RobotType type, MapLocation dest) {
        Direction[] dirs = getDirectionsToward(dest);
        for (Direction d : dirs) {
            if (rc.canSpawn(d, type)) {
                return d;
            }
        }
        return null;
    }
}
