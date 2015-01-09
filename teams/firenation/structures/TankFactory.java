package firenation.structures;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import firenation.Structure;

public class TankFactory extends Structure {

    public TankFactory(RobotController rc) throws GameActionException {
        super(rc);
        // TODO Auto-generated constructor stub
        int num = rc.readBroadcast(11);
        rc.broadcast(11, num +1);

    }

    public void execute() throws GameActionException {
        spawnUnit(RobotType.MINER);
    }

}