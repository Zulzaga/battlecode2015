package iveel;

import battlecode.common.*;

import java.util.*;

import iveel.structures.Tower;
import iveel.structures.AerospaceLab;
import iveel.structures.Barracks;
import iveel.structures.HQ;
import iveel.structures.HandwashStation;
import iveel.structures.Helipad;
import iveel.structures.MinerFactory;
import iveel.structures.SupplyDepot;
import iveel.structures.TankFactory;
import iveel.structures.TechnologyInstitute;
import iveel.structures.TrainingField;
import iveel.units.Basher;
import iveel.units.Beaver;
import iveel.units.Commander;
import iveel.units.Computer;
import iveel.units.Drone;
import iveel.units.Launcher;
import iveel.units.Miner;
import iveel.units.Soldier;
import iveel.units.Tank;

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