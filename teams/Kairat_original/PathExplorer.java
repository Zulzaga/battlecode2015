package Kairat_original;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.TerrainTile;

public class PathExplorer {
    private MapLocation enemyHQ;
    private int enemyHQx;
    private int enemyHQy;
    private RobotController rc;

    public PathExplorer(RobotController rc) {
        this.rc = rc;
        enemyHQ = rc.senseEnemyHQLocation();
        enemyHQx = enemyHQ.x;
        enemyHQy = enemyHQ.y;
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

    public List<MapLocation> aStar(MapLocation startLoc) {
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
        public List<SearchNode> data;

        public PriorityQueue() {
            data = new ArrayList<SearchNode>();
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

    public static void main(String[] args) {

    }
}
