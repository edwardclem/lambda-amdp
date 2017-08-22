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
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.behavior.valuefunction.ConstantValueFunction;
import burlap.debugtools.RandomFactory;
import burlap.mdp.auxiliary.common.GoalConditionTF;
import burlap.mdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.common.GoalBasedRF;
import burlap.mdp.singleagent.environment.Environment;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import burlap.visualizer.Visualizer;
import data.DataHelpers;
import jscheme.JScheme;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import jscheme.SchemeProcedure;
import lambdas.LambdaInterface.*;

/**
 * Created by ngopalan
 *
 * Planning with BURLAP using a lambda-calculus prop function.
 */



public class PlanEpisodesForDataset {


    public static void main(String[] args){

//        State s = CleanupDomain.getClassicState(true);

//        String preStateString = "{agent0 (agent): [x: {5} y: {6} direction: {south} ] , door0 (door): [locked: {0} top: {4} left: {6} bottom: {4} right: {6} canBeLocked: {false} ] , door1 (door): [locked: {0} top: {4} left: {2} bottom: {4} right: {2} canBeLocked: {false} ] , block0 (block): [x: {2} y: {6} shape: {chair} colour: {green} ] , room0 (room): [top: {4} left: {0} bottom: {0} right: {8} colour: {green} ] , room1 (room): [top: {8} left: {0} bottom: {4} right: {4} colour: {blue} ] , room2 (room): [top: {8} left: {4} bottom: {4} right: {8} colour: {red} ] , }";
//
//        State s = DataHelpers.loadStateFromStringCompact(preStateString);
//
//        //create JScheme evaluator
//
//        String preds = "src/CleanupPredicates.scm";
//
//        JScheme js = new JScheme();

        String fileName = "/home/nakul/workspace/lambda-amdp/data/amt/amt_allforward/deleteThisFile.bdm";

        try{
//            FileReader f = new java.io.FileReader(preds);
//            js.load(f);



            FileReader fileReader = new java.io.FileReader(fileName);

            BufferedReader br = new BufferedReader(fileReader);

            BDMReader reader = new BDMReader(br);

            while (true){
                //return next four lines!
                List<String> lines = reader.readNextFourLines();
                if(reader.endOfFile) break;
//                System.out.println("0: " +lines.get(0));
//                System.out.println("1: " +lines.get(1));
//                System.out.println("2: " +lines.get(2));
//                System.out.println("3: " +lines.get(3));
                State startState = DataHelpers.loadStateFromStringCompact(lines.get(1));
                State terminationState = DataHelpers.loadStateFromStringCompact(lines.get(1));
                Episode e = PlanEpisodesForDataset.getEpisode(startState,terminationState);
//                e.write();


                Visualizer v = CleanupVisualiser.getVisualizer("data/robotImages");

                StateConditionTest l0sc = new StateEqualityBasedConditionTest(terminationState);


                GoalBasedRF l0rf = new GoalBasedRF(l0sc, 1., 0.);
                GoalConditionTF l0tf = new GoalConditionTF(l0sc);



                Random rand = RandomFactory.getMapped(0);
                CleanupDomain dgen = new CleanupDomain(l0rf,l0tf, rand);
                dgen.includeDirectionAttribute(true);
                dgen.includePullAction(true);
                dgen.includeWallPF_s(true);
                dgen.includeLockableDoors(true);
                dgen.setLockProbability(0.0);
                OOSADomain domain = dgen.generateDomain();

                new EpisodeSequenceVisualizer(v, domain, Arrays.asList(e));

            }





        }catch (FileNotFoundException e){
            System.out.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }




    public static class BDMReader{
        BufferedReader br;
        public boolean endOfFile=false;
        public BDMReader(BufferedReader br){
            this.br=br;
        }

        public List<String> readNextFourLines() throws IOException {

            ArrayList<String> stringList = new ArrayList<>();

            int count=0;


            String s=null;

            while (count<4) {
                s = br.readLine();
                if(s == null){
                    endOfFile=true;
                    break;
                }


                if (!s.equals("")) {
                        stringList.add(s);
                        count++;
                    }
                }




            return stringList;
        }
    }




    public static Episode getEpisode(State startState, State terminalState){


        StateConditionTest l0sc = new StateEqualityBasedConditionTest(terminalState);


        GoalBasedRF l0rf = new GoalBasedRF(l0sc, 1., 0.);
        GoalConditionTF l0tf = new GoalConditionTF(l0sc);



        Random rand = RandomFactory.getMapped(0);
        CleanupDomain dgen = new CleanupDomain(l0rf,l0tf, rand);
        dgen.includeDirectionAttribute(true);
        dgen.includePullAction(true);
        dgen.includeWallPF_s(true);
        dgen.includeLockableDoors(true);
        dgen.setLockProbability(0.0);
        OOSADomain domain = dgen.generateDomain();

        FixedDoorCleanupEnv env = new FixedDoorCleanupEnv(domain,startState);
//        ValueIteration vi = new ValueIteration(domain, 0.99,new SimpleHashableStateFactory(false),0.01, 5000);
//        Policy policy = vi.planFromState(startState);
        ConstantValueFunction heuristic = new ConstantValueFunction(1.);
        BoundedRTDP brtd = new BoundedRTDP(domain, 0.99, new SimpleHashableStateFactory(false), new ConstantValueFunction(0.0), heuristic, 0.01, 500);
        brtd.setMaxRolloutDepth(50);
        brtd.toggleDebugPrinting(false);
        Policy policy = brtd.planFromState(startState);

        Episode episode = PolicyUtils.rollout(policy,env,100);
        return episode;
    }


    public static class StateEqualityBasedConditionTest implements StateConditionTest{
        State terminalState;
        public StateEqualityBasedConditionTest(State s){
            terminalState = s;
        }

        @Override
        public boolean satisfies(State state) {
            return terminalState.equals(state);
        }
    }

}
