package hugo;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public abstract class Unit extends BaseBot{

    public Unit(RobotController rc) {
        super(rc);
    }
    
    public  Direction getMoveDir(MapLocation dest) {
        Direction[] dirs = getDirectionsToward(dest);
        for (Direction d : dirs) {
            if (rc.canMove(d)) {
                return d;
            }
        }
        return null;
    }

    public  Direction getBuildDirection(RobotType type) {
        Direction[] dirs = getDirectionsToward(theirHQ);
        for (Direction d : dirs) {
            if (rc.canBuild(d, type)) {
                return d;
            }
        }
        return null;
    }

}
