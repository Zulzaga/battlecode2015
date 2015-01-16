package team105;

import battlecode.common.MapLocation;

public class PathUnit {
    private MapLocation previos;
    private MapLocation self;
    
    public PathUnit(MapLocation previos, MapLocation itself){
        this.self = itself;
        this.previos = previos;
    }
    
    public boolean checked(PathUnit other){
        return this.self == other.self;
    }
    
    public MapLocation getPreviosLoc(){
        return previos;
    }
    
    public MapLocation getCurrentLoc(){
        return self;
    }


}
