#!/bin/bash

SSH_HOST=root@149.56.109.148

scp $SSH_HOST:/opt/tomcat/logs/catalina.out catalina.out

echo "FINISHED!!!"
