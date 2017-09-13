## Filter to skip long sentences during learning
type=filter.sentence.length id=learningFilter length=%{trainingMaxSentenceLength}
#learner
#errorDriven=false for now. see how that affects the result.
[XX=0-4] type=learner.validation.perceptron id=perceptronXX data=train genlexbeam=%{gBeam} iter=%{gIter} validator=validatorXX filter=learningFilter genlex=genlexXX conflateParses=false errorDriven=false
#validation driven tester
[XX=0-4] type=tester.validation id=testerXX data=test parser=parser validator=validatorXX