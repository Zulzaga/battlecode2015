package iveel.units;

import battlecode.common.*;
import iveel.Unit;

public class Beaver extends Unit {

    public Beaver(RobotController rc) {
        super(rc);
    }
    
    public void execute() throws GameActionException {
        attackEnemyZero();
        if(Clock.getRoundNum()<700){
            buildUnit(RobotType.MINERFACTORY);
        }else{
            buildUnit(RobotType.BARRACKS);
        }
        mineAndMove();
        
        transferSupplies();
        rc.yield();
    }

}
