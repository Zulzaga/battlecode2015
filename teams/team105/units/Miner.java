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
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;

/*
 * Spawned at Miner Factory
 * Has a weak attack at short range
 * One of two units that can mine ore [0.2, 3].
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
    }


    //tend to move ore rich area
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
        if (rc.senseTerrainTile(tileInFront) != TerrainTile.NORMAL
                || !tileInFrontSafe) {
           
        } else {
            // try to move in the facing direction
            if (rc.isCoreReady() && rc.canMove(facing)) {
                rc.move(facing);
//                System.out.println("moving!");
            }
        }
//        System.out.println("end turn " + Clock.getRoundNum());

    }

    public void findOreArea() throws GameActionException{
        if (Clock.getRoundNum() > 200){
            double maxOre = 0;
            double ore1 = rc.readBroadcast(Channel_OreAmount1 ) ;
            double ore2 = rc.readBroadcast(Channel_OreAmount2 ) ;
            double ore3 = rc.readBroadcast(Channel_OreAmount3 ) ;

            ArrayList<Double> arrayList = new ArrayList<Double>(Arrays.asList(ore1, ore2,ore3));
            Collections.sort(arrayList); 
            double max = arrayList.get(arrayList.size() - 1);

            if (max == 0){
                //destination has not defined!
            }if (max == ore2){
                int coordX = rc.readBroadcast(Channel_OreAreaX2) ;
                int coordY = rc.readBroadcast(Channel_OreAreaY2);
                destination = new MapLocation(coordX , coordY);

            }else if (max == ore3){
                int coordX = rc.readBroadcast(Channel_OreAreaX3) ;
                int coordY = rc.readBroadcast(Channel_OreAreaY3);
                destination = new MapLocation(coordX , coordY);
            }
            //System.out.println( "DESTINATION for ore ----" + destination);
        }else if(Clock.getRoundNum() > 600){
            double maxOre = 0;
            double ore1 = rc.readBroadcast(Channel_OreAmount1 ) ;
            double ore2 = rc.readBroadcast(Channel_OreAmount2 ) ;
            double ore3 = rc.readBroadcast(Channel_OreAmount3 ) ;

            ArrayList<Double> arrayList = new ArrayList<Double>(Arrays.asList(ore1,ore2,ore3));
            Collections.sort(arrayList); 
            double max = arrayList.get(arrayList.size() - 1);

            if (max == 0){
                //destination has not defined!
            }else if (max == ore1){
                int coordX = rc.readBroadcast(Channel_OreAreaX1) ;
                int coordY = rc.readBroadcast(Channel_OreAreaY1);
                destination = new MapLocation(coordX , coordY);
            }else if (max == ore2){
                int coordX = rc.readBroadcast(Channel_OreAreaX2) ;
                int coordY = rc.readBroadcast(Channel_OreAreaY2);
                destination = new MapLocation(coordX , coordY);

            }else if (max == ore3){
                int coordX = rc.readBroadcast(Channel_OreAreaX3) ;
                int coordY = rc.readBroadcast(Channel_OreAreaY3);
                destination = new MapLocation(coordX , coordY);

            }
        }   
    }
    
    public void mineAndMove() throws GameActionException {
        double sensedOre = rc.senseOre(rc.getLocation());
        if (sensedOre > 1) {// there is ore, so try to mine
            if (rc.isCoreReady() && rc.canMine()) {
                rc.mine();
                recordMineAmount(sensedOre);
            }
        } else {// no ore, so look for ore
            moveAroundLookingOre();
        }
    }

    public void execute() throws GameActionException {
        attackEnemyZero();
        mineAndMove();
    }





}