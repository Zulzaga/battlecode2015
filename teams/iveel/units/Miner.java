package iveel.units;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import iveel.Unit;


/*
 * Spawned at Miner Factory
 * Has a weak attack at short range
 * One of two units that can mine ore [0.2, 3].
 * 
 * 
 */
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

    public void explorePathWithRightPreferenceToward( MapLocation dest){
        
    }

}
