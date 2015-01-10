package iveel.structures;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import iveel.Structure;

public class HandwashStation extends Structure{

    public HandwashStation(RobotController rc) throws GameActionException {
        super(rc);
        channelStartWith = "15";

        // TODO Auto-generated constructor stub
        int num = rc.readBroadcast(9);
        rc.broadcast(9, num +1);
    }

}
