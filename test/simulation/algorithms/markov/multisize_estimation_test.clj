(ns simulation.algorithms.markov.multisize-estimation-test
  (:use simulation.algorithms.markov.multisize-estimation
        clj-predicates.core
        midje.sweet))


(def time-limit 30)

(def workloads [{:until 15
                 :transitions [[0.2 0.8]
                               [1.0 0.0]]}
                {:until 30
                 :transitions [[0.5 0.5]
                               [1.0 0.0]]}])

(def window-sizes [2 4 6])

(fact
  (mean [] 100)          => 0.0
  (mean [0] 100)         => 0.0
  (mean [0 0] 100)       => 0.0
  (mean [1 1] 100)       => 0.02
  (mean [0 1] 100)       => 0.01
  (mean [1 2 3 4 5] 100) => 0.15)

(fact
  (variance [] 100)          => 0.0
  (variance [0] 100)         => 0.0
  (variance [0 0] 100)       => 0.0
  (variance [1 1] 100)       => (roughly 0.0194020202)
  (variance [0 1] 100)       => (roughly 0.0099010101)
  (variance [1 2 3 4 5] 100) => (roughly 0.5112373737)
  (variance [0 0 0 1] 100)   => (roughly 0.0099030303))

(fact
  (acceptable-variance 0.2 5)  => (roughly 0.032)
  (acceptable-variance 0.6 15) => (roughly 0.016))

(fact
  (estimate-probability [0 0 1 1 0 0 0 0 0 0] 100 0) => 0.08
  (estimate-probability [0 0 1 1 0 0 0 0 0 0] 100 1) => 0.02
  (estimate-probability [1 1 0 0 1 1 1 1 1 1] 200 0) => 0.01
  (estimate-probability [1 1 0 0 1 1 1 1 1 1] 200 1) => 0.04)

(fact
  (let [windows [(list 0 0)
                 (list 1 1)]] 
    (update-request-windows windows 4 0 0) => [[0 0 0]
                                               [1 1]]
    (update-request-windows windows 4 0 1) => [[1 0 0]
                                               [1 1]]
    (update-request-windows windows 4 1 0) => [[0 0]
                                               [0 1 1]]
    (update-request-windows windows 4 1 1) => [[0 0]
                                               [1 1 1]]
    
    (update-request-windows windows 2 0 0) => [[0 0]
                                               [1 1]]
    (update-request-windows windows 2 0 1) => [[1 0]
                                               [1 1]]
    (update-request-windows windows 2 1 0) => [[0 0]
                                               [0 1]]
    (update-request-windows windows 2 1 1) => [[0 0]
                                               [1 1]])
  
  (let [windows [(list 0 0)
                 (list 1 1)
                 (list 2 2)]] 
    (update-request-windows windows 4 0 0) => [[0 0 0]
                                               [1 1]
                                               [2 2]]
    (update-request-windows windows 4 0 1) => [[1 0 0]
                                               [1 1]
                                               [2 2]]
    (update-request-windows windows 4 0 2) => [[2 0 0]
                                               [1 1]
                                               [2 2]]
    (update-request-windows windows 4 1 0) => [[0 0]
                                               [0 1 1]
                                               [2 2]]
    (update-request-windows windows 4 1 1) => [[0 0]
                                               [1 1 1]
                                               [2 2]]
    (update-request-windows windows 4 1 2) => [[0 0]
                                               [2 1 1]
                                               [2 2]]
    (update-request-windows windows 4 2 0) => [[0 0]
                                               [1 1]
                                               [0 2 2]]
    (update-request-windows windows 4 2 1) => [[0 0]
                                               [1 1]
                                               [1 2 2]]
    (update-request-windows windows 4 2 2) => [[0 0]
                                               [1 1]
                                               [2 2 2]]
    
    (update-request-windows windows 2 0 0) => [[0 0]
                                               [1 1]
                                               [2 2]]
    (update-request-windows windows 2 0 1) => [[1 0]
                                               [1 1]
                                               [2 2]]
    (update-request-windows windows 2 0 2) => [[2 0]
                                               [1 1]
                                               [2 2]]
    (update-request-windows windows 2 1 0) => [[0 0]
                                               [0 1]
                                               [2 2]]
    (update-request-windows windows 2 1 1) => [[0 0]
                                               [1 1]
                                               [2 2]]
    (update-request-windows windows 2 1 2) => [[0 0]
                                               [2 1]
                                               [2 2]]
    (update-request-windows windows 2 2 0) => [[0 0]
                                               [1 1]
                                               [0 2]]
    (update-request-windows windows 2 2 1) => [[0 0]
                                               [1 1]
                                               [1 2]]
    (update-request-windows windows 2 2 2) => [[0 0]
                                               [1 1]
                                               [2 2]]))

