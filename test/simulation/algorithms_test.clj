(ns simulation.algorithms-test
  (:use simulation.core
        simulation.algorithms
        midje.sweet)
  (:require [simulation.math :as math]))

(unfinished f)

(def host {:mips 3000})
(def time-step 300) ;in seconds
(def migration-time 20)
(def vm1 {:mips 2000
          :utilization [0.5 0.6 0.4]})
(def vm2 {:mips 2000
          :utilization [0.2 0.3 0.6]})
(def vms (list vm1 vm2))

(fact
  (otf 0.5 0 0 host [{:mips 3000 
                      :utilization [0.9 0.8 1.1 1.2 1.3]}]) => true
  (otf 0.5 0 0 host [{:mips 3000 
                      :utilization [0.9 0.8 1.1 1.2 0.3]}]) => false)

(fact
  "THR algorithm accepts the threshold, host and vms"
  (thr 0.8 time-step migration-time host ; 0.8 * 3000 = 2400
       [{:mips 2000
         :utilization []}
        {:mips 2000
         :utilization []}]) => false
  (thr 0.8 time-step migration-time host  
       [{:mips 2000
         :utilization [0.0]}
        {:mips 2000
         :utilization [0.0]}]) => false
  (thr 0.8 time-step migration-time host  
       [{:mips 2000
         :utilization [0.4]}
        {:mips 2000
         :utilization [0.4]}]) => false
  (thr 0.8 time-step migration-time host  
       [{:mips 2000
         :utilization [0.6]}
        {:mips 2000
         :utilization [0.6]}]) => true
  (thr 0.8 time-step migration-time host  
       [{:mips 2000
         :utilization [1.0]}
        {:mips 2000
         :utilization [0.0]}]) => false
  (thr 0.8 time-step migration-time host  
       [{:mips 2000
         :utilization [1.0]}
        {:mips 2000
         :utilization [0.4]}]) => true
  (thr 0.8 time-step migration-time host  
       [{:mips 2000
         :utilization [1.0]}
        {:mips 2000
         :utilization [0.6]}]) => true
  (thr 0.8 time-step migration-time host  
       [{:mips 2000
         :utilization [0.2]}
        {:mips 2000
         :utilization [0.2]}
        {:mips 2000
         :utilization [0.2]}]) => false
  (thr 0.8 time-step migration-time host  
       [{:mips 2000
         :utilization [0.6]}
        {:mips 2000
         :utilization [0.6]}
        {:mips 2000
         :utilization [0.6]}]) => true)

(fact 
  "The algorithm applied f to the host utilization history and then thr"
  (against-background (f anything) => 0.8)
  
  (host-utilization-history-thr f host 
                                [{:mips 2000
                                  :utilization []}
                                 {:mips 2000
                                  :utilization []}]) => false
  (host-utilization-history-thr f host 
                                [{:mips 2000
                                  :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.0]}
                                 {:mips 2000
                                  :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.0]}]) => false
  (host-utilization-history-thr f host 
                                [{:mips 2000
                                  :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.4]}
                                 {:mips 2000
                                  :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.4]}]) => false
  (host-utilization-history-thr f host 
                                [{:mips 2000
                                  :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.6]}
                                 {:mips 2000
                                  :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.6]}]) => true
  (host-utilization-history-thr f host 
                                [{:mips 2000
                                  :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1.0]}
                                 {:mips 2000
                                  :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.0]}]) => false
  (host-utilization-history-thr f host 
                                [{:mips 2000
                                  :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1.0]}
                                 {:mips 2000
                                  :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.4]}]) => true
  (host-utilization-history-thr f host 
                                [{:mips 2000
                                  :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1.0]}
                                 {:mips 2000
                                  :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.6]}]) => true
  (host-utilization-history-thr f host 
                                [{:mips 2000
                                  :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.2]}
                                 {:mips 2000
                                  :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.2]}
                                 {:mips 2000
                                  :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.2]}]) => false
  (host-utilization-history-thr f host 
                                [{:mips 2000
                                  :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.6]}
                                 {:mips 2000
                                  :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.6]}
                                 {:mips 2000
                                  :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.6]}]) => true)

(fact 
  "MAD algorithm accepts a safety parameter, host and VMS"
  (against-background (math/mad anything) => 0.125)
  
  (mad 1 time-step migration-time host 
       [{:mips 2000
         :utilization []}
        {:mips 2000
         :utilization []}]) => false
  (mad 1 time-step migration-time host 
       [{:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.0]}
        {:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.0]}]) => false
  (mad 1.6 time-step migration-time host 
       [{:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.4]}
        {:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.4]}]) => false
  (mad 1.6 time-step migration-time host 
       [{:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.6]}
        {:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.6]}]) => true
  (mad 1.6 time-step migration-time host 
       [{:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1.0]}
        {:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.0]}]) => false
  (mad 1.6 time-step migration-time host 
       [{:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1.0]}
        {:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.4]}]) => true
  (mad 1.6 time-step migration-time host 
       [{:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1.0]}
        {:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.6]}]) => true
  (mad 1.6 time-step migration-time host 
       [{:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.2]}
        {:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.2]}
        {:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.2]}]) => false
  (mad 1.6 time-step migration-time host 
       [{:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.6]}
        {:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.6]}
        {:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.6]}]) => true)

(fact 
  "IQR algorithm accepts a safety parameter, host and VMS"
  (against-background (math/iqr anything) => 0.125)
  
  (iqr 1 time-step migration-time host 
       [{:mips 2000
         :utilization []}
        {:mips 2000
         :utilization []}]) => false
  (iqr 1 time-step migration-time host 
       [{:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.0]}
        {:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.0]}]) => false
  (iqr 1.6 time-step migration-time host 
       [{:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.4]}
        {:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.4]}]) => false
  (iqr 1.6 time-step migration-time host 
       [{:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.6]}
        {:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.6]}]) => true
  (iqr 1.6 time-step migration-time host 
       [{:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1.0]}
        {:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.0]}]) => false
  (iqr 1.6 time-step migration-time host 
       [{:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1.0]}
        {:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.4]}]) => true
  (iqr 1.6 time-step migration-time host 
       [{:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1.0]}
        {:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.6]}]) => true
  (iqr 1.6 time-step migration-time host 
       [{:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.2]}
        {:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.2]}
        {:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.2]}]) => false
  (iqr 1.6 time-step migration-time host 
       [{:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.6]}
        {:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.6]}
        {:mips 2000
         :utilization [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.6]}]) => true)

(def data1 [1.05 1.09 1.07 1.12 1.02 1.18 1.15 1.04 1.10 1.16 1.08])
(def data2 [0.55 0.60 0.62 0.59 0.67 0.73 0.85 0.97 0.73 0.68 0.69 
            0.52 0.51 0.55 0.48 0.46 0.52 0.55 0.58 0.65 0.70])

(fact 
  (against-background (host-utilization-history host vms) => data1)
  (loess 1.2 300 20 host vms) => true
  (loess-robust 1.2 300 20 host vms) => true)

(fact 
  (against-background (host-utilization-history host vms) => data2)
  (loess 1.2 300 20 host vms) => false
  (loess-robust 1.2 300 20 host vms) => false)











































