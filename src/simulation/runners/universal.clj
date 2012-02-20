(ns simulation.runners.universal
  (:use clj-predicates.core
        simulation.core
        clojure.java.io
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
        output (nth args 3)
        state-config (if (> (count args) 4)
                       (read-string (nth args 4))
                       nil)
        step (if (> (count args) 5)
               (Double/valueOf (nth args 5))
               nil)
        window-sizes (if (> (count args) 6)
                       (read-string (nth args 6))
                       nil)
        number-of-states (inc (count state-config))
        algorithm (cond
                    (= algorithm-name "no-migrations") algorithms/no-migrations
                    (= algorithm-name "thr") (partial algorithms/thr param)
                    (= algorithm-name "mad") (partial algorithms/mad param)
                    (= algorithm-name "iqr") (partial algorithms/iqr param)
                    (= algorithm-name "lr") (partial algorithms/loess param)
                    (= algorithm-name "lrr") (partial algorithms/loess-robust param)
                    (= algorithm-name "otf") (partial algorithms/otf param)
                    (= algorithm-name "markov-single-window-genetic") 
                    (partial markov/markov-single-window-genetic param state-config 200)
                    (= algorithm-name "markov-single-window-bruteforce") 
                    (partial markov/markov-single-window-bruteforce param state-config 1)
                    (= algorithm-name "markov-multisize") 
                    (partial markov/markov-multisize step param window-sizes state-config))] 
    (do 
      (println workload)
      (println algorithm-name)
      (println param)
      (println output)
      (println state-config)
      (println step)
      (println window-sizes)
      (println number-of-states)
      (let [results (map #(do
                            (when (= algorithm-name "markov-multisize")
                              (markov/reset-multisize-state window-sizes number-of-states)) 
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
                         [(first (io/read-pregenerated-workload workload))])
            avg-otf (double (/ 
                              (apply + (map #(:overloading-time-fraction %) results))
                              (count results)))
            avg-time (double (/ 
                               (apply + (map #(:total-time %) results))
                               (count results)))
            time-otf (/ (/ avg-time avg-otf) 1000)] 
        (do
          (println "avg-otf" avg-otf)
          (println "avg-time" avg-time)
          (println "time-otf" time-otf)
          (spit output (vec (map #(assoc % 
                                         :algorithm algorithm-name
                                         :param param
                                         :state-config state-config) 
                                 results))))))))