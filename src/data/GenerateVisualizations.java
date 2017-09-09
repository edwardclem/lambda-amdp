package data;

import amdp.cleanup.state.CleanupState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GenerateVisualizations {

    public static void main(String[] args){

        //generating visualizations
        String path = "data/blue_room/GoToTheBlueChair/post5.txt";

//        try {
            //String stateString = new String(Files.readAllBytes(Paths.get(path)));

//            String postcond = "{agent0 (agent): [x: {12} y: {10} direction: {north} ] , door0 (door): [locked: {0} top: {8} left: {12} bottom: {8} right: {12} canBeLocked: {false} ] , door1 (door): [locked: {0} top: {8} left: {4} bottom: {8} right: {4} canBeLocked: {false} ] , block0 (block): [x: {2} y: {2} shape: {chair} colour: {red} ] , block1 (block): [x: {12} y: {11} shape: {chair} colour: {blue} ] , block3 (block): [x: {3} y: {10} shape: {chair} colour: {green} ] , room0 (room): [top: {8} left: {0} bottom: {0} right: {16} colour: {red} ] , room1 (room): [top: {16} left: {0} bottom: {8} right: {8} colour: {green} ] , room2 (room): [top: {16} left: {8} bottom: {8} right: {16} colour: {blue} ] , }";

            String postcond = "{agent0 (agent): [x: {6} y: {3} direction: {south} ] , door0 (door): [locked: {0} top: {4} left: {6} bottom: {4} right: {6} canBeLocked: {false} ] , door1 (door): [locked: {0} top: {4} left: {2} bottom: {4} right: {2} canBeLocked: {false} ] , block0 (block): [x: {5} y: {3} shape: {chair} colour: {red} ] , room0 (room): [top: {4} left: {0} bottom: {0} right: {4} colour: {green} ] , room1 (room): [top: {4} left: {4} bottom: {0} right: {8} colour: {red} ] , room2 (room): [top: {8} left: {0} bottom: {4} right: {8} colour: {red} ] , }\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";

            CleanupState state = DataHelpers.loadStateFromStringCompact(postcond);
            DataHelpers.VisualizeState(state);

//        } catch (IOException e){
//            System.out.println(e);
//        }

    }
}
