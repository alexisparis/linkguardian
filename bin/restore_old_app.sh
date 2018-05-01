#!/bin/bash

SSH_HOST=root@149.56.109.148

ssh $SSH_HOST "sudo -H -u linkguardian bash -c 'source ~/bin/maintenance_on.sh'"

echo "stopping tomcat"
ssh $SSH_HOST "sudo systemctl stop tomcat"

echo "installing old distribution"
ssh $SSH_HOST "rm -Rf /opt/tomcat/webapps/ROOT*"
ssh $SSH_HOST "mv /root/old.linkguardian.war /opt/tomcat/webapps/ROOT.war"
ssh $SSH_HOST "rm -Rf /root/old.linkguardian.war"

echo "starting tomcat"
ssh $SSH_HOST "sudo systemctl start tomcat"

ssh $SSH_HOST "sudo -H -u linkguardian bash -c 'source ~/bin/maintenance_off.sh'"

echo "FINISHED!!!"
