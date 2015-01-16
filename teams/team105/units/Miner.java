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
            findOreArea();
    }
    
    public void findOreArea() throws GameActionException{
        if (Clock.getRoundNum() > 200){
        double maxOre = 0;
//        double ore1 = rc.readBroadcast(Channel_OreAmount1 ) ;
        double ore2 = rc.readBroadcast(Channel_OreAmount2 ) ;
        double ore3 = rc.readBroadcast(Channel_OreAmount3 ) ;
        double ore4 = rc.readBroadcast(Channel_OreAmount4 ) ;
        double ore5 = rc.readBroadcast(Channel_OreAmount5 ) ;
        ArrayList<Double> arrayList = new ArrayList<Double>(Arrays.asList(ore2,ore3,ore4,ore5));
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
            
        }else if (max == ore4){
            int coordX = rc.readBroadcast(Channel_OreAreaX4) ;
            int coordY = rc.readBroadcast(Channel_OreAreaY4);
            destination = new MapLocation(coordX , coordY);
            
        }else if (max == ore5){
            int coordX = rc.readBroadcast(Channel_OreAreaX5) ;
            int coordY = rc.readBroadcast(Channel_OreAreaY5);
            destination = new MapLocation(coordX , coordY);
        }
        //System.out.println( "DESTINATION for ore ----" + destination);
    }else if(Clock.getRoundNum() > 600){
        double maxOre = 0;
      double ore1 = rc.readBroadcast(Channel_OreAmount1 ) ;
      double ore2 = rc.readBroadcast(Channel_OreAmount2 ) ;
      double ore3 = rc.readBroadcast(Channel_OreAmount3 ) ;
      double ore4 = rc.readBroadcast(Channel_OreAmount4 ) ;
      double ore5 = rc.readBroadcast(Channel_OreAmount5 ) ;
      ArrayList<Double> arrayList = new ArrayList<Double>(Arrays.asList(ore1,ore2,ore3,ore4,ore5));
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
          
      }else if (max == ore4){
          int coordX = rc.readBroadcast(Channel_OreAreaX4) ;
          int coordY = rc.readBroadcast(Channel_OreAreaY4);
          destination = new MapLocation(coordX , coordY);
          
      }else if (max == ore5){
          int coordX = rc.readBroadcast(Channel_OreAreaX5) ;
          int coordY = rc.readBroadcast(Channel_OreAreaY5);
          destination = new MapLocation(coordX , coordY);
      }
            // System.out.println( "DESTINATION for ore ----" + destination);
    }
        
    }
    
    public double getLastMinedAmount(){
        double sumOre = 0; 
        for (int i = 0; i<  miningRecord.size(); i++){
            sumOre = sumOre +  miningRecord.get(i);
        }
        return sumOre;
    }
    
    public boolean notMoving(){
        if (lastSteps.size() < 6 ){
            return false;
        }else{
            double maxDist = 0;
            for (int i = 0; i<  lastSteps.size() ; i++){
                for (int j = 0; j<  lastSteps.size() ; j++){
                    if (i != j){
                        double dist = lastSteps.get(i).distanceSquaredTo(lastSteps.get(j));
                        if ( dist > maxDist){
                            maxDist = dist;
                        }
                    }
                }
            }
            System.out.println("maxDist  ----- " + maxDist);
            if (maxDist < 2){
                return true;
            }
            return false;

        }
    }

    public void improveOreDest() throws GameActionException{
        if (Clock.getRoundNum() > 1100){
            destination = null; 
            return;
        }else if ( getLastMinedAmount() < 5 && miningRecord.size() ==10 ){
//            findOreArea();
            destination = null;
        }
        
//        else if (notMoving()){
//            if ( getLastMinedAmount() < 10){
//                destination = null;
//            }
//        }
        
        
    }
    
    public void considerMovement(){
        lastSteps.add(rc.getLocation());
        if (lastSteps.size() > 6){
            lastSteps.remove(0);
        }
//        
//        lastNumMine.add(rc.getLocation());
//        if (lastNumMine.size() > 6){
//            lastNumMine.remove(0);
//        }
//        
//        lastNumMine 
    }
    

    
    public void execute() throws GameActionException {
//        considerMovement();
        improveOreDest();
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
    
    public void toDestWithMiningOre(MapLocation dest){
        MapLocation currentState = rc.getLocation();
        Direction toDest = currentState.directionTo(dest);
        //Directions toward toDest
    }
    
 
    

}