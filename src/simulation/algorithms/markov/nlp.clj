(ns simulation.algorithms.markov.nlp
  (:use clj-predicates.core))

(defn build-fitness
  "Returns a fitness function, which is a sum of L functions"
  [ls state-vector q]
  {:pre [(coll? ls)
         (coll? state-vector)
         (coll? q)]
   :post [(fn? %)]}
  (fn [& m]
    (apply + 
           (map #(% state-vector q m) ls))))

(defn build-constraint
  "Returns a constraint for the optimization problem from the L functions
   Probably need to use the times in the states over the whole history,
   because otherwise, the system may prematurely migrate (the estimated otf
   for the recent period is high, but low for the whole history)"
  [otf migration-time ls state-vector c time-in-states time-in-state-n]
  {:pre [(not-negnum? otf)
         (not-negnum? migration-time)
         (coll? ls)
         (coll? state-vector)
         (coll? c)
         (not-negnum? time-in-states)
         (not-negnum? time-in-state-n)]
   :post [(coll? %)]}
  [[(fn [& m]
      (/ (+ migration-time time-in-state-n ((last ls) state-vector c m))
         (+ migration-time time-in-states (apply + 
                                                 (map #(% state-vector c m) ls)))))
    <=
    otf]])