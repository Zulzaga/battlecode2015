package firenation;
import battlecode.common.*;


public class RobotPlayer {
	
	// run the robot
	public static void run(RobotController rc){
		// robot will not end working, endless loop
		while(true){
			
			try {
				if(rc.getType() == RobotType.HQ){
					if(rc.isCoreReady() && rc.canSpawn(Direction.NORTH, RobotType.BEAVER)){
						rc.spawn(Direction.NORTH, RobotType.BEAVER);
					}
				}
				else if(rc.getType() == RobotType.BEAVER){
					if(rc.isCoreReady() && rc.canMove(Direction.NORTH)){
						rc.move(Direction.NORTH);
					}
				}
			} catch (GameActionException e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
			}
				
			rc.yield();
		}
		
	}
}
