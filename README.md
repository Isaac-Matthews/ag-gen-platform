# Scimitar
XQ Labs: Scimitar - Intelligent Attack Path Generation

***

### Usage

Before starting scimitar, ensure that a database has been added to `/xq_db/`, and that the machine has docker and docker-compose installed.

In order to setup scimitar, either run `./setup.sh` or `./run.sh -s`. If you run the setup script alone, scimitar can be started by running `./run.sh`. This will make the folder `/input/` live, meaning that any xml, nessus or P files will be processed when dropped inside.

To stop scimitar run `stop.sh`.

##### Extra arguments:

`./setup.sh`

    -h|--help: print description and options
    -v|--verbose: run in verbose mode to see everything that is happening
    -u|--update: just update the databases instead of rerunning the full setup

`./run.sh`

    -h|--help: print description and options
    -s|--setup: run the setup script first (if this is your first run make sure the setup has run first)
    -v|--verbose: run in verbose mode to see everything that is happening; this will keep printing the compose output
    -u|--update: update the databases before running


[//]: # (After generating an OpenVAS style xml vulnerability report, a graph can be created using *scimitar*. The scripts in `mulmod` first need to be used to generate files for MulVAL graph generator. Next MulVAL is used to generate `.dot` files. These files can optionally be processed by another script in the `mulmod` container to remove the rule nodes and merge edges for cleaner graphs "this should only be used for examining the network structure and vulnerability locations")


[//]: # (The `dot` files can then be loaded into `graphbuild` that uses gephi to automatically generate graphs of the network. This is currently just a visualisation step but does help in understanding the networks you are looking at.)


[//]: # (Running `docker-compose up` is a way to bring all the necessary containers up and running at once, with a single input folder which will automatically process any files placed inside it.)



***
### Layout
+`/graphbuild/` - java app for processing mulval output into gephi graphs  
+`/mulmod/` - modified mulval attack graph creation engine  
+`/input/` - folder for testing the automation; once a scan is dropped in the folder it is automatically processed if the compose is up  
+`/xq_db/` - the mariadb containing the xq vulnerability database; used by `graphbuild`


***
