package lambdas.LambdaPlanning; /**
 * Created by edwardwilliams on 5/10/17.
 */

import amdp.cleanup.CleanupDomain;
import amdp.cleanup.state.*;
import burlap.mdp.core.state.State;
import jscheme.*;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class LambdaTest {

    public static void main(String [] args){
        //create CleanupDomain state


        State s = CleanupDomain.getClassicState(true);
        //cast to CleanupState
        CleanupState state = (CleanupState)s;

        //System.out.println(state.objects());

        String PredLoc = "src/CleanupPredicatesNew.scm";


        //create JScheme evaluator
        JScheme js = new JScheme();
        try{
            FileReader f = new java.io.FileReader(PredLoc);
            js.load(f);
            System.out.println("loaded JScheme predicates from " + PredLoc);



            System.out.println(js.call("agent", "agent0", state));

            System.out.println(js.call("red", "agent0", state));

            System.out.println(js.call("in", "agent0", "room1", state));

            //System.out.println(js.call("getAgent", state));

            js.setGlobalValue("initialState", state);

            //define determiner with respect to initial state

            js.eval("(define the (lambda (predicate) (satisfiesPredicate predicate initialState)))");

            System.out.println(js.eval("(the (lambda (x s) (and (red x s) (room x s))))"));

            String pred = "(lambda (state) (in (the block) (the (lambda (x s) (and (blue x s) (room x s) ))) state))";

            SchemeProcedure pf = (SchemeProcedure)js.eval(pred);

            System.out.println(js.call(pf, state));





        } catch (FileNotFoundException e){
            System.out.println(e.getMessage());
        }


    }

}
