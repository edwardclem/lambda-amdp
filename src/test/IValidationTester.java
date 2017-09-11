package test;

import edu.cornell.cs.nlp.spf.data.IDataItem;
import edu.cornell.cs.nlp.spf.data.ILabeledDataItem;
import edu.cornell.cs.nlp.spf.parser.ccg.model.IModelImmutable;
import test.stats.IValidationTestingStatistics;

/**
 * Created by edwardwilliams on 6/30/17.
 *
 * SAMPLE - unlabeled data used for model inference.
 *
 * LABEL - label type of weakly archive.supervised data.
 *
 * MR - meaning representation type.
 *
 * DI - labeled data.
 *
 */
public interface IValidationTester<SAMPLE extends IDataItem<?>, LABEL, MR, DI extends ILabeledDataItem<SAMPLE, LABEL>> {

    void test(IModelImmutable<SAMPLE, MR> model, IValidationTestingStatistics<SAMPLE, LABEL, MR, DI> stats);
}
