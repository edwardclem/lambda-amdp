package features;

/**
 * pairwise co-occurence between all predicates in a logical expression.
 */

import edu.cornell.cs.nlp.spf.base.collections.AllPairs;
import edu.cornell.cs.nlp.spf.base.hashvector.HashVectorFactory;
import edu.cornell.cs.nlp.spf.base.hashvector.IHashVector;
import edu.cornell.cs.nlp.spf.base.hashvector.KeyArgs;
import edu.cornell.cs.nlp.spf.data.IDataItem;
import edu.cornell.cs.nlp.spf.explat.IResourceRepository;
import edu.cornell.cs.nlp.spf.explat.resources.IResourceObjectCreator;
import edu.cornell.cs.nlp.spf.explat.resources.usage.ResourceUsage;
import edu.cornell.cs.nlp.spf.mr.lambda.*;
import edu.cornell.cs.nlp.spf.mr.lambda.visitor.GetAllPredicates;
import edu.cornell.cs.nlp.spf.mr.lambda.visitor.GetHeadString;
import edu.cornell.cs.nlp.spf.mr.lambda.visitor.ILogicalExpressionVisitor;
import edu.cornell.cs.nlp.spf.parser.ccg.IParseStep;
import edu.cornell.cs.nlp.spf.parser.ccg.model.parse.IParseFeatureSet;
import edu.cornell.cs.nlp.spf.explat.ParameterizedExperiment.Parameters;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class LogicalExpressionPairwisePredicateFeatureSet<DI extends IDataItem<?>>
        implements IParseFeatureSet<DI, LogicalExpression> {

    private static final String FEATURE_TAG = "LOGPREDPAIR";

    //uhhh dunno what to do here
    private static final long	serialVersionUID	= 7387260474009084902L;

    @Override
    public Set<KeyArgs> getDefaultFeatures() {
        return Collections.emptySet();
    }

    public void setFeatures(IParseStep<LogicalExpression> parseStep, IHashVector feats, DI dataItem){

        if (!parseStep.isFullParse()){
            //only for final form of parser
            return;
        }

        final IHashVector features = ExtractFeatures
                .of(parseStep.getRoot().getSemantics(), 1.0);
        if (feats != null) {
            features.addTimesInto(1.0, feats);
        }
    }

    public static class Creator<DI extends IDataItem<?>> implements
            IResourceObjectCreator<LogicalExpressionPairwisePredicateFeatureSet<DI>>{

        @Override
        public LogicalExpressionPairwisePredicateFeatureSet<DI> create(
                Parameters params, IResourceRepository resourceRepo){
            return new LogicalExpressionPairwisePredicateFeatureSet<DI>();
        }

        @Override
        public String type(){return "feat.logexp.predpairs";}

        @Override
        public ResourceUsage usage(){
            return new ResourceUsage.Builder(type(),
                    LogicalExpressionPairwisePredicateFeatureSet.class)
                    .setDescription("pairwise features between predicates in a full logical expressions")
                    .build();
        }
    }

    /**
     * Marks pairwise co-occurrence features between predicates in a logical expression.
     * PREDPRED = co-occurence of two predicates in logical expression
     *
     * @author Edward Williams
     */
    private static class ExtractFeatures implements ILogicalExpressionVisitor{
        private final IHashVector features = HashVectorFactory.create();

        private final double scale;

        private ExtractFeatures (double scale){
            this.scale = scale;
        }

        public static IHashVector of(LogicalExpression exp, double scale){
            final ExtractFeatures visitor = new ExtractFeatures(scale);
            visitor.visit(exp);
            return visitor.features;
        }

        @Override
        public void visit(Lambda lambda) {
            lambda.getArgument().accept(this);
            lambda.getBody().accept(this);
        }

        @Override
        public void visit(Literal literal){
            //this might work

            final Set<LogicalConstant> allPredicates =  GetAllPredicates.of(literal);

            //only look at pairs of logical constants.

            for (final List<LogicalConstant> subset : new AllPairs<LogicalConstant>(
                    allPredicates)) {
                final String first = GetHeadString.of(subset.get(0));
                final String second = GetHeadString.of(subset.get(1));
                if (first.compareTo(second) >= 0) {
                    features.set(FEATURE_TAG, "PREDPRED", first, second,
                            1.0 * scale);
                } else {
                    features.set(FEATURE_TAG, "PREDPRED", second, first,
                            1.0 * scale);
                }
            }

        }

        @Override
        public void visit(LogicalConstant logicalConstant) {
            // Nothing to do

        }

        @Override
        public void visit(LogicalExpression logicalExpression) {
            logicalExpression.accept(this);

        }

        @Override
        public void visit(Variable variable) {
            // Nothing to do
        }

    }

}
