package test.stats;

import edu.cornell.cs.nlp.spf.data.ILabeledDataItem;
import edu.cornell.cs.nlp.spf.data.sentence.Sentence;
import edu.cornell.cs.nlp.spf.data.utils.IValidator;
import edu.cornell.cs.nlp.spf.test.stats.IStatistics;
import edu.cornell.cs.nlp.spf.test.stats.SimpleStats;
import edu.cornell.cs.nlp.utils.log.ILogger;
import edu.cornell.cs.nlp.utils.log.LoggerFactory;

import java.util.List;

/**
 * Created by edwardwilliams on 6/30/17.
 */
public class ValidationTestingStatistics<SAMPLE, LABEL, MR, DI extends ILabeledDataItem<SAMPLE, LABEL>> implements IValidationTestingStatistics<SAMPLE, LABEL, MR, DI> {

    private final String metricName;
    private final String prefix;
    protected final IStatistics<SAMPLE> stats;
    protected final IValidator<DI, MR> validator;

    private static final String DEFAULT_METRIC_NAME =  "VALIDATOR_STATS";

    public static final ILogger LOG	= LoggerFactory.create(ValidationTestingStatistics.class);

    public ValidationTestingStatistics(IValidator<DI, MR> validator){
        this(null, DEFAULT_METRIC_NAME, new SimpleStats<SAMPLE>(DEFAULT_METRIC_NAME), validator);
    }

    public ValidationTestingStatistics(String prefix, String metricName, IStatistics<SAMPLE> stats, IValidator<DI, MR> validator){
        this.prefix = prefix;
        this.metricName = metricName;
        this.stats = stats;
        this.validator = validator;
    }

    @Override
    public void recordNoParse(DI dataItem){
        LOG.info("%s stats -- recording no parse :(", getMetricName());
        stats.recordFailure(dataItem.getSample());
    }

    @Override
    public void recordNoParseWithSkipping(DI dataItem){
        LOG.info("%s stats -- recording no parse with skipping",
                getMetricName());
        stats.recordSloppyFailure(dataItem.getSample());
    }

    /**
     * Record a parse.
     */
    public void recordParse(DI dataItem, MR candidate){
        if (validator.isValid(dataItem, candidate)) {
            LOG.info("%s stats -- recording correct parse: %s",
                    getMetricName(), candidate);
            stats.recordCorrect(dataItem.getSample());
        } else {
            LOG.info("%s stats -- recording wrong parse: %s", getMetricName(),
                    candidate);
            stats.recordIncorrect(dataItem.getSample());
        }

    }
    //TODO: using Yoav's version records no parse if there are multiple parses?
    public void recordParses(DI dataItem, List<MR> candidates){
        recordNoParse(dataItem);
    }

    public void recordParsesWithSkipping(DI dataItem, List<MR> candidates){
        recordNoParseWithSkipping(dataItem);
    }

    /**
     * Record a parse with word skipping enabled. Assumes a record parse for
     * this data item has been called earlier.
     */
    @Override
    public void recordParseWithSkipping(DI dataItem, MR candidate) {
        if (validator.isValid(dataItem, candidate)) {
            LOG.info("%s stats -- recording correct parse with skipping: %s",
                    getMetricName(), candidate);
            stats.recordSloppyCorrect(dataItem.getSample());
        } else {
            LOG.info("%s stats -- recording wrong parse with skipping: %s",
                    getMetricName(), candidate);
            stats.recordSloppyIncorrect(dataItem.getSample());
        }
    }

    @Override
    public String toString() {
        final StringBuilder ret = new StringBuilder("=== ").append(
                getMetricName()).append(" statistics:\n");
        ret.append("Recall: ").append(stats.getCorrects()).append('/')
                .append(stats.getTotal()).append(" = ").append(stats.recall())
                .append('\n');
        ret.append("Precision: ").append(stats.getCorrects()).append('/')
                .append(stats.getTotal() - stats.getFailures()).append(" = ")
                .append(stats.precision()).append('\n');
        ret.append("F1: ").append(stats.f1()).append('\n');
        ret.append("SKIP Recall: ")
                .append(stats.getSloppyCorrects() + stats.getCorrects())
                .append('/').append(stats.getTotal()).append(" = ")
                .append(stats.sloppyRecall()).append('\n');
        ret.append("SKIP Precision: ")
                .append(stats.getSloppyCorrects() + stats.getCorrects())
                .append('/')
                .append(stats.getTotal() - stats.getSloppyFailures())
                .append(" = ").append(stats.sloppyPrecision()).append('\n');
        ret.append("SKIP F1: ").append(stats.sloppyF1());
        return ret.toString();
        }
    @Override
    public String toTabDelimitedString() {
        final StringBuilder ret = new StringBuilder(getPrefix())
                .append("\tmetric=").append(getMetricName()).append("\t");
        ret.append("recall=").append(stats.recall()).append('\t');
        ret.append("precision=").append(stats.precision()).append('\t');
        ret.append("f1=").append(stats.f1()).append('\t');
        ret.append("skippingRecall=").append(stats.sloppyRecall()).append('\t');
        ret.append("skippingPrecision=").append(stats.sloppyPrecision())
                .append('\t');
        ret.append("skippingF1=").append(stats.sloppyF1());
        return ret.toString();
    }


    protected String getMetricName() {
        return metricName;
    }

    protected String getPrefix() {
        return prefix == null ? "" : prefix;
    }

}
