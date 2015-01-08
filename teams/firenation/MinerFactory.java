package firenation;

import battlecode.common.RobotController;

public class MinerFactory implements Building {

    private RobotController myRC;

    public MinerFactory(RobotController rc) {
        this.myRC = rc;
    }
}
