package firenation.structures;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import firenation.Structure;

public class AerospaceLab extends Structure {

    public AerospaceLab(RobotController rc) throws GameActionException {
        super(rc);
        // TODO Auto-generated constructor stub
        int num = rc.readBroadcast(12);
        rc.broadcast(12, num +1);
    }

}
