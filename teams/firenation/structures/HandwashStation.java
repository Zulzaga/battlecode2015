package firenation.structures;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import firenation.Structure;

public class HandwashStation extends Structure{

    public HandwashStation(RobotController rc) throws GameActionException {
        super(rc);
        // TODO Auto-generated constructor stub
        int num = rc.readBroadcast(9);
        rc.broadcast(9, num +1);
    }

}
