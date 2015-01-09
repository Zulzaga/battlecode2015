package iveel.structures;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import iveel.Structure;

public class MinerFactory extends Structure{

    public MinerFactory(RobotController rc) throws GameActionException {
        super(rc);
        // TODO Auto-generated constructor stub
        
        int num = rc.readBroadcast(8);
        rc.broadcast(8, num +1);
    }
    
    public void execute() throws GameActionException {
        spawnUnit(RobotType.MINER);
        
        transferSupplies();
        rc.yield();
    }

}
