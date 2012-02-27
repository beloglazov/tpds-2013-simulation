#!/bin/sh

rm results-planetlab-20-100-09-100-violations.json
rm results-planetlab-20-100-09-100-violations.csv

echo "[" > results-planetlab-20-100-09-100-violations.json

lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 markov-multisize 0.1 results-planetlab-20-100-09-100-violations.json "[1.0]" 0.1 "[30 40 50 60 70 80 90 100]"
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 markov-multisize 0.2 results-planetlab-20-100-09-100-violations.json "[1.0]" 0.1 "[30 40 50 60 70 80 90 100]"
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 markov-multisize 0.3 results-planetlab-20-100-09-100-violations.json "[1.0]" 0.1 "[30 40 50 60 70 80 90 100]"

lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 otf-limit 0.1 results-planetlab-20-100-09-100-violations.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 otf-limit 0.2 results-planetlab-20-100-09-100-violations.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 otf-limit 0.3 results-planetlab-20-100-09-100-violations.json

lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 otf-limit-migration-time 0.1 results-planetlab-20-100-09-100-violations.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 otf-limit-migration-time 0.2 results-planetlab-20-100-09-100-violations.json
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 otf-limit-migration-time 0.3 results-planetlab-20-100-09-100-violations.json

echo "]" >> results-planetlab-20-100-09-100-violations.json

lein run -m simulation.runners.json-to-csv results-planetlab-20-100-09-100-violations.json results-planetlab-20-100-09-100-violations.csv