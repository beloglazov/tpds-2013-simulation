(ns simulation.algorithms.markov.nlp-test
  (:use simulation.algorithms.markov.nlp
        midje.sweet))

(def otf 0.05)
(def migration-time 20)

(defn l0 [q p m] 2)
(defn l1 [q p m] 3)
(def ls [l0 l1])
(def qq [[-0.1 0.1]
         [0.3 -0.3]])
(def pp [1 0])

(fact
  (let [result (build-fitness ls pp qq)] 
    result => fn?
    (result pp qq .a.) => 5))

(fact
  (let [result (first (build-constraint otf migration-time ls pp qq 0 0))]
    (first result)  => fn?
    (second result) => (exactly <=)
    (last result)   => otf
    ((first result) .a.) => (/ (+ migration-time 3)
                               (+ migration-time 2 3))))