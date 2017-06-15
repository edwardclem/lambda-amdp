package lambdas.LambdaPlanning; /**
 * Created by edwardwilliams on 5/10/17.
 */

import amdp.cleanup.CleanupDomain;
import amdp.cleanup.state.CleanupState;
import burlap.mdp.core.state.State;
import jscheme.JScheme;
import jscheme.SchemeProcedure;
import jsint.Scheme;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LambdaTest3 {

    public static void main(String [] args){
        //create CleanupDomain state


        State s = CleanupDomain.getClassicState(true);
        //cast to CleanupState
        CleanupState state = (CleanupState)s;

        //System.out.println(state.objects());

        //load latest predicates
        String PredLoc = "src/CleanupPredicates3.scm";


        //create JScheme evaluator
        JScheme js = new JScheme();
        try{
            FileReader f = new FileReader(PredLoc);
            js.load(f);
            System.out.println("loaded JScheme predicates from " + PredLoc);

            //loading state into JScheme evaluator
            //js.setGlobalValue("initialState", state);

            //test arity one predicates

            System.out.println(js.call((SchemeProcedure)js.eval("(agent {agent0})"), state));

            System.out.println(js.call((SchemeProcedure)js.eval("(room {room0})"), state));

            System.out.println(js.call((SchemeProcedure)js.eval("(red {room0})"), state));

            //test and function

            SchemeProcedure and_pred = (SchemeProcedure)js.eval("(and_state (blue {room0}) (room {room0}))");

            System.out.println(js.call(and_pred, state));

            //testing definite determiner

            SchemeProcedure the = (SchemeProcedure)js.call("satisfiesPredicate", state);

            SchemeProcedure agent = (SchemeProcedure)js.eval("agent");


            System.out.println(js.call(the, agent));

            //testing spatial functions

            SchemeProcedure in = (SchemeProcedure)js.call("in", "agent0", "room1");

            System.out.println(js.call(in, state));

            //testing distance function

            SchemeProcedure dist = (SchemeProcedure)js.call("dist", "room2", "room1");

            System.out.println(js.call(dist, state));

            //testing room size function

            SchemeProcedure rsize = (SchemeProcedure)js.eval("(roomSize {room0})");

            System.out.println(js.call(rsize, state));


        } catch (FileNotFoundException e){
            System.out.println(e.getMessage());
        }


    }

}
