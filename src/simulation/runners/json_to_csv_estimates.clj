(ns simulation.runners.json-to-csv-estimates
  (:require [clojure.data.json :as json]))

(defn -main [input output]
  (let [data (json/read-json (slurp input))
        time-history (:time data)
        windows (:selected-windows data)
        estimates (:best-estimates data)]
    (do
      (spit output "Time\tWindow00\tWindow01\tWindow10\tWindow11\tEstimate00\tEstimate01\tEstimate10\tEstimate11\n")
      (doall
        (map (fn [time-step 
                  window00 window01 window10 window11
                  estimate00 estimate01 estimate10 estimate11]
               (spit output (str
                              time-step "\t"
                              window00 "\t"
                              window01 "\t"
                              window10 "\t"
                              window11 "\t"
                              estimate00 "\t"
                              estimate01 "\t"
                              estimate10 "\t"
                              estimate11 "\n")
                    :append true)) 
             (reverse time-history)
             (reverse (get-in windows [0 0])) (reverse (get-in windows [0 1])) 
             (reverse (get-in windows [1 0])) (reverse (get-in windows [1 1]))
             (reverse (get-in estimates [0 0])) (reverse (get-in estimates [0 1])) 
             (reverse (get-in estimates [1 0])) (reverse (get-in estimates [1 1]))))
      (println "done"))))