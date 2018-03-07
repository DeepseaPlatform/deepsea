#!/bin/sh
docker build . --build-arg BUILD_DATE=`date +"%Y%m%d-%H%M%S"` --tag deepsea:latest
