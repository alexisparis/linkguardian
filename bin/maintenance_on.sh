#!/bin/bash

SSH_HOST=root@149.56.109.148

echo ""
echo "call maintenance on"
ssh $SSH_HOST "sudo -H -u linkguardian bash -c 'source ~/bin/maintenance_on.sh'"

echo ""
echo "FINISHED!!!"
