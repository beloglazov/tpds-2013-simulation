(ns simulation.algorithms.markov.genetic-test
  (:use simulation.algorithms.markov.genetic
        midje.sweet))

(fact
  (in-state-n? [1 0 0]) => false
  (in-state-n? [0 1 0]) => false
  (in-state-n? [0 0 1]) => true)