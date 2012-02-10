(ns simulation.workload-generator-test
  (:use simulation.workload-generator
        clj-predicates.core
        midje.sweet))

(def time-limit 30)

(def test-workloads [{:until 15
                      :transitions [[0.2 0.8]
                                    [1.0 0.0]]}
                     {:until 30
                      :transitions [[0.5 0.5]
                                    [1.0 0.0]]}])

(def test-workloads-3-states [{:until 15
                               :transitions [[0.2 0.5 0.3]
                                             [0.5 0.0 0.5]
                                             [1.0 0.0 0.0]]}
                              {:until 30
                               :transitions [[0.5 0.5 0.0]
                                             [0.8 0.1 0.1]
                                             [1.0 0.0 0.0]]}])

(fact
  (:until (get-workload test-workloads 0))  => 15
  (:until (get-workload test-workloads 1))  => 15
  (:until (get-workload test-workloads 14)) => 15
  (:until (get-workload test-workloads 15)) => 15
  (:until (get-workload test-workloads 16)) => 30
  (:until (get-workload test-workloads 30)) => 30
  (:until (get-workload test-workloads 31)) => (throws AssertionError))

(fact
  (generate-state test-workloads 1 0) => 0
  (provided (rand) => 0.0)
  (generate-state test-workloads 1 1) => 0
  (provided (rand) => 0.0)
            
  (generate-state test-workloads 1 0) => 0
  (provided (rand) => 0.1)
  (generate-state test-workloads 1 1) => 0
  (provided (rand) => 0.1)
  
  (generate-state test-workloads 1 0) => 0
  (provided (rand) => 0.2)
  (generate-state test-workloads 1 1) => 0
  (provided (rand) => 0.2)
  
  (generate-state test-workloads 1 0) => 1
  (provided (rand) => 0.3)
  (generate-state test-workloads 1 1) => 0
  (provided (rand) => 0.3)
  
  (generate-state test-workloads 1 0) => 1
  (provided (rand) => 0.7)
  (generate-state test-workloads 1 1) => 0
  (provided (rand) => 0.7)
  
  (generate-state test-workloads 1 0) => 1
  (provided (rand) => 0.9)
  (generate-state test-workloads 1 1) => 0
  (provided (rand) => 0.9)
  
  (generate-state test-workloads 1 0) => 1
  (provided (rand) => 1.0)
  (generate-state test-workloads 1 1) => 0
  (provided (rand) => 1.0)
  
  (generate-state test-workloads 16 0) => 0
  (provided (rand) => 0.0)
  (generate-state test-workloads 16 1) => 0
  (provided (rand) => 0.0)
  
  (generate-state test-workloads 16 0) => 0
  (provided (rand) => 0.1)
  (generate-state test-workloads 16 1) => 0
  (provided (rand) => 0.1)
  
  (generate-state test-workloads 16 0) => 0
  (provided (rand) => 0.2)
  (generate-state test-workloads 16 1) => 0
  (provided (rand) => 0.2)
  
  (generate-state test-workloads 16 0) => 0
  (provided (rand) => 0.3)
  (generate-state test-workloads 16 1) => 0
  (provided (rand) => 0.3)
  
  (generate-state test-workloads 16 0) => 1
  (provided (rand) => 0.7)
  (generate-state test-workloads 16 1) => 0
  (provided (rand) => 0.7)
  
  (generate-state test-workloads 16 0) => 1
  (provided (rand) => 1.0)
  (generate-state test-workloads 16 1) => 0
  (provided (rand) => 1.0)


  (generate-state test-workloads-3-states 1 0) => 0
  (provided (rand) => 0.0)
  (generate-state test-workloads-3-states 1 1) => 0
  (provided (rand) => 0.0)
  (generate-state test-workloads-3-states 1 2) => 0
  (provided (rand) => 0.0)
            
  (generate-state test-workloads-3-states 1 0) => 0
  (provided (rand) => 0.1)
  (generate-state test-workloads-3-states 1 1) => 0
  (provided (rand) => 0.1)
  (generate-state test-workloads-3-states 1 2) => 0
  (provided (rand) => 0.1)
  
  (generate-state test-workloads-3-states 1 0) => 0
  (provided (rand) => 0.2)
  (generate-state test-workloads-3-states 1 1) => 0
  (provided (rand) => 0.2)
  (generate-state test-workloads-3-states 1 2) => 0
  (provided (rand) => 0.2)
  
  (generate-state test-workloads-3-states 1 0) => 1
  (provided (rand) => 0.3)
  (generate-state test-workloads-3-states 1 1) => 0
  (provided (rand) => 0.3)
  (generate-state test-workloads-3-states 1 2) => 0
  (provided (rand) => 0.3)
  
  (generate-state test-workloads-3-states 1 0) => 1
  (provided (rand) => 0.7)
  (generate-state test-workloads-3-states 1 1) => 2
  (provided (rand) => 0.7)
  (generate-state test-workloads-3-states 1 2) => 0
  (provided (rand) => 0.7)
  
  (generate-state test-workloads-3-states 1 0) => 2
  (provided (rand) => 0.9)
  (generate-state test-workloads-3-states 1 1) => 2
  (provided (rand) => 0.9)
  (generate-state test-workloads-3-states 1 2) => 0
  (provided (rand) => 0.9)
  
  (generate-state test-workloads-3-states 1 0) => 2
  (provided (rand) => 1.0)
  (generate-state test-workloads-3-states 1 1) => 2
  (provided (rand) => 1.0)
  (generate-state test-workloads-3-states 1 2) => 0
  (provided (rand) => 1.0)
  
  (generate-state test-workloads-3-states 16 0) => 0
  (provided (rand) => 0.0)
  (generate-state test-workloads-3-states 16 1) => 0
  (provided (rand) => 0.0)
  (generate-state test-workloads-3-states 16 2) => 0
  (provided (rand) => 0.0)
  
  (generate-state test-workloads-3-states 16 0) => 0
  (provided (rand) => 0.1)
  (generate-state test-workloads-3-states 16 1) => 0
  (provided (rand) => 0.1)
  (generate-state test-workloads-3-states 16 2) => 0
  (provided (rand) => 0.1)
  
  (generate-state test-workloads-3-states 16 0) => 0
  (provided (rand) => 0.2)
  (generate-state test-workloads-3-states 16 1) => 0
  (provided (rand) => 0.2)
  (generate-state test-workloads-3-states 16 2) => 0
  (provided (rand) => 0.2)
  
  (generate-state test-workloads-3-states 16 0) => 0
  (provided (rand) => 0.3)
  (generate-state test-workloads-3-states 16 1) => 0
  (provided (rand) => 0.3)
  (generate-state test-workloads-3-states 16 2) => 0
  (provided (rand) => 0.3)
  
  (generate-state test-workloads-3-states 16 0) => 1
  (provided (rand) => 0.7)
  (generate-state test-workloads-3-states 16 1) => 0
  (provided (rand) => 0.7)
  (generate-state test-workloads-3-states 16 2) => 0
  (provided (rand) => 0.7)
  
  (generate-state test-workloads-3-states 16 0) => 1
  (provided (rand) => 1.0)
  (generate-state test-workloads-3-states 16 1) => 2
  (provided (rand) => 1.0)
  (generate-state test-workloads-3-states 16 2) => 0
  (provided (rand) => 1.0))

