(ns simulation.io-test
  (:use simulation.io
        clj-predicates.core
        clojure.java.io
        midje.sweet))

(fact 
  (read-file "test/resources/input_short") => (just 26 47 10 0 13)
  (count (read-file "test/resources/input_real")) => 288
  (read-file "test/resources/input_real") => (partial every? integer?))

(fact
  (let [result (read-random-file "test/resources/rand/")
        options [[1 2 3 4 5]
                 [2 3 4 5 6]
                 [3 4 5 6 7]
                 [4 5 6 7 8]
                 [5 6 7 8 9]]]
    (count result) => 5
    (contains-val? options result) => true))

(fact
  (let [result (read-n-random-files "test/resources/rand/" 3)
        options [[1 2 3 4 5]
                 [2 3 4 5 6]
                 [3 4 5 6 7]
                 [4 5 6 7 8]
                 [5 6 7 8 9]]]
    (count result) => 3
    (count (nth result 0)) => 5
    (count (nth result 1)) => 5
    (count (nth result 2)) => 5
    (contains-val? options (nth result 0)) => true
    (contains-val? options (nth result 1)) => true
    (contains-val? options (nth result 2)) => true))

(fact
  (load-vms [2000 2000] {:mips 3000} 1.0 "path") => (just {:mips 2000 :utilization [0.5 0.2]}
                                                          {:mips 2000 :utilization [0.5 0.2]}
                                                          {:mips 2000 :utilization [0.5 0.2]})
  (provided (read-random-file "path") => [50 20])
  (load-vms [2000 2000] {:mips 3000} 0.8 "path") => (just {:mips 2000 :utilization [0.5 0.2]}
                                                          {:mips 2000 :utilization [0.5 0.2]})
  (provided (read-random-file "path") => [50 20])  
  (load-vms [2000 2000] {:mips 3000} 1.0 "path") => (just {:mips 2000 :utilization [0.6 0.2]}
                                                          {:mips 2000 :utilization [0.6 0.2]})
  (provided (read-random-file "path") => [60 20]))

(fact
  (let [workload (pregenerate-workload [2000 2000] 
                                       {:mips 3000}
                                       0.8
                                       5
                                       "test/resources/rand/"
                                       "test/resources/pregenerated_workload")]
    (read-pregenerated-workload "test/resources/pregenerated_workload") => workload))

(delete-file "test/resources/pregenerated_workload")