(fact
  (let [request-windows [(list 0 0 0 1)
                         (list 0 1 0 1)]
        estimate-windows [[{2 (list 0 0)
                            4 (list 0 0)}
                           {2 (list 0 0)
                            4 (list 0 0)}]
                          [{2 (list 0 0)
                            4 (list 0 0)}
                           {2 (list 0 0)
                            4 (list 0 0)}]]]
    
    (update-estimate-windows estimate-windows request-windows 
                             0) => [[{2 [1.0 0]
                                      4 [0.75 0 0]}
                                     {2 [0.0 0]
                                      4 [0.25 0 0]}]
                                    [{2 [0 0]
                                      4 [0 0]}
                                     {2 [0 0]
                                      4 [0 0]}]]
    
    (update-estimate-windows estimate-windows request-windows 
                             1) => [[{2 [0 0]
                                      4 [0 0]}
                                     {2 [0 0]
                                      4 [0 0]}]
                                    [{2 [0.5 0]
                                      4 [0.5 0 0]}
                                     {2 [0.5 0]
                                      4 [0.5 0 0]}]])
  
  (let [request-windows [(list 0 2 0 1)
                         (list 0 1 0 1)
                         (list 0 1 2 2)]
        estimate-windows [[{2 (list 0 0)
                            4 (list 0 0)}
                           {2 (list 0 0)
                            4 (list 0 0)}
                           {2 (list 0 0)
                            4 (list 0 0)}]
                          [{2 (list 0 0)
                            4 (list 0 0)}
                           {2 (list 0 0)
                            4 (list 0 0)}
                           {2 (list 0 0)
                            4 (list 0 0)}]
                          [{2 (list 0 0)
                            4 (list 0 0)}
                           {2 (list 0 0)
                            4 (list 0 0)}
                           {2 (list 0 0)
                            4 (list 0 0)}]]]
    
    (update-estimate-windows estimate-windows request-windows 
                             0) => [[{2 [0.5 0]
                                      4 [0.5 0 0]}
                                     {2 [0.0 0]
                                      4 [0.25 0 0]}
                                     {2 [0.5 0]
                                      4 [0.25 0 0]}]
                                    [{2 [0 0]
                                      4 [0 0]}
                                     {2 [0 0]
                                      4 [0 0]}
                                     {2 [0 0]
                                      4 [0 0]}]
                                    [{2 [0 0]
                                      4 [0 0]}
                                     {2 [0 0]
                                      4 [0 0]}
                                     {2 [0 0]
                                      4 [0 0]}]]
    
    (update-estimate-windows estimate-windows request-windows 
                             1) => [[{2 [0 0]
                                      4 [0 0]}
                                     {2 [0 0]
                                      4 [0 0]}
                                     {2 [0 0]
                                      4 [0 0]}]
                                    [{2 [0.5 0]
                                      4 [0.5 0 0]}
                                     {2 [0.5 0]
                                      4 [0.5 0 0]}
                                     {2 [0.0 0]
                                      4 [0.0 0 0]}]
                                    [{2 [0 0]
                                      4 [0 0]}
                                     {2 [0 0]
                                      4 [0 0]}
                                     {2 [0 0]
                                      4 [0 0]}]]
    
    (update-estimate-windows estimate-windows request-windows 
                             2) => [[{2 [0 0]
                                      4 [0 0]}
                                     {2 [0 0]
                                      4 [0 0]}
                                     {2 [0 0]
                                      4 [0 0]}]
                                    [{2 [0 0]
                                      4 [0 0]}
                                     {2 [0 0]
                                      4 [0 0]}
                                     {2 [0 0]
                                      4 [0 0]}]
                                    [{2 [0.5 0]
                                      4 [0.25 0 0]}
                                     {2 [0.5 0]
                                      4 [0.25 0 0]}
                                     {2 [0.0 0]
                                      4 [0.5 0 0]}]]))

