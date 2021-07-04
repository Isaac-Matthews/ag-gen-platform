#!/usr/bin/env bash
shopt -s nullglob
while true
do
  for f in *.xml; do y=${f%.xml}; mkdir "$y"; mv "$f" "$y"; done
  for f in *.nessus; do y=${f%.nessus}; mkdir "$y"; mv "$f" "$y"; done
  for f in *.P; do y=${f%.P}; mkdir "$y"; mv "$f" "$y"; done
  for d in */; do cd "$d"; if [ ! -f config.txt ]; then
    cp /root/config.txt .;  y=${d%\/}; if [ -f "$y.xml" ]; then
      openvas_translate.py "$y.xml"; graph_gen.sh "$y.p" -l; render.sh --nopdf; cp AttackGraph.dot "$y.dot"; process_dot.py "$y.dot"
    elif [ -f "$y.nessus" ]; then
      mv "$y.nessus" tempness.nessus; nessus_translate.sh tempness.nessus; graph_gen.sh nessus.P -l; render.sh --nopdf; cp AttackGraph.dot "$y.dot"; process_dot.py "$y.dot"; mv tempness.nessus "$y.nessus"
    else
      graph_gen.sh "$y.P" -l; render.sh --nopdf; cp AttackGraph.dot "$y.dot"; process_dot.py "$y.dot"
    fi
  fi
  cd ..;
done
  sleep 10
done
