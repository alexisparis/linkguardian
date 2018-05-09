#!/bin/bash

SSH_HOST=root@149.56.109.148

ssh $SSH_HOST "sudo -H -u linkguardian bash -c 'source ~/bin/maintenance_on.sh'"

echo "stop service"
ssh $SSH_HOST "sudo systemctl stop linkguardian-prod"

echo "installing old distribution"
ssh $SSH_HOST "rm -Rf /home/linkguardian/apps/linkguardian-PROD.war"
ssh $SSH_HOST "cp /home/linkguardian/apps/linkguardian-PROD.war.old /home/linkguardian/apps/linkguardian-PROD.war"
ssh $SSH_HOST "rm -Rf /home/linkguardian/apps/linkguardian-PROD.war.old"

echo "start service"
ssh $SSH_HOST "sudo systemctl start linkguardian-prod"

ssh $SSH_HOST "sudo -H -u linkguardian bash -c 'source ~/bin/maintenance_off.sh'"

echo "FINISHED!!!"
