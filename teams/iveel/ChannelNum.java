package iveel;

import iveel.structures.AerospaceLab;
import iveel.structures.Barracks;
import iveel.structures.HQ;
import iveel.structures.HandwashStation;
import iveel.structures.Helipad;
import iveel.structures.MinerFactory;
import iveel.structures.SupplyDepot;
import iveel.structures.TankFactory;
import iveel.structures.TechnologyInstitute;
import iveel.structures.Tower;
import iveel.structures.TrainingField;
import iveel.units.Basher;
import iveel.units.Beaver;
import iveel.units.Commander;
import iveel.units.Computer;
import iveel.units.Drone;
import iveel.units.Miner;
import iveel.units.Soldier;
import iveel.units.Tank;

public enum ChannelNum {
    
    
/*  11 - tower
 *   12 - SupplyDepot
 *   13 - TechnologyInstitute
 *   14 - Barracks
 *   15 - HandwashStation
 *   16 - TrainingField
 *   17 - TankFactory
 *   18 - AerospaceLab
 *   19 - MinerFactory
 *   20 - Helipad
 *   
 *   
 * == Units:   
 *   31 - Beaver
 *   32 - Soldier
 *   33 - Computer
 *   34 - Basher
 *   35 - Drone
 *   
 *   36 - Miner
 *   37 - Commander
 *   38 - Tank
 *   39 - Launcher
     */
    
// HQ
// TOWER
//
// SUPPLYDEPOT:
//
// TECHNOLOGYINSTITUTE:
//
// BARRACKS:
//
// HANDWASHSTATION:
//
// TRAININGFIELD:
//
// TANKFACTORY:
//
// AEROSPACELAB:
//
// MINERFACTORY:
//
// HELIPAD:
//
//
//// Units (refer to all robots that can move) - 9
// BEAVER:
//
// COMPUTER:
//
// SOLDIER:
//
// BASHER:
//
// DRONE:
//
// MINER:
//
// COMMANDER:
//
// TANK:
//
// LAUNCHER:

    TOP("top"),
    BOTTOM("bottom"),
    LEFT("left"),
    RIGHT("right");

    public final String location;

    /**
     * location can be chosen only from "top", "bottom", "left", "right"
     * @param location represents location of the walltype
     */
    ChannelNum(String location) {
        this.location = location;
    }

}
