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
        window-sizes (read-string (nth args 2))
        otf (read-string (nth args 3))
        step (read-string (nth args 4))
        number-of-states (inc (count state-config))
        vms (io/read-pregenerated-workload input)
        algorithm (partial markov/markov-multisize step otf window-sizes state-config)          
        results (map #(do
                        (markov/reset-multisize-state window-sizes number-of-states)
                        (run-simulation 
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
                          ))
                     vms)] 
    (do
      (pprint results))))

;lein run -m simulation.runners.artificial-workload-markov-multisize workload/artificial2 "[1.0]" "[30 40 50 60 70 80 90 100]" 0.3 0.5
;({:total-time 75600.0,
;  :overloading-time 22500.0,
;  :overloading-time-fraction 0.2976190476190476,
;  :execution-time 756.232366})
