package team105.structures;

import team105.Structure;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class Helipad extends Structure {

    //We would have only one helipad!
    private int numDrones =0;
    public Helipad(RobotController rc) throws GameActionException {
        super(rc);
    }

    public void execute() throws GameActionException {

//        if (numDrones < 3){
//
//            try {
//                int roundNum = Clock.getRoundNum();
//                if(rc.isCoreReady() && rc.readBroadcast(Channel_Drone) < 5) {
//                    Direction spawnDir = getSpawnDirection(RobotType.DRONE);
//                    if (spawnDir != null) {
//                        numDrones +=1;
//                        rc.spawn(spawnDir, RobotType.DRONE);
//                    }
//                }
//            } catch (GameActionException e) {
//                e.printStackTrace();
//            }
//        }

        rc.yield();
    }

}
