package config;

import logger.LogUtil;
import message.Message;

import java.util.LinkedHashMap;

/**
 * Rules used to match specific messages
 */
public class Rule {
    public Integer seqNum = null;
    public Action action = Action.NONE;
    private String src = null, dest = null, kind = null;
    private Boolean isDuplicate = null;
    Rule(LinkedHashMap<String, Object> data) {
        if (data.containsKey("src")) this.src = (String) data.get("src");
        if (data.containsKey("dest")) this.dest = (String) data.get("dest");
        if (data.containsKey("kind")) this.kind = (String) data.get("kind");
        if (data.containsKey("seqNum")) this.seqNum = (Integer) data.get("seqNum");
        if ((data.containsKey("duplicate"))) this.isDuplicate = (boolean) data.get("duplicate");
        switch ((String) data.get("action")) {
            case "drop":
                this.action = Action.DROP;
                break;
            case "duplicate":
                this.action = Action.DUPLICATE;
                break;
            case "delay":
                this.action = Action.DELAY;
                break;
            case "dropAfter":
                this.action = Action.DROP_AFTER;
                break;
        }
    }

    @Override
    public String toString() {
        return "[" + action + "]" +
                (src == null ? "" : " src=" + src) +
                (dest == null ? "" : " dest=" + dest) +
                (kind == null ? "" : " kind=" + kind) +
                (seqNum == null ? "" : " #seq=" + seqNum) +
                (" duplicate=" + isDuplicate);
    }

    public boolean matches(Message message) {
        if (this.isDuplicate != null && !this.isDuplicate && this.action == Action.DUPLICATE)
            LogUtil.info("here");
        if (this.src != null) {
            if (!src.equals(message.getSrc())) return false;
        }
        if (this.dest != null) {
            if (!dest.equals(message.getDest())) return false;
        }
        if (this.kind != null) {
            if (!kind.equals(message.getKind())) return false;
        }
        if (this.seqNum != null) {
            if (this.action == Action.DROP && !this.seqNum.equals(0))
                return false;
            else if (this.action == Action.DROP_AFTER && this.seqNum > message.getSeqNum())
                return false;
        }
//         if (this.action == Action.DROP_AFTER) {
//          if (this.seqNum >= 0) {
//            if (seqNum > message.getSeqNum()) return false;
//          }
//        }
//        else {
//          if (this.seqNum >= 0) {
//            if (seqNum != message.getSeqNum()) return false;
//          }
//        }
        if (this.isDuplicate != null)
            if (!message.isDuplicate().equals(this.isDuplicate)) return false;
        LogUtil.info(message);
        LogUtil.info(this);
        return true;
    }

    public enum Action {
        DROP, DROP_AFTER, DUPLICATE, DELAY, NONE
    }
}
