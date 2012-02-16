(ns simulation.algorithms.markov.l-2-states
  (:use clj-predicates.core
        clojure.pprint))

(defn l0 [[p0 p1] [[p00 p01]
                   [p10 p11]]
          [m0 m1]]
  {:pre [(number? p0)
         (number? p1)
         (number? p00)
         (number? p01)
         (number? p10)
         (number? p11)
         (number? m0)
         (number? m1)]
   :post [(number? %)]}
  (do 
;    (pprint [[p0 p1] [[p00 p01]
;                      [p10 p11]]
;             [m0 m1]])
    (/ (+ (* p0 (- (+ (* (* -1 m1) p11) p11) 1)) (* (- (* m1 p1) p1) p10)) (- (+ (+ (- (* p00 (+ (+ (- (* m1 (- p11 (* m0 p11))) p11) (* m0 (- p11 1))) 1)) (* m1 p11)) p11) (* (+ (- (* m1 (- (* m0 p01) p01)) (* m0 p01)) p01) p10)) 1))))

; ((p0 * (-1 * m1 * p11 + p11 - 1) + (m1 * p1 - p1) * p10) / (p00 * (m1 * (p11 - m0 * p11) - p11 + m0 * (p11 - 1) + 1) - m1 * p11 + p11 + (m1 * (m0 * p01 - p01) - m0 * p01 + p01) * p10 - 1))


(defn l1 [[p0 p1] [[p00 p01]
                   [p10 p11]]
          [m0 m1]]
  {:pre [(number? p0)
         (number? p1)
         (number? p00)
         (number? p01)
         (number? p10)
         (number? p11)
         (number? m0)
         (number? m1)]
   :post [(number? %)]}
  (do
;    (pprint [[p0 p1] [[p00 p01]
;                      [p10 p11]]
;             [m0 m1]])
    (/ (* -1 (+ (+ (* p00 (- (* m0 p1) p1)) p1) (* p0 (- p01 (* m0 p01))))) (- (+ (+ (- (* p00 (+ (+ (- (* m1 (- p11 (* m0 p11))) p11) (* m0 (- p11 1))) 1)) (* m1 p11)) p11) (* (+ (- (* m1 (- (* m0 p01) p01)) (* m0 p01)) p01) p10)) 1))))

; (-1 * (p00 * (m0 * p1 - p1) + p1 + p0 * (p01 - m0 * p01)) / (p00 * (m1 * (p11 - m0 * p11) - p11 + m0 * (p11 - 1) + 1) - m1 * p11 + p11 + (m1 * (m0 * p01 - p01) - m0 * p01 + p01) * p10 - 1))


(def ls [l0 l1])

(defn calculate [p0 p m]
  {:pre [(coll? p0)
         (coll? p)
         (coll? m)]
   :post [(coll? %)]}
  [(l0 p0 p m)
   (l1 p0 p m)])
