package lambdas.LambdaPlanning; /**
 * Created by edwardwilliams on 5/10/17.
 */

import amdp.cleanup.CleanupDomain;
import amdp.cleanup.state.CleanupState;
import burlap.mdp.core.state.State;
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
        String PredLoc = "src/CleanupPredicates.scm";


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

            SchemeProcedure dist = (SchemeProcedure)js.call("dist", "room2", "room1");

            //TODO: finish tests for this

            System.out.println(js.call(dist, state));

            //testing room size function

            SchemeProcedure rsize = (SchemeProcedure)js.eval("(roomSize {room0})");

            System.out.println(js.call(rsize, state));

            System.out.print("testing argmax... ");
            SchemeProcedure argmax = (SchemeProcedure)js.call("argmax", state);
            //TODO: more argmax tests
            Assert.assertEquals("testing argmax on rooms", "room0" ,js.call(argmax, js.eval("room"), js.eval("roomSize")));
            System.out.println("passed ");

        } catch (FileNotFoundException e){
            System.out.println(e.getMessage());
        }


    }

}
