(ns simulation.core-test
  (:use simulation.core
        midje.sweet))

(unfinished algorithm)

(def host {:mips 3000})
(def time-step 300) ;in seconds
(def migration-time 20)
(def vm1 {:mips 2000
          :utilization [0.5 0.6 0.4]})
(def vm2 {:mips 2000
          :utilization [0.2 0.3 0.6]})
(def vms [vm1 vm2])


(fact 
  "each simulation is run for an algorithm, a host, 
a set of VMs and migration time"
  (run-simulation algorithm 0 time-step migration-time host vms) => 
  (just {:time (+ migration-time (* time-step 3))
         :otf (double (/ (+ migration-time (* time-step 1))
                         (+ migration-time (* time-step 3))))
         :violation number?
         :execution-time number?})
  (provided
    (run-step algorithm time-step migration-time host 
              (get-step-vms 1 vms)) => true
    (run-step algorithm time-step migration-time host 
              (get-step-vms 2 vms)) => true
    (run-step algorithm time-step migration-time host 
              (get-step-vms 3 vms)) => false
    (overloaded? host (get-step-vms 1 vms)) => false
    (overloaded? host (get-step-vms 2 vms)) => false
    (overloaded? host (get-step-vms 3 vms)) => true))

(fact 
  "get-step-vms should return the VM list with utilization lists for the step"
  (get-step-vms 0 vms) => (just {:mips 2000
                            :utilization []}
                           {:mips 2000
                            :utilization []})
  (get-step-vms 1 vms) => (just {:mips 2000
                            :utilization [0.5]}
                           {:mips 2000
                            :utilization [0.2]})
  (get-step-vms 2 vms) => (just {:mips 2000
                            :utilization [0.5 0.6]}
                           {:mips 2000
                            :utilization [0.2 0.3]})
  (get-step-vms 3 vms) => (just {:mips 2000
                            :utilization [0.5 0.6 0.4]}
                           {:mips 2000
                            :utilization [0.2 0.3 0.6]}))

(fact
  "return the number of simulation steps calculated according to the shortest 
   utilization data of a VM"
  (get-max-vm-steps vms) => 3
  (get-max-vm-steps [{:utilization [1 2]}
                     {:utilization [1 2 3]}]) => 2)

(fact 
  "process a single step of a simulation, return true to continue"
  (run-step algorithm time-step migration-time host 
            [{:mips 2000
              :utilization []}
             {:mips 2000
              :utilization []}]) => true
  (provided 
    (algorithm time-step migration-time host 
               [{:mips 2000
                 :utilization []}
                {:mips 2000
                 :utilization []}]) => false)
  (run-step algorithm time-step migration-time host 
            [{:mips 2000
              :utilization [0.5]}
             {:mips 2000
              :utilization [0.2]}]) => true
  (provided
    (algorithm time-step migration-time host 
               [{:mips 2000
                 :utilization [0.5]}
                {:mips 2000
                 :utilization [0.2]}]) => false)
  (run-step algorithm time-step migration-time host 
            [{:mips 2000
              :utilization [0.5 0.6]}
             {:mips 2000
              :utilization [0.2 0.3]}]) => true
  (provided     
    (algorithm time-step migration-time host 
               [{:mips 2000
                 :utilization [0.5 0.6]}
                {:mips 2000
                 :utilization [0.2 0.3]}]) => false)
  (run-step algorithm time-step migration-time host 
            [{:mips 2000
              :utilization [0.5 0.6 0.4]}
             {:mips 2000
              :utilization [0.2 0.3 0.6]}]) => false    
   (provided  
     (algorithm time-step migration-time host 
                [{:mips 2000
                  :utilization [0.5 0.6 0.4]}
                 {:mips 2000
                  :utilization [0.2 0.3 0.6]}]) => true))

(fact 
  "A VM's current utilization is the last value in the list"
  (current-vm-utilization {:utilization []}) => 0.0
  (current-vm-utilization {:utilization [0.5]}) => 0.5
  (current-vm-utilization {:utilization [0.5 0.6]}) => 0.6
  (current-vm-utilization {:utilization [0.5 0.6 0.4]}) => 0.4)

