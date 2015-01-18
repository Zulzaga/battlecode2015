package team105.structures;

import team105.Structure;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class Barracks extends Structure {

    public Barracks(RobotController rc) throws GameActionException {
        super(rc);
    }

    public void execute() throws GameActionException {
        // swarmPot();
    }
    

    public void player6() throws GameActionException {
        spawnUnit(RobotType.SOLDIER);
    }

    /**
     * swarmPot example
     * 
     * @throws GameActionException
     */
    public void swarmPot() throws GameActionException {
        if (rc.isCoreReady() && rc.getTeamOre() > 200) {
            Direction newDir = getSpawnDirection(RobotType.SOLDIER, theirHQ);
            if (newDir != null) {
                rc.spawn(newDir, RobotType.SOLDIER);
            }
        }
    }

}