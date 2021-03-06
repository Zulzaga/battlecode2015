package kairat.units;

import kairat.Unit;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

/*
 * Spawned at HQ
 * Only unit that can build structures
 * One of two units that can mine ore [0.2, 3].
 * Has a weak attack at short range
 * Frequently hosed
 * 
 * 
 * 
 * 
 */
public class Beaver extends Unit {

    public Beaver(RobotController rc) throws GameActionException {
        super(rc);
        
        
        //Initialize channelID and increment total number of this RobotType
        channelStartWith = Channel_Beaver;
        initChannelNum(); 
    }

    public void execute() throws GameActionException {
        basicDistributedConstruction();
        transferSupplies();
        playWithArmyUnit();
        rc.yield();
    }


    /**
     * Build structures depending on time turn if there is enough ore.
     * Otherwise, mine or move.
     * 
     * @throws GameActionException
     */
    public void basicDistributedConstruction() throws GameActionException{

        //      * 7. Barrack   4; 200-700          -125
        //      * 8. Miner factory 3; 0-300        -100
        //      * 11. Tank factory 4; 700-1200     -120  

        //      * 9. HandwashStation 2; 1000-1300  - 150

        //      * 10. Helipad 2; 500-1000          - 250
        //      * 12. Aerospace lab 2; 1000-1700   -350

        int turn = Clock.getRoundNum();

        //try build structures in particular time steps if there is enough amount of ore.
        if (turn % 20 == 0){
            if (turn <= 300 && rc.readBroadcast(8) < 4 ){
                buildUnit(RobotType.MINERFACTORY);
            }else if( turn >= 200 && turn <700 && rc.readBroadcast(Channel_Barracks)<5 ){
                buildUnit(RobotType.BARRACKS);
            }else if ( turn >=700  && turn <= 1200 && rc.readBroadcast(Channel_Tank) < 4){
                buildUnit(RobotType.TANKFACTORY);
            }
            
        }else if (turn % 33 == 0){
            if ( turn >=1000  && turn <= 1300 && rc.readBroadcast(Channel_HandwashStation) < 3){
                buildUnit(RobotType.HANDWASHSTATION);
                
        }else if (turn % 49 == 0){
            if (turn <= 500 && turn <1000 && rc.readBroadcast(Channel_Helipad) < 3 ){
                buildUnit(RobotType.HELIPAD);
            }else if( turn >= 1000 && turn <1700 && rc.readBroadcast(Channel_AerospaceLab) < 3 ){
                buildUnit(RobotType.AEROSPACELAB);
            }
        }
        }
        
        //if building nothing.
        if (Math.random() < 0.3){
            mineAndMove();
        }else{
            moveAroundAlways();
        }
        
        
//        swarmPot() ;
        
        


    }


    /**
     * Transfer supply, player 6 example
     * @throws GameActionException
     */
    public void player6() throws GameActionException{
        attackEnemyZero();
        if(Clock.getRoundNum()<700){
            buildUnit(RobotType.MINERFACTORY);
        }else{
            buildUnit(RobotType.BARRACKS);
        }
        mineAndMove(); 
        transferSupplies();

    }


}
