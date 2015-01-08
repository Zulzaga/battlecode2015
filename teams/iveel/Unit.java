package iveel;

import java.util.Random;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;

public abstract class Unit extends BaseBot{

    private Direction facing;

    public Unit(RobotController rc) {
        super(rc);
        facing = getRandomDirection();
        rand = new Random(rc.getID());
    }

    public  Direction getRandomDirection() {
        return Direction.values()[(int)(rand.nextDouble()*8)];
    }

    public  void mineAndMove() throws GameActionException {
        if(rc.senseOre(rc.getLocation())>1){//there is ore, so try to mine
            if(rc.isCoreReady()&&rc.canMine()){
                rc.mine();
            }
        }else{//no ore, so look for ore
            moveAround();
        }
    }

    public  void moveAround() throws GameActionException {
        if(rand.nextDouble()<0.05){
            if(rand.nextDouble()<0.5){
                facing = facing.rotateLeft();
            }else{
                facing = facing.rotateRight();
            }
        }
        MapLocation tileInFront = rc.getLocation().add(facing);

        //check that the direction in front is not a tile that can be attacked by the enemy towers
        MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
        boolean tileInFrontSafe = true;
        for(MapLocation m: enemyTowers){
            if(m.distanceSquaredTo(tileInFront)<=RobotType.TOWER.attackRadiusSquared){
                tileInFrontSafe = false;
                break;
            }
        }

        //check that we are not facing off the edge of the map
        if(rc.senseTerrainTile(tileInFront)!=TerrainTile.NORMAL||!tileInFrontSafe){
            facing = facing.rotateLeft();
        }else{
            //try to move in the facing direction
            if(rc.isCoreReady()&&rc.canMove(facing)){
                rc.move(facing);
            }
        }
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
    
    public void buildUnit(RobotType type) throws GameActionException {
        if(rc.getTeamOre()>type.oreCost){
            Direction buildDir = getRandomDirection();
            if(rc.isCoreReady()&&rc.canBuild(buildDir, type)){
                rc.build(buildDir, type);
            }
        }
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
