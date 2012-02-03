(ns simulation.algorithms
  (:use simulation.core 
        clj-predicates.core
        clojure.contrib.math)
  (:require [simulation.math :as math])
  (:import [flanagan.analysis Regression Stat]))

(defn otf [param time-step migration-time host vms]
  {:pre [(not-negnum? param)
         (not-negnum? time-step)
         (not-negnum? migration-time)
         (map? host)
         (coll? vms)]
   :post [(boolean? %)]}
  (let [utilization-history (host-utilization-history host vms)
        overloading-steps (count (filter #(>= % 1) utilization-history))]
    (> (/ overloading-steps (count utilization-history)) param)))

(defn no-migrations [time-step migration-time host vms]
  {:pre [(not-negnum? time-step)
         (not-negnum? migration-time)
         (map? host)
         (coll? vms)]
   :post [(boolean? %)]}
  false)

(defn thr [threshold time-step migration-time host vms]
  {:pre [(not-negnum? threshold)
         (not-negnum? time-step)
         (not-negnum? migration-time)
         (map? host)
         (coll? vms)]
   :post [(boolean? %)]}
  (<= (* threshold (host :mips))
      (current-vms-mips vms)))

(defn host-utilization-history-thr [f host vms]
  {:pre [(fn? f)
         (map? host)
         (coll? vms)]
   :post [(boolean? %)]}
  (let [utilization-history (host-utilization-history host vms)] 
    (if (> (count utilization-history) 29) ; 12 has been suggested as a safe value 
      (thr (f utilization-history) 1 1 host vms)
      false)))

(defn mad [param time-step migration-time host vms]
  {:pre [(not-negnum? param)
         (not-negnum? time-step)
         (not-negnum? migration-time)
         (map? host)
         (coll? vms)]
   :post [(boolean? %)]}
  (host-utilization-history-thr 
    #(- 1 (* param (math/mad %))) host vms))

(defn iqr [param time-step migration-time host vms]
  {:pre [(not-negnum? param)
         (not-negnum? time-step)
         (not-negnum? migration-time)
         (map? host)
         (coll? vms)]
   :post [(boolean? %)]}
  (host-utilization-history-thr 
    #(- 1 (* param (math/iqr %))) host vms))

 (defn loess-abstract [estimate-params param time-step migration-time host vms]
  {:pre [(fn? estimate-params)
         (not-negnum? param)
         (not-negnum? time-step)
         (not-negnum? migration-time)
         (map? host)
         (coll? vms)]
   :post [(boolean? %)]}
  (let [length 10 ; we use 10 to make the regression responsive enough to latest values
        utilization-history (host-utilization-history host vms)] 
    (if (> (count utilization-history) length) 
      (let [[estimate1 estimate2 & other] (estimate-params utilization-history)
            migration-intervals (ceil (/ migration-time time-step))
            prediction (+ estimate1 (* estimate2 (+ length migration-intervals)))]
        (>= (* param prediction) 1))
      false)))

(defn loess [param time-step migration-time host vms]
  {:pre [(not-negnum? param)
         (not-negnum? time-step)
         (not-negnum? migration-time)
         (map? host)
         (coll? vms)]
   :post [(boolean? %)]}
  (loess-abstract math/loess-parameter-estimates param time-step migration-time host vms))

(defn loess-robust [param time-step migration-time host vms]
  {:pre [(not-negnum? param)
         (not-negnum? time-step)
         (not-negnum? migration-time)
         (map? host)
         (coll? vms)]
   :post [(boolean? %)]}
  (loess-abstract math/loess-robust-parameter-estimates param time-step migration-time host vms))





























