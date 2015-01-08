package hugo;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public abstract class BaseBot {

    protected RobotController rc;
    protected MapLocation myHQ, theirHQ;
    protected Team myTeam, theirTeam;

    public BaseBot(RobotController rc) {
        this.rc = rc;
        this.myHQ = rc.senseHQLocation();
        this.theirHQ = rc.senseEnemyHQLocation();
        this.myTeam = rc.getTeam();
        this.theirTeam = this.myTeam.opponent();
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

    public  void execute() throws GameActionException {
        rc.yield();
    }

}
