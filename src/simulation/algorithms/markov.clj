(ns simulation.algorithms.markov
  (:use simulation.core
        simulation.workload-generator
        clj-predicates.core
        clojure.math.numeric-tower
        clojure.pprint)
  (:require [simulation.algorithms.markov.l-counts.l-2-states :as l-counts-2]
            [simulation.algorithms.markov.l-counts.l-3-states :as l-counts-3]
            [simulation.algorithms.markov.l-probabilities.l-2-states :as l-probabilities-2]
            [simulation.algorithms.markov.l-probabilities.l-3-states :as l-probabilities-3]
            [simulation.algorithms.markov.bruteforce :as bruteforce]
            [simulation.algorithms.markov.genetic :as genetic]
            [simulation.algorithms.markov.multisize-estimation :as multisize-estimation]
            [simulation.math :as math])
  (:import [flanagan.analysis Regression Stat]))

(defn utilization-to-state
  "Returns the state corresponding to the utilization and state config"
  [state-config utilization]
  {:pre [(coll? state-config)
         (not-negnum? utilization)]
   :post [(not-negnum? %)]}
  (loop [x (first state-config)
         xs (rest state-config)
         state 0
         prev -1]
    (if (or (nil? x) 
            (and (>= utilization prev) (< utilization x)))
      state
      (recur (first xs) (rest xs) (inc state) x))))

(defn utilization-to-states
  "Returns the state history corresponding to the utilization history and state config.
   Adds the 0 state to the beginning to simulate the first transition."
  [state-config utilization]
  {:pre [(coll? state-config)
         (coll? utilization)]
   :post [(coll? %)]}
  (conj (map (partial utilization-to-state state-config) utilization) 0))

(defn transition-counts
  "Returns a matrix containing numbers of transitions between states"
  [state-config state-history]
  {:pre [(coll? state-config)
         (coll? state-history)]
   :post [(coll? %)]}  
  (let [state-count (inc (count state-config))
        transitions (partition 2 1 state-history)]
    (map (fn [i] (map 
                   #(count (filter #{[i %]} transitions)) 
                   (range state-count))) 
         (range state-count))))

(defn transition-rates
  "Returns a matrix of transition rates corresponding to the utilization history"
  [state-config utilization]
  {:pre [(coll? state-config)
         (coll? utilization)]
   :post [(coll? %)]}
  (let [steps (dec (count utilization))
        states (utilization-to-states state-config utilization)
        state-count (inc (count state-config))
        transitions (partition 2 1 states)]
    (map (fn [i] (map 
                   #(double (/ (count (filter #{[i %]} transitions)) steps)) 
                   (range state-count))) 
         (range state-count))))

(defn build-state-vector
  "Returns the current state PMF corresponding to the utilization history and state config"
  [state-config utilization]
  {:pre [(coll? state-config)
         (coll? utilization)]
   :post [(coll? %)]}
  (let [state (utilization-to-state state-config (last utilization))]
    (map #(if (= state %) 1 0) 
         (range (inc (count state-config))))))

(defn build-q
  "Returns an infinisemal transition rate matrix corresponding to the utilization history"
  [state-config utilization]
  {:pre [(coll? state-config)
         (coll? utilization)]
   :post [(coll? %)]}
  (let [transition-rates (transition-rates state-config utilization)
        state-count (inc (count state-config))]
    (map (fn [row i]
           (let [row-vec (vec row)]
             (assoc row-vec i (- (- (apply + row) (get row-vec i)))))) 
         transition-rates (range state-count))))

