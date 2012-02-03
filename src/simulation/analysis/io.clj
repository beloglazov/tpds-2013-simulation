(ns simulation.analysis.io)

(defn compute [input output]
  (let [data (flatten (read-string (slurp input)))] 
    (do 
      (spit output "TotalTime\tOverloadingTime\tOTF\tAlgorithm\tParam\tStates\tStateConfig\tOtfStateConfig\tExecutionTime\tExecutionTimeNormalized\n")
      (map #(spit output (str 
                           (:total-time %) "\t"
                           (:overloading-time %) "\t"
                           (:overloading-time-fraction %) "\t"
                           (:algorithm %) "\t"
                           (:param %) "\t"
                           (count (:state-config %)) "\t"
                           (apply str (interpose "-" (:state-config %))) "\t"
                           (str (:param %) "-" (apply str (interpose "-" (:state-config %)))) "\t"
                           (:execution-time %) "\t"
                           (/ (double (:execution-time %)) 
                              (/ (double (:total-time %))
                                 300)) "\n")
                  :append true) 
           data))))