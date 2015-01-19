package team105.units;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import team105.Unit;
import team105.units.Drone.RobotHealthComparator;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;

public class Drone extends Unit {

    //Few drones would be used for exploring the map. Essential variables for those. 
    //destination  null if it is not an explorer drone!

    //Path exploring variables
    public int mode = 0; //0= exploring, 1= reached its initial destination going to HQ, 2= transfering supply
    public boolean searchAlongY = true;
    public int searchCoord;
    public boolean exploredDeadLock = false;
    public boolean freePath = false; //if it is true, it guarantees that there is a way to reach its destination.
    public int pathTo;
    public String path; //just for debugging!
    public MapLocation oreAreaPoint = null;
    public double maxOreArea = 0;
    public int channel_maxOreX; //channelID +1
    public int channel_maxOreY; //channelID +2
    public int channel_maxOreAmount; //channelID +3
    public Direction[] allDirs = new Direction[]{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH,
            Direction.NORTH_EAST, Direction.NORTH_WEST, Direction.SOUTH_EAST, Direction.SOUTH_WEST};

    public HashSet<MapLocation> reachableSpots = new HashSet<MapLocation>();
    public ArrayList<MapLocation> recentPathRecord = new ArrayList<MapLocation>();

    public Drone(RobotController rc) throws GameActionException {
        super(rc);

        // Initialize channelID and increment total number of this RobotType
        channelStartWith = Channel_Drone;
        initChannelNum(); 
    }

    /**
     * Initialize channelNum AA BBB
     * 
     * Increment total number of this robot type.
     * 
     * @throws GameActionException
     */
    public void initChannelNum() throws GameActionException{
        int spawnedOrder = rc.readBroadcast(channelStartWith) + 1;
        rc.broadcast(channelStartWith, spawnedOrder);
        channelID = channelStartWith + spawnedOrder*10;
        channel_maxOreX = channelID + 1;
        channel_maxOreY = channelID + 2;
        channel_maxOreAmount = channelID + 3;

        pathTo = spawnedOrder %3; //would be used for broadcasting! BE CAREFUL

        if( pathTo ==1 || spawnedOrder >3 ){
            //ourHQ - > theirHQ
            destination = centerOfMap;
            path = "centerOfMap";
        }else if( pathTo ==2 ){
            destination = endCorner2;
            path = "endCorner";
        }else if( pathTo ==0 ){
            destination = endCorner1;
            path = "endCorner";
        }
    }


    /**
     * Should be called only on explorer drones! If this drone has reached its
     * final destination, it should become non explorer ??
     * 
     * @throws GameActionException
     */



    private void broadcastExporation(int status) throws GameActionException {
        int type = (channelID /10) %5;
        if( type ==1  ){
            //ourHQ - > theirHQ
            //            destination = theirHQ;
            rc.broadcast(Channel_PathCenter, status);
        }else if( type ==2 ){
            //            destination = endCorner2;
            rc.broadcast(Channel_PathCorner2, status);
        }else if( type ==3 ){
            //            destination = endCorner1;
            rc.broadcast(Channel_PathCorner1, status);
        }else if( type ==4 ){
            //            destination = middle1;
            rc.broadcast(Channel_PathMiddle1, status);
        }else if( type == 0){
            //            destination = middle2;
            rc.broadcast(Channel_PathMiddle2, status);


        }
    }

    public void execute() throws GameActionException {
        
        int roundNum = Clock.getRoundNum();
//        if (roundNum < 700 && destination != null){
            goAroungWithSpecialMode(destination);
//        }else{
//            goAroundTransferingSupply();
//        }
        rc.yield();
    }

    //    public void hugoPlan(){
    //        try{
    //            int roundNum = Clock.getRoundNum();
    //            if(roundNum < 1700){
    //                if(roundNum < 400) // start exploring
    //                    explore();
    //                else if(roundNum < 1300){
    //                    if((roundNum / 40) % 5 == 0){
    //                        // retreat
    //                        MapLocation loc = rc.getLocation();
    //                        harassToLocation(loc.add(loc.directionTo(theirHQ).opposite()));
    //                    }
    //                    else { // advance
    //                        explore();
    //                    }
    //                }
    //                else  // advance
    //                    explore();
    //            }
    //                
    //            else{ // after round 1800
    //                startAttackingTowersAndHQ();
    //            }
    //        }
    //        catch(GameActionException e){
    //            e.printStackTrace();
    //        }
    //        rc.yield();
    //    }

