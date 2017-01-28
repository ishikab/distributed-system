import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * 18842-lab0 Chenxi Wang, Ishika Batra, Team 6
 * chenxi.wang@sv.cmu.edu
 * ibatra@andrew.cmu.edu
 */
class Rule {
    enum Action {
        DROP, DROP_AFTER, DUPLICATE, DELAY, DROP_DUPLICATE, NONE
    }

    private String src = null, dest = null, kind = null;
    Integer seqNum = -1;
    Action action = Action.NONE;

    @Override
    public String toString() {
        return "Rule{" +
                "src='" + src + '\'' +
                ", dest='" + dest + '\'' +
                ", kind='" + kind + '\'' +
                ", seqNum=" + seqNum +
                ", action=" + action +
                '}';
    }

    Rule(LinkedHashMap<String, Object> data) {
        if(data.containsKey("src")) this.src = (String) data.get("src");
        if(data.containsKey("dest")) this.dest = (String) data.get("dest");
        if(data.containsKey("kind")) this.kind = (String) data.get("kind");
        if (data.containsKey("seqNum")) this.seqNum = (Integer) data.get("seqNum");
        switch ((String)data.get("action")) {
            case "drop":
                if ((Boolean)data.getOrDefault("duplicate", false)) this.action = Action.DROP_DUPLICATE;
                else this.action = Action.DROP;
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
        if (this.seqNum >= 0) {
            if (seqNum >  message.getSeqNum()) return false;
        }
        return true;
    }
}
