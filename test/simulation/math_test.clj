(ns simulation.math-test
  (:use simulation.math
        midje.sweet))

(def data1 [1 1 2 2 4 6 9])
(def data2 [105 109 107 112 102 118 115 104 110 116 108])
(def data3 [2 4 7 -20 22 -1 0 -1 7 15 8 4 -4 11 11 12 3 12 18 1])

(fact
  "Median Absolute Deviation (MAD)"
  (mad data1) => 1)

(fact
  "Inter Quartile Range (IQR)"
  (iqr data2) => 10
  (iqr data3) => 12)

(fact
  (tricube-weights 5) => (just (roughly 1.492)
                               (roughly 1.492)
                               (roughly 1.492)
                               (roughly 1.048)
                               (roughly 1.000))
  (tricube-weights 10) => (just (roughly 6.736)
                                (roughly 6.736)
                                (roughly 6.736)
                                (roughly 2.869)
                                (roughly 1.758)
                                (roughly 1.317)
                                (roughly 1.119)
                                (roughly 1.033)
                                (roughly 1.004)
                                (roughly 1.000)))

(fact
  (tricube-bisquare-weights data1) => (just (roughly 3.035) 
                                            (roughly 3.035)
                                            (roughly 3.035)
                                            (roughly 1.579)
                                            (roughly 1.417)
                                            (roughly 1.802)
                                            (roughly 5.224)))

(fact
  (loess-parameter-estimates data3) => (just (roughly 2.2639) 
                                             (roughly 0.3724)))

(fact
  (loess-robust-parameter-estimates data3) => (just (roughly 2.4547) 
                                                    (roughly 0.3901)))

























