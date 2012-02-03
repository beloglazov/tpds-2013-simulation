(ns simulation.algorithms.markov.l-3-states
  (:use incanter.core
        clj-predicates.core))

(defn l0 [[p0 p1 p2] [[c00 c01 c02]
                      [c10 c11 c12]
                      [c20 c21 c22]]
          [m0 m1 m2]]
  {:pre [(number? p0)
         (number? p1)
         (number? p2)
         (number? c00)
         (number? c01)
         (number? c02)
         (number? c10)
         (number? c11)
         (number? c12)
         (number? c20)
         (number? c21)
         (number? c22)
         (number? m0)
         (number? m1)
         (number? m2)]
   :post [(number? %)]}
  ($= (m0 * (c20 * (c12 * (p2 + p1) + c10 * (p2 + p1)) + c10 * c21 * (p2 + p1)) + c20 * (c12 * (c02 * (p2 + p1) + c01 * (p2 + p1) + c00 * (p2 + p1)) + c10 * (c02 * (p2 + p1) + c01 * (p2 + p1) + c00 * (p2 + p1))) + m1 * (c20 * (c02 * p2 + c01 * p2 + c00 * p2) + c20 * m0 * p2) + c10 * c21 * (c02 * (p2 + p1) + c01 * (p2 + p1) + c00 * (p2 + p1)) + m2 * (c10 * (c02 * p1 + c01 * p1 + c00 * p1) + c10 * m0 * p1) + (((m0 + c02 + c01 + c00) * m1 + (c12 + c10) * m0 + (c02 + c01 + c00) * c12 + (c02 + c01 + c00) * c10) * m2 + ((c21 + c20) * m0 + (c02 + c01 + c00) * c21 + (c02 + c01 + c00) * c20) * m1 + (c10 * c21 + (c12 + c10) * c20) * m0 + (c02 + c01 + c00) * c10 * c21 + ((c02 + c01 + c00) * c12 + (c02 + c01 + c00) * c10) * c20) * p0)/(((m0 + c02 + c01) * m1 + (c12 + c10) * m0 + (c02 + c01) * c12 + c02 * c10) * m2 + ((c21 + c20) * m0 + (c02 + c01) * c21 + c01 * c20) * m1 + (c10 * c21 + (c12 + c10) * c20) * m0)))

(defn l1 [[p0 p1 p2] [[c00 c01 c02]
                      [c10 c11 c12]
                      [c20 c21 c22]]
          [m0 m1 m2]]
  {:pre [(number? p0)
         (number? p1)
         (number? p2)
         (number? c00)
         (number? c01)
         (number? c02)
         (number? c10)
         (number? c11)
         (number? c12)
         (number? c20)
         (number? c21)
         (number? c22)
         (number? m0)
         (number? m1)
         (number? m2)]
   :post [(number? %)]}
  ($= (m1 * (m0 * (c21 * (p2 + p1) + c20 * p1) + c21 * (c02 * (p2 + p1) + c01 * (p2 + p1)) + c01 * c20 * (p2 + p1)) + m0 * (c21 * (c12 * (p2 + p1) + c11 * (p2 + p1) + c10 * (p2 + p1)) + c20 * (c12 * p1 + c11 * p1 + c10 * p1)) + c21 * (c12 * (c02 * (p2 + p1) + c01 * (p2 + p1)) + c11 * (c02 * (p2 + p1) + c01 * (p2 + p1)) + c10 * (c02 * (p2 + p1) + c01 * (p2 + p1))) + c20 * (c01 * c12 * (p2 + p1) + c01 * c11 * (p2 + p1) + c01 * c10 * (p2 + p1)) + m2 * (m1 * (m0 * p1 + c02 * p1 + c01 * p1) + m0 * (c12 * p1 + c11 * p1 + c10 * p1) + c12 * (c02 * p1 + c01 * p1) + c11 * (c02 * p1 + c01 * p1) + c10 * (c02 * p1 + c01 * p1)) + ((c01 * m1 + c01 * c12 + c01 * c11 + c01 * c10) * m2 + ((c02 + c01) * c21 + c01 * c20) * m1 + ((c02 + c01) * c12 + (c02 + c01) * c11 + (c02 + c01) * c10) * c21 + (c01 * c12 + c01 * c11 + c01 * c10) * c20) * p0)/(((m0 + c02 + c01) * m1 + (c12 + c10) * m0 + (c02 + c01) * c12 + c02 * c10) * m2 + ((c21 + c20) * m0 + (c02 + c01) * c21 + c01 * c20) * m1 + (c10 * c21 + (c12 + c10) * c20) * m0)))

(defn l2 [[p0 p1 p2] [[c00 c01 c02]
                      [c10 c11 c12]
                      [c20 c21 c22]]
          [m0 m1 m2]]
  {:pre [(number? p0)
         (number? p1)
         (number? p2)
         (number? c00)
         (number? c01)
         (number? c02)
         (number? c10)
         (number? c11)
         (number? c12)
         (number? c20)
         (number? c21)
         (number? c22)
         (number? m0)
         (number? m1)
         (number? m2)]
   :post [(number? %)]}
  ($= (m2 * (m0 * (c12 * (p2 + p1) + c10 * p2) + c12 * (c02 * (p2 + p1) + c01 * (p2 + p1)) + m1 * (m0 * p2 + c02 * p2 + c01 * p2) + c02 * c10 * (p2 + p1)) + m0 * (c22 * (c12 * (p2 + p1) + c10 * p2) + c21 * (c12 * (p2 + p1) + c10 * p2) + c20 * (c12 * (p2 + p1) + c10 * p2)) + c22 * (c12 * (c02 * (p2 + p1) + c01 * (p2 + p1)) + c02 * c10 * (p2 + p1)) + c21 * (c12 * (c02 * (p2 + p1) + c01 * (p2 + p1)) + c02 * c10 * (p2 + p1)) + c20 * (c12 * (c02 * (p2 + p1) + c01 * (p2 + p1)) + c02 * c10 * (p2 + p1)) + m1 * (m0 * (c22 * p2 + c21 * p2 + c20 * p2) + c22 * (c02 * p2 + c01 * p2) + c21 * (c02 * p2 + c01 * p2) + c20 * (c02 * p2 + c01 * p2)) + ((c02 * m1 + (c02 + c01) * c12 + c02 * c10) * m2 + (c02 * c22 + c02 * c21 + c02 * c20) * m1 + ((c02 + c01) * c12 + c02 * c10) * c22 + ((c02 + c01) * c12 + c02 * c10) * c21 + ((c02 + c01) * c12 + c02 * c10) * c20) * p0)/(((m0 + c02 + c01) * m1 + (c12 + c10) * m0 + (c02 + c01) * c12 + c02 * c10) * m2 + ((c21 + c20) * m0 + (c02 + c01) * c21 + c01 * c20) * m1 + (c10 * c21 + (c12 + c10) * c20) * m0)))

(def ls [l0 l1 l2])

(defn calculate [p c m]
  {:pre [(coll? p)
         (coll? c)
         (coll? m)]
   :post [(coll? %)]}
  [(l0 p c m)
   (l1 p c m)
   (l2 p c m)])

