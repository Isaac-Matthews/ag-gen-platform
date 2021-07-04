#!/usr/bin/env bash
# Add scripts and publish
set -e
curl https://bootstrap.pypa.io/get-pip.py -o ../scripts/get-pip.py
tar -zcf scripts.tar.gz ../scripts
docker build -t scimitar/mulmod ./
wait
rm scripts.tar.gz
