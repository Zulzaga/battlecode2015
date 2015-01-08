package iveel.units;

import battlecode.common.*;
import iveel.Unit;

public class Beaver extends Unit {

    public Beaver(RobotController rc) {
        super(rc);
    }
    
    public void execute() throws GameActionException {
        if (rc.isCoreReady()) {
            if (rc.getTeamOre() < 500) {
                //mine
                if (rc.senseOre(rc.getLocation()) > 0) {
                    rc.mine();
                }
                else {
                    Direction newDir = getMoveDir(this.theirHQ);

                    if (newDir != null) {
                        rc.move(newDir);
                    }
                }
            }
            else {
                //build barracks
                Direction newDir = getBuildDirection(RobotType.BARRACKS);
                if (newDir != null) {
                    rc.build(newDir, RobotType.BARRACKS);
                }
            }
        }

        rc.yield();
    }

}
