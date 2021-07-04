#!/usr/bin/env bash
shopt -s nullglob
sleep 100
/root/createDatabase.bash
/root/startSql.bash
cd /input
while true
echo RUNNING COMPGEN
do
  for d in */; do cd "$d"; if [ -f "${d%\/}-processed.dot" -a -f "gephi${d%\/}-processed.pdf" ]; then
    if [ -f AttackGraph.dot ]
    then
      if [ ! -f AttackGraph.pdf ]
      then
        render.sh
      fi
    fi
  fi
  cd ..;
done
  sleep 10
done
