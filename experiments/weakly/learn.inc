## Filter to skip long sentences during learning
type=filter.sentence.length id=learningFilter length=%{trainingMaxSentenceLength}
#learner
#errorDriven=true for now. see how that affects the result.
type=learner.validation.perceptron id=perceptron data=debug genlexbeam=%{gBeam} iter=%{gIter} validator=validator filter=learningFilter genlex=genlex conflateParses=false errorDriven=true
#validation driven tester
type=tester.validation id=tester data=debug parser=parser validator=validator