(fact
  (let [estimate-windows [[{2 (list 0.5 0)
                            4 (list 0 0 0 1)}
                           {2 (list 0.5 1.0)
                            4 (list 1 1 1 0)}]
                          [{2 (list 0.25 0.5)
                            4 (list 0.5 0.5 0.25 0.25)}
                           {2 (list 0.75 0.5)
                            4 (list 0.5 0.5 0.75 0.75)}]]
        variances [[{2 0
                     4 0}
                    {2 0
                     4 0}]
                   [{2 0
                     4 0}
                    {2 0
                     4 0}]]]
    
    (update-variances variances estimate-windows
                      0) => [[{2 0.125
                               4 0.25}
                              {2 0.125
                               4 0.25}]
                             [{2 0
                               4 0}
                              {2 0
                               4 0}]]
    (update-variances variances estimate-windows
                      1) => [[{2 0
                               4 0}
                              {2 0
                               4 0}]
                             [{2 0.03125
                               4 0.020833333333333332}
                              {2 0.03125
                               4 0.020833333333333332}]]

    (update-variances (update-variances variances estimate-windows 0) estimate-windows 
                      0) => [[{2 0.125
                               4 0.25}
                              {2 0.125
                               4 0.25}]
                             [{2 0
                               4 0}
                              {2 0
                               4 0}]])
  
  (let [estimate-windows [[{2 (list 0 0)
                            4 (list 0 0 0 1)}
                           {2 (list 1 1)
                            4 (list 1 1 0 0)}
                           {2 (list 0 0)
                            4 (list 0 0 1 0)}]
                          [{2 (list 0.25 0.5)
                            4 (list 0.25 0.5  0.05 0.25)}
                           {2 (list 0.5  0.25)
                            4 (list 0.5  0.25 0.55 0.4)}
                           {2 (list 0.25 0.25)
                            4 (list 0.25 0.25 0.4  0.35)}]
                          [{2 (list 0 1)
                            4 (list 0 1 0 1)}
                           {2 (list 1 0)
                            4 (list 1 0 0 0)}
                           {2 (list 0 0)
                            4 (list 0 0 1 0)}]]
        variances [[{2 0
                     4 0}
                    {2 0
                     4 0}
                    {2 0
                     4 0}]
                   [{2 0
                     4 0}
                    {2 0
                     4 0}
                    {2 0
                     4 0}]
                   [{2 0
                     4 0}
                    {2 0
                     4 0}
                    {2 0
                     4 0}]]]
    
    (update-variances variances estimate-windows
                      0) => [[{2 0.0
                               4 0.25}
                              {2 0.0
                               4 0.3333333333333333}
                              {2 0.0
                               4 0.25}]
                             [{2 0
                               4 0}
                              {2 0
                               4 0}
                              {2 0
                               4 0}]
                             [{2 0
                               4 0}
                              {2 0
                               4 0}
                              {2 0
                               4 0}]]
    (update-variances variances estimate-windows
                      1) => [[{2 0
                               4 0}
                              {2 0
                               4 0}
                              {2 0
                               4 0}]
                             [{2 0.03125
                               4 0.03395833333333333}
                              {2 0.03125
                               4 0.017500000000000005}
                              {2 0.0
                               4 0.005625000000000001}]
                             [{2 0
                               4 0}
                              {2 0
                               4 0}
                              {2 0
                               4 0}]]
    (update-variances variances estimate-windows
                      2) => [[{2 0
                               4 0}
                              {2 0
                               4 0}
                              {2 0
                               4 0}]
                             [{2 0
                               4 0}
                              {2 0
                               4 0}
                              {2 0
                               4 0}]
                             [{2 0.5
                               4 0.3333333333333333}
                              {2 0.5
                               4 0.25}
                              {2 0.0
                               4 0.25}]]))

