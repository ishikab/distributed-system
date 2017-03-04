package multicast;

import config.Configuration;
import config.Group;
import message.GroupMessage;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wcx73 on 2017/2/16.
 */
public class MulticastCoordinator {
    private static final MulticastCoordinator instance = new MulticastCoordinator();
    public final ArrayList<GroupMessage> holdBackQueue = new ArrayList<>();
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, AtomicInteger>> groupTimeStampMap = new ConcurrentHashMap<>();

    public static MulticastCoordinator getInstance() {
        return instance;
    }

    public static ConcurrentHashMap<String, AtomicInteger> getGroupTimeStampCopy(String groupName) {
        ConcurrentHashMap<String, AtomicInteger> ret = new ConcurrentHashMap<>();
        for (Map.Entry<String, AtomicInteger> entry : instance.getGroupTimeStampMap().get(groupName).entrySet()) {
            ret.put(entry.getKey(), new AtomicInteger(entry.getValue().get()));
        }
        return ret;
    }

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
        boolean remove = true;
        if (message.getSrc().equals(localName))
        {
          for (GroupMessage entry: holdBackQueue){
            if (!entry.equals(message)) {
            ConcurrentHashMap<String, AtomicInteger> maphb = entry.getGroupTimeStamp();
            for (String nodeName: map.keySet()) {
                if (!remove) break;
                int hbTimeStamp = maphb.get(nodeName).getAndAdd(0);
                int msgTimeStamp = map.get(nodeName).getAndAdd(0);
                if (hbTimeStamp < msgTimeStamp) {
                  remove = false;
                }
            }
         }
         }
         return remove;
        }
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
        //}
        } return remove;
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
