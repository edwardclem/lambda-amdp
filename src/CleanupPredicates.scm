(import "burlap.mdp.core.oo.state.*")
(import "amdp.cleanup.state.*")
(import "amdp.cleanup.CleanupDomain")
(import "java.util.Collections")
(import "lambdas.LambdaFunctions.Determiners")


;now working with assumption that "state" parameter will be "hidden"
;entity is implemented as a string object ID, essentially a pointer to an object
;this requires a slight modification to the type system:
;all functions of the type <e, t> are instead "collapsed" functions of the type <e, <s, t>>
;t' == <s, t>, state dependent truth value
;which implies a state-dependent truth value that maps to a boolean when applied to a state
;instead of defining new evaluation environment for subexpressions, simply evaluate stateful predicates
;with respect to the correct state


;not actually that different from CleanupPredicatesNew.scm, separating out lambda (state)

(define class
    (lambda (type)
        (lambda (entity)
            (lambda (state)
                (.equals (.className (.object state entity)) type)))))

;class predicates
(define door
    (class {door}))
(define agent
    (class {agent}))
(define block
    (class {block}))
(define room
    (class {room}))

;<attrib, <val, <e, <s, t>>>> == <attrib, <val, <e, t'>>>
(define checkAttribute
    (lambda (attribute)
        (lambda (value)
            (lambda (objID)
                (lambda (state)
                    (let ((object (.object state objID)))
                    (if (.contains (.variableKeys object) attribute)
                    (.equals (.get object attribute) value)
                    #f)))))))

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

;;internal helper function
(define (isRegion entity state)
    (or ((door entity) state) ((room entity) state)))

;spatial functions
;type-checking!!! limited vocab for now
;return false for if either is null
(define in
    (lambda (entity region)
        (lambda (state)
            (if (and (not (.equals entity {})) (not (.equals region {})) (isRegion region state) (not (isRegion entity state)) )
                (let ((objectProps (.object state entity)) (regionProps (.object state region)))
                (CleanupDomain.regionContainsPoint regionProps (.get objectProps {x}) (.get objectProps {y}) #f))
                #f))))

;distance function
;<e, <e, n>> == <e, <e, <s, n>>>

(define (2_norm nums)
    (sqrt (apply + (map (lambda (x) (* x x)) nums))))

;mean
(define (mean_1d . nums)
    (/ (apply + nums) (length nums)))

;takes in an instance of a door or room, returns the center point as a list of coords
(define (centerPoint entityProps)
    (let
        ((x (mean_1d (.get entityProps {right}) (.get entityProps {left})))
         (y (mean_1d (.get entityProps {top}) (.get entityProps {bottom}))))
         (list x y)))

;TODO: ensure this works for every object type
;TODO: define this accurately with respect to doors and rooms (some kind of argmin with respect to the other entities? or just center for now)
;TODO: safe to use instances or should I use IDs + states?
;returns list of coordinates, using centerpoint if room or door
(define (getLocation entityProps)
    (let
        ((entityClass (.className entityProps)))
        (if
            (or (.equals entityClass {agent}) (.equals entityClass {block}))
            (list (.get entityProps {x}) (.get entityProps {y}))
            (centerPoint entityProps))))


(define (dist e1 e2)
    (lambda (state)
        (let ((e1Props (.object state e1)) (e2Props (.object state e2)))
            (2_norm
                (map (lambda (x1 x2) (- x1 x2))
                    (getLocation e1Props) (getLocation e2Props))))))


(define (right e1 e2)
    (lambda (state)
    (if (and (not (.equals e1 {})) (not (.equals e2 {})))
        (let ((e1Props (.object state e1)) (e2Props (.object state e2)))
            (if
            (and (or ((block e1) state) ((agent e1) state)) (or ((block e2) state) ((agent e2) state)) (= (.get e1Props {y}) (.get e2Props {y})) (= (.get e1Props {x}) (+ (.get e2Props {x}) 1)))
                #t
                #f))
                #f)))

(define (left e1 e2)
    (lambda (state)
     (if (and (not (.equals e1 {})) (not (.equals e2 {})))
        (let ((e1Props (.object state e1)) (e2Props (.object state e2)))
            (if
            (and (or ((block e1) state) ((agent e1) state)) (or ((block e2) state) ((agent e2) state)) (= (.get e1Props {y}) (.get e2Props {y})) (= (.get e1Props {x}) (- (.get e2Props {x}) 1)))
                #t
                #f))
                #f)))




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


;definite determiner helpers

(define (iterateJavaList javaList)
    (do ((i 0 (+ i 1)))
        ((= i (.size javaList)) {done})  (.println System.out$ (.get javaList i) )))


;the update doesn't seem to work for currentObj? using set! instead
;TODO: update to resemble argmax
(define satisfiesPredicateOld
    (lambda (state)
        (lambda (predicate)
            (let ((shuffleList (.objects state)))
            ;TODO: fill shuffle
            (Collections.shuffle shuffleList)
            (do ( (i 0 (+ i 1)) (correctObj {}))
                ((or (= i (.size (shuffleList))) (not (equal? correctObj {}))) correctObj)
                (if ((predicate (.name (.get (shuffleList) i))) state)
                        (set! correctObj (.name (.get (shuffleList) i)))))))))

;wrapper for Determiners.satisfiesPredicate
;re-implementing everything in Java

(define satisfiesPredicate
    (lambda (state)
        (lambda (predicate)
            (Determiners.satisfiesPredicate state predicate))))

;argmax/min
;initialized wrt some state, then applied
(define argmax
    (lambda (state)
        (lambda (predicate measurement)
            (do
                ((i 0 (+ i 1)) (maxObj {}) (currentObj (.name (.get (.objects state) 0)) (.name (.get (.objects state) i))))
                ((= i (.size (.objects state))) maxObj)
                    (if ((predicate currentObj) state)
                        (cond
                            ((.equals maxObj {}) (set! maxObj currentObj))
                            ((> ((measurement currentObj) state) ((measurement maxObj) state)) (set! maxObj CurrentObj))))))))


;room size function
(define (roomSize roomID)
        (lambda (state)
            (let ((roomProps (.object state roomID)))
                (*
                    (- (.get roomProps {right}) (.get roomProps {left}))
                    (- (.get roomProps {top}) (.get roomProps {bottom}))))))

;simple near function - if distance between two objects is 1, then near
;TODO: move this to Java? move others to java, use Scheme predicates as wrappers?
;checking null value
(define (near obj1 obj2)
    (lambda (state)
        (if (not (or (.equals obj1 {}) (.equals obj2 {})))
        (= ((dist obj1 obj2) state) 1.0)
        #f)))