(fact
  (let [estimate-windows [[{2 (list 0.5 0)
                            4 (list 0 0 0 1)}
                           {2 (list 0.5 1.0)
                            4 (list 1 1 1 0)}]
                          [{2 (list 0.25 0.5)
                            4 (list 0.5 0.5 0.25 0.25)}
                           {2 (list 0.75 0.5)
                            4 (list 0.5 0.5 0.75 0.75)}]]
        acceptable-variances [[{2 0
                                4 0}
                               {2 0
                                4 0}]
                              [{2 0
                                4 0}
                               {2 0
                                4 0}]]]
    
    (update-acceptable-variances acceptable-variances estimate-windows
                      0) => [[{2 0.125
                               4 0.0}
                              {2 0.125
                               4 0.0}]
                             [{2 0
                               4 0}
                              {2 0
                               4 0}]]
    (update-acceptable-variances acceptable-variances estimate-windows 
                      1) => [[{2 0
                               4 0}
                              {2 0
                               4 0}]
                             [{2 0.09375
                               4 0.0625}
                              {2 0.09375
                               4 0.0625}]]    
    (update-acceptable-variances (update-acceptable-variances acceptable-variances
                                                              estimate-windows 0)
                                 estimate-windows 
                                 0) => [[{2 0.125
                                          4 0.0}
                                         {2 0.125
                                          4 0.0}]
                                        [{2 0
                                          4 0}
                                         {2 0
                                          4 0}]]
    
  (let [estimate-windows [[{2 (list 0 0)
                            4 (list 0 0 0 1)}
                           {2 (list 1 1)
                            4 (list 1 1 0 0)}
                           {2 (list 0 0)
                            4 (list 0 0 1 0)}]
                          [{2 (list 0.25 0.5)
                            4 (list 0.25 0.5  0.05 0.25)}
                           {2 (list 0.5  0.25)
                            4 (list 0.5  0.25 0.55 0.4)}
                           {2 (list 0.25 0.25)
                            4 (list 0.25 0.25 0.4  0.35)}]
                          [{2 (list 0 1)
                            4 (list 0 1 0 1)}
                           {2 (list 1 0)
                            4 (list 1 0 0 0)}
                           {2 (list 0 0)
                            4 (list 0 0 1 0)}]]
        acceptable-variances [[{2 0
                                4 0}
                               {2 0
                                4 0}
                               {2 0
                                4 0}]
                              [{2 0
                                4 0}
                               {2 0
                                4 0}
                               {2 0
                                4 0}]
                              [{2 0
                                4 0}
                               {2 0
                                4 0}
                               {2 0
                                4 0}]]]
    
    (update-acceptable-variances acceptable-variances estimate-windows
                      0) => [[{2 0.0
                               4 0.0}
                              {2 0.0
                               4 0.0}
                              {2 0.0
                               4 0.0}]
                             [{2 0
                               4 0}
                              {2 0
                               4 0}
                              {2 0
                               4 0}]
                             [{2 0
                               4 0}
                              {2 0
                               4 0}
                              {2 0
                               4 0}]]
    (update-acceptable-variances acceptable-variances estimate-windows
                      1) => [[{2 0
                               4 0}
                              {2 0
                               4 0}
                              {2 0
                               4 0}]
                             [{2 0.09375
                               4 0.046875}
                              {2 0.125
                               4 0.0625}
                              {2 0.09375
                               4 0.046875}]
                             [{2 0
                               4 0}
                              {2 0
                               4 0}
                              {2 0
                               4 0}]]
    (update-acceptable-variances acceptable-variances estimate-windows
                      2) => [[{2 0
                               4 0}
                              {2 0
                               4 0}
                              {2 0
                               4 0}]
                             [{2 0
                               4 0}
                              {2 0
                               4 0}
                              {2 0
                               4 0}]
                             [{2 0.0
                               4 0.0}
                              {2 0.0
                               4 0.0}
                              {2 0.0
                               4 0.0}]])))

