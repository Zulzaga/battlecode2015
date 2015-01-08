package iveel;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public abstract class Structure extends BaseBot {

    public Structure(RobotController rc) {
        super(rc);
    }
    
    /**
     * 
     * @param type
     * @return
     */
    public  Direction getSpawnDirection(RobotType type) {
        Direction[] dirs = getDirectionsToward(theirHQ);
        for (Direction d : dirs) {
            if (rc.canSpawn(d, type)) {
                return d;
            }
        }
        return null;
    }
    
    public void spawnUnit(RobotType type) throws GameActionException {
        Direction randomDir = getRandomDirection();
        if(rc.isCoreReady()&&rc.canSpawn(randomDir, type)){
            rc.spawn(randomDir, type);
        }
    }

}
