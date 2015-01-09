package firenation.structures;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import firenation.Structure;

public class Helipad extends Structure {

    public Helipad(RobotController rc) throws GameActionException {
        super(rc);
        // TODO Auto-generated constructor stub
        
        int num = rc.readBroadcast(10);
        rc.broadcast(10, num +1);
    }

    public void execute() throws GameActionException {
        spawnUnit(RobotType.MINER);
    }

}
