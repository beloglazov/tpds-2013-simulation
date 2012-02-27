#!/bin/sh

rm results-planetlab-20-100-09-100-extra2.json
rm results-planetlab-20-100-09-100-extra2.csv

echo "[" > results-planetlab-20-100-09-100-extra2.json

lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 lrr 1.05 results-planetlab-20-100-09-100.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 markov-multisize 0.2441 results-planetlab-20-100-09-100-extra2.json "[1.0]" 0.5 "[30 40 50 60 70 80 90 100]"

lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 lr 1.05 results-planetlab-20-100-09-100.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 markov-multisize 0.249 results-planetlab-20-100-09-100-extra2.json "[1.0]" 0.5 "[30 40 50 60 70 80 90 100]"


lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 lr 1.0 results-planetlab-20-100-09-100.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 markov-multisize 0.4 results-planetlab-20-100-09-100-extra2.json "[1.0]" 0.5 "[30 40 50 60 70 80 90 100]"

lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 lr 1.1 results-planetlab-20-100-09-100.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 markov-multisize 0.1535 results-planetlab-20-100-09-100-extra2.json "[1.0]" 0.5 "[30 40 50 60 70 80 90 100]"

lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 lr 1.2 results-planetlab-20-100-09-100.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 markov-multisize 0.0675 results-planetlab-20-100-09-100-extra2.json "[1.0]" 0.5 "[30 40 50 60 70 80 90 100]"


lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 lrr 1.0 results-planetlab-20-100-09-100.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 markov-multisize 0.366 results-planetlab-20-100-09-100-extra2.json "[1.0]" 0.5 "[30 40 50 60 70 80 90 100]"

lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 lrr 1.1 results-planetlab-20-100-09-100.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 markov-multisize 0.149 results-planetlab-20-100-09-100-extra2.json "[1.0]" 0.5 "[30 40 50 60 70 80 90 100]"

lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 lrr 1.2 results-planetlab-20-100-09-100.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 markov-multisize 0.0676 results-planetlab-20-100-09-100-extra2.json "[1.0]" 0.5 "[30 40 50 60 70 80 90 100]"

echo "]" >> results-planetlab-20-100-09-100-extra2.json

lein run -m simulation.runners.json-to-csv results-planetlab-20-100-09-100-extra2.json results-planetlab-20-100-09-100-extra2.csv