package team105.structures;

import team105.Structure;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class Helipad extends Structure {

    public Helipad(RobotController rc) throws GameActionException {
        super(rc);
    }

    public void execute() throws GameActionException {
        try {
        	int roundNum = Clock.getRoundNum();
            if(rc.isCoreReady() && rc.readBroadcast(Channel_Drone) < 5) {
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
