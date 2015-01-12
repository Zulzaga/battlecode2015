package firenation.units;

import java.util.Arrays;
import java.util.Comparator;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import firenation.Unit;

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

    private MapLocation movingLocation;

    public Tank(RobotController rc) throws GameActionException {
        super(rc);

        // Initialize channelID and increment total number of this RobotType
        channelStartWith = Channel_Tank;
        initChannelNum();
    }

    public void execute() throws GameActionException {
        int numOfTowers = rc.senseTowerLocations().length;

        if (rc.readBroadcast(this.channelID) != 1) {
            for (int i = 1; i <= numOfTowers; i++) {
                int towerChannel = Channel_Tower + i * 10;
                int numOfTanks = rc.readBroadcast(towerChannel + 2);
                if (numOfTanks < 5) {
                    int posX = rc.readBroadcast(towerChannel);
                    int posY = rc.readBroadcast(towerChannel + 1);
                    movingLocation = new MapLocation(posX + 1, posY);
                    Direction movingDirection = getMoveDir(movingLocation);
                    if (rc.isCoreReady() && rc.canMove(movingDirection)) {
                        rc.move(movingDirection);
                        rc.broadcast(towerChannel + 2, numOfTanks + 1);
                        rc.broadcast(this.channelID, 1);
                    }
                } else {
                    swarmPotTank();
                }
            }
        } else {
            attackLeastHealthEnemy();
            Direction movingDirection = getMoveDir(movingLocation);
            if (rc.isCoreReady() && rc.canMove(movingDirection)) {
                rc.move(movingDirection);
            }
        }
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

        if (Clock.getRoundNum() > 1500) {
            harassToLocation(theirHQ);
            return;
        } else {
            attackLeastHealthEnemy();
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

}
