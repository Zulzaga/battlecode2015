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
    
    
    /*
     * ARMY MODE CHANNEL: 1000 (all barraks listen to this channel to decide to build armies).
     *   If it broadcasts 0, then not building any army.
     *   Otherwise, it is new army's channel number.
     *   For each army channel 2___:
     *       2__0: broadcasting AA BBB. 
     *       AA - unit limit. (must be less than 65)
     *       BBB - clock turn limit. (must be less than 1000)
     *       
     *       2__1: x coordinate of destination.
     *       2__2: y coordinate of destination.
     *   
     */
    
    public void mayBuildArmy(){
        if (rc.readBroadcast(Channel_ArmyMode)){
            
        }
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
