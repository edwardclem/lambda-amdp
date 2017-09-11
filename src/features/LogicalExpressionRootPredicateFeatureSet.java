package features;

/**
 * pairwise co-occurence between head predicate and all other logical expressions in a literal.
 */

import edu.cornell.cs.nlp.spf.base.collections.AllPairs;
import edu.cornell.cs.nlp.spf.base.hashvector.HashVectorFactory;
import edu.cornell.cs.nlp.spf.base.hashvector.IHashVector;
import edu.cornell.cs.nlp.spf.base.hashvector.KeyArgs;
import edu.cornell.cs.nlp.spf.data.IDataItem;
import edu.cornell.cs.nlp.spf.explat.IResourceRepository;
import edu.cornell.cs.nlp.spf.explat.ParameterizedExperiment.Parameters;
import edu.cornell.cs.nlp.spf.explat.resources.IResourceObjectCreator;
import edu.cornell.cs.nlp.spf.explat.resources.usage.ResourceUsage;
import edu.cornell.cs.nlp.spf.mr.lambda.*;
import edu.cornell.cs.nlp.spf.mr.lambda.visitor.GetAllPredicates;
import edu.cornell.cs.nlp.spf.mr.lambda.visitor.GetHeadString;
import edu.cornell.cs.nlp.spf.mr.lambda.visitor.ILogicalExpressionVisitor;
import edu.cornell.cs.nlp.spf.parser.ccg.IParseStep;
import edu.cornell.cs.nlp.spf.parser.ccg.model.parse.IParseFeatureSet;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class LogicalExpressionRootPredicateFeatureSet<DI extends IDataItem<?>>
        implements IParseFeatureSet<DI, LogicalExpression> {

    private static final String FEATURE_TAG = "LOGROOTPRED";

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
            IResourceObjectCreator<LogicalExpressionRootPredicateFeatureSet<DI>>{

        @Override
        public LogicalExpressionRootPredicateFeatureSet<DI> create(
                Parameters params, IResourceRepository resourceRepo){
            return new LogicalExpressionRootPredicateFeatureSet<DI>();
        }

        @Override
        public String type(){return "feat.logexp.rootpreds";}

        @Override
        public ResourceUsage usage(){
            return new ResourceUsage.Builder(type(),
                    LogicalExpressionRootPredicateFeatureSet.class)
                    .setDescription("feats between root predicate and all other predicates")
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

            final String rootString = GetHeadString
                    .of(literal.getPredicate());

            final Set<LogicalConstant> allPredicates =  GetAllPredicates.of(literal);

            //look at pairs between head and all other predicates

            for (LogicalConstant pred : allPredicates){


                final String predString = GetHeadString.of(pred);

                if (!predString.equals(rootString)) {
                    features.set(FEATURE_TAG, "ROOTPRED", rootString, predString,
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
