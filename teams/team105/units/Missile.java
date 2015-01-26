package team105.units;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.Team;

public class Missile {

    private RobotController rc;
    private Team myTeam;
    private Team theirTeam;

    public Missile(RobotController rc) {
        this.rc = rc;
        this.myTeam = rc.getTeam();
        this.theirTeam = this.myTeam.opponent();
        // super(rc);
    }

    public void execute() throws GameActionException {
        RobotInfo[] enemies = rc.senseNearbyRobots(110, theirTeam);
        RobotInfo[] friends = null;
        MapLocation attackLocation = null;
        if (enemies.length > 0) {
            attackLocation = enemies[0].location;
            friends = rc.senseNearbyRobots(attackLocation, 2, myTeam);
        }
        if (friends != null) {
            if (rc.getLocation() != attackLocation) {
                Direction d = rc.getLocation().directionTo(attackLocation);
                if (rc.isCoreReady() && rc.canMove(d)) {
                    rc.move(d);
                }
            } else {
                rc.explode();
            }
        }
        rc.yield();
    }
}
