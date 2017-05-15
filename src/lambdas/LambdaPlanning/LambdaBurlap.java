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

        String preds = "src/CleanupPredicatesNew.scm";

        JScheme js = new JScheme();

        try{
            FileReader f = new java.io.FileReader(preds);
            js.load(f);

            //define determiner with respect to initial state

            js.setGlobalValue("initialState", (CleanupState)s);

            //definite article
            js.eval("(define the (lambda (predicate) (satisfiesPredicate predicate initialState)))");

            //is the grounding being called every time?
            //depends on how the evaluation works
            String pred = "(lambda (state) (in (the basket) (the (lambda (x s) (and (red x s) (room x s) ))) state))";

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

            //TODO: change this path
            Visualizer v = CleanupVisualiser.getVisualizer("/Users/edwardwilliams/Documents/research/amdp/data/resources/robotImages");

            new EpisodeSequenceVisualizer(v, domain, Arrays.asList(ea));



        }catch (FileNotFoundException e){
            System.out.println(e.getMessage());
        }


    }


}
