#!/bin/bash

SSH_HOST=root@149.56.109.148

echo ""
echo "delete old scripts"
ssh $SSH_HOST "rm -Rf /home/linkguardian/bin/*.sh"

echo ""
echo "copy scripts"
scp bin/remote/* $SSH_HOST:/home/linkguardian/bin

echo ""
echo "changing owner"
ssh $SSH_HOST "chown linkguardian /home/linkguardian/bin/*.sh"

echo ""
echo "make scripts executable"
ssh $SSH_HOST "sudo -H -u linkguardian bash -c 'chmod u+x ~/bin/*.sh'"

echo ""
echo "FINISHED!!!"
