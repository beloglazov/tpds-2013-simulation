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
(def migration-time 30.0)
(def host {:mips 1000})

(defn -main [& args]
  (let [input (nth args 0)
        output (nth args 1)
        state-config (read-string (nth args 2))
        otf (read-string (nth args 3))
        step (read-string (nth args 4))
        n (read-string (nth args 5))
        vms (repeat n (first (io/read-pregenerated-workload input)))
        algorithm (partial markov/markov-optimal workload-generator/workloads step otf state-config)          
        results (map #(run-simulation 
                        algorithm 
                        otf
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
                          (apply + (map #(:otf %) results))
                          (count results)))
        avg-time (double (/ 
                           (apply + (map #(:time %) results))
                           (count results)))
        time-otf (/ (/ avg-time avg-otf) 1000)] 
    (do
      (println "avg-otf" avg-otf)
      (println "avg-time" avg-time)
      (println "time-otf" time-otf)
      (io/spit-results output 
                       (map #(assoc % 
                                    :algorithm "markov-optimal"
                                    :param otf
                                    :state-config state-config) 
                            results))
      (prn))))
