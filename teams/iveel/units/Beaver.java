package iveel.units;

import battlecode.common.*;
import iveel.Unit;


/*
 * Spawned at HQ
 * Only unit that can build structures
 * One of two units that can mine ore [0.2, 3].
 * Has a weak attack at short range
 * Frequently hosed
 * 
 * 
 * 
 * 
 */
public class Beaver extends Unit {

    public Beaver(RobotController rc) {
        super(rc);
    }
    
    public void execute() throws GameActionException {
        swarmPot();
        transferSupplies();
        
        rc.yield();
    }
    
    
    /**
     * Transfer supply, player 6 example
     * @throws GameActionException
     */
    public void player6() throws GameActionException{
        attackEnemyZero();
        if(Clock.getRoundNum()<700){
            buildUnit(RobotType.MINERFACTORY);
        }else{
            buildUnit(RobotType.BARRACKS);
        }
        mineAndMove(); 
        transferSupplies();

    }
    

}
