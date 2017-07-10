package lambdas.LambdaPlanning;

import data.DataHelpers;

import amdp.cleanup.state.*;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.State;
import jscheme.JScheme;
import jscheme.SchemeProcedure;

import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by edwardwilliams on 7/9/17.
 */
public class LambdaTestNear {

    public static void main(String[] args){

        String initialStateString = "{agent0 (agent): [x: {5} y: {2} direction: {south} ] , door0 (door): [locked: {0} top: {4} left: {6} bottom: {4} right: {6} canBeLocked: {false} ] , door1 (door): [locked: {0} top: {4} left: {2} bottom: {4} right: {2} canBeLocked: {false} ] , block0 (block): [x: {2} y: {2} shape: {chair} colour: {blue} ] , room0 (room): [top: {4} left: {0} bottom: {0} right: {8} colour: {blue} ] , room1 (room): [top: {8} left: {0} bottom: {4} right: {4} colour: {green} ] , room2 (room): [top: {8} left: {4} bottom: {4} right: {8} colour: {red} ] , }";

        String targetStateString = "{agent0 (agent): [x: {3} y: {2} direction: {south} ] , door0 (door): [locked: {0} top: {4} left: {6} bottom: {4} right: {6} canBeLocked: {false} ] , door1 (door): [locked: {0} top: {4} left: {2} bottom: {4} right: {2} canBeLocked: {false} ] , block0 (block): [x: {2} y: {2} shape: {chair} colour: {blue} ] , room0 (room): [top: {4} left: {0} bottom: {0} right: {8} colour: {blue} ] , room1 (room): [top: {8} left: {0} bottom: {4} right: {4} colour: {green} ] , room2 (room): [top: {8} left: {4} bottom: {4} right: {8} colour: {red} ] , }";

        CleanupState initialState = DataHelpers.loadStateFromStringCompact(initialStateString);

        CleanupState targetState = DataHelpers.loadStateFromStringCompact(targetStateString);

        String preds = "src/CleanupPredicates.scm";
        JScheme js = new JScheme();

        try{
            FileReader f = new java.io.FileReader(preds);
            js.load(f);

            SchemeProcedure dist = (SchemeProcedure)js.eval("(near {agent0} {block0})");

            System.out.println(js.call(dist, initialState));


        } catch (FileNotFoundException e) {
            System.out.println("ERROR");
        }



    }
}
