//seed lexicon for BURLAP CleanupDomain predicates

//TODO: make sure syntactic types are correct

//nouns
agent :- N : agent:<e,t>
block :- N : block:<e,t>
room :- N : room:<e,t>
door :- N : door:<e,t>

//adjectives
red :- N/N : (lambda $0:<e,t> (lambda $1:e (and:<t*,t> (red:<e,t> $1) ($0 $1) )))
blue :- N/N : (lambda $0:<e,t> (lambda $1:e (and:<t*,t> (blue:<e,t> $1) ($0 $1) )))
green :- N/N : (lambda $0:<e,t> (lambda $1:e (and:<t*,t> (green:<e,t> $1) ($0 $1))))

//determiners
the :- NP/N : (lambda $0:<e,t> (the:<<e,t>,e> $0))
//TODO: determiner with PP as argument
//for quantifiers
//the :- N/N : (lambda $0:<e,t> $0)

//spatial prepositions
in :- PP/NP\NP : (lambda $0:e (lambda $1:e (in:<e,<e,t>> $1 $0)))
into :- PP/NP\NP : (lambda $0:e (lambda $1:e (in:<e,<e,t>> $1 $0)))

//near
near :- PP/NP\NP : (lambda $0:e (lambda $1:e (near:<e,<e,t>> $1 $0)))
next to :- PP/NP\NP : (lambda $0:e (lambda $1:e (near:<e,<e,t>> $1 $0)))
by :- PP/NP\NP : (lambda $0:e (lambda $1:e (near:<e,<e,t>> $1 $0)))
left :- PP/NP\NP : (lambda $0:e (lambda $1:e (left:<e,<e,t>> $1 $0)))
right :- PP/NP\NP : (lambda $0:e (lambda $1:e (right:<e,<e,t>> $1 $0)))

//argmax in adjectival form
big :- N/N : (lambda $0:<e,t> (lambda $1:e (eq:<e,<e,t>> $1 (argmax:<<e,t>,<<e,n>,e>> $0 size:<e,n>))))
biggest :- N/N : (lambda $0:<e,t> (lambda $1:e (eq:<e,<e,t>> $1 (argmax:<<e,t>,<<e,n>,e>> $0 size:<e,n>))))
small :- N/N : (lambda $0:<e,t> (lambda $1:e (eq:<e,<e,t>> $1 (argmin:<<e,t>,<<e,n>,e>> $0 size:<e,n>))))
smallest :- N/N : (lambda $0:<e,t> (lambda $1:e (eq:<e,<e,t>> $1 (argmin:<<e,t>,<<e,n>,e>> $0 size:<e,n>))))

//imperative
//implied subject is agent
go to :- S/NP : (lambda $0:e (in:<e,<e,t>> (the:<<e,t>,e> agent:<e,t>) $0))
go to :- S/NP : (lambda $0:e (near:<e,<e,t>> (the:<<e,t>,e> agent:<e,t>) $0))
move near :- S/NP : (lambda $0:e (near:<e,<e,t>> (the:<<e,t>,e> agent:<e,t>) $0))
move to :- S/NP : (lambda $0:e (in:<e,<e,t>> (the:<<e,t>,e> agent:<e,t>) $0))
move to :- S/NP : (lambda $0:e (near:<e,<e,t>> (the:<<e,t>,e> agent:<e,t>) $0))
//put the block (NP) in the red room (PP)
put :- (S/PP)/NP : (lambda $0:e (lambda $1:<e,t> ($1 $0)))

//empty sentence modifier
can you  :- S/S : (lambda $0:<e,t> $0)
can you  :- S/S : (lambda $0:t $0)
please :- S\S : (lambda $0:t $0)
can you  :- S/S : (lambda $0:<e,t> $0)