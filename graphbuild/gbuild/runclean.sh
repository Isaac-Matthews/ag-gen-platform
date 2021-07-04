#!/usr/bin/env bash
cd ./build-app
mvn clean compile assembly:single
java -cp target/gbuild-app-1.0-SNAPSHOT.jar  gbuild.App
