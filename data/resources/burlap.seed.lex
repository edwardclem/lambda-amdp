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
the :- N/N : (lambda $0:<e,t> $0)

//spatial prepositions
in :- PP/NP\NP : (lambda $0:e (lambda $1:e (in:<e,<e,t>> $1 $0)))
into :- PP/NP\NP : (lambda $0:e (lambda $1:e (in:<e,<e,t>> $1 $0)))

//near
near :- PP/NP\NP : (lambda $0:e (lambda $1:e (near:<e,<e,t>> $1 $0)))
next to :- PP/NP\NP : (lambda $0:e (lambda $1:e (near:<e,<e,t>> $1 $0)))
by :- PP/NP\NP : (lambda $0:e (lambda $1:e (near:<e,<e,t>> $1 $0)))
left :- PP/NP\NP : (lambda $0:e (lambda $1:e (left:<e,<e,t>> $1 $0)))
right :- PP/NP\NP : (lambda $0:e (lambda $1:e (right:<e,<e,t>> $1 $0)))

//quantifiers
//largest :- NP/N : (lambda $0:<e,t> (argmax:<<e,t>,<<e,n>,e>> $0 size:<e,n>))
//smallest :- NP/N : (lambda $0:<e,t> (argmin:<<e,t>,<<e,n>,e>> $0 size:<e,n>))
//closest to :- (NP\N)/NP : (lambda $0:e (lambda $1:<e,t> (argmin:<<e,t>,<<e,n>,e>> $1 (dist:<e,<e,n>> $0))))
//nearest to :- (NP\N)/NP : (lambda $0:e (lambda $1:<e,t> (argmin:<<e,t>,<<e,n>,e>> $1 (dist:<e,<e,n>> $0))))
//"get the nearest block" - implied referent is the agent
//nearest :- NP\N : (lambda $0:<e,t> (argmin:<<e,t>,<<e,n>,e>> $0 (dist:<e,<e,n>> (the:<<e,t>,e> agent:<e,t>))))
//closest :- NP\N : (lambda $0:<e,t> (argmin:<<e,t>,<<e,n>,e>> $0 (dist:<e,<e,n>> (the:<<e,t>,e> agent:<e,t>))))
//TODO: "nearest block to you"

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