(ns simulation.runners.optimal
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
          number-of-states (inc (count state-config))
          results (map #(solve otf 
                               (markov/utilization-to-states state-config 
                                                             (host-utilization-history host %)) 
                               number-of-states) 
                       workload)
          avg-otf (double (/ 
                            (apply + (map #(:otf %) results))
                            (count results)))
          avg-time (double (/ 
                             (apply + (map #(:time %) results))
                             (count results)))
          time-otf (/ (/ avg-time avg-otf) 1000)] 
      (do
        (println "avg-otf" avg-otf)
        (println "avg-time" avg-time)
        (println "time-otf" time-otf)))))
