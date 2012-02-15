(ns simulation.runners.artificial-workload
  (:use clj-predicates.core
        simulation.core
        clojure.pprint)
  (:require [simulation.workload-generator :as workload-generator] 
            [simulation.algorithms :as algorithms]
            [simulation.algorithms.markov :as markov]
            [simulation.io :as io])
  (:gen-class))

(def time-step 300)
(def time-limit 288)
(def migration-time 20)
(def workloads [{:until 144
                 :transitions [[0.8 0.2]
                               [1.0 0.0]]}
                {:until 288
                 :transitions [[0.5 0.5]
                               [1.0 0.0]]}])
(def state-config [0.9])
(def host (workload-generator/get-host))
(def vms [(workload-generator/get-vms workloads state-config time-limit)])
(def algorithm (partial markov/markov-optimal workloads 0.3 state-config 200))

(defn -main [& args]
  (let [results (map #(run-simulation 
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
    (pprint results)))