(fact 
  "A VM's current MIPS is the current utilization * MIPS"
  (current-vm-mips {:mips 2000 :utilization []}) => 0.0
  (current-vm-mips {:mips 2000 :utilization [0.5]}) => (* 2000 0.5)
  (current-vm-mips {:mips 2000 :utilization [0.5 0.6]}) => (* 2000 0.6)
  (current-vm-mips {:mips 2000 :utilization [0.5 0.6 0.4]}) => (* 2000 0.4))

(fact 
  "Total current MIPS is the sum of the VMs' current MIPS"
  (current-vms-mips [{:mips 2000
                      :utilization []}
                     {:mips 2000
                      :utilization []}]) => 0.0
  (current-vms-mips [{:mips 2000
                      :utilization [0.0]}
                     {:mips 2000
                      :utilization [0.0]}]) => 0.0
  (current-vms-mips [{:mips 2000
                      :utilization [0.0 0.5]}
                     {:mips 2000
                      :utilization [0.0 0.5]}]) => 2000.0
  (current-vms-mips [{:mips 2000
                      :utilization [0.0 0.5 1.0]}
                     {:mips 2000
                      :utilization [0.0 0.5 0.2]}]) => 2400.0
  (current-vms-mips [{:mips 2000
                      :utilization [0.0 0.5 0.8]}
                     {:mips 2000
                      :utilization [0.0 0.5 0.3]}
                     {:mips 3000
                      :utilization [0.0 0.5 0.6]}]) => (+ 1600.0 600.0 1800.0))

(fact 
  "Host utilization history is built by summing utilization of VMs"
  (host-utilization-history host 
                            [{:mips 2000
                              :utilization []}
                             {:mips 2000
                              :utilization []}]) => []
  (host-utilization-history host 
                            [{:mips 2000
                              :utilization [0.0]}
                             {:mips 2000
                              :utilization [0.0]}]) => [0.0]
  (host-utilization-history host 
                            [{:mips 2000
                              :utilization [0.0 0.5]}
                             {:mips 2000
                              :utilization [0.0 0.5]}]) => (just 0.0 (roughly (/ 2 3)))
  (host-utilization-history host 
                            [{:mips 2000
                              :utilization [0.0 0.5 1.0]}
                             {:mips 2000
                              :utilization [0.0 0.5 0.2]}]) => (just 0.0 (roughly (/ 2 3)) 0.8)
  (host-utilization-history host 
                            [{:mips 2000
                              :utilization [0.0 0.5 0.8]}
                             {:mips 2000
                              :utilization [0.0 0.5 0.3]}
                             {:mips 3000
                              :utilization [0.0 0.5 0.6]}]) => (just 0.0 (roughly (/ 35 30)) (roughly (/ 4 3))))

(fact 
  "A host is overloaded if the total CPU demand of the VMs >= 100%"
  (overloaded? host 
               [{:mips 2000
                 :utilization []}
                {:mips 2000
                 :utilization []}]) => false
  (overloaded? host 
               [{:mips 2000
                 :utilization [0.0]}
                {:mips 2000
                 :utilization [0.0]}]) => false
  (overloaded? host 
               [{:mips 2000
                 :utilization [0.0 0.5]}
                {:mips 2000
                 :utilization [0.0 0.5]}]) => false
  (overloaded? host 
               [{:mips 2000
                 :utilization [0.0 0.5 1.0]}
                {:mips 2000
                 :utilization [0.0 0.5 0.2]}]) => false
  (overloaded? host 
               [{:mips 2000
                 :utilization [0.0 0.5 1.0 1.0]}
                {:mips 2000
                 :utilization [0.0 0.5 0.2 0.5]}]) => true
  (overloaded? host 
               [{:mips 2000
                 :utilization [0.0 0.5 1.0 1.0 0.75]}
                {:mips 2000
                 :utilization [0.0 0.5 0.2 0.5 0.75]}]) => true
  (overloaded? host 
               [{:mips 2000
                 :utilization [0.0 0.5 1.0 1.0 0.75 0.8]}
                {:mips 2000
                 :utilization [0.0 0.5 0.2 0.5 0.75 0.8]}]) => true
  (overloaded? host 
               [{:mips 2000
                 :utilization [0.0 0.5 1.0 1.0 0.75 0.8 1.0]}
                {:mips 2000
                 :utilization [0.0 0.5 0.2 0.5 0.75 0.8 1.0]}]) => true)

