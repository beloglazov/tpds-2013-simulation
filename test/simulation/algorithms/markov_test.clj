(ns simulation.algorithms.markov-test
  (:use simulation.core
        simulation.algorithms.markov
        midje.sweet
        [clojure.walk :only [postwalk]])
  (:require [simulation.math :as math]))

(def host {:mips 3000})
(def time-step 300) ;in seconds
(def vm1 {:mips 2000
          :utilization [0.5 0.6 0.4]})
(def vm2 {:mips 2000
          :utilization [0.2 0.3 0.6]})
(def vms [vm1 vm2])
(def workloads [{:until 15
                 :transitions [[0.2 0.8]
                               [1.0 0.0]]}
                {:until 30
                 :transitions [[0.5 0.5]
                               [1.0 0.0]]}])

(def state-config [0.4 0.7])

(def data3 [0.25 0.30 0.62 0.59 0.67 0.73 0.85 0.97 0.73 0.68 0.69 
            0.52 0.51 0.25 0.38 0.46 0.52 0.55 0.58 0.65 0.70])
(def states [0 0 0 1 1 1 2 2 2 2 1 1 1 1 0 0 1 1 1 1 1 2])
(def transition-counts-matrix [[3 2 0]
                               [1 9 2]
                               [0 1 3]])

(def p [[3/5  2/5   0]
        [1/12 10/12 1/12]
        [0    1/4   3/4]])

(def pq [[-2/5  2/5   0]
         [1/12 -2/12 1/12]
         [0    1/4   -1/4]])



;;for 2 states: [0.7]
; states : [0 0 0 0 0 1 1 1 1 0 0 0 0 0 0 0 0 0 0 0 1]
; counts:
; [14 2]
; [1 3]


;; for 4 states: [0.4 0.7 0.9]
; (def states [0 0 0 1 1 1 2 2 3 2 1 1 1 1 0 0 1 1 1 1 1 1])
;counts:
; [3 2 0 0]
; [1 10 1 0]
; [0 1 1 1]
; [0 0 1 0]
;p:
; [3/5 2/5 0 0]
; [1/12 10/12 1/12 0]
; [0 1/3 1/3 1/3]
; [0 0 1/2 1/2];
;q:
;[[-2/5 2/5 0 0]
; [1/12 -2/12 1/12 0]
; [0 1/3 -2/3 1/3]
; [0 0 1/2 -1/2]]


;(def pqp [[-1  1   0]
;          [5/24 -5/12 5/24]
;          [0    5/8   -5/8]])

;(def pqp2 [[0   1   0]
;           [0.21 0.58 0.21]
;           [0    0.63   0.37]])


;(def p [[2/5  2/5   1/5]
;        [1/12 10/12 1/12]
;        [0    1/4   3/4]])

;(def pq [[-3/5  2/5  1/5]
;        [1/12 -2/12 1/12]
;        [0    1/4   -1/4]])

;(def pqp [[-3/5  2/5  1/3]
;        [1/12 -2/12 5/36]
;        [0    1/4   -1/4]])

;1/5 1/12 = 0.2 0.083
;1/3 5/36 = 0.333 0.138

(def transition-rate-matrix [[0.15 0.1  0.0]
                             [0.05 0.45 0.1]
                             [0.0  0.05 0.15]])
(def q [[-0.1   0.1   0.0]
        [ 0.05 -0.1   0.05]
        [ 0.0   0.05 -0.05]])

(def m [0.05 0.1 0.5])

(def policy [0.0025 0.005 0.025])

(def qm [[-0.15  0.1   0.0   0.05]
         [ 0.05 -0.2   0.05  0.1]
         [ 0.0   0.05 -0.55  0.5]
         [ 0.0   0.0   0.0   0.0]])

(def qp [[0.7273 0.1818 0.0    0.0909]
         [0.0909 0.6364 0.0909 0.1818]
         [0.0    0.0909 0.0    0.9090]
         [0.0    0.0    0.0    0.0]])

(def qm2 [[-1/5 1/5 0] 
          [1/10 -1/10 0] 
          [0.0 0.0 0.0]])

(def qm3 [[-4.8 4.5 0 0.3 0 0]
          [2.4 -7.35 4.5 0.15 0.3 0]
          [0 2.4 -2.55 0 0.15 0]
          [0.2 0.2 0 -5.2 4.5 0.3]
          [0 0.2 0.2 2.4 -2.95 0.15]
          [0 0 0 0.2 0.2 -0.4]])



;   0-40: 1
;  40-70: 2
; 70-100: 3
; 0-0: 2  -> -0.1
; 0-1: 2  -> 0.1
; 0-2: 0  -> 0
; 1-0: 1  -> 0.05
; 1-1: 10 -> -0.1
; 1-2: 1  -> 0.05
; 2-0: 0  -> 0
; 2-1: 1  -> 0.05
; 2-2: 3  -> -0.05
; time: 20 * time-step

