package team105;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.TerrainTile;

/*BaseBot represents Unit and Structure.
 * General:
 * 
 * Starts with 500 ore, and each team automatically receives 5 ore per turn before any mining income
 * 
 * USE OF CHANNELS:
 * Can only use radio channels from 0 to 65535. 
 * Each robot has its own unique channelNum. 
 * 
 * ///////////   5digits used : DON'T USE 5digit channels!///////////
 *   
 *   
 * == Structures (except HQ) 
 *   AA BB C
 *   
 *   AA: type of unit.
 *   BB: spawned order number. 1st or 2nd. 
 *   C: up this stucture's management. 
 *   
 *   10 - Helipad
 *   11 - tower
 *   12 - SupplyDepot
 *   13 - TechnologyInstitute
 *   14 - Barracks
 *   15 - HandwashStation
 *   16 - TrainingField
 *   17 - TankFactory
 *   18 - AerospaceLab
 *   19 - MinerFactory
 *   
 * == Units:   
 *   A BBB C 
 *   
 *   A: type of unit
 *   BBB: spawned order number. 1st or 2nd 
 *   C: up to this unit's management.
 *   
 *   2 - Beaver
 *   3 - Soldier
 *   4 - Drone
 *   5 - Tank 
 *   6 - Miner // no more than 550
 *   
 * ////////   4 digits used: DON'T USE 4digit channels begin with 1,2, 7-9.///////////
 *   Rest of robots (few number) must be 4 digits.
 *   
 * == Drone, Launcher, Computer, Commander
 *   A BB C - must be 4digits.
 *   
 *   6 - Basher
 *   7 - Launcher
 *   8 - Computer
 *   9 - Commander
 *    
 *   For example: 1st drone's channel is 1 01_
 *   
 * == HQ:  //all important global info
 *   A BBB 
 *   A:  Always 1 (making it different from structures).
 *   BBB:  up this stucture's management. 
 *   
 * == Army:
 *  A BB C
 *  A: always 2
 *  BB: spawned order number (1st army, 2nd army)
 *  C: up to this army's management.
 *  
 *  
 * ======= Channel_PathDirect:  C
 *          Channel_PathMiddle1: D;
 *          Channel_PathMiddle2: B
 *          Channel_PathCorner1: E;
 *          Channel_PathCorner2: E;
 *  
 *  In our map, we have 5 essential target destinations which lay on diagonal. (Map could be flipped or rotated) 
 *  
 *          A ********** theirHQ
 *          *   B            *
 *          *        C       *
 *          *            D   *
 *          myHQ *********** E
 *          
 *  
 *  C: centerOfMap;
 *  A: endCorner2;
 *  E: endCorner1;
 *  D: middle1;
 *  B: middle2;
 *   
 *   After sending our drones and exploring the map, we would broadcast if these kind of paths are available.
 *    A, B, D or E: 
 *    0: have not explored yet.
 *    1: there is a path to that point.
 *    2: there may not path but there are not many voids.
 *    3: there may not path and there are many voids. (useless) 
 *     
 *     Since the map is symmetric if   myHQ --B  = theirHQ -- D and myHQ --A = their -- E.
 *  In the beginning, we check if there are paths from myHQ to A, B, C, D and E.
 *  if to B and to D exist, then there is a path from myHQ -> B (or D) -> theirHQ. (XX)
 *  if to A and to E exist, then there is a path from myHQ -> A (or E) -> theirHQ. (YY)
 *  if to C exist, then there is a path from myHQ -> theirHQ. (ZZ)
 *  
 *     So if AE = 11; then there is a path from myHQ -> B (or D). A good path to reach enemyHQ.
 *     31 -> E is better than A for miners since enemy has block to go there.
 *     13 -> A is better than E for miners.
 *     
 *     A good path to reach enemyHQ. ranking:
 *     11
 *     12
 *     21
 *     
 *     Good path for miners 
 *     1,3
 *     1,2
 *     2,3
 *     2,2
 *     
 *     
 *      
 *   
 *   
 *   
 *  
 */
public abstract class BaseBot {
    // Channels for keeping track of total number of each robots //
    public static int Channel_Helipad = 10000;
    public static int Channel_Tower = 11000;
    public static int Channel_SupplyDepot = 12000;
    public static int Channel_TechnologyInstitute = 13000;
    public static int Channel_Barracks = 14000;
    public static int Channel_HandwashStation = 15000;
    public static int Channel_TrainingField = 16000;
    public static int Channel_TankFactory = 17000;
    public static int Channel_AerospaceLab = 18000;
    public static int Channel_MinerFactory = 19000;

