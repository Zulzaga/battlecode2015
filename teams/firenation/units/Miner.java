package firenation.units;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import firenation.Unit;

/*
 * Spawned at Miner Factory
 * Has a weak attack at short range
 * One of two units that can mine ore [0.2, 3].
 * 
 * 
 */
public class Miner extends Unit {
    

    /**
     * If oreArea has been specified, miner should go its own.
     * Otherwise, it should reach that area first. 
     * @param rc
     * @param oreArea
     * @throws GameActionException
     */
    public Miner(RobotController rc) throws GameActionException {
        super(rc); 
        //Initialize channelID and increment total number of this RobotType
        channelStartWith = Channel_Miner;
        initChannelNum(); 
//        int oreX = rc.readBroadcast(Channel_OreAreaX) ;
//        if (oreX != 0){
//            destination = new MapLocation(oreX,rc.readBroadcast(Channel_OreAreaY));
//        }else{
//            destination = null;
//        }
    }

    public void execute() throws GameActionException {
        if (destination == null){
        attackEnemyZero();
        mineAndMove();
        }else{
//            avoid();
            moveToLocation(destination);
            if (rc.getLocation().distanceSquaredTo(destination) < 3){
                destination = null;
            }
        }
    }

    public void explorePathWithRightPreferenceToward(MapLocation dest) {

    }
    

}