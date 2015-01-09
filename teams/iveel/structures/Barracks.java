package iveel.structures;

import battlecode.common.*;
import iveel.Structure;

public class Barracks extends Structure {

    public Barracks(RobotController rc) {
        super(rc);
    }

    public void execute() throws GameActionException {
        swarmPot();
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
            Direction newDir = getSpawnDirection(RobotType.SOLDIER);
            if (newDir != null) {
                rc.spawn(newDir, RobotType.SOLDIER);
            }
        }
    }

}