    public static int Channel_Beaver = 20000;
    public static int Channel_Soldier = 30000;
    public static int Channel_Miner = 60000;// no more than 550 miners
    public static int Channel_Tank = 50000;
    public static int Channel_Drone = 40000; 


    public static int Channel_Basher = 6000;
    public static int Channel_Launcher = 7000;
    public static int Channel_Computer = 8000;
    public static int Channel_Commander = 9000;
    public static int Channel_Army = 2000;

    // ////////Specific channels for stragies and mode of the game///////////////
    public static int Channel_Strategy = 1000;
    public static int Channel_ArmyMode = 1001;
    //explained above
    public static int Channel_PathCenter= 1002;
    public static int Channel_PathMiddle1= 1003;
    public static int Channel_PathMiddle2= 1004;
    public static int Channel_PathCorner1= 1005;
    public static int Channel_PathCorner2= 1006;
    
    ///// Channels for path which 3 drones may find///////
    public static int Channel_PathToCenter = 1100;
    public static int Channel_PathToCorner1 = 1200;
    public static int Channel_PathToCorner2 = 1300;

    //Reachable ore areas (first 5 explorer drones' channels)
    public static int Channel_OreAreaX1 = 40011; 
    public static int Channel_OreAreaY1 = 40012;
    public static int Channel_OreAmount1 = 40013;

    public static int Channel_OreAreaX2 = 40021;
    public static int Channel_OreAreaY2 = 40022;
    public static int Channel_OreAmount2 = 40023;

    public static int Channel_OreAreaX3 = 40031;
    public static int Channel_OreAreaY3 = 40032;
    public static int Channel_OreAmount3 = 40033;

    public static int Channel_OreAreaX4 = 40041;
    public static int Channel_OreAreaY4 = 40042;
    public static int Channel_OreAmount4 = 40043;

    public static int Channel_OreAreaX5 = 40051;
    public static int Channel_OreAreaY5 = 40052;
    public static int Channel_OreAmount5 = 40053;

    //Call drones for supple support
    public static int Channel_CalledOn1 = 40015; //amount, if its drone has not been called, then it must be 0;
    public static int Channel_CallSupplyX1 = 40016;
    public static int Channel_CallSupplyY1 = 40017;

    public static int Channel_CalledOn2 = 40025;
    public static int Channel_CallSupplyX2 = 40026;
    public static int Channel_CallSupplyY2 = 40027;

    public static int Channel_CalledOn3 = 40035;
    public static int Channel_CallSupplyX3 = 40036;
    public static int Channel_CallSupplyY3 = 40037;


    //90 degree relations between directions.
    protected HashMap<Direction, Direction[]> degree90 = new HashMap<Direction, Direction[]>();
    public Direction[] allDirs = new Direction[]{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH,
            Direction.NORTH_EAST, Direction.NORTH_WEST, Direction.SOUTH_EAST, Direction.SOUTH_WEST};


    // for channeling
    protected int channelID; // this channel would be used for this robot's
    // info; unique for each robot.
    protected int channelStartWith; // should be Channel_Beaver or ...
    protected ArrayList<MapLocation> criticalLocations = new ArrayList<MapLocation>();
    protected HashMap<MapLocation, MapLocation> criticalPath = new HashMap<MapLocation, MapLocation>();
    protected Direction criticalDirection;

    protected RobotController rc;
    protected MapLocation myHQ, theirHQ;
    protected Team myTeam, theirTeam;
    protected Random rand;
    private int matrixSize = 100;
    private int halfMatrixSize = 50;
    protected int supplyUpkeep;
    protected String[][] matrix = new String[matrixSize][matrixSize];

    public BaseBot(RobotController rc) {
        this.rc = rc;
        this.myHQ = rc.senseHQLocation();
        this.theirHQ = rc.senseEnemyHQLocation();
        this.myTeam = rc.getTeam();
        this.theirTeam = this.myTeam.opponent();
        this.rand = new Random(rc.getID());
        criticalDirection = myHQ.directionTo(theirHQ);
        emptyMatrix();


        for (Direction dir: allDirs){
            degree90.put(dir,  new Direction[]{dir.rotateLeft().rotateLeft(), dir.rotateRight().rotateRight()});
        }
    }

    public Boolean isNormalTerrain(MapLocation loc){
        TerrainTile t = rc.senseTerrainTile(loc);
        if ( t == TerrainTile.NORMAL){
            return true;
        }
        return false;
    }


