package kairat.structures;

import kairat.Structure;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class TrainingField extends Structure {

    public TrainingField(RobotController rc) throws GameActionException {
        super(rc);

        // Initialize channelID and increment total number of this RobotType
        channelStartWith = Channel_TrainingField;
        initChannelNum();
    }

}
