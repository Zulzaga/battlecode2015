package firenation.units;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import firenation.Unit;

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
    }

    public void execute() throws GameActionException {
        attackEnemyZero();
        mineAndMove();
    }

    public void explorePathWithRightPreferenceToward(MapLocation dest) {

    }

}