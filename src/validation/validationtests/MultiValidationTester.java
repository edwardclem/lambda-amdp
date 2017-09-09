package validation.validationtests;

import amdp.cleanup.CleanupDomain;
import amdp.cleanup.state.*;
import burlap.mdp.core.oo.state.OOState;
import data.BurlapMultiDemonstration;
import data.BurlapMultiDemonstrationDataset;
import data.DataHelpers;
import edu.cornell.cs.nlp.spf.mr.lambda.Lambda;
import validation.BurlapMultiValidator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MultiValidationTester {

    public static void main(String[] args) {

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
        CleanupAgent agent = new CleanupAgent("agent0", 2, 2);
        agent.directional = true;
        agent.currentDirection = "south";
        CleanupBlock block1 = new CleanupBlock("block0", 3, 6, "basket", "red");
        List<CleanupBlock> blocks = new ArrayList();
        blocks.add(block1);
        CleanupState afterState = new CleanupState(agent, blocks, doors, rooms);



        BurlapMultiValidator bmv = new BurlapMultiValidator();
        String parsedCommand = "(in:<e,<e,t>> (the:<<e,t>,e> (lambda $0:e (agent:<e,t> $0))) (the:<<e,t>,e> (lambda $1:e (and:<t*,t> (green:<e,t> $1) (room:<e,t> $1)))))";

        StringBuilder sb = new StringBuilder();
        sb.append("go to the red room\n");
        sb.append(DataHelpers.ooStateToStringCompact((OOState) CleanupDomain.getClassicState(true))).append("\n");
        sb.append(DataHelpers.ooStateToStringCompact((OOState) CleanupDomain.getClassicState(true))).append("\n");
        sb.append("true\n---\n");
        sb.append(DataHelpers.ooStateToStringCompact((OOState) CleanupDomain.getClassicState(true))).append("\n");
        sb.append(DataHelpers.ooStateToStringCompact(afterState)).append("\n");
        sb.append("true\n");
//        BurlapMultiDemonstration demo = BurlapMultiDemonstration.parse(sb.toString());
        //Boolean valid = bmv.isValid(demo ,parsedCommand);
        //System.out.println(valid);

        String demoString = "Go to the red room\n{agent0 (agent): [x: {6} y: {2} direction: {south} ] , door0 (door): [locked: {0} top: {4} left: {6} bottom: {4} right: {6} canBeLocked: {false} ] , door1 (door): [locked: {0} top: {4} left: {2} bottom: {4} right: {2} canBeLocked: {false} ] , block0 (block): [x: {2} y: {2} shape: {chair} colour: {red} ] , room0 (room): [top: {4} left: {0} bottom: {0} right: {8} colour: {green} ] , room1 (room): [top: {8} left: {0} bottom: {4} right: {4} colour: {red} ] , room2 (room): [top: {8} left: {4} bottom: {4} right: {8} colour: {blue} ] , }\n{agent0 (agent): [x: {2} y: {6} direction: {south} ] , door0 (door): [locked: {0} top: {4} left: {6} bottom: {4} right: {6} canBeLocked: {false} ] , door1 (door): [locked: {0} top: {4} left: {2} bottom: {4} right: {2} canBeLocked: {false} ] , block0 (block): [x: {2} y: {2} shape: {chair} colour: {red} ] , room0 (room): [top: {4} left: {0} bottom: {0} right: {8} colour: {green} ] , room1 (room): [top: {8} left: {0} bottom: {4} right: {4} colour: {red} ] , room2 (room): [top: {8} left: {4} bottom: {4} right: {8} colour: {blue} ] , }\nfalse\n---\n{agent0 (agent): [x: {6} y: {6} direction: {south} ] , door0 (door): [locked: {0} top: {4} left: {6} bottom: {4} right: {6} canBeLocked: {false} ] , door1 (door): [locked: {0} top: {4} left: {2} bottom: {4} right: {2} canBeLocked: {false} ] , block0 (block): [x: {2} y: {2} shape: {basket} colour: {red} ] , room0 (room): [top: {4} left: {0} bottom: {0} right: {8} colour: {red} ] , room1 (room): [top: {8} left: {0} bottom: {4} right: {4} colour: {green} ] , room2 (room): [top: {8} left: {4} bottom: {4} right: {8} colour: {blue} ] , }\n{agent0 (agent): [x: {6} y: {3} direction: {south} ] , door0 (door): [locked: {0} top: {4} left: {6} bottom: {4} right: {6} canBeLocked: {false} ] , door1 (door): [locked: {0} top: {4} left: {2} bottom: {4} right: {2} canBeLocked: {false} ] , block0 (block): [x: {2} y: {2} shape: {basket} colour: {red} ] , room0 (room): [top: {4} left: {0} bottom: {0} right: {8} colour: {red} ] , room1 (room): [top: {8} left: {0} bottom: {4} right: {4} colour: {green} ] , room2 (room): [top: {8} left: {4} bottom: {4} right: {8} colour: {blue} ] , }\nfalse\n---\n{agent0 (agent): [x: {6} y: {6} direction: {south} ] , door0 (door): [locked: {0} top: {4} left: {6} bottom: {4} right: {6} canBeLocked: {false} ] , door1 (door): [locked: {0} top: {4} left: {2} bottom: {4} right: {2} canBeLocked: {false} ] , block0 (block): [x: {2} y: {2} shape: {chair} colour: {red} ] , room0 (room): [top: {4} left: {0} bottom: {0} right: {8} colour: {green} ] , room1 (room): [top: {8} left: {0} bottom: {4} right: {4} colour: {red} ] , room2 (room): [top: {8} left: {4} bottom: {4} right: {8} colour: {blue} ] , }\n{agent0 (agent): [x: {2} y: {6} direction: {south} ] , door0 (door): [locked: {0} top: {4} left: {6} bottom: {4} right: {6} canBeLocked: {false} ] , door1 (door): [locked: {0} top: {4} left: {2} bottom: {4} right: {2} canBeLocked: {false} ] , block0 (block): [x: {2} y: {2} shape: {chair} colour: {red} ] , room0 (room): [top: {4} left: {0} bottom: {0} right: {8} colour: {green} ] , room1 (room): [top: {8} left: {0} bottom: {4} right: {4} colour: {red} ] , room2 (room): [top: {8} left: {4} bottom: {4} right: {8} colour: {blue} ] , }\nfalse";
//        BurlapMultiDemonstration demo2 = BurlapMultiDemonstration.parse(demoString);
        //Boolean valid2 = bmv.isValid(demo2, parsedCommand);
        //System.out.println(valid2);
    }
}
