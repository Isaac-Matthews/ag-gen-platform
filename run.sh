#!/bin/bash
for i in "$@"
do
case $i in
    -h|--help)
    echo "This will activate Scimitar on this machine."
    echo "While Scimitar is running, drag .xml scan files into the Input folder to process them."
    echo "First .dot files will be produced for hte attack graphs, then these files will be plotted to .pdfs"
    echo "-----Arguments-----"
    echo " -s|--setup : runs the setup script first; if this is your first run make sure you have either run the setup script yourself or use this argument."
    echo " -u|--update : updates all databases to their most current (both xq_db and nvd inside mulmod)"
    echo " -v|--verbose : verbose mode for full information on what is happening."
    exit 0;
    ;;
    -s|--setup)
    SETUP="t"
    shift
    ;;
    -v|--verbose)
    VERBOSE="t"
    shift
    ;;
    -u|--update)
    UPDATE="t"
    shift
    ;;
    *)
    ;;
esac
done
if [ -v SETUP ]
then
  if [ -v VERBOSE ]
  then
    ./setup -v
  else
    ./setup.sh
  fi
else
  if [ -v UPDATE ]
  then
    if [ -v VERBOSE ]
    then
      ./setup -u -v
    else
      ./setup.sh -u
    fi
  fi
  command -v docker >/dev/null 2>&1 || { echo "Docker must be installed.  Aborting." >&2; exit 1; }
  command -v docker-compose >/dev/null 2>&1 || { echo "Docker-Compose must be installed.  Aborting." >&2; exit 1; }
fi
if [ ! -d input ] ; then
  mkdir input
fi
docker-compose -f docker-compose.yml -p scimitar up -d
echo "Scimitar network is up"
echo "Input folder is live. Place any files to process inside."
