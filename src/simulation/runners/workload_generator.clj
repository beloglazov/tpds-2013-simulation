(ns simulation.runners.workload-generator
  (:use simulation.io)
  (:gen-class))

; Amazon EC2 instance types: 1700, 2000, 2400, 3000
; Amazon EC2 server types: 2x2400, 4x3000, 8x3000, 16x3000
; K. Mills, J. Filliben and C. Dabrowsk, Comparing VM-Placement Algorithms for On-Demand Clouds

(def vm-mips [1700, 2000, 2400, 3000])
(def host {:mips 12000}) ;4x3000
(def thresholds [0.8 0.85 0.9 0.95 1.0])

(defn -main [& args]
  (let [otf-min (Double/valueOf (nth args 0))
        otf-max (Double/valueOf (nth args 1))
        otf-max-at-30 (Double/valueOf (nth args 2))
        n (Double/valueOf (nth args 3))
        input-path (nth args 4)
        output-path (nth args 5)
        ] 
    (do 
      (pregenerate-workload-filter-otf vm-mips host thresholds otf-min otf-max otf-max-at-30 n input-path output-path)
      (println "Done"))))

; lein run -m simulation.runners.workload-generator 0.3 1.0 0.2 100 "../data" "workload/planetlab_30_100_20_100"