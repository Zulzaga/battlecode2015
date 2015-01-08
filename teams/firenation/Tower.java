package firenation;

import battlecode.common.RobotController;

public class Tower implements Building {

    private RobotController myRC;

    public Tower(RobotController rc) {
        this.myRC = rc;
    }
}
