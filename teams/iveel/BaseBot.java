package iveel;

import iveel.structures.AerospaceLab;
import iveel.structures.Barracks;
import iveel.structures.HQ;
import iveel.structures.HandwashStation;
import iveel.structures.Helipad;
import iveel.structures.MinerFactory;
import iveel.structures.SupplyDepot;
import iveel.structures.TankFactory;
import iveel.structures.TechnologyInstitute;
import iveel.structures.Tower;
import iveel.structures.TrainingField;
import iveel.units.Basher;
import iveel.units.Beaver;
import iveel.units.Commander;
import iveel.units.Computer;
import iveel.units.Drone;
import iveel.units.Launcher;
import iveel.units.Miner;
import iveel.units.Soldier;
import iveel.units.Tank;

import java.util.Random;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

/*BaseBot represents Unit and Structure.
 * General:
 * 
 * Starts with 500 ore, and each team automatically receives 5 ore per turn before any mining income
 * 
 * 
 * 
 * USE OF  CHANNELS.
 * 1. 
 * 2. Number of spawned beavers.
 * 3. 
 * 4. Path explorer with right preference
 * 5. Path explorer with left preference
 * 6.
 * 7. Barrack   4; 200-700
 * 8. Miner factory 3; 0-300
 * 9. HandwashStation 2; 1000-1300
 * 10. Helipad 2; 500-1000
 * 11. Tank factory 4; 700-1200
 * 12. Aerospace lab 2; 1000-1700
 * 
 *   channel numbers must be
 *   
 *   HQ:
 *   AA BB CC DDD:
 *   
 * == AA:  Always 10 (making it different from structures).
 *   BB: for special purpose. 00 if nothing special.
 *   CC: 
 *   DDD: up this stucture's management. 
 *   
 * == Structures (except HQ) 
 *   AA BB CC DDD - must be 9 digits.
 *   
 *   AA: type of unit
 *   BB: for special purpose. 00 if nothing special.
 *   CC: spawned order number. 1st or 2nd. 
 *   DDD: up this stucture's management. 
 *   
 *   11 - tower
 *   12 - SupplyDepot
 *   13 - TechnologyInstitute
 *   14 - Barracks
 *   15 - HandwashStation
 *   16 - TrainingField
 *   17 - TankFactory
 *   18 - AerospaceLab
 *   19 - MinerFactory
 *   20 - Helipad
 *   
 *   
 * == Units:   
 *   31 - Beaver
 *   32 - Soldier
 *   33 - Computer
 *   34 - Basher
 *   35 - Drone
 *   
 *   36 - Miner
 *   37 - Commander
 *   38 - Tank
 *   39 - Launcher
 *   
 * == Keep track total number of each robot.
 *   AA - must be 2 digits.
 *   AA - type of unit.
 *   
 * == Armies
 *    Keep track of all info about armies and their last dest.
 *    Each army unit listens its army channel which is unique.  
 *    
 *    AAA BB 
 *  
 *  
 *  
 */
public abstract class BaseBot {

    protected RobotController rc;
    protected MapLocation myHQ, theirHQ;
    protected Team myTeam, theirTeam;
    protected Random rand;
    protected int channelNum;
    protected String channelStartWith;

    public BaseBot(RobotController rc) {
        this.rc = rc;
        this.myHQ = rc.senseHQLocation();
        this.theirHQ = rc.senseEnemyHQLocation();
        this.myTeam = rc.getTeam();
        this.theirTeam = this.myTeam.opponent();
        this.rand = new Random(rc.getID());
    }
    
    public void initChannelNum() throws NumberFormatException, GameActionException{
        int spawnedOrder = rc.readBroadcast(Integer.parseInt(channelStartWith));
    }
    

    /**
     * Find a list of directions toward destination.
     * 
     * @param dest
     * @return
     */
    public Direction[] getDirectionsToward(MapLocation dest) {
        Direction toDest = rc.getLocation().directionTo(dest);
        Direction[] dirs = { toDest, toDest.rotateLeft(), toDest.rotateRight(),
                toDest.rotateLeft().rotateLeft(),
                toDest.rotateRight().rotateRight() };

        return dirs;
    }

    public RobotInfo[] getAllies() {
        RobotInfo[] allies = rc.senseNearbyRobots(Integer.MAX_VALUE, myTeam);
        return allies;
    }

    public RobotInfo[] getEnemiesInAttackingRange() {
        RobotInfo[] enemies = rc.senseNearbyRobots(
                RobotType.SOLDIER.attackRadiusSquared, theirTeam);
        return enemies;
    }

    public void attackLeastHealthEnemy(RobotInfo[] enemies)
            throws GameActionException {
        if (enemies.length == 0) {
            return;
        }

        double minEnergon = Double.MAX_VALUE;
        MapLocation toAttack = null;
        for (RobotInfo info : enemies) {
            if (info.health < minEnergon) {
                toAttack = info.location;
                minEnergon = info.health;
            }
        }

        rc.attackLocation(toAttack);
    }

    protected void attackEnemyZero() throws GameActionException {
        RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(rc.getLocation(),
                rc.getType().attackRadiusSquared, rc.getTeam().opponent());
        if (nearbyEnemies.length > 0) {// there are enemies nearby
            // try to shoot at them
            // specifically, try to shoot at enemy specified by nearbyEnemies[0]
            if (rc.isWeaponReady()
                    && rc.canAttackLocation(nearbyEnemies[0].location)) {
                rc.attackLocation(nearbyEnemies[0].location);
            }
        }
    }

    public void beginningOfTurn() {
        if (rc.senseEnemyHQLocation() != null) {
            theirHQ = rc.senseEnemyHQLocation();
        }
    }

    public void endOfTurn() throws GameActionException {
        transferSupplies();
    }

    public void go() throws GameActionException {
        beginningOfTurn();
        execute();
        endOfTurn();
    }

    public void execute() throws GameActionException {

    }

    public void transferSupplies() throws GameActionException {
        RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(),
                GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED, rc.getTeam());
        double lowestSupply = rc.getSupplyLevel();
        double transferAmount = 0;
        MapLocation suppliesToThisLocation = null;
        for (RobotInfo ri : nearbyAllies) {
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

    protected Direction getRandomDirection() {
        // System.out.println("heereeeee" +
        // Direction.values()[(int)(rand.nextDouble()*8)]);
        return Direction.values()[(int) (rand.nextDouble() * 8)];
    }

}