(fact
  "Transforming the utilization value into a state"
  (utilization-to-state state-config 0.0) => 0
  (utilization-to-state state-config 0.1) => 0
  (utilization-to-state state-config 0.2) => 0
  (utilization-to-state state-config 0.3) => 0
  (utilization-to-state state-config 0.4) => 1
  (utilization-to-state state-config 0.5) => 1
  (utilization-to-state state-config 0.6) => 1
  (utilization-to-state state-config 0.7) => 2
  (utilization-to-state state-config 0.8) => 2
  (utilization-to-state state-config 0.9) => 2
  (utilization-to-state state-config 1.0) => 2
  (utilization-to-state state-config 1.1) => 2
  
  (utilization-to-state [1.0] 0.0) => 0
  (utilization-to-state [1.0] 0.1) => 0
  (utilization-to-state [1.0] 0.2) => 0
  (utilization-to-state [1.0] 0.2) => 0
  (utilization-to-state [1.0] 0.3) => 0
  (utilization-to-state [1.0] 0.4) => 0
  (utilization-to-state [1.0] 0.5) => 0
  (utilization-to-state [1.0] 0.6) => 0
  (utilization-to-state [1.0] 0.7) => 0
  (utilization-to-state [1.0] 0.8) => 0
  (utilization-to-state [1.0] 0.9) => 0
  (utilization-to-state [1.0] 1.0) => 1
  (utilization-to-state [1.0] 1.1) => 1)

(fact
  "Transforming the utilization history into state history"
  (utilization-to-states state-config data3) => states
  (utilization-to-states [1.0] [0.5 0.5 1.0 1.0 0.5]) => [0 0 0 1 1 0])

(fact
  (transition-counts state-config states) => transition-counts-matrix)

(fact
  "Building a matrix of the number of transitions between states
divided by the number of time steps"
  (transition-rates state-config data3) => transition-rate-matrix)

(fact
  (build-state-vector state-config [0.0 0.1]) => [1 0 0]
  (build-state-vector state-config [0.0 0.2]) => [1 0 0]
  (build-state-vector state-config [0.0 0.3]) => [1 0 0]
  (build-state-vector state-config [0.0 0.4]) => [0 1 0]
  (build-state-vector state-config [0.0 0.5]) => [0 1 0]
  (build-state-vector state-config [0.0 0.6]) => [0 1 0]
  (build-state-vector state-config [0.0 0.7]) => [0 0 1]
  (build-state-vector state-config [0.0 0.8]) => [0 0 1]
  (build-state-vector state-config [0.0 0.9]) => [0 0 1]
  (build-state-vector state-config [0.0 1.0]) => [0 0 1])

(fact
  "Building the rate matrix out of the host's utilization history"
  (build-q state-config data3) => (just [-0.1 0.1 0.0]
                                        (just 0.05 (roughly -0.149 0.01) 0.1)
                                        (just 0.0 0.05 (roughly -0.05))))

;(fact
;  (build-p state-config data3) => p)

(fact
  (let [result (substitute-m-in-q q m)] 
    (get result 0) => (just (roughly -0.15) 0.1 0.0 0.05)
    (get result 1) => (just 0.05 (roughly -0.2) 0.05 0.1)
    (get result 2) => (just 0.0 0.05 (roughly -0.55) 0.5)
    (get result 3) => (just 0.0 0.0 0.0 0.0)))

(fact
  (max-abs-rate q)  => 0.1
  (max-abs-rate qm) => 0.55)

(fact
  (p-to-q p) => pq)

(def cc [[14 2] [1 3]])
(def cm [6.97 9.87])

(fact
  (let [result (c-to-p cc cm)]
    (get result 0) => (just (roughly 0.609 0.001) (roughly 0.087 0.001) (roughly 0.303 0.001))
    (get result 1) => (just (roughly 0.072 0.001) (roughly 0.216 0.001) (roughly 0.711 0.001))
    (get result 2) => (just 0 0 0)))

(fact
  (p-to-policy qm) => (just 0.05 0.1 0.5)
  (p-to-policy qp) => (just 0.0909 0.1818 0.9090))

(fact
  (solution-to-policy data3 m) => policy)

(fact
  (current-state [1 0 0]) => 0
  (current-state [0 1 0]) => 1
  (current-state [0 0 1]) => 2)

(def p [0.3 0.6 0.8])

(fact 
  (issue-command p [1 0 0]) => true
  (provided (rand) => 0.1)
  (issue-command p [1 0 0]) => false
  (provided (rand) => 0.3)
  (issue-command p [0 1 0]) => true
  (provided (rand) => 0.2)
  (issue-command p [0 1 0]) => false
  (provided (rand) => 0.8)
  (issue-command p [0 0 1]) => true
  (provided (rand) => 0.1)
  (issue-command p [0 0 1]) => false
  (provided (rand) => 1.0))

(fact
  (time-in-state-n state-config states) => 5)





