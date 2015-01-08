package iveel.units;

import battlecode.common.*;
import iveel.Unit;

public class Beaver extends Unit {

    public Beaver(RobotController rc) {
        super(rc);
    }
    
    public void execute() throws GameActionException {
        swarmPot();
        transferSupplies();
        
        rc.yield();
    }
    
    
    /**
     * Transfer supply, player 6 example
     * @throws GameActionException
     */
    public void player6() throws GameActionException{
        attackEnemyZero();
        if(Clock.getRoundNum()<700){
            buildUnit(RobotType.MINERFACTORY);
        }else{
            buildUnit(RobotType.BARRACKS);
        }
        mineAndMove(); 
        transferSupplies();

    }
    
    
    /**
     * SwarmPot example
     * 
     * Gather until having more than 500 ore.
     * Then move toward enemyHQ
     * @throws GameActionException
     */
    public void swarmPot() throws GameActionException{
        if (rc.isCoreReady()) {
            if (rc.getTeamOre() < 500) {
                //mine
                if (rc.senseOre(rc.getLocation()) > 0) {
                    rc.mine();
                }
                else {
                    Direction newDir = getMoveDir(this.theirHQ);

                    if (newDir != null) {
                        rc.move(newDir);
                    }
                }
            }
            else {
                //build barracks
                Direction newDir = getBuildDirection(RobotType.BARRACKS);
                if (newDir != null) {
                    rc.build(newDir, RobotType.BARRACKS);
                }
            }
        }
    }

}
