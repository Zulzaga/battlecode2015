package iveel.structures;

import battlecode.common.*;
import iveel.Structure;

public class Barracks extends Structure{

    public Barracks(RobotController rc) {
        super(rc);
        // TODO Auto-generated constructor stub
    }
    
    public void execute() throws GameActionException {
        swarmPot();
        transferSupplies();
        rc.yield();
    }
    
    public void player6() throws GameActionException{
        spawnUnit(RobotType.SOLDIER);
        transferSupplies();
    }
    
    /**
     * swarmPot example
     * @throws GameActionException
     */
    public void swarmPot() throws GameActionException{
        if (rc.isCoreReady() && rc.getTeamOre() > 200) {
            Direction newDir = getSpawnDirection(RobotType.SOLDIER);
            if (newDir != null) {
                rc.spawn(newDir, RobotType.SOLDIER);
            }
        }
    }

}
