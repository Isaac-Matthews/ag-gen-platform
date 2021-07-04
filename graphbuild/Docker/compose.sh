#!/usr/bin/env bash
shopt -s nullglob
sleep 60
echo "STARTING GBUILD COMPOSE"
while true
do
  for d in */; do cd "$d"; if [ -f "${d%\/}-processed.dot" -a ! -f "gephi${d%\/}-processed.pdf" ]; then
    echo "FOUND A DOT TO DRAW"; y=${d%\/}; java -jar ~/gbuild.jar "$y-processed.dot";
  fi
  cd ..;
done
  sleep 10
done
