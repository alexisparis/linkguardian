#!/bin/bash

SSH_HOST=root@149.56.109.148

echo "copying nginx configuration file"
scp bin/conf/nginx-defaults $SSH_HOST:/etc/nginx/sites-enabled/default

echo "restart nginx"
ssh $SSH_HOST "sudo systemctl restart nginx"

echo "FINISHED!!!"
