(ns simulation.core
  (:use clj-predicates.core
        clojure.pprint))

(defn current-vm-utilization [vm]
  {:pre [(map? vm)]
   :post [(not-negnum? %)]}
  (if-let [utilization (last (vm :utilization))]
    utilization 0.0))

(defn current-vm-mips [vm]
  {:pre [(map? vm)]
   :post [(not-negnum? %)]}
  (* (vm :mips) (current-vm-utilization vm)))

(defn current-vms-mips [vms]
  {:pre [(coll? vms)]
   :post [(not-negnum? %)]}
  (reduce + (map current-vm-mips vms)))

(defn host-utilization-history [host vms]
  {:pre [(map? host)
         (coll? vms)]
   :post [(coll? %)]}
  (apply map (fn [& xs] (/ (reduce + xs) (host :mips)))
    (map (fn [vm] 
           (map #(* % (vm :mips)) 
                (vm :utilization))) 
         vms)))

(defn overloaded? [host vms]
  {:pre [(map? host)
         (coll? vms)]
   :post [(boolean? %)]}
  (<= (host :mips) (current-vms-mips vms)))

(defn get-step-vms [step vms]
  {:pre [(not-negnum? step)
         (coll? vms)]
   :post [(coll? %)]}
  (map (fn [{:keys [mips utilization]}]
         {:mips mips
          :utilization (take step utilization)})
       vms))

(defn get-max-vm-steps [vms]
  {:pre [(coll? vms)]
   :post [(not-negnum? %)]}
  (apply min (map #(count (:utilization %)) vms)))

(defn run-step [algorithm time-step migration-time host vms]
  {:pre [(fn? algorithm)
         (not-negnum? time-step)
         (not-negnum? migration-time)
         (map? host)
         (coll? vms)]
   :post [(boolean? %)]}
  (not (algorithm time-step migration-time host vms)))

(defn run-simulation
  
  ([algorithm otf time-step migration-time host vms]
    {:pre [(fn? algorithm)
           (not-negnum? otf)
           (not-negnum? time-step)
           (not-negnum? migration-time)
           (map? host)
           (coll? vms)]
     :post [(map? %)]}
    (run-simulation algorithm otf time-step migration-time host vms (fn [step step-vms overloading-steps])))
  
  ([algorithm otf time-step migration-time host vms reporting]
    {:pre [(fn? algorithm)
           (not-negnum? otf)
           (not-negnum? time-step)
           (not-negnum? migration-time)
           (map? host)
           (coll? vms)
           (fn? reporting)]
     :post [(map? %)]}
    (let [start-time (. System nanoTime)
          max-steps (get-max-vm-steps vms)] 
      (loop [step 1
             overloading-steps 0]  
        (let [step-vms (get-step-vms step vms)
              new-overloading-steps (if (overloaded? host step-vms)
                                      (inc overloading-steps)
                                      overloading-steps)]
          (do
            (reporting step step-vms new-overloading-steps)             
            (if (and (run-step algorithm time-step migration-time host step-vms)
                     (> max-steps step))
              (recur (inc step)
                     new-overloading-steps)
              (let [result-time (+ (* step time-step) migration-time)
                    result-otf (double (/ 
                                         (+ (* new-overloading-steps time-step) 
                                            migration-time) 
                                         result-time))
                    result {:time result-time
                            ;:overloading-time (+ (* new-overloading-steps time-step) migration-time)
                            :otf result-otf
                            :violation (if (> result-otf otf) 1 0)
                            :execution-time (/ (double (- (. System nanoTime) start-time)) 1000000.0)}]
                result))))))))
