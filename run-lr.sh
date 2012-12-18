#!/bin/sh

rm results-planetlab-20-100-09-100-lr.json
rm results-planetlab-20-100-09-100-lr.csv

echo "[" > results-planetlab-20-100-09-100-lr.json

lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 lr 0.8 results-planetlab-20-100-09-100-lr.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 lr 0.9 results-planetlab-20-100-09-100-lr.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 lr 1.0 results-planetlab-20-100-09-100-lr.json

lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 lrr 0.8 results-planetlab-20-100-09-100-lr.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 lrr 0.9 results-planetlab-20-100-09-100-lr.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 lrr 1.0 results-planetlab-20-100-09-100-lr.json

# LR 0.1225
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 markov-multisize 0.13 results-planetlab-20-100-09-100-lr.json "[1.0]" 0.1 "[30 40 50 60 70 80 90 100]"
# LR 0.2305
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 markov-multisize 0.24 results-planetlab-20-100-09-100-lr.json "[1.0]" 0.1 "[30 40 50 60 70 80 90 100]"
# LR 0.2765
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 markov-multisize 0.37 results-planetlab-20-100-09-100-lr.json "[1.0]" 0.1 "[30 40 50 60 70 80 90 100]"


echo "]" >> results-planetlab-20-100-09-100-lr.json

lein run -m simulation.runners.json-to-csv results-planetlab-20-100-09-100-lr.json results-planetlab-20-100-09-100-lr.csv
