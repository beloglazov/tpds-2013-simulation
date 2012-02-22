(ns simulation.runners.artificial-workload-markov-multisize
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
        window-sizes (read-string (nth args 3))
        n (read-string (nth args 4))
        vms (repeat n (first (io/read-pregenerated-workload input)))
        algorithm (partial markov/markov-multisize 0.01 otf window-sizes state-config)          
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

; lein run -m simulation.runners.artificial-workload-markov-multisize workload/artificial "[1.0]" "[30 60 90]" 0.3 100