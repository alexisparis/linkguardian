#!/bin/sh
java -Xms3G -Xmx4G -server -XX:+UseG1GC -XX:-UseParallelGC -XX:-UseConcMarkSweepGC -jar linkguardian-PROD.war 2>> /dev/null >> /dev/null
