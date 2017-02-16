package config;

import logger.LogUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import message.Message;
/**
 * Created by wcx73 on 2017/2/14.
 */
public class Group {
    private String groupName;
    private ArrayList<String> groupMembers;
    private ArrayList<Message> holdBackList = new ArrayList<Message>();
 
    public Group(LinkedHashMap data) {
        this.groupName = (String) data.get("name");
        this.groupMembers = (ArrayList<String>) data.get("members");
        LogUtil.info("hello");
    }
   
    public String getGroupName() {
        return groupName;
    } 
   
    public ArrayList<String> getGroupMembers() {
        return groupMembers;
    }

    public void addToHoldBackList(Message msg){
        holdBackList.add(0, msg);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", groupName, groupMembers);
    }
}
