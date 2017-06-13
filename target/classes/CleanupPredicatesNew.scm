(import "burlap.mdp.core.oo.state.*")
(import "amdp.cleanup.state.*")
(import "amdp.cleanup.CleanupDomain")

;rather than handling objects directly,
;determiners and functions will deal with states and IDs

(define classType
    (lambda (type)
        (lambda (objID state)
                (.equals (.className (.object state objID)) type))))

(define getClassType
    (lambda (objID state)
        (.className (.object state objID))))

;class predicates
(define door
    (classType {door}))
(define agent
    (classType {agent}))
(define block
    (classType {block}))
(define room
    (classType {room}))

;attributes

(define checkAttribute
    (lambda (attribute)
        (lambda (value)
            (lambda (objID state)
                (let ((object (.object state objID)))
                (if (.contains (.variableKeys object) attribute)
                (.equals (.get object attribute) value)
                #f))))))

(define color
    (checkAttribute {colour}))

(define shape
    (checkAttribute {shape}))

(define red
    (color {red}))
(define blue
    (color {blue}))
(define green
    (color {green}))

(define basket
    (shape {basket}))

;TODO: change order/composition of variables
;technically can do whatever by defining new lambda functions
(define in
    (lambda (objID regionID state)
        (let ((object (.object state objID)) (region (.object state regionID)))
            (CleanupDomain.regionContainsPoint region (.get object {x}) (.get object {y}) #f))))

;determiner!
;finds first element that satisfies predicate starting from the index
;still probably a better way to do this
(define satisfiesPredicateFrom
    (lambda (predicate state index)
        (let ((objID (.name (.get (.objects state) index))))
            (if (predicate objID state)
                objID
                (if (= index 0)
                    #null
                    (satisfiesPredicateFrom predicate state (- index 1)))))))

(define satisfiesPredicate
    (lambda (predicate state)
        (satisfiesPredicateFrom predicate state (- (.size (.objects state)) 1))))

(define getAgent
    (lambda (state)
        (satisfiesPredicate agent state)))
