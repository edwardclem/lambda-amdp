/**
 * Created by edwardwilliams on 5/10/17.
 */

import amdp.cleanup.CleanupDomain;
import amdp.cleanup.state.*;
import burlap.mdp.core.state.State;
import jscheme.*;
import jsint.Evaluator;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class LambdaTest {

    public static void main(String [] args){
        //create CleanupDomain state


        State s = CleanupDomain.getClassicState(true);
        //cast to CleanupState
        CleanupState state = (CleanupState)s;

        //System.out.println(state.objects());

        String PredLoc = "src/CleanupPredicates.scm";

        //testing interface between JScheme and Java objects

        //create JScheme evaluator
        JScheme js = new JScheme();
        try{
            FileReader f = new java.io.FileReader(PredLoc);
            js.load(f);
            System.out.println("loaded JScheme predicates from " + PredLoc);

            CleanupDoor door1 = state.doors.get(0);
            CleanupAgent agent = state.agent;
            CleanupRoom room1 = state.rooms.get(0);
            CleanupRoom room2 = state.rooms.get(1);
            CleanupRoom room3 = state.rooms.get(2);
            CleanupBlock block1 = state.blocks.get(0);

//            System.out.println("object properties");
//
//
//            System.out.println(js.call("door", door1));
//
//            System.out.println(js.call("block", block1));
//            System.out.println(js.call("door", agent));
//            System.out.println(js.call("red", room1));
//            System.out.println(js.call("red", room2));
//            System.out.println("room locations: ");
//            System.out.println(js.call("in", agent, room1));
//            System.out.println(js.call("in", agent, room2));
//            System.out.println(js.call("in", agent, room3));

            //System.out.println("testing list iteration");
            //TODO: modify state such that it stores list of objects
            //rather than constructing it on each call
            //ALTERNATIVELY: use object type predicates to inform determiner


            System.out.println("testing determiners");

            //System.out.println(agent.variableKeys().contains("colour"));

            System.out.println(js.call("red", agent));

            //!!!!!!!! set the state this way
            //maybe set a global CurrentState?
            //or define the SchemeProcedure here
            js.setGlobalValue("state", state);

            //System.out.println(js.eval("(getStateObjects state)"));

            js.eval("(define the (determiner state))");

            //System.out.println(js.eval("(the red)"));

            //testing composition of functions
            System.out.println(js.eval("(in (the agent) (the (lambda (x) (and (green x) (room x)))))"));

            System.out.println(js.eval("((lambda (x) (the x)) (lambda (x) (agent x)))"));
            System.out.println(js.eval("(the agent)"));

            //System.out.println(js.eval("(getAgent (.objects state))"));


            //System.out.println(determiner);

            //System.out.println(js.call((SchemeProcedure)determiner), (SchemeProcedure)"agent");

        } catch (FileNotFoundException e){
            System.out.println(e.getMessage());
        }


    }

}
