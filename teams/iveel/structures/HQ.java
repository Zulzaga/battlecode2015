package iveel.structures;

import battlecode.common.*;
import iveel.Communication;
import iveel.Structure;
import iveel.Channels;



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
 * 13.
 *   HQ:
 *   
 *   AA BB CC DDD:
 *   
 *   AA:  Always 10 (making it different from structures).
 *   BB: for special purpose. 00 if nothing special.
 *   CC, DDD: up this stucture's management. 
 *   
 *   BB = 01 Total number of spawned each unit. 
 *   CC = unit's unique number
 *   DDD = number
 *   
 *   
 * 
 */
public class HQ extends Structure implements Channels {
    
   //Keep track all info about armies and their last dest.
   //Each army unit listens its army channel which is unique.

    
    public MapLocation centerOfMap;
    public static Communication communication;
//    public HashMap

    public HQ(RobotController rc) throws GameActionException {
        super(rc);
        channelStartWith = 10;

        centerOfMap = new MapLocation((this.myHQ.x + this.theirHQ.x) / 2,
                (this.myHQ.y + this.theirHQ.y) / 2);
        
        //set number of structures to 0
//        * 7. Barrack   4; 200-700
//        * 8. Miner factory 3; 0-300
//        * 9. HandwashStation 2; 1000-1300
//        * 10. Helipad 2; 500-1000
//        * 11. Tank factory 4; 700-1200
//        * 12. Aerospace lab 2; 1000-1700
        
        
        //Initialized total number of each robotType
        rc.broadcast(11, 0);
        rc.broadcast(12, 0);
        rc.broadcast(13, 0);
        rc.broadcast(14, 0);
        rc.broadcast(15, 0);
        rc.broadcast(16, 0);
        System.out.println("here");
        System.out.println(rc.readBroadcast(111111));
        System.out.println("here end");

    }

    public void execute() throws GameActionException {
//        swarmPot();
        buildArmy(10, RobotType.BEAVER, centerOfMap, 1000);
    }

    /**
     * Player 6 example
     * 
     * @throws GameActionException
     */
    public void player6() throws GameActionException {
        attackEnemyZero();
        spawnUnit(RobotType.BEAVER);

        transferSupplies();

    }

    /**
     * Swarm pot example
     * 
     * @throws GameActionException
     */
    public void swarmPot() throws GameActionException {
        int numBeavers = rc.readBroadcast(2);

        if (rc.isCoreReady() && rc.getTeamOre() > 100 && numBeavers < 10) {
            Direction newDir = getSpawnDirection(RobotType.BEAVER, theirHQ);
            if (newDir != null) {
                rc.spawn(newDir, RobotType.BEAVER);
                rc.broadcast(2, numBeavers + 1);
            }
        }
        MapLocation rallyPoint;
        if (Clock.getRoundNum() < 600) {
            rallyPoint = centerOfMap;
        } else {
            rallyPoint = this.theirHQ;
        }
        rc.broadcast(0, rallyPoint.x);
        rc.broadcast(1, rallyPoint.y);
    }

    @Override
    public void createChannel() {
        
        
    }

}
