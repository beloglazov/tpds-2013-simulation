(defproject tpds-2012-simulation "1.0.0-SNAPSHOT"
  :description "Simulations for the paper entitled "Host Overload Detection for Dynamic Consolidation of Virtual Machines in Clouds Based on a Markov Chain Model and Multisize Sliding Window Workload Estimation" submitted to the Special Issue of TPDS on Cloud Computing."
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [org.clojure/math.numeric-tower "0.0.1"]
                 [org.clojure/data.json "0.1.2"]
                 [clj-predicates "0.2.0"]
                 [clj-genetic "0.3.0"]
                 [midje "1.3.1"]
                 [org.flanagan/flanagan "1.0"]]
  :main simulation.runners.universal)
