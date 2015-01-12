package hugo;

import java.util.Random;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public abstract class BaseBot {

    protected RobotController rc;
    protected MapLocation myHQ, theirHQ;
    protected Team myTeam, theirTeam;
    protected Random rand;

    public BaseBot(RobotController rc) {
        this.rc = rc;
        this.myHQ = rc.senseHQLocation();
        this.theirHQ = rc.senseEnemyHQLocation();
        this.myTeam = rc.getTeam();
        this.theirTeam = this.myTeam.opponent();
        this.rand = new Random(rc.getID());
    }

    public  Direction[] getDirectionsToward(MapLocation dest) {
        Direction toDest = rc.getLocation().directionTo(dest);
        Direction[] dirs = {toDest,
                toDest.rotateLeft(), toDest.rotateRight(),
                toDest.rotateLeft().rotateLeft(), toDest.rotateRight().rotateRight()};

        return dirs;
    }

    public  RobotInfo[] getAllies() {
        RobotInfo[] allies = rc.senseNearbyRobots(Integer.MAX_VALUE, myTeam);
        return allies;
    }

    public  RobotInfo[] getEnemiesInAttackingRange() {
        RobotInfo[] enemies = rc.senseNearbyRobots(RobotType.SOLDIER.attackRadiusSquared, theirTeam);
        return enemies;
    }

    public  void attackLeastHealthEnemy(RobotInfo[] enemies) throws GameActionException {
        if (enemies.length == 0) {
            return;
        }

        double minEnergon = Double.MAX_VALUE;
        MapLocation toAttack = null;
        for (RobotInfo info : enemies) {
            if (info.health < minEnergon) {
                toAttack = info.location;
                minEnergon = info.health;
            }
        }

        rc.attackLocation(toAttack);
    }

    protected void attackEnemyZero() throws GameActionException {
        RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(rc.getLocation(),
                rc.getType().attackRadiusSquared, rc.getTeam().opponent());
        if (nearbyEnemies.length > 0) {// there are enemies nearby
            // try to shoot at them
            // specifically, try to shoot at enemy specified by nearbyEnemies[0]
            if (rc.isWeaponReady()
                    && rc.canAttackLocation(nearbyEnemies[0].location)) {
                rc.attackLocation(nearbyEnemies[0].location);
            }
        }
    }
    
    public void transferSupplies() throws GameActionException {
        RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(),
                GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED, rc.getTeam());
        double lowestSupply = rc.getSupplyLevel();
        double transferAmount = 0;
        MapLocation suppliesToThisLocation = null;
        for (RobotInfo ri : nearbyAllies) {
            if (ri.supplyLevel < lowestSupply) {
                lowestSupply = ri.supplyLevel;
                transferAmount = (rc.getSupplyLevel() - ri.supplyLevel) / 2;
                suppliesToThisLocation = ri.location;
            }
        }
        if (suppliesToThisLocation != null) {
            rc.transferSupplies((int) transferAmount, suppliesToThisLocation);
        }
    }

    public  void beginningOfTurn() {
        if (rc.senseEnemyHQLocation() != null) {
            theirHQ = rc.senseEnemyHQLocation();
        }
    }

    public  void endOfTurn() {
    }

    public  void go() throws GameActionException {
        beginningOfTurn();
        execute();
        endOfTurn();
    }
    
    protected Direction getRandomDirection() {
        // System.out.println("heereeeee" +
        // Direction.values()[(int)(rand.nextDouble()*8)]);
        return Direction.values()[(int) (rand.nextDouble() * 8)];
    }

    public  void execute() throws GameActionException {
        rc.yield();
    }
    
    
    
    
    // if the location is not in range of Towers and HQ
    public boolean safeToMove(MapLocation ml) {
        return safeFromTowers(ml) && safeFromHQ(ml);
        	
    }
    
    // if the location is not in range of Towers
    public boolean safeFromTowers(MapLocation ml){
    	MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
        boolean tileInFrontSafe = true;
        for (MapLocation m : enemyTowers) {
            if (m.distanceSquaredTo(ml) <= RobotType.TOWER.attackRadiusSquared) {
                tileInFrontSafe = false;
                break;
            }
        }
        return tileInFrontSafe;
    }
    
    // if the location is not in range of their HQ
    public boolean safeFromHQ(MapLocation location){
    	return location.distanceSquaredTo(theirHQ) > RobotType.HQ.attackRadiusSquared;
    }

}
