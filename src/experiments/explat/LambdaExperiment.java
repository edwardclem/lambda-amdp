package experiments.explat;

import burlap.mdp.core.state.State;
import data.archive.BurlapDemonstration;
import edu.cornell.cs.nlp.spf.base.hashvector.HashVectorFactory;
import edu.cornell.cs.nlp.spf.ccg.lexicon.LexicalEntry;
import edu.cornell.cs.nlp.spf.ccg.lexicon.Lexicon;
import edu.cornell.cs.nlp.spf.ccg.lexicon.factored.lambda.FactoredLexicalEntry;
import edu.cornell.cs.nlp.spf.ccg.lexicon.factored.lambda.FactoringServices;
import edu.cornell.cs.nlp.spf.data.sentence.Sentence;
import edu.cornell.cs.nlp.spf.explat.DistributedExperiment;
import edu.cornell.cs.nlp.spf.explat.Job;
import edu.cornell.cs.nlp.spf.explat.resources.ResourceCreatorRepository;
import edu.cornell.cs.nlp.spf.learn.ILearner;
import edu.cornell.cs.nlp.spf.mr.lambda.FlexibleTypeComparator;
import edu.cornell.cs.nlp.spf.mr.lambda.LogicLanguageServices;
import edu.cornell.cs.nlp.spf.mr.lambda.LogicalConstant;
import edu.cornell.cs.nlp.spf.mr.lambda.LogicalExpression;
import edu.cornell.cs.nlp.spf.mr.lambda.ccg.LogicalExpressionCategoryServices;
import edu.cornell.cs.nlp.spf.mr.language.type.TypeRepository;
import edu.cornell.cs.nlp.spf.parser.ccg.model.IModelImmutable;
import edu.cornell.cs.nlp.spf.parser.ccg.model.IModelInit;
import edu.cornell.cs.nlp.spf.parser.ccg.model.Model;
import edu.cornell.cs.nlp.spf.parser.ccg.model.ModelLogger;
import edu.cornell.cs.nlp.utils.collections.ListUtils;
import edu.cornell.cs.nlp.utils.composites.Pair;
import edu.cornell.cs.nlp.utils.log.ILogger;
import edu.cornell.cs.nlp.utils.log.LogLevel;
import edu.cornell.cs.nlp.utils.log.LoggerFactory;
import org.xml.sax.SAXException;
import test.ValidationTester;
import test.stats.ValidationTestingStatistics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Created by edwardwilliams on 6/22/17.
 */
public class LambdaExperiment extends DistributedExperiment {

    public static final ILogger LOGGER = LoggerFactory.create(LambdaExperiment.class);
    private final LogicalExpressionCategoryServices categoryServices;

    public LambdaExperiment(File expFile) throws IOException, SAXException{
        this(expFile,  Collections.<String, String> emptyMap(), new LambdaResourceRepo());
    }

    public LambdaExperiment(File expFile, Map<String, String> envParams, ResourceCreatorRepository repo) throws IOException, SAXException{
        super(expFile, envParams, repo);

        LogLevel.DEV.set(); //there appears to be a crash when debug logging happens.

        //Loading global parameters

        final File typesFile = globalParams.getAsFile("types");
        final File seedLexFile = globalParams.getAsFile("seedlex"); //only one file

        //tree hash vector? not sure what this does

        HashVectorFactory.DEFAULT = HashVectorFactory.Type.TREE;

        //initialize ontology

        final File ontologyFile = globalParams.getAsFile("ont");

       // System.out.println(ontologyFile);

        //initialize logical expression type system

        try{
            LogicLanguageServices.setInstance(new LogicLanguageServices.Builder(new TypeRepository(typesFile), new FlexibleTypeComparator())
                    .setNumeralTypeName("n")
                    .setUseOntology(true)
                    .addConstantsToOntology(ontologyFile)
                    .closeOntology(true)
                    .build()); //ontology may need to be opened for attribute learning.

            //add to set of resources.
            storeResource(ONTOLOGY_RESOURCE, LogicLanguageServices.getOntology());
        } catch (final IOException e){
            throw new RuntimeException(e);
        }

        //initialize category services.

        this.categoryServices = new LogicalExpressionCategoryServices(true);
        storeResource(CATEGORY_SERVICES_RESOURCE, categoryServices);

        //lexical factoring

        FactoringServices.set(new FactoringServices.Builder()
                .addConstant(LogicalConstant.read("exists:<<e,t>,t>"))
                .addConstant(LogicalConstant.read("the:<<e,t>,e>")).build());


        //create set of lexical entries, then factor:

        final Lexicon<LogicalExpression> readLexicon = new Lexicon<>();
        readLexicon.addEntriesFromFile(seedLexFile, categoryServices, LexicalEntry.Origin.FIXED_DOMAIN);

        final Lexicon<LogicalExpression> semiFactored = new Lexicon<LogicalExpression>();
        for (final LexicalEntry<LogicalExpression> entry : readLexicon
                .toCollection()) {
            for (final FactoredLexicalEntry factoredEntry : FactoringServices
                    .factor(entry, true, true, 2)) {
                semiFactored.add(FactoringServices.factor(factoredEntry));
            }
        }
        storeResource("seedLexicon", semiFactored);


        readResources();

        for (final Parameters params : jobParams) {
            addJob(createJob(params));
        }

    }

