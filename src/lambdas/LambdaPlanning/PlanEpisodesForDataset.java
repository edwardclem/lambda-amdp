package lambdas.LambdaPlanning;

import amdp.cleanup.CleanupDomain;
import amdp.cleanup.CleanupVisualiser;
import amdp.cleanup.FixedDoorCleanupEnv;
import amdp.cleanup.state.CleanupAgent;
import amdp.cleanup.state.CleanupBlock;
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
import burlap.shell.visual.VisualExplorer;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import burlap.visualizer.Visualizer;
import data.DataHelpers;
import jscheme.JScheme;

import java.io.*;
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

        StateConditionTest l0sc = new StateConditionTest() {
            @Override
            public boolean satisfies(State state) {
                return false;
            }
        };



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



        String fileName = "/home/nakul/workspace/lambda-amdp/data/amt/amt_allforward/trainFixed.bdm";

        try{
//            FileReader f = new java.io.FileReader(preds);
//            js.load(f);

            String basePath = "/home/nakul/workspace/lambda-amdp/data/episodesFromDataTrain/";

            try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(basePath+"BDMWithEpisodeFilePath.txt"), "utf-8"))) {


                FileReader fileReader = new java.io.FileReader(fileName);

                BufferedReader br = new BufferedReader(fileReader);

                BDMReader reader = new BDMReader(br);



                boolean test = true;

                int count = 0;

                List<Episode> episodes = new ArrayList<>();

                while (test) {
//                test=false;
                    //return next four lines!

                    List<String> lines = reader.readNextFourLines();
                    if (reader.endOfFile) break;
//                System.out.println("0: " +lines.get(0));
//                System.out.println("1: " +lines.get(1));
//                System.out.println("2: " +lines.get(2));
//                System.out.println("3: " +lines.get(3));
                    State startState = DataHelpers.loadStateFromStringCompact(lines.get(1));
                    State terminationState = DataHelpers.loadStateFromStringCompact(lines.get(2));

                    if(lines.get(0).toUpperCase().matches("^[A-Z].*$")){
                        writer.write("\n");
                    }
                    writer.write(lines.get(0));
                    writer.write("\n");
                    writer.write(lines.get(1));
                    writer.write("\n");
                    writer.write(lines.get(2));
                    writer.write("\n");
                    writer.write(lines.get(3));
                    writer.write("\n");
                    writer.write(basePath+count+".episode");
                    writer.write("\n");

                    Episode e = PlanEpisodesForDataset.getEpisode(startState, terminationState);
                    e.write(basePath + count);
                    episodes.add(e);
                    count++;


                }


//                Visualizer v = CleanupVisualiser.getVisualizer("data/robotImages");
//                new EpisodeSequenceVisualizer(v, domain, basePath);


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
//        Visualizer v = CleanupVisualiser.getVisualizer("data/robotImages");
//        new EpisodeSequenceVisualizer(v, domain, Arrays.asList(episode));
        return episode;
    }


    public static class StateEqualityBasedConditionTest implements StateConditionTest{
        CleanupState terminalState;
        CleanupAgent agent;
        List<CleanupBlock> blocks;
        public StateEqualityBasedConditionTest(State s){
            terminalState = (CleanupState)s;
            agent = terminalState.agent;
            blocks = terminalState.blocks;
        }

        @Override
        public boolean satisfies(State state) {
            CleanupState testState = (CleanupState)state;
            List<CleanupBlock> blocksTestState = testState.blocks;
            CleanupAgent agentTestState = testState.agent;

            boolean retBool = true;
            if(!(agentTestState.x==agent.x && agentTestState.y==agent.y)){return false;}
            for(CleanupBlock b:blocks){
                for(CleanupBlock bST:blocksTestState){
                    if(b.name.equals(bST.name)){
                        if(!(b.x==bST.x && b.y == bST.y)){
                            return false;
                        }
                    }
                }
            }
//            if(retBool){
//                System.out.println(retBool);
//            }


            return retBool;

        }
    }

}
