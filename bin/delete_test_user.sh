#!/bin/bash

SSH_HOST=root@149.56.109.148

echo ""
ssh $SSH_HOST "sudo -H -u linkguardian bash -c 'source ~/bin/delete_test_user.sh'"

echo ""
echo "FINISHED!!!"
