(ns simulation.runners.artificial-workload-optimal
  (:use clj-predicates.core
        simulation.core
        clojure.pprint)
  (:require [simulation.io :as io]
            [simulation.algorithms.markov :as markov]
            [simulation.runners.optimal :as optimal]))

(def time-step 300)
(def host {:mips 1000})
(def migration-time 30.0)

(defn -main [& args]
  (do
    (println args) 
    (let [vms (first (io/read-pregenerated-workload (nth args 0)))
          output (nth args 1)
          state-config (read-string (nth args 2))
          otf (read-string (nth args 3))
          number-of-states (inc (count state-config))
          utilization (host-utilization-history host vms)
          state-history (markov/utilization-to-states state-config utilization)
          results (optimal/solve otf state-history number-of-states (/ migration-time time-step))] 
      (do
;        (println state-history)
        (println (optimal/calculate-otf state-history number-of-states migration-time))
        (pprint results)
        (io/spit-results output 
                         [(assoc results 
                                 :algorithm "optimal"
                                 :param otf
                                 :state-config state-config
                                 :violation (if (> (:otf results) otf) 1 0)
                                 :execution-time 0.0)])
        (prn)))))