(defn build-p
  "Returns a matrix of transition probabilities built from the utilization history"
  [state-config utilization]
  {:pre [(coll? state-config)
         (coll? utilization)]
   :post [(coll? %)]}
  (let [transitions (transition-counts state-config utilization)
        row-sums (map (partial apply +) transitions)]
    (map (fn [row row-sum]
           (map #(/ % row-sum) row))
         transitions row-sums)))

(defn substitute-m-in-q
  "Returns a complete matrix of transition rates after the substition of the m vector"
  [q m]
  {:pre [(coll? q)
         (coll? m)]
   :post [(coll? %)]}
  (let [cnt (count m)] 
    (conj (vec 
            (for [i (range cnt)
                  :let [row (vec (get (vec q) i))
                        m-val (get (vec m) i)
                        old-x (get row i)
                        new-x(- old-x m-val)]]
              (conj (assoc row i new-x) m-val)))
          (vec (repeat (inc cnt) 0.0)))))

(defn max-abs-rate
  "Returns the maximum absolute transition rate from a specified matrix"
  [q]
  {:pre [(coll? q)]
   :post [(not-negnum? %)]}
  (apply max 
         (map #(apply max (map abs %)) q)))

(defn p-to-q
  "Transforms a matrix of transition probabilities into the corresponding matrix of transition rates"
  [p]
  {:pre [(coll? p)]
   :post [(coll? %)]}
  (map (fn [row i]
           (let [row-vec (vec row)]
             (assoc row-vec i (dec (get row-vec i))))) 
         p (range (count p))))

(defn q-to-p
  "Transforms a matrix of transition rates into the corresponding matrix of transition probabilities"
  [q]
  {:pre [(coll? q)]
   :post [(coll? %)]}
  (let [max-rate (inc (max-abs-rate q))] ;(inc (Math/ceil (max-abs-rate q))) 
    (conj (vec 
            (map (fn [i row]
                   (let [updated-row (vec (map #(/ % max-rate) row))] 
                     (assoc updated-row
                            i (inc (get updated-row i))))) 
                 (range (dec (count q)))
                 q))
          (last q))))

(defn c-to-p
  "Transforms a matrix of transition counts and an m column to the corresponding matrix of transition probabilities"
  [c m]
  {:pre [(coll? c)
         (coll? m)]
   :post [(coll? %)]}
  (conj (vec (map (fn [r mi] 
                    (let [row (conj (vec r) mi)
                          sum (apply + row)] 
                      (if (= 0 sum)
                        row
                        (map #(/ % sum) row)))) 
                  c m))
        (repeat (inc (count c)) 0)))

(defn p-to-policy
  "Extracts the migration probabilities from a matrix of transition probabilities"
  [p]
  {:pre [(coll? p)]
   :post [(coll? %)]}
  (map last (butlast p)))

(defn solution-to-policy
  "Transforms transition rates to probabilities according to the time for which the rates have been calculated"
  [utilization m]
  {:pre [(coll? utilization)
         (coll? m)]
   :post [(coll? %)]}
  (let [n (dec (count utilization))]
    (map #(/ % n) m)))

(defn current-state
  "Returns the current state index corresponding to the state probability vector"
  [state-vector]
  {:pre [(coll? state-vector)]
   :post [(not-negnum? %)]}
  (loop [i 0]
    (if (= 1 (nth state-vector i))
      i
      (recur (inc i)))))

(defn issue-command
  "Issues a migration command according to the policy PMF p and state probability vector"
  [p state-vector]
  {:pre [(coll? p)
         (coll? state-vector)]
   :post [(boolean? %)]}
;  (if (not-empty p)
;    (let [prob (get (vec p) (current-state state-vector))]
;;      (and
;;        (>= prob 0.9) ; should be greater than the step size
;;        (< (rand) prob))
;      (= prob 1.0))
;    true)
    (empty? p))

(defn time-in-state-n
  "Return the number of times the system stayed in the state n"
  [state-config state-history]
  {:pre [(coll? state-config)
         (coll? state-history)]
   :post [(not-negnum? %)]}
  (let [n (count state-config)]
    (count (filter #{n} state-history))))

(defn markov-optimal [workloads step otf state-config time-step migration-time host vms]
  {:pre [(coll? workloads)
         (posnum? step)
         (posnum? otf)
         (coll? state-config)
         (not-negnum? time-step)
         (not-negnum? migration-time)
         (map? host) 
         (coll? vms)]
   :post [(boolean? %)]}
  (let [utilization (host-utilization-history host vms)
        total-time (count utilization)]
    (if (>= total-time 30) 
      (let [state-vector (build-state-vector state-config utilization)
          state-history (utilization-to-states state-config utilization)
          time-in-state-n (time-in-state-n state-config state-history)
          p (:transitions (get-workload workloads total-time))
          ls (if (= 1 (count state-config))
               l-probabilities-2/ls
               l-probabilities-3/ls)]
      (if (every? #{0} (nth p (current-state state-vector)))
        false
        (let [policy (bruteforce/optimize step 1.0 otf (/ migration-time time-step) ls p state-vector total-time time-in-state-n)
              command (issue-command policy state-vector)]
          (do 
;            (println "--------")
;            (println "State vector: " state-vector)
;            (println "Time: " time-in-state-n " / " total-time)
;            (println "Probabilities: " p)
;            (println "Policy: " policy)
            command))))
      false)))

(def state-previous-state (atom 0))
(def state-request-windows (atom []))
(def state-estimate-windows (atom []))
(def state-variances (atom []))
(def state-acceptable-variances (atom []))

(defn reset-multisize-state [window-sizes number-of-states]
  {:pre [(coll? window-sizes)
         (posnum? number-of-states)]}
  (do
    (reset! state-previous-state 0)
    (reset! state-request-windows (multisize-estimation/init-request-windows number-of-states))
    (reset! state-estimate-windows (multisize-estimation/init-3-level-data window-sizes number-of-states))
    (reset! state-variances (multisize-estimation/init-variances window-sizes number-of-states))
    (reset! state-acceptable-variances (multisize-estimation/init-variances window-sizes number-of-states))))

(defn markov-multisize [step otf window-sizes state-config time-step migration-time host vms]
  {:pre [(posnum? step)
         (posnum? otf)
         (coll? window-sizes)
         (coll? state-config)
         (not-negnum? time-step)
         (not-negnum? migration-time)
         (map? host) 
         (coll? vms)]
   :post [(boolean? %)]}
  (let [utilization (host-utilization-history host vms)
        total-time (count utilization)
        min-window-size (apply min window-sizes)
        max-window-size (apply max window-sizes)
        state-vector (build-state-vector state-config utilization)
        state (current-state state-vector)]
    (do
      (swap! state-request-windows multisize-estimation/update-request-windows
             max-window-size @state-previous-state state)
      (swap! state-estimate-windows multisize-estimation/update-estimate-windows 
             @state-request-windows @state-previous-state)
      (swap! state-variances multisize-estimation/update-variances 
             @state-estimate-windows @state-previous-state)
      (swap! state-acceptable-variances multisize-estimation/update-acceptable-variances
             @state-estimate-windows @state-previous-state)
      (reset! state-previous-state state)
        
      (if (>= total-time min-window-size)
        (let [selected-windows (multisize-estimation/select-window 
                                 @state-variances @state-acceptable-variances window-sizes)
              p (multisize-estimation/select-best-estimates @state-estimate-windows selected-windows)
              state-history (utilization-to-states state-config utilization)
              time-in-state-n (time-in-state-n state-config state-history)
              ls (if (= 1 (count state-config))
                   l-probabilities-2/ls
                   l-probabilities-3/ls)]
          (if (every? #{0} (nth p state))
            false
            (let [policy (bruteforce/optimize step 1.0 otf (/ migration-time time-step) ls p state-vector total-time time-in-state-n)
                  command (issue-command policy state-vector)]
              (do
;                (println "--------")
;                (println @state-request-windows)
;                (println selected-windows)
;                (println "State vector: " state-vector)
;                (println "Time: " time-in-state-n " / " total-time)
;                (println "Best estimates: " p)
;                (println "Policy: " policy)
                command))))
        false))))

; What happens if a window for a particular state is not completely filled up?
; - zero probabilities
; Check probability estimation
; Check optimization
; Implement optimal time calculation by going backwards through the trace

(defn markov-single-window-bruteforce [otf state-config step time-step migration-time host vms]
  {:pre [(posnum? otf)
         (coll? state-config)
         (posnum? step)
         (not-negnum? time-step)
         (not-negnum? migration-time)
         (map? host) 
         (coll? vms)]
   :post [(boolean? %)]}
  (let [utilization (host-utilization-history host vms)
        time-in-states (count utilization)]
    (if (> time-in-states 29)
      (let [state-vector (build-state-vector state-config utilization)
            state-history (utilization-to-states state-config utilization)
            time-in-state-n (time-in-state-n state-config state-history)
            c (transition-counts state-config (take-last 30 state-history))
            ls (if (= 1 (count state-config))
                 l-counts-2/ls
                 l-counts-3/ls)]
        (if (every? #{0} (nth c (current-state state-vector)))
          false
          (let [solution (bruteforce/optimize step 100.0 otf (/ migration-time time-step) ls c state-vector time-in-states time-in-state-n)
                p (c-to-p c solution)
                policy (p-to-policy p)
                command (issue-command policy state-vector)]
            (do
;              (println "++++++++")
;              (pprint time-in-states)
;              (pprint c)
;              (pprint p)
;              (pprint policy)
;              (println "--------")
              command))))
      false)))


(defn markov-single-window-genetic [otf state-config max-generations time-step migration-time host vms]
  {:pre [(posnum? otf)
         (coll? state-config)
         (posnum? max-generations)
         (not-negnum? time-step)
         (not-negnum? migration-time)
         (map? host) 
         (coll? vms)]
   :post [(boolean? %)]}
  (let [utilization (host-utilization-history host vms)
        time-in-states (count utilization)]
    (if (> time-in-states 29)
      (let [state-vector (build-state-vector state-config utilization)
            state-history (utilization-to-states state-config utilization)
            time-in-state-n (time-in-state-n state-config state-history)
            c (transition-counts state-config (take-last 30 state-history))
            ls (if (= 1 (count state-config))
                 l-counts-2/ls
                 l-counts-3/ls)]
        (if (every? #{0} (nth c (current-state state-vector)))
          false
          (let [solution (genetic/optimize-counts otf max-generations (/ migration-time time-step) ls c state-vector time-in-states time-in-state-n)
                p (c-to-p c solution)
                policy (p-to-policy p)
                command (issue-command policy state-vector)]
            command)))
      false)))












