#!/bin/bash

SSH_HOST=root@149.56.109.148

echo "copying integration war to remote MTL"
ssh $SSH_HOST "rm -Rf /root/integration.linkguardian.war"
scp build/linkguardian-*.war $SSH_HOST:/root/integration.linkguardian.war

echo "removing old integration distribution"
ssh $SSH_HOST "rm -Rf /opt/tomcat/webapps/integration*"

echo "installing new integration distribution"
ssh $SSH_HOST "mv /root/integration.linkguardian.war /opt/tomcat/webapps/integration.war"
ssh $SSH_HOST "rm -Rf /root/integration.linkguardian.war"

echo "FINISHED!!!"
