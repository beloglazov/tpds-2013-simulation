(ns simulation.runners.optimizer
  (:use simulation.algorithms.markov.bruteforce 
        clj-predicates.core
        clojure.pprint)
  (:require [simulation.algorithms.markov.nlp :as nlp]
            [simulation.algorithms.markov.l-probabilities.l-2-states :as l2]))

(defn -main [& args]
  (do
    (pprint args) 
    (let [otf (read-string (nth args 0))
          time-in-states (read-string (nth args 1))
          time-in-state-n (read-string (nth args 2))
          state-vector (read-string (nth args 3))
          p (read-string (nth args 4))] 
      (time 
        (pprint 
          (optimize 0.01 1.0 otf (/ 20.0 300.0) l2/ls p state-vector time-in-states time-in-state-n))))))
