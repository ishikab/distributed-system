package config;

import message.Message;

import java.util.LinkedHashMap;

/**
 * Rules used to match specific messages
 */
public class Rule {
    public Integer seqNum = -1;
    public Action action = Action.NONE;
    public String src = null, dest = null, kind = null;
    public Boolean isDuplicate = false;
    Rule(LinkedHashMap<String, Object> data) {
        if (data.containsKey("src")) this.src = (String) data.get("src");
        if (data.containsKey("dest")) this.dest = (String) data.get("dest");
        if (data.containsKey("kind")) this.kind = (String) data.get("kind");
        if (data.containsKey("seqNum")) this.seqNum = (Integer) data.get("seqNum");
        if ((data.containsKey("duplicate")) && (boolean) data.get("duplicate")) this.isDuplicate = true;
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
                (seqNum < 0 ? "" : " #seq=" + seqNum) +
                (isDuplicate ? " duplicate=true" : "");
    }

    public boolean matches(Message message) {
        if (this.src != null) {
            if (!src.equals(message.getSrc())) return false;
        }
        if (this.dest != null) {
            if (!dest.equals(message.getDest())) return false;
        }
        if (this.kind != null) {
            if (!kind.equals(message.getKind())) return false;
        }
         if (this.action == Action.DROP_AFTER) {
          if (this.seqNum >= 0) {
            if (seqNum > message.getSeqNum()) return false;
          }
        }
        else {
          if (this.seqNum >= 0) {            
            if (seqNum != message.getSeqNum()) return false;
          }
        }
        if (this.isDuplicate) {
            if (!message.isDuplicate()) return false;
        }
        return true;
    }

    public enum Action {
        DROP, DROP_AFTER, DUPLICATE, DELAY, NONE
    }
}
