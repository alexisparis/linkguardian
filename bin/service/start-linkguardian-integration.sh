#!/bin/sh
java -Xms1G -Xmx2G -server -XX:+UseG1GC -XX:-UseParallelGC -XX:-UseConcMarkSweepGC -jar linkguardian-INTE.war /tmp 2>> /dev/null >> /dev/null
