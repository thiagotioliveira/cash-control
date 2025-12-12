#!/bin/bash

# SDKMAN
source "$HOME/.sdkman/bin/sdkman-init.sh"

sdk default java 21.0.5-amzn

cd ..

mvn clean install -DskipTests

docker build -t cash-control .