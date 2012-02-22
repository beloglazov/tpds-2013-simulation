(ns simulation.runners.artificial-workload-markov-optimal
  (:use clj-predicates.core
        simulation.core
        clojure.pprint)
  (:require [simulation.runners.artificial-workload-generator :as workload-generator]
            [simulation.algorithms.markov :as markov]
            [simulation.io :as io])
  (:gen-class))

(def time-step 300.0)
(def time-limit 288)
(def migration-time 20.0)
(def host {:mips 1000})

(defn -main [& args]
  (let [input (nth args 0)
        state-config (read-string (nth args 1))
        otf (read-string (nth args 2))
        n (read-string (nth args 3))
        vms (repeat n (first (io/read-pregenerated-workload input)))
        algorithm (partial markov/markov-optimal workload-generator/workloads 0.5 otf state-config)          
        results (map #(run-simulation 
                        algorithm 
                        time-step
                        migration-time
                        host
                        %
                        ;(fn [step step-vms overloading-steps]
                        ;  (println "=====================================================")
                        ;  (println "step:" step)
                        ;  (println "mips:" (current-vms-mips step-vms))
                        ;  (println "utilization:" (double (/ (current-vms-mips step-vms) (:mips host))))
                        ;  (println "otf:" (double (/ overloading-steps step))))
                        )
                     vms)
        avg-otf (double (/ 
                          (apply + (map #(:overloading-time-fraction %) results))
                          (count results)))
        avg-time (double (/ 
                           (apply + (map #(:total-time %) results))
                           (count results)))
        time-otf (/ (/ avg-time avg-otf) 1000)] 
    (do
      (pprint results)
      (println "avg-otf" avg-otf)
      (println "avg-time" avg-time)
      (println "time-otf" time-otf))))

; TODO: need to understand why markov-optimal is so conservative in terms of the resulting OTF

; lein run -m simulation.runners.artificial-workload-markov-optimal workload/artificial "[1.0]" 0.3 100
; avg-otf 0.24669333132314228
; avg-time 34860.0
; lein run -m simulation.runners.artificial-workload-markov-optimal workload/artificial "[1.0]" 0.2 100
; avg-otf 0.1673347666782451
; avg-time 10839.0
; lein run -m simulation.runners.artificial-workload-markov-optimal workload/artificial "[1.0]" 0.1 100
; avg-otf 0.012307692307692304
; avg-time 1365.0