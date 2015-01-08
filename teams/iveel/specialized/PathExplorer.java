package iveel.specialized;

import java.util.ArrayList;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.TerrainTile;
import iveel.units.Miner;

public class PathExplorer extends Miner {
    private Boolean exploredDeathLockPath = false;
    private ArrayList exploredPath = new ArrayList<MapLocation>();
    private int recordedLastTimeStamp;
    private Boolean rightHandRuled;  //ruled directions could be only left or right

    public PathExplorer(RobotController rc, boolean rightHandRuled) {
        super(rc);
        this.rightHandRuled = rightHandRuled;
        recordTimeAndPath();
        // TODO Auto-generated constructor stub
    }
    
    public void execute() throws GameActionException {
        transferSupplies();
        rc.yield();
    }
    
    public void recordTimeAndPath(){
        recordedLastTimeStamp = Clock.getRoundNum();
        exploredPath.add(rc.getLocation());
    }
    
    public  void moveAndExplore(MapLocation dest) throws GameActionException {
        if (rightHandRuled){
        facing = getMoveDirWithRightPreferenceToward(dest);
        }else{
            facing = getMoveDirWithLeftPreferenceToward(dest);
        }
        //try to move in the facing direction
        if(rc.isCoreReady()){ // rc.canMove(facing) is checked!
            recordTimeAndPath();
            rc.move(facing);
            
        }
    }
    
    
    /**
     * Move toward a destination with right direction preference. 
     * @param dest
     * @return
     */
    public Direction getMoveDirWithRightPreferenceToward(MapLocation dest){
        Direction toDest = rc.getLocation().directionTo(dest);
        while (!rc.canMove(toDest)){
            toDest = toDest.rotateRight();
            
            //check that we are not facing off the edge of the map
            MapLocation tileInFront = rc.getLocation().add(toDest);
            if(rc.senseTerrainTile(tileInFront) == TerrainTile.OFF_MAP){
                exploredDeathLockPath = true;
            }

        }
        return toDest;
    }
    
    /**
     * Move toward a destination with left direction preference. 
     * @param dest
     * @return
     */
    public Direction getMoveDirWithLeftPreferenceToward(MapLocation dest){
        Direction toDest = rc.getLocation().directionTo(dest);
        while (!rc.canMove(toDest)){
            toDest = toDest.rotateLeft();
            //check that we are not facing off the edge of the map
            MapLocation tileInFront = rc.getLocation().add(toDest);
            if(rc.senseTerrainTile(tileInFront) == TerrainTile.OFF_MAP){
                exploredDeathLockPath = true;
            }
        }
        return toDest;
    }

}
