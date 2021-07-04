#!/usr/bin/env bash
# Add scripts and publish
set -e
#curl https://bootstrap.pypa.io/get-pip.py -o ../scripts/get-pip.py
# curl https://github.com/gephi/gephi-toolkit/releases/download/v0.9.2/gephi-toolkit-0.9.2-all.jar -o ../gbuild/gephi-toolkit-0.9.2-all.jar
#tar -zcf gbuild.tar.gz ../gbuild
#cp ../gbuild/jdk-9.0.4_linux-x64_bin.tar.gz ./
cd ../gbuild/build-app
docker run -it --rm --name gbuild -v "$(pwd)":/usr/src/mymaven -w /usr/src/mymaven maven:3.3-jdk-8 mvn clean package
wait
cp target/gbuild-app-1.0-SNAPSHOT-launcher.jar ../../Docker/gbuild.jar
find . -maxdepth 2 -name "*.jar" -type f -delete
cd ../../Docker
docker build -t scimitar/graphbuild ./
wait
rm gbuild.jar
#rm jdk-9.0.4_linux-x64_bin.tar.gz
#rm gbuild.tar.gz