(fact
  (let [windows [[{2 (list 0.5 0)
                   4 (list 0 0 0 1)}
                  {2 (list 0.5 1.0)
                   4 (list 1 1 1 0)}]
                 [{2 (list 0.25 0.5)
                   4 (list 0.5 0.5 0.25 0.25)}
                  {2 (list 0.75 0.5)
                   4 (list 0.5 0.5 0.75 0.75)}]]
        variances [[{2 0.5
                     4 0}
                    {2 0.5
                     4 1}]
                   [{2 0.25
                     4 0.5}
                    {2 0.75
                     4 0.5}]]
        windows-empty [[{2 (list)
                         4 (list)}
                        {2 (list)
                         4 (list)}]
                       [{2 (list)
                         4 (list)}
                        {2 (list)
                         4 (list)}]]
        history [[{2 (list 0)
                   4 (list 0)}
                  {2 (list 0)
                   4 (list 0)}]
                 [{2 (list 0)
                   4 (list 0)}
                  {2 (list 0)
                   4 (list 0)}]]]
    
    (update-history history windows-empty) => [[{2 [0.0 0]
                                                 4 [0.0 0]}
                                                {2 [0.0 0]
                                                 4 [0.0 0]}]
                                               [{2 [0.0 0]
                                                 4 [0.0 0]}
                                                {2 [0.0 0]
                                                 4 [0.0 0]}]]
    
    (update-history history windows) => [[{2 [0.5 0]
                                           4 [0 0]}
                                          {2 [0.5 0]
                                           4 [1 0]}]
                                         [{2 [0.25 0]
                                           4 [0.5 0]}
                                          {2 [0.75 0]
                                           4 [0.5 0]}]]
    
    (update-history history variances) => [[{2 [0.5 0]
                                             4 [0 0]}
                                            {2 [0.5 0]
                                             4 [1 0]}]
                                           [{2 [0.25 0]
                                             4 [0.5 0]}
                                            {2 [0.75 0]
                                             4 [0.5 0]}]]
    
    (update-history (update-history history windows)
                    windows) => [[{2 [0.5 0.5 0]
                                   4 [0 0 0]}
                                  {2 [0.5 0.5 0]
                                   4 [1 1 0]}]
                                 [{2 [0.25 0.25 0]
                                   4 [0.5 0.5 0]}
                                  {2 [0.75 0.75 0]
                                   4 [0.5 0.5 0]}]]))

