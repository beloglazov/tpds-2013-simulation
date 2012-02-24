(ns simulation.runners.json-to-csv
  (:require [clojure.data.json :as json]))

(defn -main [input output]
  (let [data (json/read-json (slurp input))]
    (do 
      (spit output "Time\tOTF\tAlgorithm\tParam\tStates\tStateConfig\tOtfStateConfig\tExecutionTime\tExecutionTimeNormalized\n")
      (doall
        (map #(spit output (str 
                             (:time %) "\t"
                             (:otf %) "\t"
                             (:algorithm %) "\t"
                             (:param %) "\t"
                             (count (:state-config %)) "\t"
                             (apply str (interpose "-" (:state-config %))) "\t"
                             (str (:param %) "-" (apply str (interpose "-" (:state-config %)))) "\t"
                             (:execution-time %) "\t"
                             (/ (double (:execution-time %)) 
                                (/ (double (:time %))
                                   300)) "\n")
                    :append true) 
             data))
      (println "done"))))