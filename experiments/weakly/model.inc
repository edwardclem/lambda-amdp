## Default scorers
type=scorer.uniform id=uniform0Scorer   class=lexEntry  weight=0.0
## Features
type=feat.logexp.coordination id=featLogexpCoordination cpp1=true cpap=true rept=true
type=feat.lex.factored id=featLex templateScale=0.1
type=feat.lex.dynskip id=featSkip
type=feat.rules.count id=featRules scale=0.5
##0.5 was better rules count
type=feat.logexp.cooc id=featCooc
## Lexicon
type=lexicon.factored id=lexicon
## Actual model
type=model id=model lexicon=lexicon features=featLex,featLogexpCoordination,featRules,featSkip,featCooc
## Debug model logger
type=logger.model id=loggerModel cluster=true