(fact
  (let [variances [[{2 0.2
                     4 0.9}
                    {2 0.2
                     4 0.6}]
                   [{2 0.2
                     4 0}
                    {2 0.2
                     4 0.8}]]
        acceptable-variances [[{2 0.1
                                4 0.5}
                               {2 0.4
                                4 0.5}]
                              [{2 0.4
                                4 0.5}
                               {2 0.1
                                4 0.5}]]
        window-sizes [2 4]] 
    (select-window variances acceptable-variances window-sizes) => [[2
                                                                     2]
                                                                    [4
                                                                     2]])
  
  (let [variances [[{2 0
                     4 0.9}
                    {2 0
                     4 0}]
                   [{2 0
                     4 0}
                    {2 0
                     4 0.8}]]
        acceptable-variances [[{2 0.5
                                4 0.5}
                               {2 0.6
                                4 0.5}]
                              [{2 0.7
                                4 0.5}
                               {2 0.4
                                4 0.5}]]
        window-sizes [2 4]] 
    (select-window variances acceptable-variances window-sizes) => [[2
                                                                     4]
                                                                    [4
                                                                     2]])
  
  (let [variances [[{2 0
                     4 0.9}
                    {2 0
                     4 0}
                    {2 0
                     4 1.0}]
                   [{2 0
                     4 0}
                    {2 0
                     4 0.8}
                    {2 0
                     4 0}]
                   [{2 0
                     4 0}
                    {2 0
                     4 0.8}
                    {2 0.5
                     4 0}]]
        acceptable-variances [[{2 0.5
                                4 0.9}
                               {2 0.6
                                4 0.9}
                               {2 0.6
                                4 0.9}]
                              [{2 0.7
                                4 0.9}
                               {2 0.4
                                4 0.9}
                               {2 0.4
                                4 0.9}]
                              [{2 0.7
                                4 0.9}
                               {2 0.4
                                4 0.5}
                               {2 0.4
                                4 0.9}]]
        window-sizes [2 4]] 
    (select-window variances acceptable-variances window-sizes) => [[4
                                                                     4
                                                                     2]
                                                                    [4
                                                                     4
                                                                     4]
                                                                    [4
                                                                     2
                                                                     2]]))

(fact  
  (let [selected-window-history [[(list 0) 
                                  (list 0)]
                                 [(list 0)
                                  (list 0)]]
        selected-windows1 [[2
                            2]
                           [4
                            2]]
        selected-windows2 [[2
                            4]
                           [4
                            2]]]
    
    (update-selected-window-history selected-window-history selected-windows1) => [[(list 2 0) 
                                                                                    (list 2 0)]
                                                                                   [(list 4 0) 
                                                                                    (list 2 0)]]
    
    (update-selected-window-history 
      (update-selected-window-history selected-window-history selected-windows1)
      selected-windows2) => [[(list 2 2 0) 
                              (list 4 2 0)]
                             [(list 4 4 0) 
                              (list 2 2 0)]])
  
  (let [selected-window-history [[(list 0)
                                  (list 0)
                                  (list 0)]
                                 [(list 0)
                                  (list 0)
                                  (list 0)]
                                 [(list 0)
                                  (list 0)
                                  (list 0)]]
        selected-windows [[4
                           4
                           2]
                          [4
                           4
                           4]
                          [4
                           2
                           2]]]
    
    (update-selected-window-history selected-window-history selected-windows) => [[(list 4 0) 
                                                                                   (list 4 0)
                                                                                   (list 2 0)]
                                                                                  [(list 4 0) 
                                                                                   (list 4 0)
                                                                                   (list 4 0)]
                                                                                  [(list 4 0) 
                                                                                   (list 2 0)
                                                                                   (list 2 0)]]
    
    (update-selected-window-history 
      (update-selected-window-history selected-window-history selected-windows)
      selected-windows) => [[(list 4 4 0) 
                             (list 4 4 0)
                             (list 2 2 0)]
                            [(list 4 4 0) 
                             (list 4 4 0)
                             (list 4 4 0)]
                            [(list 4 4 0) 
                             (list 2 2 0)
                             (list 2 2 0)]]))

