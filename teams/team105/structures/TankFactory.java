package team105.structures;


import team105.Structure;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class TankFactory extends Structure {

    public TankFactory(RobotController rc) throws GameActionException {
        super(rc);

    }

    public void execute() throws GameActionException {
        spawnUnit(RobotType.TANK);
    }

}
