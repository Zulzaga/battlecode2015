package firenation.structures;


import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import firenation.Structure;

public class Tower extends Structure {

    /*
     * Tower's channels are 11BBC
     */

    public Tower(RobotController rc) throws GameActionException {
        super(rc);

        // Initialize channelID and increment total number of this RobotType
        channelStartWith = Channel_Tower;
        initChannelNum();

        // Tower will broadcast its posX and posY at channel 11BB0 and 11BB1
        rc.broadcast(this.channelID, rc.getLocation().x);
        rc.broadcast(this.channelID + 1, rc.getLocation().y);
    }

    public void execute() throws GameActionException {
        attackEnemyZero();

        transferSupplies();
        rc.yield();
    }

}
