package hugo;

import battlecode.common.*;

import java.util.*;

import hugo.structures.AerospaceLab;
import hugo.structures.Barracks;
import hugo.structures.HQ;
import hugo.structures.HandwashStation;
import hugo.structures.Helipad;
import hugo.structures.MinerFactory;
import hugo.structures.SupplyDepot;
import hugo.structures.TankFactory;
import hugo.structures.TechnologyInstitute;
import hugo.structures.Tower;
import hugo.structures.TrainingField;
import hugo.units.Basher;
import hugo.units.Beaver;
import hugo.units.Commander;
import hugo.units.Computer;
import hugo.units.Drone;
import hugo.units.Launcher;
import hugo.units.Miner;
import hugo.units.Soldier;
import hugo.units.Tank;

public class RobotPlayer {
    public static void run(RobotController rc) {
        RobotType myType = rc.getType();
        BaseBot myself = null;

        switch (myType){
        //Structures (refer to all robots that cannot move) -11
        case HQ: myself = new HQ(rc);
        break;
        case TOWER: myself = new Tower(rc);
        break;
        case SUPPLYDEPOT: myself = new SupplyDepot(rc) ;
        break;
        case TECHNOLOGYINSTITUTE: myself = new TechnologyInstitute(rc) ;
        break;
        case BARRACKS: myself = new Barracks(rc) ;
        break;
        case HANDWASHSTATION: myself = new HandwashStation(rc);
        break;
        case TRAININGFIELD: myself = new TrainingField(rc);
        break;
        case TANKFACTORY: myself = new TankFactory(rc);
        break;
        case AEROSPACELAB: myself = new AerospaceLab(rc) ;
        break;
        case MINERFACTORY: myself = new MinerFactory(rc) ;
        break;
        case HELIPAD: myself = new Helipad(rc);
        break;
        
        //Units (refer to all robots that can move) - 9
        case BEAVER: myself = new Beaver(rc) ;
        break;
        case COMPUTER: myself = new  Computer(rc) ;
        break;
        case SOLDIER: myself = new Soldier(rc);
        break;
        case BASHER: myself = new Basher(rc) ;
        break;
        case DRONE: myself = new  Drone(rc);
        break;
        case MINER: myself = new Miner(rc) ;
        break;
        case COMMANDER: myself = new Commander(rc)  ;
        break;
        case TANK: myself = new Tank(rc)  ;
        break;
        case LAUNCHER: myself = new Launcher(rc);
        break;
        }

        while (true) {
            try {
                myself.go();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}