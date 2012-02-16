(ns simulation.algorithms.markov.bruteforce
  (:use clj-predicates.core
        clojure.pprint)
  (:require [simulation.algorithms.markov.nlp :as nlp]
            [simulation.algorithms.markov.l-2-states :as l2]
            [simulation.algorithms.markov.l-3-states :as l3]))

(defn solve-2 [objective constraint step]
  {:pre [(fn? objective) 
         (coll? constraint) 
         (posnum? step)]
   :post [(vector? %)]}
  (loop [state1 {:objective 0
                 :solution []}
         x 0.0]
    (if (> x 1)
      (:solution state1)
      (recur 
        (loop [state2 state1
               y 0.0]
          (if (> y 1)
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

(defn solve-3 [objective constraint step]
  {:pre [(fn? objective) 
         (coll? constraint) 
         (posnum? step)]
   :post [(vector? %)]}
  (loop [state1 {:objective 0
                :solution []}
         x 0.0]
    (if (> x 1)
      (:solution state1)
      (recur 
        (loop [state2 state1
               y 0.0]
          (if (> y 1)
            state2
            (recur
              (loop [state3 state2
                     z 0.0]
                (if (> z 1)
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

(defn optimize [step otf migration-time ls p state-vector time-in-states time-in-state-n]
  {:pre [(posnum? step)
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
      (solve-2 objective constraint step)
      (solve-3 objective constraint step))))


(def p2 [[0.2 0.8]
         [0.4 0.6]])
(def p3 [[0.4 0.2 0.2]
         [0.2 0.5 0.3]
         [0.2 0.6 0.2]])

(def state-vector2 [1 0])
(def state-vector3 [1 0 0])

(defn -main [& args]
  (do
    (pprint p2) 
    (time (prn (optimize 0.001 0.3 (/ 20.0 300.0) l2/ls p2 state-vector2 0 0))))
  ;(time (prn (optimize 0.05 0.9 (/ 20.0 300.0) l3/ls p3 state-vector3 0 0)))
  )




