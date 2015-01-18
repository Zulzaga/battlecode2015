package team105.units;

import team105.Unit;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class Missile extends Unit{
	
	public Missile(RobotController rc){
        super(rc);
    }
	
	public void execute() throws GameActionException{
		if(rc.isCoreReady() && rc.canMove(Direction.NORTH))
				rc.move(Direction.NORTH);

		rc.yield();
	}
}
