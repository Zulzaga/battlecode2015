package iveel.units;

import iveel.Unit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class Drone extends Unit {
    /*
     * Drone's channel:
     * We want to spread our drone through the map.
     * Channel_Drone + 1:
     * 
     * 
     * 
     */


    //Few drones would be used for exploring the map. Essential variables for those. 
    private boolean explorer = false;
    // last dest in this list is next dest
    private List<MapLocation> explorerDestinations = new ArrayList<MapLocation>(); 
    private MapLocation currentTargetDest = null;
    
    
    public Drone(RobotController rc) throws GameActionException {
        super(rc);
        
        
        //Initialize channelID and increment total number of this RobotType
        channelStartWith = Channel_Drone;
        initChannelNum(); 
    }
    
    
    /**
     * Initialize channelNum AA BBB 
     * 
     * Increment total number of this robot type.
     * @throws GameActionException
     */
    public void initChannelNum() throws GameActionException{
        int maxDistBetweenDrones = 5;
        int spawnedOrder = rc.readBroadcast(channelStartWith) + 1;
        
        //first three drones are going to explore map.
        if(spawnedOrder >3){
            rc.broadcast(channelStartWith, spawnedOrder);
            channelID = channelStartWith + spawnedOrder*10;
            
        }else if(spawnedOrder ==1 ){
            //ourHQ - > theirHQ
            explorer = true;
            Direction toEnemy = myHQ.directionTo(theirHQ);
            MapLocation currentTargetDest = myHQ.add(toEnemy, 5);
            MapLocation endPoint0 = theirHQ.add(toEnemy.opposite(), 5);
            
            explorerDestinations.add(endPoint0 );

        }else if(spawnedOrder ==2 ){
            explorer = true;
            Direction toEnemy = myHQ.directionTo(theirHQ);
            Direction toLeft = toEnemy.rotateLeft();
            MapLocation currentTargetDest = myHQ.add(toLeft, 5);
            MapLocation endPoint1 = theirHQ.add(toLeft.opposite(), 5);
            
            explorerDestinations.add(endPoint1);
            
        }else if(spawnedOrder ==3 ){
            explorer = true;
            Direction toEnemy = myHQ.directionTo(theirHQ);
            Direction toRight = toEnemy.rotateRight();
            MapLocation currentTargetDest = myHQ.add(toRight, 5);
            MapLocation endPoint2 = theirHQ.add(toRight.opposite(), 5);
            
            explorerDestinations.add(endPoint2);

        }
    }
    
    /**
     * Should be called only on explorer drones!
     * If this drone has reached its final destination, it should become 
     * @throws GameActionException 
     */
    public void explore() throws GameActionException{
            //check of it has reached its destination
            if  (rc.getLocation().equals( currentTargetDest)){
                // no more destinations;
                int numDest = explorerDestinations.size();
                if (numDest == 0){
                    explorer = false;
                    
                }else{
                    currentTargetDest = explorerDestinations.remove(numDest-1);
                }
            }
            //move this drone
            System.out.println("toTarget");
            harassToLocation(currentTargetDest);  
            System.out.println("toTarget");

            
            
        }
        

    public void execute() throws GameActionException {
        if (explorer){
            explore();
        }else{
            swarmPot();
        }
    }

    public void player6() throws GameActionException {
        attackTower();
        moveAround();
    }

    public void swarmPot() throws GameActionException {
        RobotInfo[] enemies = getEnemiesInAttackingRange();

        if (enemies.length > 0) {
            // attack!
            if (rc.isWeaponReady()) {
                attackLeastHealthEnemy(enemies);
            }
        } else if (rc.isCoreReady()) {
            int rallyX = rc.readBroadcast(0);
            int rallyY = rc.readBroadcast(1);
            MapLocation rallyPoint = new MapLocation(rallyX, rallyY);

            Direction newDir = getMoveDir(rallyPoint);

            if (newDir != null) {
                rc.move(newDir);
            }
        }
    }

    /**
     * Attack towers if it sees towers, otherwise attack enemy with lowest
     * health
     * 
     * @throws GameActionException
     */
    private void attackTower() throws GameActionException {
        RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(rc.getLocation(),
                rc.getType().attackRadiusSquared, rc.getTeam().opponent());

        int numberOfEnemies = nearbyEnemies.length;
        if (numberOfEnemies > 0) {
            MapLocation attackBuildingLocation = null;
            for (RobotInfo enemy : nearbyEnemies) {
                if (enemy.type == RobotType.TOWER) {
                    attackBuildingLocation = enemy.location;
                }
            }

            if (attackBuildingLocation != null) {
                if (rc.isWeaponReady()
                        && rc.canAttackLocation(attackBuildingLocation)) {
                    rc.attackLocation(attackBuildingLocation);
                }
            } else {
                Arrays.sort(nearbyEnemies, new RobotHealthComparator());
                if (rc.isWeaponReady()
                        && rc.canAttackLocation(nearbyEnemies[numberOfEnemies - 1].location)) {
                    rc.attackLocation(nearbyEnemies[numberOfEnemies - 1].location);
                }
            }
        }
    }

    /**
     * Comparator for the hit points of health of two different robots
     * (Ascending order)
     */
    static class RobotHealthComparator implements Comparator<RobotInfo> {

        @Override
        public int compare(RobotInfo o1, RobotInfo o2) {
            if (o1.health > o2.health) {
                return 1;
            } else if (o1.health < o2.health) {
                return -1;
            } else {
                return 0;
            }
        }
    }
    

    // if the location is not in range of Towers and HQ
    public boolean safeToMove2(MapLocation ml) {
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
    
 // move to location
    public void moveToLocation(MapLocation location) throws GameActionException {
        if(rc.isCoreReady()){
            Direction dirs[] = getDirectionsToward(location);
            
            for(Direction newDir : dirs){
                if (rc.canMove(newDir)) {
                    if(!safeToMove2(rc.getLocation().add(newDir))){
                        continue;
                    }
                    else if(rc.canMove(newDir)){
                        rc.move(newDir);
                        return;
                    }
                }
            }
        }
    }
    
    // run to the opposite direction of the robot
    public void avoid(RobotInfo robot) throws GameActionException{
        if(rc.isCoreReady()){
            Direction oppositeDir = getMoveDir(rc.getLocation().add(rc.getLocation().directionTo(robot.location).opposite()));
            
            if(oppositeDir != null){
                Direction dirs[] = getDirectionsToward(rc.getLocation().add(oppositeDir));
                
                for(Direction newDir : dirs){
                    if (newDir != null) {
                        if(!safeToMove2(rc.getLocation().add(newDir))){
                            continue;
                        }
                        else if(rc.canMove(newDir)){
                            rc.move(newDir);
                            break ;
                        }
                    }
                }
            }
        }
    }
    
    // attack enemy
    public void attack() throws GameActionException{
        if(rc.isWeaponReady()){
            RobotInfo[] enemies = getEnemiesInAttackingRange();
            
            if(enemies.length > 0) {
                attackLeastHealthEnemy(enemies);
            }
        }
    }
    
 // harass and move to the location
    public void harassToLocation(MapLocation ml) throws GameActionException{
        RobotInfo nearestEnemy = senseNearestEnemy(rc.getType());
        
        if(nearestEnemy != null){
            int distanceToEnemy = rc.getLocation().distanceSquaredTo(nearestEnemy.location);
            if(distanceToEnemy <= rc.getType().attackRadiusSquared){
                attack();
                //attackRobot(nearestEnemy.location);
                avoid(nearestEnemy);
            }
            else{
                if(nearestEnemy.type != RobotType.TANK && nearestEnemy.type != RobotType.DRONE){
                    moveToLocation(nearestEnemy.location);
                    attack();
                    //attackRobot(nearestEnemy.location);
                }
                else{
                    avoid(nearestEnemy);
                    attack();
                    //attackRobot(nearestEnemy.location);
                }
            }
        }
        else{
            moveToLocation(ml);
            attack();
            //attackRobot(nearestEnemy.location);
        }
        
    }
    
    
    
    // return the nearest enemy robot
    public RobotInfo senseNearestEnemy(RobotType type){
        RobotInfo[] enemies = senseNearbyEnemies(type);
        
        if(enemies.length > 0){
            RobotInfo nearestRobot = null;
            int nearestDistance = Integer.MAX_VALUE;
            for(RobotInfo robot : enemies){
                int distance =  rc.getLocation().distanceSquaredTo(robot.location);
                if(distance < nearestDistance){
                    nearestDistance = distance;
                    nearestRobot = robot;
                }
            }
            return nearestRobot;
        }       
        return null;
    }
    
    // return all the sensible enemies
    public RobotInfo[] senseNearbyEnemies(RobotType type){
        return rc.senseNearbyRobots(type.sensorRadiusSquared, theirTeam);
    }
    

    public  RobotInfo[] getEnemiesInAttackingRange() {
        RobotInfo[] enemies = rc.senseNearbyRobots(RobotType.DRONE.attackRadiusSquared, theirTeam);
        return enemies;
    }


}
