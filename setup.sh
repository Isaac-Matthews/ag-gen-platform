#!/bin/bash
#check maven, docker and docker compose and jdk
#run subscripts and wait for completion
command -v docker >/dev/null 2>&1 || { echo "Docker must be installed.  Aborting." >&2; exit 1; }
command -v docker-compose >/dev/null 2>&1 || { echo "Docker-Compose must be installed.  Aborting." >&2; exit 1; }
for i in "$@"
do
case $i in
    -h|--help)
    echo "The setup script for Scimitar"
    echo "-----Arguments-----"
    echo " -u|--update : updates all databases to their most current (both xq_db and nvd inside mulmod)"
    echo " -v|--verbose : verbose mode for full information on what is happening."
    exit 0;
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
if [ -v UPDATE ]
then
  cd mulmod/Docker/
  echo "Commencing Scimitar initial setup"
  if [ -v VERBOSE ]
  then
    ./publish.bash
  else
    echo "Building scimitar/mulmod"
    ./publish.bash >/dev/null 2>&1
    echo "Mulmod has been built"
  fi
  cd ../../xq_db/  
  if [ -e ./latest.sql ]
  then    
    if [ -v VERBOSE ]
    then
      ./publish.bash
    else
      echo "Building XQ Database"
      ./publish.bash >/dev/null 2>&1
      echo "XQ Database built"
    fi
  else
    echo "Missing SQL file for xq_db"
  fi
fi
cd mulmod/Docker/
echo "Commencing Scimitar initial setup"
if [ -v VERBOSE ]
then
  ./publish.bash
else
  echo "Building scimitar/mulmod"
  ./publish.bash >/dev/null 2>&1
  echo "Mulmod has been built"
fi
cd ../../graphbuild/Docker/
if [ -v VERBOSE ]
then
  ./publish.bash
else
  echo "Building scimitar/graphbuild"
  ./publish.bash >/dev/null 2>&1
  echo "Graphbuild has been built"
fi
cd ../../xq_db/

bash get_latest_db.sh

if [ -e ./latest.sql ]
then
  if [ -v VERBOSE ]
  then
    ./publish.bash
  else
    echo "Building XQ Database"
    ./publish.bash >/dev/null 2>&1
    echo "XQ Database built"
  fi
else
  echo "Missing SQL file for xq_db"
fi
echo "Finished Scimitar setup"
