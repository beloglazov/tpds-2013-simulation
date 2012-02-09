(ns simulation.math
  (:use clj-predicates.core
        clojure.math.numeric-tower)
  (:import [flanagan.analysis Stat Regression]))

(defn median [data]
  {:pre [(coll? data)]
   :post [(number? %)]}
  (Stat/median (double-array data)))

(defn mad [data]
  {:pre [(coll? data)]
   :post [(number? %)]}
  (let [adata (double-array data)
        median (Stat/median adata)]
    (Stat/median 
      (amap ^doubles adata idx ret 
            (Math/abs (- median (aget ^doubles adata idx))) ))))

(defn iqr [data]
  {:pre [(coll? data)]
   :post [(number? %)]}
  (let [sorted-data (vec (sort data))
        n (inc (count data))
        q1 (dec (int (round (* 0.25 n))))
        q3 (dec (int (round (* 0.75 n))))]
    (- (get sorted-data q3) (get sorted-data q1))))
 
(defn tricube-weights [n]
  {:pre [(posnum? n)]
   :post [(coll? %)]}
  (let [top (dec n)
        spread top
        weights (for [i (range 2 n)
                      :let [k (Math/pow (- 1 (Math/pow (/ (- top i) spread) 3)) 3)]]
                  (if (pos? k)
                    (/ 1 k)
                    Double/MAX_VALUE))
        first-weight (first weights)]
    (conj (conj weights first-weight) first-weight)))
 
(defn tricube-bisquare-weights [data]
  {:pre [(coll? data)]
   :post [(coll? %)]}
  (let [data-vec (vec data)
        n (count data-vec)
        s6 (* 6 (median (map abs data-vec)))
        weights (vec (tricube-weights n))
        weights2 (for [i (range 2 n)
                       :let [k (Math/pow (- 1 (Math/pow (/ (get data-vec i) s6) 2)) 2)]]
                   (if (pos? k)
                     (* (/ 1 k) (get weights i))
                     Double/MAX_VALUE))
        first-weight (first weights2)]
    (conj (conj weights2 first-weight) first-weight)))

(defn loess-parameter-estimates [data]
  {:pre [(coll? data)]
   :post [(coll? %)]}
  (let [n (count data)
        xs (take n (range 1 (inc n)))        
        regression (Regression. (double-array xs) 
                                (double-array data) 
                                (double-array (tricube-weights n)))]
    (do
      (.linear regression)
      (seq (.getBestEstimates regression)))))

(defn loess-robust-parameter-estimates [data]
  {:pre [(coll? data)]
   :post [(coll? %)]}
  (let [n (count data)
        xs (double-array (take n (range 1 (inc n))))
        adata (double-array data)
        regression (Regression. xs adata (double-array (tricube-weights n)))]
    (do
      (.linear regression)
      (let [regression2 (Regression. xs adata 
                                     (double-array (tricube-bisquare-weights 
                                                     (seq (.getResiduals regression)))))]        
        (do
          (.linear regression2)
          (let [estimates (seq (.getBestEstimates regression2))]
            (if (some #(= % Double/NaN) estimates)
              (seq (.getBestEstimates regression))
              estimates))
          )))))






