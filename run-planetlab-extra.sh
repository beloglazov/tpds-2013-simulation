#!/bin/sh

rm results-planetlab-20-100-09-100-extra.json
rm results-planetlab-20-100-09-100-extra.csv

echo "[" > results-planetlab-20-100-09-100-extra.json

lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 markov-multisize 0.142 results-planetlab-20-100-09-100-extra.json "[1.0]" 0.1 "[30 40 50 60 70 80 90 100]"
lein run -m simulation.runners.universal workload/planetlab_20_100_09_100 markov-multisize 0.1486 results-planetlab-20-100-09-100-extra.json "[1.0]" 0.1 "[30 40 50 60 70 80 90 100]"

echo "]" >> results-planetlab-20-100-09-100-extra.json

lein run -m simulation.runners.json-to-csv results-planetlab-20-100-09-100-extra.json results-planetlab-20-100-09-100-extra.csv