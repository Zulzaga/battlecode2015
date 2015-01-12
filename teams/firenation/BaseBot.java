package firenation;

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
 * USE OF CHANNELS:
 * Can only use radio channels from 0 to 65535. 
 * Each robot has its own unique channelNum. 
 * 
 * ///////////   5digits used : DON'T USE 5digit channels!///////////
 *   
 *   
 * == Structures (except HQ) 
 *   AA BB C
 *   
 *   AA: type of unit.
 *   BB: spawned order number. 1st or 2nd. 
 *   C: up this stucture's management. 
 *   
 *   10 - Helipad
 *   11 - tower
 *   12 - SupplyDepot
 *   13 - TechnologyInstitute
 *   14 - Barracks
 *   15 - HandwashStation
 *   16 - TrainingField
 *   17 - TankFactory
 *   18 - AerospaceLab
 *   19 - MinerFactory
 *   
 * == Units:   
 *   A BBB C 
 *   
 *   A: type of unit
 *   BBB: spawned order number. 1st or 2nd 
 *   C: up to this unit's management.
 *   
 *   2 - Beaver
 *   3 - Soldier
 *   4 - Drone
 *   5 - Tank 
 *   6 - Miner // no more than 550
 *   
 * ////////   4 digits used: DON'T USE 4digit channels begin with 1,2, 7-9.///////////
 *   Rest of robots (few number) must be 4 digits.
 *   
 * == Drone, Launcher, Computer, Commander
 *   A BB C - must be 4digits.
 *   
 *   6 - Basher
 *   7 - Launcher
 *   8 - Computer
 *   9 - Commander
 *    
 *   For example: 1st drone's channel is 1 01_
 *   
 * == HQ:  //all important global info
 *   A BBB 
 *   A:  Always 1 (making it different from structures).
 *   BBB:  up this stucture's management. 
 *   
 * == Army:
 *  A BB C
 *  A: always 2
 *  BB: spawned order number (1st army, 2nd army)
 *  C: up to this army's management.
 *  
 *  
 * ======= Channel_PathDirect:  C
 *          Channel_PathMiddle1: D;
 *          Channel_PathMiddle2: B
 *          Channel_PathCorner1: E;
 *          Channel_PathCorner2: E;
 *  
 *  In our map, we have 5 essential target destinations which lay on diagonal. (Map could be flipped or rotated) 
 *  
 *          A ********** theirHQ
 *          *   B            *
 *          *        C       *
 *          *            D   *
 *          myHQ *********** E
 *          
 *  
 *  C: centerOfMap;
 *  A: endCorner2;
 *  E: endCorner1;
 *  D: middle1;
 *  B: middle2;
 *   
 *   After sending our drones and exploring the map, we would broadcast if these kind of paths are available.
 *    A, B, D or E: 
 *    0: have not explored yet.
 *    1: there is a path to that point.
 *    2: there may not path but there are not many voids.
 *    3: there may not path and there are many voids. (useless) 
 *     
 *     Since the map is symmetric if   myHQ --B  = theirHQ -- D and myHQ --A = their -- E.
 *  In the beginning, we check if there are paths from myHQ to A, B, C, D and E.
 *  if to B and to D exist, then there is a path from myHQ -> B (or D) -> theirHQ. (XX)
 *  if to A and to E exist, then there is a path from myHQ -> A (or E) -> theirHQ. (YY)
 *  if to C exist, then there is a path from myHQ -> theirHQ. (ZZ)
 *  
 *     So if AE = 11; then there is a path from myHQ -> B (or D). A good path to reach enemyHQ.
 *     31 -> E is better than A for miners since enemy has block to go there.
 *     13 -> A is better than E for miners.
 *     
 *     A good path to reach enemyHQ. ranking:
 *     11
 *     12
 *     21
 *     
 *     Good path for miners 
 *     1,3
 *     1,2
 *     2,3
 *     2,2
 *     
 *     
 *      
 *   
 *   
 *   
 *  
 */
public abstract class BaseBot {

    // Channels for keeping track of total number of each robots //
    public static int Channel_Helipad = 10000;
    public static int Channel_Tower = 11000;
    public static int Channel_SupplyDepot = 12000;
    public static int Channel_TechnologyInstitute = 13000;
    public static int Channel_Barracks = 14000;
    public static int Channel_HandwashStation = 15000;
    public static int Channel_TrainingField = 16000;
    public static int Channel_TankFactory = 17000;
    public static int Channel_AerospaceLab = 18000;
    public static int Channel_MinerFactory = 19000;

    public static int Channel_Beaver = 20000;
    public static int Channel_Soldier = 30000;
    public static int Channel_Miner = 60000;// no more than 550 miners
    public static int Channel_Tank = 50000;
    public static int Channel_Drone = 40000; 
     

    public static int Channel_Basher = 6000;
    public static int Channel_Launcher = 7000;
    public static int Channel_Computer = 8000;
    public static int Channel_Commander = 9000;
    public static int Channel_Army = 2000;

    // ////////Specific channels for stragies and mode of the game///////////////
    public static int Channel_Strategy = 1000;
    public static int Channel_ArmyMode = 1001;
    //explained above
    public static int Channel_PathCenter= 1002;
    public static int Channel_PathMiddle1= 1003;
    public static int Channel_PathMiddle2= 1004;
    public static int Channel_PathCorner1= 1005;
    public static int Channel_PathCorner2= 1006;

    
    //Reachable ore areas (first 5 explorer drones' channels)
    public static int Channel_OreAreaX1 = 40011; 
    public static int Channel_OreAreaY1 = 40012;

    public static int Channel_OreAreaX2 = 40021;
    public static int Channel_OreAreaY2 = 40022;

    public static int Channel_OreAreaX3 = 40031;
    public static int Channel_OreAreaY3 = 40032;

    public static int Channel_OreAreaX4 = 40041;
    public static int Channel_OreAreaY4 = 40042;

    public static int Channel_OreAreaX5 = 40051;
    public static int Channel_OreAreaY5 = 40052;





    // for channeling
    protected int channelID; // this channel would be used for this robot's
    // info; unique for each robot.
    protected int channelStartWith; // should be Channel_Beaver or ...

    protected RobotController rc;
    protected MapLocation myHQ, theirHQ;
    protected Team myTeam, theirTeam;
    protected Random rand;

    public BaseBot(RobotController rc) {
        this.rc = rc;
        this.myHQ = rc.senseHQLocation();
        this.theirHQ = rc.senseEnemyHQLocation();
        this.myTeam = rc.getTeam();
        this.theirTeam = this.myTeam.opponent();
        this.rand = new Random(rc.getID());
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
    }

    /**
     * Create a new channel for an army. Number of army must be limited to 99.
     * 
     * @return
     * @throws GameActionException
     */
    public int newArmyGetChannelID() throws GameActionException {
        int spawnedOrder = rc.readBroadcast(Channel_Army) + 1;
        rc.broadcast(Channel_Army, spawnedOrder);
        return Channel_Army + spawnedOrder * 10;
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

    public void attackLeastHealthEnemy() throws GameActionException {
        RobotInfo[] enemies = getEnemiesInAttackingRange();
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

        if (rc.isWeaponReady() && rc.canAttackLocation(toAttack)) {
            rc.attackLocation(toAttack);
        }
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
