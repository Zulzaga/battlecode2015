package DumbBot;

import battlecode.common.RobotController;

public class RobotPlayer {

    private static RobotController rc;

    public static void run(RobotController myrc) {
        rc = myrc;

        while (true) {
            rc.yield();
        }

    }
}
