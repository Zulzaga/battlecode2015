package firenation.structures;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import firenation.Structure;

public class Barracks extends Structure {

    public Barracks(RobotController rc) throws GameActionException {
        super(rc);
        // TODO Auto-generated constructor stub
        int num = rc.readBroadcast(7);
        rc.broadcast(7, num +1);
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