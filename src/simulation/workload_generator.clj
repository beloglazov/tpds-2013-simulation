(ns simulation.workload-generator
  (:use clj-predicates.core))

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
