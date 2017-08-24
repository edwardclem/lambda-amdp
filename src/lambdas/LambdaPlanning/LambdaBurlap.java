package lambdas.LambdaPlanning;

import amdp.cleanup.CleanupDomain;
import amdp.cleanup.CleanupVisualiser;
import amdp.cleanup.FixedDoorCleanupEnv;
import amdp.cleanup.state.CleanupState;
import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.planning.stochastic.rtdp.BoundedRTDP;
import burlap.behavior.valuefunction.ConstantValueFunction;
import burlap.debugtools.RandomFactory;
import burlap.mdp.auxiliary.common.GoalConditionTF;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.common.GoalBasedRF;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import burlap.visualizer.Visualizer;
import jscheme.JScheme;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Random;

import jscheme.SchemeProcedure;
import lambdas.LambdaInterface.*;

/**
 * Created by edwardwilliams on 5/15/17.
 *
 * Planning with BURLAP using a lambda-calculus prop function.
 */



public class LambdaBurlap {

    public static void main(String[] args){

        Random rand = RandomFactory.getMapped(0);
        State s = CleanupDomain.getClassicState(true);


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
            String parsedCommand = "(in:<e,<e,t>> (the:<<e,t>,e> (lambda $0:e (agent:<e,t> $0))) (the:<<e,t>,e> (lambda $1:e (and:<t*,t> (blue:<e,t> $1) (room:<e,t> $1)))))";
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
