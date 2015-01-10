package iveel.structures;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import iveel.Structure;

public class AerospaceLab extends Structure {

    public AerospaceLab(RobotController rc) throws GameActionException {
        super(rc);
        channelStartWith = "18";

        // TODO Auto-generated constructor stub
        int num = rc.readBroadcast(12);
        rc.broadcast(12, num +1);
    }

}
