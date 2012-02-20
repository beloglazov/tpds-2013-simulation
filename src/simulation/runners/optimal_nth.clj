(ns simulation.runners.optimal-nth
  (:use simulation.core
        clj-predicates.core
        clojure.pprint)
  (:require [simulation.io :as io]
            [simulation.algorithms.markov :as markov]))

(def time-step 300)
(def host {:mips 3000})

(defn calculate-otf [state-history number-of-states]
  {:pre [(coll? state-history)
         (posnum? number-of-states)]
   :post [(not-negnum? %)]}
  (double (/ (count (filter #{(dec number-of-states)} state-history))
             (count state-history))))

(defn solve [otf-constraint state-history number-of-states]
  {:pre [(posnum? otf-constraint)
         (coll? state-history)
         (posnum? number-of-states)]
   :post [(map? %)]}
  (loop [states (reverse state-history)]
    (let [otf (calculate-otf states number-of-states)] 
      (if (<= otf otf-constraint)
        {:otf otf
         :time (* time-step (count states))}
        (recur (rest states))))))

(defn -main [& args]
  (do
    (pprint args) 
    (let [workload (io/read-pregenerated-workload (nth args 0))
          state-config (read-string (nth args 1))
          otf (read-string (nth args 2))
          n (read-string (nth args 3))
          vms (nth workload n)
          number-of-states (inc (count state-config))
          utilization (host-utilization-history host vms)
          state-history (markov/utilization-to-states state-config utilization)] 
      (do
        (println state-history)
        (println (calculate-otf state-history number-of-states))
        (pprint (solve otf state-history number-of-states))))))