    public void goAroungWithSpecialMode(MapLocation ml) throws GameActionException{
        RobotInfo nearestEnemy = senseNearestEnemy(rc.getType()) ;
        if (nearestEnemy != null) {
            int distanceToEnemy = rc.getLocation().distanceSquaredTo(
                    nearestEnemy.location);
            if (distanceToEnemy <= rc.getType().attackRadiusSquared) {
                attack();
                // attackRobot(nearestEnemy.location);
                avoid(nearestEnemy);
            } else {

                if (nearestEnemy.type == RobotType.DRONE) {
                    if (shouldStand) {
                        shouldStand = false; // waited once
                    } else {
                        moveToLocation(nearestEnemy.location);
                        shouldStand = true;
                    }
                } else {
                    avoid(nearestEnemy);
                }
            }
        } else {
            if (mode == 0){
                //expanding map range and exploring path.
                moveToLocationExtandingRange(destination);
            }else if(mode ==1){
                //going to theirHQ, exploring map too.
                moveToLocation(destination);
            }else{
                //transfering supply
            }
            //avoiding enemies
        }

    }
    
    
    
    public boolean locked(MapLocation ml){
        recentPathRecord.add(ml);
        int repetition = 0;
        if ( recentPathRecord.size() > 8){
            recentPathRecord.remove(0);
        }
        for (int i =0; i< recentPathRecord.size(); i++){
            if (recentPathRecord.get(i).equals(ml)){
                repetition +=1;
            }
        }
        if (repetition > 2){return true;}
        return false;
    }
    
    public int numNormalsdAround(MapLocation ml){
        int numNormals = 0;
        
        TerrainTile loc = rc.senseTerrainTile(ml);
        if (loc.equals(TerrainTile.NORMAL)){
            numNormals +=1;
        }
        for (Direction dir: allDirs){
            loc = rc.senseTerrainTile(ml.add(dir));
            if (loc.equals(TerrainTile.NORMAL)){
                numNormals +=1;
            }
            
        }
        return numNormals;

    }
    
    // move to location (Safe!)
    public void moveToLocation(MapLocation location) throws GameActionException {
        if (rc.isCoreReady()) {
            Direction dirs[] = getDirectionsToward(location);

            for (Direction newDir : dirs) {
                if (rc.canMove(newDir)) {
                    if (!safeToMove2(rc.getLocation().add(newDir))
                            || !safeFromShortShooters(rc.getLocation().add(
                                    newDir))) {
                        continue;
                    } else if (rc.canMove(newDir)) {
                        if( !locked(rc.getLocation().add(newDir))){
                            rc.move(newDir);
                        }else{
                            mode = 2;
                        }
                        return;
                    }
                }
            }
        }
    }
    
    

    // move to location /for drone/ avoiding enemy MODE 0
    public void moveToLocationExtandingRange(MapLocation location) throws GameActionException {
        if (rc.isCoreReady()) {
            //Directions where normal exist
            MapLocation currentLoc = rc.getLocation();
            Direction[] dirs;
            Direction towardDest = currentLoc.directionTo(location);
            
            MapLocation rightLoc = currentLoc.add(towardDest.rotateRight().rotateRight(), 2);
            MapLocation leftLoc = currentLoc.add(towardDest.rotateLeft().rotateLeft(), 2);
            MapLocation forward = currentLoc.add(towardDest, 2);
            
     
                int leftNormals = numNormalsdAround(leftLoc);
                int rightNormals = numNormalsdAround( rightLoc);
                int forwardNormals = numNormalsdAround(forward);
                int maxNormals = Math.max(Math.max(leftNormals, rightNormals), forwardNormals);
                
                if (currentLoc.distanceSquaredTo(destination) < 3 || maxNormals == 0){
                    System.out.println("to their HQ");
                    destination = destination.add(toEnemy, 10);
                    mode = 1;
                    dirs = getDirectionsToward(destination);
                }else if (forwardNormals == maxNormals){
                    dirs = getDirectionsToward(destination);
                }else if ( leftNormals == maxNormals){
                    dirs = getDirectionsToward(leftLoc);
                }else{
                    dirs = getDirectionsToward(rightLoc);
                }
                
          
            
            for (Direction newDir : dirs) {
                if (rc.canMove(newDir)) {
                    if (!safeToMove2(rc.getLocation().add(newDir))
                            || !safeFromShortShooters(rc.getLocation().add(
                                    newDir))) {
//                        System.out.println("NOT  MOVING!!!");
                        continue;
                    } else if (rc.canMove(newDir)) {
                        if( !locked(currentLoc.add(newDir))){
                            rc.move(newDir);
                        }else{
                            destination = destination.add(toEnemy, 10);
                            mode = 1;
                        }
                        return;
                    }
                }
            }
            
            
            
//            System.out.println("no way to move");
        }
    }


