package demo;

import edu.cornell.cs.nlp.spf.parser.ccg.model.Model;
import edu.cornell.cs.nlp.spf.parser.ccg.normalform.NormalFormValidator;
import edu.cornell.cs.nlp.spf.parser.ccg.normalform.eisner.EisnerConstraint;
import edu.cornell.cs.nlp.spf.parser.graph.IGraphParser;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class ParserWrapper {

    /**
     * Reads in a trained model and returns reward function to maximize over multiple calls.
     * @param path
     */

    public Model model;

    public IGraphParser parser;

    public ParserWrapper(String path){

        File file = new File(path);

        try {
            model = Model.readModel(file);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final NormalFormValidator nf = new NormalFormValidator.Builder()
                .addConstraint(
                        new EisnerConstraint())
                .build();

        // Build the parser.
        final IGraphParser<Sentence, LogicalExpression> parser = new CKYParser.Builder<Sentence, LogicalExpression>(
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




    }

    public String parseSentence(String sentence){
        return model.
    }
}
