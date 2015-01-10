package iveel.units;

import iveel.Unit;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

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
        channelStartWith = "36";

    }

    public void execute() throws GameActionException {
        attackEnemyZero();
        mineAndMove();
    }

    public void explorePathWithRightPreferenceToward(MapLocation dest) {

    }

}
