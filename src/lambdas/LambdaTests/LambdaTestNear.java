package lambdas.LambdaTests;

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

        String stateString = "{\n" +
                "agent0 (agent): {\n" +
                "x: {5}\n" +
                "y: {2}\n" +
                "direction: {south}\n" +
                "}\n" +
                "door0 (door): {\n" +
                "locked: {0}\n" +
                "top: {4}\n" +
                "left: {6}\n" +
                "bottom: {4}\n" +
                "right: {6}\n" +
                "canBeLocked: {false}\n" +
                "}\n" +
                "door1 (door): {\n" +
                "locked: {0}\n" +
                "top: {4}\n" +
                "left: {2}\n" +
                "bottom: {4}\n" +
                "right: {2}\n" +
                "canBeLocked: {false}\n" +
                "}\n" +
                "block0 (block): {\n" +
                "x: {6}\n" +
                "y: {2}\n" +
                "shape: {chair}\n" +
                "colour: {blue}\n" +
                "}\n" +
                "room0 (room): {\n" +
                "top: {4}\n" +
                "left: {0}\n" +
                "bottom: {0}\n" +
                "right: {4}\n" +
                "colour: {green}\n" +
                "}\n" +
                "room1 (room): {\n" +
                "top: {4}\n" +
                "left: {4}\n" +
                "bottom: {0}\n" +
                "right: {8}\n" +
                "colour: {red}\n" +
                "}\n" +
                "room2 (room): {\n" +
                "top: {8}\n" +
                "left: {0}\n" +
                "bottom: {4}\n" +
                "right: {8}\n" +
                "colour: {blue}\n" +
                "}\n" +
                "}";

        CleanupState testState = DataHelpers.loadStateFromString(stateString);

        String preds = "src/CleanupPredicates.scm";
        JScheme js = new JScheme();

        try{
            FileReader f = new java.io.FileReader(preds);
            js.load(f);

            SchemeProcedure dist = (SchemeProcedure)js.eval("(near {agent0} {block0})");

            System.out.println(js.call(dist, initialState));

            SchemeProcedure left = (SchemeProcedure)js.eval("(left {agent0} {block0})");
            System.out.println(js.call(left, testState));

            SchemeProcedure right = (SchemeProcedure)js.eval("(right {block0} {agent0})");
            System.out.println(js.call(right, testState));

            SchemeProcedure notRight = (SchemeProcedure)js.eval("(right {door0} {agent0})");
            System.out.println(js.call(notRight, testState));

            SchemeProcedure notLeft = (SchemeProcedure)js.eval("(left {door0} {room0})");
            System.out.println(js.call(notLeft, testState));


        } catch (FileNotFoundException e) {
            System.out.println("ERROR");
        }



    }
}
