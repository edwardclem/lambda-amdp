package test;

import edu.cornell.cs.nlp.spf.base.hashvector.IHashVector;
import edu.cornell.cs.nlp.spf.ccg.lexicon.LexicalEntry;
import edu.cornell.cs.nlp.spf.data.IDataItem;
import edu.cornell.cs.nlp.spf.data.ILabeledDataItem;
import edu.cornell.cs.nlp.spf.data.collection.IDataCollection;
import edu.cornell.cs.nlp.spf.data.utils.IValidator;
import edu.cornell.cs.nlp.spf.explat.IResourceRepository;
import edu.cornell.cs.nlp.spf.explat.ParameterizedExperiment;
import edu.cornell.cs.nlp.spf.explat.resources.IResourceObjectCreator;
import edu.cornell.cs.nlp.spf.explat.resources.usage.ResourceUsage;
import edu.cornell.cs.nlp.spf.parser.IDerivation;
import edu.cornell.cs.nlp.spf.parser.IOutputLogger;
import edu.cornell.cs.nlp.spf.parser.IParser;
import edu.cornell.cs.nlp.spf.parser.IParserOutput;
import edu.cornell.cs.nlp.spf.parser.ccg.IWeightedParseStep;
import edu.cornell.cs.nlp.spf.parser.ccg.model.IDataItemModel;
import edu.cornell.cs.nlp.spf.parser.ccg.model.IModelImmutable;
import edu.cornell.cs.nlp.utils.collections.ListUtils;
import edu.cornell.cs.nlp.utils.filter.IFilter;
import edu.cornell.cs.nlp.utils.log.ILogger;
import edu.cornell.cs.nlp.utils.log.LoggerFactory;
import test.stats.IValidationTestingStatistics;

import java.util.List;

/**
 * Created by edwardwilliams on 6/26/17.
 * Testing parser using provided validation function. derived from SPF Tester class.
 */
