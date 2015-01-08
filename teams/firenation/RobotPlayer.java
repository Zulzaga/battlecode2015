package firenation;

import battlecode.common.*;

import java.util.*;

import firenation.structures.AerospaceLab;
import firenation.structures.Barracks;
import firenation.structures.HQ;
import firenation.structures.HandwashStation;
import firenation.structures.Helipad;
import firenation.structures.MinerFactory;
import firenation.structures.SupplyDepot;
import firenation.structures.TankFactory;
import firenation.structures.TechnologyInstitute;
import firenation.structures.Tower;
import firenation.structures.TrainingField;
import firenation.units.Basher;
import firenation.units.Beaver;
import firenation.units.Commander;
import firenation.units.Computer;
import firenation.units.Drone;
import firenation.units.Launcher;
import firenation.units.Miner;
import firenation.units.Soldier;
import firenation.units.Tank;

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