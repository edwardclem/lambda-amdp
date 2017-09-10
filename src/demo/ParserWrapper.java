package demo;

import burlap.mdp.core.state.State;
import data.BurlapMultiDemonstration;
import edu.cornell.cs.nlp.spf.ccg.categories.syntax.Syntax;
import edu.cornell.cs.nlp.spf.ccg.lexicon.factored.lambda.FactoringServices;
import edu.cornell.cs.nlp.spf.data.sentence.Sentence;
import edu.cornell.cs.nlp.spf.mr.lambda.FlexibleTypeComparator;
import edu.cornell.cs.nlp.spf.mr.lambda.LogicLanguageServices;
import edu.cornell.cs.nlp.spf.mr.lambda.LogicalConstant;
import edu.cornell.cs.nlp.spf.mr.lambda.LogicalExpression;
import edu.cornell.cs.nlp.spf.mr.lambda.ccg.LogicalExpressionCategoryServices;
import edu.cornell.cs.nlp.spf.mr.lambda.ccg.SimpleFullParseFilter;
import edu.cornell.cs.nlp.spf.mr.language.type.TypeRepository;
import edu.cornell.cs.nlp.spf.parser.ccg.cky.CKYBinaryParsingRule;
import edu.cornell.cs.nlp.spf.parser.ccg.cky.CKYDerivation;
import edu.cornell.cs.nlp.spf.parser.ccg.cky.CKYParserOutput;
import edu.cornell.cs.nlp.spf.parser.ccg.cky.CKYUnaryParsingRule;
import edu.cornell.cs.nlp.spf.parser.ccg.cky.multi.MultiCKYParser;
import edu.cornell.cs.nlp.spf.parser.ccg.cky.single.CKYParser;
import edu.cornell.cs.nlp.spf.parser.ccg.cky.sloppy.BackwardSkippingRule;
import edu.cornell.cs.nlp.spf.parser.ccg.cky.sloppy.ForwardSkippingRule;
import edu.cornell.cs.nlp.spf.parser.ccg.cky.sloppy.SimpleWordSkippingLexicalGenerator;
import edu.cornell.cs.nlp.spf.parser.ccg.model.DataItemModel;
import edu.cornell.cs.nlp.spf.parser.ccg.model.Model;
import edu.cornell.cs.nlp.spf.parser.ccg.model.ModelLogger;
import edu.cornell.cs.nlp.spf.parser.ccg.normalform.NormalFormValidator;
import edu.cornell.cs.nlp.spf.parser.ccg.normalform.eisner.EisnerConstraint;
import edu.cornell.cs.nlp.spf.parser.ccg.rules.lambda.PluralExistentialTypeShifting;
import edu.cornell.cs.nlp.spf.parser.ccg.rules.lambda.ThatlessRelative;
import edu.cornell.cs.nlp.spf.parser.ccg.rules.lambda.typeraising.ForwardTypeRaisedComposition;
import edu.cornell.cs.nlp.spf.parser.ccg.rules.lambda.typeshifting.PrepositionTypeShifting;
import edu.cornell.cs.nlp.spf.parser.ccg.rules.primitivebinary.application.BackwardApplication;
import edu.cornell.cs.nlp.spf.parser.ccg.rules.primitivebinary.application.ForwardApplication;
import edu.cornell.cs.nlp.spf.parser.ccg.rules.primitivebinary.composition.BackwardComposition;
import edu.cornell.cs.nlp.spf.parser.ccg.rules.primitivebinary.composition.ForwardComposition;
import edu.cornell.cs.nlp.spf.parser.graph.IGraphParser;
import edu.cornell.cs.nlp.utils.collections.SetUtils;
import edu.cornell.cs.nlp.utils.composites.Triplet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParserWrapper {

    /**
     * Reads in a trained model and returns reward function to maximize over multiple calls.
     * @param path
     */

    public Model model;

    public IGraphParser<Sentence, LogicalExpression> parser;

    public ParserWrapper(String path){

        File file = new File(path);





        final File resourceDir = new File("/home/nakul/workspace/lambda-amdp/data/resources/");

        final File typesFile = new File(resourceDir, "burlap.types");
        final File predOntology = new File(resourceDir, "burlap.preds.ont");
//        final File simpleOntology = new File(resourceDir, "burlap.consts.ont");

        try {
            // Init the logical expression type system
            LogicLanguageServices.setInstance(new LogicLanguageServices.Builder(
                    new TypeRepository(typesFile), new FlexibleTypeComparator())
//                    .addConstantsToOntology(simpleOntology)
//                    .addConstantsToOntology(predOntology)
//                    .setUseOntology(true).setNumeralTypeName("i")
//                    .closeOntology(true)
                    .build());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }



        final LogicalExpressionCategoryServices categoryServices = new LogicalExpressionCategoryServices(
                true);

        FactoringServices.set(new FactoringServices.Builder()
                .addConstant(LogicalConstant.read("exists:<<e,t>,t>"))
                .addConstant(LogicalConstant.read("the:<<e,t>,e>")).build());



        final NormalFormValidator nf = new NormalFormValidator.Builder()
                .addConstraint(
                        new EisnerConstraint())
                .build();





        // Build the parser.
         parser = new MultiCKYParser.Builder<Sentence, LogicalExpression>(
                categoryServices)
                .setCompleteParseFilter(new SimpleFullParseFilter(
                        SetUtils.createSingleton((Syntax) Syntax.S)))
                .setPruneLexicalCells(true)
                .addSloppyLexicalGenerator(
                        new SimpleWordSkippingLexicalGenerator<Sentence, LogicalExpression>(
                                categoryServices))
                .setMaxNumberOfCellsInSpan(50)
                .addParseRule(
                        new CKYBinaryParsingRule<LogicalExpression>(
                                new ForwardComposition<LogicalExpression>(
                                        categoryServices, 1, false),
                                nf))
                .addParseRule(
                        new CKYBinaryParsingRule<LogicalExpression>(
                                new BackwardComposition<LogicalExpression>(
                                        categoryServices, 1, false),
                                nf))
                .addParseRule(
                        new CKYBinaryParsingRule<LogicalExpression>(
                                new ForwardApplication<LogicalExpression>(
                                        categoryServices),
                                nf))
                .addParseRule(
                        new CKYBinaryParsingRule<LogicalExpression>(
                                new BackwardApplication<LogicalExpression>(
                                        categoryServices),
                                nf))
                .addParseRule(
                        new CKYUnaryParsingRule<LogicalExpression>(
                                new PrepositionTypeShifting(
                                        categoryServices),
                                nf))
                .addParseRule(
                        new ForwardSkippingRule<LogicalExpression>(
                                categoryServices))
                .addParseRule(
                        new BackwardSkippingRule<LogicalExpression>(
                                categoryServices, false))
                .addParseRule(
                        new CKYBinaryParsingRule<LogicalExpression>(
                                new ForwardTypeRaisedComposition(
                                        categoryServices),
                                nf))
                .addParseRule(
                        new CKYBinaryParsingRule<LogicalExpression>(
                                new ThatlessRelative(categoryServices),
                                nf))
                .addParseRule(
                        new CKYBinaryParsingRule<LogicalExpression>(
                                new PluralExistentialTypeShifting(
                                        categoryServices),
                                nf))
                .build();


        try {
            model = Model.readModel(file);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        final ModelLogger modelLogger = new ModelLogger(true);
//        modelLogger.log(model, System.err);



    }

    public String parseSentence(String sentence){
        Sentence bmd = new Sentence(sentence);
        CKYParserOutput<LogicalExpression> parsedOutput =  (CKYParserOutput) parser.parse(bmd, new DataItemModel(model, bmd));
        List<CKYDerivation<LogicalExpression>> outputs = parsedOutput.getBestDerivations();
        String s ="";
        for(CKYDerivation<LogicalExpression> ckyDerivation:outputs){
            s+=ckyDerivation.getSemantics().toString();
        }
        return s;
    }

    public static void main(String[] args) {

        String path = "/home/nakul/workspace/lambda-amdp/experiments/weakly/baseline_50/model.sp";

        System.out.println("start");

        ParserWrapper pw = new ParserWrapper(path);

        System.out.println("passed reading!");

        String sentence = "Proceed to the pink colored room";

        System.out.println(pw.parseSentence(sentence));

        System.out.println("done");

    }
}
