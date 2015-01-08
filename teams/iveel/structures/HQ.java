package iveel.structures;

import battlecode.common.*;
import iveel.Structure;

public class HQ extends Structure {
    
    public MapLocation centerOfMap;

    public HQ(RobotController rc) {
        super(rc);
        centerOfMap = new MapLocation( (this.myHQ.x + this.theirHQ.x) / 2,
                (this.myHQ.y + this.theirHQ.y) / 2);
    }
    
    public void execute() throws GameActionException {
        swarmPot();
        transferSupplies();
        rc.yield();
    }
    
    /**
     * Player 6 example
     * @throws GameActionException 
     */
    public void player6() throws GameActionException{
        attackEnemyZero();
        spawnUnit(RobotType.BEAVER);
        
        transferSupplies();
        
    }
    
    
    /**
     * Swarm pot example
     * @throws GameActionException
     */
    public void swarmPot() throws GameActionException{
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
        }
        else {
            rallyPoint = this.theirHQ;
        }
        rc.broadcast(0, rallyPoint.x);
        rc.broadcast(1, rallyPoint.y);
    }

}
