package team105.units;

import team105.Unit;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

/*
 * Spawned at Aerospace Lab
 * Very high HP
 * No attack
 * Moves slowly
 * Generates a MISSILE every 6 turns and can store up to 6
 * Super cool
 * 
 */
public class Launcher extends Unit {

    public Launcher(RobotController rc) throws GameActionException {
        super(rc);

        //Initialize channelID and increment total number of this RobotType
        channelStartWith = Channel_Launcher;
        initChannelNum(); 
    }
    
    public void execute() throws GameActionException {
    	hugoStragtegyLauncher();
    	rc.yield();
    }

	private void hugoStragtegyLauncher() {
		launchMissile(Direction.NORTH);		
	}
    
    private void launchMissile(Direction dir){
    	try {
    		if(rc.isWeaponReady() && rc.canLaunch(dir))
    			rc.launchMissile(dir);;
		} catch (GameActionException e) {
			e.printStackTrace();
		}
    }

}