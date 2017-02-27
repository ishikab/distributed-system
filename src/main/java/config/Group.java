package config;

import message.Message;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
/**
 * Created by wcx73 on 2017/2/14.
 */
public class Group {
    private String groupName;
    private HashSet<String> groupMembers;
    private ArrayList<Message> holdBackList = new ArrayList<Message>();

    @SuppressWarnings("unchecked")
    public Group(LinkedHashMap data) {
        this.groupName = (String) data.get("name");
        this.groupMembers = new HashSet((ArrayList)data.get("members")) ;
    }
   
    public String getGroupName() {
        return groupName;
    } 
   
    public HashSet<String> getGroupMembers() {
        return groupMembers;
    }

    public int getGroupSize() {
        return groupMembers.size();
    }
    public boolean hasNodeName(String nodeName) {
        return groupMembers.contains(nodeName);
    }

    public void addToHoldBackList(Message msg){
        holdBackList.add(0, msg);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", groupName, groupMembers);
    }
}
