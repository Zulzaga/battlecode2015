package iveel.structures;

import battlecode.common.*;
import iveel.Structure;

public class HQ extends Structure {

    public MapLocation centerOfMap;

    public HQ(RobotController rc) throws GameActionException {
        super(rc);
        centerOfMap = new MapLocation((this.myHQ.x + this.theirHQ.x) / 2,
                (this.myHQ.y + this.theirHQ.y) / 2);
        
        //set number of structures to 0
//        * 7. Barrack   4; 200-700
//        * 8. Miner factory 3; 0-300
//        * 9. HandwashStation 2; 1000-1300
//        * 10. Helipad 2; 500-1000
//        * 11. Tank factory 4; 700-1200
//        * 12. Aerospace lab 2; 1000-1700
        
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
