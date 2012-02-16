(ns simulation.algorithms.markov.bruteforce-test
  (:use simulation.algorithms.markov.bruteforce
        midje.sweet))

(fact
  (solve-2 (fn [x y] (+ x y)) 
           [(fn [x y] (+ x y)) < 10]
           0.1) => (just (roughly 1.0) 
                         (roughly 1.0))
  
  (solve-2 (fn [x y] (+ x y)) 
           [(fn [x y] (+ x y)) <= 0.5]
           0.1) => (just (roughly 0.0) 
                         (roughly 0.5))
  
  (solve-2 (fn [x y] (+ (* 2 x) y)) 
           [(fn [x y] (+ x y)) <= 0.5]
           0.1) => (just (roughly 0.5) 
                         (roughly 0.0))
  
  (solve-2 (fn [x y] (- x y)) 
           [(fn [x y] (- x y)) < 10]
           0.1) => (just (roughly 1.0) 
                         (roughly 0.0))
  
  (solve-2 (fn [x y] (/ x y)) 
           [(fn [x y] (/ x y)) < 10]
           0.1) => (just (roughly 1.0) 
                         (roughly 0.1)))

(fact
  (solve-3 (fn [x y z] (+ x y z)) 
           [(fn [x y z] (+ x y z)) < 10]
           0.01) => (just (roughly 1.0 0.01) 
                          (roughly 1.0 0.01)
                          (roughly 1.0 0.01))
  
  (solve-3 (fn [x y z] (+ x y z)) 
           [(fn [x y z] (+ x y z)) <= 0.05]
           0.01) => (just (roughly 0.0 0.01)
                          (roughly 0.0 0.01)
                          (roughly 0.05 0.01))
  
  (solve-3 (fn [x y z] (+ x (* 2 y) z)) 
           [(fn [x y z] (+ x y z)) <= 0.05]
           0.01) => (just (roughly 0.0 0.01)
                          (roughly 0.05 0.01) 
                          (roughly 0.0 0.01))
  
  (solve-3 (fn [x y z] (- x y)) 
           [(fn [x y z] (- x y)) < 10]
           0.01) => (just (roughly 1.0 0.01) 
                          (roughly 0.0 0.01)
                          (roughly 0.0 0.01))
  
  (solve-3 (fn [x y z] (+ x y z)) 
           [(fn [x y z] (/ x y)) < 10]
           0.01) => (just (roughly 1.0 0.01) 
                          (roughly 1.0 0.01)
                          (roughly 1.0 0.01)))
