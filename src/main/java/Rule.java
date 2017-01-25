import java.util.LinkedHashMap;

/**
 * Created by chenxiw on 1/24/17.
 * chenxi.wang@sv.cmu.edu
 */
class Rule {
    enum Action {
        DROP, DROP_AFTER, DUPLICATE, DELAY, DROP_DUPLICATE, NIL
    }
    String src = null, dest = null, kind = null;
    Integer seqNum = -1;
    Action action = Action.NIL;

    Rule(LinkedHashMap<String, Object> data) {
        if(data.containsKey("src")) this.src = (String) data.get("src");
        if(data.containsKey("dest")) this.dest = (String) data.get("dest");
        if(data.containsKey("kind")) this.kind = (String) data.get("kind");
        this.seqNum = (Integer)data.get("seqNum");
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
}
