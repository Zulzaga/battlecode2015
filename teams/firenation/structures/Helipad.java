package firenation.structures;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import firenation.Structure;

public class Helipad extends Structure {

    public Helipad(RobotController rc) throws GameActionException {
        super(rc);

        // Initialize channelID and increment total number of this RobotType
        channelStartWith = Channel_Helipad;
        initChannelNum();
    }

    public void execute() throws GameActionException {
        try {
        	int roundNum = Clock.getRoundNum();
            if( (roundNum < 300 || roundNum > 500) && rc.isCoreReady() && rc.readBroadcast(Channel_Drone) < 200) {
                Direction spawnDir = getSpawnDirection(RobotType.DRONE);
                if (spawnDir != null) {
                    rc.spawn(spawnDir, RobotType.DRONE);
                }
            }
        } catch (GameActionException e) {
            e.printStackTrace();
        }

        rc.yield();
    }

}
