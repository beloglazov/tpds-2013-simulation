(ns simulation.workload-generator
  (:use clj-predicates.core))

(def mips 1000)

(defn get-workload [workloads current-time]
  {:pre [(coll? workloads)
         (not-negnum? current-time)]
   :post [(map? %)]}
  (first (drop-while 
           #(> current-time (:until %)) 
           workloads)))

(defn generate-state [workloads current-time current-state]
  {:pre [(coll? workloads)
         (not-negnum? current-time)
         (not-negnum? current-state)]
   :post [(not-negnum? %)]}
  (let [transitions (get (:transitions (get-workload workloads current-time))
                         current-state)
        random-seed (rand)]
    (loop [state 0
           state-transitions transitions
           accumulated-probability 0]
      (let [accumulated-probability (+ accumulated-probability
                                       (first state-transitions))]
        (if (or
              (= accumulated-probability 1)
              (<= random-seed accumulated-probability))
          state
          (recur (inc state)
                 (rest state-transitions)
                 accumulated-probability))))))

(defn get-host []
  {:mips mips})

(defn get-vms [workloads state-config time-limit]
  {:pre [(coll? workloads)
         (not-negnum? time-limit)]
   :post [(coll? %)]}
  [{:mips mips
    :utilization (map (partial get (conj state-config 1.0)) 
                      (loop [current-time 0
                             current-state 0
                             states []]
                        (if (>= current-time time-limit)
                          states
                          (recur (inc current-time)
                                 (generate-state workloads current-time current-state)
                                 (conj states current-state)))))}])
