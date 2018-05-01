#!/bin/bash

echo "compiling"
#mvn -Pprod clean package -DskipTests
./gradlew -Pprod clean bootRepackage

rm build/linkguardian-temp.war
cp build/libs/linkguardian-*.war.original build/linkguardian-temp.war

echo ""
echo "archive built!!!"
