package team105;

import battlecode.common.*;

import java.util.*;

import team105.BaseBot;
import team105.structures.AerospaceLab;
import team105.structures.Barracks;
import team105.structures.HQ;
import team105.structures.HandwashStation;
import team105.structures.Helipad;
import team105.structures.MinerFactory;
import team105.structures.SupplyDepot;
import team105.structures.TankFactory;
import team105.structures.TechnologyInstitute;
import team105.structures.Tower;
import team105.structures.TrainingField;
import team105.units.Basher;
import team105.units.Beaver;
import team105.units.Commander;
import team105.units.Computer;
import team105.units.Drone;
import team105.units.Launcher;
import team105.units.Miner;
import team105.units.Soldier;
import team105.units.Tank;

public class RobotPlayer {
    public static void run(RobotController rc) throws GameActionException {
        RobotType myType = rc.getType();
        BaseBot myself = null;

        switch (myType) {
        // Structures (refer to all robots that cannot move) -11
        case HQ:
            myself = new HQ(rc);
            break;
        case TOWER:
            myself = new Tower(rc);
            break;
        case SUPPLYDEPOT:
            myself = new SupplyDepot(rc);
            break;
        case TECHNOLOGYINSTITUTE:
            myself = new TechnologyInstitute(rc);
            break;
        case BARRACKS:
            myself = new Barracks(rc);
            break;
        case HANDWASHSTATION:
            myself = new HandwashStation(rc);
            break;
        case TRAININGFIELD:
            myself = new TrainingField(rc);
            break;
        case TANKFACTORY:
            myself = new TankFactory(rc);
            break;
        case AEROSPACELAB:
            myself = new AerospaceLab(rc);
            break;
        case MINERFACTORY:
            myself = new MinerFactory(rc);
            break;
        case HELIPAD:
            myself = new Helipad(rc);
            break;

        // Units (refer to all robots that can move) - 9
        case BEAVER:
            myself = new Beaver(rc);
            break;
        case COMPUTER:
            myself = new Computer(rc);
            break;
        case SOLDIER:
            myself = new Soldier(rc);
            break;
        case BASHER:
            myself = new Basher(rc);
            break;
        case DRONE:
            myself = new Drone(rc);
            break;
        case MINER:
            myself = new Miner(rc);
            break;
        case COMMANDER:
            myself = new Commander(rc);
            break;
        case TANK:
            myself = new Tank(rc);
            break;
        case LAUNCHER:
            myself = new Launcher(rc);
            break;
        }

        while (true) {
            try {
                myself.go();
                rc.yield();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}