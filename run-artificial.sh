#!/bin/sh

rm results-artificial.json
rm results-artificial.csv
rm results-artificial-estimates.json
rm results-artificial-estimates.csv

echo "[" > results-artificial.json

lein run -m simulation.runners.artificial-workload-optimal workload/artificial results-artificial.json "[1.0]" 0.3
lein run -m simulation.runners.artificial-workload-markov-optimal workload/artificial results-artificial.json "[1.0]" 0.3 0.1 1
lein run -m simulation.runners.artificial-workload-markov-multisize workload/artificial results-artificial.json results-artificial-estimates.json "[1.0]" "[30 40 50 60 70 80 90 100]" 0.3 0.1 1

echo "]" >> results-artificial.json

lein run -m simulation.runners.json-to-csv results-artificial.json results-artificial.csv
lein run -m simulation.runners.json-to-csv-estimates results-artificial-estimates.json results-artificial-estimates.csv