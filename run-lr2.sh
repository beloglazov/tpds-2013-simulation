#!/bin/sh

rm results-planetlab-20-100-09-100-lr.json
rm results-planetlab-20-100-09-100-lr.csv

echo "[" > results-planetlab-20-100-09-100-lr.json

lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 lr 1.05 results-planetlab-20-100-09-100-lr.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 lr 0.95 results-planetlab-20-100-09-100-lr.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 lr 0.85 results-planetlab-20-100-09-100-lr.json

lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 lrr 1.05 results-planetlab-20-100-09-100-lr.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 lrr 0.95 results-planetlab-20-100-09-100-lr.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 lrr 0.85 results-planetlab-20-100-09-100-lr.json

# LR 0.0900781485873219
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 markov-multisize 0.099 results-planetlab-20-100-09-100-lr.json "[1.0]" 0.2 "[30 40 50 60 70 80 90 100]"
# LR 0.1770959824533416
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 markov-multisize 0.182 results-planetlab-20-100-09-100-lr.json "[1.0]" 0.2 "[30 40 50 60 70 80 90 100]"
# LR 0.2650566433862585
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 markov-multisize 0.31 results-planetlab-20-100-09-100-lr.json "[1.0]" 0.2 "[30 40 50 60 70 80 90 100]"

# LRR 0.08997772613535152
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 markov-multisize 0.099 results-planetlab-20-100-09-100-lr.json "[1.0]" 0.2 "[30 40 50 60 70 80 90 100]"
# LRR 0.17424215046509506
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 markov-multisize 0.1792 results-planetlab-20-100-09-100-lr.json "[1.0]" 0.2 "[30 40 50 60 70 80 90 100]"
# LRR 0.26258378742468463
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 markov-multisize 0.304 results-planetlab-20-100-09-100-lr.json "[1.0]" 0.2 "[30 40 50 60 70 80 90 100]"

echo "]" >> results-planetlab-20-100-09-100-lr.json

lein run -m simulation.runners.json-to-csv results-planetlab-20-100-09-100-lr.json results-planetlab-20-100-09-100-lr.csv
