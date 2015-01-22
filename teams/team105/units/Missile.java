package team105.units;

import team105.BaseBot;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class Missile {

    private RobotController rc;

    public Missile(RobotController rc) {
        this.rc = rc;
        // super(rc);
    }

    public void execute() throws GameActionException {
         if (rc.isCoreReady() && rc.canMove(Direction.NORTH)) {
        //System.out.println("missile is going to be executed");
             rc.move(Direction.SOUTH_EAST);
         }
        rc.yield();
    }
}
