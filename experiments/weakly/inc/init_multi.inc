[XX=0-4] type=init.lex   id=initSeedLexXX lexicon=seedLexicon
[XX=0-4] type=scorer.lenexp id=expScorerXX coef=10.0 exp=1.1
[XX=0-4] type=init.lex.weights id=initSeedLexWeightsXX lexicon=seedLexicon key=FACLEX#LEX scorer=expScorerXX
[XX=0-4] type=init.lex.weights id=initSeedXemeWeightsXX lexicon=seedLexicon key=FACLEX#XEME value=10.0
[XX=0-4] type=init.weights id=initWeightsXX file=inc/init.weights