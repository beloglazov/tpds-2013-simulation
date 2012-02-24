#!/bin/sh

rm results-planetlab-20-100-09-100.json
echo "[" > results-planetlab-20-100-09-100.json
lein run -m simulation.runners.optimal workload/planetlab_20_100_09_100 "[1.0]" 0.1 results-planetlab-20-100-09-100.json
lein run -m simulation.runners.optimal workload/planetlab_20_100_09_100 "[1.0]" 0.2 results-planetlab-20-100-09-100.json
lein run -m simulation.runners.optimal workload/planetlab_20_100_09_100 "[1.0]" 0.3 results-planetlab-20-100-09-100.json

lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 markov-multisize 0.1 results-planetlab-20-100-09-100.json "[1.0]" 0.1 "[30 40 50 60 70 80 90 100]"
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 markov-multisize 0.2 results-planetlab-20-100-09-100.json "[1.0]" 0.1 "[30 40 50 60 70 80 90 100]"
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 markov-multisize 0.3 results-planetlab-20-100-09-100.json "[1.0]" 0.1 "[30 40 50 60 70 80 90 100]"

lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 thr 0.8 results-planetlab-20-100-09-100.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 thr 0.9 results-planetlab-20-100-09-100.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 thr 1.0 results-planetlab-20-100-09-100.json

lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 mad 2.0 results-planetlab-20-100-09-100.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 mad 3.0 results-planetlab-20-100-09-100.json

lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 iqr 1.0 results-planetlab-20-100-09-100.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 iqr 2.0 results-planetlab-20-100-09-100.json

lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 lr 1.0 results-planetlab-20-100-09-100.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 lr 1.1 results-planetlab-20-100-09-100.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 lr 1.2 results-planetlab-20-100-09-100.json

lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 lrr 1.0 results-planetlab-20-100-09-100.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 lrr 1.1 results-planetlab-20-100-09-100.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 lrr 1.2 results-planetlab-20-100-09-100.json

lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 otf 0.1 results-planetlab-20-100-09-100.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 otf 0.2 results-planetlab-20-100-09-100.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 otf 0.3 results-planetlab-20-100-09-100.json

lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 otf-limit 0.1 results-planetlab-20-100-09-100.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 otf-limit 0.2 results-planetlab-20-100-09-100.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 otf-limit 0.3 results-planetlab-20-100-09-100.json

lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 otf-migration-time 0.1 results-planetlab-20-100-09-100.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 otf-migration-time 0.2 results-planetlab-20-100-09-100.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 otf-migration-time 0.3 results-planetlab-20-100-09-100.json

lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 otf-limit-migration-time 0.1 results-planetlab-20-100-09-100.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 otf-limit-migration-time 0.2 results-planetlab-20-100-09-100.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 otf-limit-migration-time 0.3 results-planetlab-20-100-09-100.json

echo "]" >> results-planetlab-20-100-09-100.json