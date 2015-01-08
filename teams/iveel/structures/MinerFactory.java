package iveel.structures;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import iveel.Structure;

public class MinerFactory extends Structure{

    public MinerFactory(RobotController rc) {
        super(rc);
        // TODO Auto-generated constructor stub
    }
    
    public void execute() throws GameActionException {
        spawnUnit(RobotType.MINER);
        
        transferSupplies();
        rc.yield();
    }

}
