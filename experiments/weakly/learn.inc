## Filter to skip long sentences during learning
type=filter.sentence.length id=learningFilter length=%{trainingMaxSentenceLength}
#learner
#errorDriven=false for now. see how that affects the result.
type=learner.validation.perceptron id=perceptron data=train genlexbeam=%{gBeam} iter=%{gIter} validator=validator filter=learningFilter genlex=genlex conflateParses=false errorDriven=false
#validation driven tester
type=tester.validation id=tester data=test parser=parser validator=validator