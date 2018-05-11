#!/bin/bash

SSH_HOST=root@149.56.109.148

echo "copying prod war to remote MTL"
ssh $SSH_HOST "rm -Rf /root/linkguardian-PROD.war"
scp build/linkguardian-*.war $SSH_HOST:/root/linkguardian-PROD.war

ssh $SSH_HOST "sudo -H -u linkguardian bash -c 'source ~/bin/maintenance_on.sh'"

echo "stop service"
ssh $SSH_HOST "sudo systemctl stop linkguardian-prod"

echo "backing up old distribution"
ssh $SSH_HOST "rm -Rf /home/linkguardian/apps/linkguardian-PROD.war.old"
ssh $SSH_HOST "mv /home/linkguardian/apps/linkguardian-PROD.war /home/linkguardian/apps/linkguardian-PROD.war.old"

echo "removing old prod distribution"
ssh $SSH_HOST "rm -Rf /home/linkguardian/apps/linkguardian-PROD.war"

echo "installing new production distribution"
ssh $SSH_HOST "mv /root/linkguardian-PROD.war /home/linkguardian/apps/linkguardian-PROD.war"
ssh $SSH_HOST "rm -Rf /root/linkguardian-PROD.war"
ssh $SSH_HOST "chown linkguardian /home/linkguardian/apps/linkguardian-PROD.war"

echo "backup database"
ssh $SSH_HOST "sudo -H -u linkguardian bash -c '~/bin/backup_database.sh off pre_deployment'"

echo "start service"
ssh $SSH_HOST "sudo systemctl start linkguardian-prod"

echo "wait 30 seconds for the app to start"
sleep 30

ssh $SSH_HOST "sudo -H -u linkguardian bash -c 'source ~/bin/maintenance_off.sh'"

echo "FINISHED!!!"
