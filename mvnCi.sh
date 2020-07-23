#!/bin/bash
pushd `dirname $0`
#mvn clean compile package
mvn clean install
#read -p "Press any key to resume ..."
echo 'Press any key to continue...'
read -n1
popd
