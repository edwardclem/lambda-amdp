## Default scorers
type=scorer.uniform id=uniform0Scorer   class=lexEntry  weight=0.0
## Features
type=feat.logexp.coordination id=featLogexpCoordination cpp1=true cpap=true rept=true
type=feat.lex.factored id=featLex templateScale=0.1
type=feat.lex.dynskip id=featSkip
type=feat.rules.count id=featRules scale=0.5
##0.5 was better rules count
type=feat.logexp.cooc id=featCooc
type=feat.logexp.predpairs id=featPairs
## Lexicon
#save model
[XX=0-4] type=lexicon.factored id=lexiconXX
## Actual model
#save model
[XX=0-4] type=model id=modelXX lexicon=lexiconXX features=featLex,featLogexpCoordination,featRules,featSkip,featCooc
## Debug model logger
type=logger.model id=loggerModel cluster=true
