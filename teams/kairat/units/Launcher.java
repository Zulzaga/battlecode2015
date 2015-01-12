package kairat.units;

import kairat.Unit;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

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

    public Launcher(RobotController rc) throws GameActionException {
        super(rc);

        // Initialize channelID and increment total number of this RobotType
        channelStartWith = Channel_Launcher;
        initChannelNum();
    }

}
