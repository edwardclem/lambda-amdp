package lambdas.LambdaTests;

import amdp.cleanup.CleanupDomain;
import amdp.cleanup.CleanupVisualiser;
import amdp.cleanup.FixedDoorCleanupEnv;
import amdp.cleanup.state.CleanupState;
import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.planning.stochastic.rtdp.BoundedRTDP;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.behavior.valuefunction.ConstantValueFunction;
import burlap.debugtools.RandomFactory;
import burlap.mdp.auxiliary.common.GoalConditionTF;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.common.GoalBasedRF;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import burlap.visualizer.Visualizer;
import data.DataHelpers;
import jscheme.JScheme;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Random;

import jscheme.SchemeProcedure;
import lambdas.LambdaInterface.*;
import lambdas.LambdaPlanning.LambdaConverter;

/**
 * Created by ngopalan
 *
 * Planning with BURLAP using a lambda-calculus prop function.
 */



public class TestStringLambdaWithStringState {

    public static void main(String[] args){

        Random rand = RandomFactory.getMapped(0);
//        State s = CleanupDomain.getClassicState(true);

        String preStateString = "{agent0 (agent): [x: {5} y: {6} direction: {south} ] , door0 (door): [locked: {0} top: {4} left: {6} bottom: {4} right: {6} canBeLocked: {false} ] , door1 (door): [locked: {0} top: {4} left: {2} bottom: {4} right: {2} canBeLocked: {false} ] , block0 (block): [x: {2} y: {6} shape: {chair} colour: {green} ] , room0 (room): [top: {4} left: {0} bottom: {0} right: {8} colour: {green} ] , room1 (room): [top: {8} left: {0} bottom: {4} right: {4} colour: {blue} ] , room2 (room): [top: {8} left: {4} bottom: {4} right: {8} colour: {red} ] , }";

        String leftPostcondition = "{agent0 (agent): [x: {2} y: {6} direction: {south} ] , door0 (door): [locked: {0} top: {4} left: {6} bottom: {4} right: {6} canBeLocked: {false} ] , door1 (door): [locked: {0} top: {4} left: {2} bottom: {4} right: {2} canBeLocked: {false} ] , block0 (block): [x: {3} y: {6} shape: {chair} colour: {red} ] , room0 (room): [top: {8} left: {0} bottom: {4} right: {8} colour: {blue} ] , room1 (room): [top: {4} left: {0} bottom: {0} right: {4} colour: {green} ] , room2 (room): [top: {4} left: {4} bottom: {0} right: {8} colour: {red} ] , }";

        String rightPostcondition =  "{agent0 (agent): [x: {6} y: {2} direction: {south} ] , door0 (door): [locked: {0} top: {4} left: {6} bottom: {4} right: {6} canBeLocked: {false} ] , door1 (door): [locked: {0} top: {4} left: {2} bottom: {4} right: {2} canBeLocked: {false} ] , block0 (block): [x: {2} y: {6} shape: {chair} colour: {blue} ] , block1 (block): [x: {5} y: {2} shape: {chair} colour: {red} ] , room0 (room): [top: {4} left: {0} bottom: {0} right: {8} colour: {blue} ] , room1 (room): [top: {8} left: {0} bottom: {4} right: {4} colour: {green} ] , room2 (room): [top: {8} left: {4} bottom: {4} right: {8} colour: {red} ] , }";


        //State s = DataHelpers.loadStateFromStringCompact(preStateString);

        State s = DataHelpers.loadStateFromStringCompact(rightPostcondition);

        //create JScheme evaluator

        String preds = "src/CleanupPredicates.scm";

        JScheme js = new JScheme();

        try{
            FileReader f = new java.io.FileReader(preds);
            js.load(f);

            //define determiner with respect to initial state

            js.setGlobalValue("initialState", (CleanupState)s);

            //definite article

            js.eval("(define the (satisfiesPredicate initialState))");

            //is the determiner being called on every application of the LambdaSC? or just once when the predicate is evaluated?
            //depends on how the evaluation works
            String parsedCommand = "(in:<e,<e,t>> (the:<<e,t>,e> (lambda $0:e (agent:<e,t> $0))) (the:<<e,t>,e> (lambda $1:e (eq:<e,<e,t>> $1 (argmax:<<e,t>,<<e,n>,e>> (lambda $2:e (green:<e,t> $2)) (lambda $3:e (size:<e,n> $3)))))))";
            //parsedCommand =  "(and:<t*,t> (in:<e,<e,t>> (the:<<e,t>,e> (lambda $0:e (agent:<e,t> $0))) (the:<<e,t>,e> (lambda $1:e (and:<t*,t> (blue:<e,t> $1) (room:<e,t> $1))))) (in:<e,<e,t>> (the:<<e,t>,e> (lambda $2:e (block:<e,t> $2))) (the:<<e,t>,e> (lambda $3:e (and:<t*,t> (green:<e,t> $3) (room:<e,t> $3))))))";
            String pred = LambdaConverter.convert(parsedCommand);
            System.out.println(pred);
            //System.out.println(js.eval(pred));

            SchemeProcedure pf = (SchemeProcedure)js.eval(pred);

            LambdaSC l0sc = new LambdaSC(pf, js);
            GoalBasedRF l0rf = new GoalBasedRF(l0sc, 1., 0.);
            GoalConditionTF l0tf = new GoalConditionTF(l0sc);
            CleanupDomain dgen = new CleanupDomain(l0rf,l0tf, rand);
            dgen.includeDirectionAttribute(true);
            dgen.includePullAction(true);
            dgen.includeWallPF_s(true);
            dgen.includeLockableDoors(true);
            dgen.setLockProbability(0.0);
            OOSADomain domain = dgen.generateDomain();

            FixedDoorCleanupEnv env = new FixedDoorCleanupEnv(domain, s);

            long startTime = System.currentTimeMillis();

            ConstantValueFunction heuristic = new ConstantValueFunction(1.);
            BoundedRTDP brtd = new BoundedRTDP(domain, 0.99, new SimpleHashableStateFactory(false), new ConstantValueFunction(0.0), heuristic, 0.01, 500);
            brtd.setMaxRolloutDepth(50);
            brtd.toggleDebugPrinting(false);
            Policy P = brtd.planFromState(s);

//            ValueIteration vi = new ValueIteration(domain, 0.99,new SimpleHashableStateFactory(false),0.01, 500);
//            Policy P = vi.planFromState(s);

            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime);

            System.out.println("total time: " + duration);

            Episode ea = PolicyUtils.rollout(P,env,100);

            Visualizer v = CleanupVisualiser.getVisualizer("data/robotImages");

            new EpisodeSequenceVisualizer(v, domain, Arrays.asList(ea));



        }catch (FileNotFoundException e){
            System.out.println(e.getMessage());
        }


    }


}
