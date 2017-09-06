##Jobs
type=init model=model init=initWeights,initSeedLex,initSeedLexWeights,initSeedXemeWeights id=init
# Log initial model
type=log id=log.pre model=model logger=loggerModel dep=init
#perceptron learner
type=train learner=perceptron id=train dep=log.pre model=model
#tester
type=test tester=tester id=test model=model dep=train validator=validator
#log final model
type=log id=log.post model=model logger=loggerModel dep=train
#save model
type=save model=model file=%{outputDir}/model.sp id=save dep=train