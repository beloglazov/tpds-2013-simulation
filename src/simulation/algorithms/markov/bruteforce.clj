(ns simulation.algorithms.markov.bruteforce
  (:use clj-predicates.core
        clojure.pprint)
  (:require [simulation.algorithms.markov.nlp :as nlp]))

(defn solve-2 [objective constraint step limit]
  {:pre [(fn? objective) 
         (coll? constraint) 
         (posnum? step)
         (posnum? limit)]
   :post [(vector? %)]}
  (loop [state1 {:objective 0
                 :solution []}
         x 0.0]
    (if (> x limit)
      (:solution state1)
      (recur 
        (loop [state2 state1
               y 0.0]
          (if (> y limit)
            state2
            (recur 
              (try
                (let [objective-result (objective x y)] 
                  (if (and 
                        (> objective-result (:objective state2))
                        ((second constraint) 
                          ((first constraint) x y)
                          (last constraint)))
                    {:objective objective-result
                     :solution [x y]}
                    state2))
                (catch ArithmeticException e
                  state2))
              (+ y step))))
        (+ x step)))))

(defn solve-3 [objective constraint step limit]
  {:pre [(fn? objective) 
         (coll? constraint) 
         (posnum? step)
         (posnum? limit)]
   :post [(vector? %)]}
  (loop [state1 {:objective 0
                :solution []}
         x 0.0]
    (if (> x limit)
      (:solution state1)
      (recur 
        (loop [state2 state1
               y 0.0]
          (if (> y limit)
            state2
            (recur
              (loop [state3 state2
                     z 0.0]
                (if (> z limit)
                  state3
                  (recur
                    (try 
                      (let [objective-result (objective x y z)] 
                        (if (and 
                              (> objective-result (:objective state3))
                              ((second constraint) 
                                ((first constraint) x y z)
                                (last constraint)))
                          {:objective objective-result
                           :solution [x y z]}
                          state3))
                      (catch ArithmeticException e
                        state3))
                    (+ z step))))
              (+ y step))))
        (+ x step)))))

(defn optimize [step limit otf migration-time ls p state-vector time-in-states time-in-state-n]
  {:pre [(posnum? step)
         (posnum? limit)
         (not-negnum? otf)         
         (not-negnum? migration-time)
         (coll? ls)
         (coll? p)
         (coll? state-vector)
         (not-negnum? time-in-states)
         (not-negnum? time-in-state-n)]
   :post [(coll? %)]}
  (let [number-of-states (count state-vector)
        objective (nlp/build-fitness ls state-vector p)
        constraint (first (nlp/build-constraint otf migration-time ls state-vector p time-in-states time-in-state-n))] 
    (if (= number-of-states 2)
      (solve-2 objective constraint step limit)
      (solve-3 objective constraint step limit))))




