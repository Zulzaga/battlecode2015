package firenation.structures;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import firenation.Structure;


/*
 * Channels:  
 *   AA BB CC DDD:
 *   AA:  Always 10 (making it different from structures).
 *   BB: for special purpose. 00 if nothing special.
 *   CC: 
 *   DDD: up this stucture's management. 
 *   
 */
public class HQ extends Structure {

    public MapLocation centerOfMap;

    public HQ(RobotController rc) throws GameActionException {
        super(rc);
        centerOfMap = new MapLocation((this.myHQ.x + this.theirHQ.x) / 2,
                (this.myHQ.y + this.theirHQ.y) / 2);
        System.out.println("x ---" +centerOfMap.x);
        System.out.println("y ---" +centerOfMap.y);

        rc.broadcast(7, 0);
        rc.broadcast(8, 0);
        rc.broadcast(9, 0);
        rc.broadcast(10, 0);
        rc.broadcast(11, 0);
        rc.broadcast(12, 0);

        
        
        
        
    }

    public void execute() throws GameActionException {
        swarmPot();
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
            Direction newDir = getSpawnDirection(RobotType.BEAVER);
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

}