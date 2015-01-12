package kairat.structures;

import kairat.Channels;
import kairat.Structure;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class Barracks extends Structure implements Channels {

    public Barracks(RobotController rc) throws GameActionException {
        super(rc);

        // Initialize channelID and increment total number of this RobotType
        channelStartWith = Channel_Barracks;
        initChannelNum();
    }

    public void execute() throws GameActionException {
        swarmPot();
    }

    public void player6() throws GameActionException {
        // if (rc.readBroadcast(Channel_Soldier) < 10) {
        // spawnUnit(RobotType.SOLDIER);
        // }
    }

    /**
     * swarmPot example
     * 
     * @throws GameActionException
     */
    public void swarmPot() throws GameActionException {
        if (rc.isCoreReady() && rc.getTeamOre() > 200) {
            Direction newDir = getSpawnDirection(RobotType.SOLDIER, theirHQ);
            if (newDir != null && rc.readBroadcast(Channel_Soldier) < 10) {
                rc.spawn(newDir, RobotType.SOLDIER);
            }
        }
    }

    @Override
    public void createChannel() {

    }

}
