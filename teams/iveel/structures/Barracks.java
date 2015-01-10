package iveel.structures;

import battlecode.common.*;
import iveel.Channels;
import iveel.Structure;

public class Barracks extends Structure implements Channels {

    public Barracks(RobotController rc) throws GameActionException {
        super(rc);
        
        
        //Initialize channelID and increment total number of this RobotType
        channelStartWith = Channel_Barracks;
        initChannelNum(); 
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
            Direction newDir = getSpawnDirection(RobotType.SOLDIER, theirHQ);
            if (newDir != null) {
                rc.spawn(newDir, RobotType.SOLDIER);
            }
        }
    }

    @Override
    public void createChannel() {
        
    }

}
