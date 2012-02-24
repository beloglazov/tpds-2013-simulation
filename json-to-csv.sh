#!/bin/sh

rm results-artificial.csv
rm results-planetlab-20-100-09-100.csv

lein run -m simulation.runners.json-to-csv results-artificial.json results-artificial.csv
lein run -m simulation.runners.json-to-csv results-planetlab-20-100-09-100.json results-planetlab-20-100-09-100.csv