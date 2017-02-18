package multicast;

import config.Configuration;
import config.Group;
import logger.LogUtil;
import message.GroupMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;

/**
 * Created by wcx73 on 2017/2/16.
 */
public class MulticastCoordinator {
    private static final MulticastCoordinator instance = new MulticastCoordinator();
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, AtomicInteger>> groupTimeStampMap = new ConcurrentHashMap<>();
    public final ArrayList<GroupMessage> holdBackQueue = new ArrayList<>();

    public void init() {
        ConcurrentHashMap<String, Group> groupMap = Configuration.getGroupMap();
        if (groupMap != null) {
            for (Group group : groupMap.values()) {
                ConcurrentHashMap<String, AtomicInteger> groupTimeStamp = new ConcurrentHashMap<>();
                for (String memberName : group.getGroupMembers())
                    groupTimeStamp.putIfAbsent(memberName, new AtomicInteger(0));
                groupTimeStampMap.putIfAbsent(group.getGroupName(), groupTimeStamp);
            }
        }
    }

    public ConcurrentHashMap<String, ConcurrentHashMap<String, AtomicInteger>> getGroupTimeStampMap() {
        return groupTimeStampMap;
    }

    @Override
    public String toString() {
        return "MulticastCoordinator{" +
                groupTimeStampMap +
                '}';
    }

    public static MulticastCoordinator getInstance() {
        return instance;
    }

    public static ConcurrentHashMap<String, AtomicInteger> getGroupTimeStampCopy(String groupName) {
        ConcurrentHashMap<String, AtomicInteger> ret = new ConcurrentHashMap<>();
        for (Map.Entry<String, AtomicInteger> entry : instance.getGroupTimeStampMap().get(groupName).entrySet()) {
            ret.put(new String(entry.getKey()), new AtomicInteger(entry.getValue().get()));
        }
        return ret;
    }

    public void incrementTime(String groupName, String localName) {
        instance.getGroupTimeStampMap().get(groupName).get(localName).addAndGet(1);
    }

    public void holdMessage(GroupMessage message) {
        this.holdBackQueue.add(message);
    }

    // TODO: known bug - something wrong with queue.pop() but doesn't affect functionality
    public synchronized boolean releaseGroupMessage(GroupMessage message){
        String localName = Configuration.localName;
        String groupName = null;
        ConcurrentHashMap<String, AtomicInteger> map = message.getGroupTimeStamp(); 
        if (message.getSrc().equals(localName))
          return true;
        boolean remove = true;
        //for (GroupMessage message: holdBackQueue){
            groupName = message.getGroupName();
            String messageSource = message.getSrc();
            for (String nodeName: map.keySet()) {
                if (!remove) break;
                int localTimeStamp = this.groupTimeStampMap.get(groupName).get(nodeName).getAndAdd(0);
                int remoteTimeStamp = map.get(nodeName).getAndAdd(0);
                if (messageSource.equals(nodeName)) {
                    if (localTimeStamp + 1 != remoteTimeStamp)
                        remove = false;
                } else {
                    if (localTimeStamp < remoteTimeStamp)
                        remove = false;
                }
            }
        //}
        return remove;
    }

   public void updateTime(String groupName, GroupMessage message) {
        // get the local and received time
        ConcurrentHashMap<String, AtomicInteger> received = message.getGroupTimeStamp();
        ConcurrentHashMap<String, AtomicInteger> local = instance.getGroupTimeStampMap().get(groupName);

        // increment and update
        for(Map.Entry<String, AtomicInteger> entry : local.entrySet()) {
            int localTime = entry.getValue().get();
            int receivedTime = received.get(entry.getKey()).get();
            int newTime = Math.max(localTime, receivedTime); 
            local.put(entry.getKey(), new AtomicInteger(newTime));
        }
    }
}
