(import "lambdas.LambdaFunctions.Determiners")
(import "lambda.LambdaFunctions.Relations")

;re-implementing CleanupPredicates.scm using as many Java internals as possible

;ATTRIBUTES




;DETERMINERS
;used for definite determiner
(define satisfiesPredicate
    (lambda (state)
        (lambda (predicate)
            (Determiners.satisfiesPredicate state predicate))))

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