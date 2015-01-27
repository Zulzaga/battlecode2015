package team105.units;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import team105.Unit;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;

/*
 * Spawned at Miner Factory
 * Has a weak attack at short range
 * One of two units that can mine ore [0.2, 3].
 */
public class Miner extends Unit {
    
    private MapLocation comesFrom = myHQ;


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
    }


    //tend to move ore rich area (preferring toward direction)
    private void moveAroundLookingOre() throws GameActionException {
        double maxOre = 0;
        Direction[] directions = new Direction[]{facing.rotateLeft().rotateLeft(), facing.rotateRight().rotateRight(), facing.rotateLeft(), facing.rotateRight(), facing};
        Direction toGo = null;
        for (Direction dir: directions){
            MapLocation loc = rc.getLocation().add(dir);
            if ( !rc.isLocationOccupied(loc) && rc.senseTerrainTile(loc) == TerrainTile.NORMAL ){
                double amount = rc.senseOre(loc) + rc.senseOre(loc.add(dir));
                if ( maxOre <=  amount){
                    toGo = dir;
                    maxOre = amount;
                }
                
            }
        }
        
        if ( toGo == null){
            Direction[] directions1 = new Direction[]{ facing.opposite(), facing.opposite().rotateLeft(), facing.opposite().rotateRight().rotateRight()};
            for (Direction dir: directions1){
                MapLocation loc = rc.getLocation().add(dir);
                if ( !rc.isLocationOccupied(loc) && rc.senseTerrainTile(loc) == TerrainTile.NORMAL){
                    double amount = rc.senseOre(loc) + rc.senseOre(loc.add(dir));
                    if ( maxOre <= amount){
                        toGo = dir;
                        maxOre = amount;
                    }
                }
            }
        }

        if (toGo != null){
            facing = toGo;
        }
        
        

        MapLocation tileInFront = rc.getLocation().add(facing);
        MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
        boolean tileInFrontSafe = true;
        for (MapLocation m : enemyTowers) {
            if (m.distanceSquaredTo(tileInFront) <= RobotType.TOWER.attackRadiusSquared) {
                tileInFrontSafe = false;
                break;
            }
        }

        // check that we are not facing off the edge of the map
        if (tileInFrontSafe) {
            // try to move in the facing direction
            if (rc.isCoreReady() && rc.canMove(facing)) {
                comesFrom = rc.getLocation();
                rc.move(facing);
            }
        }else{
            facing = getRandomDirection();
        }

    }
    
    public void mineAndMove() throws GameActionException {
        double sensedOre = rc.senseOre(rc.getLocation());
        if (sensedOre > 1) {// there is ore, so try to mine
            if (rc.isCoreReady() && rc.canMine()) {
                if (rc.senseNearbyRobots(comesFrom, 1, myTeam).length <= 2){
                    rc.mine(); //leave for next robot
                }else{
                    moveAroundLookingOre();
                }
            }
        } else {// no ore, so look for ore
            moveAroundLookingOre();
        }
    }

    public void execute() throws GameActionException {
        RobotInfo nearestEnemy = senseNearestEnemy(rc.getType());

        if (nearestEnemy != null) {
            int distanceToEnemy = rc.getLocation().distanceSquaredTo(
                    nearestEnemy.location);
            if (distanceToEnemy <= rc.getType().attackRadiusSquared) {
                attack();
                // attackRobot(nearestEnemy.location);
                avoid(nearestEnemy);
                return;
            } else {
                if (nearestEnemy.type == RobotType.MINER) {
                    if (shouldStand) {
                        shouldStand = false; // waited once
                    } else {
                        moveToLocation(nearestEnemy.location);
                        shouldStand = true;
                    }
                } else {
                    avoid(nearestEnemy);
                }
            }
        } else {
            mineAndMove();
        }
     
    }





}