package validation.validationtests;

import amdp.cleanup.CleanupDomain;
import amdp.cleanup.state.*;
import burlap.mdp.core.state.State;
import validation.BurlapValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: update this tester to use new file format
 * Created by edwardwilliams on 6/23/17.
 */
public class ValidationTester {

    public static void main(String[] args) {

        String parsedCommand = "(in:<e,<e,t>> (the:<<e,t>,e> (lambda $0:e (block:<e,t> $0))) (the:<<e,t>,e> (lambda $1:e (and:<t*,t> (green:<e,t> $1) (room:<e,t> $1)))))";
        State s = CleanupDomain.getClassicState(true);

        CleanupRoom r1 = new CleanupRoom("room0", 4, 0, 0, 8, "red");
        CleanupRoom r2 = new CleanupRoom("room1", 8, 0, 4, 4, "green");
        CleanupRoom r3 = new CleanupRoom("room2", 8, 4, 4, 8, "blue");
        List<CleanupRoom> rooms = new ArrayList();
        rooms.add(r1);
        rooms.add(r2);
        rooms.add(r3);
        CleanupDoor d1 = new CleanupDoor("door0", 0, 4, 6, 4, 6, false);
        CleanupDoor d2 = new CleanupDoor("door1", 0, 4, 2, 4, 2, false);
        List<CleanupDoor> doors = new ArrayList();
        doors.add(d1);
        doors.add(d2);
        CleanupAgent agent = new CleanupAgent("agent0", 2, 6);
        agent.directional = true;
        agent.currentDirection = "south";
        CleanupBlock block1 = new CleanupBlock("block0", 3, 6, "basket", "red");
        List<CleanupBlock> blocks = new ArrayList();
        blocks.add(block1);
        CleanupState afterState = new CleanupState(agent, blocks, doors, rooms);
        //BurlapDemonstration demonstration = new BurlapDemonstration("put the block in the green room",s, afterState);
        BurlapValidator v = new BurlapValidator();
        //System.out.println(v.isValid(demonstration, parsedCommand));
    }
}
