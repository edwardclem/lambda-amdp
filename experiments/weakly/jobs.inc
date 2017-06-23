##Jobs
type=init model=model init=initWeights,initSeedLex,initSeedLexWeights,initSeedXemeWeights id=init
# Log initial model
type=log id=log.pre model=model logger=loggerModel dep=init
#perceptron learner
type=train learner=perceptron id=train dep=log.pre model=model