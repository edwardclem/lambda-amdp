(import "burlap.mdp.core.oo.state.*")
(import "amdp.cleanup.state.*")
(import "amdp.cleanup.CleanupDomain")

;predicates handling objects directly

(define getStateObjects
    (lambda (state)
        (.objects state)))
;base function for class name predicates
(define classType
    (lambda (className)
        (lambda (object)
            (.equals (.className object) className))))
;some class predicates
(define door
    (classType {door}))
(define agent
    (classType {agent}))
(define block
    (classType {block}))
(define room
    (classType {room}))
;base function for attribute predicates
;essentially re-implementing PF_IsColor
;TODO: handle types, use Burlap types to inform attribute values
(define checkAttribute
    (lambda (attribute)
        (lambda (value)
            (lambda (object)
                (if (.contains (.variableKeys object) attribute)
                (.equals (.get object attribute) value)
                #f)))))
(define color
    (checkAttribute {colour}))
(define red
    (color {red}))
(define blue
    (color {blue}))
(define green
    (color {green}))
;Spatial prop functions
(define in
    (lambda (object room)
        (CleanupDomain.regionContainsPoint room (.get object {x}) (.get object {y}) #f)))
;TODO: probabilistic test for "near"
;determiners
;define a determiner relative to a state, then re-define determiner if state changes!
;enables temporal determiners
;finds a state object that satisfies the predicate

;Testing iteration over all items in a Java List
;starting at end of list, going down
;messy iteration b/c "first" and "rest" don't work with Java lists
(define iterateFrom
    (lambda (JList index)
        (write (.get JList index))
        (if (> index 0) (iterateFrom JList (- index 1)))))
;iterate through all
(define iterate
    (lambda (JList)
         (iterateFrom JList (- (.size JList) 1) )))

;finds an (the first for now) element in the list that satisfies a predicate
;TODO: re-write this as a MapReduce procedure?
;Would need to convert Java List to Scheme list or write Map and Reduce procedures
;for Java Lists in scheme, or use Java ForEach followed by a reduction
;maybe more trouble than it's worth here
(define satisfiesPredicateFrom
    (lambda (predicate JList index)
        (if (predicate (.get JList index))
            (.get JList index)
            (if (= index 0)
                #null
                (satisfiesPredicateFrom predicate JList (- index 1))))))

(define satisfiesPredicate
    (lambda (predicate JList)
        (satisfiesPredicateFrom predicate JList (- (.size JList) 1))))

(define getAgent
    (lambda (JList)
        (satisfiesPredicate agent JList)))

;TODO: use object types to inform search space
;TODO: include param with respect to object types?
(define determiner
    (lambda (state)
            (lambda (predicate)
                (satisfiesPredicate predicate (.objects state)))))



