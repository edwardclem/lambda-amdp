package data;

import amdp.cleanup.state.*;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.State;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by edwardwilliams on 6/21/17.
 * Helper functions for saving and loading BURLAP states.
 */
public class DataHelpers {

    /**
     * Modified version of burlap.mdp.core.state.StateUtilities.
     * Returns single-line representation of state, easier to parse.
     * @param s
     * @return
     */
    public static String stateToStringCompact(State s){
        StringBuilder buf = new StringBuilder();
        //buf.append("{");
        List<Object> keys = s.variableKeys();
        for(Object key : keys){
            buf.append(key.toString()).append(": {").append(s.get(key).toString()).append("} ");
        }
        //buf.append("}");
        return buf.toString();
    }

    /**
     * Same as above.
     * @param o
     * @return
     */
    public static String objectInstanceToStringCompact(ObjectInstance o){
        StringBuilder buf = new StringBuilder();
        buf.append(o.name()).append(" (").append(o.className()).append("): ")
                .append("[").append(stateToStringCompact(o)).append("]");
        return buf.toString();
    }

    public static String ooStateToStringCompact(OOState s){
        StringBuilder buf = new StringBuilder();
        buf.append("{");
        for(ObjectInstance o : s.objects()){
            buf.append(objectInstanceToStringCompact(o)).append(" , ");
        }
        buf.append("}");
        return buf.toString();
    }

    public static CleanupState loadStateFromString(String stateString){

        //initializing objects

        List<CleanupRoom> rooms = new ArrayList<CleanupRoom>();
        List<CleanupDoor> doors = new ArrayList<CleanupDoor>();
        List<CleanupBlock> blocks = new ArrayList<CleanupBlock>();
        CleanupAgent agent = null;

        //trim first and last bracket
        String trimmed = stateString.substring(1, stateString.length() - 1);

        //System.out.println(trimmed);

        String objRegex = "(?<id>\\S+) \\((?<type>\\S+)\\): \\[(?<props>.+?)\\]";

        Pattern objPattern = Pattern.compile(objRegex);
        Matcher matcher = objPattern.matcher(trimmed);

        String propRegex = "(?<key>\\S+): \\{(?<val>\\S+?)\\}";
        Pattern propPattern = Pattern.compile(propRegex);


        while(matcher.find()) {
            String id  = matcher.group("id");

            String type = matcher.group("type");

            String propString = matcher.group("props");

            //System.out.println("object: " + " " + id +  " " + type);

            Matcher propMatcher = propPattern.matcher(propString);

            HashMap<String, String> props = new HashMap<>();
            //extract properties
            while(propMatcher.find()){
                String key = propMatcher.group("key");
                String val = propMatcher.group("val");
                props.put(key, val);
            }

            //System.out.println(props);

            //add to different list, based on object type.

            if (type.equals("agent")){
                agent = new CleanupAgent(id, Integer.parseInt(props.get("x")), Integer.parseInt(props.get("y")));
                agent.directional = true;
                agent.currentDirection = props.get("direction");
            } else if (type.equals("room")){
                CleanupRoom r = new CleanupRoom(id, Integer.parseInt(props.get("top")), Integer.parseInt(props.get("left")), Integer.parseInt(props.get("bottom")), Integer.parseInt(props.get("right")), props.get("colour"));
                rooms.add(r);

            } else if (type.equals("door")){
                CleanupDoor d = new CleanupDoor(id, Integer.parseInt(props.get("locked")),Integer.parseInt(props.get("top")), Integer.parseInt(props.get("left")), Integer.parseInt(props.get("bottom")), Integer.parseInt(props.get("right")), Boolean.parseBoolean(props.get("canBeLocked")));
                doors.add(d);

            } else if (type.equals("block")){
                CleanupBlock b = new CleanupBlock(id, Integer.parseInt(props.get("x")), Integer.parseInt(props.get("y")), props.get("shape"), props.get("colour"));
                blocks.add(b);
            } else{
                System.out.println("invalid object. ignoring.");
            }
        }

        CleanupState state = new CleanupState(agent, blocks, doors, rooms);

        return state;
    }
}
