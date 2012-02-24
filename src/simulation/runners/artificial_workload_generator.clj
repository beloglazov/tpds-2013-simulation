(ns simulation.runners.artificial-workload-generator
  (:use clj-predicates.core
        simulation.core)
  (:require [simulation.workload-generator :as workload-generator]
            [clojure.data.json :as json])
  (:gen-class))

(def time-step 300.0)
(def time-limit 160)
(def migration-time 30.0)

(def workloads [{:until 60
                 :transitions [[1.0 0.0]
                               [1.0 0.0]]}
                {:until 86
                 :transitions [[0.0 1.0]
                               [0.0 1.0]]}
                {:until 200
                 :transitions [[1.0 0.0]
                               [1.0 0.0]]}])
(def state-config [1.0])

(defn -main [& args]
  (let [output-path (nth args 0)
        vms [(workload-generator/get-vms workloads state-config time-limit)]
        utilization (:utilization (ffirst vms))]
    (do
      (println "OTF: " (double (/ (count (filter #(>= % 1) utilization)) (count utilization))))
      (spit output-path (json/json-str vms)))))
