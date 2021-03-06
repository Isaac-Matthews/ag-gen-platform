#!/bin/bash
# Delete all containers
docker rm $(docker ps -a -q)
# Delete all images
docker rmi $(docker images -q)
# Pull latest
docker pull isaacmatthews/mulmod:latest