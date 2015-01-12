package transferSupplyPlayer6;

import iveel.units.Miner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.TerrainTile;

public class PathExplorer extends Miner {
    private MapLocation enemyHQ;
    private int enemyHQx;
    private int enemyHQy;
    private Boolean exploredDeathLockPath = false;
    private ArrayList exploredPath = new ArrayList<MapLocation>();
    private int recordedLastTimeStamp;
    private Boolean rightHandRuled; // ruled directions could be only left or
                                    // right

    public PathExplorer(RobotController rc, boolean rightHandRuled)
            throws GameActionException {
        super(rc);
        this.rightHandRuled = rightHandRuled;
        recordTimeAndPath();
        enemyHQ = rc.senseEnemyHQLocation();
        enemyHQx = enemyHQ.x;
        enemyHQy = enemyHQ.y;
    }

    public void execute() throws GameActionException {
        transferSupplies();
        rc.yield();
    }

    public void recordTimeAndPath() {
        recordedLastTimeStamp = Clock.getRoundNum();
        exploredPath.add(rc.getLocation());
    }

    public void moveAndExplore(MapLocation dest) throws GameActionException {
        if (rightHandRuled) {
            facing = getMoveDirWithRightPreferenceToward(dest);
        } else {
            facing = getMoveDirWithLeftPreferenceToward(dest);
        }
        // try to move in the facing direction
        if (rc.isCoreReady()) { // rc.canMove(facing) is checked!
            recordTimeAndPath();
            rc.move(facing);

        }
    }

    /**
     * Move toward a destination with right direction preference.
     * 
     * @param dest
     * @return
     */
    public Direction getMoveDirWithRightPreferenceToward(MapLocation dest) {
        Direction toDest = rc.getLocation().directionTo(dest);
        while (!rc.canMove(toDest)) {
            toDest = toDest.rotateRight();

            // check that we are not facing off the edge of the map
            MapLocation tileInFront = rc.getLocation().add(toDest);
            if (rc.senseTerrainTile(tileInFront) == TerrainTile.OFF_MAP) {
                exploredDeathLockPath = true;
            }

        }
        return toDest;
    }

    /**
     * Move toward a destination with left direction preference.
     * 
     * @param dest
     * @return
     */
    public Direction getMoveDirWithLeftPreferenceToward(MapLocation dest) {
        Direction toDest = rc.getLocation().directionTo(dest);
        while (!rc.canMove(toDest)) {
            toDest = toDest.rotateLeft();
            // check that we are not facing off the edge of the map
            MapLocation tileInFront = rc.getLocation().add(toDest);
            if (rc.senseTerrainTile(tileInFront) == TerrainTile.OFF_MAP) {
                exploredDeathLockPath = true;
            }
        }
        return toDest;
    }

    private boolean goalTest(MapLocation loc) {
        int goalX = loc.x;
        int goalY = loc.y;
        if (((enemyHQx - 3) < goalX || (enemyHQx + 3) > goalX)
                && ((enemyHQy - 3) < goalY || (enemyHQy + 3) > goalY)) {
            return true;
        }
        return false;
    }

    private List<MapLocation> aStar(MapLocation startLoc) {
        if (goalTest(startLoc)) {
            return Arrays.asList(startLoc);
        } else {
            SearchNode startNode = new SearchNode(startLoc, null);
            PriorityQueue agenda = new PriorityQueue();
            agenda.push(startNode);
            Set<MapLocation> expanded = new HashSet<MapLocation>();
            while (!agenda.isEmpty()) {
                SearchNode parent = agenda.pop();
                if (!expanded.contains(parent.currentLocation)) {
                    expanded.add(parent.currentLocation);
                    if (goalTest(parent.currentLocation)) {
                        return parent.path();
                    }
                    for (MapLocation locationAround : successors(parent.currentLocation)) {
                        SearchNode child = new SearchNode(locationAround,
                                parent);
                        if (expanded.contains(locationAround)) {
                            continue;
                        } else {
                            agenda.push(child);
                        }
                    }
                }
            }
            return null;
        }
    }

    public class PriorityQueue {
        public List<SearchNode> data = new ArrayList<SearchNode>();

        public PriorityQueue() {

        }

        public void push(SearchNode node) {
            this.data.add(node);
        }

        public SearchNode pop() {
            SearchNode toPop = this.data.get(0);
            this.data.remove(0);
            return toPop;
        }

        public boolean isEmpty() {
            return (this.data.size() == 0);
        }
    }

    public class SearchNode {
        public MapLocation currentLocation;
        public SearchNode parentNode;

        public SearchNode(MapLocation currentLoc, SearchNode parentNode) {
            this.currentLocation = currentLoc;
            this.parentNode = parentNode;
        }

        public List<MapLocation> path() {
            List<MapLocation> pathToDestination = Arrays
                    .asList(this.currentLocation);
            if (this.parentNode == null) {
                return pathToDestination;
            } else {
                List<MapLocation> newList = new ArrayList<MapLocation>(
                        pathToDestination);
                newList.addAll(this.parentNode.path());
                return newList;
            }
        }
    }

    public List<MapLocation> successors(MapLocation loc) {
        List<MapLocation> locationsAround = new ArrayList<MapLocation>();
        int locX = loc.x;
        int locY = loc.y;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if ((i == 0) && (j == 0)) {
                    continue;
                } else {
                    MapLocation newLoc = new MapLocation((locX + i), (locY + j));
                    if (rc.senseTerrainTile(newLoc) == TerrainTile.NORMAL) {
                        locationsAround.add(newLoc);
                    }
                }
            }
        }
        return locationsAround;
    }

}
