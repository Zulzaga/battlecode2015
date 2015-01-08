package hugo;

import battlecode.common.Direction;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public abstract class Structure extends BaseBot {

    public Structure(RobotController rc) {
        super(rc);
    }
    
    public  Direction getSpawnDirection(RobotType type) {
        Direction[] dirs = getDirectionsToward(theirHQ);
        for (Direction d : dirs) {
            if (rc.canSpawn(d, type)) {
                return d;
            }
        }
        return null;
    }

}