    public void emptyMatrix(){
        for (int i = 0; i<matrixSize; i++){
            for (int j = 0; j<matrixSize; j++){
                matrix[i][j] = ".";
            }
        }
    }
    public void MatrixtoString(){
        String matrixString = "\n";
        for (int r = 0; r < matrixSize; r++){
            String line ="";
            for(int c = 0; c < matrixSize; c++){
                line += matrix[c][r];
            }
            line +="\n";
            matrixString += line;
        }
        System.out.println(matrixString);
    }

    public void markVoidMatrix(MapLocation loc){
        matrix[ loc.x - myHQ.x + halfMatrixSize][ loc.y  -myHQ.y  +halfMatrixSize] = "#";
    }

    public void markPathMatrix(MapLocation loc){
        matrix[ loc.x - myHQ.x + halfMatrixSize][loc.y  -myHQ.y  +halfMatrixSize ] = "*";
    }

    public void markNormalMartrix(MapLocation loc){
        matrix[ loc.x - myHQ.x + halfMatrixSize][loc.y  -myHQ.y  +halfMatrixSize] = "-";
    }

    public void markStartMatrix(MapLocation loc){
        matrix[ loc.x - myHQ.x + halfMatrixSize][loc.y  -myHQ.y  +halfMatrixSize ] = "S";
    }

    public void markDestMatrix(MapLocation loc){
        matrix[ loc.x - myHQ.x + halfMatrixSize][loc.y  -myHQ.y  + halfMatrixSize] = "D";
    }

    public void markSpecMartrix(MapLocation loc){
        matrix[ loc.x - myHQ.x + halfMatrixSize][loc.y  -myHQ.y  + halfMatrixSize ] = "=";
    }


    /**
     * Initialize channelNum AA BBB
     * 
     * Increment total number of this robot type.
     * 
     * @throws GameActionException
     */
    public void initChannelNum() throws GameActionException {
        int spawnedOrder = rc.readBroadcast(channelStartWith) + 1;
        rc.broadcast(channelStartWith, spawnedOrder);
        channelID = channelStartWith + spawnedOrder * 10;
    }

    /**
     * Create a new channel for an army. Number of army must be limited to 99.
     * 
     * @return
     * @throws GameActionException
     */
    public int newArmyGetChannelID() throws GameActionException {
        int spawnedOrder = rc.readBroadcast(Channel_Army) + 1;
        rc.broadcast(Channel_Army, spawnedOrder);
        return Channel_Army + spawnedOrder * 10;
    }

    /**
     * Find a list of directions toward destination.
     * 
     * @param dest
     * @return
     */
    public Direction[] getDirectionsToward(MapLocation dest) {
        Direction toDest = rc.getLocation().directionTo(dest);
        Direction[] dirs = { toDest, toDest.rotateLeft(), toDest.rotateRight(),
                toDest.rotateLeft().rotateLeft(),
                toDest.rotateRight().rotateRight() };

        return dirs;
    }

    public Direction[] getDirectionsTowardAndNext(MapLocation dest){
        Direction toDest = rc.getLocation().directionTo(dest);
        Direction[] dirs = { toDest, toDest.rotateLeft(), toDest.rotateRight(),
                toDest.rotateLeft().rotateLeft(),
                toDest.rotateRight().rotateRight(),
                toDest.rotateLeft().rotateLeft().rotateLeft(),
                toDest.rotateRight().rotateRight().rotateRight()};

        return dirs;
    }


    /**
     * Find a list of all PathUnits which are Normal TerrainTile, giving priority to given destination.
     * @param dest
     * @return
     */
    public ArrayList<MapLocation> getAllDirectionalLocations( MapLocation current) {
        Direction[] dirs = {Direction.EAST,  Direction.WEST, Direction.NORTH, Direction.SOUTH, 
                Direction.NORTH_WEST, Direction.SOUTH_EAST, Direction.SOUTH_WEST, Direction.NORTH_EAST
        };

        ArrayList<MapLocation> allNeighbors = new ArrayList<MapLocation>();
        for (Direction dir: dirs){
            TerrainTile t = rc.senseTerrainTile(current.add(dir));
            if ( t == TerrainTile.NORMAL){
                allNeighbors.add( current.add(dir));
                markNormalMartrix(current.add(dir));
            }else{
                markVoidMatrix(current.add(dir));
            }
        }
        return allNeighbors;
    }


    /**
     * Find list of MapLocations, representing a path from starting tile to end tile.
     * @param end
     * @return
     */
    public ArrayList<MapLocation> getPath(PathUnit end){
        ArrayList<MapLocation> path = new ArrayList<MapLocation>();
        path.add(end.getCurrentLoc());
        PathUnit preUnit = end.getPreviosLoc();
        while (preUnit != null ){
            path.add(preUnit.getCurrentLoc());
            preUnit = preUnit.getPreviosLoc();
        }
        return path;
    }

