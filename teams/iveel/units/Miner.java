package iveel.units;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import iveel.Unit;

public class Miner extends Unit {

    public Miner(RobotController rc) {
        super(rc);
        // TODO Auto-generated constructor stub
    }
    
    public void execute() throws GameActionException {
        attackEnemyZero();
        mineAndMove();
        
        transferSupplies();
        rc.yield();
    }

}
