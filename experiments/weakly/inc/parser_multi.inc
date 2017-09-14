# Parsing rules
type=rule.composition id=ruleComp
type=rule.application id=ruleApp
type=rule.shifting.pp id=shiftPP
type=rule.typeraise.composition.forward id=ruleRaiseComp
type=rule.thatless id=ruleThatless
type=rule.shift.pluralexists id=rulePluralExists
# NF constraints
type=nf.eisner id=nfValidator
# Parse filter
type=parsefilter.lambda.simple id=parseFilter syntax=S
# CKY Parser
#re-added preChartPruning=true, okay for perceptron
[XX=0-4] type=parser.cky.multi id=parserXX parseFilter=parseFilter beam=%{gBeam} preChartPruning=true pruneLexicalCells=true rules=ruleComp,ruleApp,ruleRaiseComp,ruleThatless,rulePluralExists,shiftPP executor=executor nfValidator=nfValidator wordSkipping=simple