public class ValidationTester<SAMPLE extends IDataItem<?>, LABEL extends IDataItem<?>, MR, DI extends ILabeledDataItem<SAMPLE, LABEL>>
        implements IValidationTester<SAMPLE, LABEL, MR, DI>{
    public static final ILogger LOG = LoggerFactory.create(ValidationTester.class.getName());

    private final IOutputLogger<MR> outputLogger;

    private final IParser<SAMPLE, MR> parser;

    private final IFilter<SAMPLE> skipParsingFilter;

    private final IDataCollection<? extends DI> testData;

    //private final IBurlapValidator<LABEL, MR> validator;

    //going back to standard validator - passing in the entire labeled dataItem is totally a valid way to use it
    private final IValidator<DI, MR> validator;


    private ValidationTester(IDataCollection<? extends DI> testData,
                             IFilter<SAMPLE> skipParsingFilter,
                             IParser<SAMPLE, MR> parser,
                             IOutputLogger<MR> outputLogger,
                             IValidator<DI, MR> validator){
            this.testData = testData;
        this.skipParsingFilter = skipParsingFilter;
        this.parser = parser;
        this.outputLogger = outputLogger;
        this.validator = validator;
        LOG.info("Init Tester:  testData.size()=%d", testData.size());
    }

    @Override
    public void test(IModelImmutable<SAMPLE, MR> model,
                     IValidationTestingStatistics<SAMPLE, LABEL, MR, DI> stats) {
        test(testData, model, stats);
    }

    private void test(IDataCollection<? extends DI> dataset,
                      IModelImmutable<SAMPLE, MR> model,
                      IValidationTestingStatistics<SAMPLE, LABEL, MR, DI> stats) {
        int itemCounter = 0;
        for (final DI item : dataset) {
            ++itemCounter;
            test(itemCounter, item, model, stats);
        }
    }

    private void test(int itemCounter, final DI dataItem,
                      IModelImmutable<SAMPLE, MR> model,
                      IValidationTestingStatistics<SAMPLE, LABEL, MR, DI> stats) {
        LOG.info("%d : ==================", itemCounter);
        LOG.info("%s", dataItem);

        final IDataItemModel<MR> dataItemModel = model
                .createDataItemModel(dataItem.getSample());

        // Try a simple model parse
        final IParserOutput<MR> modelParserOutput = parser
                .parse(dataItem.getSample(), dataItemModel);
        LOG.info("Test parsing time %.2fsec",
                modelParserOutput.getParsingTime() / 1000.0);
        outputLogger.log(modelParserOutput, dataItemModel,
                String.format("test-%d", itemCounter));

        final List<? extends IDerivation<MR>> bestModelParses = modelParserOutput
                .getBestDerivations();
        if (bestModelParses.size() == 1) {
            // Case we have a single parse
            processSingleBestParse(dataItem, dataItemModel, modelParserOutput,
                    bestModelParses.get(0), false, stats);
        } else if (bestModelParses.size() > 1) {
            // Multiple top parses

            // Update statistics
            stats.recordParses(dataItem,
                    ListUtils.map(bestModelParses, obj -> obj.getSemantics()));

            // There are more than one equally high scoring
            // logical forms. If this is the case, we abstain
            // from returning a result.
            LOG.info("too many parses");
            LOG.info("%d parses:", bestModelParses.size());
            for (final IDerivation<MR> parse : bestModelParses) {
                logParse(dataItem, parse, false, null, model);
            }
            // Check if we had the correct parse and it just wasn't the best
            final List<? extends IDerivation<MR>> correctParses = modelParserOutput
                    .getMaxDerivations(
                            e -> validator.isValid(dataItem, e.getSemantics()));

            LOG.info("Had correct parses: %s", !correctParses.isEmpty());
            if (!correctParses.isEmpty()) {
                for (final IDerivation<MR> correctParse : correctParses) {
                    logDerivation(correctParse, dataItemModel);
                }
            }
        } else {
            // No parses
            LOG.info("no parses");

            // Update stats
            stats.recordNoParse(dataItem);

            // Potentially re-parse with word skipping
            if (skipParsingFilter.test(dataItem.getSample())) {
                final IParserOutput<MR> parserOutputWithSkipping = parser
                        .parse(dataItem.getSample(), dataItemModel, true);
                LOG.info("EMPTY Parsing time %fsec",
                        parserOutputWithSkipping.getParsingTime() / 1000.0);
                outputLogger.log(parserOutputWithSkipping, dataItemModel,
                        String.format("test-%d-sloppy", itemCounter));
                final List<? extends IDerivation<MR>> bestEmptiesParses = parserOutputWithSkipping
                        .getBestDerivations();

                if (bestEmptiesParses.size() == 1) {
                    processSingleBestParse(dataItem, dataItemModel,
                            parserOutputWithSkipping, bestEmptiesParses.get(0),
                            true, stats);
                } else if (bestEmptiesParses.isEmpty()) {
                    // No parses
                    LOG.info("no parses");

                    stats.recordNoParseWithSkipping(dataItem);
                } else {
                    // too many parses or no parses
                    stats.recordParsesWithSkipping(dataItem, ListUtils
                            .map(bestEmptiesParses, obj -> obj.getSemantics()));

                    LOG.info("WRONG: %d parses", bestEmptiesParses.size());
                    for (final IDerivation<MR> parse : bestEmptiesParses) {
                        logParse(dataItem, parse, false, null, model);
                    }
                    // Check if we had the correct parse and it just wasn't
                    // the best
                    final List<? extends IDerivation<MR>> correctParses = parserOutputWithSkipping
                            .getMaxDerivations(e -> validator.isValid(dataItem, e.getSemantics()));
                    LOG.info("Had correct parses: %s",
                            !correctParses.isEmpty());
                    if (!correctParses.isEmpty()) {
                        for (final IDerivation<MR> correctParse : correctParses) {
                            logDerivation(correctParse, dataItemModel);
                        }
                    }
                }
            } else {
                LOG.info("Skipping word-skip parsing due to length");
                stats.recordNoParseWithSkipping(dataItem);
            }
        }
    }


    private void processSingleBestParse(final DI dataItem,
                                        IDataItemModel<MR> dataItemModel,
                                        final IParserOutput<MR> modelParserOutput,
                                        final IDerivation<MR> parse, boolean withWordSkipping,
                                        IValidationTestingStatistics<SAMPLE, LABEL, MR, DI> stats) {
        final MR label = parse.getSemantics();

        // Update statistics
        if (withWordSkipping) {
            stats.recordParseWithSkipping(dataItem, parse.getSemantics());
        } else {
            stats.recordParse(dataItem, parse.getSemantics());
        }

        if (validator.isValid(dataItem, label)) {
            // A correct parse
            LOG.info("CORRECT");
            logDerivation(parse, dataItemModel);
        } else {
            // One parse, but a wrong one
            LOG.info("WRONG", label);
            logDerivation(parse, dataItemModel);

            // Check if we had the correct parse and it just wasn't the best
            final List<? extends IDerivation<MR>> correctParses = modelParserOutput
                    .getMaxDerivations(
                            e -> validator.isValid(dataItem, e.getSemantics()));
            LOG.info("Had correct parses: %s", !correctParses.isEmpty());
            if (!correctParses.isEmpty()) {
                for (final IDerivation<MR> correctParse : correctParses) {
                    LOG.info("Correct derivation:");
                    logDerivation(correctParse, dataItemModel);
                    final IHashVector diff = correctParse
                            .getAverageMaxFeatureVector()
                            .addTimes(-1.0, parse.getAverageMaxFeatureVector());
                    diff.dropNoise();
                    LOG.info("Diff: %s",
                            dataItemModel.getTheta().printValues(diff));
                }
            }
            LOG.info("Feats: %s", dataItemModel.getTheta()
                    .printValues(parse.getAverageMaxFeatureVector()));
        }
    }

    private void logDerivation(IDerivation<MR> derivation,
                               IDataItemModel<MR> dataItemModel) {
        LOG.info("[%.2f] %s", derivation.getScore(), derivation);
        for (final IWeightedParseStep<MR> step : derivation.getMaxSteps()) {
            LOG.info("\t%s",
                    step.toString(false, false, dataItemModel.getTheta()));
        }
    }

    private void logParse(ILabeledDataItem<SAMPLE, LABEL> dataItem,
                          IDerivation<MR> parse, boolean logLexicalItems, String tag,
                          IModelImmutable<SAMPLE, MR> model) {
        LOG.info("%s%s[S%.2f] %s",
                dataItem.getLabel().equals(parse.getCategory()) ? "* " : "  ",
                tag == null ? "" : tag + " ", parse.getScore(), parse);
        LOG.info("Calculated score: %f",
                model.score(parse.getAverageMaxFeatureVector()));
        LOG.info("Features: %s", model.getTheta()
                .printValues(parse.getAverageMaxFeatureVector()));
        if (logLexicalItems) {
            for (final LexicalEntry<MR> entry : parse.getMaxLexicalEntries()) {
                LOG.info("\t[%f] %s", model.score(entry), entry);
            }
        }
    }


    public static class Creator<SAMPLE extends IDataItem<?>, LABEL extends IDataItem<?>, MR, DI extends ILabeledDataItem<SAMPLE, LABEL>>
            implements IResourceObjectCreator<ValidationTester<SAMPLE, LABEL, MR, DI>> {

        @SuppressWarnings("unchecked")
        @Override
        public ValidationTester<SAMPLE, LABEL, MR, DI> create(ParameterizedExperiment.Parameters parameters,
                                                IResourceRepository resourceRepo) {

            // Get the testing set
            final IDataCollection<DI> testSet;
            {
                // [yoav] [17/10/2011] Store in Object to javac known bug
                final Object dataCollection = resourceRepo
                        .get(parameters.get("data"));
                if (dataCollection == null
                        || !(dataCollection instanceof IDataCollection<?>)) {
                    throw new RuntimeException(
                            "Unknown or non labeled dataset: "
                                    + parameters.get("data"));
                } else {
                    testSet = (IDataCollection<DI>) dataCollection;
                }
            }

            if (!parameters.contains("parser")) {
                throw new IllegalStateException(
                        "tester now requires you to provide a parser");
            }

            final ValidationTester.Builder<SAMPLE, LABEL, MR, DI> builder = new ValidationTester.Builder<SAMPLE, LABEL, MR, DI>(
                    testSet, (IParser<SAMPLE, MR>) resourceRepo
                    .get(parameters.get("parser")), resourceRepo.get(parameters.get("validator")));

            if (parameters.get("skippingFilter") != null) {
                builder.setSkipParsingFilter((IFilter<SAMPLE>) resourceRepo
                        .get(parameters.get("skippingFilter")));
            }

            return builder.build();
        }

        @Override
        public String type() {
            return "tester.validation";
        }

        @Override
        public ResourceUsage usage() {
            return new ResourceUsage.Builder(type(), ValidationTester.class)
                    .setDescription(
                            "Model tester. Tests inference using the model on some testing data")
                    .addParam("data", "id",
                            "IDataCollection that holds ILabaledDataItem entries")
                    .addParam("parser", "id", "Parser object")
                    .addParam("validator", "id", "Validator object")
                    .addParam("skippingFilter", "id",
                            "IFilter used to decide which data items to skip")
                    .build();
        }

    }


    public static class Builder<SAMPLE extends IDataItem<?>, LABEL extends IDataItem<?>, MR, DI extends ILabeledDataItem<SAMPLE, LABEL>> {

        private IOutputLogger<MR> outputLogger = new IOutputLogger<MR>() {
            private static final long serialVersionUID = -2828347737693835555L;

            @Override
            public void log(IParserOutput<MR> output,
                            IDataItemModel<MR> dataItemModel, String tag) {
                // Stub.
            }
        };

        private final IParser<SAMPLE, MR> parser;
        private IValidator<DI, MR> validator;

        /** Filters which data items are valid for parsing with word skipping */
        private IFilter<SAMPLE> skipParsingFilter = e -> true;

        private final IDataCollection<? extends DI> testData;

        public Builder(IDataCollection<? extends DI> testData,
                       IParser<SAMPLE, MR> parser, IValidator<DI, MR> validator) {
            this.testData = testData;
            this.parser = parser;
            this.validator = validator;
        }

        public ValidationTester<SAMPLE, LABEL, MR, DI> build() {
            return new ValidationTester<SAMPLE, LABEL, MR, DI>(testData, skipParsingFilter,
                    parser, outputLogger, validator);
        }

        public Builder<SAMPLE, LABEL, MR, DI> setOutputLogger(
                IOutputLogger<MR> outputLogger) {
            this.outputLogger = outputLogger;
            return this;
        }

        public Builder<SAMPLE, LABEL, MR, DI> setValidator(
                IValidator<DI, MR> validator){
            this.validator=validator;
            return this;
        }

        public Builder<SAMPLE, LABEL,  MR, DI> setSkipParsingFilter(
                IFilter<SAMPLE> skipParsingFilter) {
            this.skipParsingFilter = skipParsingFilter;
            return this;
        }
    }
}

