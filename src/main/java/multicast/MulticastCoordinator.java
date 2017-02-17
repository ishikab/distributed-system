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

/**
 * Created by wcx73 on 2017/2/16.
 */
public class MulticastCoordinator {
    private static final MulticastCoordinator instance = new MulticastCoordinator();
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, AtomicInteger>> groupTimeStampMap = new ConcurrentHashMap<>();
    private final LinkedBlockingDeque<GroupMessage> holdBackQueue = new LinkedBlockingDeque<>();

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
            ret.put(entry.getKey(), entry.getValue());
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
    public GroupMessage releaseGroupMessage(ConcurrentHashMap<String, AtomicInteger> map){
        String localName = Configuration.localName;
        String groupName = null;
        for (GroupMessage message: holdBackQueue){
            groupName = message.getGroupName();
            boolean shouldBreak = false;
            String messageSource = message.getSrc();
            for (String nodeName: map.keySet()) {
                if (shouldBreak) break;
                int localTimeStamp = this.groupTimeStampMap.get(groupName).get(nodeName).getAndAdd(0);
                int remoteTimeStamp = map.get(nodeName).getAndAdd(0);
                if (messageSource.equals(nodeName)) {
                    if (localTimeStamp + 1 != remoteTimeStamp)
                        shouldBreak = true;
                } else {
                    if (localTimeStamp < remoteTimeStamp)
                        shouldBreak = true;
                }
            }
            if (!shouldBreak){
                LogUtil.info("candidate found");
                LogUtil.info(message);
                LogUtil.info(map);
                return message;
            }
        }
        return null;
    }
}
