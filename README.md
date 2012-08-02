This project contains the source code and results of the simulations for the paper entitled
["Managing Overloaded Hosts for Dynamic Consolidation of Virtual Machines in Cloud Data Centers Under Quality of Service Constraints"](http://beloglazov.info/papers/2012-host-overload-detection-tpds.pdf),
which has been accepted to IEEE Transactions on Parallel and Distributed Systems.

## Usage

First, run the script to install Michael Thomas Flanagan's Java Scientific
Library (http://www.ee.ucl.ac.uk/~mflanaga/java/) to the local Maven repository:

> ./install_flanagan

To download the dependencies and compile the source code:

> lein compile

To create Eclipse project files:

> lein eclipse

To run the unit tests:

> lein midje

Please see examples of running simulation in the included scripts: run-*.sh

## Workload data

The workload data have been obtained from the CoMon project, a monitoring infrastructure for
PlanetLab (http://comon.cs.princeton.edu/). The data used in the simulations in the CSV format are
available at https://github.com/beloglazov/tpds-2012-workload

The workload data have been transformed in the required internal data format and is located in the
workload/ directory.

The file name format is the following: planetlab_n_otf1_otf2_index

    where:

        n     - the number of different simulation runs
        otf1  - the minimum OTF value of each CPU utilization trace
        otf2  - the maximum OTF value after the first 30 time frames of each CPU
                utilization trace
        index - the index of the workload file


## License

Copyright (C) 2012 Anton Beloglazov
