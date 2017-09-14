package validation;

import amdp.cleanup.CleanupDomain;
import amdp.cleanup.FixedDoorCleanupEnv;
import amdp.cleanup.state.CleanupState;
import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.planning.stochastic.rtdp.BoundedRTDP;
import burlap.behavior.valuefunction.ConstantValueFunction;
import burlap.debugtools.RandomFactory;
import burlap.mdp.auxiliary.common.GoalConditionTF;
import burlap.mdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.common.GoalBasedRF;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import data.DataHelpers;
import edu.cornell.cs.nlp.spf.data.ILabeledDataItem;
import edu.cornell.cs.nlp.spf.data.sentence.Sentence;
import edu.cornell.cs.nlp.spf.data.utils.IValidator;
import edu.cornell.cs.nlp.spf.explat.IResourceRepository;
import edu.cornell.cs.nlp.spf.explat.ParameterizedExperiment;
import edu.cornell.cs.nlp.spf.explat.resources.IResourceObjectCreator;
import edu.cornell.cs.nlp.spf.explat.resources.usage.ResourceUsage;
import edu.cornell.cs.nlp.spf.mr.lambda.LogicalExpression;
import edu.cornell.cs.nlp.utils.composites.Triplet;
import edu.cornell.cs.nlp.utils.log.ILogger;
import edu.cornell.cs.nlp.utils.log.LoggerFactory;
import jscheme.JScheme;
import jscheme.SchemeProcedure;
import lambdas.LambdaInterface.LambdaSC;
import lambdas.LambdaPlanning.LambdaConverter;
import lambdas.LambdaPlanning.PlanEpisodesForDataset;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BurlapTerminalStatePlanValidator<DI extends ILabeledDataItem<Sentence,List<Episode>>, MR extends LogicalExpression> implements IValidator<DI, MR> {

    private static JScheme js;
    //updated to use java-based predicates
    //TODO: load this from the ExPlat file
    private String preds = "src/CleanupPredicatesJava.scm";
    public static final ILogger LOG = LoggerFactory.create(BurlapTerminalStatePlanValidator.class);

    public void setMaxPlanLength(int maxPlanLength) {
        this.maxPlanLength=maxPlanLength;
    }

    public int maxPlanLength = 1;

    public BurlapTerminalStatePlanValidator() {
        js = new JScheme();

        try{
            FileReader f = new java.io.FileReader(preds);
            js.load(f);

        } catch (FileNotFoundException e) {
            System.out.println("ERROR");
        }
    }


    @Override
    public boolean isValid(DI dataItem, MR var1) {

        String parse_string = var1.toString();

        //LOG.info("Starting validation of " + dataItem.getSample().toString() + " with form " + var1.toString());

        if (!parse_string.contains("#")){
            String pred = LambdaConverter.convert(var1.toString());
            List<Episode> dataset = dataItem.getLabel();

            boolean retBool = true;

            for(Episode e : dataset) {
                CleanupState initialState = (CleanupState) e.stateSequence.get(0);
                js.setGlobalValue("initialState", initialState);
                //NOTE:using strict definite determiner
                js.eval("(define the (definiteDeterminer initialState))");
                //define argmin and argmax wrt initial state
                js.eval("(define argmax (argmaxState initialState))");
                js.eval("(define argmin (argminState initialState))");
//                CleanupState after = (CleanupState) triple.second();

//                StateConditionTest sc = new LambdaSC()

                SchemeProcedure pf = (SchemeProcedure) js.eval(pred);

                StateConditionTest l0sc = new LambdaSC(pf, js);
                Episode planOnEval = BurlapTerminalStatePlanValidator.getEpisode(initialState, l0sc,maxPlanLength);
                if (planOnEval.stateSequence.size() >= maxPlanLength) {
                    return false;
                }


                if (!l0sc.satisfies(e.stateSequence.get(e.stateSequence.size() - 1)) || !l0sc.satisfies(planOnEval.stateSequence.get(planOnEval.stateSequence.size() - 1))) {
                    return false;
                }


            }

            return retBool;
        }
        else{
            return false;
        }
    }


    public static class Creator implements IResourceObjectCreator<BurlapTerminalStatePlanValidator> {

        @Override
        public BurlapTerminalStatePlanValidator create(ParameterizedExperiment.Parameters params, IResourceRepository repo){
            return new BurlapTerminalStatePlanValidator();
        }

        @Override
        public String type(){
            return "validator.burlap.plan.terminal.state";
        }

        @Override
        public ResourceUsage usage(){
            return new ResourceUsage.Builder(type(), BurlapTerminalStatePlanValidator.class)
                    .setDescription("validator that checks if the logical expression correctly evaluates on the provided set of pre - and post- condition states given in the planned episode.")
                    .build();
        }
    }


    public static Episode getEpisode(State startState, StateConditionTest l0sc, int maxPlanLength){



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
        brtd.setMaxRolloutDepth(maxPlanLength);
        brtd.toggleDebugPrinting(false);
        Policy policy = brtd.planFromState(startState);

        Episode episode = PolicyUtils.rollout(policy,env,maxPlanLength);
//        Visualizer v = CleanupVisualiser.getVisualizer("data/robotImages");
//        new EpisodeSequenceVisualizer(v, domain, Arrays.asList(episode));
        return episode;
    }


}
