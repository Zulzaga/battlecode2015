package firenation.structures;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import firenation.Structure;

public class AerospaceLab extends Structure {

    public AerospaceLab(RobotController rc) throws GameActionException {
        super(rc);

        // Initialize channelID and increment total number of this RobotType
        channelStartWith = Channel_AerospaceLab;
        initChannelNum();
    }

    public void execute() throws GameActionException {
        swarmPot();
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
