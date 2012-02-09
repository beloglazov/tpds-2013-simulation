(ns simulation.algorithms.markov.l-2-states
  (:use incanter.core
        clj-predicates.core))

(defn l0 [[p0 p1] [[c00 c01]
                   [c10 c11]]
          [m0 m1]]
  {:pre [(number? p0)
         (number? p1)
         (number? c00)
         (number? c01)
         (number? c10)
         (number? c11)
         (number? m0)
         (number? m1)]
   :post [(number? %)]}
  (/ (+ (+ (* c10 (+ (* c01 p1) (* c00 p1))) (* (* c10 m0) p1)) (* (+ (+ (* (+ (+ m0 c01) c00) m1) (* c10 m0)) (* (+ c01 c00) c10)) p0)) (+ (* (+ m0 c01) m1) (* c10 m0))))

; ($= (c10 * (c01 * p1 + c00 * p1) + c10 * m0 * p1 + ((m0 + c01 + c00) * m1 + c10 * m0 + (c01 + c00) * c10) * p0) / ((m0 + c01) * m1 + c10 * m0))


(defn l1 [[p0 p1] [[c00 c01]
                   [c10 c11]]
          [m0 m1]]
  {:pre [(number? p0)
         (number? p1)
         (number? c00)
         (number? c01)
         (number? c10)
         (number? c11)
         (number? m0)
         (number? m1)]
   :post [(number? %)]}
  (/ (+ (+ (+ (+ (+ (* m1 (+ (+ (+ (* m0 (+ (* (* 2 c01) p1) (* c00 p1))) (* (Math/pow m0 2) p1)) (* (Math/pow c01 2) p1)) (* (* c00 c01) p1))) (* m0 (+ (* c11 (+ (* (* 2 c01) p1) (* c00 p1))) (* c10 (+ (* (* 2 c01) p1) (* c00 p1)))))) (* (Math/pow m0 2) (+ (* c11 p1) (* c10 p1)))) (* c11 (+ (* (Math/pow c01 2) p1) (* (* c00 c01) p1)))) (* c10 (+ (* (Math/pow c01 2) p1) (* (* c00 c01) p1)))) (* (+ (+ (+ (* (+ (+ (* c01 m0) (Math/pow c01 2)) (* c00 c01)) m1) (* (+ (* c01 c11) (* c01 c10)) m0)) (* (+ (Math/pow c01 2) (* c00 c01)) c11)) (* (+ (Math/pow c01 2) (* c00 c01)) c10)) p0)) (+ (+ (* (+ (+ (+ (Math/pow m0 2) (* (+ (* 2 c01) c00) m0)) (Math/pow c01 2)) (* c00 c01)) m1) (* c10 (Math/pow m0 2))) (* (* (+ c01 c00) c10) m0))))

;($= (m1 * (m0 * (2 * c01 * p1 + c00 * p1) + m0 ** 2 * p1 + c01 ** 2 * p1 + c00 * c01 * p1) + m0 * (c11 * (2 * c01 * p1 + c00 * p1) + c10 * (2 * c01 * p1 + c00 * p1)) + m0 ** 2 * (c11 * p1 + c10 * p1) + c11 * (c01 ** 2 * p1 + c00 * c01 * p1) + c10 * (c01 ** 2 * p1 + c00 * c01 * p1) + ((c01 * m0 + c01 ** 2 + c00 * c01) * m1 + (c01 * c11 + c01 * c10) * m0 + (c01 ** 2 + c00 * c01) * c11 + (c01 ** 2 + c00 * c01) * c10) * p0) / ((m0 ** 2 + (2 * c01 + c00) * m0 + c01 ** 2 + c00 * c01) * m1 + c10 * m0 ** 2 + (c01 + c00) * c10 * m0))


(def ls [l0 l1])

(defn calculate [p c m]
  {:pre [(coll? p)
         (coll? c)
         (coll? m)]
   :post [(coll? %)]}
  [(l0 p c m)
   (l1 p c m)])
