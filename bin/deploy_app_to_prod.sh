#!/bin/bash

SSH_HOST=root@149.56.109.148

echo "copying war to remote MTL"
ssh $SSH_HOST "rm -Rf /root/new.linkguardian.war"
scp build/linkguardian-*.war $SSH_HOST:/root/new.linkguardian.war

ssh $SSH_HOST "sudo -H -u linkguardian bash -c 'source ~/bin/maintenance_on.sh'"

echo "stopping tomcat"
ssh $SSH_HOST "sudo systemctl stop tomcat"

echo "backing up old distribution"
ssh $SSH_HOST "rm -Rf /root/old.linkguardian.war"
ssh $SSH_HOST "mv /opt/tomcat/webapps/ROOT.war /root/old.linkguardian.war"

echo "removing old distribution"
ssh $SSH_HOST "rm -Rf /opt/tomcat/webapps/ROOT*"

echo "installing new distribution"
ssh $SSH_HOST "mv /root/new.linkguardian.war /opt/tomcat/webapps/ROOT.war"
ssh $SSH_HOST "rm -Rf /root/new.linkguardian.war"

echo "backup database"
ssh $SSH_HOST "sudo -H -u linkguardian bash -c '~/bin/backup_database.sh off pre_deployment'"

echo "starting tomcat"
ssh $SSH_HOST "sudo systemctl start tomcat"

ssh $SSH_HOST "sudo -H -u linkguardian bash -c 'source ~/bin/maintenance_off.sh'"

echo "FINISHED!!!"
