(import "lambdas.LambdaFunctions.Determiners")
(import "lambdas.LambdaFunctions.Relations")
(import "lambdas.LambdaFunctions.Attributes")

;re-implementing CleanupPredicates.scm using as many Java internals as possible

;ATTRIBUTES

(define class
    (lambda (type)
        (lambda (object)
            (lambda (state)
                (Attributes.checkClass type object state)))))

;class predicates
(define door
    (class {door}))
(define agent
    (class {agent}))
(define block
    (class {block}))
(define room
    (class {room}))

;checking object attribute
(define checkAttribute
    (lambda (attribute)
        (lambda (value)
            (lambda (objID)
                (lambda (state)
                    (Attributes.checkAttribute attribute value objID state))))))

(define color
    (checkAttribute {colour}))

(define red
    (color {red}))
(define blue
    (color {blue}))
(define green
    (color {green}))
(define yellow
    (color {yellow}))

(define shape
    (checkAttribute {shape}))

(define basket
    (shape {basket}))

(define size
    (lambda (object)
        (lambda (state)
            (Attributes.size object state))))

;DETERMINERS
;used for definite determiner
(define satisfiesPredicate
    (lambda (state)
        (lambda (predicate)
            (Determiners.satisfiesPredicate state predicate))))

;strict definite determiner
(define definiteDeterminer
    (lambda (state)
        (lambda (predicate)
            (Determiners.definiteDeterminer state predicate))))

;ARGMAX/MIN
;argmax also initialized wrt state
(define argmax
    (lambda (state)
        (lambda (predicate)
            (lambda (measure)
                (Determiners.argmax state measure predicate)))))

(define argmin
    (lambda (state)
        (lambda (predicate)
            (lambda (measure)
                (Determiners.argmin state measure predicate)))))

;RELATIONS
;near predicate
(define (near obj1 obj2)
    (lambda (state)
        (Relations.near obj1 obj2 state)))

;in predicate
(define (in entity region)
    (lambda (state)
        (Relations.in entity region state)))

;left and right predicates
(define (left obj1 obj2)
    (lambda (state)
        (Relations.left obj1 obj2 state)))

(define (right obj1 obj2)
    (lambda (state)
        (Relations.right obj1 obj2 state)))


;numerical relations
(define (dist e1 e2)
    (lambda (state)
        (Relations.dist e1 e2 state)))

(define (left obj1 obj2)
    (lambda (state)
        (Relations.left obj1 obj2 state)))

(define (right obj1 obj2)
    (lambda (state)
        (Relations.right obj1 obj2 state)))

;HELPER FUNCTIONS
;list helpers

(define (first lst) (car lst))

(define (rest lst) (cdr lst))

;defining conjunctions w.r.t. state-dependent truth values
;arguments: arbitrary number, each arg is map from <s, t>
;args is the list of predicates

;use andmap function  - much shorter!
(define (and_ . preds)
    (lambda (state)
        (andmap (lambda (pred) (pred state)) preds)))


;single-argument andmap implementation
;short-circuiting behavior as well
(define (andmap pred lst)
    (if (null? lst)
        #t
        (if (pred (first lst))
            (andmap pred (rest lst))
            #f)))