    //Using BFS, not visiting nodes which we already visited in our search algorithm.

    /**
     * Finds shortest Normal TerrainTile path to given dest, exploring tiles within in specific radius.
     * @param dest
     * @param searchWithinRadiusSquared
     * @return empty list if current location is dest
     *          null if there is no such path.
     */
    public ArrayList<MapLocation> findShortestPath(MapLocation dest, double searchWithinRadiusSquared){
        ArrayList<PathUnit> agenda = new ArrayList<PathUnit>();
        ArrayList<MapLocation> visited = new ArrayList<MapLocation>();   

        MapLocation currentLoc = rc.getLocation();
        Direction toDest = currentLoc.directionTo(dest);
        if (currentLoc.equals(dest)){
            new ArrayList<MapLocation>();
        }

        PathUnit startUnit = new PathUnit(null, currentLoc);
        for (MapLocation next: getAllDirectionalLocations( currentLoc) ){
            agenda.add(new PathUnit(startUnit, next));
        }

        while (agenda.size() >0 ){
            PathUnit pathLoc = agenda.remove(0);
            if (pathLoc.getCurrentLoc().equals(dest)){
                return getPath(pathLoc);
            }

            if (currentLoc.distanceSquaredTo(pathLoc.getCurrentLoc()) < searchWithinRadiusSquared){
                //Add locations never have been visited before.
                for (MapLocation nextMapLoc: getAllDirectionalLocations(pathLoc.getCurrentLoc())){
                    if (!visited.contains(nextMapLoc)){
                        agenda.add(new PathUnit(pathLoc, nextMapLoc));
                        visited.add(nextMapLoc);
                    }
                }
            }
        }
        //there is no path;
        return null;
    }


    /*
     * 
     * Find shortest path using our sensed range area.
     */
    public ArrayList<MapLocation> findShortestPathAstar(MapLocation dest,double searchWithinRadius ){
        //dest is not our sense range, it is likely we could not any path.
        //        if (rc.canSenseLocation(dest)){
        //            return null;
        //        }

        MapLocation start = rc.getLocation(); 
        markStartMatrix(start);
        markDestMatrix(dest);

        HashSet<MapLocation> closedSet = new HashSet<MapLocation>(); // The set of nodes already evaluated.
        HashSet<MapLocation> openSet = new HashSet<MapLocation>(); // The set of tentative nodes to be evaluated, initially containing the start node
        HashMap<MapLocation, Double> getHereCost = new HashMap<MapLocation, Double>();

        openSet.add(start);
        getHereCost.put(start, (double) 0);
        HashMap<MapLocation, MapLocation> cameFrom = new HashMap<MapLocation, MapLocation>();
        cameFrom.put(start, null); 

        HashMap<MapLocation, Double> estimatedCost = new HashMap<MapLocation, Double>();
        estimatedCost.put(dest, (double) Math.pow(dest.distanceSquaredTo(start), 0.5));

        while (!openSet.isEmpty()){
            MapLocation current = findLowestCostLocation(openSet, getHereCost, dest);
            markSpecMartrix(current);
            if ( current.equals(dest)){
                //                return constructPath( cameFrom, dest);
                return constructCriticalPathPoints( cameFrom, dest);
            }

            openSet.remove(current);
            closedSet.add(current);
            for (MapLocation neighbor: getAllDirectionalLocations(current)){
                if (closedSet.contains(neighbor)){
                    continue;
                }

                Double toHereScore = getHereCost.get(current) + Math.pow(current.distanceSquaredTo(neighbor), 0.5);
                if (toHereScore < searchWithinRadius){
                    if ( !openSet.contains(neighbor)) {
                        cameFrom.put(neighbor, current);
                        getHereCost.put(neighbor, toHereScore);
                        estimatedCost.put(neighbor, toHereScore + Math.pow(dest.distanceSquaredTo(neighbor), 0.5));
                        openSet.add(neighbor);
                    }else if( getHereCost.get(neighbor) > toHereScore){
                        cameFrom.put(neighbor, current);
                        getHereCost.put(neighbor, toHereScore);
                        estimatedCost.put(neighbor, toHereScore + Math.pow(dest.distanceSquaredTo(neighbor), 0.5));
                    } 
                }
            }
        }
        System.out.println("no path!");
        MatrixtoString();
        //        System.out.println("start: " + start.x + "  " +  start.y);
        //        System.out.println("dest: " + dest.x + "  " +  dest.y);

        return null;

    }

