(ns simulation.algorithms.markov.multisize-estimation
  (:use clj-predicates.core
        clojure.pprint)
  (:require [simulation.workload-generator :as workload-generator]))

(defn mean [data window-size]
  {:pre [(coll? data)
         (posnum? window-size)]
   :post [(number? %)]}
  (double (/ (apply + data)
             window-size)))

(defn variance [data window-size]
  {:pre [(coll? data)
         (posnum? window-size)]
   :post [(not-negnum? %)]}
  (let [m (mean data window-size)]
    (double 
      (/ (reduce (fn [sum number]
                   (+ sum (Math/pow (- number m) 2)))
                 0 data)
         (dec window-size)))))

(defn acceptable-variance [probability window-size]
  {:pre [(not-negnum? probability)
         (posnum? window-size)]
   :post [(not-negnum? %)]}
  (double (/ (* probability
                (- 1 probability))
             window-size)))

(defn estimate-probability [data window-size state]
  {:pre [(coll? data)
         (posnum? window-size)
         (not-negnum? state)]
   :post [(not-negnum? %)]}
  (double 
    (/ (count (filter #{state} data))
       window-size)))

(defn update-request-windows [request-windows max-window-size previous-state current-state]
  {:pre [(vector? request-windows)
         (posnum? max-window-size)
         (not-negnum? previous-state)
         (not-negnum? current-state)]
   :post [(coll? %)]}
  (let [window (get request-windows previous-state)] 
    (assoc request-windows previous-state
           (conj (take (dec max-window-size) window) current-state))))

(defn update-estimate-windows [estimate-windows request-windows previous-state]
  {:pre [(vector? estimate-windows)
         (vector? request-windows)
         (not-negnum? previous-state)]
   :post [(coll? %)]}
  (let [request-window (get request-windows previous-state)
        estimate-window (get estimate-windows previous-state)] 
    (assoc estimate-windows previous-state
           (vec (map (fn [estimate-map state]
                       (zipmap (keys estimate-map)
                               (map (fn [[window-size estimates]]
                                      (conj (take (dec window-size) estimates) 
                                            (estimate-probability 
                                              (take window-size request-window) 
                                              window-size state)))
                                    estimate-map))) 
                     estimate-window (range))))))

(defn update-history [history windows]
  {:pre [(vector? history)
         (vector? windows)]
   :post [(vector? %)]}
  (vec (map (fn [history-states windows-states]
              (vec (map (fn [history-map windows-map]
                          (zipmap (keys history-map)
                                  (map (fn [[window-size history-data]]
                                         (let [windows-data (get windows-map window-size)]
                                           (conj history-data 
                                                 (if (coll? windows-data)
                                                   (if (not-empty windows-data)
                                                     (first windows-data)
                                                     0.0)
                                                   windows-data))))
                                       history-map))) 
                        history-states windows-states))) 
            history windows)))

(defn update-variances [variances estimate-windows previous-state]
  {:pre [(vector? variances)
         (vector? estimate-windows)
         (not-negnum? previous-state)]
   :post [(vector? %)]}
  (let [estimate-window (get estimate-windows previous-state)
        variance-states (get variances previous-state)] 
    (assoc variances previous-state
           (vec (map (fn [variance-map estimate-map]
                       (zipmap (keys variance-map)
                               (map (fn [[window-size variance-data]]
                                      (let [estimates (get estimate-map window-size)] 
                                        (if (< (count estimates) window-size) 
                                          1.0
                                          (variance estimates window-size))))
                                    variance-map)))
                     variance-states estimate-window)))))

(defn update-acceptable-variances [acceptable-variances estimate-windows previous-state]
  {:pre [(vector? acceptable-variances)
         (vector? estimate-windows)
         (not-negnum? previous-state)]
   :post [(vector? %)]}
  (let [estimate-window (get estimate-windows previous-state)
        acceptable-variance-states (get acceptable-variances previous-state)] 
    (assoc acceptable-variances previous-state
           (vec (map (fn [acceptable-variance-map estimate-map]
                       (zipmap (keys acceptable-variance-map)
                               (map (fn [[window-size acceptable-variance-data]]
                                      (let [estimates (get estimate-map window-size)] 
                                        (acceptable-variance (first estimates) window-size)))
                                    acceptable-variance-map)))
                     acceptable-variance-states estimate-window)))))

(defn compute-error-of-estimates [workloads estimate-history]
  {:pre [(every-contains-keys? workloads :until :transitions)
         (vector? estimate-history)]
   :post [(vector %)]}
  (vec 
    (map 
      (fn [estimate-states state1]
        (vec 
          (map 
            (fn [estimate-map state2]
              (zipmap 
                (keys estimate-map)
                (map (fn [[window-size estimates-reversed]]
                       (let [estimates (reverse estimates-reversed)] 
                         (double 
                           (/ (apply + (map 
                                         (fn [estimate t]
                                           (Math/pow (- estimate
                                                        (get-in (workload-generator/get-workload 
                                                                  workloads t)
                                                                [:transitions state1 state2])) 
                                                     2))
                                         estimates (range)))
                              (count estimates)))))
                     estimate-map)))
            estimate-states (range))))
      estimate-history (range))))

(defn compute-error-of-best-estimates [workloads best-estimate-history]
  {:pre [(every-contains-keys? workloads :until :transitions)
         (vector? best-estimate-history)]
   :post [(vector %)]}
  (vec (map (fn [best-estimate-states state1]
              (vec (map (fn [estimates-reversed state2]
                          (let [estimates (reverse estimates-reversed)] 
                            (double 
                              (/ (apply + (map 
                                            (fn [estimate t]
                                              (Math/pow (- estimate
                                                           (get-in (workload-generator/get-workload 
                                                                     workloads t)
                                                                   [:transitions state1 state2])) 
                                                        2))
                                            estimates (range)))
                                 (count estimates)))))
                        best-estimate-states (range))))
            best-estimate-history (range))))

(defn select-window [variances acceptable-variances window-sizes]
  {:pre [(vector? variances)
         (vector? acceptable-variances)
         (coll? window-sizes)]
   :post [(vector? %)]}
  (vec (map (fn [variance-states acceptable-variance-states]
              (vec (map (fn [variance-map acceptable-variance-map]
                          (loop [sizes window-sizes
                                 previous-window-size (first sizes)]
                            (let [window-size (first sizes)] 
                              (if (or (empty? sizes) 
                                      (> (get variance-map window-size)
                                         (get acceptable-variance-map window-size)))
                                previous-window-size
                                (recur (rest sizes)
                                       window-size)))))
                        variance-states acceptable-variance-states)))
            variances acceptable-variances)))

(defn update-selected-window-history [selected-window-history selected-windows]
  {:pre [(vector? selected-window-history)
         (vector? selected-windows)]
   :post [(vector? %)]}
  (vec (map (fn [selected-window-history-states selected-windows-states]
              (vec (map (fn [selected-window-history-data selected-window]
                          (conj selected-window-history-data selected-window))
                        selected-window-history-states selected-windows-states)))
            selected-window-history selected-windows)))

(defn select-best-estimates [estimate-windows selected-windows]
  {:pre [(vector? estimate-windows)
         (vector? selected-windows)]
   :post [(vector? %)]}
  (vec (map (fn [estimate-states selected-window-states] 
              (vec (map #(if-let [item (first (get %1 %2))]
                           item
                           0.0) 
                        estimate-states selected-window-states)))
            estimate-windows selected-windows)))

(defn update-best-estimate-history [best-estimate-history best-estimates]
  {:pre [(vector? best-estimate-history)
         (vector? best-estimates)]
   :post [(vector? %)]}
  (vec (map (fn [history-states estimate-states] 
              (vec (map #(conj %1 %2) 
                        history-states estimate-states)))
            best-estimate-history best-estimates)))

(defn init-request-windows [number-of-states]
  {:pre [(posnum? number-of-states)]
   :post [(vector? %)]}
  (vec (repeat number-of-states (list))))

(defn init-3-level-data [window-sizes number-of-states]
  {:pre [(coll? window-sizes)
         (posnum? number-of-states)]
   :post [(vector? %)]}
  (vec (repeat number-of-states 
               (vec (repeat number-of-states 
                            (zipmap window-sizes
                                    (repeat (list))))))))

(defn init-variances [window-sizes number-of-states]
  {:pre [(coll? window-sizes)
         (posnum? number-of-states)]
   :post [(vector? %)]}
  (vec (repeat number-of-states 
               (vec (repeat number-of-states 
                            (zipmap window-sizes
                                    (repeat 1.0)))))))

(defn init-2-level-history [number-of-states]
  {:pre [(posnum? number-of-states)]
   :post [(vector? %)]}
  (vec (repeat number-of-states 
               (vec (repeat number-of-states (list))))))

(defn init-selected-window-sizes [window-sizes number-of-states]
  {:pre [(coll? window-sizes)
         (posnum? number-of-states)]
   :post [(vector? %)]}
  (vec (repeat number-of-states 
               (vec (repeat number-of-states (first window-sizes))))))


