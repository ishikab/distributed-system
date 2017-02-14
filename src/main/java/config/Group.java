package config;

import logger.LogUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by wcx73 on 2017/2/14.
 */
public class Group {
    private String groupName;
    private ArrayList<String> groupMembers;

    public Group(LinkedHashMap data) {
        this.groupName = (String) data.get("name");
        this.groupMembers= (ArrayList<String>) data.get("members");
        LogUtil.info("hello");
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", groupName, groupMembers);
    }
}
