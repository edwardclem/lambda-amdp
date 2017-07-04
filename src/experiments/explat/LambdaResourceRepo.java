package experiments.explat;

import data.BurlapDemonstration;
import data.BurlapDemonstrationDataset;
import data.BurlapInstruction;
import edu.cornell.cs.nlp.spf.ccg.lexicon.factored.lambda.FactoredLexicon;
import edu.cornell.cs.nlp.spf.data.sentence.Sentence;
import edu.cornell.cs.nlp.spf.data.sentence.SentenceLengthFilter;
import edu.cornell.cs.nlp.spf.explat.resources.ResourceCreatorRepository;
import edu.cornell.cs.nlp.spf.genlex.ccg.template.coarse.TemplateCoarseGenlex;
import edu.cornell.cs.nlp.spf.learn.validation.perceptron.ValidationPerceptron;
import edu.cornell.cs.nlp.spf.mr.lambda.LogicalExpression;
import edu.cornell.cs.nlp.spf.mr.lambda.ccg.SimpleFullParseFilter;
import edu.cornell.cs.nlp.spf.parser.ccg.cky.multi.MultiCKYParser;
import edu.cornell.cs.nlp.spf.parser.ccg.factoredlex.features.FactoredLexicalFeatureSet;
import edu.cornell.cs.nlp.spf.parser.ccg.features.basic.DynamicWordSkippingFeatures;
import edu.cornell.cs.nlp.spf.parser.ccg.features.basic.LexicalFeaturesInit;
import edu.cornell.cs.nlp.spf.parser.ccg.features.basic.RuleUsageFeatureSet;
import edu.cornell.cs.nlp.spf.parser.ccg.features.basic.scorer.ExpLengthLexicalEntryScorer;
import edu.cornell.cs.nlp.spf.parser.ccg.features.basic.scorer.SkippingSensitiveLexicalEntryScorer;
import edu.cornell.cs.nlp.spf.parser.ccg.features.basic.scorer.UniformScorer;
import edu.cornell.cs.nlp.spf.parser.ccg.features.lambda.LogicalExpressionCoordinationFeatureSet;
import edu.cornell.cs.nlp.spf.parser.ccg.model.LexiconModelInit;
import edu.cornell.cs.nlp.spf.parser.ccg.model.Model;
import edu.cornell.cs.nlp.spf.parser.ccg.model.ModelLogger;
import edu.cornell.cs.nlp.spf.parser.ccg.model.WeightInit;
import edu.cornell.cs.nlp.spf.parser.ccg.normalform.eisner.EisnerNormalFormCreator;
import edu.cornell.cs.nlp.spf.parser.ccg.rules.lambda.PluralExistentialTypeShifting;
import edu.cornell.cs.nlp.spf.parser.ccg.rules.lambda.ThatlessRelative;
import edu.cornell.cs.nlp.spf.parser.ccg.rules.lambda.typeraising.ForwardTypeRaisedComposition;
import edu.cornell.cs.nlp.spf.parser.ccg.rules.lambda.typeshifting.PrepositionTypeShifting;
import edu.cornell.cs.nlp.spf.parser.ccg.rules.primitivebinary.application.ApplicationCreator;
import edu.cornell.cs.nlp.spf.parser.ccg.rules.primitivebinary.composition.CompositionCreator;
import genlex.TemplateCoarseGenlexWeakLabel;
import test.ValidationTester;
import validation.BurlapSingleValidator;

/**
 * Created by edwardwilliams on 6/22/17.
 * Register resources with the resource creator.
 */
public class LambdaResourceRepo extends ResourceCreatorRepository {

    public LambdaResourceRepo(){
        //register data collection
        registerResourceCreator(new BurlapDemonstrationDataset.Creator());

        //register parsing resources
        registerResourceCreator(new ApplicationCreator<LogicalExpression>());
        registerResourceCreator(new CompositionCreator<LogicalExpression>());
        registerResourceCreator(new PrepositionTypeShifting.Creator());
        registerResourceCreator(new ForwardTypeRaisedComposition.Creator());
        registerResourceCreator(new ThatlessRelative.Creator());
        registerResourceCreator(new PluralExistentialTypeShifting.Creator());
        registerResourceCreator(
                new MultiCKYParser.Creator<Sentence, LogicalExpression>());
        registerResourceCreator(new SimpleFullParseFilter.Creator());
        registerResourceCreator(
                new ExpLengthLexicalEntryScorer.Creator<LogicalExpression>());
        registerResourceCreator(
                new LexicalFeaturesInit.Creator<Sentence, LogicalExpression>());
        registerResourceCreator(new EisnerNormalFormCreator());

        //model + feature resources
        registerResourceCreator(
                new Model.Creator<Sentence, LogicalExpression>());
        registerResourceCreator(new ModelLogger.Creator());
        registerResourceCreator(new UniformScorer.Creator<LogicalExpression>());
        registerResourceCreator(
                new FactoredLexicalFeatureSet.Creator<Sentence>());
        registerResourceCreator(
                new SkippingSensitiveLexicalEntryScorer.Creator<LogicalExpression>());
        registerResourceCreator(
                new LogicalExpressionCoordinationFeatureSet.Creator<Sentence>());
        registerResourceCreator(new DynamicWordSkippingFeatures.Creator<>());
        registerResourceCreator(
                new RuleUsageFeatureSet.Creator<Sentence, LogicalExpression>());
        registerResourceCreator(
                new LexiconModelInit.Creator<Sentence, LogicalExpression>());

        //register lexicon resources
        registerResourceCreator(new FactoredLexicon.Creator());

        //register validator
        registerResourceCreator(new BurlapSingleValidator.Creator());

        //register genlex resources
        //registerResourceCreator(new TemplateCoarseGenlex.Creator<>());
        registerResourceCreator(new TemplateCoarseGenlexWeakLabel.Creator<>());

        //register learning algorithm resources
        registerResourceCreator(new SentenceLengthFilter.Creator<Sentence>());

        //TODO: will need to change if we start conditioning on initial state
        registerResourceCreator(new ValidationPerceptron.Creator<Sentence, BurlapDemonstration, LogicalExpression>());

        //register initializer
        registerResourceCreator(new WeightInit.Creator<>());

        //add tester! Yay!
        //this should be everything?
        registerResourceCreator(new ValidationTester.Creator<>());



    }
}
