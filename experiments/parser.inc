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
#removed preChartPruning=true, shouldn't use for gradient descent
type=parser.cky.multi id=parser parseFilter=parseFilter beam=%{gBeam} pruneLexicalCells=true rules=ruleComp,ruleApp,ruleRaiseComp,ruleThatless,rulePluralExists,shiftPP executor=executor nfValidator=nfValidator wordSkipping=simple
# Parsing filter
type=parser.filter.archive.supervised id=filterFactory
