package iveel.units;

import battlecode.common.RobotController;
import iveel.Unit;


/*
 * Spawned at Aerospace Lab
 * Very high HP
 * No attack
 * Moves slowly
 * Generates a MISSILE every 6 turns and can store up to 6
 * Super cool
 * 
 */
public class Launcher extends Unit {

    public Launcher(RobotController rc) {
        super(rc);
        channelStartWith = "39";

        // TODO Auto-generated constructor stub
    }

}
