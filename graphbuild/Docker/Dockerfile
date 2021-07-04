FROM anapsix/alpine-java:8_jdk
WORKDIR /root
ADD gbuild.jar /root
ADD compose.sh /root
WORKDIR /input
CMD ~/compose.sh