(fact
  (let [estimate-windows [[{2 (list 0 0)
                            4 (list 0 0 0 1)}
                           {2 (list 1 1)
                            4 (list 1 1 0 0)}
                           {2 (list 0 0)
                            4 (list 0 0 1 0)}]
                          [{2 (list 0.25 0.5)
                            4 (list 0.25 0.5  0.05 0.25)}
                           {2 (list 0.5  0.25)
                            4 (list 0.6  0.25 0.55 0.4)}
                           {2 (list 0.25 0.25)
                            4 (list 0.15 0.25 0.4  0.35)}]
                          [{2 (list 0 1)
                            4 (list 0 1 0 1)}
                           {2 (list 1 0)
                            4 (list 0.2 0 0 0)}
                           {2 (list 0 0)
                            4 (list 0 0 1 0)}]]
        selected-windows1 [[2 4 2]
                           [2 2 4]
                           [4 2 2]]
        selected-windows2 [[4 4 4]
                           [2 2 2]
                           [2 4 2]]] 
    
    (select-best-estimates estimate-windows selected-windows1) => [[0
                                                                    1
                                                                    0]
                                                                   [0.25
                                                                    0.5
                                                                    0.15]
                                                                   [0
                                                                    1
                                                                    0]]
    
    (select-best-estimates estimate-windows selected-windows2) => [[0
                                                                    1
                                                                    0]
                                                                   [0.25
                                                                    0.5
                                                                    0.25]
                                                                   [0
                                                                    0.2
                                                                    0]])
  
  (let [estimate-windows [[{2 (list)
                            4 (list)}
                           {2 (list)
                            4 (list)}]
                          [{2 (list)
                            4 (list)}
                           {2 (list)
                            4 (list)}]]] 
    
    (select-best-estimates estimate-windows [[2 4]
                                             [4 2]]) => [[0.0 0.0]
                                                         [0.0 0.0]]
    
    (select-best-estimates estimate-windows [[2 2]
                                             [4 4]]) => [[0.0 0.0]
                                                         [0.0 0.0]]))

(fact
  (let [best-estimate-history [[(list 0)
                                (list 0)
                                (list 0)]
                               [(list 0)
                                (list 0)
                                (list 0)]
                               [(list 0)
                                (list 0)
                                (list 0)]]
        best-estimates1 [[0
                          1
                          0]
                         [0.25
                          0.5
                          0.25]
                         [0
                          1
                          0]]
        best-estimates2 [[0
                          1
                          0]
                         [0.25
                          0.6
                          0.15]
                         [0
                          1
                          0]]] 
    
    (update-best-estimate-history best-estimate-history
                                  best-estimates1) => [[[0 0]
                                                        [1 0]
                                                        [0 0]]
                                                       [[0.25 0]
                                                        [0.5 0]
                                                        [0.25 0]]
                                                       [[0 0]
                                                        [1 0]
                                                        [0 0]]]
    
    (update-best-estimate-history (update-best-estimate-history best-estimate-history
                                                                best-estimates1)
                                  best-estimates2) => [[[0 0 0]
                                                        [1 1 0]
                                                        [0 0 0]]
                                                       [[0.25 0.25 0]
                                                        [0.6 0.5 0]
                                                        [0.15 0.25 0]]
                                                       [[0 0 0]
                                                        [1 1 0]
                                                        [0 0 0]]]))

(fact
  (let [workloads [{:until 1
                    :transitions [[0.2 0.8]
                                  [1.0 0.0]]}
                   {:until 4
                    :transitions [[0.5 0.5]
                                  [1.0 0.0]]}]
        estimate-history [[{2 [0.4 0.2 0.1]
                            4 [0.6 0.4 0.2 0.1]}
                           {2 [0.3 0.7 0.6]
                            4 [0.3 0.3 0.7 0.6]}]
                          [{2 [0.8 0.9 0.9]
                            4 [0.8 0.8 0.9 0.9]}
                           {2 [0.1 0.1 0.1]
                            4 [0.1 0.1 0.1 0.1]}]]]
    
    (compute-error-of-estimates workloads 
                                estimate-history) => [[{2 0.03666666666666667
                                                        4 0.03}
                                                       {2 0.04000000000000001
                                                        4 0.04000000000000001}]
                                                      [{2 0.01999999999999999
                                                        4 0.024999999999999988}
                                                       {2 0.010000000000000002
                                                        4 0.010000000000000002}]]))

(fact
  (let [workloads [{:until 1
                    :transitions [[0.2 0.8]
                                  [1.0 0.0]]}
                   {:until 4
                    :transitions [[0.5 0.5]
                                  [1.0 0.0]]}]
        best-estimate-history [[[0.6 0.4 0.2 0.1]
                                [0.3 0.3 0.7 0.6]]
                               [[0.8 0.8 0.9 0.9]
                                [0.1 0.1 0.1 0.1]]]]
    
    (compute-error-of-best-estimates workloads 
                                     best-estimate-history) => [[0.03
                                                                 0.04000000000000001]
                                                                [0.024999999999999988
                                                                 0.010000000000000002]]))

