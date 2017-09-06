## Model initializers
type=init.lex.unification id=initSeedLex data=train sentenceSyntax=S
type=scorer.lenexp id=expScorer coef=10.0 exp=1.1
#no noun phrases in this model
#type=init.lex	id=initNPs	lexicon=npLexicon
#type=init.lex.weights id=initNPsLexWeights lexicon=npLexicon key=FACLEX#LEX scorer=expScorer
#type=init.lex.weights id=initNPsXemeWeights lexicon=npLexicon key=FACLEX#XEME value=10.0
