(ns simulation.algorithms.markov.genetic
  (:use simulation.algorithms.markov
        clj-predicates.core)
  (:require [simulation.algorithms.markov.nlp :as nlp]
            [clj-genetic.core :as genetic]
            [clj-genetic.objective :as genetic-objective]
            [clj-genetic.selection :as genetic-selection]
            [clj-genetic.recombination :as genetic-recombination]
            [clj-genetic.mutation :as genetic-mutation]
            [clj-genetic.crossover :as genetic-crossover]
            [clj-genetic.random-generators :as genetic-random-generators]))

(defn optimize
  "If the solution is infeasible, returns a command to migrate if the system is in state n, no migration otherwise.
   If the solution is feasible, returns the solution."
  [otf max-generations migration-time ls p state-vector time-in-states time-in-state-n]
  {:pre [(not-negnum? otf)
         (posnum? max-generations)
         (not-negnum? migration-time)
         (coll? ls)
         (coll? p)
         (coll? state-vector)
         (not-negnum? time-in-states)
         (not-negnum? time-in-state-n)]
   :post [(coll? %)]}
  (let [vars (count state-vector)
        limits (repeat vars {:min 0 :max 1})
        population-size (* 40 vars)
        solution (genetic/run
                     (genetic-objective/maximize (nlp/build-fitness ls state-vector p) 
                                                 (nlp/build-constraint otf migration-time ls state-vector p time-in-states time-in-state-n))
                     genetic-selection/binary-tournament-without-replacement
                     (partial genetic-recombination/crossover 
                              (partial genetic-crossover/simulated-binary-with-limits limits))
                     (genetic/terminate-max-generations? max-generations)
                     (genetic-random-generators/generate-population population-size limits))] 
    (if (:feasible solution) 
      (map #(if (< % 0) 0 %) (:solution solution))
      (if (in-state-n? state-vector) 
        (map #(* 1000 %) state-vector) ; migration probability from state n is 1
        (repeat (count state-vector) 0))))) ; migration probability from other states is 0
