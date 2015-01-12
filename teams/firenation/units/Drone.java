package firenation.units;

import firenation.units.Drone.RobotHealthComparator;

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
import firenation.Unit;

public class Drone extends Unit {

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
        harassStrategy(theirHQ);
    }
    
    public void harassStrategy(MapLocation ml) throws GameActionException{
    	harassToLocation(ml);  	
    	
        transferSupplies();
        rc.yield();
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

        //@Override
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