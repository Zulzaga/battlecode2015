package kairat;


/*BaseBot represents Unit and Structure.
 * General:
 * 
 * Starts with 500 ore, and each team automatically receives 5 ore per turn before any mining income
 * USE OF  CHANNELS.
 * 1. 
 * 2. Number of spawned beavers.
 * 3. 
 * 4. Path explorer with right preference
 * 5. Path explorer with left preference
 * 6.
 * 7. Barrack   4; 200-700
 * 8. Miner factory 3; 0-300
 * 9. HandwashStation 2; 1000-1300
 * 10. Helipad 2; 500-1000
 * 11. Tank factory 4; 700-1200
 * 12. Aerospace lab 2; 1000-1700
 * 
 *   channel numbers must be
 *   
 *   HQ:
 *   
 *   AA BB CC DDD:
 *   
 *   AA:  Always 10 (making it different from structures).
 *   BB: for special purpose. 00 if nothing special.
 *   CC: 
 *   DDD: up this stucture's management. 
 *   
 *    
 *   Structures (except HQ) 
 *   AA BB CC DDD - must be 9 digits.
 *   
 *   AA: type of unit
 *   BB: for special purpose. 00 if nothing special.
 *   CC: spawned order number. 1st or 2nd. 
 *   DDD: up this stucture's management. 
 *   
 *   11 - tower
 *   12 - SupplyDepot
 *   13 - TechnologyInstitute
 *   14 - Barracks
 *   15 - HandwashStation
 *   16 - TrainingField
 *   17 - TankFactory
 *   18 - AerospaceLab
 *   19 - MinerFactory
 *   20 - Helipad
 *   
 *   
 *   Units:
 *   AA BB CC DD - must be 8digits. 
 *   
 *   AA: type of unit
 *   BB: 00 if it is not in army. Otherwise, it's army number.
 *   CC: spawned order number. 1st or 2nd 
 *   DD: up to this unit's management.
 *   
 *   11 - Beaver
 *   12 - Soldier
 *   13 - Computer
 *   14 - Basher
 *   15 - Drone
 *   
 *   16 - Miner
 *   17 - Commander
 *   18 - Tank
 *   19 - Launcher
 *   Details HQ:
 *   
 *   00
 *  
 */

public interface Channels {
    
    public void createChannel();

}
