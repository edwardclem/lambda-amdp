package lambdas.LambdaTests; /**
 * Created by edwardwilliams on 5/10/17.
 */

import amdp.cleanup.CleanupDomain;
import amdp.cleanup.state.CleanupState;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.state.State;
import data.DataHelpers;
import jscheme.JScheme;
import jscheme.SchemeProcedure;
import jsint.Scheme;
import org.junit.Assert;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LambdaTest {

    public static void main(String [] args){
        //create CleanupDomain state

        State s = CleanupDomain.getClassicState(true);
        //cast to CleanupState
        CleanupState state = (CleanupState)s;

        //System.out.println(state.objects());

        //load latest predicates
        String PredLoc = "src/CleanupPredicatesJava.scm";


        //create JScheme evaluator
        JScheme js = new JScheme();
        try{
            FileReader f = new FileReader(PredLoc);
            js.load(f);
            System.out.println("loaded JScheme predicates from " + PredLoc);

            //loading state into JScheme evaluator
            //js.setGlobalValue("initialState", state);

            //test arity one predicates
            System.out.print("testing agent predicate... ");
            Assert.assertTrue("agent predicate", (Boolean)js.call((SchemeProcedure)js.eval("(agent {agent0})"), state));
            System.out.println("passed");

            System.out.print("testing room predicate... ");
            Assert.assertTrue("room predicate on room0", (Boolean)js.call((SchemeProcedure)js.eval("(room {room0})"), state));
            Assert.assertTrue("room predicate on room1", (Boolean)js.call((SchemeProcedure)js.eval("(room {room1})"), state));
            Assert.assertTrue("room predicate on room2", (Boolean)js.call((SchemeProcedure)js.eval("(room {room2})"), state));
            System.out.println("passed");

            System.out.print("testing color predicates ... ");
            Assert.assertTrue("red predicate on room0", (Boolean)js.call((SchemeProcedure)js.eval("(red {room0})"), state));
            Assert.assertTrue("green predicate on room1", (Boolean)js.call((SchemeProcedure)js.eval("(green {room1})"), state));
            Assert.assertTrue("blue predicate on room2", (Boolean)js.call((SchemeProcedure)js.eval("(blue {room2})"), state));
            System.out.println("passed");

            System.out.print("testing basket predicate... ");
            Assert.assertTrue("basket predicate on block", (Boolean)js.call((SchemeProcedure)js.eval("(basket {block0})"), state));
            System.out.println("passed");
            //test and function

            System.out.print("testing state-dependent and predicate... ");
            SchemeProcedure and_pred = (SchemeProcedure)js.eval("(and_ (red {room0}) (room {room0}))");

            Assert.assertTrue("room0 is red and room", (Boolean)js.call(and_pred, state));
            System.out.println("passed");
            //testing definite determiner

            SchemeProcedure the = (SchemeProcedure)js.call("satisfiesPredicate", state);

            SchemeProcedure agent = (SchemeProcedure)js.eval("agent");

            System.out.print("testing determiner... ");

            //System.out.println(js.call(agent, "agent0"));


            Assert.assertEquals("Determiner on agent", "agent0", js.call(the, agent));
            Assert.assertEquals("Determiner on blue room", "room2", js.call(the,(SchemeProcedure)js.eval("(lambda ($1) (and_ (blue $1) (room $1)))")));
            Assert.assertEquals("Determiner on red room", "room0", js.call(the,(SchemeProcedure)js.eval("(lambda ($1) (and_ (red $1) (room $1)))")));
            Assert.assertEquals("Determiner on green room", "room1", js.call(the,(SchemeProcedure)js.eval("(lambda ($1) (and_ (green $1) (room $1)))")));
            System.out.println("passed");

            //testing spatial functions

            SchemeProcedure in = (SchemeProcedure)js.call("in", "agent0", "room1");
            System.out.print("testing in function...");
            Assert.assertTrue("agent begins in room1",  (Boolean)js.call(in, state));
            System.out.println("passed");

            //testing distance function

            System.out.print("testing distance function...");
            SchemeProcedure dist = (SchemeProcedure)js.call("dist", "room2", "room1");

            //TODO: finish tests for this

            //System.out.println(js.call(dist, state));

            Assert.assertEquals("distance between room centers is 4.0", 4.0, js.call(dist, state));

            System.out.println("passed");


            //TODO: expand tests
            System.out.print("testing near predicate...");
            String nearPrecondition = "{agent0 (agent): [x: {5} y: {6} direction: {south} ] , door0 (door): [locked: {0} top: {4} left: {6} bottom: {4} right: {6} canBeLocked: {false} ] , door1 (door): [locked: {0} top: {4} left: {2} bottom: {4} right: {2} canBeLocked: {false} ] , block0 (block): [x: {2} y: {6} shape: {chair} colour: {green} ] , room0 (room): [top: {4} left: {0} bottom: {0} right: {8} colour: {green} ] , room1 (room): [top: {8} left: {0} bottom: {4} right: {4} colour: {blue} ] , room2 (room): [top: {8} left: {4} bottom: {4} right: {8} colour: {red} ] , }";
            String nearPostcondition = "{agent0 (agent): [x: {3} y: {6} direction: {south} ] , door0 (door): [locked: {0} top: {4} left: {6} bottom: {4} right: {6} canBeLocked: {false} ] , door1 (door): [locked: {0} top: {4} left: {2} bottom: {4} right: {2} canBeLocked: {false} ] , block0 (block): [x: {2} y: {6} shape: {chair} colour: {green} ] , room0 (room): [top: {4} left: {0} bottom: {0} right: {8} colour: {green} ] , room1 (room): [top: {8} left: {0} bottom: {4} right: {4} colour: {blue} ] , room2 (room): [top: {8} left: {4} bottom: {4} right: {8} colour: {red} ] , }";

            CleanupState nearPreState = DataHelpers.loadStateFromStringCompact(nearPrecondition);
            CleanupState nearPostState = DataHelpers.loadStateFromStringCompact(nearPostcondition);

            Assert.assertTrue("agent is not near the block in provided precondition", !(Boolean)js.call((SchemeProcedure)js.eval("(near {agent0} {block0})"), nearPreState));
            Assert.assertTrue("agent is near block in postcondition", (Boolean)js.call((SchemeProcedure)js.eval("(near {agent0} {block0})"), nearPostState));
            System.out.println("passed");

            System.out.print("testing left predicate...");

            String leftPrecondition = "{agent0 (agent): [x: {4} y: {6} direction: {south} ] , door0 (door): [locked: {0} top: {4} left: {6} bottom: {4} right: {6} canBeLocked: {false} ] , door1 (door): [locked: {0} top: {4} left: {2} bottom: {4} right: {2} canBeLocked: {false} ] , block0 (block): [x: {3} y: {6} shape: {chair} colour: {red} ] , room0 (room): [top: {8} left: {0} bottom: {4} right: {8} colour: {blue} ] , room1 (room): [top: {4} left: {0} bottom: {0} right: {4} colour: {green} ] , room2 (room): [top: {4} left: {4} bottom: {0} right: {8} colour: {red} ] , }";
            String leftPostcondition = "{agent0 (agent): [x: {2} y: {6} direction: {south} ] , door0 (door): [locked: {0} top: {4} left: {6} bottom: {4} right: {6} canBeLocked: {false} ] , door1 (door): [locked: {0} top: {4} left: {2} bottom: {4} right: {2} canBeLocked: {false} ] , block0 (block): [x: {3} y: {6} shape: {chair} colour: {red} ] , room0 (room): [top: {8} left: {0} bottom: {4} right: {8} colour: {blue} ] , room1 (room): [top: {4} left: {0} bottom: {0} right: {4} colour: {green} ] , room2 (room): [top: {4} left: {4} bottom: {0} right: {8} colour: {red} ] , }";

            CleanupState leftPreState = DataHelpers.loadStateFromStringCompact(leftPrecondition);
            CleanupState leftPostState = DataHelpers.loadStateFromStringCompact(leftPostcondition);

            Assert.assertTrue("agent is not left of the block in provided precondition", !(Boolean)js.call((SchemeProcedure)js.eval("(left {agent0} {block0})"), leftPreState));
            Assert.assertTrue("agent is left of block in postcondition", (Boolean)js.call((SchemeProcedure)js.eval("(left {agent0} {block0})"), leftPostState));
            System.out.println("passed");

            System.out.print("testing right predicate...");

            String rightPrecondition = "{agent0 (agent): [x: {4} y: {2} direction: {south} ] , door0 (door): [locked: {0} top: {4} left: {6} bottom: {4} right: {6} canBeLocked: {false} ] , door1 (door): [locked: {0} top: {4} left: {2} bottom: {4} right: {2} canBeLocked: {false} ] , block0 (block): [x: {2} y: {6} shape: {chair} colour: {blue} ] , block1 (block): [x: {5} y: {2} shape: {chair} colour: {red} ] , room0 (room): [top: {4} left: {0} bottom: {0} right: {8} colour: {blue} ] , room1 (room): [top: {8} left: {0} bottom: {4} right: {4} colour: {green} ] , room2 (room): [top: {8} left: {4} bottom: {4} right: {8} colour: {red} ] , }";
            String rightPostcondition =  "{agent0 (agent): [x: {6} y: {2} direction: {south} ] , door0 (door): [locked: {0} top: {4} left: {6} bottom: {4} right: {6} canBeLocked: {false} ] , door1 (door): [locked: {0} top: {4} left: {2} bottom: {4} right: {2} canBeLocked: {false} ] , block0 (block): [x: {2} y: {6} shape: {chair} colour: {blue} ] , block1 (block): [x: {5} y: {2} shape: {chair} colour: {red} ] , room0 (room): [top: {4} left: {0} bottom: {0} right: {8} colour: {blue} ] , room1 (room): [top: {8} left: {0} bottom: {4} right: {4} colour: {green} ] , room2 (room): [top: {8} left: {4} bottom: {4} right: {8} colour: {red} ] , }";

            CleanupState rightPreState = DataHelpers.loadStateFromStringCompact(rightPrecondition);
            CleanupState rightPostState = DataHelpers.loadStateFromStringCompact(rightPostcondition);

            Assert.assertTrue("agent is not right of the block in provided precondition", !(Boolean)js.call((SchemeProcedure)js.eval("(right {agent0} {block1})"), rightPreState));
            Assert.assertTrue("agent is right of block in postcondition", (Boolean)js.call((SchemeProcedure)js.eval("(right {agent0} {block1})"), rightPostState));
            System.out.println("passed");

            //testing size function

            System.out.print("testing size function...");

            Assert.assertEquals("room 0 size is 32", 32.0, js.call((SchemeProcedure)js.eval("(size {room0})"), state));
            Assert.assertEquals("room 1 size is 16", 16.0, js.call((SchemeProcedure)js.eval("(size {room1})"), state));
            Assert.assertEquals("room 2 size is 16", 16.0, js.call((SchemeProcedure)js.eval("(size {room2})"), state));

            Assert.assertEquals("block size is 2, no 'big' or 'small' specified", 2.0, js.call((SchemeProcedure)js.eval("(size {block0})"), state));

            System.out.println("passed");
//

            //TODO: expand tests here
            System.out.print("testing room size argmax... ");
            SchemeProcedure argmax = (SchemeProcedure)js.call("argmaxState", state);


            Assert.assertEquals("room0 is biggest", "room0" ,js.call(argmax, js.eval("room"), js.eval("size")));



            System.out.println("passed");

            System.out.print("testing room size argmin...");
            SchemeProcedure argmin = (SchemeProcedure)js.call("argminState", state);

            Assert.assertTrue("room 1 or room2 are argmin", js.call(argmin, js.eval("room"), js.eval("size")).equals("room1") || js.call(argmin, js.eval("room"), js.eval("size")).equals("room2") );
            System.out.println("passed");
//
//
//            //testing null function
//
//            SchemeProcedure yellow = (SchemeProcedure)js.eval("yellow");
//
//            System.out.println(js.call(the, yellow).equals(""));
//
//            //defining determiner wrt initial state:
//            js.setGlobalValue("initialState", state);
//            js.eval("(define the (satisfiesPredicate initialState))");
//
//            SchemeProcedure inNull = (SchemeProcedure)js.eval("(in (the yellow) (the red))");
//
//            System.out.println(js.call(inNull, state));

        } catch (FileNotFoundException e){
            System.out.println(e.getMessage());
        }


    }

}
