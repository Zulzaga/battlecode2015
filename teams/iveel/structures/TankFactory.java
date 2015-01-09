package iveel.structures;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import iveel.Structure;

public class TankFactory extends Structure {

    public TankFactory(RobotController rc) {
        super(rc);
    }

    public void execute() throws GameActionException {
        spawnUnit(RobotType.MINER);
    }

}