    private Job createJob(Parameters params) throws FileNotFoundException {
        final String type = params.get("type");
        if (type.equals("init")) {
            return createModelInitJob(params);
        } else if (type.equals("log")) {
            return createModelLoggingJob(params);
        } else if (type.equals("train")) {
            return createTrainJob(params);
        } else if (type.equals("test")) {
            return createTestJob(params);
        } else if (type.equals("save")) {
            return createSaveJob(params);
        } else {
            throw new RuntimeException("Unsupported job type: " + type);
        }
    }

    private Job createModelInitJob(Parameters params)
            throws FileNotFoundException {
        final Model<Sentence, LogicalExpression> model = get(params
                .get("model"));
        final List<IModelInit<Sentence, LogicalExpression>> modelInits = ListUtils
                .map(params.getSplit("init"),
                        new ListUtils.Mapper<String, IModelInit<Sentence, LogicalExpression>>() {
                            @Override
                            public IModelInit<Sentence, LogicalExpression> process(
                                    String obj) {
                                return get(obj);
                            }
                        });

        return new Job(params.get("id"), new HashSet<String>(
                params.getSplit("dep")), this,
                createJobOutputFile(params.get("id")),
                createJobLogFile(params.get("id"))) {

            @Override
            protected void doJob() {
                for (final IModelInit<Sentence, LogicalExpression> modelInit : modelInits) {
                    modelInit.init(model);
                }
            }
        };
    }

    private Job createModelLoggingJob(Parameters params)
            throws FileNotFoundException {
        final IModelImmutable<?, ?> model = get(params.get("model"));
        final ModelLogger modelLogger = get(params.get("logger"));
        return new Job(params.get("id"), new HashSet<String>(
                params.getSplit("dep")), this,
                createJobOutputFile(params.get("id")),
                createJobLogFile(params.get("id"))) {

            @Override
            protected void doJob() {
                modelLogger.log(model, getOutputStream());
            }
        };
    }

    private Job createTrainJob(Parameters params) throws FileNotFoundException {
        // The model to use
        final Model<Sentence, LogicalExpression> model = (Model<Sentence, LogicalExpression>) get(params
                .get("model"));

        // The learning - I think the types line up here
        final ILearner<Sentence, BurlapDemonstration, Model<Sentence, LogicalExpression>> learner =
                (ILearner<Sentence, BurlapDemonstration, Model<Sentence, LogicalExpression>>) get(params
                .get("learner"));

        return new Job(params.get("id"), new HashSet<String>(
                params.getSplit("dep")), this,
                createJobOutputFile(params.get("id")),
                createJobLogFile(params.get("id"))) {

            @Override
            protected void doJob() {
                final long startTime = System.currentTimeMillis();

                // Start job
                LOG.info("============ (Job %s started)", getId());

                // Do the learning
                learner.train(model);

                // Log the final model
                LOG.info("Final model:\n%s", model);

                // Output total run time
                LOG.info("Total run time %.4f seconds",
                        (System.currentTimeMillis() - startTime) / 1000.0);

                // Job completed
                LOG.info("============ (Job %s completed)", getId());

            }
        };
    }

    private Job createTestJob(Parameters params) throws FileNotFoundException{
        //TODO: switch all uses of CleanupState to State
        //TODO: initialize this as a resource?
        final ValidationTestingStatistics<Sentence, Pair<State, State>, LogicalExpression, BurlapDemonstration> stats =
                new ValidationTestingStatistics<>(get(params.get("validator")));

        //get tester
        final ValidationTester<Sentence, Pair<State, State>, LogicalExpression, BurlapDemonstration> tester =
                get(params.get("tester"));
        //get model
        final Model<Sentence, LogicalExpression> model =
                get(params.get("model"));

        // Create and return the job
        return new Job(params.get("id"), new HashSet<String>(
                params.getSplit("dep")), this,
                createJobOutputFile(params.get("id")),
                createJobLogFile(params.get("id"))) {

            @Override
            protected void doJob() {

                // Record start time
                final long startTime = System.currentTimeMillis();

                // Job started
                LOG.info("============ (Job %s started)", getId());

                tester.test(model, stats);
                LOG.info("%s", stats);

                // Output total run time
                LOG.info("Total run time %.4f seconds",
                        (System.currentTimeMillis() - startTime) / 1000.0);

                // Output machine readable stats
                getOutputStream().println(stats.toTabDelimitedString());

                // Job completed
                LOG.info("============ (Job %s completed)", getId());
            }
        };

    }

    private Job createSaveJob(final Parameters params)
            throws FileNotFoundException {
        return new Job(params.get("id"), new HashSet<String>(
                params.getSplit("dep")), this,
                createJobOutputFile(params.get("id")),
                createJobLogFile(params.get("id"))) {

            @SuppressWarnings("unchecked")
            @Override
            protected void doJob() {
                // Save the model to file.
                try {
                    LOG.info("Saving model (id=%s) to: %s",
                            params.get("model"), params.getAsFile("file")
                                    .getAbsolutePath());
                    Model.write((Model<Sentence, LogicalExpression>) get(params
                            .get("model")), params.getAsFile("file"));
                } catch (final IOException e) {
                    LOG.error("Failed to save model to: %s", params.get("file"));
                    throw new RuntimeException(e);
                }

            }
        };
    }


    public void readResources() {
        try {
            super.readResrouces();
        } catch (final RuntimeException e) {
            end();
            throw e;
        }
    }


}
