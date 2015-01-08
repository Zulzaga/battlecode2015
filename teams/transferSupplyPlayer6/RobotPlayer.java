package transferSupplyPlayer6;

import java.util.Random;

import battlecode.common.*;

public class RobotPlayer{

    static Direction facing;
    static Random rand;
    static RobotController rc;
    static  MapLocation myHQ;
    static  MapLocation theirHQ;
    static Team myTeam;
    static Team theirTeam;

    public static void run(RobotController myrc){
        rc = myrc;
        rand = new Random(rc.getID());
        facing = getRandomDirection();//randomize starting direction

        myHQ = rc.senseHQLocation();
        theirHQ = rc.senseEnemyHQLocation();
        myTeam = rc.getTeam();
        theirTeam = myTeam.opponent();

        while(true){
            try {
                if(rc.getType()==RobotType.HQ){
                    attackEnemyZero();
                    spawnUnit(RobotType.BEAVER);
                }else if(rc.getType()==RobotType.BEAVER){
                    attackEnemyZero();
                    if(Clock.getRoundNum()<700){
                        buildUnit(RobotType.MINERFACTORY);
                    }else{
                        buildUnit(RobotType.BARRACKS);
                    }
                    mineAndMove();
                }else if(rc.getType()==RobotType.MINER){
                    attackEnemyZero();
                    mineAndMove();
                }else if(rc.getType()==RobotType.MINERFACTORY){
                    spawnUnit(RobotType.MINER);
                }else if(rc.getType()==RobotType.BARRACKS){
                    spawnUnit(RobotType.SOLDIER);
                }else if(rc.getType()==RobotType.TOWER){
                    attackEnemyZero();
                }else if(rc.getType()==RobotType.SOLDIER){
                    attackEnemyZero();
                    moveAround();
                }
                transferSupplies();

            } catch (GameActionException e) {

                e.printStackTrace();
            }

            rc.yield();
        }

    }

    private static void transferSupplies() throws GameActionException {
        RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(),GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED,rc.getTeam());
        double lowestSupply = rc.getSupplyLevel();
        double transferAmount = 0;
        MapLocation suppliesToThisLocation = null;
        for(RobotInfo ri:nearbyAllies){
            if(ri.supplyLevel<lowestSupply){
                lowestSupply = ri.supplyLevel;
                transferAmount = (rc.getSupplyLevel()-ri.supplyLevel)/2;
                suppliesToThisLocation = ri.location;
            }
        }
        if(suppliesToThisLocation!=null){
            rc.transferSupplies((int)transferAmount, suppliesToThisLocation);
        }
    }

    private static void buildUnit(RobotType type) throws GameActionException {
        if(rc.getTeamOre()>type.oreCost){
            Direction buildDir = getRandomDirection();
            if(rc.isCoreReady()&&rc.canBuild(buildDir, type)){
                rc.build(buildDir, type);
            }
        }
    }

    private static void attackEnemyZero() throws GameActionException {
        RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(rc.getLocation(),rc.getType().attackRadiusSquared,rc.getTeam().opponent());
        if(nearbyEnemies.length>0){//there are enemies nearby
            //try to shoot at them
            //specifically, try to shoot at enemy specified by nearbyEnemies[0]
            if(rc.isWeaponReady()&&rc.canAttackLocation(nearbyEnemies[0].location)){
                rc.attackLocation(nearbyEnemies[0].location);
            }
        }
    }

    private static void spawnUnit(RobotType type) throws GameActionException {
        Direction randomDir = getRandomDirection();
        if(rc.isCoreReady()&&rc.canSpawn(randomDir, type)){
            rc.spawn(randomDir, type);
        }
    }

    private static Direction getRandomDirection() {
        return Direction.values()[(int)(rand.nextDouble()*8)];
    }

    private static void mineAndMove() throws GameActionException {
        if(rc.senseOre(rc.getLocation())>1){//there is ore, so try to mine
            if(rc.isCoreReady()&&rc.canMine()){
                rc.mine();
            }
        }else{//no ore, so look for ore
            moveAround();
        }
    }

    private static void moveAround() throws GameActionException {
        if(rand.nextDouble()<0.05){
            if(rand.nextDouble()<0.5){
                facing = facing.rotateLeft();
            }else{
                facing = facing.rotateRight();
            }
        }
        MapLocation tileInFront = rc.getLocation().add(facing);

        //check that the direction in front is not a tile that can be attacked by the enemy towers
        MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
        boolean tileInFrontSafe = true;
        for(MapLocation m: enemyTowers){
            if(m.distanceSquaredTo(tileInFront)<=RobotType.TOWER.attackRadiusSquared){
                tileInFrontSafe = false;
                break;
            }
        }

        //check that we are not facing off the edge of the map
        if(rc.senseTerrainTile(tileInFront)!=TerrainTile.NORMAL||!tileInFrontSafe){
            facing = facing.rotateLeft();
        }else{
            //try to move in the facing direction
            if(rc.isCoreReady()&&rc.canMove(facing)){
                rc.move(facing);
            }
        }
    }

    public void executeHQ() throws GameActionException {
        int numBeavers = rc.readBroadcast(2);

        if (rc.isCoreReady() && rc.getTeamOre() > 100 && numBeavers < 10) {
            Direction newDir = getSpawnDirection(RobotType.BEAVER);
            if (newDir != null) {
                rc.spawn(newDir, RobotType.BEAVER);
                rc.broadcast(2, numBeavers + 1);
            }
        }
        MapLocation rallyPoint;
        if (Clock.getRoundNum() < 600) {
            rallyPoint = new MapLocation( (this.myHQ.x + this.theirHQ.x) / 2,
                    (this.myHQ.y + this.theirHQ.y) / 2);
        }
        else {
            rallyPoint = this.theirHQ;
        }
        rc.broadcast(0, rallyPoint.x);
        rc.broadcast(1, rallyPoint.y);

        rc.yield();
    }

    public Direction[] getDirectionsToward(MapLocation dest) {
        Direction toDest = rc.getLocation().directionTo(dest);
        Direction[] dirs = {toDest,
                toDest.rotateLeft(), toDest.rotateRight(),
                toDest.rotateLeft().rotateLeft(), toDest.rotateRight().rotateRight()};

        return dirs;
    }

    public Direction getMoveDir(MapLocation dest) {
        Direction[] dirs = getDirectionsToward(dest);
        for (Direction d : dirs) {
            if (rc.canMove(d)) {
                return d;
            }
        }
        return null;
    }

    public Direction getSpawnDirection(RobotType type) {
        Direction[] dirs = getDirectionsToward(this.theirHQ);
        for (Direction d : dirs) {
            if (rc.canSpawn(d, type)) {
                return d;
            }
        }
        return null;
    }

    public Direction getBuildDirection(RobotType type) {
        Direction[] dirs = getDirectionsToward(this.theirHQ);
        for (Direction d : dirs) {
            if (rc.canBuild(d, type)) {
                return d;
            }
        }
        return null;
    }

    public RobotInfo[] getAllies() {
        RobotInfo[] allies = rc.senseNearbyRobots(Integer.MAX_VALUE, myTeam);
        return allies;
    }

    public RobotInfo[] getEnemiesInAttackingRange() {
        RobotInfo[] enemies = rc.senseNearbyRobots(RobotType.SOLDIER.attackRadiusSquared, theirTeam);
        return enemies;
    }

    public void attackLeastHealthEnemy(RobotInfo[] enemies) throws GameActionException {
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



}