package kairat;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;

/*
 * 
 * Handles communication channels
 * It should be used for less frequent communicative channels because it uses more bytecodes. 
 * 
 * There should be only one communication instance for the game time 
 * which should be maintained by HQ.
 * 
 * 
 * In broadcast communication: (int channel,int data)
 * 
 * channel - the channel to write to, from 0 to BROADCAST_MAX_CHANNELS.
 * data - one int's worth of data to write.
 * 
 * broadcast - 25 bytecode.
 * readBroadcast - 5 bytecode.
 */

public class Communication {
    private static int numChannels;
    private static Map<String, Integer> channelTable; // name of channel,
                                                      // channel #

    /**
     * Initialize communication channels.
     */
    public Communication() {
        numChannels = 20; // must be 0, but we are using 2 channels
        channelTable = new HashMap<String, Integer>();
    }

    /**
     * Create a new channel and increment total number of channels.
     * 
     * @param rc
     *            Robotcontroller from which new channel is requested
     * @param channelName
     * @param value
     *            the value to be broadcasted
     * @return false if there is another channel under the same name. Otherwise,
     *         channel would be created and returns true.
     * @throws GameActionException
     */
    public boolean createChannel(RobotController rc, String channelName,
            int value) throws GameActionException {
        if (channelTable.containsKey(channelName)) {
            return false;
        }
        channelTable.put(channelName, numChannels);
        rc.broadcast(numChannels, value);
        numChannels = +1;
        return true;
    }

    /**
     * Broadcasts value.
     * 
     * @param rc
     * @param channelName
     *            where the value to be broadcasted.
     * @param value
     * @throws GameActionException
     */
    public static void broadcastValue(RobotController rc, String channelName,
            int value) throws GameActionException {
        assert (channelTable.containsKey(channelName));
        int channelNum = channelTable.get(channelName);
        rc.broadcast(channelNum, value);

    }

    /**
     * Increment or decrement the broadcasted value
     * 
     * @param rc
     * @param channelName
     * @param value
     * @throws GameActionException
     */
    public static void broadcastIncrementValue(RobotController rc,
            String channelName, int value) throws GameActionException {
        assert (channelTable.containsKey(channelName));
        int channelNum = channelTable.get(channelName);
        int oldValue = rc.readBroadcast(channelNum);
        rc.broadcast(channelNum, oldValue + value);
    }

}