    public ArrayList<MapLocation> constructPath( HashMap<MapLocation, MapLocation> came_from, MapLocation goal){
        ArrayList<MapLocation> path = new ArrayList<MapLocation>();
        MapLocation start = goal;
        while( start != null){
            markPathMatrix(start);
            path.add(start);
            start = came_from.get(start);
        }
        MatrixtoString();
        return path;

    }

    public ArrayList<MapLocation> constructCriticalPathPoints( HashMap<MapLocation, MapLocation> came_from, MapLocation goal){
        ArrayList<MapLocation> path = new ArrayList<MapLocation>();

        MapLocation start = goal;
        markDestMatrix(start);
//        path.add(start);  //it is our HQ
        MapLocation end = null;
        Direction pre = null;
        Direction after = null;
        while( start != null){
            if (after != null){
                if (after == pre.rotateLeft().rotateLeft() || after == pre.rotateRight().rotateRight() ){
                    if (end != null){
                        path.add(end);
                        markDestMatrix(end);
                    }
                } 
            }

            after = pre;
            end = start;
            start = came_from.get(start);
            if (start != null){
                pre = start.directionTo(end);
            }
        }
        MatrixtoString();
        System.out.println("/n CRITICAL POINTS");
        return path;

    }



    public MapLocation findLowestCostLocation( HashSet<MapLocation> openSet, HashMap<MapLocation, Double> getHereCost, MapLocation dest){
        MapLocation minCostly = null;
        double minValue = Double.MAX_VALUE;
        for ( MapLocation node: openSet){
            double temp = getHereCost.get(node) + Math.pow(dest.distanceSquaredTo(node), 0.5);
            if ( minValue > temp){
                minValue = temp;
                minCostly = node;
            }
        }
        return minCostly;
    }





    public RobotInfo[] getAllies() {
        RobotInfo[] allies = rc.senseNearbyRobots(Integer.MAX_VALUE, myTeam);
        return allies;
    }

    public RobotInfo[] getEnemiesInAttackingRange() {
        RobotInfo[] enemies = rc.senseNearbyRobots(
                rc.getType().attackRadiusSquared, theirTeam);
        return enemies;
    }

    public void attackLeastHealthEnemy() throws GameActionException {
        RobotInfo[] enemies = getEnemiesInAttackingRange();
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

        if (rc.isWeaponReady() && rc.canAttackLocation(toAttack)) {
            rc.attackLocation(toAttack);
        }
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

    //This method should be written in sturctures and units.
    public void go() throws GameActionException {
    }


    //Drones should not use this!!!
    public void transferSupplies() throws GameActionException {
        //have enough supply to share
        if ( rc.getSupplyLevel() > this.supplyUpkeep*3){
            //structures always 0 then never calls drone.
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

            //need to call drones for supple
        }else {
            int aveSupply = (int) Math.ceil(aroundAverageSupply()) +1 ;  //should never broadcast 0
            if (aveSupply < 50 || rc.senseNearbyRobots(20, myTeam).length > 4 );
            if (rc.readBroadcast(Channel_CalledOn1) == 0){
                rc.broadcast(Channel_CalledOn1, aveSupply +1);
                rc.broadcast(Channel_CallSupplyX1, rc.getLocation().x);
                rc.broadcast(Channel_CallSupplyY1, rc.getLocation().y);
            }else if(rc.readBroadcast(Channel_CalledOn2) == 0){
                rc.broadcast(Channel_CalledOn2, aveSupply +1);
                rc.broadcast(Channel_CallSupplyX2, rc.getLocation().x);
                rc.broadcast(Channel_CallSupplyY2, rc.getLocation().y);
            }else if( rc.readBroadcast(Channel_CalledOn3) == 0){
                rc.broadcast(Channel_CalledOn3, aveSupply +1);
                rc.broadcast(Channel_CallSupplyX3, rc.getLocation().x);
                rc.broadcast(Channel_CallSupplyY3, rc.getLocation().y);
            }

        }
    }



    public double aroundAverageSupply(){
        RobotInfo[] mySide = rc.senseNearbyRobots(15, myTeam);
        double totalSupply = rc.getSupplyLevel();
        for(RobotInfo fellow: mySide){
            totalSupply = fellow.supplyLevel;
        }
        return totalSupply/mySide.length+1; 
    }

    protected Direction getRandomDirection() {
        return Direction.values()[(int) (rand.nextDouble() * 8)];
    }

    public static void main(String[] args) {

    }




}
