#!/bin/bash

SSH_HOST=root@149.56.109.148

echo "copying integration war to remote MTL"
ssh $SSH_HOST "rm -Rf /root/linkguardian-INTE.war"
scp build/linkguardian-*.war $SSH_HOST:/root/linkguardian-INTE.war

echo "stop service"
ssh $SSH_HOST "sudo systemctl stop linkguardian-inte"

echo "removing old integration distribution"
ssh $SSH_HOST "rm -Rf /home/linkguardian/apps/linkguardian-INTE.war"

echo "installing new integration distribution"
ssh $SSH_HOST "mv /root/linkguardian-INTE.war /home/linkguardian/apps/linkguardian-INTE.war"
ssh $SSH_HOST "rm -Rf /root/linkguardian-INTE.war"
ssh $SSH_HOST "chown linkguardian /home/linkguardian/apps/linkguardian-INTE.war"

echo "start service"
ssh $SSH_HOST "sudo systemctl start linkguardian-inte"

echo "FINISHED!!!"
