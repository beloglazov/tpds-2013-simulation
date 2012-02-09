(ns simulation.io
  (:use clj-predicates.core
        simulation.core)
  (:require [clojure.java.io :as io]
            [clojure.data.json :as json]))

(defn read-file [file-path]
  {:pre [(string? file-path)]
   :post [(coll? %)]}
  (map #(Integer/parseInt %) 
       (with-open [rdr (io/reader file-path)] 
         (doall (line-seq rdr)))))

(defn read-random-file [dir-path]
  {:pre [(string? dir-path)]
   :post [(coll? %)]}
  (read-file (.getAbsolutePath 
               (rand-nth (filter #(.isFile %)
                                 (.listFiles (io/file dir-path)))))))

(defn read-n-random-files [dir-path n]
  {:pre [(string? dir-path)
         (posnum? n)]
   :post [(coll? %)]}
  (repeatedly n #(read-file (.getAbsolutePath 
                              (rand-nth (filter (fn [f] (.isFile f)) 
                                                (.listFiles (io/file dir-path))))))))

(defn load-vms [mips host threshold dir-path]
  {:pre [(coll? mips)
         (map? host)
         (posnum? threshold)
         (string? dir-path)]
   :post [(coll? %)]}
  (loop [vms []         
         host-mips (* threshold (:mips host))]
    (let [vm-utilization (vec (map #(/ % 100) (read-random-file dir-path)))
          vm-initial-utilization (first vm-utilization)
          vm-mips (rand-nth mips)
          vm-initial-mips (* vm-mips vm-initial-utilization)]
      (if (>= host-mips vm-initial-mips)
        (recur (conj vms {:mips vm-mips
                          :utilization vm-utilization})
               (- host-mips vm-initial-mips))
        vms))))

(defn pregenerate-workload [mips host threshold n dir-path output-path]
  {:pre [(coll? mips)
         (map? host)
         (posnum? threshold)
         (posnum? n)
         (string? dir-path)
         (string? output-path)]
   :post [(coll? %)]}
  (let [workload (repeatedly n (partial load-vms mips host threshold dir-path))] 
    (do 
      (spit output-path (json/json-str workload))
      workload)))

(defn pregenerate-workload-filter-otf [mips host thresholds otf-min otf-max otf-max-at-30 n input-path output-path]
  {:pre [(coll? mips)
         (map? host)
         (coll? thresholds)
         (posnum? otf-min)
         (posnum? otf-max)
         (posnum? otf-max-at-30)
         (posnum? n)
         (string? input-path)
         (string? output-path)]
   :post [(coll? %)]}
  (loop [workload []]
    (if (== n (count workload)) 
      (do 
        (spit output-path (json/json-str workload))
        workload)
      (let [vms (load-vms mips host (rand-nth thresholds) input-path)
            host-utilization (host-utilization-history host vms)
            host-utilization-30 (take 30 host-utilization)
            otf (double (/ (count (filter #(>= % 1) host-utilization)) (count host-utilization)))
            otf-at-30 (double (/ (count (filter #(>= % 1) host-utilization-30)) (count host-utilization-30)))]
        (if (and
              (>= otf otf-min)
              (<= otf otf-max)
              (<= otf-at-30 otf-max-at-30))
          (do
            (prn "otf" otf)
            (prn "otf-at-30" otf-at-30)
            (recur (conj workload vms)))
          (recur workload))))))

(defn read-pregenerated-workload [input-path]
  {:pre [(string? input-path)]
   :post [(coll? %)]}
  (json/read-json (slurp input-path)))

