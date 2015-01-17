package team105;

import battlecode.common.MapLocation;

public class PathUnit {
    private PathUnit previos;
    private MapLocation self;
    
    public PathUnit(PathUnit previos, MapLocation itself){
        this.self = itself;
        this.previos = previos;
    }
    
    public boolean checked(PathUnit other){
        return this.self == other.self;
    }
    
    public PathUnit getPreviosLoc(){
        return previos;
    }
    
    public MapLocation getCurrentLoc(){
        return self;
    }


}
