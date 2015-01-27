package team105.structures;

import team105.Structure;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class MinerFactory extends Structure {

    public MinerFactory(RobotController rc) throws GameActionException {
        super(rc);

    }

    public void execute() throws GameActionException {
        if (rc.readBroadcast(Channel_Miner) < 15) {
            spawnUnit(RobotType.MINER);
        }else if(rc.readBroadcast(Channel_Tank) > 2 ){
            spawnUnit(RobotType.MINER);
        }else if(rc.readBroadcast(Channel_Miner) < 30){
            spawnUnit(RobotType.MINER);
        }

    }

}
