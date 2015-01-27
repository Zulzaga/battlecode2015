package team105.structures;

import team105.Structure;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class AerospaceLab extends Structure {

    public AerospaceLab(RobotController rc) throws GameActionException {
        super(rc);
    }

    public void execute() throws GameActionException {
        if (rc.readBroadcast(Channel_Launcher) < 10){
            spawnUnit(RobotType.LAUNCHER);
        }
    }

}
