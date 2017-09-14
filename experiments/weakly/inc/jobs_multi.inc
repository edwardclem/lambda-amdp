##Jobs
#job 1 - can you use the loops
[XX=0-4] type=init model=modelXX init=initWeightsXX,initSeedLexXX,initSeedLexWeightsXX,initSeedXemeWeightsXX id=initXX
#perceptron learner - sequential, avoid concurrency issues
#job0
type=train learner=perceptron0 id=train0 dep=init0 model=model0
type=test tester=tester0 id=test0 model=model0 dep=train0 validator=validator0
#job1
type=train learner=perceptron1 id=train1 dep=init1,test0 model=model1
type=test tester=tester1 id=test1 model=model1 dep=train1 validator=validator1
#job2
type=train learner=perceptron2 id=train2 dep=init2,test1 model=model2
type=test tester=tester2 id=test2 model=model2 dep=train2 validator=validator2
#job3
type=train learner=perceptron3 id=train3 dep=init3,test2 model=model3
type=test tester=tester3 id=test3 model=model3 dep=train3 validator=validator3
#job4
type=train learner=perceptron4 id=train4 dep=init4,test3 model=model4
type=test tester=tester4 id=test4 model=model4 dep=train4 validator=validator4
#log final model
[XX=0-4] type=log id=log.postXX model=modelXX logger=loggerModelXX dep=trainXX
#save model
[XX=0-4] type=save model=modelXX file=%{outputDir}/modelXX.sp id=saveXX dep=trainXX
