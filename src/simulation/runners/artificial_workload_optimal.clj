(ns simulation.runners.artificial-workload-optimal
  (:use clj-predicates.core
        simulation.core
        clojure.pprint)
  (:require [simulation.io :as io]
            [simulation.algorithms.markov :as markov]
            [simulation.runners.optimal :as optimal]))

(def time-step 300)
(def host {:mips 1000})

(defn -main [& args]
  (do
    (pprint args) 
    (let [vms (first (io/read-pregenerated-workload (nth args 0)))
          state-config (read-string (nth args 1))
          otf (read-string (nth args 2))
          number-of-states (inc (count state-config))
          utilization (host-utilization-history host vms)
          state-history (markov/utilization-to-states state-config utilization)] 
      (do
        (println state-history)
        (println (optimal/calculate-otf state-history number-of-states))
        (pprint (optimal/solve otf state-history number-of-states))))))

; lein run -m simulation.runners.artificial-workload-optimal workload/artificial "[1.0]" 0.3
; otf 0.2987012987012987
; time 69300
; lein run -m simulation.runners.artificial-workload-optimal workload/artificial "[1.0]" 0.2
; otf 0.2
; time 21000
; lein run -m simulation.runners.artificial-workload-optimal workload/artificial "[1.0]" 0.1
; otf 0.1
; time 6000