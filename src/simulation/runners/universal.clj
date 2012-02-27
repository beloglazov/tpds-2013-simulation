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
;(def migration-time (* time-step 1))
(def migration-time 30)
(def host {:mips 12000}) ;4x3000

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
                    (= algorithm-name "otf-limit") (partial algorithms/otf-limit param)
                    (= algorithm-name "otf-migration-time") (partial algorithms/otf-migration-time param)
                    (= algorithm-name "otf-limit-migration-time") (partial algorithms/otf-limit-migration-time param)
                    (= algorithm-name "markov-single-window-genetic") 
                    (partial markov/markov-single-window-genetic param state-config 200)
                    (= algorithm-name "markov-single-window-bruteforce") 
                    (partial markov/markov-single-window-bruteforce param state-config 1)
                    (= algorithm-name "markov-multisize") 
                    (partial markov/markov-multisize step param window-sizes state-config))
        otf (if (contains-val? ["otf" "otf-limit" "otf-migration-time" "otf-limit-migration-time"
                                "markov-single-window-genetic" "markov-single-window-bruteforce"
                                "markov-multisize"]
                               algorithm-name)
              param
              0.0)] 
    (do 
      (println workload)
      (println algorithm-name)
      (println param)
      (let [results (map #(do
                            (when (= algorithm-name "markov-multisize")
                              (markov/reset-multisize-state window-sizes number-of-states)) 
                            (run-simulation 
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
                              ))
                         (io/read-pregenerated-workload workload))
            violations (count (filter #{1}
                                      (map #(:violation %) results)))
            avg-otf (double (/ 
                              (apply + (map #(:otf %) results))
                              (count results)))
            avg-time (double (/ 
                               (apply + (map #(:time %) results))
                               (count results)))
            time-otf (/ (/ avg-time avg-otf) 1000)] 
        (do
;          (pprint results)
          (println "violations" violations)
          (println "avg-otf" avg-otf)
          (println "avg-time" avg-time)
          (println "time-otf" time-otf)
          (io/spit-results output 
                           (map #(assoc % 
                                        :algorithm algorithm-name
                                        :param param
                                        :state-config state-config) 
                                results))
          (prn))))))

; OTFT fails (violates) because it does not contain a prediction part compared to the markov algorithm

