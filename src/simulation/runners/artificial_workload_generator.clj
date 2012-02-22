(ns simulation.runners.artificial-workload-generator
  (:use clj-predicates.core
        simulation.core)
  (:require [simulation.workload-generator :as workload-generator]
            [clojure.data.json :as json])
  (:gen-class))

(def time-step 300.0)
(def time-limit 288)
(def migration-time 20.0)
(def workloads [{:until 60
                 :transitions [[0.8 0.2]
                               [1.0 0.0]]}
                {:until 110
                 :transitions [[0.5 0.5]
                               [1.0 0.0]]}
                {:until 130
                 :transitions [[0.2 0.8]
                               [1.0 0.0]]}
                {:until 180
                 :transitions [[0.8 0.2]
                               [0.6 0.4]]}
                {:until 190
                 :transitions [[0.8 0.2]
                               [0.3 0.7]]}
                {:until 210
                 :transitions [[0.8 0.2]
                               [0.8 0.2]]}
                {:until 250
                 :transitions [[0.2 0.8]
                               [0.8 0.2]]}
                {:until 288
                 :transitions [[0.2 0.8]
                               [0.2 0.8]]}])

;(def workloads [{:until 30
;                 :transitions [[0.8 0.2]
;                               [1.0 0.0]]}
;                {:until 80
;                 :transitions [[0.5 0.5]
;                               [1.0 0.0]]}
;                {:until 100
;                 :transitions [[0.2 0.8]
;                               [1.0 0.0]]}
;                {:until 150
;                 :transitions [[0.8 0.2]
;                               [0.6 0.4]]}
;                {:until 160
;                 :transitions [[0.8 0.2]
;                               [0.3 0.7]]}
;                {:until 180
;                 :transitions [[0.8 0.2]
;                               [0.8 0.2]]}
;                {:until 220
;                 :transitions [[0.2 0.8]
;                               [0.8 0.2]]}
;                {:until 288
;                 :transitions [[0.2 0.8]
;                               [0.2 0.8]]}])
(def state-config [1.0])

(defn -main [& args]
  (let [output-path (nth args 0)
        vms [(workload-generator/get-vms workloads state-config time-limit)]
        utilization (:utilization (ffirst vms))]
    (do
      (println "OTF: " (double (/ (count (filter #(>= % 1) utilization)) (count utilization))))
      (spit output-path (json/json-str vms)))))

; lein run -m simulation.runners.artificial-workload-generator "workload/artificial"
; lein run -m simulation.runners.artificial-workload-generator "workload/artificial2"