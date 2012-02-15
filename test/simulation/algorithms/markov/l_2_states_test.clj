(ns simulation.algorithms.markov.l-2-states-test
  (:use simulation.algorithms.markov.l-2-states
        midje.sweet))

(def p [[0.4 0.6]
        [0.9 0.1]])

(def p0 [1 0])

(fact
  (l0 p0 p [0.2 0.8]) => (roughly 1.689)
  (l1 p0 p [0.2 0.8]) => (roughly 0.827)
  
  (l0 p0 p [0.62 0.38]) => (roughly 1.403)
  (l1 p0 p [0.62 0.38]) => (roughly 0.341))