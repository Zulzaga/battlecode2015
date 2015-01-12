package kairat;

import kairat.structures.AerospaceLab;
import kairat.structures.Barracks;
import kairat.structures.HQ;
import kairat.structures.HandwashStation;
import kairat.structures.Helipad;
import kairat.structures.MinerFactory;
import kairat.structures.SupplyDepot;
import kairat.structures.TankFactory;
import kairat.structures.TechnologyInstitute;
import kairat.structures.Tower;
import kairat.structures.TrainingField;
import kairat.units.Basher;
import kairat.units.Beaver;
import kairat.units.Commander;
import kairat.units.Computer;
import kairat.units.Drone;
import kairat.units.Launcher;
import kairat.units.Miner;
import kairat.units.Soldier;
import kairat.units.Tank;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

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