    // return the nearest enemy robot within radius
    public RobotInfo senseNearestEnemyWithin(int radiusSquared) {
        RobotInfo[] enemies = rc.senseNearbyRobots( radiusSquared, theirTeam);

        if (enemies.length > 0) {
            RobotInfo nearestRobot = null;
            int nearestDistance = Integer.MAX_VALUE;
            for (RobotInfo robot : enemies) {
                int distance = rc.getLocation().distanceSquaredTo(
                        robot.location);
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestRobot = robot;
                }
            }

            if (nearestDistance > radiusSquared){
                return null;
            }
            return nearestRobot;
        }
        return null;
    }

    public void goAroundTransferingSupply(){

    }

    public void startAttackingTowersAndHQ() throws GameActionException{
        MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();

        MapLocation nearestAttackableTowerSafeFromHQ = nearestAttackableTowerSafeFromHQ(enemyTowers);

        if(nearestAttackableTowerSafeFromHQ != null){
            attackTower();
            moveToLocationSafeFromHQ(nearestAttackableTowerSafeFromHQ);
        }
        else if(enemyTowers.length > 0){
            attackTower();
            moveToLocationNotSafe(enemyTowers[0]);
        }
        else{
            attackTower();
            moveToLocationNotSafe(theirHQ);
        }
    }

    public MapLocation nearestAttackableTowerSafeFromHQ(MapLocation[] enemyTowers){
        MapLocation towerLocation = null;
        int distance = Integer.MAX_VALUE;
        MapLocation droneLocation = rc.getLocation();

        for(MapLocation location : enemyTowers){
            int tempDistance = droneLocation.distanceSquaredTo(location);
            if(tempDistance < distance && safelyAttackableFromHQ(location)){
                distance = tempDistance;
                towerLocation = location;
            }
        }

        return towerLocation;
    }

    public boolean safelyAttackableFromHQ(MapLocation location){
        return location.distanceSquaredTo(theirHQ) > 1;
    }

    public void harassStrategy(MapLocation ml) throws GameActionException {
        harassToLocation(ml);
    }

    public void player6() throws GameActionException {
        attackTower();
        moveAround();
    }

    public void swarmPot() throws GameActionException {
        //        attackLeastHealthEnemy();

        if (rc.isCoreReady()) {
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
     * Check if it has found greater ore area than it had found before.
     * If so , broadcast it. (since that spot is reachable).
     * @throws GameActionException
     */
    public void checkOreArea() throws GameActionException{ 
        double tempMax = 0;
        Integer coordX = null;
        Integer coordY = null;
        for (MapLocation loc: reachableSpots){
            double sensedOre = rc.senseOre(loc);
            if (sensedOre > tempMax){
                tempMax = sensedOre;
                coordX = loc.x;
                coordY = loc.y;
            }
        }

        if (maxOreArea < tempMax){
            maxOreArea = tempMax;
            rc.broadcast(channel_maxOreAmount, (int) Math.ceil(maxOreArea));
            rc.broadcast(channel_maxOreX, coordX);
            rc.broadcast(channel_maxOreY, coordY);
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

        // @Override
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




}