(fact
  (init-request-windows 1) => [(list)]
  (init-request-windows 2) => [(list)
                               (list)]
  (init-request-windows 3) => [(list)
                               (list)
                               (list)])

(fact
  (init-variances [2 4] 1) => [[{2 1.0
                                 4 1.0}]]
  (init-variances [2 4] 2) => [[{2 1.0
                                 4 1.0}
                                {2 1.0
                                 4 1.0}]
                               [{2 1.0
                                 4 1.0}
                                {2 1.0
                                 4 1.0}]]
  (init-variances [2 4] 3) => [[{2 1.0
                                 4 1.0}
                                {2 1.0
                                 4 1.0}
                                {2 1.0
                                 4 1.0}]
                               [{2 1.0
                                 4 1.0}
                                {2 1.0
                                 4 1.0}
                                {2 1.0
                                 4 1.0}]
                               [{2 1.0
                                 4 1.0}
                                {2 1.0
                                 4 1.0}
                                {2 1.0
                                 4 1.0}]])

(fact
  (init-3-level-data [2 4] 1) => [[{2 (list)
                                    4 (list)}]]
  (init-3-level-data [2 4] 2) => [[{2 (list)
                                    4 (list)}
                                   {2 (list)
                                    4 (list)}]
                                  [{2 (list)
                                    4 (list)}
                                   {2 (list)
                                    4 (list)}]]
  (init-3-level-data [2 4] 3) => [[{2 (list)
                                    4 (list)}
                                   {2 (list)
                                    4 (list)}
                                   {2 (list)
                                    4 (list)}]
                                  [{2 (list)
                                    4 (list)}
                                   {2 (list)
                                    4 (list)}
                                   {2 (list)
                                    4 (list)}]
                                  [{2 (list)
                                    4 (list)}
                                   {2 (list)
                                    4 (list)}
                                   {2 (list)
                                    4 (list)}]])

(fact
  (init-2-level-history 1) => [[(list)]]
  (init-2-level-history 2) => [[(list)
                                (list)]
                               [(list)
                                (list)]]
  (init-2-level-history 3) => [[(list)
                                (list)
                                (list)]
                               [(list)
                                (list)
                                (list)]
                               [(list)
                                (list)
                                (list)]])

(fact
  (init-selected-window-sizes [2 4] 1) => [[2]]
  (init-selected-window-sizes [2 4] 2) => [[2 2]
                                           [2 2]]
  (init-selected-window-sizes [2 4] 3) => [[2 2 2]
                                           [2 2 2]
                                           [2 2 2]])
;
;(fact
;  (run-simulation workloads window-sizes time-limit) => (just 
;                                                          {:last-time 29
;                                                           :states (n-of 0 30)
;                                                           :request-windows vector?
;                                                           :estimate-windows vector?
;                                                           :variances vector?
;                                                           :acceptable-variances vector?
;                                                           :selected-windows coll?
;                                                           :best-estimates vector?
;                                                           :estimate-errors vector?
;                                                           :best-estimate-errors vector?})
;  (provided
;    (workload-generator/generate-state workloads (as-checker not-negnum?) 0) => 0)
;  
;  (run-simulation workloads window-sizes time-limit) => (just 
;                                                          {:last-time 29
;                                                           :states (n-of 1 30)
;                                                           :request-windows vector?
;                                                           :estimate-windows vector?
;                                                           :variances vector?
;                                                           :acceptable-variances vector?
;                                                           :selected-windows coll?
;                                                           :best-estimates vector?
;                                                           :estimate-errors vector?
;                                                           :best-estimate-errors vector?})
;  (provided
;    (workload-generator/generate-state workloads (as-checker not-negnum?) anything) => 1))
;
;
;
;
;
;
;
;




