(ns simulation.runners.universal
  (:use clj-predicates.core
        simulation.core
        clojure.pprint)
  (:require [simulation.algorithms :as algorithms]
            [simulation.algorithms.markov :as markov]
            [simulation.io :as io])
  (:gen-class))

(def time-step 300)
(def migration-time 20)
(def host {:mips 3000})

(defn -main [& args]
  (let [workload (nth args 0)
        algorithm-name (nth args 1)
        param (Double/valueOf (nth args 2))
        state-config (if (= algorithm-name "markov")
                       (if (= 6 (count args)) 
                         [(Double/valueOf (nth args 3))]
                         [(Double/valueOf (nth args 3)) (Double/valueOf (nth args 4))])
                       [])
        max-generations (if (= algorithm-name "markov")
                          (if (= 6 (count args)) 
                            (Double/valueOf (nth args 4))
                            (Double/valueOf (nth args 5)))
                          0)
        output (if (= 6 (count args)) 
                 (nth args 5)
                 (nth args 6))
        algorithm (cond
                    (= algorithm-name "no-migrations") algorithms/no-migrations
                    (= algorithm-name "thr") (partial algorithms/thr param)
                    (= algorithm-name "mad") (partial algorithms/mad param)
                    (= algorithm-name "iqr") (partial algorithms/iqr param)
                    (= algorithm-name "lr") (partial algorithms/loess param)
                    (= algorithm-name "lrr") (partial algorithms/loess-robust param)
                    (= algorithm-name "otf") (partial algorithms/otf param)
                    (= algorithm-name "markov") (partial markov/markov param state-config max-generations))] 
    (do 
      (println workload)
      (println algorithm-name)
      (println param)
      (println state-config)
      (println max-generations)
      (println output)
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
                       (io/read-pregenerated-workload workload))
          avg-otf (double (/ 
                            (apply + (map #(:overloading-time-fraction %) results))
                            (count results)))
          avg-time (double (/ 
                             (apply + (map #(:total-time %) results))
                             (count results)))
          time-otf (/ (/ avg-time avg-otf) 1000)] 
      (do
        (spit output (vec (map #(assoc % 
                                       :algorithm algorithm-name
                                       :param param
                                       :state-config state-config) 
                               results))))))))